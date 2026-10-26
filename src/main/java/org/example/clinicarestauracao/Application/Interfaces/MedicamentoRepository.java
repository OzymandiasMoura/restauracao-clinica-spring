package org.example.clinicarestauracao.Application.Interfaces;

import org.example.clinicarestauracao.Domain.Entities.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    Optional<Medicamento> findByNomeIgnoreCase(String nome);


    @Query("SELECT e FROM Medicamento e WHERE e.quantidadeEstoque <= e.quantidadeMinima")
    List<Medicamento> findEstoquesEmAlerta();

}
