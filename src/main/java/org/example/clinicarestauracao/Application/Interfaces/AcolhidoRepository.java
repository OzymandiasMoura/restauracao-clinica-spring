package org.example.clinicarestauracao.Application.Interfaces;

import org.example.clinicarestauracao.Domain.Entities.Acolhido;
import org.example.clinicarestauracao.Domain.Entities.Responsavel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AcolhidoRepository extends JpaRepository<Acolhido, Long>
{
    List<Acolhido> findAcolhidoByNomeContainingIgnoreCase(String nome);
    Optional<Acolhido> findAcolhidoByEmail(String email);
    Optional<Acolhido> findAcolhidoByCpf(String cpf);
    boolean existsAcolhidoByResponsavelIdAndAtivoTrue(Long id);
    boolean existsAcolhidoByResponsavelId(Long id);
    boolean existsByResponsavelIdAndAtivoTrueAndIdNot(Long responsavelId, Long acolhidoId);
}
