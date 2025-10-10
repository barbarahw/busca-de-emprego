/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controller;

import com.example.demo.dto.ErrorResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.model.Usuario;
import com.example.demo.dto.UsuarioRequest;
import com.example.demo.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.BufferedReader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.MethodArgumentNotValidException;

@RestController
@RequestMapping
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    private ObjectMapper mapper = new ObjectMapper();

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        StringBuilder corpoJson = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                corpoJson.append(linha);
            }
        } catch (Exception e) {
            corpoJson.append("Não foi possível capturar o corpo da requisição.");
        }
        
        List<Map<String, String>> detalhes = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> Map.of(
                "field", error.getField(),
                "error", error.getDefaultMessage()
        ))
                .toList();
        
        // Criando resposta conforme protocolo (422 UNPROCESSABLE)
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("message", "Validation error");
        resposta.put("code", "UNPROCESSABLE");
        resposta.put("details", detalhes);

        try {
            System.out.println("JSON recebido: " + corpoJson);
            String json = mapper.writeValueAsString(resposta);
            System.out.println("JSON enviado: " + json);
        } catch (Exception e) {
            System.out.println("Erro ao converter JSON de validação: " + e.getMessage());
        }

        // Alterado para 422 conforme protocolo
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(resposta);
    }

    private void logJsonRecebido(Object dadosRecebidos) {
        try {
            if (dadosRecebidos != null) {
                String jsonRecebido = mapper.writeValueAsString(dadosRecebidos);
                System.out.println("JSON recebido: " + jsonRecebido);
            }
        } catch (Exception e) {
            System.out.println("Erro ao logar JSON recebido: " + e.getMessage());
        }
    }

    private void logJsonEnviado(Object resposta) {
        try {
            String jsonEnviado = mapper.writeValueAsString(resposta);
            System.out.println("JSON enviado: " + jsonEnviado);
        } catch (Exception e) {
            System.out.println("Erro ao logar JSON enviado: " + e.getMessage());
        }
    }

    @PostMapping("/users")
    public ResponseEntity<?> cadastrar(@Valid @RequestBody UsuarioRequest request) {
        try {
            logJsonRecebido(request);

            Usuario u = service.cadastrar(request);
            
            // Resposta conforme protocolo (201 Created)
            Map<String, String> resposta = new HashMap<>();
            resposta.put("message", "Created");

            logJsonEnviado(resposta);
            return ResponseEntity.status(HttpStatus.CREATED).body(resposta);

        } catch (Exception e) {
            // Verifica se é erro de username duplicado (409) ou outro erro
            Map<String, String> resposta = new HashMap<>();
            resposta.put("message", e.getMessage());
            
            logJsonEnviado(resposta);
            
            // Se for erro de username duplicado, retorna 409, caso contrário 422
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("username") && 
                e.getMessage().toLowerCase().contains("already")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(resposta);
            } else {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(resposta);
            }
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            logJsonRecebido(loginRequest);
            String token = service.login(loginRequest.getUsername(), loginRequest.getPassword());
            int expiresIn = service.getExpiration();

            LoginResponse resposta = new LoginResponse(token, expiresIn);
            logJsonEnviado(resposta);
            return ResponseEntity.ok(resposta);

        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse("Invalid credentials");
            logJsonEnviado(error);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);

        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Erro interno do servidor");
            logJsonEnviado(error);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /*@GetMapping
    public ResponseEntity<?> lerDados(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")){
                return ResponseEntity.status(401).body(Map.of("Message", "Invalid Token"));
            }
            
            String token = authHeader.substring(7);
            Usuario usuario = service.buscarPorToken(token);
            
            if (usuario == null) {
                return ResponseEntity.status(404).body(Map.of("message", "User not found"));
            }
            
            Map<String, String> dados = Map.of(
                    "name", usuario.getName(),
                    "username", usuario.getUsername(),
                    "email", usuario.getEmail() != null ? usuario.getEmail() : "",
                    "phone", usuario.getPhone() != null ? usuario.getPhone() : "",
                    "experience", usuario.getExperience() != null ? usuario.getExperience() : "",
                    "education", usuario.getEducation() != null ? usuario.getEducation() : ""
            );
            return ResponseEntity.ok(dados);
            
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("message", "Forbidden"));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));
        }
        
    }*/
}