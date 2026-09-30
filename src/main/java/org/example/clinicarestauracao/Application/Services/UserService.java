package org.example.clinicarestauracao.Application.Services;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.clinicarestauracao.Application.Exceptions.User.UserNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.UsernameAlredyInUseException;
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

    public User registerUser(User user)
    {
        if(userRepository.findUserByUsername(user.getUsername())!=null)
        {
            throw new UsernameAlredyInUseException("Nome de usuário já existe.");
        }

        String cryptPassword = passwordEncoder.encode(user.getPassword());

        User newUser = new User(user.getUsername(), cryptPassword, user.getRole());

        return userRepository.save(newUser);
    }

    public User findUserById(Long id)
    {
        if (id == null || id <= 0)
        {
            throw new UserNotFoundException("Usuário não encontrado.");
        }
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));
    }

    @Transactional
    public User updateUserCredentials(User user, String username, String password
    )
    {
        if (user == null)
        {
            throw new UserNotFoundException("Usuário não encontrado.");
        }

        String passwordForValidation = password == null ? user.getPassword() : password;

        User validatedData = new User(username, passwordForValidation, user.getRole());

        var usernameOwner = userRepository.findUserByUsername(validatedData.getUsername());

        if (usernameOwner != null && !usernameOwner.equals(user))
        {
            throw new UsernameAlredyInUseException("Nome de usuário já existe.");
        }

        String encryptedPassword = password == null ? null : passwordEncoder.encode(password);

        user.setUsername(validatedData.getUsername());

        if (encryptedPassword != null)
        {
            user.setPassword(encryptedPassword);
        }

        return userRepository.save(user);
    }

}
