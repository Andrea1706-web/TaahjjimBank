
package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import model.CartoesModel;
import service.DriverS3;

import java.util.Map;

public class LambdaHandler implements RequestHandler<Map<String,Object>, String> {

    @Override
    public String handleRequest(Map<String, Object> event, Context context) {
        CartoesModel cartao = new CartoesModel();
        cartao.setValidade("12/25");
        cartao.setCodigo("123");
        cartao.setNumeroConta("987654321");

        //Define o nome do bucket e a chave para o objeto
        String bucketName = "zupbankdatabase";
        String key = "dados/cartao-teste.json";

        //Cria uma instância do Driver S3 para CartaoModel
        DriverS3<CartoesModel> drivesS3 = new DriverS3<>(bucketName, CartoesModel.class);

        //Salva o objeto CartaoModel no S3
        try {
            drivesS3.save(key, cartao);
            return "Cartao salvo com sucesso no S3";
        } catch (Exception e) {
            throw e;
        }
        try {
            Optional<CartaoModel> cartao2 = driverS3.read(key);
            return cartao2;
        } catch (Exception e) {
            throw e; // Lança a exceção para o chamador
        }
}
