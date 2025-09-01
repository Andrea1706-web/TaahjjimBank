package util;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class QuickCommandTransacoes {

    private static final String TOKEN_URL = "https://idm.stackspot.com/itau/oidc/oauth/token";
    private static final String POST_URL = "https://genai-code-buddy-api.stackspot.com/v1/quick-commands/create-execution/analisatransacao";
    private static final String GET_URL_TEMPLATE = "https://genai-code-buddy-api.stackspot.com/v1/quick-commands/callback/%s";

    private final String clientId;
    private final String clientSecret;
    private String token;

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public QuickCommandTransacoes(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    private void generateToken() throws IOException {
        FormBody formBody = new FormBody.Builder()
                .add("client_id", clientId)
                .add("grant_type", "client_credentials")
                .add("client_secret", clientSecret)
                .build();

        Request request = new Request.Builder()
                .url(TOKEN_URL)
                .post(formBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erro ao gerar token: " + response.message());
            }

            JsonNode jsonNode = objectMapper.readTree(response.body().string());
            this.token = jsonNode.get("access_token").asText();
        }
    }

    public String executeQuickCommand(String payload) throws IOException {
        if (token == null) {
            generateToken();
        }

        RequestBody body = RequestBody.create(
                payload,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(POST_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erro ao executar QuickCommand: " + response.message());
            }

            JsonNode jsonNode = objectMapper.readTree(response.body().string());
            return jsonNode.get("execution_id").asText();
        }
    }

    public String monitorExecution(String executionId, int timeout, int interval) throws IOException, InterruptedException {
        String url = String.format(GET_URL_TEMPLATE, executionId);

        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeout * 1000L) {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Erro ao monitorar execução: " + response.message());
                }

                JsonNode jsonNode = objectMapper.readTree(response.body().string());
                String status = jsonNode.get("status").asText();

                if ("fraude".equals(status) || "normal".equals(status)) {
                    return status;
                }
            }

            TimeUnit.SECONDS.sleep(interval);
        }

        throw new IOException("Timeout ao monitorar a execução do QuickCommand.");
    }

    public static void main(String[] args) {
        String clientId = "c5213a2b-5277-46ed-84e0-df741c5b60f9";
        String clientSecret = "4P84pefz3U0T199irrUm9CZVly7vSHY4hmg84ey09A2qtLtGJeK03EPOKVYd9BY9";

        QuickCommandTransacoes util = new QuickCommandTransacoes(clientId, clientSecret);

        String payload = "{ \"data\": \"exemplo de dados para análise\" }";

        try {
            String executionId = util.executeQuickCommand(payload);
            System.out.println("Execution ID: " + executionId);

            String status = util.monitorExecution(executionId, 60, 5);
            System.out.println("Status da execução: " + status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
