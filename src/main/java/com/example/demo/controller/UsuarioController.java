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
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@RestController
@RequestMapping
public class UsuarioController {

    @Autowired
    private UsuarioService service;
    
    ObjectMapper mapper = new ObjectMapper();

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping("/users")
    public ResponseEntity<?> cadastrar(@Valid @RequestBody UsuarioRequest request) {
        try {
            String json = mapper.writeValueAsString(request);
            System.out.println("JSON recebido: " + json);
            Usuario u = service.cadastrar(request);
            Map<String, String> resposta = new HashMap<>();
            resposta.put("message", "created");
            System.out.println("JSON enviado: " + resposta);

            return ResponseEntity.status(201).body(resposta);

        } catch (Exception e) {
            Map<String, String> resposta = new HashMap<>();
            resposta.put("message", e.getMessage());
            return ResponseEntity.status(409).body(resposta);
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        try {
            String jsonRecebido = mapper.writeValueAsString(loginRequest);
            System.out.println("JSON recebido: " + jsonRecebido);
            String token = service.login(loginRequest.getUsername(), loginRequest.getPassword());
            int expiresIn = service.getExpiration();
            
            LoginResponse response = new LoginResponse(token, expiresIn);
            String jsonEnviado = mapper.writeValueAsString(response);
            System.out.println("JSON enviado: " + jsonEnviado);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e ) {
            ErrorResponse error = new ErrorResponse("Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e){
            ErrorResponse error = new ErrorResponse("Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping
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
        
    }
}
