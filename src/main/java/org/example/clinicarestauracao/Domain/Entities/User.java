package org.example.clinicarestauracao.Domain.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.clinicarestauracao.Application.Exceptions.UserWithInvalidInformationException;
import org.example.clinicarestauracao.Domain.Enums.UserRoles;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "Usuarios")
public class User implements UserDetails
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRoles role;

    public User(String username, String password, UserRoles role)
    {
        setUsername(username);
        setPassword(password);
        setRole(role);
    }


    @Override
    @NullMarked
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        if(this.role == UserRoles.ADMIN)
        {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        }
        else
        {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }

    public void setUsername(String username)
    {
        if(username == null || username.isBlank())
        {
            throw new UserWithInvalidInformationException("Nome de usuário não pode ser vazio.");
        }
        else if(username.length() < 3)
        {
            throw new UserWithInvalidInformationException("Nome de usuário deve ter no mínimo 3 caracteres.");
        }
        else
        {
            this.username = username;
        }
    }

    public void setPassword(String password)
    {
        if (password == null || password.isBlank())
        {
            throw new UserWithInvalidInformationException("Senha de usuário não pode ser vazio.");
        }
        else if (password.length() < 3)
        {
            throw new UserWithInvalidInformationException("Senha de usuário não pode ter menos que 3 caracteres.");
        }
        else
        {
            this.password = password;
        }
    }

    public void setRole(UserRoles role)
    {
        if (role == null)
        {
            throw new UserWithInvalidInformationException("Papel do usuário deve ser definido.");
        }
        else
        {
            this.role = role;
        }
    }
}
