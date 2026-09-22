package org.example.clinicarestauracao.Application.Services;

import org.example.clinicarestauracao.Application.Exceptions.Funcionario.FuncionarioNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Funcionario.FuncionarioWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.FuncionarioRepository;
import org.example.clinicarestauracao.Builders.FuncionarioTestBuilder;
import org.example.clinicarestauracao.Domain.Entities.Funcionario;
import org.example.clinicarestauracao.Domain.Entities.User;
import org.example.clinicarestauracao.Domain.Enums.UserRoles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FuncionarioServiceTest
{
    @Mock
    private FuncionarioRepository repository;
    @InjectMocks
    private FuncionarioService service;

    @Test
    void shouldCreateFuncionarioWithoutUserSuccessfully()
    {
        Funcionario entrada = FuncionarioTestBuilder.newFuncionario().setUser(null).buildForCreate();
        Funcionario salvo = FuncionarioTestBuilder.newFuncionario().setUser(null).build();

        Mockito.when(repository.findFuncionarioByCpf(entrada.getCpf())).thenReturn(Optional.empty());
        Mockito.when(repository.findFuncionarioByEmail(entrada.getEmail())).thenReturn(Optional.empty());
        Mockito.when(repository.save(entrada)).thenReturn(salvo);

        Funcionario resultado = service.createFuncionario(entrada);

        assertSame(salvo, resultado);
        assertNotNull(resultado.getId());

        Mockito.verify(repository).findFuncionarioByCpf(entrada.getCpf());
        Mockito.verify(repository).findFuncionarioByEmail(entrada.getEmail());
        Mockito.verify(repository, Mockito.never()).findFuncionarioByUser(Mockito.any());
        Mockito.verify(repository).save(entrada);
    }

    @Test
    void shouldCreateFuncionarioWithUserSuccessfully()
    {
        User user = new User(1L, "pedro", "senha123", UserRoles.USER);

        Funcionario entrada = FuncionarioTestBuilder.newFuncionario().setUser(user).buildForCreate();
        Funcionario salvo = FuncionarioTestBuilder.newFuncionario().setUser(user).build();

        Mockito.when(repository.findFuncionarioByCpf(entrada.getCpf())).thenReturn(Optional.empty());
        Mockito.when(repository.findFuncionarioByEmail(entrada.getEmail())).thenReturn(Optional.empty());
        Mockito.when(repository.findFuncionarioByUser(user)).thenReturn(Optional.empty());
        Mockito.when(repository.save(entrada)).thenReturn(salvo);

        Funcionario resultado = service.createFuncionario(entrada);

        assertSame(salvo, resultado);

        Mockito.verify(repository).findFuncionarioByUser(entrada.getUser());
        Mockito.verify(repository).save(entrada);
    }

    @Test
    void shouldRejectFuncionarioWithDuplicatedCpf()
    {
        Funcionario entrada = FuncionarioTestBuilder.newFuncionario().buildForCreate();
        Funcionario existente = FuncionarioTestBuilder.newFuncionario().build();

        Mockito.when(repository.findFuncionarioByCpf(entrada.getCpf())).thenReturn(Optional.of(existente));

        var exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> service.createFuncionario(entrada));

        assertEquals("CPF já cadastrado.", exception.getMessage());

        Mockito.verify(repository, Mockito.never()).findFuncionarioByEmail(Mockito.anyString());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void shouldRejectFuncionarioWithDuplicatedEmail()
    {
        Funcionario entrada = FuncionarioTestBuilder.newFuncionario().buildForCreate();
        Funcionario existente = FuncionarioTestBuilder.newFuncionario().build();

        Mockito.when(repository.findFuncionarioByCpf(entrada.getCpf())).thenReturn(Optional.empty());
        Mockito.when(repository.findFuncionarioByEmail(entrada.getEmail())).thenReturn(Optional.of(existente));

        var exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> service.createFuncionario(entrada));

        assertEquals("E-mail já cadastrado.", exception.getMessage());

        Mockito.verify(repository, Mockito.never()).findFuncionarioByUser(Mockito.any());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void shouldRejectUserAlreadyLinkedToAnotherFuncionario()
    {
        User user = new User(1L, "pedro", "senha123", UserRoles.USER);

        Funcionario entrada = FuncionarioTestBuilder.newFuncionario().setUser(user).buildForCreate();
        Funcionario existente = FuncionarioTestBuilder.newFuncionario().setUser(user).build();

        Mockito.when(repository.findFuncionarioByCpf(entrada.getCpf())).thenReturn(Optional.empty());
        Mockito.when(repository.findFuncionarioByEmail(entrada.getEmail())).thenReturn(Optional.empty());
        Mockito.when(repository.findFuncionarioByUser(user)).thenReturn(Optional.of(existente));

        var exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> service.createFuncionario(entrada));

        assertEquals("Usuário já vinculado a outro funcionário.", exception.getMessage());

        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void shouldFindFuncionarioByIdSuccessfully()
    {
        Funcionario func = FuncionarioTestBuilder.newFuncionario().build();

        Mockito.when(repository.findFuncionarioById(func.getId())).thenReturn(Optional.of(func));

        Funcionario response = service.findFuncionarioById(func.getId());

        assertSame(func, response);

        Mockito.verify(repository).findFuncionarioById(func.getId());
    }

    @Test
    void shouldThrowFuncionarioNotFoundExceptionWhenFuncionarioDoesNotExist()
    {
        Long id =  1L;
        Mockito.when(repository.findFuncionarioById(id)).thenReturn(Optional.empty());

        var exception = assertThrows(FuncionarioNotFoundException.class, () -> service.findFuncionarioById(id));

        assertEquals("Funcionário não encontrado.",  exception.getMessage());

        Mockito.verify(repository).findFuncionarioById(id);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldThrowFuncionarioNotFoundExceptionWhenFuncionarioIdIsInvalid(Long id)
    {
        FuncionarioNotFoundException exception = assertThrows(FuncionarioNotFoundException.class, () -> service.findFuncionarioById(id));

        assertEquals("Funcionário não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }


}