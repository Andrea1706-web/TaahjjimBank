package service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.TransacaoModel;
import model.eStatusTransacao;
import org.springframework.stereotype.Service;
import util.ValidationUtil;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
                List<TransacaoModel> models = objectMapper.readValue(
                        body,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, TransacaoModel.class)
                );
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

        List<TransacaoModel> transacoesNormais = driverS3.readList(pathNormal, TransacaoModel.class)
                .orElse(new ArrayList<>());

        List<TransacaoModel> transacoesAgendadas = driverS3.readList(pathAgendada, TransacaoModel.class)
                .orElse(new ArrayList<>());

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

        // verifica se é agendada comparando a data
        boolean isAgendada = model.getDataAgendamento().toLocalDate().isAfter(LocalDate.now());
        String statusPath = isAgendada ? "transacaoAgendada/" : "transacao/";
        this.model.setStatusTransacao(isAgendada ? eStatusTransacao.AGENDADA : eStatusTransacao.CONCLUIDA);

        // se for agendada, salva sem liquidar
        if (isAgendada) {
            String keyAgendada = PATH + statusPath + this.model.getNumeroContaOrigem() + ".json";
            List<TransacaoModel> agendadas = driverS3.readList(keyAgendada, TransacaoModel.class).orElse(new ArrayList<>());
            agendadas.add(this.model);
            driverS3.saveList(keyAgendada, agendadas);
            return List.of(this.model);
        } else {
            // LIQUIDAÇÃO IMEDIATA
            // Salvar na origem
            String key1 = PATH + statusPath + this.model.getNumeroContaOrigem() + ".json";
            List<TransacaoModel> contaOrigemList = driverS3.readList(key1, TransacaoModel.class)
                    .orElse(new ArrayList<>());
            this.model.setValorTransacao(this.model.getValorTransacao() * -1);
            contaOrigemList.add(this.model);
            driverS3.saveList(key1, contaOrigemList);

            String key2 = PATH + statusPath + this.model.getNumeroContaDestino() + ".json";
            List<TransacaoModel> contaDestinoList = driverS3.readList(key2, TransacaoModel.class)
                    .orElse(new ArrayList<>());
            this.model.setValorTransacao(this.model.getValorTransacao() * -1);
            contaDestinoList.add(this.model);
            driverS3.saveList(key2, contaDestinoList);

            List<TransacaoModel> resultado = new ArrayList<>();
            resultado.add(this.model);
            return resultado;
        }
    }

    public void filtrarTransacoesAgendadas(String filaLiquidacaoSqs) {
        AmazonSQS sqsClient = AmazonSQSClientBuilder.defaultClient();
        String queueUrl = sqsClient.getQueueUrl(filaLiquidacaoSqs).getQueueUrl();

        String pathAgendadas = PATH + "transacaoAgendada/";
        List<String> arquivosAgendados = driverS3.listObjectsNames(pathAgendadas); //lista o nome de todos os arquivos

        LocalDateTime dataHoraAtual = LocalDateTime.now();

        for (String key : arquivosAgendados) {
            // Lê a lista de transações do arquivo S3
            List<TransacaoModel> transacoes = driverS3.readList(key, TransacaoModel.class).orElse(new ArrayList<>());

            // Cria lista para armazenar as transações que serão liquidadas
            List<TransacaoModel> transacoesParaLiquidadar = new ArrayList<>();

            // Cria lista para armazenar as transações que ainda permanecem agendadas
            List<TransacaoModel> transacoesPendentes = new ArrayList<>();

            // percorre todas as transações do arquivo
            for (TransacaoModel transacao : transacoes) {
                // Verifica se a dataAgendamento é igual ou anterior ao momento atual
                if (transacao.getDataAgendamento().isBefore(dataHoraAtual) || transacao.getDataAgendamento().isEqual(dataHoraAtual)) {
                    transacao.setStatusTransacao(eStatusTransacao.INICIADA); // Marca a transação como iniciada para liquidação
                    transacoesParaLiquidadar.add(transacao); // Adiciona na lista para liquidação
                } else {
                    transacoesPendentes.add(transacao); // Se ainda não chegou a data, mantém na lista das restantes
                }
            }

            //PARA CADA TRANSACAO QUE SERA LIQUIDADA
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
                driverS3.deleteObject(key); // apaga arquivo se não sobrou nada
            } else {
                driverS3.saveList(key, transacoesPendentes);
            }
        }
    }

}