package org.example.clinicarestauracao.Domain.Validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CnpjValidatorTest
{
    @ParameterizedTest
    @ValueSource(strings = {
            "11.222.333/0001-81",
            "11222333000181",
            "12.ABC.345/01DE-35",
            "12ABC34501DE35",
            "12.abc.345/01de-35"
    })
    void shouldAcceptValidCnpj(String cnpj)
    {
        boolean result = CnpjValidator.validate(cnpj);

        assertTrue(result);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            " ",
            "00000000000000",
            "11111111111111",
            "11.222.333/0001-82",
            "12.ABC.345/01DE-36",
            "12.ABC.345/01DE-AA",
            "12@ABC@345@01DE@35",
            "123"
    })
    void shouldRejectInvalidCnpj(String cnpj)
    {
        boolean result = CnpjValidator.validate(cnpj);

        assertFalse(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1122233300018",
            "112223330001811"
    })
    void shouldRejectCnpjWithInvalidLength(String cnpj)
    {
        assertThat(CnpjValidator.validate(cnpj)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "11222333000101", // primeiro dígito alterado
            "11222333000180", // segundo dígito alterado
            "12ABC34501DE45", // primeiro DV alfanumérico alterado
            "12ABC34501DE36"  // segundo DV alfanumérico alterado
    })
    void shouldRejectCnpjWithInvalidCheckDigits(String cnpj)
    {
        assertThat(CnpjValidator.validate(cnpj)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12ABC34501DEA5",
            "12ABC34501DE3A",
            "12ABC34501DEAA"
    })
    void shouldRejectLettersInCheckDigits(String cnpj)
    {
        assertThat(CnpjValidator.validate(cnpj)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12@ABC34501DE35",
            "12ABC#34501DE35",
            "12ABC34501DE!35",
            "12_ABC_345_01DE_35"
    })
    void shouldRejectUnexpectedSpecialCharacters(String cnpj)
    {
        assertThat(CnpjValidator.validate(cnpj)).isFalse();
    }

    @Test
    void shouldAcceptLowercaseLetters()
    {
        assertTrue(CnpjValidator.validate("12.abc.345/01de-35"));
    }

    @ParameterizedTest
    @CsvSource({
            "'11.222.333/0001-81', '11222333000181'",
            "'12.ABC.345/01DE-35', '12ABC34501DE35'",
            "'12.abc.345/01de-35', '12ABC34501DE35'",
            "' 11.222.333/0001-81 ', '11222333000181'"
    })
    void shouldNormalizeCnpj(String input, String expected)
    {
        assertThat(CnpjValidator.normalize(input)).isEqualTo(expected);
    }

    @Test
    void shouldReturnNullWhenNormalizingNull()
    {
        assertThat(CnpjValidator.normalize(null)).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            ".../--",
            "   ",
            "./-"
    })
    void shouldRejectInputContainingOnlyFormattingCharacters(String cnpj)
    {
        assertThat(CnpjValidator.validate(cnpj)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "00000000000000",
            "11111111111111",
            "22222222222222",
            "99999999999999"
    })
    void shouldRejectRepeatedNumericSequences(String cnpj)
    {
        assertThat(CnpjValidator.validate(cnpj)).isFalse();
    }

    @Test
    void shouldAcceptCnpjWithSpaces()
    {
        assertThat(CnpjValidator.validate("11 222 333 0001 81"))
                .isTrue();
    }
}