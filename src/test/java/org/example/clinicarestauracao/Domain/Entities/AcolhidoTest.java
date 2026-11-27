package org.example.clinicarestauracao.Domain.Entities;


import org.junit.jupiter.api.DisplayName;
import org.example.clinicarestauracao.Application.Exceptions.Acolhido.AcolhidoWithInvalidInformationException;
import org.example.clinicarestauracao.Builders.AcolhidoTestBuilder;
import org.example.clinicarestauracao.Builders.EnderecoTestBuilder;
import org.example.clinicarestauracao.Builders.ResponsavelTestBuilder;
import org.example.clinicarestauracao.Domain.Enums.Etnia;
import org.example.clinicarestauracao.Domain.ValueObjects.Endereco;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AcolhidoTest
{
    // Testes para nome

    @DisplayName("Deve normalizar nome")
    @ParameterizedTest
    @CsvSource({"'   Maria Silva   ', 'Maria Silva'", "' Ana', 'Ana'", "'João ', 'João'"})
    void shouldNormalizeName(String receivedName, String expectedName)
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setNome(receivedName).build();

        assertEquals(expectedName, acolhido.getNome());
    }

    @DisplayName("Deve rejeitar nome nulo ou em branco")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void shouldRejectNullOrBlankName(String nome)
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setNome(nome).build());

        assertEquals("Nome do acolhido não pode ser nulo ou vazio.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar nome com menos de três caracteres após a normalização")
    @ParameterizedTest
    @ValueSource(strings = {"A", "Pe", "  Pe  "})
    void shouldRejectNameWithLessThanThreeCharactersAfterNormalization(String nome)
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setNome(nome).build());

        assertEquals("Nome do acolhido deve ter no mínimo 3 caracteres.", exception.getMessage());
    }

    @DisplayName("Deve aceitar nome em permitido comprimento limites")
    @ParameterizedTest
    @ValueSource(ints = {3, 250})
    void shouldAcceptNameAtAllowedLengthLimits(int length)
    {
        String nome = "A".repeat(length);

        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setNome(nome).build();

        assertEquals(nome, acolhido.getNome());
    }

    @DisplayName("Deve rejeitar nome com mais de 250 caracteres após a normalização")
    @Test
    void shouldRejectNameWithMoreThan250CharactersAfterNormalization()
    {
        String nome = "  " + "A".repeat(251) + "  ";

        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setNome(nome).build());

        assertEquals("Nome do acolhido deve ter no máximo 250 caracteres.", exception.getMessage());
    }

    // Testes para e-mail

    @DisplayName("Deve normalizar e-mail")
    @Test
    void shouldNormalizeEmail()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setEmail("  MARIA@EMAIL.COM  ").build();

        assertEquals("maria@email.com", acolhido.getEmail());
    }

    @DisplayName("Deve aceitar e-mail nulo")
    @Test
    void shouldAcceptNullEmail()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setEmail(null).buildForCreate();

        assertNull(acolhido.getEmail());
    }

    @DisplayName("Deve rejeitar e-mail vazio ou em branco")
    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "   "})
    void shouldRejectBlankEmail(String email)
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setEmail(email).build());

        assertEquals("Email do acolhido não pode ser vazio.", exception.getMessage());
    }

    // Testes construtores Acolhido
    @DisplayName("Deve definir a data de criação ao criar um acolhido")
    @Test
    void shouldSetCreationDateWhenCreatingAcolhido()
    {
        LocalDateTime beforeCreation = LocalDateTime.now();

        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().buildForCreate();

        LocalDateTime afterCreation = LocalDateTime.now();

        assertNotNull(acolhido.getDataCriacao());
        assertFalse(acolhido.getDataCriacao().isBefore(beforeCreation));
        assertFalse(acolhido.getDataCriacao().isAfter(afterCreation));
    }

    @DisplayName("Deve preservar a data de criação ao recuperar um acolhido")
    @Test
    void shouldPreserveCreationDateWhenRecoveringAcolhido()
    {
        LocalDateTime creationDate = LocalDateTime.of(2020, 1, 2, 3, 4);

        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido()
                .setDataCriacao(creationDate)
                .build();

        assertEquals(creationDate, acolhido.getDataCriacao());
    }

    @DisplayName("Deve preservar o estado inativo ao recuperar um acolhido")
    @Test
    void shouldPreserveInactiveStatusWhenRecoveringAcolhido()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido()
                .setAtivo(false)
                .build();

        assertFalse(acolhido.getAtivo());
    }

    @DisplayName("Deve rejeitar ID nulo ao recuperar um acolhido")
    @Test
    void shouldRejectNullIdWhenRecoveringAcolhido()
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(
                AcolhidoWithInvalidInformationException.class,
                () -> AcolhidoTestBuilder.newAcolhido().setId(null).build());

        assertEquals("Id inválido.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar ID menor ou igual a zero ao recuperar um acolhido")
    @ParameterizedTest
    @ValueSource(longs = {0, -1})
    void shouldRejectNonPositiveIdWhenRecoveringAcolhido(Long id)
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(
                AcolhidoWithInvalidInformationException.class,
                () -> AcolhidoTestBuilder.newAcolhido().setId(id).build());

        assertEquals("Id inválido.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar data de criação nula ao recuperar um acolhido")
    @Test
    void shouldRejectNullCreationDateWhenRecoveringAcolhido()
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(
                AcolhidoWithInvalidInformationException.class,
                () -> AcolhidoTestBuilder.newAcolhido().setDataCriacao(null).build());

        assertEquals("Data de criação do acolhido deve ser informada.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar estado ativo nulo ao recuperar um acolhido")
    @Test
    void shouldRejectNullActiveStateWhenRecoveringAcolhido()
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(
                AcolhidoWithInvalidInformationException.class,
                () -> AcolhidoTestBuilder.newAcolhido().setAtivo(null).build());

        assertEquals("Acolhido não pode ter estado de ativo nulo.", exception.getMessage());
    }

    @DisplayName("Deve preservar os dados demográficos ao recuperar um acolhido")
    @Test
    void shouldPreserveDemographicDataWhenRecoveringAcolhido()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido()
                .setEtnia(Etnia.PRETO)
                .setGenero("Masculino")
                .setOrientacaoSexual("Bissexual")
                .build();

        assertEquals(Etnia.PRETO, acolhido.getEtnia());
        assertEquals("Masculino", acolhido.getGenero());
        assertEquals("Bissexual", acolhido.getOrientacaoSexual());
    }

    @DisplayName("Deve aplicar os valores não informados ao criar um acolhido")
    @Test
    void shouldApplyNotInformedDefaultsWhenCreatingAcolhido()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido()
                .setEtnia(null)
                .setGenero(null)
                .setOrientacaoSexual(null)
                .buildForCreate();

        assertEquals(Etnia.NAO_INFORMADO, acolhido.getEtnia());
        assertEquals("Não informado.", acolhido.getGenero());
        assertEquals("Não informado.", acolhido.getOrientacaoSexual());
    }

    @DisplayName("Deve rejeitar e-mail inválido")
    @ParameterizedTest
    @ValueSource(strings = {"maria@email", "maria.com", "maria@@email.com", "maria..silva@email.com"})
    void shouldRejectInvalidEmail(String email)
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setEmail(email).build());

        assertEquals("Email do acolhido está invalido.", exception.getMessage());
    }

    // Testes para CPF

    @DisplayName("Deve normalizar CPF")
    @ParameterizedTest
    @CsvSource({"'529.982.247-25', '52998224725'", "'  529.982.247-25  ', '52998224725'", "'529 982 247 25', '52998224725'"})
    void shouldNormalizeCpf(String receivedCpf, String expectedCpf)
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setCpf(receivedCpf).build();

        assertEquals(expectedCpf, acolhido.getCpf());
    }

    @DisplayName("Deve rejeitar CPF nulo ou em branco")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void shouldRejectNullOrBlankCpf(String cpf)
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setCpf(cpf).build());

        assertEquals("CPF do acolhido não pode ser nulo ou vazio.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar CPF inválido")
    @ParameterizedTest
    @ValueSource(strings = {"529.982.247-24", "111.111.111-11", "123"})
    void shouldRejectInvalidCpf(String cpf)
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setCpf(cpf).build());

        assertEquals("CPF do acolhido está invalido.", exception.getMessage());
    }


