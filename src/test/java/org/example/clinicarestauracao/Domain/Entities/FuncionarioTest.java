package org.example.clinicarestauracao.Domain.Entities;

import org.example.clinicarestauracao.Application.Exceptions.FuncionarioWithInvalidInformationException;
import org.example.clinicarestauracao.Builders.FuncionarioTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioTest
{
    //Testes para o nome

    @ParameterizedTest
    @CsvSource({
            "'       Pedro      ', 'Pedro'",
            "'  Pedro Moura  ', 'Pedro Moura'",
            "'Pedro Moura', 'Pedro Moura'"
    })
    void shouldNormalizeLeadingAndTrailingSpaces(String nome, String nomeEsperado)
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setNome(nome).build();

        assertEquals(nomeEsperado, funcionario.getNome());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void shouldRejectNullOrBlankName(String nome)
    {
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setNome(nome).build());

        assertEquals("Funcionário não pode ter o nome vazio ou em branco.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "Pe"})
    void shouldRejectNameWithLessThanThreeCharacters(String nome)
    {
        var exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setNome(nome).build());

        assertEquals("Nome deve ter no mínimo 3 caracteres.", exception.getMessage());
    }

    @Test
    void shouldCreateFuncionarioWithValidName()
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setNome("Pedro Moura").build();

        assertEquals("Pedro Moura", funcionario.getNome());
    }

    @Test
    void shouldRejectShortNameAfterNormalization()
    {
        var exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setNome("  Pe  ").build());

        assertEquals("Nome deve ter no mínimo 3 caracteres.", exception.getMessage());
    }

    //Testes para o CPF

    @Test
    void shouldNormalizeCpf()
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setCpf("529.982.247-25").build();

        assertEquals("52998224725", funcionario.getCpf());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {" ", ""})
    void shouldRejectNullOrBlankCpf(String cpf)
    {
        var exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setCpf(cpf).build());

        assertEquals("CPF não pode ser vazio ou em branco.", exception.getMessage());
    }

    @Test
    void shouldRejectInvalidCpf()
    {
        FuncionarioWithInvalidInformationException ex = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setCpf("529.982.247-24").build());

        assertEquals("CPF é inválido.", ex.getMessage());
    }

}