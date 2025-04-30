package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.ContaBancariaModel;
import org.springframework.stereotype.Service;

/*
Implementa a interface CrudService<ContaBancariaModel>, definindo os métodos obter(String numeroConta) e criar(ContaBancariaModel conta).
A persistência no S3 é realizada diretamente nesses métodos, onde:
- obter: busca a conta bancária no S3 usando o número da conta como chave.
- criar: salva uma nova conta bancária no S3, utilizando o número da conta como chave para armazená-la.
*/

@Service
public class ContaBancariaService implements CrudService<ContaBancariaModel> {
    private final DriverS3<ContaBancariaModel> driverS3;
    private final ObjectMapper objectMapper;

    public ContaBancariaService(String bucketName) {
        this.driverS3 = new DriverS3<>(bucketName, ContaBancariaModel.class);
        this.objectMapper = new ObjectMapper();
    }
    @Override
    public ContaBancariaModel obter(String numeroConta) {
        String key = "dados/contaBancaria/" + numeroConta + ".json";
        return driverS3.read(key).orElse(null);
    }

    @Override
    public void criar(ContaBancariaModel conta) {
        String key = "dados/contaBancaria/" + conta.getNumeroCC() + ".json";
        driverS3.save(key, conta);
    }
}
