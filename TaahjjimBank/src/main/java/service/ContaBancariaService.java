package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.ContaBancariaModel;
import model.TipoConta;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ContaBancariaService {
    private final DriverS3<ContaBancariaModel> driverContaBancaria;
    private final ObjectMapper objectMapper;
    private final String PATH = "dados/contaBancaria/";

    public ContaBancariaService(String bucketName) {
        this.driverContaBancaria = new DriverS3<>(bucketName, ContaBancariaModel.class);
        this.objectMapper = new ObjectMapper();
    }

    public String criar(Map<String, Object> payload) throws JsonProcessingException {
        ContaBancariaModel contaBancaria = new ContaBancariaModel(
                null,
                (int) payload.get("agencia"),
                (String) payload.get("numeroCC"),
                (double) payload.get("saldo"),
                (String) payload.get("cpf"),
                (TipoConta) payload.get("tipoConta")
        );

        String key = PATH + contaBancaria.getNumeroCC() + ".json";
        driverContaBancaria.save(key, contaBancaria);
        return objectMapper.writeValueAsString(contaBancaria);
    }
}
