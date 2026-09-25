package org.example.clinicarestauracao.Application.Services;

import org.example.clinicarestauracao.Application.Exceptions.Funcionario.FuncionarioNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Funcionario.FuncionarioWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.FuncionarioRepository;
import org.example.clinicarestauracao.Builders.CargoTestBuilder;
import org.example.clinicarestauracao.Builders.FuncionarioTestBuilder;
import org.example.clinicarestauracao.Domain.Entities.Cargo;
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

import java.time.LocalDate;
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

        FuncionarioNotFoundException exception = assertThrows(FuncionarioNotFoundException.class, () -> service.findFuncionarioByCpf(entryCpf));

        assertEquals("Funcionário não encontrado.", exception.getMessage());

        Mockito.verify(repository).findFuncionarioByCpf(entryCpf);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", " . - "})
    void shouldThrowFuncionarioNotFoundExceptionWhenCpfIsInvalid(String cpf)
    {
        FuncionarioNotFoundException exception = assertThrows(FuncionarioNotFoundException.class, () -> service.findFuncionarioByCpf(cpf));

        assertEquals("Funcionário não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

    @Test
    void shouldFindAllFuncionariosSuccessfully()
    {
        Funcionario activeFuncionario = FuncionarioTestBuilder.newFuncionario().setId(1L).setAtivo(true).build();
        Funcionario inactiveFuncionario = FuncionarioTestBuilder.newFuncionario().setId(2L).setAtivo(false).build();
        List<Funcionario> funcionarios = List.of(activeFuncionario, inactiveFuncionario);

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

    @Test
    void shouldUpdateFuncionarioSuccessfullyWithoutUserAndPreserveStatus()
    {
        Cargo updatedCargo = CargoTestBuilder.newCargo().setId(2L).setNome("Fisioterapeuta").build();
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setAtivo(false).setUser(null).build();
        Funcionario updatedData = FuncionarioTestBuilder.newFuncionario().setNome("Pedro Moura Atualizado").setCpf("12345678909").setEmail("pedro.atualizado@email.com").setDataNascimento(LocalDate.of(1991, 5, 20)).setEndereco("Avenida Paulista, 1000 - São Paulo").setCep("01310-100").setCargo(updatedCargo).setUser(null).buildForCreate();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(repository.findFuncionarioByCpf(updatedData.getCpf())).thenReturn(Optional.empty());
        Mockito.when(repository.findFuncionarioByEmail(updatedData.getEmail())).thenReturn(Optional.empty());
        Mockito.when(repository.save(existing)).thenReturn(existing);

        Funcionario response = service.updateFuncionario(1L, updatedData);

        assertEquals(1L, response.getId());
        assertEquals(updatedData.getNome(), response.getNome());
        assertEquals(updatedData.getCpf(), response.getCpf());
        assertEquals(updatedData.getEmail(), response.getEmail());
        assertEquals(updatedData.getDataNascimento(), response.getDataNascimento());
        assertEquals(updatedData.getEndereco(), response.getEndereco());
        assertEquals(updatedData.getCep(), response.getCep());
        assertSame(updatedData.getCargo(), response.getCargo());
        assertFalse(response.isAtivo());
        assertNull(response.getUser());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).findFuncionarioByCpf(updatedData.getCpf());
        Mockito.verify(repository).findFuncionarioByEmail(updatedData.getEmail());
        Mockito.verify(repository, Mockito.never()).findFuncionarioByUser(Mockito.any());
        Mockito.verify(repository).save(existing);
    }

    @Test
    void shouldUpdateFuncionarioWhenUniqueDataBelongsToSameFuncionario()
    {
        User user = new User(1L, "pedro", "senha123", UserRoles.USER);
        Cargo updatedCargo = CargoTestBuilder.newCargo().setId(2L).setNome("Fisioterapeuta").build();
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setAtivo(false).setUser(user).build();
        Funcionario updatedData = FuncionarioTestBuilder.newFuncionario().setNome("Pedro Moura Atualizado").setCpf(existing.getCpf()).setEmail(existing.getEmail()).setDataNascimento(LocalDate.of(1991, 5, 20)).setEndereco("Avenida Paulista, 1000 - São Paulo").setCep("01310-100").setCargo(updatedCargo).setUser(user).buildForCreate();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(repository.findFuncionarioByCpf(updatedData.getCpf())).thenReturn(Optional.of(existing));
        Mockito.when(repository.findFuncionarioByEmail(updatedData.getEmail())).thenReturn(Optional.of(existing));
        Mockito.when(repository.save(existing)).thenReturn(existing);

        Funcionario response = service.updateFuncionario(1L, updatedData);

        assertSame(existing, response);
        assertEquals(1L, response.getId());
        assertEquals(updatedData.getNome(), response.getNome());
        assertEquals(updatedData.getDataNascimento(), response.getDataNascimento());
        assertEquals(updatedData.getEndereco(), response.getEndereco());
        assertEquals(updatedData.getCep(), response.getCep());
        assertSame(updatedCargo, response.getCargo());
        assertSame(user, response.getUser());
        assertFalse(response.isAtivo());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).findFuncionarioByCpf(updatedData.getCpf());
        Mockito.verify(repository).findFuncionarioByEmail(updatedData.getEmail());
        Mockito.verify(repository).save(existing);
    }

    @Test
    void shouldThrowFuncionarioNotFoundExceptionWhenUpdatingNonexistentFuncionario()
    {
        User user = new User(1L, "pedro", "senha123", UserRoles.USER);
        Cargo updatedCargo = CargoTestBuilder.newCargo().setId(2L).setNome("Fisioterapeuta").build();
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setAtivo(false).setUser(user).build();
        Funcionario updatedData = FuncionarioTestBuilder.newFuncionario().setNome("Pedro Moura Atualizado").setCpf(existing.getCpf()).setEmail(existing.getEmail()).setDataNascimento(LocalDate.of(1991, 5, 20)).setEndereco("Avenida Paulista, 1000 - São Paulo").setCep("01310-100").setCargo(updatedCargo).setUser(user).buildForCreate();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.empty());

        FuncionarioNotFoundException exception = assertThrows(FuncionarioNotFoundException.class, () -> service.updateFuncionario(1L, updatedData));

        assertEquals("Funcionário não encontrado.", exception.getMessage());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository, Mockito.never()).findFuncionarioByCpf(Mockito.any());
        Mockito.verify(repository, Mockito.never()).findFuncionarioByEmail(Mockito.any());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void shouldRejectUpdateWhenCpfBelongsToAnotherFuncionario()
    {
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).build();
        Funcionario another =  FuncionarioTestBuilder.newFuncionario().setId(2L).setCpf("11144477735").build();
        Funcionario updated =  FuncionarioTestBuilder.newFuncionario().setCpf("11144477735").buildForCreate();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(repository.findFuncionarioByCpf(updated.getCpf())).thenReturn(Optional.of(another));

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> service.updateFuncionario(1L, updated));

        assertEquals("CPF já cadastrado.",  exception.getMessage());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).findFuncionarioByCpf(updated.getCpf());
        Mockito.verify(repository, Mockito.never()).findFuncionarioByEmail(Mockito.any());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void shouldRejectUpdateWhenEmailBelongsToAnotherFuncionario()
    {
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).build();
        Funcionario another =  FuncionarioTestBuilder.newFuncionario().setId(2L).setEmail("outro@email.com").build();
        Funcionario updated =  FuncionarioTestBuilder.newFuncionario().setEmail("outro@email.com").buildForCreate();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(repository.findFuncionarioByCpf(updated.getCpf())).thenReturn(Optional.empty());
        Mockito.when(repository.findFuncionarioByEmail(updated.getEmail())).thenReturn(Optional.of(another));

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> service.updateFuncionario(1L, updated));

        assertEquals("E-mail já cadastrado.", exception.getMessage());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).findFuncionarioByCpf(updated.getCpf());
        Mockito.verify(repository).findFuncionarioByEmail(updated.getEmail());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void shouldPreserveExistingUserWhenUpdatingFuncionario()
    {
        User existingUser = new User(1L, "pedro", "senha123", UserRoles.USER);
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setAtivo(false).setUser(existingUser).build();
        Funcionario updatedData = FuncionarioTestBuilder.newFuncionario().setNome("Pedro Moura Atualizado").setCpf("12345678909").setEmail("pedro.atualizado@email.com").setUser(null).buildForCreate();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(repository.findFuncionarioByCpf(updatedData.getCpf())).thenReturn(Optional.empty());
        Mockito.when(repository.findFuncionarioByEmail(updatedData.getEmail())).thenReturn(Optional.empty());
        Mockito.when(repository.save(existing)).thenReturn(existing);

        Funcionario response = service.updateFuncionario(1L, updatedData);

        assertSame(existing, response);
        assertSame(existingUser, response.getUser());
        assertEquals(updatedData.getNome(), response.getNome());
        assertEquals(updatedData.getCpf(), response.getCpf());
        assertEquals(updatedData.getEmail(), response.getEmail());
        assertFalse(response.isAtivo());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).findFuncionarioByCpf(updatedData.getCpf());
        Mockito.verify(repository).findFuncionarioByEmail(updatedData.getEmail());
        Mockito.verify(repository).save(existing);
    }

    @Test
    void shouldPreserveExistingUserWhenUpdateContainsDifferentUser()
    {
        User existingUser = new User(1L, "pedro", "senha123", UserRoles.USER);
        User receivedUser = new User(2L, "maria", "senha456", UserRoles.USER);

        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(existingUser).build();
        Funcionario updatedData = FuncionarioTestBuilder.newFuncionario().setNome("Pedro Moura Atualizado").setCpf("12345678909").setEmail("pedro.atualizado@email.com").setUser(receivedUser).buildForCreate();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(repository.findFuncionarioByCpf(updatedData.getCpf())).thenReturn(Optional.empty());
        Mockito.when(repository.findFuncionarioByEmail(updatedData.getEmail())).thenReturn(Optional.empty());
        Mockito.when(repository.save(existing)).thenReturn(existing);

        Funcionario response = service.updateFuncionario(1L, updatedData);

        assertSame(existing, response);
        assertSame(existingUser, response.getUser());
        assertNotSame(receivedUser, response.getUser());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).findFuncionarioByCpf(updatedData.getCpf());
        Mockito.verify(repository).findFuncionarioByEmail(updatedData.getEmail());
        Mockito.verify(repository, Mockito.never()).findFuncionarioByUser(Mockito.any());
        Mockito.verify(repository).save(existing);
    }

    @Test
    void shouldNotLinkUserWhenUpdatingFuncionarioWithoutUser()
    {
        User receivedUser = new User(2L, "maria", "senha456", UserRoles.USER);
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(null).build();
        Funcionario updatedData = FuncionarioTestBuilder.newFuncionario().setNome("Pedro Moura Atualizado").setCpf("12345678909").setEmail("pedro.atualizado@email.com").setUser(receivedUser).buildForCreate();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(repository.findFuncionarioByCpf(updatedData.getCpf())).thenReturn(Optional.empty());
        Mockito.when(repository.findFuncionarioByEmail(updatedData.getEmail())).thenReturn(Optional.empty());
        Mockito.when(repository.save(existing)).thenReturn(existing);

        Funcionario response = service.updateFuncionario(1L, updatedData);

        assertSame(existing, response);
        assertNull(response.getUser());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).findFuncionarioByCpf(updatedData.getCpf());
        Mockito.verify(repository).findFuncionarioByEmail(updatedData.getEmail());
        Mockito.verify(repository, Mockito.never()).findFuncionarioByUser(Mockito.any());
        Mockito.verify(repository).save(existing);
    }

    @Test
    void shouldPreserveEmploymentDatesWhenUpdatingFuncionario()
    {
        LocalDate hoje = LocalDate.now();
        LocalDate dataAdmissaoExistente = hoje.minusYears(2);
        LocalDate dataDemissaoExistente = hoje.minusDays(2);
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setAtivo(false).setDataAdmissao(dataAdmissaoExistente).setDataDemissao(dataDemissaoExistente).build();
        Funcionario updatedData = FuncionarioTestBuilder.newFuncionario().setNome("Pedro Moura Atualizado").setCpf("12345678909").setEmail("pedro.atualizado@email.com").setDataAdmissao(hoje.minusYears(1)).setDataDemissao(hoje.minusDays(1)).buildForCreate();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(repository.findFuncionarioByCpf(updatedData.getCpf())).thenReturn(Optional.empty());
        Mockito.when(repository.findFuncionarioByEmail(updatedData.getEmail())).thenReturn(Optional.empty());
        Mockito.when(repository.save(existing)).thenReturn(existing);

        Funcionario response = service.updateFuncionario(1L, updatedData);

        assertSame(existing, response);
        assertEquals(dataAdmissaoExistente, response.getDataAdmissao());
        assertEquals(dataDemissaoExistente, response.getDataDemissao());
        assertFalse(response.isAtivo());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).findFuncionarioByCpf(updatedData.getCpf());
        Mockito.verify(repository).findFuncionarioByEmail(updatedData.getEmail());
        Mockito.verify(repository).save(existing);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldThrowFuncionarioNotFoundExceptionWhenUpdatingWithInvalidId(Long id)
    {
        Funcionario updatedData = FuncionarioTestBuilder.newFuncionario().setNome("Pedro Moura Atualizado").setCpf("12345678909").setEmail("pedro.atualizado@email.com").buildForCreate();

        FuncionarioNotFoundException exception = assertThrows(FuncionarioNotFoundException.class, () -> service.updateFuncionario(id,updatedData));

        assertEquals("Funcionário não encontrado.", exception.getMessage());
        Mockito.verifyNoInteractions(repository);
    }

    @Test
    void shouldDismissFuncionarioSuccessfully()
    {
        LocalDate hoje = LocalDate.now();
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setDataAdmissao(hoje.minusYears(1)).setAtivo(true).build();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));

        service.dismissFuncionarioById(1L, hoje);

        assertFalse(existing.isAtivo());
        assertEquals(hoje, existing.getDataDemissao());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).save(existing);
    }

    @Test
    void shouldUpdateDismissalDateWhenFuncionarioIsAlreadyInactive()
    {
        LocalDate hoje = LocalDate.now();
        LocalDate primeiraDemissao = hoje.minusDays(2);
        LocalDate novaDemissao = hoje.minusDays(1);
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setDataAdmissao(hoje.minusYears(1)).setAtivo(false).setDataDemissao(primeiraDemissao).build();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));

        service.dismissFuncionarioById(1L, novaDemissao);

        assertFalse(existing.isAtivo());
        assertEquals(novaDemissao, existing.getDataDemissao());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).save(existing);
    }

    @Test
    void shouldNotSaveFuncionarioWhenDismissalDateIsNull()
    {
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setAtivo(true).build();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> service.dismissFuncionarioById(1L, null));

        assertEquals("Data de demissão deve ser informada.", exception.getMessage());
        assertTrue(existing.isAtivo());
        assertNull(existing.getDataDemissao());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void shouldNotSaveFuncionarioWhenDismissalDateIsInvalid()
    {
        LocalDate hoje = LocalDate.now();
        LocalDate dataDemissaoFutura = hoje.plusDays(1);
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setDataAdmissao(hoje.minusYears(1)).setAtivo(true).build();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> service.dismissFuncionarioById(1L, dataDemissaoFutura));

        assertEquals("Data de demissão não pode ser futura.", exception.getMessage());
        assertTrue(existing.isAtivo());
        assertNull(existing.getDataDemissao());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void shouldThrowFuncionarioNotFoundExceptionWhenDismissingNonexistentFuncionario()
    {
        LocalDate dataDemissao = LocalDate.now();
        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.empty());

        FuncionarioNotFoundException exception = assertThrows(FuncionarioNotFoundException.class, () -> service.dismissFuncionarioById(1L, dataDemissao));

        assertEquals("Funcionário não encontrado.",  exception.getMessage());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldThrowFuncionarioNotFoundExceptionWhenDismissingWithInvalidId(Long id)
    {
        FuncionarioNotFoundException exception = assertThrows(FuncionarioNotFoundException.class, () -> service.dismissFuncionarioById(id, LocalDate.now()));

        assertEquals("Funcionário não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

    @Test
    void shouldReactivateFuncionarioSuccessfully()
    {
        LocalDate hoje = LocalDate.now();
        LocalDate dataAdmissao = hoje.minusYears(1);
        LocalDate dataDemissao = hoje.minusDays(1);

        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setDataAdmissao(dataAdmissao).setDataDemissao(dataDemissao).setAtivo(false).build();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));

        service.reactivateFuncionarioById(1L);

        assertTrue(existing.isAtivo());
        assertNull(existing.getDataDemissao());
        assertEquals(dataAdmissao, existing.getDataAdmissao());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).save(existing);
    }

    @Test
    void shouldNotSaveWhenFuncionarioIsAlreadyActive()
    {
        Funcionario existed =  FuncionarioTestBuilder.newFuncionario().setId(1L).setAtivo(true).build();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existed));

        service.reactivateFuncionarioById(1L);
        assertTrue(existed.isAtivo());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void shouldThrowFuncionarioNotFoundExceptionWhenReactivatingNonexistentFuncionario()
    {
        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.empty());

        FuncionarioNotFoundException exception = assertThrows(FuncionarioNotFoundException.class, () -> service.reactivateFuncionarioById(1L));

        assertEquals("Funcionário não encontrado.", exception.getMessage());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldThrowFuncionarioNotFoundExceptionWhenReactivatingWithInvalidId(Long id)
    {
        FuncionarioNotFoundException exception = assertThrows(FuncionarioNotFoundException.class, () -> service.reactivateFuncionarioById(id));

        assertEquals("Funcionário não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

    @Test
    void shouldLinkUserToFuncionarioSuccessfully()
    {
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(null).build();
        User receivedUser = new User(2L, "maria", "senha456", UserRoles.USER);

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(repository.findFuncionarioByUser(receivedUser)).thenReturn(Optional.empty());

        service.linkUserToFuncionario(1L, receivedUser);

        assertSame(receivedUser, existing.getUser());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).findFuncionarioByUser(receivedUser);
        Mockito.verify(repository).save(existing);
    }

    @Test
    void shouldReplaceExistingUserWhenLinkingDifferentUser()
    {
        User existingUser = new User(1L, "pedro", "senha123", UserRoles.USER);
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(existingUser).build();
        User receivedUser = new User(2L, "maria", "senha456", UserRoles.USER);

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(repository.findFuncionarioByUser(receivedUser)).thenReturn(Optional.empty());

        service.linkUserToFuncionario(1L, receivedUser);

        assertSame(receivedUser, existing.getUser());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).findFuncionarioByUser(receivedUser);
        Mockito.verify(repository).save(existing);
    }

    @Test
    void shouldNotSaveWhenSameUserIsAlreadyLinkedToFuncionario()
    {
        User existingUser = new User(1L, "pedro", "senha123", UserRoles.USER);
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(existingUser).build();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));

        service.linkUserToFuncionario(1L, existingUser);

        assertSame(existingUser, existing.getUser());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository, Mockito.never()).findFuncionarioByUser(Mockito.any());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void shouldRejectUserAlreadyLinkedToAnotherFuncionarioWhenLinking()
    {
        User existingUser = new User(1L, "pedro", "senha123", UserRoles.USER);
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(null).build();
        Funcionario found = FuncionarioTestBuilder.newFuncionario().setId(2L).setUser(existingUser).build();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(repository.findFuncionarioByUser(existingUser)).thenReturn(Optional.of(found));

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> service.linkUserToFuncionario(1L, existingUser));

        assertNull(existing.getUser());
        assertEquals( "Usuário já vinculado a outro funcionário.", exception.getMessage());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).findFuncionarioByUser(existingUser);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void shouldRejectNullUserWhenLinkingToFuncionario()
    {
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> service.linkUserToFuncionario(1L, null));

        assertEquals("Usuário inválido.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

    @Test
    void shouldThrowFuncionarioNotFoundExceptionWhenLinkingUserToNonexistentFuncionario()
    {
        User existingUser = new User(1L, "pedro", "senha123", UserRoles.USER);

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.empty());

        FuncionarioNotFoundException exception = assertThrows(FuncionarioNotFoundException.class, () -> service.linkUserToFuncionario(1L, existingUser));

        assertEquals("Funcionário não encontrado.", exception.getMessage());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository, Mockito.never()).findFuncionarioByUser(Mockito.any());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldThrowFuncionarioNotFoundExceptionWhenLinkingUserWithInvalidFuncionarioId(Long id)
    {
        User existingUser = new User(1L, "pedro", "senha123", UserRoles.USER);

        FuncionarioNotFoundException exception = assertThrows(FuncionarioNotFoundException.class, () -> service.linkUserToFuncionario(id, existingUser));

        assertEquals("Funcionário não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

    @Test
    void shouldUnlinkUserFromFuncionarioSuccessfully()
    {
        User existingUser = new User(1L, "pedro", "senha123", UserRoles.USER);
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(existingUser).build();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));

        service.unlinkUserFromFuncionario(1L);

        assertNull(existing.getUser());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).save(existing);
    }

    @Test
    void shouldNotSaveWhenFuncionarioHasNoUserToUnlink()
    {
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(null).build();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));

        service.unlinkUserFromFuncionario(1L);
        assertNull(existing.getUser());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void shouldThrowFuncionarioNotFoundExceptionWhenUnlinkingUserFromNonexistentFuncionario()
    {
        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.empty());

        FuncionarioNotFoundException exception = assertThrows(FuncionarioNotFoundException.class, () -> service.unlinkUserFromFuncionario(1L));

        assertEquals("Funcionário não encontrado.", exception.getMessage());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldThrowFuncionarioNotFoundExceptionWhenUnlinkingUserWithInvalidFuncionarioId(Long id)
    {
        FuncionarioNotFoundException exception = assertThrows(FuncionarioNotFoundException.class, () -> service.unlinkUserFromFuncionario(id));

        assertEquals("Funcionário não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

    @Test
    void shouldReactivateFuncionarioSuccessfullyWhenReactivating()
    {
        LocalDate hoje = LocalDate.now();
        LocalDate dataAdmissao = hoje.minusYears(1);
        LocalDate dataDemissao = hoje.minusDays(1);

        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setDataAdmissao(dataAdmissao).setDataDemissao(dataDemissao).setAtivo(false).build();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));

        service.reactivateFuncionarioById(1L);

        assertTrue(existing.isAtivo());
        assertNull(existing.getDataDemissao());
        assertEquals(dataAdmissao, existing.getDataAdmissao());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).save(existing);
    }

}
