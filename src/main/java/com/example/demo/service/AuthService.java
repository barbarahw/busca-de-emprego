/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service;

import com.example.demo.security.JwtUtil;
import com.example.demo.repositories.EmpresaRepository;
import com.example.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private EmpresaService empresaService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private EmpresaRepository empresaRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public String login(String username, String password) {
        
        if (usuarioRepository.findByUsername(username).isPresent()) {
            String token = usuarioService.login(username, password);
            jwtUtil.addLoggedUser(username);
            return token;
        }
        
        if (empresaRepository.findByUsername(username).isPresent()) {
            String token = empresaService.login(username, password);
            jwtUtil.addLoggedUser(username);
            return token;
        }
        
        throw new RuntimeException("Invalid Credentials");
    }
    
    public int getExpiration(){
        return 3600;
    }
}
