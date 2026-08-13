package org.example.clinicarestauracao.Application.Services;

import org.example.clinicarestauracao.Application.Dtos.SecurityDtos.RegisterDto;
import org.example.clinicarestauracao.Application.Interfaces.UserRepository;
import org.example.clinicarestauracao.Domain.Entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService
{
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean registerUser(RegisterDto user)
    {
        if(userRepository.findUserByUsername(user.username())!=null)
        {
            return false;
        }

        String cryptPassword = passwordEncoder.encode(user.password());

        User newUser = new User(user.username(), cryptPassword, user.role());

        userRepository.save(newUser);
        return true;
    }


}
