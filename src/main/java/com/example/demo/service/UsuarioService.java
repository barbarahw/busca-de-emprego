/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service;

import com.example.demo.model.Usuario;
import com.example.demo.repositories.UsuarioRepository;
import com.example.demo.dto.UsuarioRequest;
import com.example.demo.security.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UsuarioService {
    
    @Autowired
    private final UsuarioRepository repositorio;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public UsuarioService (UsuarioRepository repo) {
        this.repositorio = repo;
    }
    
    public Usuario buscarPorToken(String token) {
        return repositorio.findByToken(token)
                .orElse(null);
    }
    
    public Usuario cadastrar(UsuarioRequest dto) {
        if (repositorio.findByUsername(dto.getUsername()).isPresent()){
            throw new RuntimeException("Username já existe");   
        }
        
        Usuario u = new Usuario();
        u.setName(dto.getName());
        u.setUsername(dto.getUsername());
        u.setPassword(dto.getPassword());
        u.setEmail(dto.getEmail());
        u.setPhone(dto.getPhone());
        u.setExperience(dto.getEducation());
        u.setEducation(dto.getEducation());
        
        return repositorio.save(u);
    }
    
    public String login (String username, String password){
        Optional<Usuario> usuarioOpt = repositorio.findByUsername(username);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            if (usuario.getPassword().equals(password)) {
                String token = jwtUtil.gerarToken(username);
                return token;
            } else {
                throw new RuntimeException("Invalid crdentials");
            }
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }
    
    public int getExpiration(){
        return jwtUtil.getExpirationInSeconds();
    }
}
