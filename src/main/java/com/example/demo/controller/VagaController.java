package com.example.demo.controller;

import com.example.demo.dto.VagaFiltroRequest;
import com.example.demo.dto.VagaRequest;
import com.example.demo.dto.VagaResponse;
import com.example.demo.model.Vaga;
import com.example.demo.service.VagaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class VagaController {

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

   
    
}
