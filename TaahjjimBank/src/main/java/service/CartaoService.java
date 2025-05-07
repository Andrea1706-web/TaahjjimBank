package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.CartaoModel;
import org.springframework.stereotype.Service;
import util.MapperUtil;

import java.util.Map;

@Service
public class CartaoService implements CrudService<CartaoModel> {

    private final DriverS3<CartaoModel> driverS3;
    private final ObjectMapper objectMapper;
    private final CartaoModel model;

    public CartaoService(String bucketName, String body) {
        this.driverS3 = new DriverS3<>(bucketName, CartaoModel.class);
        this.objectMapper = new ObjectMapper();
        CartaoModel bodyMap;
        try {
            bodyMap = objectMapper.readValue(body, CartaoModel.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao deserializar bodyJson", e);
        }
        this.model = bodyMap;
    }

    @Override
    public CartaoModel obter(String numeroCartao) {
        String key = "dados/" + numeroCartao + ".json";
        return driverS3.read(key).orElse(null);
    }

    @Override
    public void criar() {
        String key = "dados/" + this.model.getNumeroCartao() + ".json";
        driverS3.save(key, this.model);
    }
}
