package org.example.clinicarestauracao.Domain.Entities;


import org.junit.jupiter.api.DisplayName;
import org.example.clinicarestauracao.Application.Exceptions.Convenio.ConvenioWithInvalidInformationException;
import org.example.clinicarestauracao.Builders.ConvenioTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ConvenioTest
{
    ConvenioTestBuilder builder = new ConvenioTestBuilder();

    @DisplayName("Deve criar um convênio válido")
    @Test
    void shouldCreateValidConvenio()
    {
        Convenio convenio = builder.build();

        assertEquals(1L, convenio.getId());
        assertEquals("Convênio", convenio.getDescricao());
        assertEquals("11222333000181", convenio.getCnpj());
        assertEquals(20, convenio.getMaxVagas());
        assertTrue(convenio.isAtivo());
        assertTrue(convenio.isPagamento());
    }

    @DisplayName("Deve criar novo convênio como ativo")
    @Test
    void shouldCreateNewConvenioAsActive()
    {
        Convenio convenio = new Convenio(builder.getDescricao(), builder.getCnpj(), builder.getMaxVagas(), builder.isPagamento(), builder.getCor());

        assertTrue(convenio.isAtivo());
    }

    @DisplayName("Deve preservar ativo e pagamento valores")
    @Test
    void shouldPreserveActiveAndPaymentValues()
    {
        Convenio convenio = new ConvenioTestBuilder().setAtivo(false).setPagamento(true).build();

        assertFalse(convenio.isAtivo());
        assertTrue(convenio.isPagamento());
    }

    @DisplayName("Deve normalizar fornecido CNPJ")
    @Test
    void shouldNormalizeProvidedCnpj()
    {
        Convenio convenio = new ConvenioTestBuilder().setCNPJ("11.222.333/0001-81").build();

        assertEquals("11222333000181", convenio.getCnpj());
    }

    @DisplayName("Deve rejeitar CNPJ fornecido inválido")
    @ParameterizedTest
    @ValueSource(strings = {
            "123",
            "11.222.333/0001-82",
            "CNPJ inválido"
    })
    void shouldRejectInvalidProvidedCnpj(String cnpj)
    {
        ConvenioWithInvalidInformationException exception = assertThrows(ConvenioWithInvalidInformationException.class, () -> new Convenio(builder.getDescricao(), cnpj, builder.getMaxVagas(), builder.isPagamento(),  builder.getCor()));

        assertEquals("CNPJ é invalido.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar ausente descrição")
    @Test
    void shouldRejectMissingDescription()
    {
        ConvenioWithInvalidInformationException exception = assertThrows(ConvenioWithInvalidInformationException.class, () -> new Convenio(null, builder.getCnpj(), builder.getMaxVagas(), builder.isPagamento(),  builder.getCor()));

        assertEquals("Descrição não pode ser nulo ou vazia", exception.getMessage());
    }

    @DisplayName("Deve rejeitar em branco descrição")
    @Test
    void shouldRejectBlankDescription()
    {
        ConvenioWithInvalidInformationException exception = assertThrows(ConvenioWithInvalidInformationException.class, () -> new Convenio(" ", builder.getCnpj(), builder.getMaxVagas(), builder.isPagamento(),   builder.getCor()));

        assertEquals("Descrição não pode ser nulo ou vazia", exception.getMessage());
    }

    @DisplayName("Deve rejeitar descrição com menor que três caracteres")
    @Test
    void shouldRejectDescriptionWithLessThanThreeCharacters()
    {
        ConvenioWithInvalidInformationException exception = assertThrows(ConvenioWithInvalidInformationException.class, () -> new Convenio("as", builder.getCnpj(), builder.getMaxVagas(), builder.isPagamento(), builder.getCor()));

        assertEquals("Descrição não pode ter menos de 3 caracteres", exception.getMessage());
    }

    @DisplayName("Deve aceitar zero como número máximo de vagas")
    @Test
    void shouldAcceptZeroAsMaximumVacancies()
    {
        Convenio convenio = new ConvenioTestBuilder().setMaxVagas(0).build();

        assertEquals(0, convenio.getMaxVagas());
    }

    @DisplayName("Deve rejeitar número máximo de vagas negativo")
    @Test
    void shouldRejectNegativeMaximumVacancies()
    {
        ConvenioWithInvalidInformationException exception = assertThrows(ConvenioWithInvalidInformationException.class, () -> new Convenio(builder.getDescricao(), builder.getCnpj(), -1, builder.isPagamento(),  builder.getCor()));

        assertEquals("Numero de vagas deve ser 0 ou mais.", exception.getMessage());
    }

    @DisplayName("Deve normalizar hexadecimal cor")
    @Test
    void shouldNormalizeHexadecimalColor()
    {
        Convenio convenio = new ConvenioTestBuilder().setCor("#abcdef").build();

        assertEquals("#ABCDEF", convenio.getCor());
    }

    @DisplayName("Deve lançar exceção quando cor é inválido")
    @Test
    void shouldThrowExceptionWhenColorIsInvalid()
    {
        assertThrows(ConvenioWithInvalidInformationException.class, () -> new ConvenioTestBuilder().setCor("azul").build());
    }

    @DisplayName("Deve rejeitar cor inválido")
    @ParameterizedTest
    @ValueSource(strings = {"FFFFFF", "#FFFF", "#FFFFFFFF", "#GGGGGG", "azul"})
    void shouldRejectInvalidColor(String cor)
    {
        ConvenioWithInvalidInformationException exception = assertThrows(ConvenioWithInvalidInformationException.class, () -> new ConvenioTestBuilder().setCor(cor).build());

        assertEquals("Cor deve estar no formato hexadecimal #RRGGBB.", exception.getMessage());
    }
    @DisplayName("Deve rejeitar ausente cor")
    @ParameterizedTest
    @ValueSource(strings = {"   "})
    void shouldRejectMissingColor(String cor)
    {
        ConvenioWithInvalidInformationException exception = assertThrows(ConvenioWithInvalidInformationException.class, () -> new ConvenioTestBuilder().setCor(cor).build());

        assertEquals("Cor não pode ser nula ou vazia.", exception.getMessage());
    }

    @DisplayName("Deve remover espaços iniciais e finais de descrição")
    @Test
    void shouldNormalizeLeadingAndTrailingSpacesFromDescricao()
    {
        Convenio convenio = new ConvenioTestBuilder().setDescricao("  Pilates  ").build();

        assertEquals("Pilates", convenio.getDescricao());
    }

    @DisplayName("Deve rejeitar descrição com menor que três caracteres após a normalização")
    @Test
    void shouldRejectDescricaoWithLessThanThreeCharactersAfterNormalization()
    {
        ConvenioWithInvalidInformationException exception = assertThrows(ConvenioWithInvalidInformationException.class, () -> new ConvenioTestBuilder().setDescricao("  ab  ").build());

        assertEquals("Descrição não pode ter menos de 3 caracteres", exception.getMessage());
    }

    @DisplayName("Deve rejeitar número máximo de vagas nulo")
    @Test
    void shouldRejectNullMaximumVacancies()
    {
        ConvenioWithInvalidInformationException exception = assertThrows(
                ConvenioWithInvalidInformationException.class,
                () -> new Convenio(
                        builder.getDescricao(),
                        builder.getCnpj(),
                        null,
                        builder.isPagamento(),
                        builder.getCor()
                )
        );

        assertEquals("Numero de vagas não pode ser nulo.", exception.getMessage());
    }

}
