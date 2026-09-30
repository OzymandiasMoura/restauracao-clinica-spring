package org.example.clinicarestauracao.Application.Services;

import jakarta.transaction.Transactional;
import org.example.clinicarestauracao.Application.Exceptions.Funcionario.FuncionarioNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Funcionario.FuncionarioWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.FuncionarioRepository;
import org.example.clinicarestauracao.Domain.Entities.Funcionario;
import org.example.clinicarestauracao.Domain.Entities.User;
import org.example.clinicarestauracao.Domain.Validation.CpfValidator;
import org.example.clinicarestauracao.Domain.Validation.EmailValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    public Funcionario updateFuncionario(Long id, Funcionario funcionario)
    {
        Funcionario existed = this.findFuncionarioById(id);

        Optional<Funcionario> sameCpf = repository.findFuncionarioByCpf(funcionario.getCpf());
        if(sameCpf.isPresent() && !sameCpf.get().getId().equals(existed.getId()))
        {
            throw new FuncionarioWithInvalidInformationException("CPF já cadastrado.");
        }

        Optional<Funcionario> sameEmail = repository.findFuncionarioByEmail(funcionario.getEmail());
        if(sameEmail.isPresent() && !sameEmail.get().getId().equals(existed.getId()))
        {
            throw new FuncionarioWithInvalidInformationException("E-mail já cadastrado.");
        }

        existed.atualizarDatas(funcionario.getDataNascimento(), funcionario.getDataAdmissao());
        existed.setNome(funcionario.getNome());
        existed.setCpf(funcionario.getCpf());
        existed.setEmail(funcionario.getEmail());
        existed.setEndereco(funcionario.getEndereco());
        existed.setCep(funcionario.getCep());
        existed.setCargo(funcionario.getCargo());

        return repository.save(existed);
    }

    @Transactional
    public void dismissFuncionarioById(Long id, LocalDate dataDemissao)
    {
        if (id == null || id <= 0)
        {
            throw new FuncionarioNotFoundException("Funcionário não encontrado.");
        }

        Funcionario funcionario = this.findFuncionarioById(id);
        User user = funcionario.getUser();

        if (user == null)
        {
            throw new FuncionarioWithInvalidInformationException("Funcionário não possui usuário vinculado.");
        }

        funcionario.demitir(dataDemissao);

        if (user.isEnabled())
        {
            user.deactivate();
        }

        repository.save(funcionario);
    }


    @Transactional
    public void reactivateFuncionarioById(Long id)
    {
        if (id == null || id <= 0)
        {
            throw new FuncionarioNotFoundException("Funcionário não encontrado.");
        }

        Funcionario funcionario = this.findFuncionarioById(id);
        User user = funcionario.getUser();

        if (user == null)
        {
            throw new FuncionarioWithInvalidInformationException("Funcionário não possui usuário vinculado.");
        }

        if (funcionario.isAtivo())
        {
            return;
        }

        user.reactivate();
        funcionario.reactivate();

        repository.save(funcionario);
    }

    public void linkUserToFuncionario(Long id, User user)
    {
        if(user == null)
        {
            throw new FuncionarioWithInvalidInformationException("Usuário inválido.");
        }

        Funcionario f = this.findFuncionarioById(id);

        if(user.equals(f.getUser()))
        {
            return;
        }

        Optional<Funcionario> sameUser = repository.findFuncionarioByUser(user);

        if (sameUser.isPresent())
        {
            throw new FuncionarioWithInvalidInformationException("Usuário já vinculado a outro funcionário.");
        }

        f.setUser(user);
        repository.save(f);
    }

    public void unlinkUserFromFuncionario(Long id)
    {
        Funcionario f = this.findFuncionarioById(id);

        if (f.getUser() == null)
        {
            return;
        }
        f.setUser(null);
        repository.save(f);
    }
}
