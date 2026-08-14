package org.example.clinicarestauracao.Application.Services;
import lombok.AllArgsConstructor;
import org.example.clinicarestauracao.Application.Interfaces.UserRepository;
import org.example.clinicarestauracao.Domain.Entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserService
{
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public boolean registerUser(User user)
    {
        if(userRepository.findUserByUsername(user.getUsername())!=null)
        {
            return false;
        }

        String cryptPassword = passwordEncoder.encode(user.getPassword());

        User newUser = new User(user.getUsername(), cryptPassword, user.getRole());

        userRepository.save(newUser);
        return true;
    }
}
