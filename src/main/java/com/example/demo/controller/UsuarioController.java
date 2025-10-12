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
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {

        List<Map<String, String>> detalhes = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> Map.of(
                "field", error.getField(),
                "error", error.getDefaultMessage()
        ))
                .toList();

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("message", "Validation error");
        resposta.put("code", "UNPROCESSABLE");
        resposta.put("details", detalhes);

        logJsonEnviado(resposta);

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(resposta);
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
        logJsonRecebido(request);

        try {

            Usuario u = service.cadastrar(request);

            Map<String, String> resposta = new HashMap<>();
            resposta.put("message", "Created");

            logJsonEnviado(resposta);
            return ResponseEntity.status(HttpStatus.CREATED).body(resposta);

        } catch (Exception e) {
            Map<String, String> resposta = new HashMap<>();
            resposta.put("message", e.getMessage());

            logJsonEnviado(resposta);

            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("username")
                    && e.getMessage().toLowerCase().contains("already")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(resposta);
            } else {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(resposta);
            }
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {

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

    private void logJsonRecebido(Object request) {
        try {
            String jsonRecebido = mapper.writeValueAsString(request);
            System.out.println("JSON recebido: " + jsonRecebido);
        } catch (Exception e) {
            System.out.println("Erro ao logar JSON recebido: " + e.getMessage());
        }
    }
}
