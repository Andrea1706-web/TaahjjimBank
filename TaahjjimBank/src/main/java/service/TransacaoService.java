package service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.ContaBancariaModel;
import model.TransacaoModel;
import model.eStatusTransacao;
import org.springframework.stereotype.Service;
import util.ValidationUtil;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@Service
public class TransacaoService implements iCrudService<List<TransacaoModel>> {
    private final DriverS3<TransacaoModel> driverS3;
    private final ObjectMapper objectMapper;
    private final String PATH = "dados/";
    private final TransacaoModel model;

    public TransacaoService(String bucketName, String body) {
        this.driverS3 = new DriverS3<>(bucketName, TransacaoModel.class);
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        if (body != null && !body.trim().isEmpty()) {
            try {
                List<TransacaoModel> models = objectMapper.readValue(body, objectMapper.getTypeFactory().constructCollectionType(List.class, TransacaoModel.class));
                this.model = models.isEmpty() ? null : models.get(0);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Erro ao deserializar bodyJson", e);
            }
        } else {
            this.model = null;
        }
    }

    @Override
    public List<TransacaoModel> obter(String contaOrigem) {
        String pathNormal = PATH + "transacao/" + contaOrigem + ".json";
        String pathAgendada = PATH + "transacaoAgendada/" + contaOrigem + ".json";

        List<TransacaoModel> transacoesNormais = driverS3.readList(pathNormal, TransacaoModel.class).orElse(new ArrayList<>());

        List<TransacaoModel> transacoesAgendadas = driverS3.readList(pathAgendada, TransacaoModel.class).orElse(new ArrayList<>());

        List<TransacaoModel> todasTransacoes = new ArrayList<>();
        todasTransacoes.addAll(transacoesNormais);
        todasTransacoes.addAll(transacoesAgendadas);

        return todasTransacoes;
    }

    @Override
    public List<TransacaoModel> criar() {
        ValidationUtil.validar(this.model);

        ContaBancariaService contaService = new ContaBancariaService("zupbankdatabase", null);

        if (!contaService.contaExiste(this.model.getNumeroContaOrigem())) {
            throw new IllegalArgumentException("Conta origem não existe: " + this.model.getNumeroContaOrigem());
        }
        if (!contaService.contaExiste(this.model.getNumeroContaDestino())) {
            throw new IllegalArgumentException("Conta destino não existe: " + this.model.getNumeroContaDestino());
        }

        // Verifica se é agendada e salva no bucket sem liquidar
        boolean isAgendada = model.getDataAgendamento().toLocalDate().isAfter(LocalDate.now());
        if (isAgendada) {
            this.model.setStatusTransacao(eStatusTransacao.AGENDADA);
            String keyAgendada = PATH + "transacaoAgendada/" + this.model.getNumeroContaOrigem() + ".json";
            List<TransacaoModel> agendadas = driverS3.readList(keyAgendada, TransacaoModel.class).orElse(new ArrayList<>());
            agendadas.add(this.model);
            driverS3.saveList(keyAgendada, agendadas);
            return List.of(this.model);
        } else {
            // Liquida imediatamente
            return liquidar(this.model, false);
        }
    }

    public List<TransacaoModel> liquidar(TransacaoModel transacao, boolean removerAgendada) {
        ContaBancariaService contaService = new ContaBancariaService("zupbankdatabase", null);
        ContaBancariaModel contaOrigem = contaService.obter(transacao.getNumeroContaOrigem());
        ContaBancariaModel contaDestino = contaService.obter(transacao.getNumeroContaDestino());

        // conta inesxistente: rejeita e registra no extrato
        if (contaOrigem == null || contaDestino == null) {
            transacao.setStatusTransacao(eStatusTransacao.REJEITADA);
            transacao.setDataTransacao(LocalDateTime.now());

            String keyRejeitada = PATH + transacao.getNumeroContaOrigem() + ".json";
            List<TransacaoModel> extrato = driverS3.readList(keyRejeitada, TransacaoModel.class).orElse(new ArrayList<>());
            extrato.add(transacao);
            driverS3.saveList(keyRejeitada, extrato);

            return List.of(transacao);
        }

        //  Saldo insuficiente: envia para fila DLQ
        if (contaOrigem.getSaldo() < transacao.getValorTransacao()) {
            throw new RuntimeException("Saldo insuficiente para a transação: " + transacao.getId());
        }

        if (removerAgendada) {
            String keyAgendada = PATH + "transacaoAgendada/" + transacao.getNumeroContaOrigem() + ".json";
            List<TransacaoModel> agendadas = driverS3.readList(keyAgendada, TransacaoModel.class).orElse(new ArrayList<>());
            agendadas.removeIf(t -> t.getId().equals(transacao.getId()));
            if (agendadas.isEmpty()) {
                driverS3.deleteObject(keyAgendada);
            } else {
                driverS3.saveList(keyAgendada, agendadas);
            }
        }

        // Atualiza status e data
        transacao.setStatusTransacao(eStatusTransacao.CONCLUIDA);
        transacao.setDataTransacao(java.time.LocalDateTime.now());

        //salva extrato da origem (debito)
        String keyOrigem = PATH + transacao.getNumeroContaOrigem() + ".json";
        List<TransacaoModel> transacoesOrigem = driverS3.readList(keyOrigem, TransacaoModel.class).orElse(new ArrayList<>());
        TransacaoModel transacaoOrigem = cloneTransacao(transacao);
        transacaoOrigem.setValorTransacao(transacao.getValorTransacao() * -1);
        transacoesOrigem.add(transacaoOrigem);
        driverS3.saveList(keyOrigem, transacoesOrigem);

        //salva extrato do destino (credito)
        String keyDestino = PATH + transacao.getNumeroContaDestino() + ".json";
        List<TransacaoModel> transacoesDestino = driverS3.readList(keyDestino, TransacaoModel.class).orElse(new ArrayList<>());
        TransacaoModel transacaoDestino = cloneTransacao(transacao);
        transacoesDestino.add(transacaoDestino);
        driverS3.saveList(keyDestino, transacoesDestino);

        //atualiza saldo na origem
        contaOrigem.setSaldo(contaOrigem.getSaldo() - transacao.getValorTransacao());
        contaService.salvar(contaOrigem);

        //atualiza saldo no destino
        contaDestino.setSaldo(contaDestino.getSaldo() + transacao.getValorTransacao());
        contaService.salvar(contaDestino);

        return List.of(transacaoOrigem, transacaoDestino);
    }

