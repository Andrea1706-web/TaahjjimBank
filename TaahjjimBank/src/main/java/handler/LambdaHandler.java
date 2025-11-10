package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.ProdutoModel;
import service.DriverS3;
import service.ProdutoService;
import service.interfaces.iListarService;
import service.interfaces.iDriverS3;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/* trata /produto pelo novo padrão (iStorageDriver)) e mantém compatibilidade */
public class LambdaHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private static final String BUCKET = System.getenv().getOrDefault("S3_BUCKET_NAME", "zupbankdatabase");

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

        String path = (String) event.get("path");
        String bodyJson = (String) event.get("body");

        try {
            // PRODUTO - novo padrão
            if (path != null && path.contains("produto")) {
                AmazonS3 amazonS3 = AmazonS3ClientBuilder.defaultClient();
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());

                iDriverS3<ProdutoModel> driver = new DriverS3<>(
                        amazonS3,
                        BUCKET,
                        ProdutoModel.class,
                        mapper
                );

                // injeta no service
                ProdutoService produtoService = new ProdutoService(driver);

                if ("GET".equalsIgnoreCase(httpMethod)) {
                    Object resultado;
                    if (idRegistro == null) {
                        resultado = produtoService.listar();
                    } else {
                        resultado = produtoService.obter(idRegistro);
                    }
                    if (resultado == null) {
                        return criarResposta(404, "Registro não encontrado");
                    }
                    return criarResposta(200, objectMapper.writeValueAsString(resultado));
                }

                if ("POST".equalsIgnoreCase(httpMethod)) {
                    if (bodyJson == null || bodyJson.trim().isEmpty()) {
                        return criarResposta(400, "Body não informado");
                    }
                    ProdutoModel model = objectMapper.readValue(bodyJson, ProdutoModel.class);
                    ProdutoModel criado = produtoService.criar(model);
                    return criarResposta(201, objectMapper.writeValueAsString(criado));
                }

                return criarResposta(405, "Método HTTP não suportado para /produto");
            }

            // RESTO DOS SERVIÇOS - sem alteração (fluxo antigo)
            Object serviceObj = null;
            Class<?> serviceClass = null;

            if (path != null && path.contains("cartao")) {
                serviceClass = service.CartaoService.class;
            } else if (path != null && path.contains("contabancaria")) {
                serviceClass = service.ContaBancariaService.class;
            } else if (path != null && path.contains("transacao")) {
                serviceClass = service.TransacaoService.class;
            } else if (path != null && path.contains("usuario")) {
                serviceClass = service.UsuarioService.class;
            } else if (path != null && path.contains("login")) {
                serviceClass = service.LoginService.class;
            } else {
                return criarResposta(404, "Serviço não encontrado");
            }

            try {
                serviceObj = serviceClass.getConstructor(String.class, String.class).newInstance(BUCKET, bodyJson);
            } catch (NoSuchMethodException nsme) {
                try {
                    serviceObj = serviceClass.getConstructor().newInstance();
                } catch (NoSuchMethodException ex) {
                    return criarResposta(500, "Construtor compatível não encontrado para " + serviceClass.getSimpleName());
                }
            }

            if ("GET".equalsIgnoreCase(httpMethod)) {
                Object resultado;
                if (idRegistro == null && (serviceObj instanceof iListarService)) {
                    resultado = ((iListarService<?>) serviceObj).listar();
                } else {
                    Method obterMethod = serviceObj.getClass().getMethod("obter", String.class);
                    resultado = obterMethod.invoke(serviceObj, idRegistro);
                }

                if (resultado == null) {
                    return criarResposta(404, "Registro não encontrado");
                }
                return criarResposta(200, objectMapper.writeValueAsString(resultado));
            }

            if ("POST".equalsIgnoreCase(httpMethod)) {
                if (bodyJson != null && !bodyJson.trim().isEmpty()) {
                    Method criarComParametro = null;
                    for (Method m : serviceObj.getClass().getMethods()) {
                        if ("criar".equals(m.getName()) && m.getParameterCount() == 1) {
                            criarComParametro = m;
                            break;
                        }
                    }
                    if (criarComParametro != null) {
                        Class<?> paramType = criarComParametro.getParameterTypes()[0];
                        Object paramObject = objectMapper.readValue(bodyJson, paramType);
                        Object novo = criarComParametro.invoke(serviceObj, paramObject);
                        return criarResposta(201, objectMapper.writeValueAsString(novo));
                    }
                }

                Method criarNoParam = serviceObj.getClass().getMethod("criar");
                Object novoObj = criarNoParam.invoke(serviceObj);
                return criarResposta(201, objectMapper.writeValueAsString(novoObj));
            }

            return criarResposta(405, "Método HTTP não suportado");
        } catch (Exception e) {
            e.printStackTrace();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTrace = sw.toString();
            return criarResposta(500, "Erro interno: " + stackTrace);
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
