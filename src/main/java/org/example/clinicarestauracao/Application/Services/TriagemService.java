package org.example.clinicarestauracao.Application.Services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.clinicarestauracao.Application.Exceptions.Triagem.TriagemNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Triagem.TriagemWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.TriagemRepository;
import org.example.clinicarestauracao.Domain.Entities.Triagem;

import org.example.clinicarestauracao.Domain.Enums.Status;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class TriagemService {

    private final TriagemRepository repository;
    public List<Triagem> findAllTriagens() {
        return repository.findAll();
    }

    public Triagem findTriagemById(Long id)
    {
        if (id == null || id <= 0)
        {
            throw new TriagemNotFoundException("Triagem não encontrada.");
        }

        return repository.findById(id).orElseThrow(() -> new TriagemNotFoundException("Triagem não encontrada."));
    }

    public List<Triagem> findTriagemByName(String nomePossivelAcolhido)
    {
        if (nomePossivelAcolhido == null || nomePossivelAcolhido.isBlank())
        {
            throw new TriagemNotFoundException("Triagens não encontradas.");
        }
        List<Triagem> triagem = repository.findByNomePossivelAcolhidoContainingIgnoreCase(nomePossivelAcolhido.strip());

        if(triagem.isEmpty())
        {
            throw new TriagemNotFoundException("Triagens não encontradas.");
        }

        return triagem;
    }

    public List<Triagem> findTriagemByStatus(Status status)
    {
        if (status == null)
        {
            throw new TriagemNotFoundException("Triagem não encontrada.");
        }
        List<Triagem> triagens = repository.findByStatus(status);

        if(triagens.isEmpty())
        {
            throw new TriagemNotFoundException("Triagens não encontradas.");
        }

        return triagens;
    }

    @Transactional
    public Triagem createTriagem(Triagem toCreate) {

        if (toCreate == null)
        {
            throw new TriagemWithInvalidInformationException("Informações de triagem não pode ser nula.");
        }

        return repository.save(toCreate);
    }

    @Transactional
    public Triagem updateTriagem(Long id, Triagem toUpdate)
    {
        if (toUpdate == null)
        {
            throw new TriagemWithInvalidInformationException("Informações de triagem não podem ser nulas.");
        }
        Triagem triagem = findTriagemById(id);

        if (triagem.getStatus() == Status.CONCLUIDA)
        {
            throw new TriagemWithInvalidInformationException("Uma triagem concluída deve ser desconcluída antes de ser alterada.");
        }

        triagem.setConvenio(toUpdate.getConvenio());
        triagem.setNomePossivelAcolhido(toUpdate.getNomePossivelAcolhido());
        triagem.setTelefone(toUpdate.getTelefone());
        triagem.reagendar(toUpdate.getDataAgendada());
        triagem.setObservacaoEntrevista(toUpdate.getObservacaoEntrevista());

        Triagem triagemAtualizada = repository.save(triagem);

        return triagemAtualizada;
    }

    @Transactional
    public void deleteTriagem(Long id)
    {
        if (id == null || id <= 0)
        {
            throw new TriagemNotFoundException("Triagem não encontrada.");
        }

        Triagem triagem = findTriagemById(id);

        repository.delete(triagem);
    }

    @Transactional
    public Triagem concluir(Long id) {

        Triagem triagem = findTriagemById(id);

        triagem.concluir();

        return repository.save(triagem);
    }

    @Transactional
    public Triagem concluir(Long id, LocalDateTime dateConclusao) {

        Triagem triagem = findTriagemById(id);

        triagem.concluir(dateConclusao);

        return repository.save(triagem);
    }

    @Transactional
    public Triagem desconcluir(Long id) {

        Triagem triagem = findTriagemById(id);

        triagem.desconcluir();

        return repository.save(triagem);
    }

    @Transactional
    public Triagem reagendar(Long id, LocalDateTime novaData) {

        Triagem triagem = findTriagemById(id);

        triagem.reagendar(novaData);

        return repository.save(triagem);
    }

    @Transactional
    public Triagem cancelarTriagem(Long id)
    {
        Triagem triagem = findTriagemById(id);

        triagem.cancelarTriagem();

        return repository.save(triagem);
    }

    @Transactional
    public Triagem descancelarTriagem(Long id)
    {
        Triagem triagem = findTriagemById(id);

        triagem.descancelarTriagem();

        return repository.save(triagem);
    }

    @Transactional
    public void atualizarTriagensVencidas()
    {
        List<Triagem> triagens = repository.findByStatusAndDataAgendadaBefore(Status.AGENDADA, LocalDateTime.now());

        for (Triagem triagem : triagens) {
            triagem.marcarComoVencida();
        }

        repository.saveAll(triagens);
    }
}