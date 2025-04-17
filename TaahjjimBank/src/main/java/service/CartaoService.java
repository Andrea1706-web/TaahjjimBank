package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import handler.LambdaHandler;
import model.CartaoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.amazonaws.services.lambda.runtime.Context;

@Service
public class CartaoService {

// @Autowired
// private LambdaHandler lambdaHandler;

// Método para exibir todos os cartões
    public List<CartaoModel> exibirTodos() {
        // Configura o evento para chamar o LambdaHandler
        Map<String, Object> event = Map.of(
                "path", "/cartao",
                "httpMethod", "GET"
        );
        Context context = null; // Pode ser um mock de Context, se necessário

        // Chama o LambdaHandler e processa a resposta
        String response = "teste";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response, new TypeReference<List<CartaoModel>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao processar JSON: " + e.getMessage());
        }
    }

    // Método para cadastrar um novo cartão
    public CartaoModel cadastrarCartao(CartaoModel cartaoModel) {
        // Configura o evento para chamar o LambdaHandler
        Map<String, Object> event = Map.of(
                "path", "/cartao",
                "httpMethod", "POST",
                "body", Map.of(
                        "numeroCartao", cartaoModel.getNumeroCartao(),
                        "validade", cartaoModel.getValidade(),
                        "codigo", cartaoModel.getCodigo(),
                        "numeroConta", cartaoModel.getNumeroConta()
                )
        );
        Context context = null; // Pode ser um mock de Context, se necessário

        // Chama o LambdaHandler e processa a resposta
       String response = "teste 2";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response, CartaoModel.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao processar JSON: " + e.getMessage());
        }
    }
}