    public void filtrarTransacoesAgendadas(String filaLiquidacaoSqs) {
        AmazonSQS sqsClient = AmazonSQSClientBuilder.defaultClient();
        String queueUrl = sqsClient.getQueueUrl(filaLiquidacaoSqs).getQueueUrl();

        String pathAgendadas = PATH + "transacaoAgendada/";
        List<String> arquivosAgendados = driverS3.listObjectsNames(pathAgendadas); //lista o nome de todos os arquivos

        LocalDateTime dataAtual = LocalDateTime.now();

        for (String key : arquivosAgendados) {
            // Lê a lista de transações do arquivo S3
            List<TransacaoModel> transacoes = driverS3.readList(key, TransacaoModel.class).orElse(new ArrayList<>());

            // Cria lista para armazenar as transações que serão liquidadas
            List<TransacaoModel> transacoesParaLiquidadar = new ArrayList<>();

            // Cria lista para armazenar as transações que ainda permanecem agendadas
            List<TransacaoModel> transacoesPendentes = new ArrayList<>();

            for (TransacaoModel transacao : transacoes) {
                // Verifica se a dataAgendamento é igual ao dia atual
                if (transacao.getDataAgendamento().toLocalDate().isEqual(dataAtual.toLocalDate())) {
                    transacao.setStatusTransacao(eStatusTransacao.INICIADA);
                    transacoesParaLiquidadar.add(transacao); // Adiciona na lista para liquidação
                } else {
                    transacoesPendentes.add(transacao);
                }
            }

            //Para cada transacao que sera liquidada
            for (TransacaoModel transacao : transacoesParaLiquidadar) {
                try {
                    // Converte a transação em JSON para enviar na fila SQS
                    String msg = objectMapper.writeValueAsString(transacao);
                    // Envia a mensagem para a fila SQS para processamento da liquidação
                    sqsClient.sendMessage(new SendMessageRequest(queueUrl, msg));
                } catch (Exception e) {
                    System.err.println("Erro ao enviar SQS para transacao atual " + transacao.getId() + ": " + e.getMessage());
                }
            }

            // Atualiza o arquivo no S3 com as transações restantes
            if (transacoesPendentes.isEmpty()) {
                driverS3.deleteObject(key); //apaga arquivo se não sobrou nada
            } else {
                driverS3.saveList(key, transacoesPendentes);
            }
        }
    }

    private TransacaoModel cloneTransacao(TransacaoModel original) {
        TransacaoModel copia = new TransacaoModel(original.getNumeroContaOrigem(), original.getNumeroContaDestino(), original.getDataAgendamento(), original.getValorTransacao(), original.getTipoTransacao(), original.getLocalidade(), original.getDispositivo());
        copia.setId(original.getId());
        copia.setEhFraude(original.isEhFraude());
        copia.setStatusTransacao(original.getStatusTransacao());
        return copia;
    }

    private void validarTransacaoComQuickCommand() {
        String url = "https://genai-code-buddy-api.stackspot.com/v1/quick-commands/create-execution/consultar-transacoes";
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"; // Substitua pelo token gerado

        try {
            // Configuração da conexão
            URL endpoint = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Enviar a requisição
            OutputStream os = connection.getOutputStream();
            os.flush();
            os.close();

            // Ler a resposta
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();
                System.out.println("Validação bem-sucedida: " + response.toString());
            } else {
                System.out.println("Erro na validação: " + responseCode + ", Detalhes: " + connection.getResponseMessage());
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}