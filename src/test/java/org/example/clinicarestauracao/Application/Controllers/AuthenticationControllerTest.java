package org.example.clinicarestauracao.Application.Controllers;
import org.example.clinicarestauracao.Application.Dtos.SecurityDtos.AuthenticationDto;
import org.example.clinicarestauracao.Application.Dtos.SecurityDtos.LoginResponseDto;
import org.example.clinicarestauracao.Application.Dtos.SecurityDtos.RegisterDto;
import org.example.clinicarestauracao.Application.Services.UserService;
import org.example.clinicarestauracao.Builders.UserTestBuilder;
import org.example.clinicarestauracao.Domain.Entities.User;
import org.example.clinicarestauracao.Infrastruct.Security.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest
{
    private UserTestBuilder builder = new UserTestBuilder();
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private TokenService tokenService;
    @Mock
    private UserService userService;
    @InjectMocks
    private AuthenticationController controller;

    @Test
    void shouldRegisterUserAndReturnOk()
    {
        User user = (User) builder.buildForCreate();
        RegisterDto dto = new RegisterDto(user.getUsername(), user.getPassword(), user.getRole());
        Mockito.when(userService.registerUser(any(User.class))).thenReturn(true);

        var response = controller.register(dto);

        assertEquals(200, response.getStatusCode().value());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).registerUser(userCaptor.capture());

        User userSentToService = userCaptor.getValue();

        assertEquals(dto.username(), userSentToService.getUsername());
        assertEquals(dto.password(), userSentToService.getPassword());

        assertEquals(dto.role(), userSentToService.getRole());
    }

    @Test
    void shouldLoginAndReturnToken()
    {
        AuthenticationDto dto = new AuthenticationDto("Pedro", "1234");
        User userAuthenticated = (User) builder.build();

        Authentication authentication = Mockito.mock(Authentication.class);

        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(userAuthenticated);
        Mockito.when(tokenService.generateToken(any())).thenReturn("jwt-token");

        var response = controller.login(dto);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());

        LoginResponseDto responseBody = (LoginResponseDto) response.getBody();
        assertEquals("jwt-token", responseBody.token());
        verify(tokenService).generateToken(userAuthenticated);
    }
}