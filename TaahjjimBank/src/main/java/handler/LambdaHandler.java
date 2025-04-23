package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import service.CartaoService;

import java.util.Map;
import java.util.HashMap;

public class LambdaHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {
    private final CartaoService cartaoService;

    public LambdaHandler() {
        this.cartaoService = new CartaoService("zupbankdatabase");
    }

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {
        String path = (String) event.get("path");
        String httpMethod = (String) event.get("httpMethod");
        try {
            String response;
            if ("GET".equalsIgnoreCase(httpMethod)) {
                response = cartaoService.obter();
            } else if ("POST".equalsIgnoreCase(httpMethod)) {
                response = cartaoService.criar((Map<String, Object>) event.get("body"));
            } else {
                return criarResposta(405, "Método não permitido");
            }
            return criarResposta(200, response);
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
