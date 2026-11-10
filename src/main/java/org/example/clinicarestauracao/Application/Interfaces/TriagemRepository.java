package org.example.clinicarestauracao.Application.Interfaces;


import org.example.clinicarestauracao.Domain.Entities.Triagem;
import org.example.clinicarestauracao.Domain.Enums.TriagemStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface TriagemRepository extends JpaRepository<Triagem, Long> {

    Optional<Triagem> findByNomePossivelAcolhidoIgnoreCase(String nomePossivelAcolhido);

    List<Triagem> findByTriagemStatus(TriagemStatus triagemStatus);

    List<Triagem> findByTriagemStatusAndDataAgendadaBefore(
            TriagemStatus triagemStatus,
            LocalDateTime data
    );
    List<Triagem> findByTriagemStatusAndAtivoTrueAndDataAgendadaBefore(
            TriagemStatus status,
            LocalDateTime data
    );
}
