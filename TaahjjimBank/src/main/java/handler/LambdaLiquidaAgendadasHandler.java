package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.TransacaoModel;
import model.TransacaoPagamentoDebito;
import model.TransacaoPix;
import service.ContaBancariaService;
import service.DriverS3;
import service.command.PixCommand;
import service.NotificacaoService;

import java.util.List;

public class LambdaLiquidaAgendadasHandler implements RequestHandler<SQSEvent, Void> {

    private final ObjectMapper objectMapper;

    public LambdaLiquidaAgendadasHandler() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerSubtypes(TransacaoPix.class, TransacaoPagamentoDebito.class);
    }

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        String bucketName = System.getenv("BUCKET_NAME");
        String topicArn = System.getenv("TOPIC_NOTIFICACOES");

        DriverS3<TransacaoModel> driverS3 = new DriverS3<>(bucketName, TransacaoModel.class);
        ContaBancariaService contaService = new ContaBancariaService(bucketName, null);

        PixCommand pixCommand = new PixCommand();
        NotificacaoService notificacaoService = new NotificacaoService(bucketName, topicArn);

        for (SQSEvent.SQSMessage message : event.getRecords()) {
            try {
                TransacaoModel transacao = objectMapper
                        .readerFor(TransacaoModel.class)
                        .readValue(message.getBody());

                // Executa liquidação imediata (isAgendada = false)
                List<TransacaoModel> resultado = pixCommand.executar(transacao, false, driverS3, contaService);

                resultado.forEach(tx -> context.getLogger().log("Transação liquidada: " + tx.getId()));

                // verificar o que acontece se a notificacao falhar
                resultado.stream()
                        .filter(tx -> tx.getValorTransacao() < 0)
                        .forEach(notificacaoService::notificarTransacao);

            } catch (Exception e) {
                context.getLogger().log("Erro ao processar mensagem SQS: " + e.getMessage());
                throw new RuntimeException(e); // relança para a Lambda não deletar a mensagem da fila

            }
        }
        return null;
    }
}
