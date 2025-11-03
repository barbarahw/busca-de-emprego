/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controller;

import com.example.demo.model.Empresa;
import com.example.demo.dto.EmpresaRequest;
import com.example.demo.dto.ErrorResponse;
import com.example.demo.service.EmpresaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmpresaController {

    private final EmpresaService empresaService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
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

    private void logJsonRecebido(Object request) {
        try {
            String jsonRecebido = mapper.writeValueAsString(request);
            System.out.println("JSON recebido: " + jsonRecebido);
        } catch (Exception e) {
            System.out.println("Erro ao logar JSON recebido: " + e.getMessage());
        }
    }

    @PostMapping("/companies")
    public ResponseEntity<?> cadastrar(@Valid @RequestBody EmpresaRequest request) {
        logJsonRecebido(request);

        try {

            Empresa e = empresaService.cadastrar(request);

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

    @GetMapping("/companies/{id}")
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
            Long IdDoToken = empresaService.getCompanieIdFromToken(token);

            if (!IdDoToken.equals(id)) {
                Map<String, String> resposta = Map.of("message", "Forbidden");
                logJsonEnviado(resposta);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(resposta);
            }

            Empresa empresa = empresaService.buscarPorId(id);

            if (empresa == null) {
                Map<String, String> resposta = Map.of("message", "User not found");
                logJsonEnviado(resposta);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
            }

            Map<String, Object> resposta = new HashMap<>();
            resposta.put("name", empresa.getName());
            resposta.put("bussines", empresa.getBusiness());
            resposta.put("username", empresa.getUsername());
            resposta.put("street", empresa.getStreet());
            resposta.put("number", empresa.getNumber());
            resposta.put("city", empresa.getCity());
            resposta.put("state", empresa.getState());
            resposta.put("phone", empresa.getPhone());
            resposta.put("email", empresa.getEmail());

            logJsonEnviado(resposta);
            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            Map<String, String> resposta = Map.of("message", "Invalid token");
            logJsonEnviado(resposta);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resposta);
        }
    }
    
    @PatchMapping("/companies/{id}")
    public ResponseEntity<?> editarEmpresa(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody EmpresaRequest request ) {
        logJsonRecebido(request);
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ErrorResponse error = new ErrorResponse("Invalid Token");
            logJsonEnviado(request);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        String token = authHeader.substring(7);
        
        try {
            if (!empresaService.validarToken(token)) {
                ErrorResponse error = new ErrorResponse("Invalid token");
                logJsonEnviado(error);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            Long userIdToken = empresaService.getCompanieIdFromToken(token);
            if (!userIdToken.equals(id)) {
                ErrorResponse error = new ErrorResponse("Forbidden");
                logJsonEnviado(error);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            Empresa empresaAtualizada = empresaService.editarEmpresa(id, request);
            
            Map<String, Object> resposta = new HashMap<>();
            resposta.put("message", "Created");

            logJsonEnviado(resposta);
            return ResponseEntity.status(HttpStatus.OK).body(resposta);
            
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
}
