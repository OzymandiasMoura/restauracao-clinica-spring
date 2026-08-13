package org.example.clinicarestauracao.Infrastruct.Security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.example.clinicarestauracao.Domain.Entities.User;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TokenService
{
    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user)
    {
        try
        {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create().withIssuer("auth-api").withSubject(user.getUsername()).withExpiresAt(generateExpirationDate()).sign(algorithm);
            return token;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Erro ao gerar o token JWT",e);
        }
    }

    public String validateToken(String token)
    {
        try
        {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm).withIssuer("auth-api").build().verify(token).getSubject();
        }
        catch (Exception e)
        {
            return "";
        }
    }

    private Instant generateExpirationDate()
    {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-3"));
    }
}
