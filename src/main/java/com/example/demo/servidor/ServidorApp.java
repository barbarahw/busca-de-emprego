package com.example.demo.servidor;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import java.util.Scanner;

@SpringBootApplication
public class ServidorApp {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Digite a porta do servidor: ");
        String porta = sc.nextLine().trim();
        sc.close();

        iniciar(porta);
    }

    public static void iniciar(String porta) {
        try {

            new SpringApplicationBuilder(ServidorApp.class)
                    .properties("server.port=" + porta)
                    .web(WebApplicationType.SERVLET)
                    .run();

        } catch (NumberFormatException e) {
            System.out.println("❌ Valor de porta inválido! Use apenas números.");
        } catch (Exception e) {
            System.out.println("❌ Erro ao iniciar servidor: " + e.getMessage());
        }
    }
}
