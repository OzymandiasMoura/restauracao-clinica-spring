package org.example.clinicarestauracao.Application.Services;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.clinicarestauracao.Application.Exceptions.User.UserNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.UsernameAlredyInUseException;
import org.example.clinicarestauracao.Application.Interfaces.UserRepository;
import org.example.clinicarestauracao.Domain.Entities.User;
import org.example.clinicarestauracao.Domain.Enums.UserRoles;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
    public User updateUsername(Long userId, String username)
    {
        User userToUpdate = findUserById(userId);

        User userToFind = User.forUsernameUpdate(username);

        User userFound = (User) userRepository.findUserByUsername(userToFind.getUsername());

        if (userFound != null && !Objects.equals(userFound.getId(), userToUpdate.getId()))
        {
            throw new UsernameAlredyInUseException("Nome de usuário já existe.");
        }

        userToUpdate.setUsername(userToFind.getUsername());
        return userRepository.save(userToUpdate);
    }

    @Transactional
    public User updatePassword(Long userId, String newPassword)
    {
        User userToUpdate = findUserById(userId);

        User userTransactional = new User(userToUpdate.getUsername(), newPassword, userToUpdate.getRole());

        String encryptedPassword = passwordEncoder.encode(userTransactional.getPassword());

        userToUpdate.setPassword(encryptedPassword);
        return userRepository.save(userToUpdate);
    }

    @Transactional
    public User updateRole(Long userId, UserRoles role)
    {
        User userToUpdate = findUserById(userId);
        UserRoles currentRole = userToUpdate.getRole();

        userToUpdate.forRoleUpdate(role);

        if (currentRole == role)
        {
            return userToUpdate;
        }

        return userRepository.save(userToUpdate);
    }
}
