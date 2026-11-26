package org.example.clinicarestauracao.Domain.Validation;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TelefoneValidatorTest
{
    //Testes para validate

    @DisplayName("Deve aceitar telefone válido")
    @ParameterizedTest
    @ValueSource(strings = {
            "11999999999",
            "(11) 99999-9999",
            "1133334444",
            "(11) 3333-4444"
    })
    void shouldAcceptValidTelefone(String telefone)
    {
        assertTrue(TelefoneValidator.validate(telefone));
    }

    @DisplayName("Deve rejeitar telefone inválido")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            " ",
            "119999999",
            "119999999999",
            "11899999999",
            "1199999999",
            "+55 (11) 99999-9999",
            "1199999999A",
            "11.99999.9999",
            "11/99999/9999"
    })
    void shouldRejectInvalidTelefone(String telefone)
    {
        assertFalse(TelefoneValidator.validate(telefone));
    }

    //Testes para normalize

    @DisplayName("Deve normalizar telefone")
    @ParameterizedTest
    @CsvSource({
            "'(11) 99999-9999', 11999999999",
            "'(11) 3333-4444', 1133334444",
            "'  11999999999  ', 11999999999"
    })
    void shouldNormalizeTelefone(String telefone, String esperado)
    {
        assertEquals(esperado, TelefoneValidator.normalize(telefone));
    }

    @DisplayName("Deve preservar caracteres que pertencer para validação")
    @Test
    void shouldPreserveCharactersThatBelongToValidation()
    {
        assertEquals("+5511999999999", TelefoneValidator.normalize("+55 (11) 99999-9999"));
    }

    @DisplayName("Deve retornar texto vazio quando normalizando em branco valor")
    @Test
    void shouldReturnEmptyStringWhenNormalizingBlankValue()
    {
        assertEquals("", TelefoneValidator.normalize("   "));
    }

    @DisplayName("Deve retornar nulo ao normalizar uma entrada nula")
    @Test
    void shouldReturnNullWhenNormalizingNull()
    {
        assertNull(TelefoneValidator.normalize(null));
    }
}
