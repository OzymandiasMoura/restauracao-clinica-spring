package org.example.clinicarestauracao.Domain.Entities;


import org.junit.jupiter.api.DisplayName;
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

    @DisplayName("Deve criar responsável com informações válidas")
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

    @DisplayName("Deve reativar o responsável inativo com ID válido")
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

    @DisplayName("Deve rejeitar ID inválido")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldRejectInvalidId(Long id)
    {
        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> ResponsavelTestBuilder.newResponsavel().setId(id).build());

        assertEquals("Id não pode ser nulo ou menor ou igual a zero.", exception.getMessage());
    }

    // Testes do nome

    @DisplayName("Deve normalizar o nome do responsável")
    @Test
    void shouldNormalizeResponsavelName()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setNome("  João da Silva  ").buildForCreate();

        assertEquals("João da Silva", responsavel.getNome());
    }

    @DisplayName("Deve rejeitar nome nulo ou em branco")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "\t", "\n"})
    void shouldRejectNullOrBlankName(String nome)
    {
        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> ResponsavelTestBuilder.newResponsavel().setNome(nome).buildForCreate());

        assertEquals("Nome não pode ser nulo ou vazio", exception.getMessage());
    }

    @DisplayName("Deve rejeitar nome menor que três caracteres após a normalização")
    @ParameterizedTest
    @ValueSource(strings = {"A", "AB", " A ", " AB "})
    void shouldRejectNameShorterThanThreeCharactersAfterNormalization(
            String nome)
    {
        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> ResponsavelTestBuilder.newResponsavel().setNome(nome).buildForCreate());

        assertEquals("Nome do responsável deve ter no mínimo 3 caracteres.", exception.getMessage());
    }

    @DisplayName("Deve aceitar nome com exatamente três caracteres")
    @Test
    void shouldAcceptNameWithExactlyThreeCharacters()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setNome("Ana").buildForCreate();

        assertEquals("Ana", responsavel.getNome());
    }

    @DisplayName("Deve aceitar nome com exatamente 250 caracteres")
    @Test
    void shouldAcceptNameWithExactlyTwoHundredAndFiftyCharacters()
    {
        String nome = "A".repeat(250);

        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setNome(nome).buildForCreate();

        assertEquals(nome, responsavel.getNome());
    }

    @DisplayName("Deve rejeitar nome com mais de 250 caracteres")
    @Test
    void shouldRejectNameLongerThanTwoHundredAndFiftyCharacters()
    {
        String nome = "A".repeat(251);

        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> ResponsavelTestBuilder.newResponsavel().setNome(nome).buildForCreate());

        assertEquals("Nome do responsável deve ter no máximo 250 caracteres.", exception.getMessage());
    }

    @DisplayName("Deve preservar o nome atual após uma atualização inválida")
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

    @DisplayName("Deve normalizar responsável e-mail")
    @Test
    void shouldNormalizeResponsavelEmail()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setEmail("  JOAO.SILVA@EMAIL.COM  ").buildForCreate();

        assertEquals("joao.silva@email.com", responsavel.getEmail());
    }

    @DisplayName("Deve rejeitar e-mail nulo ou em branco")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
    void shouldRejectNullOrBlankEmail(String email)
    {
        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> ResponsavelTestBuilder.newResponsavel().setEmail(email).buildForCreate());

        assertEquals("Email do responsável não pode ser nulo ou vazio", exception.getMessage());
    }

    @DisplayName("Deve rejeitar e-mail inválido")
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

    @DisplayName("Deve aceitar e-mail com exatamente 254 caracteres")
    @Test
    void shouldAcceptEmailWithExactlyTwoHundredAndFiftyFourCharacters()
    {
        String email = "a".repeat(242) + "@example.com";

        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setEmail(email).buildForCreate();

        assertEquals(email, responsavel.getEmail());
    }

    @DisplayName("Deve rejeitar e-mail com mais de 254 caracteres")
    @Test
    void shouldRejectEmailLongerThanTwoHundredAndFiftyFourCharacters()
    {
        String email = "a".repeat(243) + "@example.com";

        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> ResponsavelTestBuilder.newResponsavel().setEmail(email).buildForCreate());

        assertEquals("Email é inválido.", exception.getMessage());
    }

    @DisplayName("Deve preservar o e-mail atual após uma atualização inválida")
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

    @DisplayName("Deve normalizar responsável CPF")
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

    @DisplayName("Deve rejeitar CPF nulo ou em branco")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
    void shouldRejectNullOrBlankCpf(String cpf)
    {
        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> ResponsavelTestBuilder.newResponsavel().setCpf(cpf).buildForCreate());

        assertEquals("CPF do responsável não pode ser nulo ou vazio", exception.getMessage());
    }

    @DisplayName("Deve rejeitar CPF inválido")
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

    @DisplayName("Deve preservar o CPF atual após uma atualização inválida")
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

    @DisplayName("Deve normalizar responsável telefone")
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

    @DisplayName("Deve rejeitar telefone nulo ou em branco")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
    void shouldRejectNullOrBlankTelefone(String telefone)
    {
        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> ResponsavelTestBuilder.newResponsavel().setTelefone(telefone).buildForCreate());

        assertEquals("Telefone não pode ser nulo ou vazio", exception.getMessage());
    }

    @DisplayName("Deve rejeitar telefone inválido")
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

    @DisplayName("Deve preservar o telefone atual após uma atualização inválida")
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

    @DisplayName("Deve aceitar endereço válido")
    @Test
    void shouldAcceptValidEndereco()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().build();
        Endereco novoEndereco = EnderecoTestBuilder.newEndereco().setLogradouro("Avenida Paulista").setNumero(1000).setBairro("Bela Vista").build();

        responsavel.setEndereco(novoEndereco);

        assertSame(novoEndereco, responsavel.getEndereco());
    }

    @DisplayName("Deve rejeitar endereço nulo")
    @Test
    void shouldRejectNullEndereco()
    {
        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> ResponsavelTestBuilder.newResponsavel().setEndereco(null).buildForCreate());

        assertEquals("Endereço do responsável não pode ser nulo.", exception.getMessage());
    }

    @DisplayName("Deve preservar o endereço atual depois nulo atualizar")
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

    @DisplayName("Deve normalizar responsável CEP")
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

    @DisplayName("Deve rejeitar CEP nulo ou em branco")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
    void shouldRejectNullOrBlankCep(String cep)
    {
        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> ResponsavelTestBuilder.newResponsavel().setCep(cep).buildForCreate());

        assertEquals("CEP não pode ser nulo ou vazio.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar CEP inválido")
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

    @DisplayName("Deve preservar atual CEP após uma atualização inválida")
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

    @DisplayName("Deve desativar ativo responsável")
    @Test
    void shouldDeactivateActiveResponsavel()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().buildForCreate();

        responsavel.deactivateResponsavel();

        assertFalse(responsavel.isAtivo());
    }

    @DisplayName("Deve rejeitar a desativação de um responsável inativo")
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

    @DisplayName("Deve reativar inativo responsável")
    @Test
    void shouldReactivateInactiveResponsavel()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setAtivo(false).build();

        responsavel.reactivateResponsavel();

        assertTrue(responsavel.isAtivo());
    }

    @DisplayName("Deve rejeitar a reativação de um responsável ativo")
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
