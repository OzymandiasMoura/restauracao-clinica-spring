package org.example.clinicarestauracao.Domain.Entities;

import org.example.clinicarestauracao.Builders.UserTestBuilder;
import org.example.clinicarestauracao.Domain.Enums.UserRoles;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class UserTest
{
    private final UserTestBuilder builder = new UserTestBuilder();

    @Test
    void shouldReturnUserRole()
    {
        User user = (User) builder.setRole(UserRoles.USER).build();

        var authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        assertThat(authorities).containsExactly("ROLE_USER");
    }

    @Test
    void shouldReturnUserAndAdminRoles()
    {
        User user = (User) builder.build();

        var authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        assertThat(authorities).containsExactly("ROLE_ADMIN", "ROLE_USER");
    }
}