package org.example.clinicarestauracao.Domain.Entities;

import org.example.clinicarestauracao.Application.Exceptions.UserWithInvalidInformationException;
import org.example.clinicarestauracao.Builders.UserTestBuilder;
import org.example.clinicarestauracao.Domain.Enums.UserRoles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.core.GrantedAuthority;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @ParameterizedTest(name = "{index}")
    @MethodSource(value = "dataProvider")
    void shouldThrowExceptionWhenUserHasNullInformation(String username, String password, UserRoles role, String message)
    {
        UserWithInvalidInformationException e = assertThrows(UserWithInvalidInformationException.class, () ->
                new User(username, password, role));

        assertThat(e.getMessage()).isEqualTo(message);
    }

    @ParameterizedTest(name = "{index}")
    @MethodSource(value = "dataProvider2")
    void shouldThrowExceptionWhenUserHasBlankInformation(String username, String password, UserRoles role, String message)
    {
        UserWithInvalidInformationException e = assertThrows(UserWithInvalidInformationException.class, () -> new User(username, password, role));

        assertThat(e.getMessage()).isEqualTo(message);
    }

    private static Stream<Arguments> dataProvider()
    {
        return Stream.of(
                Arguments.of(null, "123", UserRoles.ADMIN, "Nome de usuário não pode ser vazio."),
                Arguments.of("Pedro", null, UserRoles.ADMIN, "Senha de usuário não pode ser vazio."),
                Arguments.of("Pedro", "123", null, "Papel do usuário deve ser definido.")
        );
    }

    private static Stream<Arguments> dataProvider2()
    {
        return Stream.of(
                Arguments.of(" ", "123", UserRoles.ADMIN, "Nome de usuário não pode ser vazio."),
                Arguments.of("Pedro", " ", UserRoles.ADMIN, "Senha de usuário não pode ser vazio.")
        );
    }

}
