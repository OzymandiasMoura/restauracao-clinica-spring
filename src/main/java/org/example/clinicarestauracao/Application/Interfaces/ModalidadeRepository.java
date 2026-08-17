package org.example.clinicarestauracao.Application.Interfaces;

import org.example.clinicarestauracao.Domain.Entities.Modalidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModalidadeRepository extends JpaRepository<Modalidade, Long>
{
    Optional<Modalidade> findModalidadeByDescricao(String descricao);
}
