package com.example.demo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class ClienteApp {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Digite o IP do servidor: ");
        String ip = sc.nextLine().trim();

        System.out.print("Digite a porta do servidor: ");
        String porta = sc.nextLine().trim();

        String baseUrl = "http://" + ip + ":" + porta;

        HttpClient client = HttpClient.newHttpClient();

        boolean rodando = true;
        while (rodando) {
            System.out.println("\n--- MENU ---");
            System.out.println("1 - Cadastrar usuário");
            System.out.println("2 - Sair");
            System.out.print("Escolha uma opção: ");
            String opcao = sc.nextLine().trim();

            switch (opcao) {
                case "1":
                    System.out.print("Nome: ");
                    String name = sc.nextLine();
                    System.out.print("Username: ");
                    String username = sc.nextLine();
                    System.out.print("Password: ");
                    String password = sc.nextLine();
                    System.out.print("Email (opcional): ");
                    String email = sc.nextLine();
                    System.out.print("Phone (opcional): ");
                    String phone = sc.nextLine();

                    String json = String.format("""
                            {
                              "name": "%s",
                              "username": "%s",
                              "password": "%s",
                              "email": "%s",
                              "phone": "%s"
                            }
                            """, name, username, password, email, phone);

                    try {
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(baseUrl + "/users"))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(json))
                                .build();

                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        // Imprime o JSON enviado
                        System.out.println("JSON enviado: " + json);

                        // Imprime o status HTTP
                        System.out.println("Status: " + response.statusCode());

                        // Imprime o JSON recebido do servidor
                        System.out.println("JSON recebido: " + response.body());
                        
                    } catch (Exception e) {
                        System.out.println("Erro ao conectar: " + e.getMessage());
                    }
                    break;

                case "2":
                    rodando = false;
                    System.out.println("Encerrando cliente...");
                    break;

                default:
                    System.out.println("Opção inválida!");
            }
        }

        sc.close();
    }
}
