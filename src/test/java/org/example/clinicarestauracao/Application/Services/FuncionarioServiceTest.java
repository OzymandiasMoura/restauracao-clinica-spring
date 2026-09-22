package org.example.clinicarestauracao.Application.Services;

import org.example.clinicarestauracao.Application.Exceptions.Funcionario.FuncionarioNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Funcionario.FuncionarioWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.FuncionarioRepository;
import org.example.clinicarestauracao.Builders.FuncionarioTestBuilder;
import org.example.clinicarestauracao.Domain.Entities.Funcionario;
import org.example.clinicarestauracao.Domain.Entities.User;
import org.example.clinicarestauracao.Domain.Enums.UserRoles;
import org.example.clinicarestauracao.Domain.Validation.EmailValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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
        Long id = 1L;
        Mockito.when(repository.findFuncionarioById(id)).thenReturn(Optional.empty());

        var exception = assertThrows(FuncionarioNotFoundException.class, () -> service.findFuncionarioById(id));

        assertEquals("Funcionário não encontrado.", exception.getMessage());

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

    @Test
    void shouldFindFuncionarioByEmailSuccessfully()
    {
        String entryEmail = " PEDRO@EMAIL.COM ";
        String formatedEmail = "pedro@email.com";
        Funcionario func = FuncionarioTestBuilder.newFuncionario().build();

        Mockito.when(repository.findFuncionarioByEmail(formatedEmail)).thenReturn(Optional.of(func));

        Funcionario response = service.findFuncionarioByEmail(entryEmail);

        assertSame(func, response);

        Mockito.verify(repository).findFuncionarioByEmail(formatedEmail);
    }

    @Test
    void shouldThrowFuncionarioNotFoundExceptionWhenEmailDoesNotExist()
    {
        String entryEmail = "pedro@email.com";
        Mockito.when(repository.findFuncionarioByEmail(entryEmail)).thenReturn(Optional.empty());

        var exception = assertThrows(FuncionarioNotFoundException.class, () -> service.findFuncionarioByEmail(entryEmail));

        assertEquals("Funcionário não encontrado.", exception.getMessage());

        Mockito.verify(repository).findFuncionarioByEmail(entryEmail);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "    "})
    void shouldThrowFuncionarioNotFoundExceptionWhenEmailIsInvalid(String email)
    {
        FuncionarioNotFoundException exception = assertThrows(FuncionarioNotFoundException.class, () -> service.findFuncionarioByEmail(email));

        assertEquals("Funcionário não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

    @Test
    void shouldFindFuncionarioByCpfSuccessfully()
    {
        String entryCpf = "529.982.247-25";
        String formatedCpf = "52998224725";
        Funcionario func = FuncionarioTestBuilder.newFuncionario().build();

        Mockito.when(repository.findFuncionarioByCpf(formatedCpf)).thenReturn(Optional.of(func));

        Funcionario response = service.findFuncionarioByCpf(entryCpf);

        assertSame(func, response);

        Mockito.verify(repository).findFuncionarioByCpf(formatedCpf);
    }

    @Test
    void shouldThrowFuncionarioNotFoundExceptionWhenCpfDoesNotExist()
    {
        String entryCpf = "52998224725";

        Mockito.when(repository.findFuncionarioByCpf(entryCpf)).thenReturn(Optional.empty());

        FuncionarioNotFoundException exception =  assertThrows(FuncionarioNotFoundException.class, () -> service.findFuncionarioByCpf(entryCpf));

        assertEquals( "Funcionário não encontrado.",  exception.getMessage());

        Mockito.verify(repository).findFuncionarioByCpf(entryCpf);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", " . - "})
    void shouldThrowFuncionarioNotFoundExceptionWhenCpfIsInvalid(String cpf)
    {
        FuncionarioNotFoundException exception = assertThrows(FuncionarioNotFoundException.class, () -> service.findFuncionarioByCpf(cpf));

        assertEquals("Funcionário não encontrado.",  exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

    @Test
    void shouldFindAllFuncionariosSuccessfully()
    {
        Funcionario activeFuncionario = FuncionarioTestBuilder.newFuncionario().setId(1L).setAtivo(true).build();
        Funcionario inactiveFuncionario = FuncionarioTestBuilder.newFuncionario().setId(2L).setAtivo(false).build();
        List<Funcionario>  funcionarios = List.of(activeFuncionario, inactiveFuncionario);

        Mockito.when(repository.findAll()).thenReturn(funcionarios);

        List<Funcionario> response = service.findAllFuncionarios();

        assertSame(funcionarios, response);
        assertSame(activeFuncionario, response.get(0));
        assertSame(inactiveFuncionario, response.get(1));
        assertEquals(2, response.size());

        Mockito.verify(repository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoFuncionariosExist()
    {
        Mockito.when(repository.findAll()).thenReturn(List.of());

        List<Funcionario> response = service.findAllFuncionarios();

        assertNotNull(response);
        assertTrue(response.isEmpty());

        Mockito.verify(repository).findAll();
    }
}