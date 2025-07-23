package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import service.TransacaoService;

public class LambdaFiltragemAgendadasHandler implements RequestHandler<Object, String> {

    @Override
    public String handleRequest(Object input, Context context) {
        TransacaoService transacaoService = new TransacaoService("zupbankdatabase", null);

        String filaSQS = "fila-liquidacao-transacao";
        transacaoService.filtrarTransacoesAgendadas(filaSQS);

        return "Filtragem concluída com sucesso.";
    }
}
