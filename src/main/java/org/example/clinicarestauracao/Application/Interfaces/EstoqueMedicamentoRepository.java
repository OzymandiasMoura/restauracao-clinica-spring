package org.example.clinicarestauracao.Application.Interfaces;

import org.example.clinicarestauracao.Domain.Entities.EstoqueMedicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EstoqueMedicamentoRepository extends JpaRepository<EstoqueMedicamento, Long> {
    Optional<EstoqueMedicamento> findByMedicamentoId(Long id);

    @Query("SELECT e FROM EstoqueMedicamento e WHERE e.quantidadeEstoque <= e.quantidadeMinimaAlerta")
    List<EstoqueMedicamento> findEstoquesEmAlerta();
}
