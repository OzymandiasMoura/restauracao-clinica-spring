package org.example.clinicarestauracao.Builders;

import org.example.clinicarestauracao.Domain.Entities.User;
import org.example.clinicarestauracao.Domain.Enums.UserRoles;
import org.springframework.security.core.userdetails.UserDetails;

public class UserTestBuilder
{
    private Long id = 1L;
    private String username = "Pedro";
    private String password = "1234";
    private UserRoles role = UserRoles.ADMIN;

    public UserTestBuilder()
    {
    }

    public UserTestBuilder setId(Long id)
    {
        this.id = id;
        return this;
    }

    public UserTestBuilder setUsername(String username)
    {
        this.username = username;
        return this;
    }

    public UserTestBuilder setPassword(String password)
    {
        this.password = password;
        return this;
    }

    public UserTestBuilder setRole(UserRoles role)
    {
        this.role = role;
        return this;
    }

    public UserDetails build()
    {
        User user = new User(id, username, password, role);
        return user;
    }

    public UserDetails buildForCreate()
    {
        User user = new User(username, password, role);
        return user;
    }

}
