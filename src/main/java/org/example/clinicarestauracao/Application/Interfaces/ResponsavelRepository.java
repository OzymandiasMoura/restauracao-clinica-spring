package org.example.clinicarestauracao.Application.Interfaces;

import org.example.clinicarestauracao.Domain.Entities.Responsavel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResponsavelRepository extends JpaRepository<Responsavel, Long>
{
    List<Responsavel> findByNomeContainingIgnoreCase(String nome);

    Optional<Responsavel> findByEmail(String email);

    Optional<Responsavel> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);
}
