package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import service.*;
import service.interfaces.*;

import java.io.*;
import java.util.*;


public class LambdaAberturaContaHadler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String httpMethod = (String) event.get("httpMethod");

        Map<String, String> pathParameters = (Map<String, String>) event.get("pathParameters");
        String idRegistro = null;
        if (pathParameters != null) {
            idRegistro = pathParameters.get("id");
        }

        iCrudService service = serviceFactory(event);
        if (service == null) {
            return criarResposta(404, "Serviço não encontrado");
        }
        try {
            if ("GET".equalsIgnoreCase(httpMethod)) {
                Object resultado = (idRegistro == null && service instanceof iListarService)
                        ? ((iListarService<?>) service).listar()
                        : service.obter(idRegistro);

                if (resultado == null) {
                    return criarResposta(404, "Registro não encontrado");
                }

                return criarResposta(200, objectMapper.writeValueAsString(resultado));
            }
            if ("POST".equalsIgnoreCase(httpMethod)) {
                Object novoObjeto = service.criar();
                return criarResposta(201, objectMapper.writeValueAsString(novoObjeto));
            }
            return criarResposta(405, "Método HTTP não suportado");
        } catch (IllegalArgumentException e) {
            return criarResposta(409, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            // Para log detalhado (stack trace completo):
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTrace = sw.toString();

            return criarResposta(500, "Erro interno: " + stackTrace);
        }
    }

    private iCrudService serviceFactory(Map<String, Object> event) {
        String path = (String) event.get("path");
        String bodyJson = (String) event.get("body");
        if (path.contains("contabancaria/open")) {
            return new AberturaContaService("zupbankdatabase", bodyJson);
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
    /* teste pipeline */
}
