package org.example.clinicarestauracao.Domain.Entities;

import org.example.clinicarestauracao.Application.Exceptions.FuncionarioWithInvalidInformationException;
import org.example.clinicarestauracao.Builders.FuncionarioTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

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
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setNome(nome).build());

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
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setNome("  Pe  ").build());

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
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setCpf(cpf).build());

        assertEquals("CPF não pode ser vazio ou em branco.", exception.getMessage());
    }

    @Test
    void shouldRejectInvalidCpf()
    {
        FuncionarioWithInvalidInformationException ex = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setCpf("529.982.247-24").build());

        assertEquals("CPF é inválido.", ex.getMessage());
    }

    //Testes de email

    @Test
    void shouldNormalizeEmail()
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setEmail("  PEDRO@EMAIL.COM  ").build();

        assertEquals("pedro@email.com", funcionario.getEmail());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void shouldRejectNullOrBlankEmail(String email)
    {
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setEmail(email).build());

        assertEquals("E-mail não pode ser vazio ou em branco.", exception.getMessage());
    }

    @Test
    void shouldRejectInvalidEmail()
    {
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setEmail("pedro@email").build());

        assertEquals("E-mail é inválido.", exception.getMessage());
    }

    //Testes para dataNascimento

    @Test
    void shouldCreateFuncionarioWithValidBirthDate()
    {
        LocalDate dataNascimento = LocalDate.of(1990, 1, 10);

        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setDataNascimento(dataNascimento).build();

        assertEquals(dataNascimento, funcionario.getDataNascimento());
    }

    @Test
    void shouldRejectNullBirthDate()
    {
        FuncionarioWithInvalidInformationException ex = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setDataNascimento(null).build());

        assertEquals("É necessário definir a data de nascimento.", ex.getMessage());
    }

    @Test
    void shouldRejectFutureBirthDate()
    {
        LocalDate dataFutura = LocalDate.now().plusDays(1);

        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setDataNascimento(dataFutura).build());

        assertEquals("Data de nascimento não pode ser futura.", exception.getMessage());
    }

    //Testes de endereço

    @Test
    void shouldCreateFuncionarioWithValidAddress()
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setEndereco("Praça da Sé, 1 - São Paulo - SP").build();

        assertEquals("Praça da Sé, 1 - São Paulo - SP", funcionario.getEndereco());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void shouldRejectNullOrBlankAddress(String endereco)
    {
        FuncionarioWithInvalidInformationException exception = assertThrows(FuncionarioWithInvalidInformationException.class, () -> FuncionarioTestBuilder.newFuncionario().setEndereco(endereco).build());

        assertEquals("Endereço não pode ser nulo ou vazio.", exception.getMessage());
    }

    @Test
    void shouldNormalizeLeadingAndTrailingSpacesFromAddress()
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setEndereco("   Praça da Sé, 1 - São Paulo - SP   ").build();

        assertEquals("Praça da Sé, 1 - São Paulo - SP", funcionario.getEndereco());
    }


}