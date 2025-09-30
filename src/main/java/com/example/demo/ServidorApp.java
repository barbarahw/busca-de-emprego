package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Scanner;

@SpringBootApplication
public class ServidorApp {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
                System.out.println("Digite a porta do servidor:");
                String porta = sc.nextLine();
                sc.close();
                
                new SpringApplicationBuilder(ServidorApp.class)
                        .properties("server.port=" + porta)
                        .web(WebApplicationType.SERVLET)
                        .run(args);
                
                System.out.println("Servidor rodando na porta : " + porta);
	}

}
