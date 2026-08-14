package org.example.clinicarestauracao.Application.Exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest
{
    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void shouldReturnUnauthorizedWhenCredentialsAreInvalid()
    {
        var exception = new BadCredentialsException("mensagem interna");

        var response = handler.handleBadCredentialsException(exception);

        assertEquals(401, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("mensagem interna", response.getBody().message());
    }

    @Test
    void shouldReturnConflictWhenUsernameIsInUse()
    {
        var exception = new UsernameAlredyInUseException("mensagem interna");

        var response = handler.handleUsernameAlreadyInUse(exception);

        assertEquals(409, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("mensagem interna", response.getBody().message());
    }


}