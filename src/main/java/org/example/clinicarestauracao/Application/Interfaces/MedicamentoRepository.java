package org.example.clinicarestauracao.Application.Interfaces;

import jakarta.persistence.LockModeType;
import org.example.clinicarestauracao.Domain.Entities.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    Optional<Medicamento> findByNomeIgnoreCase(String nome);


    @Query("SELECT e FROM Medicamento e WHERE e.quantidadeEstoque <= e.quantidadeMinima")
    List<Medicamento> findEstoquesEmAlerta();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM Medicamento m WHERE m.id = :id")
    Optional<Medicamento> buscarParaMovimentacao(
            @Param("id") Long id
    );
}
