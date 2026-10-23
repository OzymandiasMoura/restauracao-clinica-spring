package org.example.clinicarestauracao.Domain.Validation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CpfValidatorTest
{
    @ParameterizedTest
    @ValueSource(strings = {
            "529.982.247-25",
            "52998224725",
            " 529.982.247-25 "
    })
    void shouldAcceptValidCpf(String cpf)
    {
        assertTrue(CpfValidator.validate(cpf));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            " ",
            "123",
            "52998224724",
            "00000000000",
            "11111111111",
            "529.982.247-AA",
            "529@982@247-25"
    })
    void shouldRejectInvalidCpf(String cpf)
    {
        assertFalse(CpfValidator.validate(cpf));
    }
}