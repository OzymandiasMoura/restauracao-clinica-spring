package org.example.clinicarestauracao.Domain.Validation;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class EmailValidatorTest
{
    @DisplayName("Deve aceitar e-mail válido")
    @ParameterizedTest
    @ValueSource(strings = {
            "pedro@email.com",
            "pedro.moura@email.com.br",
            "pedro+moura@email.com",
            "PEDRO@EMAIL.COM"
    })
    void shouldAcceptValidEmail(String email)
    {
        assertTrue(EmailValidator.validate(email));
    }

    @DisplayName("Deve rejeitar e-mail inválido")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            " ",
            "pedro",
            "pedro@",
            "@email.com",
            "pedro@email",
            "pedro email@email.com",
            "pedro..moura@email.com",
            "pedro@email..com"
    })
    void shouldRejectInvalidEmail(String email)
    {
        assertFalse(EmailValidator.validate(email));
    }

    @DisplayName("Deve normalizar e-mail")
    @Test
    void shouldNormalizeEmail()
    {
        String result = EmailValidator.normalize("  PEDRO.MOURA@EMAIL.COM  ");

        assertEquals("pedro.moura@email.com", result);
    }

    @DisplayName("Deve retornar nulo ao normalizar uma entrada nula")
    @Test
    void shouldReturnNullWhenNormalizingNull()
    {
        assertNull(EmailValidator.normalize(null));
    }

}