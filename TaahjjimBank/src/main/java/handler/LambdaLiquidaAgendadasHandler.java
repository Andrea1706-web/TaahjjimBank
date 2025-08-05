package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.TransacaoModel;
import service.TransacaoService;


public class LambdaLiquidaAgendadasHandler implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        TransacaoService service = new TransacaoService("zupbankdatabase", null);

        for (SQSEvent.SQSMessage message : event.getRecords()) {
            try {
                TransacaoModel transacao = objectMapper.readValue(message.getBody(), TransacaoModel.class);
                service.liquidar(transacao, true);
            } catch (Exception e) {
                throw new RuntimeException("Erro ao processar mensagem SQS", e);
            }
        }
        return null;
    }
}

