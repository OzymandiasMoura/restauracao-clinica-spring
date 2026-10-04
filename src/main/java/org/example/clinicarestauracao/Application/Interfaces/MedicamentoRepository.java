package org.example.clinicarestauracao.Application.Interfaces;

import org.example.clinicarestauracao.Domain.Entities.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    Optional<Medicamento> findByNomeIgnoreCase(String nome);

    List<Medicamento> findByAtivoTrue();

    List<Medicamento> findByAtivoFalse();

}
