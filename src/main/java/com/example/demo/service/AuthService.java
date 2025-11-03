/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service;

import com.example.demo.service.EmpresaService;
import com.example.demo.service.UsuarioService;
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
    
    public String login(String username, String password) {
        if (usuarioRepository.findByUsername(username).isPresent()) {
            return usuarioService.login(username, password);
        }
        
        if (empresaRepository.findByUsername(username).isPresent()) {
            return empresaService.login(username, password);
        }
        
        throw new RuntimeException("Invalid Credentials");
    }
    
    public int getExpiration(){
        return 3600;
    }
}
