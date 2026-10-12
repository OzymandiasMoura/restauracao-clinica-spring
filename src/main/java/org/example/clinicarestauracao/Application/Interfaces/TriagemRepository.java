package org.example.clinicarestauracao.Application.Interfaces;


import org.example.clinicarestauracao.Domain.Entities.Triagem;
import org.example.clinicarestauracao.Domain.Enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface TriagemRepository extends JpaRepository<Triagem, Long> {

    Optional<Triagem> findByNomePossivelAcolhidoIgnoreCase(String nomePossivelAcolhido);

    List<Triagem> findByStatus(Status status);
}
