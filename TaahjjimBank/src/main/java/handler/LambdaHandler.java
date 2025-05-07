package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import service.CartaoService;
import service.ContaBancariaService;
import service.CrudService;

import java.util.HashMap;
import java.util.Map;

public class LambdaHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {

        ObjectMapper objectMapper = new ObjectMapper();

        String httpMethod = (String) event.get("httpMethod");
        String chave = (String) event.get("chave");

        CrudService service = serviceFactory(event);
        if (service == null) {
            return criarResposta(404, "Serviço não encontrado");
        }
        try {
            if ("GET".equalsIgnoreCase(httpMethod)) {
                Object resultado = service.obter(chave);
                return criarResposta(200, objectMapper.writeValueAsString(resultado));
            } else if ("POST".equalsIgnoreCase(httpMethod)) {
                service.criar();
                return criarResposta(201, "Criado com sucesso");
            }
            return criarResposta(405, "Método HTTP não suportado");
        } catch (Exception e) {
            e.printStackTrace();
            return criarResposta(500, "Erro interno: " + e.getMessage());
        }
    }

    private CrudService serviceFactory(Map<String, Object> event) {
        String path = (String) event.get("path");
        String bodyJson = (String) event.get("body");
        if (path.contains("cartao")) {
            return new CartaoService("zupbankdatabase", bodyJson);
        } else if (path.contains("conta-bancaria")) {
            return new ContaBancariaService("zupbankdatabase", bodyJson);
        }
        return null;
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
