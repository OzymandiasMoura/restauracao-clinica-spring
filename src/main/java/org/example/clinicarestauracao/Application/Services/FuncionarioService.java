package org.example.clinicarestauracao.Application.Services;

import org.example.clinicarestauracao.Application.Exceptions.Funcionario.FuncionarioWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.FuncionarioRepository;
import org.example.clinicarestauracao.Domain.Entities.Funcionario;
import org.springframework.stereotype.Service;

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
}
