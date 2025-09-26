/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service;

import com.example.demo.model.Usuario;
import com.example.demo.repositories.UsuarioRepository;
import com.example.demo.dto.UsuarioRequest;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    private final UsuarioRepository repositorio;
    
    public UsuarioService (UsuarioRepository repo) {
        this.repositorio = repo;
    }
    
    public Usuario cadastrar(UsuarioRequest dto) {
        if (repositorio.findByUsername(dto.getUsername()).isPresent()){
            throw new RuntimeException("Username já existe");   
        }
        if (repositorio.findByName(dto.getName()).isPresent()) {
            throw new RuntimeException("Name já existe");
        }
        
        Usuario u = new Usuario();
        u.setName(dto.getName());
        u.setUsername(dto.getUsername());
        u.setPassword(dto.getPassword());
        u.setEmail(dto.getEmail());
        u.setPhone(dto.getPhone());
        
        return repositorio.save(u);
    }
}
