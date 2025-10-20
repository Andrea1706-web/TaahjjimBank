package service.command;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.*;
import model.enums.eStatusTransacao;
import model.enums.eTipoTransacao;
import service.*;
import service.interfaces.iTransacaoCommand;
import util.Consts;
import util.MensagensErro;
import util.QuickCommandUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PixCommand implements iTransacaoCommand {

    @Override
    public boolean aceita(eTipoTransacao tipo) {
        return tipo == eTipoTransacao.PIX;
    }

    @Override
    public List<TransacaoModel> executar(TransacaoModel transacao, boolean isAgendada,
                                         DriverS3<TransacaoModel> driverS3, ContaBancariaService contaService) {

        transacao.validarEspecifica(isAgendada);
        TransacaoPix pix = (TransacaoPix) transacao;

        if (isAgendada) {
            transacao.setStatusTransacao(eStatusTransacao.AGENDADA);
            String key = Consts.PATH_BUCKET_TRANSACAO_AGENDADA + transacao.getNumeroContaOrigem() + ".json";
            List<TransacaoModel> agendadas = driverS3.readList(key, TransacaoModel.class).orElse(new ArrayList<>());
            agendadas.add(transacao);
            driverS3.saveList(key, agendadas);
            return List.of(transacao);
        }

        String idContaOrigem = pix.getNumeroContaOrigem();
        String idContaDestino = pix.getNumeroContaDestino();

        ContaBancariaModel contaOrigem = contaService.obter(idContaOrigem);
        if (contaOrigem == null) throw new RuntimeException(MensagensErro.CONTA_ORIGEM_NAO_ENCONTRADA);

        ContaBancariaModel contaDestino = contaService.obter(idContaDestino);
        if (contaDestino == null) throw new RuntimeException(MensagensErro.CONTA_DESTINO_NAO_ENCONTRADA);

        if (contaOrigem.getSaldo() < transacao.getValorTransacao()) {
            throw new RuntimeException(MensagensErro.SALDO_INSUFICIENTE);
        }

        transacao.setStatusTransacao(eStatusTransacao.CONCLUIDA);
        transacao.setDataTransacao(LocalDateTime.now());

        String keyOrigem = Consts.PATH_BUCKET_TRANSACAO + idContaOrigem + ".json";
        List<TransacaoModel> transacoesOrigem = driverS3.readList(keyOrigem, TransacaoModel.class).orElse(new ArrayList<>());
        TransacaoModel transacaoOrigem = cloneTransacao(transacao);
        transacaoOrigem.setValorTransacao(-transacao.getValorTransacao());
        transacoesOrigem.add(transacaoOrigem);
        driverS3.saveList(keyOrigem, transacoesOrigem);

        String keyDestino = Consts.PATH_BUCKET_TRANSACAO + idContaDestino + ".json";
        List<TransacaoModel> transacoesDestino = driverS3.readList(keyDestino, TransacaoModel.class).orElse(new ArrayList<>());
        TransacaoModel transacaoDestino = cloneTransacao(transacao);
        transacoesDestino.add(transacaoDestino);
        driverS3.saveList(keyDestino, transacoesDestino);

        contaOrigem.setSaldo(contaOrigem.getSaldo() - transacao.getValorTransacao());
        contaService.salvar(contaOrigem);

        contaDestino.setSaldo(contaDestino.getSaldo() + transacao.getValorTransacao());
        contaService.salvar(contaDestino);

        return List.of(transacaoOrigem, transacaoDestino);
    }

    public void filtrarTransacoesAgendadas(String filaLiquidacaoSqs, DriverS3<TransacaoModel> driverS3) {

        AmazonSQS sqsClient = AmazonSQSClientBuilder.defaultClient();
        String queueUrl = sqsClient.getQueueUrl(filaLiquidacaoSqs).getQueueUrl();

        List<String> arquivosAgendados = driverS3.listObjectsNames(Consts.PATH_BUCKET_TRANSACAO_AGENDADA);
        LocalDateTime dataAtual = LocalDateTime.now();

        for (String key : arquivosAgendados) {
            List<TransacaoModel> transacoes = driverS3.readList(key, TransacaoModel.class).orElse(new ArrayList<>());

            List<TransacaoModel> paraLiquidar = new ArrayList<>();
            List<TransacaoModel> pendentes = new ArrayList<>();

            for (TransacaoModel transacao : transacoes) {
                if (transacao.getDataAgendamento().toLocalDate().isEqual(dataAtual.toLocalDate())) {
                    transacao.setStatusTransacao(eStatusTransacao.INICIADA);
                    paraLiquidar.add(transacao);
                } else {
                    pendentes.add(transacao);
                }
            }

            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

            for (TransacaoModel tx : paraLiquidar) {
                try {
                    String json = mapper.writeValueAsString(tx);
                    sqsClient.sendMessage(new SendMessageRequest(queueUrl, json));
                } catch (Exception e) {
                    System.err.println("Erro ao enviar transação " + tx.getId() + ": " + e.getMessage());
                }
            }
            if (pendentes.isEmpty()) {
                driverS3.deleteObject(key);
            } else {
                driverS3.saveList(key, pendentes);
            }
        }
    }

    private TransacaoModel cloneTransacao(TransacaoModel original) {
        if (original instanceof TransacaoPix pix) {
            TransacaoPix clone = new TransacaoPix();
            clone.setId(pix.getId());
            clone.setNumeroContaOrigem(pix.getNumeroContaOrigem());
            clone.setNumeroContaDestino(pix.getNumeroContaDestino());
            clone.setValorTransacao(pix.getValorTransacao());
            clone.setDataTransacao(pix.getDataTransacao());
            clone.setStatusTransacao(pix.getStatusTransacao());
            clone.setTipoTransacao(pix.getTipoTransacao());
            clone.setLocalidade(pix.getLocalidade());
            clone.setDispositivo(pix.getDispositivo());
            clone.setDataAgendamento(pix.getDataAgendamento());

            return clone;
        }
        throw new UnsupportedOperationException("Tipo de transação não suportado para clonagem");
    }

    public void rodarQCTransacao() {
        String clientId = "fb197df9-4fe7-4ae9-8ae8-3052868dfb14";
        String clientSecret = "xnxs3Yp6Q1O3EX838cpH9LwhMV8W82uYg27m9P3yddiRjQdft68V8XT5JJarnf2e";
        QuickCommandUtil qcUtil = new QuickCommandUtil(
                clientId,
                clientSecret,
                Consts.QUICK_COMMAD_ANALISA_TRANSACAO_POST_URL,
                Consts.QUICK_COMMAND_GET_URL_TEMPLATE,
                null
        );
        String payload = "{ \"input_data\": \"valor de entrada\" }";
        String resultado = qcUtil.runQuickCommand(payload, 5, "completed");
        System.out.println("Resultado do QC de Transação: " + resultado);

    }
}
