package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.CartaoModel;
import org.springframework.stereotype.Service;
import service.interfaces.iCrudService;
import util.MensagensErro;
import util.ValidationUtil;

import java.util.List;

@Service
public class CartaoService implements iCrudService<CartaoModel> {

    private final DriverS3<CartaoModel> driverS3;
    private final ObjectMapper objectMapper;
    private static final String PATH = "dados/cartao/";
    private final CartaoModel model;

    public CartaoService(String bucketName, String body) {
        this.driverS3 = new DriverS3<>(bucketName, CartaoModel.class);
        this.objectMapper = new ObjectMapper();

        if (body != null && !body.trim().isEmpty()) {
            try {
                this.model = objectMapper.readValue(body, CartaoModel.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(MensagensErro.ERRO_DESERIALIZACAO, e);
            }
        } else {
            this.model = null;
        }

    }

    @Override
    public CartaoModel obter(String numeroCartao) {
        String key = PATH + numeroCartao + ".json";
        return driverS3.read(key).orElse(null);
    }

    @Override
    public CartaoModel criar() {
        ValidationUtil.validar(this.model); // Valida o model antes de persistir
        String key = PATH + this.model.getNumeroCartao() + ".json";
        validarDuplicidade(this.model);
        driverS3.save(key, model);
        return model;
    }

    // Validação de duplicidade
    private void validarDuplicidade(CartaoModel model) {
        List<CartaoModel> cartoes = driverS3.readAll(PATH);
        if (cartoes.stream().anyMatch(c -> c.getId().equals(model.getId()))) {
            throw new IllegalArgumentException(MensagensErro.CARTAO_ID_DUPLICADO + model.getId());
        }
        if (cartoes.stream().anyMatch(c -> c.getNumeroCartao().equalsIgnoreCase(model.getNumeroCartao()))) {
            throw new IllegalArgumentException(MensagensErro.CARTAO_NUMERO_DUPLICADO + model.getNumeroCartao());
        }
    }

}
