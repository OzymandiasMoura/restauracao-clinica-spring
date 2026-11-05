package org.example.clinicarestauracao.Domain.Entities;

import org.example.clinicarestauracao.Application.Exceptions.Responsavel.ResponsavelWithInvalidInformationException;
import org.example.clinicarestauracao.Builders.EnderecoTestBuilder;
import org.example.clinicarestauracao.Builders.ResponsavelTestBuilder;
import org.example.clinicarestauracao.Domain.ValueObjects.Endereco;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ResponsavelTest
{
    // Testes dos construtores

    @Test
    void shouldCreateResponsavelWithValidInformation()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().build();

        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setNome("João da Silva").setEmail("joao.silva@email.com").setCpf("16899535009").setTelefone("11999999999").setEndereco(endereco).setCep("01001000").buildForCreate();

        assertAll(
                () -> assertNull(responsavel.getId()),
                () -> assertEquals("João da Silva", responsavel.getNome()),
                () -> assertEquals("joao.silva@email.com", responsavel.getEmail()),
                () -> assertEquals("16899535009", responsavel.getCpf()),
                () -> assertEquals("11999999999", responsavel.getTelefone()),
                () -> assertSame(endereco, responsavel.getEndereco()),
                () -> assertEquals("01001000", responsavel.getCep()),
                () -> assertTrue(responsavel.isAtivo())
        );
    }

    @Test
    void shouldRestoreInactiveResponsavelWithValidId()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel()
                .setId(10L)
                .setAtivo(false)
                .build();

        assertAll(
                () -> assertEquals(10L, responsavel.getId()),
                () -> assertFalse(responsavel.isAtivo())
        );
    }

    // Testes do ID

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldRejectInvalidId(Long id)
    {
        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> ResponsavelTestBuilder.newResponsavel().setId(id).build());

        assertEquals("Id não pode ser nulo ou menor ou igual a zero.", exception.getMessage());
    }

    // Testes do nome

    @Test
    void shouldNormalizeResponsavelName()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setNome("  João da Silva  ").buildForCreate();

        assertEquals("João da Silva", responsavel.getNome());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "\t", "\n"})
    void shouldRejectNullOrBlankName(String nome)
    {
        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> ResponsavelTestBuilder.newResponsavel().setNome(nome).buildForCreate());

        assertEquals("Nome não pode ser nulo ou vazio", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "AB", " A ", " AB "})
    void shouldRejectNameShorterThanThreeCharactersAfterNormalization(
            String nome)
    {
        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> ResponsavelTestBuilder.newResponsavel().setNome(nome).buildForCreate());

        assertEquals("Nome do responsável deve ter no mínimo 3 caracteres.", exception.getMessage());
    }

    @Test
    void shouldAcceptNameWithExactlyThreeCharacters()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setNome("Ana").buildForCreate();

        assertEquals("Ana", responsavel.getNome());
    }

    @Test
    void shouldAcceptNameWithExactlyTwoHundredAndFiftyCharacters()
    {
        String nome = "A".repeat(250);

        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setNome(nome).buildForCreate();

        assertEquals(nome, responsavel.getNome());
    }

    @Test
    void shouldRejectNameLongerThanTwoHundredAndFiftyCharacters()
    {
        String nome = "A".repeat(251);

        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> ResponsavelTestBuilder.newResponsavel().setNome(nome).buildForCreate());

        assertEquals("Nome do responsável deve ter no máximo 250 caracteres.", exception.getMessage());
    }

    @Test
    void shouldPreserveCurrentNameAfterInvalidUpdate()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setNome("João da Silva").build();

        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> responsavel.setNome("AB"));

        assertAll(
                () -> assertEquals("Nome do responsável deve ter no mínimo 3 caracteres.", exception.getMessage()),
                () -> assertEquals("João da Silva", responsavel.getNome())
        );
    }

    // Testes do email

    @Test
    void shouldNormalizeResponsavelEmail()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setEmail("  JOAO.SILVA@EMAIL.COM  ").buildForCreate();

        assertEquals("joao.silva@email.com", responsavel.getEmail());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
    void shouldRejectNullOrBlankEmail(String email)
    {
        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> ResponsavelTestBuilder.newResponsavel().setEmail(email).buildForCreate());

        assertEquals("Email do responsável não pode ser nulo ou vazio", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "joao@email",
            "joao.com",
            "joao@@email.com",
            "joao..silva@email.com",
            "@email.com",
            "joao@.com"
    })
    void shouldRejectInvalidEmail(String email)
    {
        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> ResponsavelTestBuilder.newResponsavel().setEmail(email).buildForCreate());

        assertEquals("Email é inválido.", exception.getMessage());
    }

    @Test
    void shouldAcceptEmailWithExactlyTwoHundredAndFiftyFourCharacters()
    {
        String email = "a".repeat(242) + "@example.com";

        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setEmail(email).buildForCreate();

        assertEquals(email, responsavel.getEmail());
    }

    @Test
    void shouldRejectEmailLongerThanTwoHundredAndFiftyFourCharacters()
    {
        String email = "a".repeat(243) + "@example.com";

        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> ResponsavelTestBuilder.newResponsavel().setEmail(email).buildForCreate());

        assertEquals("Email é inválido.", exception.getMessage());
    }

    @Test
    void shouldPreserveCurrentEmailAfterInvalidUpdate()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setEmail("joao.silva@email.com").build();

        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> responsavel.setEmail("email-invalido"));

        assertAll(
                () -> assertEquals("Email é inválido.", exception.getMessage()),
                () -> assertEquals("joao.silva@email.com", responsavel.getEmail())
        );
    }

    // Testes do CPF

    @ParameterizedTest
    @ValueSource(strings = {
            "168.995.350-09",
            "  168.995.350-09  ",
            "168 995 350 09"
    })
    void shouldNormalizeResponsavelCpf(String cpf)
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setCpf(cpf).buildForCreate();

        assertEquals("16899535009", responsavel.getCpf());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
    void shouldRejectNullOrBlankCpf(String cpf)
    {
        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> ResponsavelTestBuilder.newResponsavel().setCpf(cpf).buildForCreate());

        assertEquals("CPF do responsável não pode ser nulo ou vazio", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "16899535008",
            "11111111111",
            "123",
            "168995350090",
            "1689953500A"
    })
    void shouldRejectInvalidCpf(String cpf)
    {
        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> ResponsavelTestBuilder.newResponsavel().setCpf(cpf).buildForCreate());

        assertEquals("CPF inválido.", exception.getMessage());
    }

    @Test
    void shouldPreserveCurrentCpfAfterInvalidUpdate()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setCpf("16899535009").build();

        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> responsavel.setCpf("16899535008")
        );

        assertAll(
                () -> assertEquals("CPF inválido.", exception.getMessage()),
                () -> assertEquals("16899535009", responsavel.getCpf())
        );
    }

    // Testes do telefone

    @ParameterizedTest
    @CsvSource({
            "'(11) 99999-9999', '11999999999'",
            "'(11) 3333-4444', '1133334444'",
            "'  11999999999  ', '11999999999'"
    })
    void shouldNormalizeResponsavelTelefone(String telefone, String telefoneEsperado)
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setTelefone(telefone).buildForCreate();

        assertEquals(telefoneEsperado, responsavel.getTelefone());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
    void shouldRejectNullOrBlankTelefone(String telefone)
    {
        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> ResponsavelTestBuilder.newResponsavel().setTelefone(telefone).buildForCreate());

        assertEquals("Telefone não pode ser nulo ou vazio", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "119999999",
            "119999999999",
            "11899999999",
            "1199999999",
            "+55 (11) 99999-9999",
            "1199999999A"
    })
    void shouldRejectInvalidTelefone(String telefone)
    {
        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> ResponsavelTestBuilder.newResponsavel().setTelefone(telefone).buildForCreate());

        assertEquals("Telefone inválido.", exception.getMessage());
    }

    @Test
    void shouldPreserveCurrentTelefoneAfterInvalidUpdate()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setTelefone("11999999999").build();

        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> responsavel.setTelefone("119999999"));

        assertAll(
                () -> assertEquals("Telefone inválido.", exception.getMessage()),
                () -> assertEquals("11999999999", responsavel.getTelefone())
        );
    }

    // Testes do endereço

    @Test
    void shouldAcceptValidEndereco()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().build();
        Endereco novoEndereco = EnderecoTestBuilder.newEndereco().setLogradouro("Avenida Paulista").setNumero(1000).setBairro("Bela Vista").build();

        responsavel.setEndereco(novoEndereco);

        assertSame(novoEndereco, responsavel.getEndereco());
    }

    @Test
    void shouldRejectNullEndereco()
    {
        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> ResponsavelTestBuilder.newResponsavel().setEndereco(null).buildForCreate());

        assertEquals("Endereço do responsável não pode ser nulo.", exception.getMessage());
    }

    @Test
    void shouldPreserveCurrentEnderecoAfterNullUpdate()
    {
        Endereco enderecoAtual = EnderecoTestBuilder.newEndereco().build();
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setEndereco(enderecoAtual).build();

        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> responsavel.setEndereco(null));

        assertAll(
                () -> assertEquals("Endereço do responsável não pode ser nulo.", exception.getMessage()),
                () -> assertSame(enderecoAtual, responsavel.getEndereco())
        );
    }

    // Testes do CEP

    @ParameterizedTest
    @CsvSource({
            "'01001-000', '01001000'",
            "'  01001-000  ', '01001000'",
            "'01001000', '01001000'"
    })
    void shouldNormalizeResponsavelCep(String cep, String cepEsperado)
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setCep(cep).buildForCreate();

        assertEquals(cepEsperado, responsavel.getCep());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
    void shouldRejectNullOrBlankCep(String cep)
    {
        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> ResponsavelTestBuilder.newResponsavel().setCep(cep).buildForCreate());

        assertEquals("CEP não pode ser nulo ou vazio.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "0100100",
            "010010000",
            "01001A00",
            "01001.000",
            "abcdefgh"
    })
    void shouldRejectInvalidCep(String cep)
    {
        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> ResponsavelTestBuilder.newResponsavel().setCep(cep).buildForCreate());

        assertEquals("CEP inválido.", exception.getMessage());
    }

    @Test
    void shouldPreserveCurrentCepAfterInvalidUpdate()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setCep("01001000").build();

        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> responsavel.setCep("01001A00"));

        assertAll(
                () -> assertEquals("CEP inválido.", exception.getMessage()),
                () -> assertEquals("01001000", responsavel.getCep())
        );
    }

    // Testes de desativação e reativação

    @Test
    void shouldDeactivateActiveResponsavel()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().buildForCreate();

        responsavel.deactivateResponsavel();

        assertFalse(responsavel.isAtivo());
    }

    @Test
    void shouldRejectDeactivationOfInactiveResponsavel()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setAtivo(false).build();

        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, responsavel::deactivateResponsavel);

        assertAll(
                () -> assertEquals("Responsável já está desativado.", exception.getMessage()),
                () -> assertFalse(responsavel.isAtivo())
        );
    }

    @Test
    void shouldReactivateInactiveResponsavel()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setAtivo(false).build();

        responsavel.reactivateResponsavel();

        assertTrue(responsavel.isAtivo());
    }

    @Test
    void shouldRejectReactivationOfActiveResponsavel()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().buildForCreate();

        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, responsavel::reactivateResponsavel);

        assertAll(
                () -> assertEquals("Responsável já está ativado.", exception.getMessage()),
                () -> assertTrue(responsavel.isAtivo())
        );
    }
}
