package service;

import model.ProdutoModel;
import org.springframework.stereotype.Service;
import service.interfaces.iListarService;
import service.interfaces.iDriverS3;
import util.*;

import java.util.List;

@Service
public class ProdutoService implements iListarService<ProdutoModel> {

    private final iDriverS3<ProdutoModel> storageDriver;

    //recebe o driver por injeção no construtor
    public ProdutoService(iDriverS3<ProdutoModel> storageDriver) {
        this.storageDriver = storageDriver;
    }

    @Override
    public ProdutoModel obter(String nome) {
        String key = Consts.PATH_PRODUTO + nome + ".json";
        return storageDriver.read(key).orElse(null);
    }

    @Override
    public List<ProdutoModel> listar() {
        return storageDriver.readAll(Consts.PATH_PRODUTO);
    }

    public ProdutoModel criar(ProdutoModel model) {
        ValidationUtil.validar(model);
        validarDuplicidade(model);
        String key = Consts.PATH_PRODUTO + model.getNome() + ".json";
        storageDriver.save(key, model);
        return model;
    }

    private void validarDuplicidade(ProdutoModel model) {
        List<ProdutoModel> produtos = listar();
        if (produtos.stream().anyMatch(p -> p.getNome().equalsIgnoreCase(model.getNome()))) {
            throw new IllegalArgumentException(MensagensErro.PRODUTO_DUPLICADO + model.getNome());
        }
    }
}