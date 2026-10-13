package org.example.clinicarestauracao.Domain.Entities;

import org.example.clinicarestauracao.Application.Exceptions.Acolhido.AcolhidoWithInvalidInformationException;
import org.example.clinicarestauracao.Builders.AcolhidoTestBuilder;
import org.example.clinicarestauracao.Builders.EnderecoTestBuilder;
import org.example.clinicarestauracao.Domain.ValueObjects.Endereco;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AcolhidoTest
{
    // Testes para nome

    @ParameterizedTest
    @CsvSource({"'   Maria Silva   ', 'Maria Silva'", "' Ana', 'Ana'", "'João ', 'João'"})
    void shouldNormalizeName(String receivedName, String expectedName)
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setNome(receivedName).build();

        assertEquals(expectedName, acolhido.getNome());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void shouldRejectNullOrBlankName(String nome)
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setNome(nome).build());

        assertEquals("Nome do acolhido não pode ser nulo ou vazio.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "Pe", "  Pe  "})
    void shouldRejectNameWithLessThanThreeCharactersAfterNormalization(String nome)
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setNome(nome).build());

        assertEquals("Nome do acolhido deve ter no mínimo 3 caracteres.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 250})
    void shouldAcceptNameAtAllowedLengthLimits(int length)
    {
        String nome = "A".repeat(length);

        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setNome(nome).build();

        assertEquals(nome, acolhido.getNome());
    }

    @Test
    void shouldRejectNameWithMoreThan250CharactersAfterNormalization()
    {
        String nome = "  " + "A".repeat(251) + "  ";

        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setNome(nome).build());

        assertEquals("Nome do acolhido deve ter no máximo 250 caracteres.", exception.getMessage());
    }

    // Testes para e-mail

    @Test
    void shouldNormalizeEmail()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setEmail("  MARIA@EMAIL.COM  ").build();

        assertEquals("maria@email.com", acolhido.getEmail());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void shouldRejectNullOrBlankEmail(String email)
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setEmail(email).build());

        assertEquals("Email do acolhido não pode ser nulo ou vazio.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"maria@email", "maria.com", "maria@@email.com", "maria..silva@email.com"})
    void shouldRejectInvalidEmail(String email)
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setEmail(email).build());

        assertEquals("Email do acolhido está invalido.", exception.getMessage());
    }

    // Testes para CPF

    @ParameterizedTest
    @CsvSource({"'529.982.247-25', '52998224725'", "'  529.982.247-25  ', '52998224725'", "'529 982 247 25', '52998224725'"})
    void shouldNormalizeCpf(String receivedCpf, String expectedCpf)
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setCpf(receivedCpf).build();

        assertEquals(expectedCpf, acolhido.getCpf());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void shouldRejectNullOrBlankCpf(String cpf)
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setCpf(cpf).build());

        assertEquals("CPF do acolhido não pode ser nulo ou vazio.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"529.982.247-24", "111.111.111-11", "123"})
    void shouldRejectInvalidCpf(String cpf)
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setCpf(cpf).build());

        assertEquals("CPF do acolhido está invalido.", exception.getMessage());
    }


// Testes para CEP

    @ParameterizedTest
    @CsvSource({"'01310-100', '01310100'", "'  01310-100  ', '01310100'", "'01310100', '01310100'"})
    void shouldNormalizeCep(String receivedCep, String expectedCep)
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setCep(receivedCep).build();

        assertEquals(expectedCep, acolhido.getCep());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void shouldRejectNullOrBlankCep(String cep)
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setCep(cep).build());

        assertEquals("CEP do acolhido não pode ser nulo ou vazio.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234567", "123456789", "12345-67A", "12345 678", "12345@678"})
    void shouldRejectInvalidCep(String cep)
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setCep(cep).build());

        assertEquals("CEP do acolhido está invalido.", exception.getMessage());
    }

    // Testes para endereço

    @Test
    void shouldSetEnderecoSuccessfully()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().build();

        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setEndereco(endereco).build();

        assertEquals(endereco, acolhido.getEndereco());
    }

    @Test
    void shouldRejectNullEndereco()
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setEndereco(null).build());

        assertEquals("Endereço do acolhido não pode ser nulo ou vazio.", exception.getMessage());
    }

    // Testes para telefone

    @ParameterizedTest
    @CsvSource({"'(11) 99999-9999', '11999999999'", "'(11) 3333-4444', '1133334444'", "'  11999999999  ', '11999999999'"})
    void shouldNormalizeTelefone(String receivedTelefone, String expectedTelefone)
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setTelefone(receivedTelefone).build();

        assertEquals(expectedTelefone, acolhido.getTelefone());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void shouldRejectNullOrBlankTelefone(String telefone)
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setTelefone(telefone).build());

        assertEquals("Telefone do acolhido não pode ser nulo ou vazio.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"119999999", "119999999999", "11899999999", "1199999999", "+55 (11) 99999-9999", "1199999999A"})
    void shouldRejectInvalidTelefone(String telefone)
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setTelefone(telefone).build());

        assertEquals("Telefone do acolhido está invalido.", exception.getMessage());
    }

    // Testes para data de nascimento

    @Test
    void shouldSetValidBirthDate()
    {
        LocalDate dataNascimento = LocalDate.of(1990, 1, 10);

        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setDataNascimento(dataNascimento).build();

        assertEquals(dataNascimento, acolhido.getDataNascimento());
    }

    @Test
    void shouldRejectNullBirthDate()
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setDataNascimento(null).build());

        assertEquals("É necessário definir a data de nascimento.", exception.getMessage());
    }

    @Test
    void shouldRejectFutureBirthDate()
    {
        LocalDate futureDate = LocalDate.now().plusDays(1);

        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setDataNascimento(futureDate).build());

        assertEquals("Data de nascimento não pode ser futura.", exception.getMessage());
    }

    @Test
    void shouldRejectBirthDateBefore1900()
    {
        LocalDate birthDate = LocalDate.of(1899, 12, 31);

        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setDataNascimento(birthDate).build());

        assertEquals("Data de nascimento não pode ser anterior a 1900.", exception.getMessage());
    }

    @Test
    void shouldAcceptBirthDateAtMinimumLimit()
    {
        LocalDate birthDate = LocalDate.of(1900, 1, 1);

        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setDataNascimento(birthDate).build();

        assertEquals(birthDate, acolhido.getDataNascimento());
    }

    @Test
    void shouldAcceptCurrentDateAsBirthDate()
    {
        LocalDate birthDate = LocalDate.now();

        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setDataNascimento(birthDate).build();

        assertEquals(birthDate, acolhido.getDataNascimento());
    }
}
