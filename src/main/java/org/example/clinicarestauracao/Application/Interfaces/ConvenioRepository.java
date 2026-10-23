package org.example.clinicarestauracao.Application.Interfaces;

import org.example.clinicarestauracao.Domain.Entities.Convenio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConvenioRepository extends JpaRepository<Convenio, Long>
{
    Optional<Convenio> findConvenioByDescricaoIgnoreCase(String descricao);

    Optional<Convenio> findConvenioByCnpj(String cnpj);

    Optional<Convenio> findConvenioByCor(String cor);

}
