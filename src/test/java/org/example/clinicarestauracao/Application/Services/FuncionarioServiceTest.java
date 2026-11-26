package org.example.clinicarestauracao.Application.Services;


import org.junit.jupiter.api.DisplayName;
import org.example.clinicarestauracao.Application.Exceptions.Funcionario.FuncionarioNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Funcionario.FuncionarioWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Exceptions.User.UserWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Exceptions.User.UsernameAlredyInUseException;
import org.example.clinicarestauracao.Application.Interfaces.FuncionarioRepository;
import org.example.clinicarestauracao.Builders.CargoTestBuilder;
import org.example.clinicarestauracao.Builders.EnderecoTestBuilder;
import org.example.clinicarestauracao.Builders.FuncionarioTestBuilder;
import org.example.clinicarestauracao.Infrastruct.Database.Audit.DatabaseAuditContext;
import org.example.clinicarestauracao.Domain.Entities.Cargo;
import org.example.clinicarestauracao.Domain.Entities.Funcionario;
import org.example.clinicarestauracao.Domain.Entities.User;
import org.example.clinicarestauracao.Domain.Enums.UserRoles;
import org.example.clinicarestauracao.Domain.ValueObjects.Endereco;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
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
    @Mock
    private UserService userService;
    @Mock
    private DatabaseAuditContext auditContext;
    @InjectMocks
    private FuncionarioService service;

    @DisplayName("Deve rejeitar funcionário sem usuário")
    @Test
    void shouldRejectFuncionarioWithoutUser()
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().buildForCreateWithNullUser();

        Mockito.when(repository.findFuncionarioByCpf(funcionario.getCpf())).thenReturn(Optional.empty());
        Mockito.when(repository.findFuncionarioByEmail(funcionario.getEmail())).thenReturn(Optional.empty());

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> service.createFuncionario(funcionario));

        assertEquals("Funcionário deve possuir um usuário.", exception.getMessage());

        Mockito.verifyNoInteractions(userService);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @DisplayName("Deve criar funcionário e usuário com sucesso")
    @Test
    void shouldCreateFuncionarioAndUserSuccessfully()
    {
        User receivedUser = new User("pedro", "senha123", UserRoles.ADMIN);
        User savedUser = new User(1L, "pedro", "senha-criptografada", UserRoles.ADMIN);
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setUser(receivedUser).buildForCreate();
        Funcionario savedFuncionario = FuncionarioTestBuilder.newFuncionario().setUser(savedUser).build();

        Mockito.when(repository.findFuncionarioByCpf(funcionario.getCpf())).thenReturn(Optional.empty());
        Mockito.when(repository.findFuncionarioByEmail(funcionario.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userService.registerUser(Mockito.any(User.class))).thenReturn(savedUser);
        Mockito.when(repository.save(funcionario)).thenReturn(savedFuncionario);

        Funcionario result = service.createFuncionario(funcionario);

        assertSame(savedFuncionario, result);
        assertSame(savedUser, funcionario.getUser());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        Mockito.verify(userService).registerUser(userCaptor.capture());

        User userSentToRegistration = userCaptor.getValue();

        assertEquals("pedro", userSentToRegistration.getUsername());
        assertEquals("senha123", userSentToRegistration.getPassword());
        assertEquals(UserRoles.ADMIN, userSentToRegistration.getRole());

        Mockito.verify(repository).save(funcionario);
    }


    @DisplayName("Deve rejeitar funcionário com duplicado CPF")
    @Test
    void shouldRejectFuncionarioWithDuplicatedCpf()
    {
        Funcionario entrada = FuncionarioTestBuilder.newFuncionario().buildForCreate();
        Funcionario existente = FuncionarioTestBuilder.newFuncionario().build();

        Mockito.when(repository.findFuncionarioByCpf(entrada.getCpf())).thenReturn(Optional.of(existente));

        var exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> service.createFuncionario(entrada));

        assertEquals("CPF já cadastrado.", exception.getMessage());

        Mockito.verify(repository, Mockito.never()).findFuncionarioByEmail(Mockito.anyString());
        Mockito.verifyNoInteractions(userService);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @DisplayName("Deve rejeitar funcionário com duplicado e-mail")
    @Test
    void shouldRejectFuncionarioWithDuplicatedEmail()
    {
        Funcionario entrada = FuncionarioTestBuilder.newFuncionario().buildForCreate();
        Funcionario existente = FuncionarioTestBuilder.newFuncionario().build();

        Mockito.when(repository.findFuncionarioByCpf(entrada.getCpf())).thenReturn(Optional.empty());
        Mockito.when(repository.findFuncionarioByEmail(entrada.getEmail())).thenReturn(Optional.of(existente));

        var exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> service.createFuncionario(entrada));

        assertEquals("E-mail já cadastrado.", exception.getMessage());

        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
        Mockito.verifyNoInteractions(userService);
    }

    @DisplayName("Deve buscar funcionário por ID com sucesso")
    @Test
    void shouldFindFuncionarioByIdSuccessfully()
    {
        Funcionario func = FuncionarioTestBuilder.newFuncionario().build();

        Mockito.when(repository.findFuncionarioById(func.getId())).thenReturn(Optional.of(func));

        Funcionario response = service.findFuncionarioById(func.getId());

        assertSame(func, response);

        Mockito.verify(repository).findFuncionarioById(func.getId());
    }

    @DisplayName("Deve lançar exceção quando o funcionário não for encontrado quando funcionário não existir")
    @Test
    void shouldThrowFuncionarioNotFoundExceptionWhenFuncionarioDoesNotExist()
    {
        Long id = 1L;
        Mockito.when(repository.findFuncionarioById(id)).thenReturn(Optional.empty());

        var exception = assertThrows(FuncionarioNotFoundException.class, () -> service.findFuncionarioById(id));

        assertEquals("Funcionário não encontrado.", exception.getMessage());

        Mockito.verify(repository).findFuncionarioById(id);
    }

    @DisplayName("Deve lançar exceção quando o funcionário não for encontrado quando funcionário ID é inválido")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldThrowFuncionarioNotFoundExceptionWhenFuncionarioIdIsInvalid(Long id)
    {
        FuncionarioNotFoundException exception = assertThrows(FuncionarioNotFoundException.class, () -> service.findFuncionarioById(id));

        assertEquals("Funcionário não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

    @DisplayName("Deve buscar funcionário por e-mail com sucesso")
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

    @DisplayName("Deve lançar exceção quando o funcionário não for encontrado quando e-mail não existir")
    @Test
    void shouldThrowFuncionarioNotFoundExceptionWhenEmailDoesNotExist()
    {
        String entryEmail = "pedro@email.com";
        Mockito.when(repository.findFuncionarioByEmail(entryEmail)).thenReturn(Optional.empty());

        var exception = assertThrows(FuncionarioNotFoundException.class, () -> service.findFuncionarioByEmail(entryEmail));

        assertEquals("Funcionário não encontrado.", exception.getMessage());

        Mockito.verify(repository).findFuncionarioByEmail(entryEmail);
    }

    @DisplayName("Deve lançar exceção quando o funcionário não for encontrado quando e-mail é inválido")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "    "})
    void shouldThrowFuncionarioNotFoundExceptionWhenEmailIsInvalid(String email)
    {
        FuncionarioNotFoundException exception = assertThrows(FuncionarioNotFoundException.class, () -> service.findFuncionarioByEmail(email));

        assertEquals("Funcionário não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

    @DisplayName("Deve buscar funcionário por CPF com sucesso")
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

    @DisplayName("Deve lançar exceção quando o funcionário não for encontrado quando CPF não existir")
    @Test
    void shouldThrowFuncionarioNotFoundExceptionWhenCpfDoesNotExist()
    {
        String entryCpf = "52998224725";

        Mockito.when(repository.findFuncionarioByCpf(entryCpf)).thenReturn(Optional.empty());

        FuncionarioNotFoundException exception = assertThrows(FuncionarioNotFoundException.class, () -> service.findFuncionarioByCpf(entryCpf));

        assertEquals("Funcionário não encontrado.", exception.getMessage());

        Mockito.verify(repository).findFuncionarioByCpf(entryCpf);
    }

    @DisplayName("Deve lançar exceção quando o funcionário não for encontrado quando CPF é inválido")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", " . - "})
    void shouldThrowFuncionarioNotFoundExceptionWhenCpfIsInvalid(String cpf)
    {
        FuncionarioNotFoundException exception = assertThrows(FuncionarioNotFoundException.class, () -> service.findFuncionarioByCpf(cpf));

        assertEquals("Funcionário não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

    @DisplayName("Deve buscar todos os funcionários com sucesso")
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

    @DisplayName("Deve retornar uma lista vazia quando nenhum funcionários existir")
    @Test
    void shouldReturnEmptyListWhenNoFuncionariosExist()
    {
        Mockito.when(repository.findAll()).thenReturn(List.of());

        List<Funcionario> response = service.findAllFuncionarios();

        assertNotNull(response);
        assertTrue(response.isEmpty());

        Mockito.verify(repository).findAll();
    }

    @DisplayName("Deve rejeitar a atualização quando funcionário tem nenhum usuário")
    @Test
    void shouldRejectUpdateWhenFuncionarioHasNoUser()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().build();
        Cargo updatedCargo = CargoTestBuilder.newCargo().setId(2L).setNome("Fisioterapeuta").build();
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setAtivo(false).buildWithNullUser();
        Funcionario updatedData = FuncionarioTestBuilder.newFuncionario().setNome("Pedro Moura Atualizado").setCpf("12345678909").setEmail("pedro.atualizado@email.com").setDataNascimento(LocalDate.of(1991, 5, 20)).setEndereco(endereco).setCep("01310-100").setCargo(updatedCargo).buildForCreateWithNullUser();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));
        FuncionarioWithInvalidInformationException exception = assertThrows(
                FuncionarioWithInvalidInformationException.class,
                () -> service.updateFuncionario(1L, updatedData)
        );

        assertEquals("Funcionário não possui usuário vinculado.", exception.getMessage());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verifyNoInteractions(userService);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @DisplayName("Deve atualizar funcionário quando único data pertence para mesmo funcionário")
    @Test
    void shouldUpdateFuncionarioWhenUniqueDataBelongsToSameFuncionario()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().build();
        User user = new User(1L, "pedro", "senha123", UserRoles.USER);
        Cargo updatedCargo = CargoTestBuilder.newCargo().setId(2L).setNome("Fisioterapeuta").build();
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setAtivo(false).setUser(user).build();
        Funcionario updatedData = FuncionarioTestBuilder.newFuncionario().setNome("Pedro Moura Atualizado").setCpf(existing.getCpf()).setEmail(existing.getEmail()).setDataNascimento(LocalDate.of(1991, 5, 20)).setEndereco(endereco).setCep("01310-100").setTelefone("(21) 98888-7777").setCargo(updatedCargo).setUser(User.forUsernameUpdate("pedro")).buildForCreate();

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
        assertEquals("21988887777", response.getTelefone());
        assertSame(updatedCargo, response.getCargo());
        assertSame(user, response.getUser());
        assertFalse(response.isAtivo());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).findFuncionarioByCpf(updatedData.getCpf());
        Mockito.verify(repository).findFuncionarioByEmail(updatedData.getEmail());
        Mockito.verify(userService).updateUsername(user.getId(), "pedro");
        Mockito.verify(repository).save(existing);
    }

    @DisplayName("Deve preservar existente senha quando atualizando funcionário")
    @Test
    void shouldPreserveExistingPasswordWhenUpdatingFuncionario()
    {
        User existingUser = new User(1L, "pedro", "senha-antiga", UserRoles.USER);
        User receivedUser = new User("pedro.atualizado", "nova-senha", UserRoles.USER);
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(existingUser).build();
        Funcionario updatedData = FuncionarioTestBuilder.newFuncionario().setUser(receivedUser).buildForCreate();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(repository.findFuncionarioByCpf(updatedData.getCpf())).thenReturn(Optional.of(existing));
        Mockito.when(repository.findFuncionarioByEmail(updatedData.getEmail())).thenReturn(Optional.of(existing));
        Mockito.when(repository.save(existing)).thenReturn(existing);

        Funcionario response = service.updateFuncionario(1L, updatedData);

        assertSame(existing, response);
        assertSame(existingUser, response.getUser());
        assertEquals("senha-antiga", existingUser.getPassword());
        Mockito.verify(userService).updateUsername(existingUser.getId(), "pedro.atualizado");
        Mockito.verifyNoMoreInteractions(userService);
        Mockito.verify(repository).save(existing);
    }

    @DisplayName("Deve rejeitar a atualização quando usuário data é ausente")
    @Test
    void shouldRejectUpdateWhenUserDataIsMissing()
    {
        User user = new User(1L, "pedro", "senha123", UserRoles.USER);
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(user).build();
        Funcionario updatedData = FuncionarioTestBuilder.newFuncionario().buildForCreateWithNullUser();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));

        FuncionarioWithInvalidInformationException exception = assertThrows(
                FuncionarioWithInvalidInformationException.class,
                () -> service.updateFuncionario(1L, updatedData)
        );

        assertEquals("Dados do usuário devem ser informados.", exception.getMessage());
        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository, Mockito.never()).findFuncionarioByCpf(Mockito.any());
        Mockito.verify(repository, Mockito.never()).findFuncionarioByEmail(Mockito.any());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
        Mockito.verifyNoInteractions(userService);
    }

    @DisplayName("Não deve salvar funcionário quando atualizando nome de usuário falha")
    @Test
    void shouldNotSaveFuncionarioWhenUpdatingUsernameFails()
    {
        User user = new User(1L, "pedro", "senha123", UserRoles.USER);
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(user).build();
        Funcionario updatedData = FuncionarioTestBuilder.newFuncionario().setUser(User.forUsernameUpdate("username.existente")).buildForCreate();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(repository.findFuncionarioByCpf(updatedData.getCpf())).thenReturn(Optional.of(existing));
        Mockito.when(repository.findFuncionarioByEmail(updatedData.getEmail())).thenReturn(Optional.of(existing));
        Mockito.doThrow(new UsernameAlredyInUseException("Nome de usuário já existe."))
                .when(userService).updateUsername(user.getId(), "username.existente");

        UsernameAlredyInUseException exception = assertThrows(
                UsernameAlredyInUseException.class,
                () -> service.updateFuncionario(1L, updatedData)
        );

        assertEquals("Nome de usuário já existe.", exception.getMessage());
        Mockito.verify(userService).updateUsername(user.getId(), "username.existente");
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @DisplayName("Deve lançar exceção quando o funcionário não for encontrado quando atualizando inexistente funcionário")
    @Test
    void shouldThrowFuncionarioNotFoundExceptionWhenUpdatingNonexistentFuncionario()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().build();
        User user = new User(1L, "pedro", "senha123", UserRoles.USER);
        Cargo updatedCargo = CargoTestBuilder.newCargo().setId(2L).setNome("Fisioterapeuta").build();
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setAtivo(false).setUser(user).build();
        Funcionario updatedData = FuncionarioTestBuilder.newFuncionario().setNome("Pedro Moura Atualizado").setCpf(existing.getCpf()).setEmail(existing.getEmail()).setDataNascimento(LocalDate.of(1991, 5, 20)).setEndereco(endereco).setCep("01310-100").setCargo(updatedCargo).setUser(user).buildForCreate();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.empty());

        FuncionarioNotFoundException exception = assertThrows(FuncionarioNotFoundException.class, () -> service.updateFuncionario(1L, updatedData));

        assertEquals("Funcionário não encontrado.", exception.getMessage());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository, Mockito.never()).findFuncionarioByCpf(Mockito.any());
        Mockito.verify(repository, Mockito.never()).findFuncionarioByEmail(Mockito.any());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @DisplayName("Deve rejeitar a atualização quando CPF pertence para outro funcionário")
    @Test
    void shouldRejectUpdateWhenCpfBelongsToAnotherFuncionario()
    {
        User user = new User(1L, "pedro", "senha123", UserRoles.USER);
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(user).build();
        Funcionario another =  FuncionarioTestBuilder.newFuncionario().setId(2L).setCpf("11144477735").build();
        Funcionario updated =  FuncionarioTestBuilder.newFuncionario().setCpf("11144477735").setUser(User.forUsernameUpdate("pedro")).buildForCreate();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(repository.findFuncionarioByCpf(updated.getCpf())).thenReturn(Optional.of(another));

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> service.updateFuncionario(1L, updated));

        assertEquals("CPF já cadastrado.",  exception.getMessage());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).findFuncionarioByCpf(updated.getCpf());
        Mockito.verify(repository, Mockito.never()).findFuncionarioByEmail(Mockito.any());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @DisplayName("Deve rejeitar a atualização quando e-mail pertence para outro funcionário")
    @Test
    void shouldRejectUpdateWhenEmailBelongsToAnotherFuncionario()
    {
        User user = new User(1L, "pedro", "senha123", UserRoles.USER);
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(user).build();
        Funcionario another =  FuncionarioTestBuilder.newFuncionario().setId(2L).setEmail("outro@email.com").build();
        Funcionario updated =  FuncionarioTestBuilder.newFuncionario().setEmail("outro@email.com").setUser(User.forUsernameUpdate("pedro")).buildForCreate();

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

    @DisplayName("Deve preservar existente usuário quando atualizando funcionário")
    @Test
    void shouldPreserveExistingUserWhenUpdatingFuncionario()
    {
        User existingUser = new User(1L, "pedro", "senha123", UserRoles.USER);
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setAtivo(false).setUser(existingUser).build();
        Funcionario updatedData = FuncionarioTestBuilder.newFuncionario().setNome("Pedro Moura Atualizado").setCpf("12345678909").setEmail("pedro.atualizado@email.com").setUser(User.forUsernameUpdate("pedro.atualizado")).buildForCreate();

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
        Mockito.verify(userService).updateUsername(existingUser.getId(), "pedro.atualizado");
        Mockito.verify(repository).save(existing);
    }

    @DisplayName("Deve preservar existente usuário quando atualizar contém diferente usuário")
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
        assertEquals("senha123", existingUser.getPassword());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).findFuncionarioByCpf(updatedData.getCpf());
        Mockito.verify(repository).findFuncionarioByEmail(updatedData.getEmail());
        Mockito.verify(repository, Mockito.never()).findFuncionarioByUser(Mockito.any());
        Mockito.verify(userService).updateUsername(existingUser.getId(), "maria");
        Mockito.verify(repository).save(existing);
    }

    @DisplayName("Deve rejeitar a atualização quando existente funcionário tem nenhum usuário")
    @Test
    void shouldRejectUpdateWhenExistingFuncionarioHasNoUser()
    {
        User receivedUser = new User(2L, "maria", "senha456", UserRoles.USER);
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).buildWithNullUser();
        Funcionario updatedData = FuncionarioTestBuilder.newFuncionario().setNome("Pedro Moura Atualizado").setCpf("12345678909").setEmail("pedro.atualizado@email.com").setUser(receivedUser).buildForCreate();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));
        FuncionarioWithInvalidInformationException exception = assertThrows(
                FuncionarioWithInvalidInformationException.class,
                () -> service.updateFuncionario(1L, updatedData)
        );

        assertEquals("Funcionário não possui usuário vinculado.", exception.getMessage());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verifyNoInteractions(userService);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @DisplayName("Deve atualizar admissão e preservar demissão quando atualizando funcionário")
    @Test
    void shouldUpdateAdmissionAndPreserveDismissalWhenUpdatingFuncionario()
    {
        LocalDate hoje = LocalDate.now();
        LocalDate dataAdmissaoExistente = hoje.minusYears(2);
        LocalDate dataDemissaoExistente = hoje.minusDays(2);
        User user = new User(1L, "pedro", "senha123", UserRoles.USER);
        user.deactivate();
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setAtivo(false).setUser(user).setDataAdmissao(dataAdmissaoExistente).setDataDemissao(dataDemissaoExistente).build();
        Funcionario updatedData = FuncionarioTestBuilder.newFuncionario().setNome("Pedro Moura Atualizado").setCpf("12345678909").setEmail("pedro.atualizado@email.com").setDataAdmissao(hoje.minusYears(1)).setDataDemissao(hoje.minusDays(1)).setUser(User.forUsernameUpdate("pedro.atualizado")).buildForCreate();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(repository.findFuncionarioByCpf(updatedData.getCpf())).thenReturn(Optional.empty());
        Mockito.when(repository.findFuncionarioByEmail(updatedData.getEmail())).thenReturn(Optional.empty());
        Mockito.when(repository.save(existing)).thenReturn(existing);

        Funcionario response = service.updateFuncionario(1L, updatedData);

        assertSame(existing, response);
        assertEquals(updatedData.getDataAdmissao(), response.getDataAdmissao());
        assertEquals(dataDemissaoExistente, response.getDataDemissao());
        assertFalse(response.isAtivo());
        assertEquals(UserRoles.NO_ACCESS, user.getRole());
        assertEquals(UserRoles.USER, user.getPreviousRole());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).findFuncionarioByCpf(updatedData.getCpf());
        Mockito.verify(repository).findFuncionarioByEmail(updatedData.getEmail());
        Mockito.verify(userService).updateUsername(user.getId(), "pedro.atualizado");
        Mockito.verify(repository).save(existing);
    }

    @DisplayName("Deve rejeitar a atualização quando admissão é não antes demissão")
    @Test
    void shouldRejectUpdateWhenAdmissionIsNotBeforeDismissal()
    {
        LocalDate hoje = LocalDate.now();
        LocalDate dataAdmissaoExistente = hoje.minusYears(2);
        LocalDate dataDemissaoExistente = hoje.minusDays(2);

        User user = new User(1L, "pedro", "senha123", UserRoles.USER);
        user.deactivate();
        Funcionario existing = FuncionarioTestBuilder.newFuncionario()
                .setId(1L)
                .setNome("Pedro Moura")
                .setAtivo(false)
                .setUser(user)
                .setDataAdmissao(dataAdmissaoExistente)
                .setDataDemissao(dataDemissaoExistente)
                .build();

        String telefoneExistente = existing.getTelefone();

        Funcionario updatedData = FuncionarioTestBuilder.newFuncionario()
                .setNome("Pedro Moura Atualizado")
                .setCpf("12345678909")
                .setEmail("pedro.atualizado@email.com")
                .setTelefone("(21) 98888-7777")
                .setDataAdmissao(dataDemissaoExistente)
                .setUser(User.forUsernameUpdate("pedro"))
                .buildForCreate();

        Mockito.when(repository.findFuncionarioById(1L))
                .thenReturn(Optional.of(existing));
        Mockito.when(repository.findFuncionarioByCpf(updatedData.getCpf()))
                .thenReturn(Optional.empty());
        Mockito.when(repository.findFuncionarioByEmail(updatedData.getEmail()))
                .thenReturn(Optional.empty());

        FuncionarioWithInvalidInformationException exception =
                assertThrows(
                        FuncionarioWithInvalidInformationException.class,
                        () -> service.updateFuncionario(1L, updatedData)
                );

        assertEquals(
                "Data de admissão deve ser anterior à data de demissão.",
                exception.getMessage()
        );
        assertEquals("Pedro Moura", existing.getNome());
        assertEquals(dataAdmissaoExistente, existing.getDataAdmissao());
        assertEquals(dataDemissaoExistente, existing.getDataDemissao());
        assertEquals(telefoneExistente, existing.getTelefone());

        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
        Mockito.verifyNoInteractions(userService);
    }

    @DisplayName("Deve lançar exceção quando o funcionário não for encontrado quando atualizando com ID inválido")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldThrowFuncionarioNotFoundExceptionWhenUpdatingWithInvalidId(Long id)
    {
        Funcionario updatedData = FuncionarioTestBuilder.newFuncionario().setNome("Pedro Moura Atualizado").setCpf("12345678909").setEmail("pedro.atualizado@email.com").buildForCreate();

        FuncionarioNotFoundException exception = assertThrows(FuncionarioNotFoundException.class, () -> service.updateFuncionario(id, updatedData));

        assertEquals("Funcionário não encontrado.", exception.getMessage());
        Mockito.verifyNoInteractions(repository);
    }

    @DisplayName("Deve demitir funcionário e desativar usuário")
    @ParameterizedTest
    @EnumSource(
            value = UserRoles.class,
            names = {"USER", "ADMIN"}
    )
    void shouldDismissFuncionarioAndDeactivateUser(UserRoles originalRole)
    {
        LocalDate hoje = LocalDate.now();
        User user = new User(1L, "Pedro", "senha123", originalRole);

        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(user).setDataAdmissao(hoje.minusYears(1)).setAtivo(true).build();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));

        service.dismissFuncionarioById(1L, hoje);

        assertFalse(existing.isAtivo());
        assertEquals(hoje, existing.getDataDemissao());
        assertEquals(UserRoles.NO_ACCESS, user.getRole());
        assertEquals(originalRole, user.getPreviousRole());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).save(existing);
    }


    @DisplayName("Deve atualizar a data de demissão mesmo quando o funcionário já está inativo")
    @Test
    void shouldUpdateDismissalDateWhenFuncionarioIsAlreadyInactive()
    {
        User user = new User(1L, "Pedro", "senha123", UserRoles.ADMIN);
        user.deactivate();
        LocalDate hoje = LocalDate.now();
        LocalDate primeiraDemissao = hoje.minusDays(2);
        LocalDate novaDemissao = hoje.minusDays(1);
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setDataAdmissao(hoje.minusYears(1)).setAtivo(false).setDataDemissao(primeiraDemissao).setUser(user).build();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));

        service.dismissFuncionarioById(1L, novaDemissao);

        assertFalse(existing.isAtivo());
        assertEquals(novaDemissao, existing.getDataDemissao());
        assertEquals(UserRoles.NO_ACCESS, user.getRole());
        assertEquals(UserRoles.ADMIN, user.getPreviousRole());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).save(existing);
    }

    @DisplayName("Não deve salvar funcionário quando demissão data é nulo")
    @Test
    void shouldNotSaveFuncionarioWhenDismissalDateIsNull()
    {
        User user = new User(1L, "Pedro", "senha123", UserRoles.USER);

        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setAtivo(true).setUser(user).build();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> service.dismissFuncionarioById(1L, null));

        assertEquals("Data de demissão deve ser informada.", exception.getMessage());
        assertTrue(existing.isAtivo());
        assertNull(existing.getDataDemissao());
        assertTrue(user.isEnabled());
        assertNull(user.getPreviousRole());


        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @DisplayName("Não deve salvar funcionário quando demissão data é inválido")
    @Test
    void shouldNotSaveFuncionarioWhenDismissalDateIsInvalid()
    {
        User user = new User(1L, "Pedro", "senha123", UserRoles.USER);
        LocalDate hoje = LocalDate.now();
        LocalDate dataDemissaoFutura = hoje.plusDays(1);
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setDataAdmissao(hoje.minusYears(1)).setAtivo(true).setUser(user).build();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> service.dismissFuncionarioById(1L, dataDemissaoFutura));

        assertEquals("Data de demissão não pode ser futura.", exception.getMessage());
        assertTrue(existing.isAtivo());
        assertNull(existing.getDataDemissao());
        assertTrue(user.isEnabled());
        assertNull(user.getPreviousRole());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @DisplayName("Deve lançar exceção quando o funcionário não for encontrado quando demitindo inexistente funcionário")
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

    @DisplayName("Deve lançar exceção quando o funcionário não for encontrado quando demitindo com ID inválido")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldThrowFuncionarioNotFoundExceptionWhenDismissingWithInvalidId(Long id)
    {
        FuncionarioNotFoundException exception = assertThrows(FuncionarioNotFoundException.class, () -> service.dismissFuncionarioById(id, LocalDate.now()));

        assertEquals("Funcionário não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

    @DisplayName("Deve reativar funcionário e restaurar usuário perfil")
    @ParameterizedTest
    @EnumSource(value = UserRoles.class, names = {"USER", "ADMIN"})
    void shouldReactivateFuncionarioAndRestoreUserRole(UserRoles originalRole)
    {
        LocalDate hoje = LocalDate.now();
        LocalDate dataAdmissao = hoje.minusYears(1);
        LocalDate dataDemissao = hoje.minusDays(1);

        User user = new User(1L, "Pedro", "senha123", originalRole);
        user.deactivate();

        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(user).setDataAdmissao(dataAdmissao).setDataDemissao(dataDemissao).setAtivo(false).build();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));

        service.reactivateFuncionarioById(1L);

        assertTrue(existing.isAtivo());
        assertNull(existing.getDataDemissao());
        assertEquals(dataAdmissao, existing.getDataAdmissao());
        assertEquals(originalRole, user.getRole());
        assertNull(user.getPreviousRole());
        assertTrue(user.isEnabled());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).save(existing);
    }


    @DisplayName("Não deve salvar quando funcionário e usuário são já ativo")
    @Test
    void shouldNotSaveWhenFuncionarioAndUserAreAlreadyActive()
    {
        User user = new User(1L, "Pedro", "senha123", UserRoles.USER);

        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(user).setAtivo(true).build();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));

        service.reactivateFuncionarioById(1L);

        assertTrue(existing.isAtivo());
        assertTrue(user.isEnabled());
        assertNull(user.getPreviousRole());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @DisplayName("Não deve demitir funcionário sem usuário")
    @Test
    void shouldNotDismissFuncionarioWithoutUser()
    {
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).buildWithNullUser();
        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> service.dismissFuncionarioById(1L, LocalDate.now()));

        assertEquals("Funcionário não possui usuário vinculado.", exception.getMessage());
        assertTrue(existing.isAtivo());
        assertNull(existing.getDataDemissao());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository, Mockito.never())
                .save(Mockito.any());
    }

    @DisplayName("Deve lançar exceção quando o funcionário não for encontrado quando reativando inexistente funcionário")
    @Test
    void shouldThrowFuncionarioNotFoundExceptionWhenReactivatingNonexistentFuncionario()
    {
        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.empty());

        FuncionarioNotFoundException exception = assertThrows(FuncionarioNotFoundException.class, () -> service.reactivateFuncionarioById(1L));

        assertEquals("Funcionário não encontrado.", exception.getMessage());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @DisplayName("Deve lançar exceção quando o funcionário não for encontrado quando reativando com ID inválido")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldThrowFuncionarioNotFoundExceptionWhenReactivatingWithInvalidId(Long id)
    {
        FuncionarioNotFoundException exception = assertThrows(FuncionarioNotFoundException.class, () -> service.reactivateFuncionarioById(id));

        assertEquals("Funcionário não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

    @DisplayName("Deve substituir existente usuário e desativar anterior usuário")
    @ParameterizedTest
    @EnumSource(
            value = UserRoles.class,
            names = {"USER", "ADMIN"}
    )
    void shouldReplaceExistingUserAndDeactivatePreviousUser(UserRoles previousRole)
    {
        User existingUser = new User(1L, "pedro", "senha123", previousRole);
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(existingUser).build();
        User receivedUser = new User(2L, "maria", "senha456", UserRoles.USER);

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(repository.findFuncionarioByUser(receivedUser)).thenReturn(Optional.empty());

        service.linkUserToFuncionario(1L, receivedUser);

        assertSame(receivedUser, existing.getUser());
        assertEquals(UserRoles.NO_ACCESS, existingUser.getRole());
        assertEquals(previousRole, existingUser.getPreviousRole());
        assertTrue(receivedUser.isEnabled());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).findFuncionarioByUser(receivedUser);
        Mockito.verify(repository).save(existing);
    }

    @DisplayName("Deve desativar novo usuário quando substituindo usuário de inativo funcionário")
    @ParameterizedTest
    @EnumSource(
            value = UserRoles.class,
            names = {"USER", "ADMIN"}
    )
    void shouldDeactivateNewUserWhenReplacingUserOfInactiveFuncionario(UserRoles newUserRole)
    {
        LocalDate dismissalDate = LocalDate.now().minusDays(1);
        User previousUser = new User(1L, "pedro", "senha123", UserRoles.USER);
        previousUser.deactivate();
        User receivedUser = new User(2L, "maria", "senha456", newUserRole);
        Funcionario existing = FuncionarioTestBuilder.newFuncionario()
                .setId(1L)
                .setUser(previousUser)
                .setAtivo(false)
                .setDataDemissao(dismissalDate)
                .build();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(repository.findFuncionarioByUser(receivedUser)).thenReturn(Optional.empty());

        service.linkUserToFuncionario(1L, receivedUser);

        assertSame(receivedUser, existing.getUser());
        assertFalse(existing.isAtivo());
        assertEquals(dismissalDate, existing.getDataDemissao());
        assertEquals(UserRoles.NO_ACCESS, receivedUser.getRole());
        assertEquals(newUserRole, receivedUser.getPreviousRole());
        assertEquals(UserRoles.NO_ACCESS, previousUser.getRole());
        assertEquals(UserRoles.USER, previousUser.getPreviousRole());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).findFuncionarioByUser(receivedUser);
        Mockito.verify(repository).save(existing);
    }

    @DisplayName("Não deve salvar quando mesmo usuário é já vinculado para funcionário")
    @Test
    void shouldNotSaveWhenSameUserIsAlreadyLinkedToFuncionario()
    {
        User existingUser = new User(1L, "pedro", "senha123", UserRoles.USER);
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(existingUser).build();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));

        service.linkUserToFuncionario(1L, existingUser);

        assertSame(existingUser, existing.getUser());
        assertEquals(UserRoles.USER, existingUser.getRole());
        assertNull(existingUser.getPreviousRole());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository, Mockito.never()).findFuncionarioByUser(Mockito.any());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @DisplayName("Deve rejeitar usuário já vinculado para outro funcionário quando vinculando")
    @Test
    void shouldRejectUserAlreadyLinkedToAnotherFuncionarioWhenLinking()
    {
        User currentUser = new User(1L, "pedro", "senha123", UserRoles.USER);
        User receivedUser = new User(2L, "maria", "senha456", UserRoles.USER);
        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(currentUser).build();
        Funcionario found = FuncionarioTestBuilder.newFuncionario().setId(2L).setUser(receivedUser).build();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(repository.findFuncionarioByUser(receivedUser)).thenReturn(Optional.of(found));

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> service.linkUserToFuncionario(1L, receivedUser));

        assertSame(currentUser, existing.getUser());
        assertEquals(UserRoles.USER, currentUser.getRole());
        assertNull(currentUser.getPreviousRole());
        assertEquals( "Usuário já vinculado a outro funcionário.", exception.getMessage());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).findFuncionarioByUser(receivedUser);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @DisplayName("Deve rejeitar nulo usuário quando vinculando para funcionário")
    @Test
    void shouldRejectNullUserWhenLinkingToFuncionario()
    {
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> service.linkUserToFuncionario(1L, null));

        assertEquals("Usuário inválido.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

    @DisplayName("Deve lançar exceção quando o funcionário não for encontrado quando vinculando usuário para inexistente funcionário")
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

    @DisplayName("Deve lançar exceção quando o funcionário não for encontrado quando vinculando usuário com inválido funcionário ID")
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

    @DisplayName("Deve demitir funcionário e desativar usuário")
    @Test
    void shouldDismissFuncionarioAndDeactivateUser()
    {
        LocalDate hoje = LocalDate.now();
        User user = new User(1L, "Pedro", "senha123", UserRoles.USER);

        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(user).setDataAdmissao(hoje.minusYears(1)).setAtivo(true).build();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));

        service.dismissFuncionarioById(1L, hoje);

        assertFalse(existing.isAtivo());
        assertEquals(hoje, existing.getDataDemissao());
        assertEquals(UserRoles.NO_ACCESS, user.getRole());
        assertEquals(UserRoles.USER, user.getPreviousRole());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).save(existing);
    }

    @DisplayName("Não deve reativar funcionário sem usuário")
    @Test
    void shouldNotReactivateFuncionarioWithoutUser()
    {
        LocalDate dataDemissao = LocalDate.now().minusDays(1);

        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setAtivo(false).setDataDemissao(dataDemissao).buildWithNullUser();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> service.reactivateFuncionarioById(1L));

        assertEquals("Funcionário não possui usuário vinculado.", exception.getMessage());
        assertFalse(existing.isAtivo());
        assertEquals(dataDemissao, existing.getDataDemissao());

        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @DisplayName("Não deve reativar funcionário quando o usuário não tem anterior perfil")
    @Test
    void shouldNotReactivateFuncionarioWhenUserHasNoPreviousRole()
    {
        LocalDate dataDemissao = LocalDate.now().minusDays(1);

        User user = new User(1L, "Pedro", "senha123", UserRoles.NO_ACCESS);

        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(user).setAtivo(false).setDataDemissao(dataDemissao).build();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));

        UserWithInvalidInformationException exception = assertThrows(UserWithInvalidInformationException.class, () -> service.reactivateFuncionarioById(1L));

        assertEquals("Usuário não tinha permissão válida.", exception.getMessage());
        assertFalse(existing.isAtivo());
        assertEquals(dataDemissao, existing.getDataDemissao());
        assertEquals(UserRoles.NO_ACCESS, user.getRole());

        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @DisplayName("Não deve salvar funcionário quando usuário cadastro falha")
    @Test
    void shouldNotSaveFuncionarioWhenUserRegistrationFails()
    {
        User receivedUser = new User("pedro", "senha123", UserRoles.USER);
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setUser(receivedUser).buildForCreate();

        Mockito.when(repository.findFuncionarioByCpf(funcionario.getCpf())).thenReturn(Optional.empty());
        Mockito.when(repository.findFuncionarioByEmail(funcionario.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userService.registerUser(Mockito.any(User.class))).thenThrow(new UsernameAlredyInUseException("Nome de usuário já existe."));

        UsernameAlredyInUseException exception = assertThrows(UsernameAlredyInUseException.class, () -> service.createFuncionario(funcionario));

        assertEquals("Nome de usuário já existe.", exception.getMessage());

        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @DisplayName("Deve reativar novo usuário quando substituindo usuário de ativo funcionário")
    @ParameterizedTest
    @EnumSource(value = UserRoles.class, names = {"USER", "ADMIN"})
    void shouldReactivateNewUserWhenReplacingUserOfActiveFuncionario(UserRoles previousRole)
    {
        User previousUser = new User(1L, "pedro", "senha123", UserRoles.USER);

        User receivedUser = new User(2L, "maria", "senha456", previousRole);
        receivedUser.deactivate();

        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(previousUser).setAtivo(true).build();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(repository.findFuncionarioByUser(receivedUser)).thenReturn(Optional.empty());

        service.linkUserToFuncionario(1L, receivedUser);

        assertSame(receivedUser, existing.getUser());
        assertEquals(previousRole, receivedUser.getRole());
        assertNull(receivedUser.getPreviousRole());
        assertTrue(receivedUser.isEnabled());
        assertEquals(UserRoles.NO_ACCESS, previousUser.getRole());
        assertEquals(UserRoles.USER, previousUser.getPreviousRole());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).findFuncionarioByUser(receivedUser);
        Mockito.verify(repository).save(existing);
    }

    @DisplayName("Não deve alterar anterior usuário ou vincular quando novo usuário não pode ser reativado")
    @Test
    void shouldNotChangePreviousUserOrLinkWhenNewUserCannotBeReactivated()
    {
        User previousUser = new User(1L, "pedro", "senha123", UserRoles.USER);

        User receivedUser = new User(2L, "maria", "senha456", UserRoles.NO_ACCESS);

        Funcionario existing = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(previousUser).setAtivo(true).build();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(repository.findFuncionarioByUser(receivedUser)).thenReturn(Optional.empty());

        UserWithInvalidInformationException exception = assertThrows(UserWithInvalidInformationException.class, () -> service.linkUserToFuncionario(1L, receivedUser));

        assertEquals("Usuário não tinha permissão válida.", exception.getMessage());
        assertSame(previousUser, existing.getUser());
        assertEquals(UserRoles.USER, previousUser.getRole());
        assertNull(previousUser.getPreviousRole());
        assertEquals(UserRoles.NO_ACCESS, receivedUser.getRole());
        assertNull(receivedUser.getPreviousRole());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verify(repository).findFuncionarioByUser(receivedUser);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    // Testes deleteFuncionarioById

    @DisplayName("Deve excluir funcionário e desativar usuário")
    @ParameterizedTest
    @EnumSource(value = UserRoles.class, names = {"USER", "ADMIN"})
    void shouldDeleteFuncionarioAndDeactivateUser(UserRoles role)
    {
        User user = new User(10L, "pedro", "senha123", role);
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(user).build();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(funcionario));

        service.deleteFuncionarioById(1L);

        assertAll(
                () -> assertFalse(user.isEnabled()),
                () -> assertEquals(UserRoles.NO_ACCESS, user.getRole()),
                () -> assertEquals(role, user.getPreviousRole()),
                () -> assertEquals(10L, user.getId()),
                () -> assertEquals("pedro", user.getUsername()),
                () -> assertEquals("senha123", user.getPassword()),
                () -> assertSame(user, funcionario.getUser())
        );

        Mockito.verify(repository).findFuncionarioById(1L);
        InOrder inOrder = Mockito.inOrder(auditContext, repository);
        inOrder.verify(auditContext).setCurrentActor();
        inOrder.verify(repository).delete(Mockito.same(funcionario));
        inOrder.verify(repository).flush();
        inOrder.verify(auditContext).clearActor();
        Mockito.verifyNoMoreInteractions(repository);
        Mockito.verifyNoMoreInteractions(auditContext);
        Mockito.verifyNoInteractions(userService);
    }

    @DisplayName("Deve excluir demitido funcionário sem alterando bloqueado usuário")
    @ParameterizedTest
    @EnumSource(value = UserRoles.class, names = {"USER", "ADMIN"})
    void shouldDeleteDismissedFuncionarioWithoutChangingBlockedUser(UserRoles previousRole)
    {
        User user = new User(10L, "pedro", "senha123", previousRole);
        user.deactivate();

        LocalDate dataDemissao = LocalDate.now().minusDays(1);
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(user).setAtivo(false).setDataDemissao(dataDemissao).build();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(funcionario));

        service.deleteFuncionarioById(1L);

        assertAll(
                () -> assertFalse(user.isEnabled()),
                () -> assertEquals(UserRoles.NO_ACCESS, user.getRole()),
                () -> assertEquals(previousRole, user.getPreviousRole()),
                () -> assertSame(user, funcionario.getUser()),
                () -> assertEquals(dataDemissao, funcionario.getDataDemissao())
        );

        Mockito.verify(repository).findFuncionarioById(1L);
        InOrder inOrder = Mockito.inOrder(auditContext, repository);
        inOrder.verify(auditContext).setCurrentActor();
        inOrder.verify(repository).delete(Mockito.same(funcionario));
        inOrder.verify(repository).flush();
        inOrder.verify(auditContext).clearActor();
        Mockito.verifyNoMoreInteractions(repository);
        Mockito.verifyNoMoreInteractions(auditContext);
        Mockito.verifyNoInteractions(userService);
    }

    @DisplayName("Deve excluir funcionário com legado bloqueado usuário sem anterior perfil")
    @Test
    void shouldDeleteFuncionarioWithLegacyBlockedUserWithoutPreviousRole()
    {
        User user = new User(10L, "pedro", "senha123", UserRoles.NO_ACCESS);
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(user).build();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(funcionario));

        service.deleteFuncionarioById(1L);

        assertAll(
                () -> assertFalse(user.isEnabled()),
                () -> assertEquals(UserRoles.NO_ACCESS, user.getRole()),
                () -> assertNull(user.getPreviousRole())
        );

        Mockito.verify(repository).findFuncionarioById(1L);
        InOrder inOrder = Mockito.inOrder(auditContext, repository);
        inOrder.verify(auditContext).setCurrentActor();
        inOrder.verify(repository).delete(Mockito.same(funcionario));
        inOrder.verify(repository).flush();
        inOrder.verify(auditContext).clearActor();
        Mockito.verifyNoMoreInteractions(repository);
        Mockito.verifyNoMoreInteractions(auditContext);
        Mockito.verifyNoInteractions(userService);
    }

    @DisplayName("Deve rejeitar excluindo funcionário sem usuário")
    @Test
    void shouldRejectDeletingFuncionarioWithoutUser()
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setId(1L).buildWithNullUser();

        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.of(funcionario));

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> service.deleteFuncionarioById(1L));

        assertEquals("Funcionário não possui usuário vinculado.", exception.getMessage());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verifyNoMoreInteractions(repository);
        Mockito.verifyNoInteractions(userService, auditContext);
    }

    @DisplayName("Deve rejeitar excluindo inexistente funcionário")
    @Test
    void shouldRejectDeletingNonexistentFuncionario()
    {
        Mockito.when(repository.findFuncionarioById(1L)).thenReturn(Optional.empty());

        FuncionarioNotFoundException exception = assertThrows(FuncionarioNotFoundException.class, () -> service.deleteFuncionarioById(1L));

        assertEquals("Funcionário não encontrado.", exception.getMessage());

        Mockito.verify(repository).findFuncionarioById(1L);
        Mockito.verifyNoMoreInteractions(repository);
        Mockito.verifyNoInteractions(userService, auditContext);
    }

    @DisplayName("Deve rejeitar excluindo funcionário com ID inválido")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldRejectDeletingFuncionarioWithInvalidId(Long id)
    {
        FuncionarioNotFoundException exception = assertThrows(FuncionarioNotFoundException.class, () -> service.deleteFuncionarioById(id));

        assertEquals("Funcionário não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository, userService, auditContext);
    }

    // Testes countAllFuncionarios
    @DisplayName("Deve retornar total funcionários contar")
    @ParameterizedTest
    @ValueSource(longs = {0L, 1L, 12L})
    void shouldReturnTotalFuncionariosCount(long total)
    {
        Mockito.when(repository.count()).thenReturn(total);

        Long response = service.countAllFuncionarios();

        assertEquals(total, response);
        Mockito.verify(repository).count();
        Mockito.verifyNoMoreInteractions(repository);
        Mockito.verifyNoInteractions(userService, auditContext);
    }

    // Testes countActiveFuncionarios
    @DisplayName("Deve retornar ativo funcionários contar")
    @ParameterizedTest
    @ValueSource(longs = {0L, 1L, 12L})
    void shouldReturnActiveFuncionariosCount(long totalAtivos)
    {
        Mockito.when(repository.countFuncionarioByAtivoTrue()).thenReturn(totalAtivos);

        Long response = service.countActiveFuncionarios();

        assertEquals(totalAtivos, response);
        Mockito.verify(repository).countFuncionarioByAtivoTrue();
        Mockito.verifyNoMoreInteractions(repository);
        Mockito.verifyNoInteractions(userService, auditContext);
    }

    // Testes countInactiveFuncionarios
    @DisplayName("Deve retornar inativo funcionários contar")
    @ParameterizedTest
    @ValueSource(longs = {0L, 1L, 12L})
    void shouldReturnInactiveFuncionariosCount(long totalInativos)
    {
        Mockito.when(repository.countFuncionarioByAtivoFalse()).thenReturn(totalInativos);

        Long response = service.countInactiveFuncionarios();

        assertEquals(totalInativos, response);
        Mockito.verify(repository).countFuncionarioByAtivoFalse();
        Mockito.verifyNoMoreInteractions(repository);
        Mockito.verifyNoInteractions(userService, auditContext);
    }

}
