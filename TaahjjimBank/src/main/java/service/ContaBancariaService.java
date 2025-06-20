package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import model.ContaBancariaModel;
import org.springframework.stereotype.Service;
import util.ValidationUtil;

@Service
public class ContaBancariaService implements iCrudService<ContaBancariaModel> {
    private final DriverS3<ContaBancariaModel> driverS3;
    private final ObjectMapper objectMapper;
    private final String PATH = "dados/contaBancaria/";
    private final ContaBancariaModel model;

    public ContaBancariaService(String bucketName, String body) {
        this.driverS3 = new DriverS3<>(bucketName, ContaBancariaModel.class);
        this.objectMapper = new ObjectMapper();

        if (body != null && !body.trim().isEmpty()) {
            try {
                this.model = objectMapper.readValue(body, ContaBancariaModel.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Erro ao deserializar bodyJson", e);
            }
        } else {
            this.model = null;
        }
    }

    @Override
    public ContaBancariaModel obter(String numeroConta) {
        String key = PATH + numeroConta + ".json";
        return driverS3.read(key).orElse(null);
    }

    @Override
    public ContaBancariaModel criar() {
        ValidationUtil.validar(this);
        String key = PATH + this.model.getNumeroCC() + ".json";
        driverS3.save(key, this.model);
        return this.model;
    }
}
