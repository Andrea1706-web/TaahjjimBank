package factory;

import model.CartaoModel;
import model.ContaBancariaModel;

import java.util.Map;

// Interface que define o contrato para criação de modelos fábrica de objetos
// Encapsula a lógica de instanciamento de modelos a partir dos dados
public interface ModelFactory {
    CartaoModel criarCartao(Map<String, Object> body);
    ContaBancariaModel criarContaBancaria(Map<String, Object> body);
}
