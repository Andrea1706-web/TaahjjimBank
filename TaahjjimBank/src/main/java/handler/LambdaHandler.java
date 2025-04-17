
package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.CartaoModel;
import service.DriverS3;

import java.util.List;
import java.util.Map;
import java.util.Optional;

//public class LambdaHandler implements RequestHandler<Map<String,Object>, String> {
//
//    @Override
//    public String handleRequest(Map<String, Object> event, Context context) {
//        CartaoModel cartao = new CartaoModel();
//        cartao.setId("0");
//        cartao.setNumeroCartao("1234-1236-1235-1236");
//        cartao.setValidade("12/25");
//        cartao.setCodigo("123");
//        cartao.setNumeroConta("987654321");
//
//        //Define o nome do bucket e a chave para o objeto
//        String bucketName = "zupbankdatabase";
//        String key = "dados/cartao-teste.json";
//
//        //Cria uma instância do Driver S3 para CartaoModel
//        DriverS3<CartaoModel> driverS3 = new DriverS3<>(bucketName, CartaoModel.class);
//
//        //Salva o objeto CartaoModel no S3
//        try {
//            driverS3.save(key, cartao);
//        } catch (Exception e) {
//            throw e;
//        }
//        try {
//            Optional<CartaoModel> cartao2 = driverS3.read(key);
//            if (cartao2.isPresent()) {
//                ObjectMapper objectMapper = new ObjectMapper();
//                return objectMapper.writeValueAsString(cartao2.get());
//            }
//        } catch (JsonProcessingException e) {
//            // Trate a exceção de processamento JSON aqui
//            e.printStackTrace();
//            return "Erro ao processar JSON";
//
//        } catch (Exception e) {
//            throw e;
//        }
//        return "Objeto não encontrado";
//    }
//}

public class LambdaHandler implements RequestHandler<Map<String, Object>, String> {

    private final String bucketName = "zupbankdatabase";
    private final DriverS3<CartaoModel> driverS3 = new DriverS3<>(bucketName, CartaoModel.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String handleRequest(Map<String, Object> event, Context context) {
        String path = (String) event.get("path");
        String httpMethod = (String) event.get("httpMethod");

        try {
            context.getLogger().log("Variável Path" + path);
            context.getLogger().log("Variável httpMethod" + httpMethod);
            context.getLogger().log("Object event" + event);
            if ("/cartao".equals(path)) {
                if ("GET".equals(httpMethod)) {
                    return exibirTodos();
                } else if ("POST".equals(httpMethod)) {
                    Map<String, Object> body = (Map<String, Object>) event.get("body");
                    return cadastrarCartao(body);
                }
            }
            return "Endpoint ou método não suportado.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao processar requisição: " + e.getMessage();
        }
    }

    private String exibirTodos() throws JsonProcessingException {
        // Lê todos os cartões do S3 (simulação)
        List<CartaoModel> cartoes = List.of(
                new CartaoModel("1", "1234-5678-9012-3456", "12/25", "123", "987654321"),
                new CartaoModel("2", "9876-5432-1098-7654", "11/24", "456", "123456789")
        );
        return objectMapper.writeValueAsString(cartoes);
    }

    private String cadastrarCartao(Map<String, Object> body) throws JsonProcessingException {
        // Cria um novo cartão a partir do corpo da requisição
        CartaoModel cartao = new CartaoModel(
                null,
                (String) body.get("numeroCartao"),
                (String) body.get("validade"),
                (String) body.get("codigo"),
                (String) body.get("numeroConta")
        );

        // Define a chave para salvar no S3
        String key = "dados/" + cartao.getNumeroCartao() + ".json";

        // Salva o cartão no S3
        driverS3.save(key, cartao);

        // Retorna o cartão salvo como JSON
        return objectMapper.writeValueAsString(cartao);
    }
}
