package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.TransacaoModel;
import model.eStatusTransacao;
import model.ContaBancariaModel;
import service.DriverS3;
import service.ContaBancariaService;

import java.util.ArrayList;
import java.util.List;

public class LambdaLiquidaAgendadasHandler implements RequestHandler<SQSEvent, Void> {

    private final String PATH_TRANSACOES = "dados/transacao/";
    private final String PATH_AGENDADAS = "dados/transacaoAgendada/";
    private static final String BUCKET_NAME = "zupbankdatabase";

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final DriverS3<TransacaoModel> driverS3 = new DriverS3<>(BUCKET_NAME, TransacaoModel.class);


    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        ContaBancariaService contaService = new ContaBancariaService("zupbankdatabase", null);

        for (SQSEvent.SQSMessage message : event.getRecords()) {
            try {
                TransacaoModel transacao = objectMapper.readValue(message.getBody(), TransacaoModel.class);

                // atualiza status para CONCLUIDA e define dataTransacao atual
                transacao.setStatusTransacao(eStatusTransacao.CONCLUIDA);
                transacao.setDataTransacao(java.time.LocalDateTime.now());

                //liquida na origem (debito)
                String keyOrigem = PATH_TRANSACOES + transacao.getNumeroContaOrigem() + ".json";
                List<TransacaoModel> transacoesOrigem = driverS3.readList(keyOrigem, TransacaoModel.class).orElse(new ArrayList<>());
                TransacaoModel transacaoOrigem = cloneTransacao(transacao);
                transacaoOrigem.setValorTransacao(transacao.getValorTransacao() * -1);
                transacoesOrigem.add(transacaoOrigem);
                driverS3.saveList(keyOrigem, transacoesOrigem);

                //liquida no destino (credito)
                String keyDestino = PATH_TRANSACOES + transacao.getNumeroContaDestino() + ".json";
                List<TransacaoModel> transacoesDestino = driverS3.readList(keyDestino, TransacaoModel.class).orElse(new ArrayList<>());
                TransacaoModel transacaoDestino = cloneTransacao(transacao);
                transacoesDestino.add(transacaoDestino);
                driverS3.saveList(keyDestino, transacoesDestino);

                //remove a transação agendada do arquivo original
                String keyAgendada = PATH_AGENDADAS + transacao.getNumeroContaOrigem() + ".json";
                List<TransacaoModel> transacoesAgendadas = driverS3.readList(keyAgendada, TransacaoModel.class).orElse(new ArrayList<>());
                transacoesAgendadas.removeIf(t -> t.getId().equals(transacao.getId()));

                //atualiza saldo na origem
                ContaBancariaModel contaOrigem = contaService.obter(transacao.getNumeroContaOrigem());
                if (contaOrigem == null) throw new RuntimeException("Conta origem não encontrada");
                contaOrigem.setSaldo(contaOrigem.getSaldo() - transacao.getValorTransacao());
                contaService.salvar(contaOrigem);

                //atualiza saldo no destino
                ContaBancariaModel contaDestino = contaService.obter(transacao.getNumeroContaDestino());
                if (contaDestino == null) throw new RuntimeException("Conta destino não encontrada");
                contaDestino.setSaldo(contaDestino.getSaldo() + transacao.getValorTransacao());
                contaService.salvar(contaOrigem);

                if (transacoesAgendadas.isEmpty()) {
                    driverS3.deleteObject(keyAgendada);
                } else {
                    driverS3.saveList(keyAgendada, transacoesAgendadas);
                }
            } catch (Exception e) {
                context.getLogger().log("Erro ao processar transação agendada: " + e.getMessage());
            }
        }

        return null;
    }

    private TransacaoModel cloneTransacao(TransacaoModel original) {
        TransacaoModel copia = new TransacaoModel(original.getNumeroContaOrigem(), original.getNumeroContaDestino(), original.getDataAgendamento(), original.getValorTransacao(), original.getTipoTransacao(), original.getLocalidade(), original.getDispositivo());
        copia.setId(original.getId());
        copia.setEhFraude(original.isEhFraude());
        copia.setStatusTransacao(original.getStatusTransacao());
        return copia;
    }
}

