package org.example.clinicarestauracao.Application.Interfaces;

import org.example.clinicarestauracao.Domain.Entities.Acolhido;
import org.example.clinicarestauracao.Domain.Entities.Convenio;
import org.example.clinicarestauracao.Domain.Entities.Internacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InternacaoRepository extends JpaRepository<Internacao, Long>
{
    List<Internacao> findByAcolhido(Acolhido acolhido);
    List<Internacao> findByConvenio(Convenio convenio);
    boolean existsByAcolhidoAndAtivoTrue(Acolhido acolhido);
    boolean existsByAcolhido(Acolhido acolhido);
    boolean existsByConvenio(Convenio convenio);
    List<Internacao> findByAtivoTrue();

}
