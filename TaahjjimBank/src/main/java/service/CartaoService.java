package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.CartaoModel;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class CartaoService {

    private final DriverS3<CartaoModel> driverS3;
    private final ObjectMapper objectMapper;

    public CartaoService(String bucketName) {
        this.driverS3 = new DriverS3<>(bucketName, CartaoModel.class);
        this.objectMapper = new ObjectMapper();
    }

    public String obter(String numeroCartao) throws JsonProcessingException {
        // Define a chave para buscar no S3
        String key = "dados/cartoes/" + numeroCartao + ".json";
        // Busca o cartão no S3
        Optional<CartaoModel> cartao = driverS3.read(key);
        // Verifica se o cartão foi encontrado e retorna como JSON
        if (cartao.isPresent()) {
            return objectMapper.writeValueAsString(cartao.get());
        } else {
            // Retorna uma mensagem de erro ou um JSON vazio
            return "{\"error\": \"Cartão não encontrado\"}";
        }
    }

    public String criar(Map<String, Object> body) throws JsonProcessingException {
    // Cria um novo cartão a partir do corpo da requisição
        CartaoModel cartao = new CartaoModel(
                null,
                (String) body.get("numeroCartao"),
                (String) body.get("validade"),
                (String) body.get("codigo"),
                (String) body.get("numeroConta")
        );

    // Define a chave para salvar no S3
        String key = "dados/cartoes" + cartao.getNumeroCartao() + ".json";

    // Salva o cartão no S3
        driverS3.save(key, cartao);

    // Retorna o cartão salvo como JSON
        return objectMapper.writeValueAsString(cartao);
    }
}
