package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import service.command.PixCommand;
import model.TransacaoModel;
import service.DriverS3;

public class LambdaFiltragemAgendadasHandler implements RequestHandler<Object, String> {

    @Override
    public String handleRequest(Object input, Context context) {
        String bucketAgendadas = System.getenv("BUCKET_NAME");
        String filaAgendamento = System.getenv("FILA_LIQUIDACAO");

        DriverS3<TransacaoModel> driverS3 = new DriverS3<>(bucketAgendadas, TransacaoModel.class);
        PixCommand pixCommand = new PixCommand();

        pixCommand.filtrarTransacoesAgendadas(filaAgendamento, driverS3);

        return "Filtragem concluída";
    }
}
