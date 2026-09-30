package org.example.clinicarestauracao.Domain.Entities;

import jakarta.persistence.*;
import lombok.*;
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
    @Enumerated(EnumType.STRING)
    @Column(name = "previous_role")
    @Setter(AccessLevel.NONE)
    private UserRoles previousRole;

    public User(Long id, String username, String password, UserRoles role)
    {
        this.id = id;
        setUsername(username);
        setPassword(password);
        setRole(role);
    }

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
        return switch (this.role)
        {
            case ADMIN -> List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
            case USER -> List.of(new SimpleGrantedAuthority("ROLE_USER"));
            case NO_ACCESS ->  List.of();
        };
    }

    public void setUsername(String username)
    {


        if(username == null || username.isBlank())
        {
            throw new UserWithInvalidInformationException("Nome de usuário não pode ser vazio.");
        }

        String usernameTrimmed = username.trim();

        if(usernameTrimmed.length() < 3)
        {
            throw new UserWithInvalidInformationException("Nome de usuário deve ter no mínimo 3 caracteres.");
        }
        else
        {
            this.username = usernameTrimmed;
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

    private void setRole(UserRoles role)
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

    public void deactivate()
    {
        if (this.role == UserRoles.NO_ACCESS)
        {
            throw new UserWithInvalidInformationException("Usuário já está desativado.");
        }
        this.previousRole = this.role;
        this.role = UserRoles.NO_ACCESS;
    }

    public void reactivate()
    {
        if (this.role != UserRoles.NO_ACCESS)
        {
            throw new UserWithInvalidInformationException("Usuário não está desativado.");
        }

        if (this.previousRole == null || this.previousRole == UserRoles.NO_ACCESS)
        {
            throw new UserWithInvalidInformationException("Usuário não tinha permissão válida.");
        }

        this.role = this.previousRole;
        this.previousRole = null;
    }

    @Override
    public boolean isEnabled()
    {
        return this.role != UserRoles.NO_ACCESS;
    }
}
