package com.example.demo.Cliente;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import org.json.JSONObject;

public class ClienteHttp {

    private final HttpClient client;
    private final String baseUrl;

    public ClienteHttp(String baseUrl) {
        this.client = HttpClient.newHttpClient();
        this.baseUrl = baseUrl;
    }

    public HttpResponse<String> cadastrarUsuario(String json) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        logRequisicao("POST", request, json);

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        logResposta(response);
        return response;
    }

    public HttpResponse<String> fazerLogin(String loginJson) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(loginJson))
                .build();

        logRequisicao("POST", request, loginJson);

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        logResposta(response);
        return response;
    }

    public HttpResponse<String> lerUsuario(String token) throws Exception {
        String userId = extrairToken(token);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/users/" + userId))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        logRequisicao("GET", request, null);

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        logResposta(response);
        return response;
    }

    public HttpResponse<String> editarUsuario(String token, String json) throws Exception {
        String userId = extrairToken(token);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/users/" + userId))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .build();

        logRequisicao("PATCH", request, json);

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        logResposta(response);
        return response;
    }

    public HttpResponse<String> excluirUsuario(String token) throws Exception {
        String userId = extrairToken(token);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/users/" + userId))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        logRequisicao("DELETE", request, null);

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        logResposta(response);
        return response;
    }

    private String extrairToken(String token) {
        try {
            token = token.replace("Bearer ", "").trim();

            String[] partes = token.split("\\.");
            if (partes.length < 2) {
                throw new IllegalArgumentException("Token JWT inválido");
            }

            String payload = new String(Base64.getUrlDecoder().decode(partes[1]));
            JSONObject json = new JSONObject(payload);

            return json.getString("sub");
        } catch (Exception e) {
            System.out.println("[ERRO] Falha ao extrair ID do token: " + e.getMessage());
            return null;
        }
    }

    // -------------------------------
    // 🔍 MÉTODOS DE LOG
    // -------------------------------
    private void logRequisicao(String metodo, HttpRequest request, String body) {
        System.out.println("\n=== [CLIENTE -> SERVIDOR] ===");
        System.out.println("Método: " + metodo);
        System.out.println("URL: " + request.uri());
        System.out.println("Headers enviados: " + request.headers().map());
        if (body != null && !body.isEmpty()) {
            System.out.println("JSON enviado: " + body);
        } else {
            System.out.println("JSON enviado: (nenhum corpo)");
        }
        System.out.println("=============================");
    }

    private void logResposta(HttpResponse<String> response) {
        System.out.println("\n=== [SERVIDOR -> CLIENTE] ===");
        System.out.println("Status: " + response.statusCode());
        System.out.println("Headers recebidos: " + response.headers().map());
        System.out.println("JSON recebido: " + response.body());
        System.out.println("=============================\n");
    }
}
