package org.example.clinicarestauracao.Application.Services;

import org.example.clinicarestauracao.Application.Interfaces.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AuthorizationService implements UserDetailsService
{
    UserRepository repository;

    public AuthorizationService(UserRepository userRepository)
    {
        this.repository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        return repository.findUserByUsername(username);
    }
}
