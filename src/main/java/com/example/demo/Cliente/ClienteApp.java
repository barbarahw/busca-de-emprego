package com.example.demo.Cliente;

import java.net.http.HttpResponse;
import java.util.Scanner;
import org.json.JSONObject;
import com.example.demo.security.JwtUtil;

public class ClienteApp {

    private static String token = null;
    private static String usernameLogado = null;
    private static String tipoUsuario = null; // "USER" ou "COMPANY"
    private static JwtUtil jwtUtil = new JwtUtil();

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
                if ("USER".equals(tipoUsuario)) {
                    rodando = menuLogadoUsuario(sc, clienteHttp);
                } else if ("COMPANY".equals(tipoUsuario)) {
                    rodando = menuLogadoEmpresa(sc, clienteHttp);
                }
            }
        }
        sc.close();
    }

    // ========================================
    // MENU NÃO LOGADO
    // ========================================
    private static boolean menuNaoLogado(Scanner sc, ClienteHttp clienteHttp) {
        System.out.println("\n======= SISTEMA DE VAGAS =======");
        System.out.println("1 - Cadastrar Usuário");
        System.out.println("2 - Cadastrar Empresa");
        System.out.println("3 - Login");
        System.out.println("0 - Sair");
        System.out.println("================================");
        System.out.print("Escolha uma opção: ");
        String opcao = sc.nextLine().trim();

        switch (opcao) {
            case "1":
                cadastrarUsuario(sc, clienteHttp);
                return true;
            case "2":
                cadastrarEmpresa(sc, clienteHttp);
                return true;
            case "3":
                fazerLogin(sc, clienteHttp);
                return true;
            case "0":
                System.out.println("Encerrando cliente...");
                return false;
            default:
                System.out.println("❌ Opção inválida!");
                return true;
        }
    }

    // ========================================
    // MENU LOGADO - USUÁRIO
    // ========================================
    private static boolean menuLogadoUsuario(Scanner sc, ClienteHttp clienteHttp) {
        System.out.println("\n===== MENU DO USUÁRIO =====");
        System.out.println("Logado como: " + usernameLogado);
        System.out.println("1 - Ver meus dados");
        System.out.println("2 - Editar perfil");
        System.out.println("3 - Excluir conta");
        System.out.println("4 - Logout");
        System.out.println("===========================");
        System.out.print("Escolha uma opção: ");
        String opcao = sc.nextLine().trim();

        switch (opcao) {
            case "1":
                lerDadosUsuario(clienteHttp);
                return true;
            case "2":
                editarPerfilUsuario(sc, clienteHttp);
                return true;
            case "3":
                excluirContaUsuario(sc, clienteHttp);
                return true;
            case "4":
                fazerLogout();
                return true;
            default:
                System.out.println("❌ Opção inválida");
                return true;
        }
    }

    // ========================================
    // MENU LOGADO - EMPRESA
    // ========================================
    private static boolean menuLogadoEmpresa(Scanner sc, ClienteHttp clienteHttp) {
        System.out.println("\n===== MENU DA EMPRESA =====");
        System.out.println("Logado como: " + usernameLogado);
        System.out.println("1 - Ver dados da empresa");
        System.out.println("2 - Editar dados da empresa");
        System.out.println("3 - Excluir empresa");
        System.out.println("4 - Logout");
        System.out.println("===========================");
        System.out.print("Escolha uma opção: ");
        String opcao = sc.nextLine().trim();

        switch (opcao) {
            case "1":
                lerDadosEmpresa(clienteHttp);
                return true;
            case "2":
                editarDadosEmpresa(sc, clienteHttp);
                return true;
            case "3":
                excluirEmpresa(sc, clienteHttp);
                return true;
            case "4":
                fazerLogout();
                return true;
            default:
                System.out.println("❌ Opção inválida");
                return true;
        }
    }

    // ========================================
    // FUNÇÕES DE USUÁRIO
    // ========================================
    private static void cadastrarUsuario(Scanner sc, ClienteHttp clienteHttp) {
        System.out.println("\n--- CADASTRAR USUÁRIO ---");
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
        System.out.print("Experience (opcional): ");
        String experience = sc.nextLine();
        System.out.print("Education (opcional): ");
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

        try {
            HttpResponse<String> response = clienteHttp.cadastrarUsuario(jsonBuilder.toString());
            if (response.statusCode() == 201) {
                System.out.println("✅ Usuário cadastrado com sucesso!");
            } else {
                System.out.println("❌ Erro ao cadastrar: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("❌ Erro ao conectar: " + e.getMessage());
        }
    }

    /*private static void fazerLoginUsuario(Scanner sc, ClienteHttp clienteHttp) {
        System.out.println("\n--- LOGIN USUÁRIO ---");
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        String loginJson = String.format("""
        {
            "username": "%s",
            "password": "%s"
        }
        """, username, password);

        try {
            HttpResponse<String> response = clienteHttp.fazerLogin(loginJson);

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                if (responseBody.contains("\"token\"")) {
                    int start = responseBody.indexOf("\"token\"") + 9;
                    int end = responseBody.indexOf("\"", start);
                    token = responseBody.substring(start, end);
                    usernameLogado = username;
                    tipoUsuario = "USER";
                    System.out.println("✅ Login realizado com sucesso!");
                }
            } else {
                System.out.println("❌ Erro no login: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("❌ Erro ao conectar: " + e.getMessage());
        }
    }*/

    private static void lerDadosUsuario(ClienteHttp clienteHttp) {
        System.out.println("\n--- MEUS DADOS ---");
        try {
            HttpResponse<String> response = clienteHttp.lerUsuario(token);

            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                System.out.println("Nome: " + json.optString("name", "N/A"));
                System.out.println("Username: " + json.optString("username", "N/A"));
                System.out.println("Email: " + json.optString("email", "N/A"));
                System.out.println("Phone: " + json.optString("phone", "N/A"));
                System.out.println("Experience: " + json.optString("experience", "N/A"));
                System.out.println("Education: " + json.optString("education", "N/A"));
            } else {
                System.out.println("❌ Erro ao buscar dados: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("❌ Erro: " + e.getMessage());
        }
    }

    private static void editarPerfilUsuario(Scanner sc, ClienteHttp clienteHttp) {
        System.out.println("\n--- EDITAR PERFIL ---");
        System.out.println("Deixe em branco os campos que não deseja alterar");
        System.out.print("Novo nome: ");
        String name = sc.nextLine();
        System.out.print("Novo email: ");
        String email = sc.nextLine();
        System.out.print("Nova senha: ");
        String senha = sc.nextLine();
        System.out.print("Novo número de celular: ");
        String phone = sc.nextLine();
        System.out.print("Experiencia: ");
        String experience = sc.nextLine();
        System.out.print("Formação: ");
        String education = sc.nextLine();

        String json = String.format("""
        {
          "name": "%s",
          "email": "%s",
          "password": "%s",
          "phone": "%s",
          "experience": "%s",
          "education": "%s"
        }
        """, name, email, senha, phone, experience, education);

        try {
            HttpResponse<String> response = clienteHttp.editarUsuario(token, json);
            if (response.statusCode() == 200) {
                System.out.println("✅ Perfil atualizado com sucesso!");
            } else {
                System.out.println("❌ Erro ao atualizar: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("❌ Erro: " + e.getMessage());
        }
    }

    private static void excluirContaUsuario(Scanner sc, ClienteHttp clienteHttp) {
        System.out.println("\n--- EXCLUIR CONTA ---");
        System.out.print("Tem certeza que deseja excluir sua conta? (s/n): ");
        String confirmacao = sc.nextLine().trim();

        if (confirmacao.equalsIgnoreCase("s")) {
            try {
                HttpResponse<String> response = clienteHttp.excluirUsuario(token);
                if (response.statusCode() == 200) {
                    fazerLogout();
                    System.out.println("✅ Conta excluída com sucesso!");
                } else {
                    System.out.println("❌ Erro ao excluir: " + response.body());
                }
            } catch (Exception e) {
                System.out.println("❌ Erro: " + e.getMessage());
            }
        } else {
            System.out.println("❌ Exclusão cancelada");
        }
    }

    // ========================================
    // FUNÇÕES DE EMPRESA
    // ========================================
    private static void cadastrarEmpresa(Scanner sc, ClienteHttp clienteHttp) {
        System.out.println("\n--- CADASTRAR EMPRESA ---");
        System.out.print("Nome da Empresa: ");
        String name = sc.nextLine();
        System.out.print("Ramo de Negócio: ");
        String business = sc.nextLine();
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();
        System.out.print("Rua: ");
        String street = sc.nextLine();
        System.out.print("Número: ");
        String number = sc.nextLine();
        System.out.print("Cidade: ");
        String city = sc.nextLine();
        System.out.print("Estado: ");
        String state = sc.nextLine();
        System.out.print("Telefone: ");
        String phone = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();

        String json = String.format("""
        {
          "name": "%s",
          "business": "%s",
          "username": "%s",
          "password": "%s",
          "street": "%s",
          "number": "%s",
          "city": "%s",
          "state": "%s",
          "phone": "%s",
          "email": "%s"
        }
        """, name, business, username, password, street, number, city, state, phone, email);

        try {
            HttpResponse<String> response = clienteHttp.cadastrarEmpresa(json);
            if (response.statusCode() == 201) {
                System.out.println("✅ Empresa cadastrada com sucesso!");
            } else {
                System.out.println("❌ Erro ao cadastrar: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("❌ Erro ao conectar: " + e.getMessage());
        }
    }

    /*private static void fazerLoginEmpresa(Scanner sc, ClienteHttp clienteHttp) {
        System.out.println("\n--- LOGIN EMPRESA ---");
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        String loginJson = String.format("""
        {
            "username": "%s",
            "password": "%s"
        }
        """, username, password);

        try {
            HttpResponse<String> response = clienteHttp.fazerLogin(loginJson);

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                if (responseBody.contains("\"token\"")) {
                    int start = responseBody.indexOf("\"token\"") + 9;
                    int end = responseBody.indexOf("\"", start);
                    token = responseBody.substring(start, end);
                    usernameLogado = username;
                    tipoUsuario = "COMPANY";
                    System.out.println("✅ Login realizado com sucesso!");
                }
            } else {
                System.out.println("❌ Erro no login: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("❌ Erro ao conectar: " + e.getMessage());
        }
    }*/

    private static void lerDadosEmpresa(ClienteHttp clienteHttp) {
        System.out.println("\n--- DADOS DA EMPRESA ---");
        try {
            HttpResponse<String> response = clienteHttp.lerEmpresa(token);

            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                System.out.println("Nome: " + json.optString("name", "N/A"));
                System.out.println("Ramo: " + json.optString("bussines", "N/A"));
                System.out.println("Username: " + json.optString("username", "N/A"));
                System.out.println("Endereço: " + json.optString("street", "N/A")
                        + ", " + json.optString("number", "N/A"));
                System.out.println("Cidade: " + json.optString("city", "N/A"));
                System.out.println("Estado: " + json.optString("state", "N/A"));
                System.out.println("Telefone: " + json.optString("phone", "N/A"));
                System.out.println("Email: " + json.optString("email", "N/A"));
            } else {
                System.out.println("❌ Erro ao buscar dados: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("❌ Erro: " + e.getMessage());
        }
    }

    private static void editarDadosEmpresa(Scanner sc, ClienteHttp clienteHttp) {
        System.out.println("\n--- EDITAR DADOS DA EMPRESA ---");
        System.out.println("Deixe em branco os campos que não deseja alterar");
        System.out.print("Novo nome: ");
        String name = sc.nextLine();
        System.out.print("Novo ramo: ");
        String business = sc.nextLine();
        System.out.print("Nova senha: ");
        String password = sc.nextLine();
        System.out.print("Nova rua: ");
        String street = sc.nextLine();
        System.out.print("Novo número: ");
        String number = sc.nextLine();
        System.out.print("Nova cidade: ");
        String city = sc.nextLine();
        System.out.print("Novo estado: ");
        String state = sc.nextLine();
        System.out.print("Novo telefone: ");
        String phone = sc.nextLine();
        System.out.print("Novo email: ");
        String email = sc.nextLine();

        String json = String.format("""
        {
          "name": "%s",
          "business": "%s",
          "password": "%s",
          "street": "%s",
          "number": "%s",
          "city": "%s",
          "state": "%s",
          "phone": "%s",
          "email": "%s"
        }
        """, name, business, password, street, number, city, state, phone, email);

        try {
            HttpResponse<String> response = clienteHttp.editarEmpresa(token, json);
            if (response.statusCode() == 200) {
                System.out.println("✅ Dados atualizados com sucesso!");
            } else {
                System.out.println("❌ Erro ao atualizar: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("❌ Erro: " + e.getMessage());
        }
    }

    private static void excluirEmpresa(Scanner sc, ClienteHttp clienteHttp) {
        System.out.println("\n--- EXCLUIR EMPRESA ---");
        System.out.print("Tem certeza que deseja excluir a empresa? (s/n): ");
        String confirmacao = sc.nextLine().trim();

        if (confirmacao.equalsIgnoreCase("s")) {
            try {
                HttpResponse<String> response = clienteHttp.excluirEmpresa(token);
                if (response.statusCode() == 200) {
                    fazerLogout();
                    System.out.println("✅ Empresa excluída com sucesso!");
                } else {
                    System.out.println("❌ Erro ao excluir: " + response.body());
                }
            } catch (Exception e) {
                System.out.println("❌ Erro: " + e.getMessage());
            }
        } else {
            System.out.println("❌ Exclusão cancelada");
        }
    }

    // ========================================
    // FUNÇÃO DE LOGIN E LOGOUT
    // ========================================
    private static void fazerLogin(Scanner sc, ClienteHttp clienteHttp) {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Username: ");
        String username = sc.nextLine();
        System.out.print("Password: ");
        String password = sc.nextLine();

        String loginJson = String.format("""
        {
            "username": "%s",
            "password": "%s"
        }
        """, username, password);

        try {
            HttpResponse<String> response = clienteHttp.fazerLogin(loginJson);

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                if (responseBody.contains("\"token\"")) {
                    int start = responseBody.indexOf("\"token\"") + 9;
                    int end = responseBody.indexOf("\"", start);
                    token = responseBody.substring(start, end);
                    usernameLogado = username;
                    tipoUsuario = jwtUtil.getRoleFromToken(token);
                    
                    if (tipoUsuario == null) {
                        System.out.println("Não foi possível identificar o tipo de usuário. Será usado USER");
                        tipoUsuario = "USER";
                    }
                    tipoUsuario = tipoUsuario.toUpperCase();
                    
                    System.out.println("✅ Login realizado com sucesso!");

                }
            } else {
                System.out.println("❌ Erro no login: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("❌ Erro ao conectar: " + e.getMessage());
        }
    
    }

    private static void fazerLogout() {
        token = null;
        usernameLogado = null;
        tipoUsuario = null;
        System.out.println("✅ Logout realizado com sucesso!");
    }

}
