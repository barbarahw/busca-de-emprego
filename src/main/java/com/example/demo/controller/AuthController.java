/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controller;

import com.example.demo.dto.ErrorResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired private JwtUtil jwtUtil;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        
        try {
            String token = authService.login(loginRequest.getUsername(),loginRequest.getPassword());
            int expiresIn = authService.getExpiration();
            
            LoginResponse response = new LoginResponse(token, expiresIn);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e){
            ErrorResponse error = new ErrorResponse("Invalid Credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Token inválido ou ausente"));
    }

    String token = authHeader.substring(7);
    String username = jwtUtil.pegarUsername(token);

    jwtUtil.removeLoggedUser(username);

    return ResponseEntity.ok(new ErrorResponse("Logout efetuado com sucesso"));
}
}
