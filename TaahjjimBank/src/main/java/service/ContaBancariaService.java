package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import model.ContaBancariaModel;
import org.springframework.stereotype.Service;

@Service
public class ContaBancariaService implements CrudService<ContaBancariaModel> {
    private final DriverS3<ContaBancariaModel> driverS3;
    private final ObjectMapper objectMapper;
    private final String PATH = "dados/contaBancaria/";
    private final ContaBancariaModel model;

    public ContaBancariaService(String bucketName, String body) {
        this.driverS3 = new DriverS3<>(bucketName, ContaBancariaModel.class);
        this.objectMapper = new ObjectMapper();
        ContaBancariaModel bodyMap;
        try {
            bodyMap = objectMapper.readValue(body, ContaBancariaModel.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao deserializar bodyJson", e);
        }

        this.model = bodyMap;
    }

    @Override
    public ContaBancariaModel obter(String numeroConta) {
        String key = PATH + numeroConta + ".json";
        return driverS3.read(key).orElse(null);
    }

    @Override
    public ContaBancariaModel criar() {
        String key = PATH + this.model.getNumeroCC() + ".json";
        driverS3.save(key, this.model);
        return this.model;
    }
}
