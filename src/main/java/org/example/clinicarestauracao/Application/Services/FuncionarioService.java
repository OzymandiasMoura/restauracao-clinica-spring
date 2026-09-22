package org.example.clinicarestauracao.Application.Services;

import org.example.clinicarestauracao.Application.Exceptions.Funcionario.FuncionarioNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Funcionario.FuncionarioWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.FuncionarioRepository;
import org.example.clinicarestauracao.Domain.Entities.Funcionario;
import org.example.clinicarestauracao.Domain.Validation.CpfValidator;
import org.example.clinicarestauracao.Domain.Validation.EmailValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuncionarioService
{
    private final FuncionarioRepository repository;

    public FuncionarioService(FuncionarioRepository repository)
    {
        this.repository = repository;
    }

    public Funcionario createFuncionario(Funcionario funcionario)
    {
        if(repository.findFuncionarioByCpf(funcionario.getCpf()).isPresent())
        {
            throw new FuncionarioWithInvalidInformationException("CPF já cadastrado.");
        }
        if(repository.findFuncionarioByEmail(funcionario.getEmail()).isPresent())
        {
            throw new FuncionarioWithInvalidInformationException("E-mail já cadastrado.");
        }
        if(funcionario.getUser() != null && repository.findFuncionarioByUser(funcionario.getUser()).isPresent())
        {
            throw new FuncionarioWithInvalidInformationException("Usuário já vinculado a outro funcionário.");
        }
        return repository.save(funcionario);
    }

    public Funcionario findFuncionarioById(Long id)
    {
        if (id == null || id <= 0)
        {
            throw new FuncionarioNotFoundException("Funcionário não encontrado.");
        }
        return repository.findFuncionarioById(id).orElseThrow(() -> new FuncionarioNotFoundException("Funcionário não encontrado."));
    }

    public Funcionario findFuncionarioByEmail(String email)
    {
        if (email == null || email.isBlank())
        {
            throw new FuncionarioNotFoundException("Funcionário não encontrado.");
        }

        String normalized = EmailValidator.normalize(email);

        return repository.findFuncionarioByEmail(normalized).orElseThrow(() -> new FuncionarioNotFoundException("Funcionário não encontrado."));
    }

    public Funcionario findFuncionarioByCpf(String cpf)
    {
        String normalized = CpfValidator.normalize(cpf);

        if(normalized == null || normalized.isBlank())
        {
            throw new FuncionarioNotFoundException("Funcionário não encontrado.");
        }

        return repository.findFuncionarioByCpf(normalized).orElseThrow(() -> new FuncionarioNotFoundException("Funcionário não encontrado."));
    }

    public List<Funcionario> findAllFuncionarios()
    {
        return repository.findAll();
    }
}
