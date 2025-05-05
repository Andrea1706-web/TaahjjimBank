package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import factory.ModelFactory;
import factory.ModelFactoryImpl;
import model.CartaoModel;
import model.ContaBancariaModel;
import service.CartaoService;
import service.ContaBancariaService;
import service.CrudService;

import java.util.HashMap;
import java.util.Map;

// Classe principal que manipula requisições da AWS Lambda
// Atua como controller REST improvisado dentro do ambiente serverless
public class LambdaHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private final ModelFactory modelFactory;

    // Inicializa a fábrica de modelos com a implementação concreta
    public LambdaHandler() {
        this.modelFactory = new ModelFactoryImpl();
    }

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {
        // Extrai dados da requisição recebida pela Lambda
        String path = (String) event.get("path");
        String httpMethod = (String) event.get("httpMethod");
        String bodyJson = (String) event.get("body");
        String chave = (String) event.get("chave");

        ObjectMapper objectMapper = new ObjectMapper();
        // Seleciona a implementação de serviço com base no path
        CrudService service = serviceFactory(path);

        if (service == null) {
            return criarResposta(404, "Serviço não encontrado");
        }

        try {
            // Se o método for GET, busca a entidade pela chave e retorna no body da resposta
            if ("GET".equalsIgnoreCase(httpMethod)) {
                Object resultado = service.obter(chave);
                return criarResposta(200, objectMapper.writeValueAsString(resultado));

            // Se o método for POST, cria um novo recurso com base no body recebido
            } else if ("POST".equalsIgnoreCase(httpMethod)) {
                Map<String, Object> bodyMap = objectMapper.readValue(bodyJson, Map.class);
                // Criação de cartão
                if (path.contains("cartao")) {
                    CartaoModel cartao = modelFactory.criarCartao(bodyMap);
                    service.criar(cartao);
                // Criação de conta bancária
                } else if (path.contains("conta-bancaria")) {
                    ContaBancariaModel conta = modelFactory.criarContaBancaria(bodyMap);
                    service.criar(conta);
                }

                return criarResposta(201, "Criado com sucesso");
            }

            return criarResposta(405, "Método HTTP não suportado");

        } catch (Exception e) {
            e.printStackTrace();
            return criarResposta(500, "Erro interno: " + e.getMessage());
        }
    }

    // Cria instâncias de serviços com base no path da requisição
    private CrudService serviceFactory(String path) {
        if (path.contains("cartao")) {
            return new CartaoService("zupbankdatabase");
        } else if (path.contains("conta-bancaria")) {
            return new ContaBancariaService("zupbankdatabase");
        }
        return null;
    }

    // Constrói e retorna o objeto de resposta da Lambda
    private Map<String, Object> criarResposta(int statusCode, String body) {
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", statusCode);
        response.put("headers", Map.of("Content-Type", "application/json"));
        response.put("body", body);
        response.put("isBase64Encoded", false);
        return response;
    }
}
