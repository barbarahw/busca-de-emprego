/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service;

import com.example.demo.repositories.EmpresaRepository;
import com.example.demo.dto.EmpresaRequest;
import com.example.demo.model.Empresa;
import com.example.demo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmpresaService {
    
    private EmpresaRepository repositorio;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    public EmpresaService (EmpresaRepository repositorio) {
        this.repositorio = repositorio;
    }
    
    public Empresa cadastrar(EmpresaRequest dto) {
        if (repositorio.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Username já existe");
        }
        
        Empresa e = new Empresa();
        e.setName(dto.getName().toUpperCase());
        e.setBusiness(dto.getBusiness());
        e.setUsername(dto.getUsername());
        e.setPassword(dto.getPassword());
        e.setStreet(dto.getStreet());
        e.setNumber(dto.getNumber());
        e.setCity(dto.getCity());
        e.setState(dto.getState());
        e.setPhone(dto.getPhone());
        e.setEmail(dto.getEmail());
        
        return repositorio.save(e);
        
    }
}
