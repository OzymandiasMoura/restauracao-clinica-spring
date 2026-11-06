package org.example.clinicarestauracao.Application.Services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.clinicarestauracao.Application.Exceptions.Responsavel.ResponsavelNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Responsavel.ResponsavelWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.ResponsavelRepository;
import org.example.clinicarestauracao.Domain.Entities.Responsavel;
import org.example.clinicarestauracao.Domain.Validation.CpfValidator;
import org.example.clinicarestauracao.Domain.Validation.EmailValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ResponsavelService
{
    private final ResponsavelRepository repository;

    public List<Responsavel> findAll()
    {
        return repository.findAll();
    }

    public Responsavel findResponsavelById(Long id)
    {
        if (id == null || id <= 0)
        {
            throw new ResponsavelNotFoundException("Responsável não encontrado.");
        }
        return repository.findById(id).orElseThrow(() -> new ResponsavelNotFoundException("Responsável não encontrado."));
    }

    public List<Responsavel> findResponsavelByNome(String nome)
    {
        if (nome == null || nome.isBlank())
        {
            throw new ResponsavelNotFoundException("Responsável não encontrado.");
        }

        String nomeNormalized = nome.strip();

        List<Responsavel> response = repository.findByNomeContainingIgnoreCase(nomeNormalized);

        if (response.isEmpty())
        {
            throw new ResponsavelNotFoundException("Responsável não encontrado.");
        }

        return response;
    }

    public Responsavel findResponsavelByEmail(String email)
    {
        if (email == null || email.isBlank())
        {
            throw new ResponsavelNotFoundException("Responsável não encontrado.");
        }

        String emailNormalized = EmailValidator.normalize(email);

        return repository.findByEmail(emailNormalized).orElseThrow(() -> new ResponsavelNotFoundException("Responsável não encontrado."));
    }

    public Responsavel findResponsavelByCpf(String cpf)
    {
        if (cpf == null || cpf.isBlank())
        {
            throw new ResponsavelNotFoundException("Responsável não encontrado.");
        }

        String cpfNormalized = CpfValidator.normalize(cpf);

        return repository.findByCpf(cpfNormalized).orElseThrow(() -> new ResponsavelNotFoundException("Responsável não encontrado."));
    }

    public Responsavel createResponsavel(Responsavel responsavel)
    {
        if (responsavel == null)
        {
            throw new ResponsavelWithInvalidInformationException("Responsável não pode ser nulo.");
        }

        if (repository.existsByCpf(responsavel.getCpf()))
        {
            throw new ResponsavelWithInvalidInformationException("CPF já cadastrado.");
        }

        if (repository.existsByEmail(responsavel.getEmail()))
        {
            throw new ResponsavelWithInvalidInformationException("Email já cadastrado.");
        }

        return repository.save(responsavel);
    }

    @Transactional
    public Responsavel updateResponsavel(Long id, Responsavel responsavel)
    {
        if (id == null || id <= 0)
        {
            throw new ResponsavelNotFoundException("Responsável não encontrado.");
        }
        if (responsavel == null)
        {
            throw new ResponsavelWithInvalidInformationException("As informações para atualização não podem ser nulas.");
        }

        Responsavel responsavelToUpdate = findResponsavelById(id);

        Optional<Responsavel> sameCpf = repository.findByCpf(responsavel.getCpf());
        if (sameCpf.isPresent() && !sameCpf.get().getId().equals(responsavelToUpdate.getId()))
        {
            throw new ResponsavelWithInvalidInformationException("CPF já cadastrado.");
        }
        Optional<Responsavel> sameEmail = repository.findByEmail(responsavel.getEmail());
        if (sameEmail.isPresent() && !sameEmail.get().getId().equals(responsavelToUpdate.getId()))
        {
            throw new ResponsavelWithInvalidInformationException("Email já cadastrado.");
        }

        responsavelToUpdate.setNome(responsavel.getNome());
        responsavelToUpdate.setCpf(responsavel.getCpf());
        responsavelToUpdate.setEmail(responsavel.getEmail());
        responsavelToUpdate.setCep(responsavel.getCep());
        responsavelToUpdate.setEndereco(responsavel.getEndereco());
        responsavelToUpdate.setTelefone(responsavel.getTelefone());

        return repository.save(responsavelToUpdate);
    }

    @Transactional
    public Responsavel deactivateResponsavel(Long id)
    {
        Responsavel responsavel = findResponsavelById(id);

        responsavel.deactivateResponsavel();

        return repository.save(responsavel);
    }

    @Transactional
    public Responsavel reactivateResponsavel(Long id)
    {
        Responsavel responsavel = findResponsavelById(id);

        responsavel.reactivateResponsavel();

        return repository.save(responsavel);
    }

    @Transactional
    public void deleteResponsavel(Long id)
    {
        Responsavel responsavel = findResponsavelById(id);

        repository.delete(responsavel);
    }
}
