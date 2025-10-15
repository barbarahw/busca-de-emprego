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

    // ✅ Cadastrar novo usuário
    public Usuario cadastrar(UsuarioRequest dto) {
        if (repositorio.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Username já existe");
        }

        Usuario u = new Usuario();
        u.setName(dto.getName().toUpperCase()); // conforme o protocolo, nome salvo em maiúsculo
        u.setUsername(dto.getUsername());
        u.setPassword(dto.getPassword());
        u.setEmail(dto.getEmail());
        u.setPhone(dto.getPhone());
        u.setExperience(dto.getExperience());
        u.setEducation(dto.getEducation());

        return repositorio.save(u);
    }

    // ✅ Login com geração do token
    public String login(String username, String password) {
        Optional<Usuario> usuarioOpt = repositorio.findByUsername(username);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            if (usuario.getPassword().equals(password)) {
                // Gera token com ID como "subject"
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

    // ✅ Ler usuário por ID (para o GET /users/{id})
    public Usuario buscarPorId(Long id) {
        return repositorio.findById(id).orElse(null);
    }

    // ✅ Extrai o ID do usuário a partir do token JWT
    public Long getUserIdFromToken(String token) {
        try {
            String subject = jwtUtil.extrairSubject(token);
            return Long.parseLong(subject);
        } catch (Exception e) {
            return null;
        }
    }

    public int getExpiration() {
        return jwtUtil.getExpirationInSeconds();
    }
}
