package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import model.ProdutoModel;
import org.springframework.stereotype.Service;
import service.interfaces.iListarService;
import util.*;

import java.util.List;

@Service
public class ProdutoService implements iListarService {

    private final DriverS3<ProdutoModel> driverS3;
    private final ObjectMapper objectMapper;
    private final ProdutoModel model;

    public ProdutoService(String bucketName, String bodyJson) {
        this.driverS3 = new DriverS3<>(bucketName, ProdutoModel.class);
        this.objectMapper = new ObjectMapper();

        if (bodyJson != null && !bodyJson.trim().isEmpty()) {
            try {
                this.model = objectMapper.readValue(bodyJson, ProdutoModel.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(MensagensErro.ERRO_DESERIALIZACAO, e);
            }
        } else {
            this.model = null;
        }
    }

    @Override
    public ProdutoModel obter(String nome) {
        String key = Consts.PATH_PRODUTO + nome + ".json";
        return driverS3.read(key).orElse(null);
    }

    @Override
    public List<ProdutoModel> listar() {
        return driverS3.readAll(Consts.PATH_PRODUTO);
    }

    @Override
    public ProdutoModel criar() {
        ValidationUtil.validar(this.model);

        validarDuplicidade(this.model);
        String key = Consts.PATH_PRODUTO + model.getNome() + ".json";
        driverS3.save(key, model);
        return model;
    }

    private void validarDuplicidade(ProdutoModel model) {
        List<ProdutoModel> produtos = listar();

        if (produtos.stream().anyMatch(p -> p.getNome().equalsIgnoreCase(model.getNome()))) {
            throw new IllegalArgumentException(MensagensErro.PRODUTO_DUPLICADO + model.getNome());
        }
    }
}