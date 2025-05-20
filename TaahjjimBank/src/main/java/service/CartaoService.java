package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.CartaoModel;
import org.springframework.stereotype.Service;

@Service
public class CartaoService implements CrudService<CartaoModel> {

    private final DriverS3<CartaoModel> driverS3;
    private final ObjectMapper objectMapper;
    private final String PATH = "dados/cartao/";
    private final CartaoModel model;

    public CartaoService(String bucketName, String body) {
        this.driverS3 = new DriverS3<>(bucketName, CartaoModel.class);
        this.objectMapper = new ObjectMapper();

        if (body == null || body.trim().isEmpty()) {
            this.model = null;
        } else {
            CartaoModel model;
            try {
                model = objectMapper.readValue(body, CartaoModel.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                model = null;
            }
            this.model = model;
        }

    }

    @Override
    public CartaoModel obter(String numeroCartao) {
        String key = PATH + numeroCartao + ".json";
        return driverS3.read(key).orElse(null);
    }

    @Override
    public CartaoModel criar() {
        String key = PATH + this.model.getNumeroCartao() + ".json";
        driverS3.save(key, this.model);
        return this.model;
    }

}
