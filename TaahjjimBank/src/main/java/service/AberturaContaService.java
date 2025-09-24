package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.AberturaContaModel;
import model.ContaBancariaModel;
import service.interfaces.iCrudService;
import util.*;

import java.util.List;

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
        validarDuplicidadeCpf(cpf);
        validarDuplicidadeContaBancariaPorCpf(cpf);
        String key = Consts.PATH_ABERTURA_CONTA + cpf + "/" + cpf + ".json";
        driverS3.save(key, this.model);
        return this.model;
    }

    private void validarDuplicidadeCpf(String cpf) {
        String key = Consts.PATH_ABERTURA_CONTA + cpf;
        if (driverS3.read(key).isPresent()) {
            throw new IllegalArgumentException(MensagensErro.CPF_DUPLICADO + model.getCpf());
        }
    }

    private void validarDuplicidadeContaBancariaPorCpf(String cpf) {
        ContaBancariaService contaBancariaService = new ContaBancariaService(Consts.BUCKET, null);
        List<ContaBancariaModel> contas = contaBancariaService.listar();
        if (contas.stream().anyMatch(c -> c.getCpf().equals(cpf))) {
            throw new IllegalArgumentException(MensagensErro.CONTA_DUPLICADA + "Para o cpf: " + model.getCpf());
        }
    }
}
