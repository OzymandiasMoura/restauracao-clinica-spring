package org.example.clinicarestauracao.Application.Services;

import org.example.clinicarestauracao.Application.Exceptions.User.UserNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.UserWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Exceptions.UsernameAlredyInUseException;
import org.example.clinicarestauracao.Application.Interfaces.UserRepository;
import org.example.clinicarestauracao.Builders.UserTestBuilder;
import org.example.clinicarestauracao.Domain.Entities.User;
import org.example.clinicarestauracao.Domain.Enums.UserRoles;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest
{
    private UserTestBuilder builder = new UserTestBuilder();
    @Mock
    private UserRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService service;

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


    @Test
    void shouldFindUserByIdSuccessfully()
    {
        User user = (User) builder.build();
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(user));
        User response = service.findUserById(1L);

        assertSame(user, response);

        Mockito.verify(repository).findById(1L);
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist()
    {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> service.findUserById(1L));

        assertEquals("Usuário não encontrado.", exception.getMessage());

        Mockito.verify(repository).findById(1L);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldThrowUserNotFoundExceptionWhenIdIsInvalid(Long id)
    {
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> service.findUserById(id));

        assertEquals("Usuário não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

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

    @Test
    void shouldUpdateUsernameAndPassword()
    {
        User user = new User(1L, "Pedro", "senha-antiga-criptografada", UserRoles.USER);

        Mockito.when(repository.findUserByUsername("PedroAtualizado")).thenReturn(null);
        Mockito.when(passwordEncoder.encode("senhaNova")).thenReturn("senha-nova-criptografada");
        Mockito.when(repository.save(user)).thenReturn(user);

        User result = service.updateUserCredentials(user, "  PedroAtualizado  ", "senhaNova");

        assertSame(user, result);
        assertEquals("PedroAtualizado", user.getUsername());
        assertEquals("senha-nova-criptografada", user.getPassword());
        assertEquals(UserRoles.USER, user.getRole());
        assertNull(user.getPreviousRole());

        verify(repository).findUserByUsername("PedroAtualizado");
        verify(passwordEncoder).encode("senhaNova");
        verify(repository).save(user);
    }

    @Test
    void shouldPreservePasswordWhenUpdatePasswordIsNull()
    {
        User user = new User(1L, "Pedro", "senha-antiga-criptografada", UserRoles.USER);

        Mockito.when(repository.findUserByUsername("PedroAtualizado")).thenReturn(null);
        Mockito.when(repository.save(user)).thenReturn(user);

        service.updateUserCredentials(user, "PedroAtualizado", null);

        assertEquals("PedroAtualizado", user.getUsername());
        assertEquals("senha-antiga-criptografada", user.getPassword());

        Mockito.verifyNoInteractions(passwordEncoder);
        verify(repository).save(user);
    }

    @Test
    void shouldRejectDuplicatedUsernameWhenUpdatingUser()
    {
        User user = new User(1L, "Pedro", "senha-antiga-criptografada", UserRoles.USER);
        User usernameOwner = new User(2L, "Maria", "outra-senha-criptografada", UserRoles.USER);

        Mockito.when(repository.findUserByUsername("Maria")).thenReturn(usernameOwner);

        UsernameAlredyInUseException exception = assertThrows(
                UsernameAlredyInUseException.class,
                () -> service.updateUserCredentials(user, "Maria", "senhaNova")
        );

        assertEquals("Nome de usuário já existe.", exception.getMessage());
        assertEquals("Pedro", user.getUsername());
        assertEquals("senha-antiga-criptografada", user.getPassword());

        Mockito.verifyNoInteractions(passwordEncoder);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void shouldUpdateCredentialsWithoutReactivatingUser()
    {
        User user = new User(1L, "Pedro", "senha-antiga-criptografada", UserRoles.ADMIN);
        user.deactivate();

        Mockito.when(repository.findUserByUsername("PedroAtualizado")).thenReturn(null);
        Mockito.when(repository.save(user)).thenReturn(user);

        service.updateUserCredentials(user, "PedroAtualizado", null);

        assertEquals(UserRoles.NO_ACCESS, user.getRole());
        assertEquals(UserRoles.ADMIN, user.getPreviousRole());
        assertFalse(user.isEnabled());
        assertEquals("senha-antiga-criptografada", user.getPassword());

        Mockito.verifyNoInteractions(passwordEncoder);
        verify(repository).save(user);
    }

    @Test
    void shouldAllowUsernameOwnedBySameUser()
    {
        User user = new User(1L, "Pedro", "senha-criptografada", UserRoles.USER);

        Mockito.when(repository.findUserByUsername("Pedro")).thenReturn(user);
        Mockito.when(repository.save(user)).thenReturn(user);

        assertDoesNotThrow(() -> service.updateUserCredentials(user, "Pedro", null));

        verify(repository).save(user);
    }

    @Test
    void shouldRejectNullUserWhenUpdatingCredentials()
    {
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> service.updateUserCredentials(null, "Pedro", null)
        );

        assertEquals("Usuário não encontrado.", exception.getMessage());
        Mockito.verifyNoInteractions(repository, passwordEncoder);
    }

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

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldRejectInvalidUserIdWhenUpdatingUsername(Long userId)
    {
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> service.updateUsername(userId, "PedroAtualizado"));

        assertEquals("Usuário não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository, passwordEncoder);
    }

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

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldRejectInvalidUserIdWhenUpdatingPassword(Long userId)
    {
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> service.updatePassword(userId, "nova-senha"));

        assertEquals("Usuário não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository, passwordEncoder);
    }

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

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldRejectInvalidUserIdWhenUpdatingRole(Long userId)
    {
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> service.updateRole(userId, UserRoles.ADMIN));

        assertEquals("Usuário não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository, passwordEncoder);
    }

}
