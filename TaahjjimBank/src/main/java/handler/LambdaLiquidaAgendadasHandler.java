package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.TransacaoModel;
import service.command.PixCommand;
import service.ContaBancariaService;
import service.DriverS3;

import java.util.List;

public class LambdaLiquidaAgendadasHandler implements RequestHandler<SQSEvent, Void> {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        String bucketName = System.getenv("BUCKET_NAME");
        DriverS3<TransacaoModel> driverS3 = new DriverS3<>(bucketName, TransacaoModel.class);
        ContaBancariaService contaService = new ContaBancariaService(bucketName, null);

        PixCommand pixCommand = new PixCommand();

        for (SQSEvent.SQSMessage message : event.getRecords()) {
            try {
                TransacaoModel transacao = objectMapper.readValue(message.getBody(), TransacaoModel.class);

                // Chama executar com isAgendada = false para liquidação imediata
                List<TransacaoModel> resultado = pixCommand.executar(transacao, false, driverS3, contaService);

                context.getLogger().log("Transação liquidada: " + resultado);
            } catch (Exception e) {
                context.getLogger().log("Erro ao processar mensagem SQS: " + e.getMessage());
                throw new RuntimeException(e); // relança para a Lambda não deletar a mensagem da fila
            }
        }

        return null;
    }
}