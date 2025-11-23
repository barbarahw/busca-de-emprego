package com.example.demo.service;

import com.example.demo.dto.VagaFiltroRequest;
import com.example.demo.dto.VagaRequest;
import com.example.demo.dto.VagaResponse;
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
        String role = jwtUtil.getRoleFromToken(token);

        if (!"company".equalsIgnoreCase(role)) {
            throw new SecurityException("Forbidden");
        }

        Long empresaId = jwtUtil.getCompanyIdFromToken(token);

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));


        if (req.getTitle() == null || req.getTitle().trim().length() < 3 || req.getTitle().length() > 150) {
            throw new IllegalArgumentException("Invalid title");
        }

        List<String> AREAS_VALIDAS = List.of(
                "Administração", "Agricultura", "Artes", "Atendimento ao Cliente",
                "Comercial", "Comunicação", "Construção Civil", "Consultoria",
                "Contabilidade", "Design", "Educação", "Engenharia", "Finanças",
                "Jurídica", "Logística", "Marketing", "Produção", "Recursos Humanos",
                "Saúde", "Segurança", "Tecnologia da Informação", "Telemarketing",
                "Vendas", "Outros"
        );

        if (req.getArea() == null || !AREAS_VALIDAS.contains(req.getArea())) {
            throw new IllegalArgumentException("Invalid area");
        }

        if (req.getDescription() == null || req.getDescription().length() < 10 || req.getDescription().length() > 5000) {
            throw new IllegalArgumentException("Invalid description");
        }

        if (req.getState() == null || req.getState().isBlank()) {
            throw new IllegalArgumentException("Invalid state");
        }

        if (req.getCity() == null || req.getCity().isBlank()) {
            throw new IllegalArgumentException("Invalid city");
        }

        if (req.getSalary() != null && req.getSalary() <= 0) {
            throw new IllegalArgumentException("Invalid salary");
        }

        Vaga vaga = new Vaga();
        vaga.setTitle(req.getTitle());
        vaga.setArea(req.getArea());
        vaga.setDescription(req.getDescription());
        vaga.setState(req.getState());
        vaga.setCity(req.getCity());
        vaga.setSalary(req.getSalary());
        vaga.setCompany(empresa);
        vaga.setContact(empresa.getEmail());

        return vagaRepository.save(vaga);
    }

    
    
    public List<VagaResponse> listarVagasPorEmpresa(String token, Long companyId, VagaFiltroRequest filtros) {

        Long empresaIdToken = jwtUtil.getCompanyIdFromToken(token);

        if (empresaIdToken == null) {
            throw new RuntimeException("Invalid token");
        }

        if (!empresaIdToken.equals(companyId)) {
            throw new SecurityException("Forbidden");
        }

        List<Vaga> vagas = listarVagasDaEmpresa(token);

        if (vagas.isEmpty()) {
            throw new RuntimeException("Job not found");
        }

        if (filtros != null && filtros.getFilters() != null && !filtros.getFilters().isEmpty()) {
            VagaFiltroRequest.Filtro f = filtros.getFilters().get(0);

            vagas = vagas.stream().filter(v -> {

                boolean ok = true;

                if (f.title != null && !v.getTitle().toLowerCase().contains(f.title.toLowerCase())) {
                    ok = false;
                }

                if (f.area != null && !v.getArea().equalsIgnoreCase(f.area)) {
                    ok = false;
                }

                if (f.state != null && !v.getState().equalsIgnoreCase(f.state)) {
                    ok = false;
                }

                if (f.city != null && !v.getCity().equalsIgnoreCase(f.city)) {
                    ok = false;
                }

                if (f.salary_range != null) {
                    Double sal = v.getSalary();
                    if (f.salary_range.min != null && sal < f.salary_range.min) {
                        ok = false;
                    }
                    if (f.salary_range.max != null && sal > f.salary_range.max) {
                        ok = false;
                    }
                }

                return ok;

            }).toList();
        }

        return vagas.stream().map(v -> {
            VagaResponse r = new VagaResponse();
            r.setJob_id(v.getId());
            r.setTitle(v.getTitle());
            r.setArea(v.getArea());
            r.setCompany(v.getCompany().getName());
            r.setDescription(v.getDescription());
            r.setState(v.getState());
            
            r.setCity(v.getCity());
            r.setSalary(v.getSalary());
            r.setContact(v.getCompany().getEmail());
            return r;
        }).toList();
    }


    public List<Vaga> listAll() {
        return vagaRepository.findAll();
    }
    
    public List<Vaga> listarVagasDaEmpresa(String token) {

        Long companyId = jwtUtil.getCompanyIdFromToken(token);

        Empresa empresa = empresaRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        return vagaRepository.findByCompany(empresa);
    }

    public Optional<Vaga> getById(Long id) {
        return vagaRepository.findById(id);
    }

}
