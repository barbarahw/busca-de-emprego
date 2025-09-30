/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.dto.UsuarioRequest;
import com.example.demo.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/users")
public class UsuarioController {
    private final UsuarioService service;
    
    public UsuarioController(UsuarioService service){
        this.service = service;
    }
    
    @PostMapping
    public ResponseEntity<?> cadastrar(@Valid @RequestBody UsuarioRequest request) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(request);
            System.out.println("Recebido: " + json);
            Usuario u = service.cadastrar(request);
            Map<String, String> resposta = new HashMap<>();
            resposta.put("message", "created");
            System.out.println("Enviado: " + resposta);
            
            return ResponseEntity.status(201).body(resposta);
            
        } catch (Exception e ) {
            Map<String, String> resposta = new HashMap<>();
            resposta.put("message", e.getMessage());
            return ResponseEntity.status(409).body(resposta);
        }
    }
}
