package org.example.clinicarestauracao.Application.Services;


import org.junit.jupiter.api.DisplayName;
import org.example.clinicarestauracao.Application.Interfaces.UserRepository;
import org.example.clinicarestauracao.Builders.UserTestBuilder;
import org.example.clinicarestauracao.Domain.Entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest
{
    private UserTestBuilder builder =  new UserTestBuilder();

    @Mock
    private UserRepository repository;

    @InjectMocks
    private AuthorizationService authorizationService;

    @DisplayName("Deve carregar usuário por nome de usuário")
    @Test
    void shouldLoadUserByUsername()
    {
        User user = (User) builder.build();

        Mockito.when(repository.findUserByUsername("Pedro")).thenReturn(user);

        var result = authorizationService.loadUserByUsername("Pedro");

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
        assertNotNull(result.getAuthorities());
    }


    @DisplayName("Deve lançar exceção quando nome de usuário não existir")
    @Test
    void shouldThrowExceptionWhenUsernameDoesNotExist()
    {
        Mockito.when(repository.findUserByUsername("inexistente"))
                .thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> authorizationService.loadUserByUsername("inexistente")
        );
    }
}