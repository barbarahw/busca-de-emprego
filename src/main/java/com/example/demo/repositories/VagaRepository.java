package com.example.demo.repositories;

import com.example.demo.model.Vaga;
import com.example.demo.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VagaRepository extends JpaRepository<Vaga, Long> {
    List<Vaga> findByCompany(Empresa company);
    

}
