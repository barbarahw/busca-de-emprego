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

    private final UsuarioRepository repositorio;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    public UsuarioService(UsuarioRepository repo) {
        this.repositorio = repo;
    }

    public Usuario cadastrar(UsuarioRequest dto) {
        if (repositorio.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Username já existe");
        }

        Usuario u = new Usuario();
        u.setName(dto.getName().toUpperCase());
        u.setUsername(dto.getUsername());
        u.setPassword(dto.getPassword());
        u.setEmail(dto.getEmail());
        u.setPhone(dto.getPhone());
        u.setExperience(dto.getExperience());
        u.setEducation(dto.getEducation());

        return repositorio.save(u);
    }

    public String login(String username, String password) {
        Optional<Usuario> usuarioOpt = repositorio.findByUsername(username);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            if (usuario.getPassword().equals(password)) {

                return jwtUtil.gerarToken(
                        String.valueOf(usuario.getId()),
                        usuario.getUsername(),
                        "user"
                );
            } else {
                throw new RuntimeException("Invalid credentials");
            }
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }
    
    public Usuario editarUsuario (Long id, UsuarioRequest dto) {
        Optional<Usuario> usuarioOpt = repositorio.findById(id);
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException();
        }
        
        Usuario u = usuarioOpt.get();
        
        if (dto.getName() != null) u.setName(dto.getName());
        if (dto.getPassword()!= null) u.setPassword(dto.getPassword());
        if (dto.getEmail()!= null) u.setEmail(dto.getEmail());
        if (dto.getPhone()!= null) u.setPhone(dto.getPhone());
        if (dto.getExperience()!= null) u.setExperience(dto.getExperience());
        if (dto.getEducation()!= null) u.setEducation(dto.getEducation());
        
        return repositorio.save(u);
    }

    public Usuario buscarPorId(Long id) {
        return repositorio.findById(id).orElse(null);
    }

    public Long getUserIdFromToken(String token) {
        try {
            String subject = jwtUtil.extrairSubject(token);
            return Long.parseLong(subject);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validarToken(String token) {
        return jwtUtil.validarToken(token);
    }

    public int getExpiration() {
        return jwtUtil.getExpirationInSeconds();
    }
}
