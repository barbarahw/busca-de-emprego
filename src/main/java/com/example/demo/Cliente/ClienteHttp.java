package com.example.demo.Cliente;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
    
    public HttpResponse<String> fazerLogin (String loginJson) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(loginJson))
                .build();
        
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
    
    public HttpResponse<String> lerUsuario(String token, String userId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/users" + userId))
                .header("Authorization", "Bearer" + token)
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    HttpResponse<String> editarUsuario(String token, String json, String userId) throws Exception{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/users" + userId))
                .header("Authorization", "Bearer" + token)
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    HttpResponse<String> excluirUsuario(String token, String userId) throws Exception{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/users" + userId))
                .header("Authorization", "Bearer" + token)
                .header("Content-Type", "application/json")
                .DELETE()
                .build();
        
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
