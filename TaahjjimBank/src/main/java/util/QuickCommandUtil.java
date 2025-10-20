package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class QuickCommandUtil {
    private static final String TOKEN_URL = "https://idm.stackspot.com/stackspot-freemium/oidc/oauth/token";
    private final String clientId;
    private final String clientSecret;
    private final String executionUrl;
    private final String callbackUrl;
    private String token;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public QuickCommandUtil(String clientId, String clientSecret, String executionUrl, String callbackUrl, String token) {
        this.clientId = Objects.requireNonNull(clientId, "clientId não pode ser nulo");
        this.clientSecret = Objects.requireNonNull(clientSecret, "clientSecret não pode ser nulo");
        this.executionUrl = Objects.requireNonNull(executionUrl, "executionUrl não pode ser nulo");
        this.callbackUrl = Objects.requireNonNull(callbackUrl, "callbackUrl não pode ser nulo");
        this.token = token;
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }


     // Gera o token de acesso usando client credentials.

    public void generateToken() throws IOException {
        RequestBody formBody = new FormBody.Builder()
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
                throw new IOException("Erro ao gerar token: " + response.code() + " - " + response.message());
            }
            if (response.body() == null) {
                throw new IOException("Resposta sem corpo ao gerar token.");
            }
            JsonNode jsonNode = objectMapper.readTree(response.body().string());
            this.token = jsonNode.get("access_token").asText();
        }
    }


     //Executa um QuickCommand.

    public String executeQuickCommand(String payload) throws IOException {
        ensureToken();
        RequestBody body = RequestBody.create(payload, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(executionUrl)
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erro ao executar QuickCommand: " + response.code() + " - " + response.message());
            }
            if (response.body() == null) {
                throw new IOException("Resposta sem corpo ao executar QuickCommand.");
            }
            JsonNode jsonNode = objectMapper.readTree(response.body().string());
            return jsonNode.get("execution_id").asText();
        }
    }


     // Monitora a execução até o status desejado.

    public String monitorExecution(String executionId, int interval, String stopStatus) throws IOException, InterruptedException {
        ensureToken();
        String url = String.format(callbackUrl, executionId);
        while (true) {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Erro ao monitorar execução: " + response.code() + " - " + response.message());
                }
                if (response.body() == null) {
                    throw new IOException("Resposta sem corpo ao monitorar execução.");
                }
                JsonNode jsonNode = objectMapper.readTree(response.body().string());
                String status = jsonNode.get("status").asText();
                if (stopStatus.equalsIgnoreCase(status)) {
                    return jsonNode.toString();
                }
            }
            TimeUnit.SECONDS.sleep(interval);
        }
    }

     // Garante que o token foi gerado.

    private void ensureToken() {
        if (token == null) {
            throw new IllegalStateException("Token não gerado.");
        }
    }

    // Executa e monitora o QuickCommand até o status desejado.
    public String runQuickCommand(String payload, int interval, String stopStatus) throws IOException, InterruptedException {

        String executionId = executeQuickCommand(payload);

        return monitorExecution(executionId, interval, stopStatus);
    }
}
