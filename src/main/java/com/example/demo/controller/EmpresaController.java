/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controller;

import com.example.demo.model.Empresa;
import com.example.demo.dto.EmpresaRequest;
import com.example.demo.service.EmpresaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmpresaController {
    private final EmpresaService empresaService; 
    private final ObjectMapper mapper = new ObjectMapper();
    
    @Autowired
    public EmpresaController (EmpresaService empresaService){
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
        
        try{
            
            Empresa e = empresaService.cadastrar(request);
            
            Map<String, String> resposta = new HashMap<>();
            resposta.put("message", "Created");

            logJsonEnviado(resposta);
            return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
            
        }catch (Exception e) {
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
}
