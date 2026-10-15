package org.example.clinicarestauracao.Application.Interfaces;

import org.example.clinicarestauracao.Domain.Entities.Acolhido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AcolhidoRepository extends JpaRepository<Acolhido, Long>
{
    List<Acolhido> findAcolhidoByNomeContainingIgnoreCase(String nome);
    Optional<Acolhido> findAcolhidoByEmail(String email);
    Optional<Acolhido> findAcolhidoByCpf(String cpf);

}
