package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import service.TransacaoService;

public class LambdaFiltragemAgendadasHandler implements RequestHandler<Object, String> {

    @Override
    public String handleRequest(Object input, Context context) {
        String bucketAgendadas = System.getenv("BUCKET_NAME");
        String filaAgendamento = System.getenv("FILA_LIQUIDACAO");

        TransacaoService service = new TransacaoService(bucketAgendadas, null);
        service.filtrarTransacoesAgendadas(filaAgendamento);

        return "Filtragem concluída";
    }
}
