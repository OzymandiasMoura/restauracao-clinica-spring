package org.example.clinicarestauracao.Application.Interfaces;

import org.example.clinicarestauracao.Domain.Entities.EstoqueMedicamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstoqueMedicamentoRepository extends JpaRepository<EstoqueMedicamento, Long> {
}
