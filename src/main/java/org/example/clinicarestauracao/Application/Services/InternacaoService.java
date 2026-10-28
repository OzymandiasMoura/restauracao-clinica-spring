package org.example.clinicarestauracao.Application.Services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.clinicarestauracao.Application.Exceptions.Internacao.InternacaoNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Internacao.InternacaoWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.InternacaoRepository;
import org.example.clinicarestauracao.Domain.Entities.Acolhido;
import org.example.clinicarestauracao.Domain.Entities.Convenio;
import org.example.clinicarestauracao.Domain.Entities.Internacao;
import org.example.clinicarestauracao.Domain.Enums.AltaTypes;
import org.example.clinicarestauracao.Domain.Enums.TreatmentStage;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class InternacaoService
{
    private final InternacaoRepository repository;
    private final AcolhidoService acolhidoService;
    private final ConvenioService convenioService;

    public Internacao findInternacaoById(Long id)
    {
        if(id == null || id <= 0)
        {
            throw new InternacaoNotFoundException("Internação não encontrada.");
        }

        return repository.findById(id).orElseThrow(() -> new InternacaoNotFoundException("Internação não encontrada."));
    }

    public List<Internacao> findAllInternacoes()
    {
        return repository.findAll();
    }

    public List<Internacao> findInternacoesByAcolhidoId(Long id)
    {
        if(id == null || id <= 0)
        {
            throw new InternacaoNotFoundException("Internações não encontradas.");
        }
        Acolhido acolhidoToFind = acolhidoService.findAcolhidoById(id);

        return repository.findByAcolhido(acolhidoToFind);
    }

    public List<Internacao> findInternacoesByConvenioId(Long id)
    {
        if(id == null || id <= 0)
        {
            throw new InternacaoNotFoundException("Internações não encontradas.");
        }
        Convenio convenioToFind = convenioService.findConvenioById(id);

        return repository.findByConvenio(convenioToFind);
    }

    @Transactional
    public Internacao createInternacao(Internacao internacao)
    {
        if (internacao == null)
        {
            throw new InternacaoWithInvalidInformationException("Internação não pode ser nula.");
        }

        Convenio convenio = convenioService.findConvenioById(internacao.getConvenio().getId());
        Acolhido acolhido = acolhidoService.findAcolhidoById(internacao.getAcolhido().getId());

        if (!convenio.isAtivo())
        {
            throw new InternacaoWithInvalidInformationException("Convênio inválido.");
        }
        if (!acolhido.getAtivo())
        {
            throw new InternacaoWithInvalidInformationException("Acolhido inválido.");
        }
        if (repository.existsByAcolhidoAndAtivoTrue(acolhido))
        {
            throw new InternacaoWithInvalidInformationException("Acolhido já possui internação ativa.");
        }

        return repository.save(internacao);
    }

    @Transactional
    public Internacao updateInternacao(Long id, LocalDate dataSaidaPrevista, TreatmentStage stage, BigDecimal valorMensalidade)
    {
        Internacao internacaoToUpdate = findInternacaoById(id);

        if (!Boolean.TRUE.equals(internacaoToUpdate.getAtivo()))
        {
            throw new InternacaoWithInvalidInformationException("Internação encerrada não pode ser atualizada.");
        }
        if (dataSaidaPrevista == null || stage == null ||  valorMensalidade == null)
        {
            throw new InternacaoWithInvalidInformationException("Os dados da internação são obrigatórios.");
        }

        internacaoToUpdate.setDataSaidaPrevista(dataSaidaPrevista);
        internacaoToUpdate.setValorMensalidade(valorMensalidade);
        internacaoToUpdate.setEtapaTratamento(stage);

        return repository.save(internacaoToUpdate);
    }

    @Transactional
    public void endInternacaoById(Long id, AltaTypes alta, LocalDate dataSaida)
    {
        Internacao internacaoToEnd = findInternacaoById(id);

        internacaoToEnd.endInternacao(alta, dataSaida);

        repository.save(internacaoToEnd);
    }

    @Transactional
    public void deleteInternacaoById(Long id)
    {
        Internacao internacaoToDelete = findInternacaoById(id);

        repository.delete(internacaoToDelete);
    }
}
