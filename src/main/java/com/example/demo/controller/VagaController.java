package com.example.demo.controller;

import com.example.demo.dto.VagaFiltroRequest;
import com.example.demo.dto.VagaRequest;
import com.example.demo.dto.VagaResponse;
import com.example.demo.model.Vaga;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.VagaService;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class VagaController {

    @Autowired
    private JwtUtil jwtUtil;
    
    
    @Autowired
    private VagaService vagaService;

    @PostMapping ("/jobs")
    public ResponseEntity<?> cadastrarVaga(@RequestHeader("Authorization") String authHeader,
                                       @RequestBody VagaRequest request) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Vaga vaga = vagaService.cadastrarVaga(token, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Created"));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of(
                "message", "Validation error",
                "code", "UNPROCESSABLE",
                "details", List.of(Map.of("error", e.getMessage()))
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Internal error"));
        }
    }
    
    
    @PostMapping("/companies/{companyId}/jobs")
    public ResponseEntity<?> listarVagasEmpresa(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("companyId") Long companyId,
            @RequestBody(required = false) VagaFiltroRequest filtros) {

        try {
            String token = authHeader.replace("Bearer ", "");

            List<VagaResponse> lista = vagaService.listarVagasPorEmpresa(token, companyId, filtros);

            return ResponseEntity.ok(Map.of("items", lista));

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(422).body(Map.of(
                    "message", "Validation error",
                    "code", "UNPROCESSABLE",
                    "details", List.of(Map.of("error", e.getMessage()))
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid token"));
        }
    }
    
    @GetMapping("/jobs/{id}")
    public ResponseEntity<?> lerVaga(
            @RequestHeader("token") String token,
            @PathVariable Long id
    ) {
        try {
            VagaResponse response = vagaService.lerVaga(id, token);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("message", "Forbidden"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }
    
    @PostMapping("/jobs/search")
    public ResponseEntity<?> listarVagasUser(
            @RequestHeader("Authorization") String token,
            @RequestBody VagaFiltroRequest request) {
        
        String jwt = token.replace("Bearer ", "");
        
        if (!jwtUtil.validarToken(jwt)) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Invalid Token"));
        }
        
        var result = vagaService.buscarVagas(request);
        
        if (result.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Job not found"));
        }
        
        return ResponseEntity.ok(Map.of("items", result));
    }
    
    @PatchMapping("/jobs/{id}")
    public ResponseEntity<?> atualizarVaga(
            @PathVariable Long id,
            @RequestBody VagaRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try{
            String token = authHeader.replace("Bearer ", "");
            
            if (!jwtUtil.validarToken(token)) {
                return ResponseEntity.status(401)
                        .body(Map.of("message", "Invalid Token"));
            }
            
            vagaService.atualizarVaga(id, request, token);
            
            return ResponseEntity.ok(Map.of("message", "job updated successfully"));
            
        } catch (JwtException e) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));
            
        }catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("message", "Job not found"));
            
        } catch (ValidationException e) {
            return ResponseEntity.status(422).body(
                    Map.of("message", "Validation error",
                            "code", "UNPROCESSABLE",
                            "details", e.getMessage())); //CORRIGIR DETALHES
        }
    }
    
    
    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<?> deletarVaga(
            @PathVariable long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));
                
            }
            
            String token = authHeader.replace("Bearer ", "");
            
            vagaService.deletarVaga(id, token);
            
            return ResponseEntity.ok(Map.of("message", "Job deleted successfully"));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("message", "Forbidden"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("message", "Job not found"));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));
        }
    }

   
    
}
