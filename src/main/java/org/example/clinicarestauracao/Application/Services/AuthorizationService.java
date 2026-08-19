package org.example.clinicarestauracao.Application.Services;

import lombok.AllArgsConstructor;
import org.example.clinicarestauracao.Application.Interfaces.UserRepository;
import org.example.clinicarestauracao.Domain.Entities.User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthorizationService implements UserDetailsService
{
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        UserDetails user = repository.findUserByUsername(username);
        if (user == null)
        {
            throw new BadCredentialsException("Nome de usuário ou senha inválidos.");
        }
        return user;
    }
}
