package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.CartaoModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CartaoService {

    private final DriverS3<CartaoModel> driverS3;
    private final ObjectMapper objectMapper;

    public CartaoService(String bucketName) {
        this.driverS3 = new DriverS3<>(bucketName, CartaoModel.class);
        this.objectMapper = new ObjectMapper();
    }

    public String obter() throws JsonProcessingException {
    // Lê todos os cartões do S3 (simulação)
        List<CartaoModel> cartoes = List.of(
                new CartaoModel("1", "1234-5678-9012-3456", "12/25", "123", "987654321"),
                new CartaoModel("2", "9876-5432-1098-7654", "11/24", "456", "123456789")
        );
        return objectMapper.writeValueAsString(cartoes);
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
        String key = "dados/" + cartao.getNumeroCartao() + ".json";

    // Salva o cartão no S3
        driverS3.save(key, cartao);

    // Retorna o cartão salvo como JSON
        return objectMapper.writeValueAsString(cartao);
    }
}
