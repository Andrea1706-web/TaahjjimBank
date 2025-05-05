package factory;

import model.CartaoModel;
import model.ContaBancariaModel;
import util.MapperUtil;
import java.util.Map;

// Implementação concreta da interface ModelFactory, responsável por transformar os payloads JSON em instâncias de modelo
public class ModelFactoryImpl implements ModelFactory {

    @Override
    public CartaoModel criarCartao(Map<String, Object> body) {
        // Converte o Map em uma instância de CartaoModel
        return MapperUtil.toModel(body, CartaoModel.class);
    }

    @Override
    public ContaBancariaModel criarContaBancaria(Map<String, Object> body) {
        return MapperUtil.toModel(body, ContaBancariaModel.class);
    }
}
