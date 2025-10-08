package com.example.demo.Cliente;

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
        ClienteHttp clienteHttp = new ClienteHttp(baseUrl);

        boolean rodando = true;
        while (rodando) {
            System.out.println("\n--- MENU ---");
            System.out.println("1 - Cadastrar usuário");
            System.out.println("2 - Login");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");
            String opcao = sc.nextLine().trim();

            switch (opcao) {
                case "1":
                    cadastrar(sc, clienteHttp);
                    break;

                case "2":
                    fazerLogin(sc, clienteHttp);
                    break;
                        
                case "0":
                    rodando = false;
                    System.out.println("Encerrando cliente...");
                    break;

                default:
                    System.out.println("Opção inválida!");
            }
        }
        sc.close();
    }

    private static void cadastrar(Scanner sc, ClienteHttp clienteHttp) {
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
        System.out.println("Experience (opcional): ");
        String experience = sc.nextLine();
        System.out.println("Education (opcional): ");
        String education = sc.nextLine();

        StringBuilder jsonBuilder = new StringBuilder();

        jsonBuilder.append(String.format("""
                    {
                      "name": "%s",
                      "username": "%s",
                      "password": "%s"
                    """, name, username, password));

        if (!email.isEmpty()) {
            jsonBuilder.append(String.format(",\n  \"email\": \"%s\"", email));
        }

        if (!phone.isEmpty()) {
            jsonBuilder.append(String.format(",\n  \"phone\": \"%s\"", phone));
        }

        if (!experience.isEmpty()) {
            jsonBuilder.append(String.format(",\n  \"experience\": \"%s\"", experience));
        }

        if (!education.isEmpty()) {
            jsonBuilder.append(String.format(",\n  \"education\": \"%s\"", education));
        }

        jsonBuilder.append("\n}");

        String json = jsonBuilder.toString();

        try {
            HttpResponse<String> response = clienteHttp.cadastrarUsuario(json);

            System.out.println("JSON enviado: " + json);
            System.out.println("Status: " + response.statusCode());
            System.out.println("JSON recebido: " + response.body());

        } catch (Exception e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
        }
    }
    
    private static void fazerLogin(Scanner sc, ClienteHttp clienteHttp){
        System.out.println("Username: ");
        String username = sc.nextLine();
        System.out.println("Password: ");
        String password = sc.nextLine();
        
        String loginJson = String.format("""
        {
            "username": "%s",
            "password": "%s"
        }                
        """, username, password);
        
        try{
            HttpResponse<String> response = clienteHttp.fazerLogin(loginJson);
            System.out.println("JSON enviado: " + loginJson);
            System.out.println("Status: " + response.statusCode());
            System.out.println("JSON recebido: " + response.body());
            
            if (response.statusCode() == 200){
                
                //parsear json
                String token = response.body();
                //System.out.println(token);
                
            }
        } catch (Exception e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
        }
    }

}
