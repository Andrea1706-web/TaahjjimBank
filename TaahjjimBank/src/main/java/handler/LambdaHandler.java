
package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.CartaoModel;
import service.DriverS3;

import java.util.Map;
import java.util.Optional;

public class LambdaHandler implements RequestHandler<Map<String,Object>, String> {

    @Override
    public String handleRequest(Map<String, Object> event, Context context) {
        CartaoModel cartao = new CartaoModel();
        cartao.setId("0");
        cartao.setNumeroCartao("1234-1236-1235-1236");
        cartao.setValidade("12/25");
        cartao.setCodigo("123");
        cartao.setNumeroConta("987654321");

        //Define o nome do bucket e a chave para o objeto
        String bucketName = "zupbankdatabase";
        String key = "dados/cartao-teste.json";

        //Cria uma instância do Driver S3 para CartaoModel
        DriverS3<CartaoModel> driverS3 = new DriverS3<>(bucketName, CartaoModel.class);

        //Salva o objeto CartaoModel no S3
        try {
            driverS3.save(key, cartao);
        } catch (Exception e) {
            throw e;
        }
        try {
            Optional<CartaoModel> cartao2 = driverS3.read(key);
            if (cartao2.isPresent()) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writeValueAsString(cartao2.get());
            }
        } catch (JsonProcessingException e) {
            // Trate a exceção de processamento JSON aqui
            e.printStackTrace();
            return "Erro ao processar JSON";

        } catch (Exception e) {
            throw e;
        }
        return "Objeto não encontrado";
    }
}
