
package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.CartaoModel;
import model.ContaBancariaModel;
import service.CartaoService;
import service.ContaBancariaService;
import util.MapperUtil;

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
                    Map<String, Object> bodyMap = objectMapper.readValue(body, Map.class);

                    ContaBancariaModel conta = MapperUtil.toModel(bodyMap, ContaBancariaModel.class);
                    contaBancariaService.criar(conta);

                    response = objectMapper.writeValueAsString(conta);
                    return criarResposta(201, response);
                } else {
                    return criarResposta(405, "Método não permitido");
                }
            }

            if (path.equalsIgnoreCase("/cartao")) {
                if ("GET".equalsIgnoreCase(httpMethod)) {
                    Map<String, String> queryParams = (Map<String, String>) event.get("queryStringParameters");
                    String numeroCartao = queryParams.get("numeroCartao");
                    CartaoModel cartao = cartaoService.obter(numeroCartao);
                    response = objectMapper.writeValueAsString(cartao);
                    return criarResposta(200, response);
                } else if ("POST".equalsIgnoreCase(httpMethod)) {
                    String body = (String) event.get("body");
                    Map<String, Object> bodyMap = objectMapper.readValue(body, Map.class);

                    CartaoModel cartao = MapperUtil.toModel(bodyMap, CartaoModel.class);
                    cartaoService.criar(cartao);

                    response = objectMapper.writeValueAsString(cartao);
                    return criarResposta(201, response);
                } else {
                    return criarResposta(405, "Método não permitido");
                }
            }

            return criarResposta(404, "Path não encontrado");
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