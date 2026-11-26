package org.example.clinicarestauracao.Application.Services;


import org.junit.jupiter.api.DisplayName;
import org.example.clinicarestauracao.Application.Exceptions.User.UserNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.User.UserWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Exceptions.User.UsernameAlredyInUseException;
import org.example.clinicarestauracao.Application.Interfaces.FuncionarioRepository;
import org.example.clinicarestauracao.Application.Interfaces.UserRepository;
import org.example.clinicarestauracao.Builders.UserTestBuilder;
import org.example.clinicarestauracao.Domain.Entities.User;
import org.example.clinicarestauracao.Domain.Enums.UserRoles;
import org.example.clinicarestauracao.Infrastruct.Database.Audit.DatabaseAuditContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest
{
    private UserTestBuilder builder = new UserTestBuilder();
    @Mock
    private FuncionarioRepository funcionarioRepository;
    @Mock
    private UserRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private DatabaseAuditContext audit;
    @InjectMocks
    private UserService service;

    @DisplayName("Deve cadastrar e retornar novo usuário")
    @Test
    void shouldRegisterAndReturnNewUser()
    {
        User receivedUser = new User("Pedro", "1234", UserRoles.USER);

        User savedUser = new User(1L, "Pedro", "senha-criptografada", UserRoles.USER);

        Mockito.when(repository.findUserByUsername("Pedro")).thenReturn(null);
        Mockito.when(passwordEncoder.encode("1234")).thenReturn("senha-criptografada");
        Mockito.when(repository.save(Mockito.any(User.class))).thenReturn(savedUser);

        User result = service.registerUser(receivedUser);

        assertSame(savedUser, result);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(repository).save(captor.capture());

        User persistedUser = captor.getValue();

        assertEquals("Pedro", persistedUser.getUsername());
        assertEquals("senha-criptografada", persistedUser.getPassword());
        assertEquals(UserRoles.USER, persistedUser.getRole());

        verify(repository).findUserByUsername("Pedro");
        verify(passwordEncoder).encode("1234");
    }


    @DisplayName("Deve buscar usuário por ID com sucesso")
    @Test
    void shouldFindUserByIdSuccessfully()
    {
        User user = (User) builder.build();
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(user));
        User response = service.findUserById(1L);

        assertSame(user, response);

        Mockito.verify(repository).findById(1L);
    }

    @DisplayName("Deve lançar exceção quando o usuário não for encontrado quando usuário não existir")
    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist()
    {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> service.findUserById(1L));

        assertEquals("Usuário não encontrado.", exception.getMessage());

        Mockito.verify(repository).findById(1L);
    }

    @DisplayName("Deve lançar exceção quando o usuário não for encontrado quando o ID é inválido")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldThrowUserNotFoundExceptionWhenIdIsInvalid(Long id)
    {
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> service.findUserById(id));

        assertEquals("Usuário não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

    @DisplayName("Deve rejeitar duplicado nome de usuário quando cadastrando usuário")
    @Test
    void shouldRejectDuplicatedUsernameWhenRegisteringUser()
    {
        User receivedUser = new User("Pedro", "1234", UserRoles.USER);
        User existingUser = new User(1L, "Pedro", "senha-criptografada", UserRoles.USER);

        Mockito.when(repository.findUserByUsername("Pedro")).thenReturn(existingUser);

        UsernameAlredyInUseException exception = assertThrows(UsernameAlredyInUseException.class, () -> service.registerUser(receivedUser)
        );

        assertEquals("Nome de usuário já existe.", exception.getMessage());
        Mockito.verify(repository).findUserByUsername("Pedro");
        Mockito.verifyNoInteractions(passwordEncoder);
        Mockito.verify(repository, Mockito.never())
                .save(Mockito.any());
    }

    @DisplayName("Deve atualizar nome de usuário e preservar senha e acesso estado")
    @Test
    void shouldUpdateUsernameAndPreservePasswordAndAccessState()
    {
        User user = new User(1L, "Pedro", "senha-antiga-criptografada", UserRoles.ADMIN);

        user.deactivate();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(repository.findUserByUsername("PedroAtualizado")).thenReturn(null);
        Mockito.when(repository.save(user)).thenReturn(user);

        User result = service.updateUsername(1L, "  PedroAtualizado  ");

        assertSame(user, result);
        assertEquals("PedroAtualizado", user.getUsername());
        assertEquals("senha-antiga-criptografada", user.getPassword());
        assertEquals(UserRoles.NO_ACCESS, user.getRole());
        assertEquals(UserRoles.ADMIN, user.getPreviousRole());
        assertFalse(user.isEnabled());

        verify(repository).findById(1L);
        verify(repository).findUserByUsername("PedroAtualizado");
        verify(repository).save(user);
        Mockito.verifyNoInteractions(passwordEncoder);
    }

    @DisplayName("Deve rejeitar nome de usuário pertencente por outro usuário")
    @Test
    void shouldRejectUsernameOwnedByAnotherUser()
    {
        User user = new User(1L, "Pedro", "senha-antiga-criptografada", UserRoles.USER);
        User usernameOwner = new User(2L, "Maria", "outra-senha-criptografada", UserRoles.USER);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(repository.findUserByUsername("Maria")).thenReturn(usernameOwner);

        UsernameAlredyInUseException exception = assertThrows(UsernameAlredyInUseException.class, () -> service.updateUsername(1L, "Maria"));

        assertEquals("Nome de usuário já existe.", exception.getMessage());
        assertEquals("Pedro", user.getUsername());
        assertEquals("senha-antiga-criptografada", user.getPassword());

        verify(repository).findById(1L);
        verify(repository).findUserByUsername("Maria");
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
        Mockito.verifyNoInteractions(passwordEncoder);
    }

    @DisplayName("Deve permitir nome de usuário pertencente por mesmo usuário quando atualizando nome de usuário")
    @Test
    void shouldAllowUsernameOwnedBySameUserWhenUpdatingUsername()
    {
        User user = new User(1L, "Pedro", "senha-criptografada", UserRoles.USER);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(repository.findUserByUsername("Pedro")).thenReturn(user);
        Mockito.when(repository.save(user)).thenReturn(user);

        User result = assertDoesNotThrow(() -> service.updateUsername(1L, "Pedro"));

        assertSame(user, result);
        assertEquals("Pedro", user.getUsername());
        assertEquals("senha-criptografada", user.getPassword());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).findUserByUsername("Pedro");
        Mockito.verify(repository).save(user);
        Mockito.verifyNoInteractions(passwordEncoder);
    }

    @DisplayName("Deve rejeitar nome de usuário atualizar quando usuário não existir")
    @Test
    void shouldRejectUsernameUpdateWhenUserDoesNotExist()
    {
        Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> service.updateUsername(99L, "PedroAtualizado"));

        assertEquals("Usuário não encontrado.", exception.getMessage());

        Mockito.verify(repository).findById(99L);
        Mockito.verify(repository, Mockito.never()).findUserByUsername(Mockito.anyString());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
        Mockito.verifyNoInteractions(passwordEncoder);
    }

    @DisplayName("Deve rejeitar inválido usuário ID quando atualizando nome de usuário")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldRejectInvalidUserIdWhenUpdatingUsername(Long userId)
    {
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> service.updateUsername(userId, "PedroAtualizado"));

        assertEquals("Usuário não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository, passwordEncoder);
    }

    @DisplayName("Deve rejeitar nome inválido de usuário quando atualizando nome de usuário")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "Pe"})
    void shouldRejectInvalidUsernameWhenUpdatingUsername(String username)
    {
        User user = new User(1L, "Pedro", "senha-criptografada", UserRoles.USER);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(UserWithInvalidInformationException.class, () -> service.updateUsername(1L, username));
        assertEquals("Pedro", user.getUsername());
        assertEquals("senha-criptografada", user.getPassword());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).findUserByUsername(Mockito.anyString());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
        Mockito.verifyNoInteractions(passwordEncoder);
    }

    //Testes para updatePassword

    @DisplayName("Deve atualizar senha com sucesso")
    @Test
    void shouldUpdatePasswordSuccessfully()
    {
        User user = new User(1L, "Pedro", "senha-antiga-criptografada", UserRoles.USER);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.encode("nova-senha")).thenReturn("nova-senha-criptografada");
        Mockito.when(repository.save(user)).thenReturn(user);

        User result = service.updatePassword(1L, "nova-senha");

        assertSame(user, result);
        assertEquals("Pedro", user.getUsername());
        assertEquals("nova-senha-criptografada", user.getPassword());
        assertEquals(UserRoles.USER, user.getRole());
        assertNull(user.getPreviousRole());
        assertTrue(user.isEnabled());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(passwordEncoder).encode("nova-senha");
        Mockito.verify(repository).save(user);
    }

    @DisplayName("Deve atualizar senha sem reativando usuário")
    @Test
    void shouldUpdatePasswordWithoutReactivatingUser()
    {
        User user = new User(1L, "Pedro", "senha-antiga-criptografada", UserRoles.ADMIN);
        user.deactivate();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.encode("nova-senha"))
                .thenReturn("nova-senha-criptografada");
        Mockito.when(repository.save(user))
                .thenReturn(user);

        User result = service.updatePassword(1L, "nova-senha");

        assertSame(user, result);
        assertEquals("Pedro", user.getUsername());
        assertEquals("nova-senha-criptografada", user.getPassword());
        assertEquals(UserRoles.NO_ACCESS, user.getRole());
        assertEquals(UserRoles.ADMIN, user.getPreviousRole());
        assertFalse(user.isEnabled());

        verify(repository).findById(1L);
        verify(passwordEncoder).encode("nova-senha");
        verify(repository).save(user);
    }

    @DisplayName("Deve rejeitar inválido senha quando atualizando senha")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "12"})
    void shouldRejectInvalidPasswordWhenUpdatingPassword(String newPassword)
    {
        User user = new User(1L, "Pedro", "senha-antiga-criptografada", UserRoles.USER);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(UserWithInvalidInformationException.class, () -> service.updatePassword(1L, newPassword));
        assertEquals("senha-antiga-criptografada", user.getPassword());
        assertEquals("Pedro", user.getUsername());
        assertEquals(UserRoles.USER, user.getRole());

        verify(repository).findById(1L);
        Mockito.verifyNoInteractions(passwordEncoder);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @DisplayName("Deve rejeitar senha atualizar quando usuário não existir")
    @Test
    void shouldRejectPasswordUpdateWhenUserDoesNotExist()
    {
        Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> service.updatePassword(99L, "nova-senha"));

        assertEquals("Usuário não encontrado.", exception.getMessage());

        verify(repository).findById(99L);
        Mockito.verifyNoInteractions(passwordEncoder);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @DisplayName("Deve rejeitar inválido usuário ID quando atualizando senha")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldRejectInvalidUserIdWhenUpdatingPassword(Long userId)
    {
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> service.updatePassword(userId, "nova-senha"));

        assertEquals("Usuário não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository, passwordEncoder);
    }

    @DisplayName("Deve preservar senha quando senha codificação falha")
    @Test
    void shouldPreservePasswordWhenPasswordEncodingFails()
    {
        User user = new User(1L, "Pedro", "senha-antiga-criptografada", UserRoles.USER);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.encode("nova-senha")).thenThrow(new IllegalStateException("Falha ao criptografar."));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> service.updatePassword(1L, "nova-senha"));

        assertEquals("Falha ao criptografar.", exception.getMessage());
        assertEquals("senha-antiga-criptografada", user.getPassword());
        assertEquals("Pedro", user.getUsername());
        assertEquals(UserRoles.USER, user.getRole());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(passwordEncoder).encode("nova-senha");
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    //Testes updateRole


    @DisplayName("Deve atualizar perfil com sucesso")
    @Test
    void shouldUpdateRoleSuccessfully()
    {
        User user = new User(1L, "Pedro", "senha-criptografada", UserRoles.USER);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(repository.save(user)).thenReturn(user);

        User result = service.updateRole(1L, UserRoles.ADMIN);

        assertSame(user, result);
        assertEquals(UserRoles.ADMIN, user.getRole());
        assertNull(user.getPreviousRole());
        assertEquals("Pedro", user.getUsername());
        assertEquals("senha-criptografada", user.getPassword());
        assertTrue(user.isEnabled());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).save(user);
        Mockito.verifyNoInteractions(passwordEncoder);
    }

    @DisplayName("Não deve salvar quando perfil é já atribuído")
    @Test
    void shouldNotSaveWhenRoleIsAlreadyAssigned()
    {
        User user = new User(1L, "Pedro", "senha-criptografada", UserRoles.ADMIN);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(user));

        User result = service.updateRole(1L, UserRoles.ADMIN);

        assertSame(user, result);
        assertEquals(UserRoles.ADMIN, user.getRole());
        assertNull(user.getPreviousRole());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
        Mockito.verifyNoInteractions(passwordEncoder);
    }

    @DisplayName("Deve rejeitar nenhum acesso como novo perfil")
    @Test
    void shouldRejectNoAccessAsNewRole()
    {
        User user = new User(1L, "Pedro", "senha-criptografada", UserRoles.USER);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(user));

        UserWithInvalidInformationException exception = assertThrows(UserWithInvalidInformationException.class, () -> service.updateRole(1L, UserRoles.NO_ACCESS));

        assertEquals("Papel de usuário invalido.", exception.getMessage());
        assertEquals(UserRoles.USER, user.getRole());
        assertNull(user.getPreviousRole());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
        Mockito.verifyNoInteractions(passwordEncoder);
    }

    @DisplayName("Deve rejeitar perfil atualizar quando usuário é desabilitado")
    @Test
    void shouldRejectRoleUpdateWhenUserIsDisabled()
    {
        User user = new User(1L, "Pedro", "senha-criptografada", UserRoles.ADMIN);
        user.deactivate();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(user));

        UserWithInvalidInformationException exception = assertThrows(UserWithInvalidInformationException.class, () -> service.updateRole(1L, UserRoles.USER));

        assertEquals("Usuário está desativado.", exception.getMessage());
        assertEquals(UserRoles.NO_ACCESS, user.getRole());
        assertEquals(UserRoles.ADMIN, user.getPreviousRole());
        assertFalse(user.isEnabled());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
        Mockito.verifyNoInteractions(passwordEncoder);
    }

    @DisplayName("Deve rejeitar mesmo nenhum acesso perfil antes idempotente retornar")
    @Test
    void shouldRejectSameNoAccessRoleBeforeIdempotentReturn()
    {
        User user = new User(1L, "Pedro", "senha-criptografada", UserRoles.ADMIN);
        user.deactivate();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(user));

        UserWithInvalidInformationException exception = assertThrows(UserWithInvalidInformationException.class, () -> service.updateRole(1L, UserRoles.NO_ACCESS));

        assertEquals("Papel de usuário invalido.", exception.getMessage());
        assertEquals(UserRoles.NO_ACCESS, user.getRole());
        assertEquals(UserRoles.ADMIN, user.getPreviousRole());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
        Mockito.verifyNoInteractions(passwordEncoder);
    }

    @DisplayName("Deve rejeitar nulo perfil")
    @Test
    void shouldRejectNullRole()
    {
        User user = new User(1L, "Pedro", "senha-criptografada", UserRoles.USER);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(user));

        UserWithInvalidInformationException exception = assertThrows(UserWithInvalidInformationException.class, () -> service.updateRole(1L, null));

        assertEquals("Papel de usuário invalido.", exception.getMessage());
        assertEquals(UserRoles.USER, user.getRole());
        assertNull(user.getPreviousRole());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
        Mockito.verifyNoInteractions(passwordEncoder);
    }

    @DisplayName("Deve rejeitar perfil atualizar quando usuário não existir")
    @Test
    void shouldRejectRoleUpdateWhenUserDoesNotExist()
    {
        Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> service.updateRole(99L, UserRoles.ADMIN));

        assertEquals("Usuário não encontrado.", exception.getMessage());

        Mockito.verify(repository).findById(99L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
        Mockito.verifyNoInteractions(passwordEncoder);
    }

    @DisplayName("Deve rejeitar inválido usuário ID quando atualizando perfil")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldRejectInvalidUserIdWhenUpdatingRole(Long userId)
    {
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> service.updateRole(userId, UserRoles.ADMIN));

        assertEquals("Usuário não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository, passwordEncoder);
    }

    @DisplayName("Deve excluir usuário quando nenhum funcionário é vinculado")
    @Test
    void shouldDeleteUserWhenNoFuncionarioIsLinked()
    {
        User user = new User(1L, "Pedro", "senha-criptografada", UserRoles.USER);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(funcionarioRepository.existsByUser(user)).thenReturn(false);

        assertDoesNotThrow(() -> service.deleteUser(1L));

        var inOrder = Mockito.inOrder(repository, funcionarioRepository, audit);
        inOrder.verify(repository).findById(1L);
        inOrder.verify(funcionarioRepository).existsByUser(user);
        inOrder.verify(audit).setCurrentActor();
        inOrder.verify(repository).delete(user);
        inOrder.verify(repository).flush();
        inOrder.verify(audit).clearActor();
        Mockito.verifyNoMoreInteractions(repository, funcionarioRepository, audit);
        Mockito.verifyNoInteractions(passwordEncoder);
    }

    @DisplayName("Deve rejeitar excluindo usuário quando funcionário é vinculado")
    @Test
    void shouldRejectDeletingUserWhenFuncionarioIsLinked()
    {
        User user = new User(1L, "Pedro", "senha-criptografada", UserRoles.USER);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(funcionarioRepository.existsByUser(user)).thenReturn(true);

        UserWithInvalidInformationException exception = assertThrows(
                UserWithInvalidInformationException.class,
                () -> service.deleteUser(1L)
        );

        assertEquals(
                "Não é possível excluir usuário vinculado a um funcionário.",
                exception.getMessage()
        );

        Mockito.verify(repository).findById(1L);
        Mockito.verify(funcionarioRepository).existsByUser(user);
        Mockito.verify(repository, Mockito.never()).delete(Mockito.any(User.class));
        Mockito.verifyNoInteractions(audit);
        Mockito.verifyNoMoreInteractions(repository, funcionarioRepository);
        Mockito.verifyNoInteractions(passwordEncoder);
    }

    @DisplayName("Deve rejeitar excluindo usuário que não existir")
    @Test
    void shouldRejectDeletingUserThatDoesNotExist()
    {
        Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> service.deleteUser(99L)
        );

        assertEquals("Usuário não encontrado.", exception.getMessage());

        Mockito.verify(repository).findById(99L);
        Mockito.verifyNoMoreInteractions(repository);
        Mockito.verifyNoInteractions(funcionarioRepository, passwordEncoder, audit);
    }

    @DisplayName("Deve rejeitar excluindo usuário com ID inválido")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldRejectDeletingUserWithInvalidId(Long id)
    {
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> service.deleteUser(id)
        );

        assertEquals("Usuário não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository, funcionarioRepository, passwordEncoder, audit);
    }

}
