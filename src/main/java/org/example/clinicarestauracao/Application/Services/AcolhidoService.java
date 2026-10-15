package org.example.clinicarestauracao.Application.Services;

import jakarta.transaction.Transactional;
import org.example.clinicarestauracao.Application.Exceptions.Acolhido.AcolhidoNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Acolhido.AcolhidoWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.AcolhidoRepository;
import org.example.clinicarestauracao.Domain.Entities.Acolhido;
import org.example.clinicarestauracao.Domain.Validation.CpfValidator;
import org.example.clinicarestauracao.Domain.Validation.EmailValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AcolhidoService
{
    private final AcolhidoRepository repository;

    public AcolhidoService(AcolhidoRepository repository)
    {
        this.repository = repository;
    }

    public List<Acolhido> findAllAcolhidos()
    {
        return repository.findAll();
    }

    public Acolhido findAcolhidoById(Long id)
    {
        if (id == null || id <= 0)
        {
            throw new AcolhidoNotFoundException("Acolhido não encontrado.");
        }

        return repository.findById(id).orElseThrow(() -> new AcolhidoNotFoundException("Acolhido não encontrado."));
    }

    public List<Acolhido> findAcolhidoByNome(String nome)
    {
        if(nome == null || nome.isBlank())
        {
            throw new AcolhidoNotFoundException("Acolhido não encontrado.");
        }

        String nomeNormalizado = nome.strip();

        List<Acolhido> resultado = repository.findAcolhidoByNomeContainingIgnoreCase(nomeNormalizado);

        if(resultado.isEmpty())
        {
            throw new AcolhidoNotFoundException("Acolhido não encontrado.");
        }
        return resultado;
    }

    public Acolhido findAcolhidoByEmail(String email)
    {
        if(email == null || email.isBlank())
        {
            throw new AcolhidoNotFoundException("Acolhido não encontrado.");
        }

        String emailNormalizado = EmailValidator.normalize(email);

        if (emailNormalizado == null || emailNormalizado.isBlank())
        {
            throw new AcolhidoNotFoundException("Acolhido não encontrado.");
        }

        return repository.findAcolhidoByEmail(emailNormalizado).orElseThrow(() -> new AcolhidoNotFoundException("Acolhido não encontrado."));
    }

    public Acolhido findAcolhidoByCpf(String cpf)
    {
        String cpfNormalizado = CpfValidator.normalize(cpf);

        if (cpfNormalizado == null || cpfNormalizado.isBlank())
        {
            throw new AcolhidoNotFoundException("Acolhido não encontrado.");
        }

        return repository.findAcolhidoByCpf(cpfNormalizado).orElseThrow(() -> new AcolhidoNotFoundException("Acolhido não encontrado."));
    }

    public Acolhido createAcolhido(Acolhido acolhido)
    {
        boolean sameCpf = repository.findAcolhidoByCpf(acolhido.getCpf()).isPresent();
        if (sameCpf)
        {
            throw new AcolhidoWithInvalidInformationException("CPF já cadastrado.");
        }
        boolean sameEmail = repository.findAcolhidoByEmail(acolhido.getEmail()).isPresent();
        if (sameEmail)
        {
            throw new AcolhidoWithInvalidInformationException("Email já cadastrado.");
        }

        return repository.save(acolhido);
    }

    @Transactional
    public Acolhido updateAcolhido(Long id, Acolhido acolhido)
    {
        Acolhido acolhidoToUpdate = findAcolhidoById(id);

        Optional<Acolhido> sameCpf = repository.findAcolhidoByCpf(acolhido.getCpf());
        if (sameCpf.isPresent() && !sameCpf.get().getId().equals(acolhidoToUpdate.getId()))
        {
            throw new AcolhidoWithInvalidInformationException("CPF já cadastrado.");
        }
        Optional<Acolhido> sameEmail = repository.findAcolhidoByEmail(acolhido.getEmail());
        if (sameEmail.isPresent() && !sameEmail.get().getId().equals(acolhidoToUpdate.getId()))
        {
            throw new AcolhidoWithInvalidInformationException("Email já cadastrado.");
        }

        acolhidoToUpdate.setNome(acolhido.getNome());
        acolhidoToUpdate.setEmail(acolhido.getEmail());
        acolhidoToUpdate.setCpf(acolhido.getCpf());
        acolhidoToUpdate.setCep(acolhido.getCep());
        acolhidoToUpdate.setEndereco(acolhido.getEndereco());
        acolhidoToUpdate.setTelefone(acolhido.getTelefone());
        acolhidoToUpdate.setDataNascimento(acolhido.getDataNascimento());

        return repository.save(acolhidoToUpdate);
    }

    @Transactional
    public void deactivateAcolhido(Long id)
    {
        Acolhido acolhidoToDeactivate = findAcolhidoById(id);

        acolhidoToDeactivate.deactivate();

        repository.save(acolhidoToDeactivate);
    }

    @Transactional
    public void reactivateAcolhido(Long id)
    {
        Acolhido acolhidoToReactivate = findAcolhidoById(id);

        acolhidoToReactivate.reactivate();

        repository.save(acolhidoToReactivate);
    }

    @Transactional
    public void deleteAcolhidoById(Long id)
    {
        Acolhido acolhidoToDelete = findAcolhidoById(id);

        repository.delete(acolhidoToDelete);
    }
}
