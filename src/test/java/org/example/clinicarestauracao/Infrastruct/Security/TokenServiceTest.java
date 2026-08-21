package org.example.clinicarestauracao.Infrastruct.Security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.clinicarestauracao.Builders.UserTestBuilder;
import org.example.clinicarestauracao.Domain.Entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest
{
    UserTestBuilder builder = new UserTestBuilder();
    private final String secret = "SECRET";
    private TokenService tokenService;
    private User user;

    @BeforeEach
    void setUp()
    {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", secret);

        user = (User) builder.build();
    }

    @Test
    void shouldGenerateTokenWithUserASSubject()
    {
        String token = tokenService.generateToken(user);
        assertNotNull(token);
        assertFalse(token.isBlank());

        var decodedToken = JWT.decode(token);

        assertEquals(user.getUsername(), decodedToken.getSubject());
        assertEquals("auth-api", decodedToken.getIssuer());
        assertNotNull(decodedToken.getExpiresAt());
    }

    @Test
    void shouldReturnUsernameWhenTokenIsValid()
    {
        String token = tokenService.generateToken(user);

        String subject = tokenService.validateToken(token);

        assertEquals(user.getUsername(), subject);
    }

    @Test
    void shouldReturnEmptyStringWhenTokenIsInvalid()
    {
        String token = tokenService.validateToken("invalid-token");

        assertEquals("", token);
    }

    @Test
    void shouldReturnEmptyStringWhenSecretIsInvalid()
    {
        String token = JWT.create().withIssuer("auth-api").withSubject("Pedro").withExpiresAt(Instant.now().plusSeconds(3600)).sign(Algorithm.HMAC256("another-secret"));

        String subject = tokenService.validateToken(token);
        assertEquals("", subject);
    }

    @Test
    void shouldReturnEmptyStringWhenTokenIsExpired()
    {
        String token = JWT.create().withIssuer("auth-api").withSubject("Pedro").withExpiresAt(Instant.now().minusSeconds(60)).sign(Algorithm.HMAC256("another-secret"));

        String subject = tokenService.validateToken(token);

        assertEquals("", subject);
    }

    @Test
    void shouldReturnEmptyStringWhenIssuerIsInvalid()
    {
        String token = JWT.create().withIssuer("another-issuer").withSubject("Pedro").withExpiresAt(Instant.now().minusSeconds(60)).sign(Algorithm.HMAC256("another-secret"));
        String subject = tokenService.validateToken(token);
        assertEquals("", subject);
    }

    @Test
    void shouldGenerateTokenWithUserRole()
    {
        User user = (User) builder.build();

        String token = tokenService.generateToken(user);

        DecodedJWT decodedToken = JWT.require(Algorithm.HMAC256(secret)).withIssuer("auth-api").build().verify(token);

        assertEquals(user.getRole().name(), decodedToken.getClaim("role").asString());
    }

}