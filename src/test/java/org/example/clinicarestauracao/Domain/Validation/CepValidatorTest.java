package org.example.clinicarestauracao.Domain.Validation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CepValidatorTest
{
    @ParameterizedTest
    @ValueSource(strings = {
            "12345678",
            "12345-678",
            "  12345-678  "
    })
    void shouldAcceptValidCep(String cep)
    {
        assertTrue(CepValidator.validate(cep));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            " ",
            "1234567",
            "123456789",
            "12345-67A",
            "12345 678",
            "12345@678"
    })
    void shouldRejectInvalidCep(String cep)
    {
        assertFalse(CepValidator.validate(cep));
    }
}