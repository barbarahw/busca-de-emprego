package com.example.demo.Cliente;

import java.net.http.HttpResponse;
import java.util.Scanner;

public class ClienteApp {

    private static String token = null;
    private static String UsernameLogado = null;
    private static String userId = null;

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
            if (token == null) {
                rodando = menuNaoLogado(sc, clienteHttp);
            } else {
                rodando = menuLogado(sc, clienteHttp);
            }

        }
        sc.close();
    }

    private static boolean menuNaoLogado(Scanner sc, ClienteHttp clienteHttp) {
        System.out.println("\n--- MENU ---");
        System.out.println("1 - Cadastrar usuário");
        System.out.println("2 - Login");
        System.out.println("0 - Sair");
        System.out.print("Escolha uma opção: ");
        String opcao = sc.nextLine().trim();

        switch (opcao) {
            case "1":
                cadastrar(sc, clienteHttp);
                return true;

            case "2":
                fazerLogin(sc, clienteHttp);
                return true;

            case "0":
                System.out.println("Encerrando cliente...");
                return false;

            default:
                System.out.println("Opção inválida!");
                return true;
        }
    }

    private static boolean menuLogado(Scanner sc, ClienteHttp clienteHttp) {
        System.out.println("\n--- MENU DO USUÁRIO---");
        System.out.println("1 - ver meus dados");
        System.out.println("2 - Editar perfil");
        System.out.println("3 - Excluir conta");
        System.out.println("4 - Logout");
        System.out.print("Escolha uma opção: ");
        String opcao = sc.nextLine().trim();

        switch (opcao) {
            case "1":
                lerDados(clienteHttp);
                return true;
            case "2":
                EditarPerfil(sc, clienteHttp);
                return true;
            case "3":
                ExcluirConta(sc, clienteHttp);
                return true;
            case "4":
                FazerLogout();
                return true;
            default:
                System.out.println("Opção inválida");
                return true;
        }
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

    private static void fazerLogin(Scanner sc, ClienteHttp clienteHttp) {
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

        try {
            HttpResponse<String> response = clienteHttp.fazerLogin(loginJson);
            System.out.println("JSON enviado: " + loginJson);
            System.out.println("Status: " + response.statusCode());
            System.out.println("JSON recebido: " + response.body());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                if (responseBody.contains("\"token\"")) {
                    int start = responseBody.indexOf("\"token\"") + 9;
                    int end = responseBody.indexOf("\"", start);
                    token = responseBody.substring(start, end);
                    UsernameLogado = username;
                    
                    //TO DO: MUDAR O ID PARA O ID DO BANCO
                    userId = username;
                    
                    System.out.println("Login realizado com sucesso");
                }

            }
        } catch (Exception e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
        }
    }

    private static void lerDados(ClienteHttp clienteHttp) {
        System.out.println("\n --- MEUS DADOS ---");
        try {
            HttpResponse<String> response = clienteHttp.lerUsuario(token, userId);
            System.out.println("Status: " + response.statusCode());
            System.out.println("JSON recebido: " + response.body());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void EditarPerfil(Scanner sc, ClienteHttp clienteHttp) {
        System.out.println("\n --- EDITAR PERFIL ---");

        System.out.println("Deixe em branco os campos que não deseja alterar");
        System.out.println("Novo nome: ");
        String name = sc.nextLine();
        System.out.println("Novo email: ");
        String email = sc.nextLine();
        System.out.println("Nova senha: ");
        String senha = sc.nextLine();
        System.out.println("Novo número de celular: ");
        String phone = sc.nextLine();
        System.out.println("Experiencia: ");
        String experience = sc.nextLine();
        System.out.println("Formação: ");
        String education = sc.nextLine();

        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{*");

        boolean firstField = true;

        if (!name.isEmpty()) {
            jsonBuilder.append(String.format("\n \"name\": \"%s\"", name));
            firstField = false;
        }

        if (!email.isEmpty()) {
            if (!firstField) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append(String.format("\n \"email\": \"%s\"", email));
            firstField = false;
        }

        if (!phone.isEmpty()) {
            if (!firstField) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append(String.format("\n \"phone\": \"%s\"", phone));
            firstField = false;
        }

        if (!experience.isEmpty()) {
            if (!firstField) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append(String.format("\n \"experience\": \"%s\"", experience));
            firstField = false;
        }

        if (!education.isEmpty()) {
            if (!firstField) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append(String.format("\n \"education\": \"%s\"", education));
            firstField = false;
        }

        jsonBuilder.append("\n}");

        String json = jsonBuilder.toString();

        try {
            HttpResponse<String> response = clienteHttp.editarUsuario(token, json, userId);
            System.out.println("Status: " + response.statusCode());
            System.out.println("JSON enviado: " + json);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void ExcluirConta(Scanner sc, ClienteHttp clienteHttp) {
        System.out.println("\n --- EXCLUIR CONTA ---");
        System.out.println("Tem certeza que deseja excluir sua conta? (s/n)");
        String confirmacao = sc.nextLine().trim();

        if (confirmacao.equalsIgnoreCase("s")) {
            try {
                HttpResponse<String> response = clienteHttp.excluirUsuario(token, userId);
                System.out.println("Status: " + response.statusCode());
                System.out.println("JSON recebido: " + response.body());
                
                if (response.statusCode() == 200){
                    FazerLogout();
                    System.out.println("Conta excluída com sucesso!");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Exclusão cancelada");
        }
    }

    private static void FazerLogout() {
        token = null;
        UsernameLogado = null;
        System.out.println("");
    }

}
