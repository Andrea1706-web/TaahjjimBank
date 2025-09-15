package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class QuickCommandUtil {

    private static final String TOKEN_URL = "https://idm.stackspot.com/itau/oidc/oauth/token";

    private final String clientId;
    private final String clientSecret;
    private final String postUrl;          
    private final String getUrlTemplate;   
    private String token;

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public QuickCommandUtil(String clientId, String clientSecret, String postUrl, String getUrlTemplate) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.postUrl = postUrl;
        this.getUrlTemplate = getUrlTemplate;
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
                .url(postUrl)
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
        String url = String.format(getUrlTemplate, executionId);

        long startTime = System.currentTimeMillis();

        do {
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
        } while (System.currentTimeMillis() - startTime < timeout * 1000L);

        throw new IOException("Timeout ao monitorar a execução do QuickCommand.");
    }
}
