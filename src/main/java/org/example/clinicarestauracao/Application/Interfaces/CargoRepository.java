package org.example.clinicarestauracao.Application.Interfaces;

import org.example.clinicarestauracao.Domain.Entities.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CargoRepository extends JpaRepository<Cargo, Long>
{
    Optional<Cargo> findCargoByNome(String nome);

    Optional<Cargo> findCargoById(Long id);
}