// Testes para CEP

    @DisplayName("Deve normalizar CEP")
    @ParameterizedTest
    @CsvSource({"'01310-100', '01310100'", "'  01310-100  ', '01310100'", "'01310100', '01310100'"})
    void shouldNormalizeCep(String receivedCep, String expectedCep)
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setCep(receivedCep).build();

        assertEquals(expectedCep, acolhido.getCep());
    }

    @DisplayName("Deve rejeitar CEP nulo quando o acolhido não está em situação de rua")
    @Test
    void shouldRejectNullCepWhenAcolhidoHasFixedAddress()
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(
                AcolhidoWithInvalidInformationException.class,
                () -> AcolhidoTestBuilder.newAcolhido().setCep(null).build());

        assertEquals("CEP e endereço não podem ser nulos enquanto o acolhido não se encontra em situação de rua.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar CEP em branco quando o acolhido não está em situação de rua")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void shouldRejectBlankCepWhenAcolhidoHasFixedAddress(String cep)
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(
                AcolhidoWithInvalidInformationException.class,
                () -> AcolhidoTestBuilder.newAcolhido().setCep(cep).build());

        assertEquals("CEP do acolhido está inválido.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar CEP inválido")
    @ParameterizedTest
    @ValueSource(strings = {"1234567", "123456789", "12345-67A", "12345 678", "12345@678"})
    void shouldRejectInvalidCep(String cep)
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setCep(cep).build());

        assertEquals("CEP do acolhido está inválido.", exception.getMessage());
    }

    @DisplayName("Deve aceitar CEP nulo para acolhido em situação de rua")
    @Test
    void shouldAllowNullCepWhenAcolhidoLivesOnStreet()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido()
                .setMoradorRua(true)
                .setCep(null)
                .setEndereco(null)
                .build();

        assertNull(acolhido.getCep());
        assertTrue(acolhido.getMoradorRua());
    }

    // Testes para endereço

    @DisplayName("Deve definir endereço com sucesso")
    @Test
    void shouldSetEnderecoSuccessfully()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().build();

        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setEndereco(endereco).build();

        assertEquals(endereco, acolhido.getEndereco());
    }

    @DisplayName("Deve rejeitar endereço nulo")
    @Test
    void shouldRejectNullEndereco()
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setEndereco(null).build());

        assertEquals("CEP e endereço não podem ser nulos enquanto o acolhido não se encontra em situação de rua.", exception.getMessage());
    }

    @DisplayName("Deve aceitar endereço nulo para acolhido em situação de rua")
    @Test
    void shouldAllowNullEnderecoWhenAcolhidoLivesOnStreet()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido()
                .setMoradorRua(true)
                .setCep(null)
                .setEndereco(null)
                .build();

        assertNull(acolhido.getEndereco());
        assertTrue(acolhido.getMoradorRua());
    }

    // Testes updateHousingStatus
    @DisplayName("Deve atualizar moradia de acolhido em situação de rua")
    @Test
    void shouldUpdateHousingStatusForAcolhidoLivingOnStreet()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        acolhido.updateHousingStatus(true, null, null);

        assertTrue(acolhido.getMoradorRua());
        assertNull(acolhido.getCep());
        assertNull(acolhido.getEndereco());
    }

    @DisplayName("Deve atualizar moradia de acolhido com endereço e CEP")
    @Test
    void shouldUpdateHousingStatusForAcolhidoWithAddress()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido()
                .setMoradorRua(true)
                .setCep(null)
                .setEndereco(null)
                .build();
        Endereco endereco = EnderecoTestBuilder.newEndereco().build();

        acolhido.updateHousingStatus(false, "01310-100", endereco);

        assertFalse(acolhido.getMoradorRua());
        assertEquals("01310100", acolhido.getCep());
        assertEquals(endereco, acolhido.getEndereco());
    }

    @DisplayName("Deve rejeitar estado de moradia nulo")
    @Test
    void shouldRejectNullHousingStatus()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        assertThrows(AcolhidoWithInvalidInformationException.class,
                () -> acolhido.updateHousingStatus(null, "01310100", EnderecoTestBuilder.newEndereco().build()));
    }

    @DisplayName("Deve rejeitar CEP ou endereço informados para acolhido em situação de rua")
    @Test
    void shouldRejectAddressForAcolhidoLivingOnStreet()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        assertThrows(AcolhidoWithInvalidInformationException.class,
                () -> acolhido.updateHousingStatus(true, "01310100", null));
    }

    @DisplayName("Deve rejeitar CEP ou endereço ausentes quando acolhido não está em situação de rua")
    @Test
    void shouldRejectMissingAddressForAcolhidoNotLivingOnStreet()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        assertThrows(AcolhidoWithInvalidInformationException.class,
                () -> acolhido.updateHousingStatus(false, null, EnderecoTestBuilder.newEndereco().build()));
    }

    @DisplayName("Deve preservar a moradia atual quando o novo CEP é inválido")
    @Test
    void shouldPreserveHousingStatusWhenNewCepIsInvalid()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();
        String cepAtual = acolhido.getCep();
        Endereco enderecoAtual = acolhido.getEndereco();

        assertThrows(AcolhidoWithInvalidInformationException.class,
                () -> acolhido.updateHousingStatus(false, "1234567", EnderecoTestBuilder.newEndereco().build()));

        assertFalse(acolhido.getMoradorRua());
        assertEquals(cepAtual, acolhido.getCep());
        assertEquals(enderecoAtual, acolhido.getEndereco());
    }

    // Testes setTelefone

    @DisplayName("Deve normalizar telefone")
    @ParameterizedTest
    @CsvSource({"'(11) 99999-9999', '11999999999'", "'(11) 3333-4444', '1133334444'", "'  11999999999  ', '11999999999'"})
    void shouldNormalizeTelefone(String receivedTelefone, String expectedTelefone)
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setTelefone(receivedTelefone).build();

        assertEquals(expectedTelefone, acolhido.getTelefone());
    }

    @DisplayName("Deve aceitar telefone nulo")
    @Test
    void shouldAcceptNullTelefone()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        acolhido.setTelefone(null);

        assertNull(acolhido.getTelefone());
    }

    @DisplayName("Deve rejeitar telefone em branco")
    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "   "})
    void shouldRejectBlankTelefone(String telefone)
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setTelefone(telefone).build());

        assertEquals("Telefone do acolhido não pode ser nulo ou vazio.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar telefone inválido")
    @ParameterizedTest
    @ValueSource(strings = {"119999999", "119999999999", "11899999999", "1199999999", "+55 (11) 99999-9999", "1199999999A"})
    void shouldRejectInvalidTelefone(String telefone)
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setTelefone(telefone).build());

        assertEquals("Telefone do acolhido está invalido.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar telefone igual ao telefone 2 após normalização")
    @Test
    void shouldRejectTelefoneEqualToTelefone2AfterNormalization()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido()
                .setTelefone2("1133334444")
                .build();

        AcolhidoWithInvalidInformationException exception = assertThrows(
                AcolhidoWithInvalidInformationException.class,
                () -> acolhido.setTelefone("(11) 3333-4444"));

        assertEquals("Telefone não pode ser igual ao telefone 2.", exception.getMessage());
        assertEquals("11999999999", acolhido.getTelefone());
    }

    // Testes setTelefone2

    @DisplayName("Deve normalizar telefone 2")
    @ParameterizedTest
    @CsvSource({"'(11) 3333-4444', '1133334444'", "'  1133334444  ', '1133334444'"})
    void shouldNormalizeTelefone2(String receivedTelefone, String expectedTelefone)
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido()
                .setTelefone2(receivedTelefone)
                .build();

        assertEquals(expectedTelefone, acolhido.getTelefone2());
    }

    @DisplayName("Deve aceitar telefone 2 nulo")
    @Test
    void shouldAcceptNullTelefone2()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido()
                .setTelefone2("1133334444")
                .build();

        acolhido.setTelefone2(null);

        assertNull(acolhido.getTelefone2());
    }

    @DisplayName("Deve rejeitar telefone 2 em branco")
    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "   "})
    void shouldRejectBlankTelefone2(String telefone2)
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        AcolhidoWithInvalidInformationException exception = assertThrows(
                AcolhidoWithInvalidInformationException.class,
                () -> acolhido.setTelefone2(telefone2));

        assertEquals("Telefone do acolhido não pode ser nulo ou vazio.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar telefone 2 inválido")
    @ParameterizedTest
    @ValueSource(strings = {"113333444", "11333344444", "1183334444", "+55 (11) 3333-4444", "1133334444A"})
    void shouldRejectInvalidTelefone2(String telefone2)
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        AcolhidoWithInvalidInformationException exception = assertThrows(
                AcolhidoWithInvalidInformationException.class,
                () -> acolhido.setTelefone2(telefone2));

        assertEquals("Telefone do acolhido está invalido.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar telefone 2 igual ao telefone após normalização")
    @Test
    void shouldRejectTelefone2EqualToTelefoneAfterNormalization()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido()
                .setTelefone2("1133334444")
                .build();

        AcolhidoWithInvalidInformationException exception = assertThrows(
                AcolhidoWithInvalidInformationException.class,
                () -> acolhido.setTelefone2("(11) 99999-9999"));

        assertEquals("Telefone 2 não pode ser igual ao telefone.", exception.getMessage());
        assertEquals("1133334444", acolhido.getTelefone2());
    }

    // Testes updateTelefones

    @DisplayName("Deve normalizar e atualizar os dois telefones")
    @Test
    void shouldNormalizeAndUpdateBothTelefones()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        acolhido.updateTelefones("(11) 3333-4444", "(11) 99999-9999");

        assertEquals("1133334444", acolhido.getTelefone());
        assertEquals("11999999999", acolhido.getTelefone2());
    }

    @DisplayName("Deve permitir trocar os telefones principal e secundário")
    @Test
    void shouldAllowSwappingPrimaryAndSecondaryTelefones()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido()
                .setTelefone("11999999999")
                .setTelefone2("1133334444")
                .build();

        acolhido.updateTelefones("1133334444", "11999999999");

        assertEquals("1133334444", acolhido.getTelefone());
        assertEquals("11999999999", acolhido.getTelefone2());
    }

    @DisplayName("Deve permitir telefone principal nulo e manter telefone 2")
    @Test
    void shouldAllowNullPrimaryTelefoneWhenUpdatingTelefones()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        acolhido.updateTelefones(null, "(11) 3333-4444");

        assertNull(acolhido.getTelefone());
        assertEquals("1133334444", acolhido.getTelefone2());
    }

    @DisplayName("Deve permitir telefone 2 nulo e manter telefone principal")
    @Test
    void shouldAllowNullSecondaryTelefoneWhenUpdatingTelefones()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        acolhido.updateTelefones("(11) 3333-4444", null);

        assertEquals("1133334444", acolhido.getTelefone());
        assertNull(acolhido.getTelefone2());
    }

    @DisplayName("Deve limpar os dois telefones quando ambos forem nulos")
    @Test
    void shouldClearBothTelefonesWhenUpdatingWithNullValues()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido()
                .setTelefone("11999999999")
                .setTelefone2("1133334444")
                .build();

        acolhido.updateTelefones(null, null);

        assertNull(acolhido.getTelefone());
        assertNull(acolhido.getTelefone2());
    }

    @DisplayName("Deve rejeitar telefones iguais após normalização e preservar os valores atuais")
    @Test
    void shouldRejectDuplicatedTelefonesAndPreserveCurrentValues()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido()
                .setTelefone("11999999999")
                .setTelefone2("1133334444")
                .build();

        AcolhidoWithInvalidInformationException exception = assertThrows(
                AcolhidoWithInvalidInformationException.class,
                () -> acolhido.updateTelefones("(11) 3333-4444", "1133334444"));

        assertEquals("Telefone não pode ser igual ao telefone 2.", exception.getMessage());
        assertEquals("11999999999", acolhido.getTelefone());
        assertEquals("1133334444", acolhido.getTelefone2());
    }

    @DisplayName("Deve rejeitar telefone inválido e preservar o par atual")
    @ParameterizedTest
    @CsvSource({"'119999999', '1133334444'", "'11999999999', '113333444'"})
    void shouldRejectInvalidTelefoneAndPreserveCurrentPair(String telefone, String telefone2)
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido()
                .setTelefone("11999999999")
                .setTelefone2("1133334444")
                .build();

        AcolhidoWithInvalidInformationException exception = assertThrows(
                AcolhidoWithInvalidInformationException.class,
                () -> acolhido.updateTelefones(telefone, telefone2));

        assertEquals("Telefone do acolhido está invalido.", exception.getMessage());
        assertEquals("11999999999", acolhido.getTelefone());
        assertEquals("1133334444", acolhido.getTelefone2());
    }

    @DisplayName("Deve rejeitar telefone vazio ou em branco e preservar o par atual")
    @ParameterizedTest
    @CsvSource({"'', '1133334444'", "'11999999999', '   '"})
    void shouldRejectBlankTelefoneAndPreserveCurrentPair(String telefone, String telefone2)
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido()
                .setTelefone("11999999999")
                .setTelefone2("1133334444")
                .build();

        AcolhidoWithInvalidInformationException exception = assertThrows(
                AcolhidoWithInvalidInformationException.class,
                () -> acolhido.updateTelefones(telefone, telefone2));

        assertEquals("Telefone do acolhido não pode ser nulo ou vazio.", exception.getMessage());
        assertEquals("11999999999", acolhido.getTelefone());
        assertEquals("1133334444", acolhido.getTelefone2());
    }

    // Testes para data de nascimento

    @DisplayName("Deve definir uma data de nascimento válida")
    @Test
    void shouldSetValidBirthDate()
    {
        LocalDate dataNascimento = LocalDate.of(1990, 1, 10);

        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setDataNascimento(dataNascimento).build();

        assertEquals(dataNascimento, acolhido.getDataNascimento());
    }

    @DisplayName("Deve rejeitar data de nascimento nula")
    @Test
    void shouldRejectNullBirthDate()
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setDataNascimento(null).build());

        assertEquals("É necessário definir a data de nascimento.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar data de nascimento futura")
    @Test
    void shouldRejectFutureBirthDate()
    {
        LocalDate futureDate = LocalDate.now().plusDays(1);

        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setDataNascimento(futureDate).build());

        assertEquals("Data de nascimento não pode ser futura.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar data de nascimento anterior a 1900")
    @Test
    void shouldRejectBirthDateBefore1900()
    {
        LocalDate birthDate = LocalDate.of(1899, 12, 31);

        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setDataNascimento(birthDate).build());

        assertEquals("Data de nascimento não pode ser anterior a 1900.", exception.getMessage());
    }

    @DisplayName("Deve aceitar a data mínima de nascimento permitida")
    @Test
    void shouldAcceptBirthDateAtMinimumLimit()
    {
        LocalDate birthDate = LocalDate.of(1900, 1, 1);

        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setDataNascimento(birthDate).build();

        assertEquals(birthDate, acolhido.getDataNascimento());
    }

    @DisplayName("Deve rejeitar a data de nascimento quando o acolhido tem menos de 18 anos")
    @Test
    void shouldRejectBirthDateWhenAcolhidoIsUnderEighteen()
    {
        LocalDate birthDate = LocalDate.now();

        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> AcolhidoTestBuilder.newAcolhido().setDataNascimento(birthDate).build());

        assertEquals("Acolhido não pode ter menos que 18 anos.", exception.getMessage());
    }

    // Testes setEtnia

    @DisplayName("Deve definir a etnia informada")
    @Test
    void shouldSetEtnia()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        acolhido.setEtnia(Etnia.PARDO);

        assertEquals(Etnia.PARDO, acolhido.getEtnia());
    }

    @DisplayName("Deve usar não informado quando a etnia for nula")
    @Test
    void shouldDefaultNullEtniaToNotInformed()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido()
                .setEtnia(null)
                .build();

        assertEquals(Etnia.NAO_INFORMADO, acolhido.getEtnia());
    }

    // Testes setGenero

    @DisplayName("Deve normalizar gênero removendo espaços externos")
    @Test
    void shouldNormalizeGenero()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        acolhido.setGenero("  Feminino  ");

        assertEquals("Feminino", acolhido.getGenero());
    }

    @DisplayName("Deve usar não informado quando gênero for nulo ou em branco")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void shouldDefaultNullOrBlankGeneroToNotInformed(String genero)
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        acolhido.setGenero(genero);

        assertEquals("Não informado.", acolhido.getGenero());
    }

    @DisplayName("Deve rejeitar gênero com menos de três caracteres após normalização")
    @ParameterizedTest
    @ValueSource(strings = {"M", "XY", "  XY  "})
    void shouldRejectGeneroWithLessThanThreeCharactersAfterNormalization(String genero)
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        AcolhidoWithInvalidInformationException exception = assertThrows(
                AcolhidoWithInvalidInformationException.class,
                () -> acolhido.setGenero(genero));

        assertEquals("Gênero deve ter pelo menos 3 caracteres.", exception.getMessage());
    }

    // Testes setOrientacaoSexual

    @DisplayName("Deve normalizar orientação sexual removendo espaços externos")
    @Test
    void shouldNormalizeOrientacaoSexual()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        acolhido.setOrientacaoSexual("  Heterossexual  ");

        assertEquals("Heterossexual", acolhido.getOrientacaoSexual());
    }

    @DisplayName("Deve usar não informado quando orientação sexual for nula ou em branco")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void shouldDefaultNullOrBlankOrientacaoSexualToNotInformed(String orientacaoSexual)
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        acolhido.setOrientacaoSexual(orientacaoSexual);

        assertEquals("Não informado.", acolhido.getOrientacaoSexual());
    }

    @DisplayName("Deve rejeitar orientação sexual com menos de três caracteres após normalização")
    @ParameterizedTest
    @ValueSource(strings = {"X", "XY", "  XY  "})
    void shouldRejectOrientacaoSexualWithLessThanThreeCharactersAfterNormalization(String orientacaoSexual)
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        AcolhidoWithInvalidInformationException exception = assertThrows(
                AcolhidoWithInvalidInformationException.class,
                () -> acolhido.setOrientacaoSexual(orientacaoSexual));

        assertEquals("Orientação sexual deve ter pelo menos 3 caracteres.", exception.getMessage());
    }

    // Testes para responsável

    @DisplayName("Deve definir ativo responsável")
    @Test
    void shouldSetActiveResponsavel()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().build();

        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setResponsavel(responsavel).buildForCreate();

        assertSame(responsavel, acolhido.getResponsavel());
    }

    @DisplayName("Deve rejeitar responsável nulo")
    @Test
    void shouldRejectNullResponsavel()
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(
                AcolhidoWithInvalidInformationException.class,
                () -> AcolhidoTestBuilder.newAcolhido().setResponsavel(null).build()
        );

        assertEquals("Responsável do acolhido não pode ser nulo.", exception.getMessage());
    }

    @DisplayName("Deve aceitar responsável inativo ao recuperar o acolhido")
    @Test
    void shouldAllowInactiveResponsavelWhenRecoveringAcolhido()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setAtivo(false).build();

        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido()
                .setResponsavel(responsavel)
                .build();

        assertSame(responsavel, acolhido.getResponsavel());
        assertFalse(acolhido.getResponsavel().isAtivo());
    }

    @DisplayName("Deve rejeitar responsável inativo ao criar o acolhido")
    @Test
    void shouldRejectInactiveResponsavelWhenCreatingAcolhido()
    {
        Responsavel responsavelInativo = ResponsavelTestBuilder.newResponsavel().setAtivo(false).build();

        AcolhidoWithInvalidInformationException exception = assertThrows(
                AcolhidoWithInvalidInformationException.class,
                () -> AcolhidoTestBuilder.newAcolhido()
                        .setResponsavel(responsavelInativo)
                        .buildForCreate()
        );

        assertEquals("Responsável do acolhido não pode ser inativo.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar responsável nulo ao criar o acolhido")
    @Test
    void shouldRejectNullResponsavelWhenCreatingAcolhido()
    {
        AcolhidoWithInvalidInformationException exception = assertThrows(
                AcolhidoWithInvalidInformationException.class,
                () -> AcolhidoTestBuilder.newAcolhido()
                        .setResponsavel(null)
                        .buildForCreate()
        );

        assertEquals("Responsável do acolhido não pode ser nulo.", exception.getMessage());
    }

    @DisplayName("Deve preservar responsável atual quando a substituição recebida é nula")
    @Test
    void shouldPreserveCurrentResponsavelWhenReplacementIsNull()
    {
        Responsavel responsavelAtual = ResponsavelTestBuilder.newResponsavel().build();
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido()
                .setResponsavel(responsavelAtual)
                .build();

        assertThrows(AcolhidoWithInvalidInformationException.class,
                () -> acolhido.setResponsavel(null));

        assertSame(responsavelAtual, acolhido.getResponsavel());
    }

    // Testes setGrauParentesco

    @DisplayName("Deve definir o grau de parentesco")
    @Test
    void shouldSetGrauParentesco()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        acolhido.setGrauParentesco("Filha");

        assertEquals("Filha", acolhido.getGrauParentesco());
    }

    @DisplayName("Deve rejeitar grau de parentesco nulo ou em branco")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void shouldRejectNullOrBlankGrauParentesco(String grauParentesco)
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        AcolhidoWithInvalidInformationException exception = assertThrows(
                AcolhidoWithInvalidInformationException.class,
                () -> acolhido.setGrauParentesco(grauParentesco));

        assertEquals("O gau de parentesco entre acolhido e responsável não pode ser nulo ou vazio.", exception.getMessage());
    }

    // Testes para estado ativo

    @DisplayName("Deve criar ativo acolhido com ID")
    @Test
    void shouldCreateActiveAcolhidoWithId()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        assertTrue(acolhido.getAtivo());
    }

    @DisplayName("Deve criar ativo acolhido sem ID")
    @Test
    void shouldCreateActiveAcolhidoWithoutId()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().buildForCreate();

        assertTrue(acolhido.getAtivo());
    }

    @DisplayName("Deve desativar acolhido")
    @Test
    void shouldDeactivateAcolhido()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        acolhido.deactivate();

        assertFalse(acolhido.getAtivo());
    }

    @DisplayName("Deve reativar acolhido")
    @Test
    void shouldReactivateAcolhido()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        acolhido.deactivate();
        acolhido.reactivate();

        assertTrue(acolhido.getAtivo());
    }

    @DisplayName("Deve rejeitar desativando já inativo acolhido")
    @Test
    void shouldRejectDeactivatingAlreadyInactiveAcolhido()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        acolhido.deactivate();

        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, acolhido::deactivate);

        assertEquals("Acolhido já desativado.", exception.getMessage());
        assertFalse(acolhido.getAtivo());
    }

    @DisplayName("Deve rejeitar reativando já ativo acolhido")
    @Test
    void shouldRejectReactivatingAlreadyActiveAcolhido()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, acolhido::reactivate);

        assertEquals("Acolhido já ativado.", exception.getMessage());
        assertTrue(acolhido.getAtivo());
    }
}
