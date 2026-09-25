package org.example.clinicarestauracao.Application.Services;

import org.example.clinicarestauracao.Application.Exceptions.User.UserNotFoundException;
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
    void shouldRegisterNewUser()
    {
        User user = (User) builder.build();

        Mockito.when(repository.findUserByUsername(user.getUsername())).thenReturn(null);
        Mockito.when(repository.save(Mockito.any(User.class))).thenReturn(user);
        Mockito.when(passwordEncoder.encode(user.getPassword())).thenReturn(user.getPassword());

        var result = service.registerUser(user);

        assertTrue(result);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(repository).save(userCaptor.capture());

        User userCaptorValue = userCaptor.getValue();

        assertEquals("Pedro", userCaptorValue.getUsername());
        assertEquals("1234", userCaptorValue.getPassword());
        assertEquals(UserRoles.ADMIN, userCaptorValue.getRole());

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


}