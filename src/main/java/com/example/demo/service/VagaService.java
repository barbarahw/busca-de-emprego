package com.example.demo.service;

import com.example.demo.dto.VagaRequest;
import com.example.demo.model.Empresa;
import com.example.demo.model.Vaga;
import com.example.demo.repositories.VagaRepository;
import com.example.demo.repositories.EmpresaRepository;
import com.example.demo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class VagaService {

    @Autowired
    private VagaRepository vagaRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public Vaga cadastrarVaga(String token, VagaRequest req) {
        String username = jwtUtil.pegarUsername(token);
        String role = jwtUtil.getRoleFromToken(token);

        if (!"company".equals(role)) {
            throw new SecurityException("Forbidden");
        }

        Empresa empresa = empresaRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        Vaga vaga = new Vaga();
        vaga.setTitle(req.getTitle());
        vaga.setArea(req.getArea());
        vaga.setDescription(req.getDescription());
        vaga.setLocation(req.getLocation());
        vaga.setContact(req.getContact());
        vaga.setSalary(req.getSalary());
        vaga.setCompany(empresa);

        return vagaRepository.save(vaga);
    }

    public List<Vaga> listAll() {
        return vagaRepository.findAll();
    }

    public Optional<Vaga> getById(Long id) {
        return vagaRepository.findById(id);
    }

}
