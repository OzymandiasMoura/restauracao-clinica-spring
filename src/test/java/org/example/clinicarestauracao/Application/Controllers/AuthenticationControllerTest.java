package org.example.clinicarestauracao.Application.Controllers;

import org.example.clinicarestauracao.Application.Interfaces.UserRepository;
import org.example.clinicarestauracao.Application.Services.UserService;
import org.example.clinicarestauracao.Infrastruct.Security.TokenService;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationControllerTest
{
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private TokenService tokenService;
    @Mock
    private UserService userService;

}