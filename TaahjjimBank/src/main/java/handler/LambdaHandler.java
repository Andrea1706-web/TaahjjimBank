
package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.ContaBancariaModel;
import service.CartaoService;
import service.ContaBancariaService;
import util.Validation;

import javax.validation.ConstraintViolationException;
import java.util.Map;
import java.util.HashMap;

public class LambdaHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {
    private final CartaoService cartaoService;
    private final ContaBancariaService contaBancariaService;

    public LambdaHandler() {
        this.cartaoService = new CartaoService("zupbankdatabase");
        this.contaBancariaService = new ContaBancariaService("zupbankdatabase");
    }

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {
        String path = (String) event.get("path");
        String httpMethod = (String) event.get("httpMethod");
        ObjectMapper objectMapper = new ObjectMapper();
        
        try {
            String response;

            if (path.equalsIgnoreCase("/contabancaria")) {
                if ("POST".equalsIgnoreCase(httpMethod)) {
                    String body = (String) event.get("body");
//                    Map<String, Object> bodyMap = objectMapper.readValue(body, Map.class);
                    ContaBancariaModel conta = objectMapper.readValue(body, ContaBancariaModel.class);
                    Validation.validar(conta);
                    response = contaBancariaService.criar(conta);
                } else {
                    return criarResposta(405, "Método não permitido");
                }
                return criarResposta(201, response);
            }

            if ("GET".equalsIgnoreCase(httpMethod)) {
                response = cartaoService.obter();
            } else if ("POST".equalsIgnoreCase(httpMethod)) {
                String body = (String) event.get("body");
                Map<String, Object> bodyMap = objectMapper.readValue(body, Map.class);
                response = cartaoService.criar(bodyMap);
            } else {
                return criarResposta(405, "Método não permitido");
            }
            return criarResposta(200, response);
        } catch (ConstraintViolationException e) {
            return criarResposta(400, e.getMessage());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return criarResposta(500, "Erro ao processar JSON");
        }
    }

    private Map<String, Object> criarResposta(int statusCode, String body) {
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", statusCode);
        response.put("headers", Map.of("Content-Type", "application/json"));
        response.put("body", body);
        response.put("isBase64Encoded", false);
        return response;
    }
}
