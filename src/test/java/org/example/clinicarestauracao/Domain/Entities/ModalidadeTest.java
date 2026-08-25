package org.example.clinicarestauracao.Domain.Entities;

import org.example.clinicarestauracao.Application.Exceptions.ModalidadeWithInvalidInformationException;
import org.example.clinicarestauracao.Builders.ModalidadeTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ModalidadeTest
{
    ModalidadeTestBuilder builder = new ModalidadeTestBuilder();

    @Test
    void shouldCreateValidModalidade()
    {
        Modalidade modalidade = builder.build();

        assertEquals(1L, modalidade.getId());
        assertEquals("Modalidade", modalidade.getDescricao());
        assertEquals("11222333000181", modalidade.getCnpj());
        assertEquals(20, modalidade.getMaxVagas());
        assertTrue(modalidade.isAtivo());
        assertTrue(modalidade.isPagamento());
    }

    @Test
    void shouldCreateNewModalidadeAsActive()
    {
        Modalidade modalidade = new Modalidade(builder.getDescricao(), builder.getCnpj(), builder.getMaxVagas(), builder.isPagamento(), builder.getCor());

        assertTrue(modalidade.isAtivo());
    }

    @Test
    void shouldPreserveActiveAndPaymentValues()
    {
        Modalidade modalidade = new ModalidadeTestBuilder().setAtivo(false).setPagamento(true).build();

        assertFalse(modalidade.isAtivo());
        assertTrue(modalidade.isPagamento());
    }

    @Test
    void shouldNormalizeProvidedCnpj()
    {
        Modalidade modalidade = new ModalidadeTestBuilder().setCNPJ("11.222.333/0001-81").build();

        assertEquals("11222333000181", modalidade.getCnpj());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123",
            "11.222.333/0001-82",
            "CNPJ inválido"
    })
    void shouldRejectInvalidProvidedCnpj(String cnpj)
    {
        ModalidadeWithInvalidInformationException exception = assertThrows(ModalidadeWithInvalidInformationException.class, () -> new Modalidade(builder.getDescricao(), cnpj, builder.getMaxVagas(), builder.isPagamento(),  builder.getCor()));

        assertEquals("CNPJ é invalido.", exception.getMessage());
    }

    @Test
    void shouldRejectMissingDescription()
    {
        ModalidadeWithInvalidInformationException exception = assertThrows(ModalidadeWithInvalidInformationException.class, () -> new Modalidade(null, builder.getCnpj(), builder.getMaxVagas(), builder.isPagamento(),  builder.getCor()));

        assertEquals("Descrição não pode ser nulo ou vazia", exception.getMessage());
    }

    @Test
    void shouldRejectBlankDescription()
    {
        ModalidadeWithInvalidInformationException exception = assertThrows(ModalidadeWithInvalidInformationException.class, () -> new Modalidade(" ", builder.getCnpj(), builder.getMaxVagas(), builder.isPagamento(),   builder.getCor()));

        assertEquals("Descrição não pode ser nulo ou vazia", exception.getMessage());
    }

    @Test
    void shouldRejectDescriptionWithLessThanThreeCharacters()
    {
        ModalidadeWithInvalidInformationException exception = assertThrows(ModalidadeWithInvalidInformationException.class, () -> new Modalidade("as", builder.getCnpj(), builder.getMaxVagas(), builder.isPagamento(), builder.getCor()));

        assertEquals("Descrição não pode ter menos de 3 caracteres", exception.getMessage());
    }

    @Test
    void shouldAcceptZeroAsMaximumVacancies()
    {
        Modalidade modalidade = new ModalidadeTestBuilder().setMaxVagas(0).build();

        assertEquals(0, modalidade.getMaxVagas());
    }

    @Test
    void shouldRejectNegativeMaximumVacancies()
    {
        ModalidadeWithInvalidInformationException exception = assertThrows(ModalidadeWithInvalidInformationException.class, () -> new Modalidade(builder.getDescricao(), builder.getCnpj(), -1, builder.isPagamento(),  builder.getCor()));

        assertEquals("Numero de vagas deve ser 0 ou mais.", exception.getMessage());
    }

    @Test
    void shouldNormalizeHexadecimalColor()
    {
        Modalidade modalidade = new ModalidadeTestBuilder().setCor("#abcdef").build();

        assertEquals("#ABCDEF", modalidade.getCor());
    }

    @Test
    void shouldThrowExceptionWhenColorIsInvalid()
    {
        assertThrows(ModalidadeWithInvalidInformationException.class, () -> new ModalidadeTestBuilder().setCor("azul").build());
    }

    @ParameterizedTest
    @ValueSource(strings = {"FFFFFF", "#FFFF", "#FFFFFFFF", "#GGGGGG", "azul"})
    void shouldRejectInvalidColor(String cor)
    {
        ModalidadeWithInvalidInformationException exception = assertThrows(ModalidadeWithInvalidInformationException.class, () -> new ModalidadeTestBuilder().setCor(cor).build());

        assertEquals("Cor deve estar no formato hexadecimal #RRGGBB.", exception.getMessage());
    }
    @ParameterizedTest
    @ValueSource(strings = {"   "})
    void shouldRejectMissingColor(String cor)
    {
        ModalidadeWithInvalidInformationException exception = assertThrows(ModalidadeWithInvalidInformationException.class, () -> new ModalidadeTestBuilder().setCor(cor).build());

        assertEquals("Cor não pode ser nula ou vazia.", exception.getMessage());
    }
}