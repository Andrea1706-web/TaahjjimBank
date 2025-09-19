package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.AberturaContaModel;
import service.interfaces.iCrudService;
import util.*;

public class AberturaContaService implements iCrudService<AberturaContaModel>  {

    private final DriverS3<AberturaContaModel> driverS3;
    private final ObjectMapper objectMapper;
    private final AberturaContaModel model;

    public AberturaContaService(String bucketName, String body) {
        this.driverS3 = new DriverS3<>(bucketName, AberturaContaModel.class);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        if (body != null && !body.trim().isEmpty()) {
            try {
                this.model = objectMapper.readValue(body, AberturaContaModel.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(MensagensErro.ERRO_DESERIALIZACAO, e);
            }
        } else {
            this.model = null;
        }
    }

    @Override
    public AberturaContaModel obter(String cpf) {
        String key = Consts.PATH_ABERTURA_CONTA + cpf + "/" + cpf + ".json";
        return driverS3.read(key).orElse(null);
    }

    public AberturaContaModel criar() {
        ValidationUtil.validar(this.model);
        String cpf = this.model.getCpf();
        String key = Consts.PATH_ABERTURA_CONTA + cpf + "/" + cpf + ".json";
        driverS3.save(key, this.model);
        return this.model;
    }
}
