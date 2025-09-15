package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.NotificacaoDTO;
import service.NotificacaoEmailService;
import util.MensagensErro;

public class LambdaNotificacaoEmailHandler implements RequestHandler<SQSEvent, Void> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        NotificacaoEmailService notificacaoService = new NotificacaoEmailService();

        for (SQSEvent.SQSMessage message : event.getRecords()) {
            try {
                NotificacaoDTO dto = objectMapper.readValue(message.getBody(), NotificacaoDTO.class);
                notificacaoService.enviarResultadoLiquidacaoSeNaoEnviado(dto);
            } catch (Exception e) {
                context.getLogger().log(MensagensErro.ERRO_PROCESSAR_NOTIFICACAO + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
