package org.example.clinicarestauracao.Application.Interfaces;

import jakarta.persistence.LockModeType;
import org.example.clinicarestauracao.Domain.Entities.Medicamento;
import org.example.clinicarestauracao.Domain.Entities.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MovimentacaoEstoqueRepository
        extends JpaRepository<MovimentacaoEstoque, Long> {

    List<MovimentacaoEstoque>
    findByMedicamentoIdOrderByDataMovimentacaoDescIdDesc(
            Long medicamentoId
    );

    boolean existsByMedicamentoId(Long id);
}