/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controller;

import com.example.demo.dto.ErrorResponse;
import com.example.demo.model.Usuario;
import com.example.demo.dto.UsuarioRequest;
import com.example.demo.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

@RestController
public class UsuarioController {

    private final UsuarioService service;
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
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

    @GetMapping("/users/{id}")
    public ResponseEntity<?> lerDados(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            Map<String, String> resposta = Map.of("message", "Invalid token");
            logJsonEnviado(resposta);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resposta);
        }

        String token = authHeader.substring(7);

        try {
            Long IdDoToken = service.getUserIdFromToken(token);

            if (!IdDoToken.equals(id)) {
                Map<String, String> resposta = Map.of("message", "Forbidden");
                logJsonEnviado(resposta);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(resposta);
            }

            Usuario usuario = service.buscarPorId(id);

            if (usuario == null) {
                Map<String, String> resposta = Map.of("message", "User not found");
                logJsonEnviado(resposta);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
            }

            Map<String, Object> resposta = new HashMap<>();
            resposta.put("name", usuario.getName());
            resposta.put("username", usuario.getUsername());
            resposta.put("email", usuario.getEmail());
            resposta.put("phone", usuario.getPhone());
            resposta.put("experience", usuario.getExperience());
            resposta.put("education", usuario.getEducation());

            logJsonEnviado(resposta);
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            Map<String, String> resposta = Map.of("message", "Invalid token");
            logJsonEnviado(resposta);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resposta);
        }
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<?> editarUsuario(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody UsuarioRequest request) {
        logJsonRecebido(request);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ErrorResponse error = new ErrorResponse("Invalid Token");
            logJsonEnviado(error);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        String token = authHeader.substring(7);

        try {
            if (!service.validarToken(token)) {
                ErrorResponse error = new ErrorResponse("Invalid token");
                logJsonEnviado(error);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            Long userIdToken = service.getUserIdFromToken(token);
            if (!userIdToken.equals(id)) {
                ErrorResponse error = new ErrorResponse("Forbidden");
                logJsonEnviado(error);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            Usuario usuarioAtualizado = service.editarUsuario(id, request);

            logJsonEnviado(usuarioAtualizado);
            return ResponseEntity.ok(usuarioAtualizado);
            
        } catch (RuntimeException e) {
            if (e.getMessage().equalsIgnoreCase("User not found")) {
                ErrorResponse error = new ErrorResponse("User not found");
                logJsonEnviado(error);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            ErrorResponse error = new ErrorResponse(e.getMessage());
            logJsonEnviado(error);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);

        } catch (Exception e) {

            ErrorResponse error = new ErrorResponse("Internal server error");
            logJsonEnviado(error);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deletarUsuario(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            Map<String, String> resposta = Map.of("message", "invalid token");
            logJsonEnviado(resposta);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resposta);
        }

        String token = authHeader.substring(7);

        try {
            if (!service.validarToken(token)) {
                Map<String, String> resposta = Map.of("message", "Invalid token");
                logJsonEnviado(resposta);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resposta);
            }

            Long userIdToken = service.getUserIdFromToken(token);
            if (!userIdToken.equals(id)) {
                Map<String, String> resposta = Map.of("message", "Forbidden");
                logJsonEnviado(resposta);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(resposta);
            }

            boolean deletado = service.deletarUsuario(id);
            if (!deletado) {
                Map<String, String> resposta = Map.of("message", "User not found");
                logJsonEnviado(resposta);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
            }

            Map<String, String> resposta = Map.of("message", "User deletedd successfully");
            logJsonEnviado(resposta);
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            Map<String, String> resposta = Map.of("message", "Internal server error");
            logJsonEnviado(resposta);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta);
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

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Server is running");
    }

}
