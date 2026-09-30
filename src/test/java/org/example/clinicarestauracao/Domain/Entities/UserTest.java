package org.example.clinicarestauracao.Domain.Entities;

import org.example.clinicarestauracao.Application.Exceptions.UserWithInvalidInformationException;
import org.example.clinicarestauracao.Builders.UserTestBuilder;
import org.example.clinicarestauracao.Domain.Enums.UserRoles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.core.GrantedAuthority;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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

    @ParameterizedTest(name = "{index}")
    @MethodSource(value = "dataProvider3")
    void shouldThrowExceptionWhenUsernameOrPasswordHasLessThan3(String username, String password, UserRoles role, String message)
    {
        UserWithInvalidInformationException e = assertThrows(UserWithInvalidInformationException.class, () -> new User(username, password, role));

        assertThat(e.getMessage()).isEqualTo(message);
    }

    @Test
    void shouldTrimUsername()
    {
        User user = new User("  Pedro  ", "123", UserRoles.ADMIN);

        assertEquals("Pedro", user.getUsername());
    }

    @Test
    void shouldValidateUsernameLengthAfterTrim()
    {
        UserWithInvalidInformationException exception = assertThrows(UserWithInvalidInformationException.class, () -> new User("  Pe  ", "123", UserRoles.ADMIN));

        assertEquals("Nome de usuário deve ter no mínimo 3 caracteres.", exception.getMessage());
    }

    @Test
    void shouldReturnNoAuthoritiesWhenUserHasNoAccess()
    {
        User user = (User) builder.setRole(UserRoles.NO_ACCESS).build();

        assertThat(user.getAuthorities()).isEmpty();
    }

    @Test
    void shouldCreateActiveUserWithoutPreviousRole()
    {
        User user = new User(1L, "Pedro", "123", UserRoles.USER);

        assertEquals(UserRoles.USER, user.getRole());
        assertNull(user.getPreviousRole());
    }

    @ParameterizedTest
    @EnumSource(
            value = UserRoles.class,
            names = {"USER", "ADMIN"}
    )
    void shouldDeactivateUserAndPreservePreviousRole(UserRoles originalRole)
    {
        User user = new User(1L, "Pedro", "123", originalRole);

        user.deactivate();

        assertEquals(UserRoles.NO_ACCESS, user.getRole());
        assertEquals(originalRole, user.getPreviousRole());
        assertThat(user.getAuthorities()).isEmpty();
    }

    @Test
    void shouldNotOverwritePreviousRoleWhenUserIsAlreadyDeactivated()
    {
        User user = new User(1L, "Pedro", "123", UserRoles.ADMIN);

        user.deactivate();
        UserWithInvalidInformationException exception = assertThrows(UserWithInvalidInformationException.class, user::deactivate) ;

        assertEquals(UserRoles.NO_ACCESS, user.getRole());
        assertEquals(UserRoles.ADMIN, user.getPreviousRole());
        assertEquals("Usuário já está desativado.", exception.getMessage());
    }

    @ParameterizedTest
    @EnumSource(
            value = UserRoles.class,
            names = {"USER", "ADMIN"}
    )
    void shouldReactivateUserRestoringPreviousRole(UserRoles originalRole)
    {
        User user = new User(1L, "Pedro", "123", originalRole);
        user.deactivate();

        user.reactivate();

        assertEquals(originalRole, user.getRole());
        assertNull(user.getPreviousRole());
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotDeactivated()
    {
        User user = new User(1L, "Pedro", "123", UserRoles.USER);

        UserWithInvalidInformationException exception = assertThrows(UserWithInvalidInformationException.class, user::reactivate);

        assertEquals("Usuário não está desativado.", exception.getMessage());
        assertEquals(UserRoles.USER, user.getRole());
        assertNull(user.getPreviousRole());
    }

    @Test
    void shouldThrowExceptionWhenUserHasNoValidPreviousRole()
    {
        User user = new User(1L, "Pedro", "123", UserRoles.NO_ACCESS);

        UserWithInvalidInformationException exception = assertThrows(UserWithInvalidInformationException.class, user::reactivate);

        assertEquals("Usuário não tinha permissão válida.", exception.getMessage());
        assertEquals(UserRoles.NO_ACCESS, user.getRole());
        assertNull(user.getPreviousRole());
    }

    @ParameterizedTest
    @EnumSource(
            value = UserRoles.class,
            names = {"USER", "ADMIN"}
    )
    void shouldBeEnabledWhenUserHasAccess(UserRoles role)
    {
        User user = new User(1L, "Pedro", "123", role);

        assertTrue(user.isEnabled());
    }

    @Test
    void shouldBeDisabledWhenUserHasNoAccess()
    {
        User user = new User(1L, "Pedro", "123", UserRoles.NO_ACCESS);

        assertFalse(user.isEnabled());
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
                Arguments.of("Pedro", " ", UserRoles.ADMIN, "Senha de usuário não pode ser vazio."),
                Arguments.of("", "123", UserRoles.ADMIN, "Nome de usuário não pode ser vazio."),
                Arguments.of("Pedro", "", UserRoles.ADMIN, "Senha de usuário não pode ser vazio.")
        );
    }

    private static Stream<Arguments> dataProvider3()
    {
        return Stream.of(
                Arguments.of("Pe", "123", UserRoles.ADMIN, "Nome de usuário deve ter no mínimo 3 caracteres."),
                Arguments.of("Pedro", "12", UserRoles.ADMIN, "Senha de usuário não pode ter menos que 3 caracteres.")
        );
    }

    @Test
    void shouldCreateUserForCredentialsUpdateWithNewPassword()
    {
        User user = User.forCredentialsUpdate(
                "pedro.atualizado",
                "nova-senha"
        );

        assertEquals("pedro.atualizado", user.getUsername());
        assertEquals("nova-senha", user.getPassword());
        assertEquals(UserRoles.USER, user.getRole());
    }

    @Test
    void shouldCreateUserForCredentialsUpdateWithoutPassword()
    {
        User user = User.forCredentialsUpdate(
                "pedro.atualizado",
                null
        );

        assertEquals("pedro.atualizado", user.getUsername());
        assertNull(user.getPassword());
        assertEquals(UserRoles.USER, user.getRole());
    }
}
