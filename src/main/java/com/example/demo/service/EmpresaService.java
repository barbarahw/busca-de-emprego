/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service;

import com.example.demo.repositories.EmpresaRepository;
import com.example.demo.dto.EmpresaRequest;
import com.example.demo.model.Empresa;
import com.example.demo.security.JwtUtil;
import io.jsonwebtoken.Claims;
import java.util.Optional;
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
    
    public String login(String username, String password) {
        Optional<Empresa> empresaOpt = repositorio.findByUsername(username);

        if (empresaOpt.isPresent()) {
            Empresa empresa = empresaOpt.get();

            if (empresa.getPassword().equals(password)) {

                return jwtUtil.gerarToken(
                        String.valueOf(empresa.getId()),
                        empresa.getUsername(),
                        "company"
                );
            } else {
                throw new RuntimeException("Invalid credentials");
            }
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }
    
    public Empresa editarEmpresa (Long id, EmpresaRequest dto) {
        Optional<Empresa> empresaOpt = repositorio.findById(id);
        if (empresaOpt.isEmpty()) {
            throw new RuntimeException();
        }
        
        Empresa e = empresaOpt.get();
        
        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            e.setName(dto.getName());
        }
        
        if (dto.getPassword()!= null && !dto.getPassword().trim().isEmpty()) {
            e.setPassword(dto.getPassword());
        }
        
        if (dto.getEmail()!= null && !dto.getEmail().trim().isEmpty()) {
            e.setEmail(dto.getEmail());
        }
        
        if (dto.getPhone()!= null && !dto.getPhone().trim().isEmpty()) {
            e.setPhone(dto.getPhone());
        }
        
        if (dto.getStreet()!= null && !dto.getStreet().trim().isEmpty()) {
            e.setStreet(dto.getStreet());
        }
        
        if (dto.getCity()!= null && !dto.getCity().trim().isEmpty()) {
            e.setCity(dto.getCity());
        }
        
        if (dto.getState()!= null && !dto.getState().trim().isEmpty()) {
            e.setState(dto.getState());
        }
        
        if (dto.getBusiness()!= null && !dto.getBusiness().trim().isEmpty()) {
            e.setBusiness(dto.getBusiness());
        }
        
        if (dto.getNumber()!= null && !dto.getNumber().trim().isEmpty()) {
            e.setNumber(dto.getNumber());
        }
        
        return repositorio.save(e);
    }
    
    public Long getCompanyIdFromToken(String token) {
        try {
            String subject = jwtUtil.extrairSubject(token);
            return Long.parseLong(subject);
        } catch (Exception e) {
            return null;
        }
    }
    
    public String getRoleFromToken(String token) {
        Claims claims = jwtUtil.parseToken(token);
        return (String) claims.get("role");
    }
    
    public boolean validarToken(String token) {
        return jwtUtil.validarToken(token);
    }
    
    public Empresa buscarPorId(Long id) {
        return repositorio.findById(id).orElse(null);
    }
    
    public int getExpiration() {
        return jwtUtil.getExpirationInSeconds();
    }

    public boolean deletarEmpresa(Long id) {
        Optional<Empresa> empresaOpt = repositorio.findById(id);
        if (empresaOpt.isEmpty()) {
            return false;
        }

        repositorio.deleteById(id);
        return true;
    }
}
