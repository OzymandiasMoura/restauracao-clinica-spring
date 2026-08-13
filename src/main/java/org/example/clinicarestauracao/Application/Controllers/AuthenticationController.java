package org.example.clinicarestauracao.Application.Controllers;

import org.example.clinicarestauracao.Application.Dtos.SecurityDtos.AuthenticationDto;
import org.example.clinicarestauracao.Application.Dtos.SecurityDtos.LoginResponseDto;
import org.example.clinicarestauracao.Application.Dtos.SecurityDtos.RegisterDto;
import org.example.clinicarestauracao.Application.Interfaces.UserRepository;
import org.example.clinicarestauracao.Domain.Entities.User;
import org.example.clinicarestauracao.Infrastruct.Security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthenticationController
{
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private UserRepository repository;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, TokenService tokenService, UserRepository userRepository)
    {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.repository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthenticationDto user)
    {
        var userPassword = new UsernamePasswordAuthenticationToken(user.username(), user.password());
        var auth = authenticationManager.authenticate(userPassword);

        var token =  tokenService.generateToken((User)auth.getPrincipal());
        return ResponseEntity.ok(new LoginResponseDto(token));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterDto user)
    {
        if(this.repository.findUserByUsername(user.username())!=null)
        {
            return ResponseEntity.badRequest().build();
        }

        String cryptPassword =  new BCryptPasswordEncoder().encode(user.password());

        User newUser = new User(user.username(), cryptPassword, user.role());

        this.repository.save(newUser);
        return ResponseEntity.ok().build();
    }

}
