
package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import model.CartoesModel;
import service.DriverS3;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Optional;

public class LambdaHandler implements RequestHandler<Map<String,Object>, String> {

    @Override
    public String handleRequest(Map<String, Object> event, Context context) throws JsonProcessingException {
        CartoesModel cartao = new CartoesModel();
        cartao.setValidade("12/25");
        cartao.setCodigo("123");
        cartao.setNumeroConta("987654321");

        //Define o nome do bucket e a chave para o objeto
        String bucketName = "zupbankdatabase";
        String key = "dados/cartao-teste.json";

        //Cria uma instância do Driver S3 para CartaoModel
        DriverS3<CartoesModel> driverS3 = new DriverS3<>(bucketName, CartoesModel.class);

        //Salva o objeto CartaoModel no S3
        try {
            driverS3.save(key, cartao);
        } catch (Exception e) {
            throw e;
        }
        try {
            Optional<CartoesModel> cartao2 = driverS3.read(key);
            if (cartao2.isPresent()) {
               ObjectMapper objectMapper = new ObjectMapper();
        // Converte o objeto para uma string JSON
            return objectMapper.writeValueAsString(cartao2.get());
            }
        } catch (Exception e) {
            throw e;
        }
            return "Objeto não encontrado";
        
    }
}
