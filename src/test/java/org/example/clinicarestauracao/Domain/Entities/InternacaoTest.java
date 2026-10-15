package org.example.clinicarestauracao.Domain.Entities;

import org.example.clinicarestauracao.Application.Exceptions.Internacao.InternacaoWithInvalidInformationException;
import org.example.clinicarestauracao.Builders.AcolhidoTestBuilder;
import org.example.clinicarestauracao.Builders.InternacaoTestBuilder;
import org.example.clinicarestauracao.Builders.ModalidadeTestBuilder;
import org.example.clinicarestauracao.Domain.Enums.TreatmentStage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InternacaoTest
{
    // Testes do construtor de criação

    @Test
    void shouldCreateInternacaoWithProvidedInformation()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();
        Modalidade modalidade = new ModalidadeTestBuilder().build();
        LocalDate dataEntrada = LocalDate.of(2026, 1, 10);
        LocalDate dataSaidaPrevista = dataEntrada.plusMonths(9);
        BigDecimal valorMensalidade = new BigDecimal("1200.00");

        Internacao internacao = InternacaoTestBuilder.newInternacao().setAcolhido(acolhido).setModalidade(modalidade).setDataEntrada(dataEntrada).setDataSaidaPrevista(dataSaidaPrevista).setEtapaTratamento(TreatmentStage.INICIO).setValorMensalidade(valorMensalidade).buildForCreate();

        assertAll(
                () -> assertNull(internacao.getId()),
                () -> assertSame(acolhido, internacao.getAcolhido()),
                () -> assertSame(modalidade, internacao.getModalidade()),
                () -> assertEquals(dataEntrada, internacao.getDataEntrada()),
                () -> assertEquals(dataSaidaPrevista, internacao.getDataSaidaPrevista()),
                () -> assertEquals(TreatmentStage.INICIO, internacao.getEtapaTratamento()),
                () -> assertEquals(valorMensalidade, internacao.getValorMensalidade()),
                () -> assertTrue(internacao.getAtivo())
        );
    }

    @Test
    void shouldAlwaysCreateInternacaoAsActive()
    {
        Internacao internacao = InternacaoTestBuilder.newInternacao().setAtivo(false).buildForCreate();

        assertTrue(internacao.getAtivo());
    }

    // Testes do construtor de recuperação

    @Test
    void shouldRestoreExistingInternacaoWithProvidedInformation()
    {
        Internacao internacao = InternacaoTestBuilder.newInternacao().setId(10L).setAtivo(false).build();

        assertAll(
                () -> assertEquals(10L, internacao.getId()),
                () -> assertFalse(internacao.getAtivo())
        );
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1})
    void shouldRejectInvalidId(Long id)
    {
        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> InternacaoTestBuilder.newInternacao().setId(id).build());

        assertEquals("Id da internação é inválido.", exception.getMessage());
    }

    // Testes para acolhido

    @Test
    void shouldRejectNullAcolhido()
    {
        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> InternacaoTestBuilder.newInternacao().setAcolhido(null).buildForCreate());

        assertEquals("Acolhido é obrigatório.", exception.getMessage());
    }

    @Test
    void shouldRejectInactiveAcolhidoWhenCreatingInternacao()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();
        acolhido.deactivate();

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> InternacaoTestBuilder.newInternacao().setAcolhido(acolhido).buildForCreate());

        assertEquals("Acolhido não pode estar desativado.", exception.getMessage());
    }

    @Test
    void shouldAllowInactiveAcolhidoWhenRestoringExistingInternacao()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();
        acolhido.deactivate();
        Internacao internacao = InternacaoTestBuilder.newInternacao().setAcolhido(acolhido).build();

        assertSame(acolhido, internacao.getAcolhido());
        assertFalse(internacao.getAcolhido().getAtivo());
    }

    // Testes para modalidade

    @Test
    void shouldRejectNullModalidade()
    {
        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> InternacaoTestBuilder.newInternacao().setModalidade(null).buildForCreate());

        assertEquals("Modalidade é obrigatória.", exception.getMessage());
    }

    @Test
    void shouldRejectInactiveModalidadeWhenCreatingInternacao()
    {
        Modalidade modalidade = new ModalidadeTestBuilder().setAtivo(false).build();

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> InternacaoTestBuilder.newInternacao().setModalidade(modalidade).buildForCreate());

        assertEquals("Modalidade não pode estar desativada.", exception.getMessage());
    }

    @Test
    void shouldAllowInactiveModalidadeWhenRestoringExistingInternacao()
    {
        Modalidade modalidade = new ModalidadeTestBuilder().setAtivo(false).build();

        Internacao internacao = InternacaoTestBuilder.newInternacao().setModalidade(modalidade).build();

        assertSame(modalidade, internacao.getModalidade());
        assertFalse(internacao.getModalidade().isAtivo());
    }

    // Testes para data de entrada

    @Test
    void shouldSetValidEntryDate()
    {
        LocalDate dataEntrada = LocalDate.of(2026, 1, 10);

        Internacao internacao = InternacaoTestBuilder.newInternacao().setDataEntrada(dataEntrada).buildForCreate();

        assertEquals(dataEntrada, internacao.getDataEntrada());
    }

    @Test
    void shouldAcceptCurrentDateAsEntryDate()
    {
        LocalDate dataEntrada = LocalDate.now();

        Internacao internacao = InternacaoTestBuilder.newInternacao().setDataEntrada(dataEntrada).buildForCreate();

        assertEquals(dataEntrada, internacao.getDataEntrada());
    }

    @Test
    void shouldAcceptEntryDateEqualToAcolhidoBirthDate()
    {
        LocalDate dataNascimento = LocalDate.of(2000, 1, 10);
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setDataNascimento(dataNascimento).build();
        Internacao internacao = InternacaoTestBuilder.newInternacao().setAcolhido(acolhido).setDataEntrada(dataNascimento).buildForCreate();

        assertEquals(dataNascimento, internacao.getDataEntrada());
    }

    @Test
    void shouldRejectNullEntryDate()
    {
        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> InternacaoTestBuilder.newInternacao().setDataEntrada(null).buildForCreate());

        assertEquals("Data de entrada é obrigatória.", exception.getMessage());
    }

    @Test
    void shouldRejectFutureEntryDate()
    {
        LocalDate dataEntrada = LocalDate.now().plusDays(1);

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> InternacaoTestBuilder.newInternacao().setDataEntrada(dataEntrada).buildForCreate());

        assertEquals("Data de entrada não pode ser futura.", exception.getMessage());
    }

    @Test
    void shouldRejectEntryDateBeforeAcolhidoBirthDate()
    {
        LocalDate dataNascimento = LocalDate.of(2000, 1, 10);
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setDataNascimento(dataNascimento).build();

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> InternacaoTestBuilder.newInternacao().setAcolhido(acolhido).setDataEntrada(dataNascimento.minusDays(1)).buildForCreate());

        assertEquals("Data de entrada não pode ser anterior ao nascimento do acolhido.", exception.getMessage());
    }


    // Testes para data de saída prevista

    @Test
    void shouldInitiallySetExpectedExitDateNineMonthsAfterEntryDate()
    {
        LocalDate dataEntrada = LocalDate.of(2026, 1, 10);

        Internacao internacao = InternacaoTestBuilder.newInternacao().setDataEntrada(dataEntrada).setDataSaida(dataEntrada).buildForCreate();

        assertEquals(LocalDate.of(2026, 10, 10), internacao.getDataSaidaPrevista());
    }

    @Test
    void shouldAdjustExpectedExitDateToLastValidDayOfMonth()
    {
        LocalDate dataEntrada = LocalDate.of(2026, 5, 31);

        Internacao internacao = InternacaoTestBuilder.newInternacao().setDataEntrada(dataEntrada).setDataSaida(dataEntrada).buildForCreate();

        assertEquals(LocalDate.of(2027, 2, 28), internacao.getDataSaidaPrevista());
    }

    @Test
    void shouldSetValidExpectedExitDate()
    {
        LocalDate dataEntrada = LocalDate.of(2026, 1, 10);
        LocalDate novaDataSaidaPrevista = LocalDate.of(2026, 11, 10);

        Internacao internacao = InternacaoTestBuilder.newInternacao().setDataEntrada(dataEntrada).setDataSaida(dataEntrada).build();

        internacao.setDataSaidaPrevista(novaDataSaidaPrevista);

        assertEquals(novaDataSaidaPrevista, internacao.getDataSaidaPrevista());
    }

    @Test
    void shouldAcceptExpectedExitDateEqualToEntryDate()
    {
        LocalDate dataEntrada = LocalDate.of(2026, 1, 10);

        Internacao internacao = InternacaoTestBuilder.newInternacao().setDataEntrada(dataEntrada).setDataSaidaPrevista(dataEntrada).setDataSaida(dataEntrada).build();

        assertEquals(dataEntrada, internacao.getDataSaidaPrevista());
    }

    @Test
    void shouldRejectNullExpectedExitDateAndPreserveCurrentValue()
    {
        LocalDate dataEntrada = LocalDate.of(2026, 1, 10);
        LocalDate dataSaidaPrevistaAtual = LocalDate.of(2026, 10, 10);
        Internacao internacao = InternacaoTestBuilder.newInternacao().setDataEntrada(dataEntrada).setDataSaidaPrevista(dataSaidaPrevistaAtual).setDataSaida(dataEntrada).build();

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> internacao.setDataSaidaPrevista(null));

        assertAll(
                () -> assertEquals("Data de saída prevista é obrigatória.", exception.getMessage()),
                () -> assertEquals(dataSaidaPrevistaAtual, internacao.getDataSaidaPrevista()));
    }

    @Test
    void shouldRejectExpectedExitDateBeforeEntryDateAndPreserveCurrentValue()
    {
        LocalDate dataEntrada = LocalDate.of(2026, 1, 10);
        LocalDate dataSaidaPrevistaAtual = LocalDate.of(2026, 10, 10);
        Internacao internacao = InternacaoTestBuilder.newInternacao().setDataEntrada(dataEntrada).setDataSaidaPrevista(dataSaidaPrevistaAtual).setDataSaida(dataEntrada).build();

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> internacao.setDataSaidaPrevista(dataEntrada.minusDays(1)));

        assertAll(
                () -> assertEquals("Data de saída prevista não pode ser anterior a data de entrada.", exception.getMessage()),
                () -> assertEquals(dataSaidaPrevistaAtual, internacao.getDataSaidaPrevista())
        );
    }

    // Testes para valor da mensalidade

    @Test
    void shouldAcceptPositiveMonthlyPaymentForPaidModalidade()
    {
        BigDecimal valorMensalidade = new BigDecimal("1200.00");

        Internacao internacao = InternacaoTestBuilder.newInternacao().setValorMensalidade(valorMensalidade).buildForCreate();

        assertEquals(valorMensalidade, internacao.getValorMensalidade());
    }

    @Test
    void shouldRoundMonthlyPaymentHalfUpToTwoDecimalPlaces()
    {
        Internacao internacao = InternacaoTestBuilder.newInternacao().setValorMensalidade(new BigDecimal("10.995")).buildForCreate();

        assertEquals(new BigDecimal("11.00"), internacao.getValorMensalidade());
    }

    @Test
    void shouldAcceptPaidMonthlyPaymentThatRoundsToOneCent()
    {
        Internacao internacao = InternacaoTestBuilder.newInternacao().setValorMensalidade(new BigDecimal("0.005")).buildForCreate();

        assertEquals(new BigDecimal("0.01"), internacao.getValorMensalidade());
    }

    @Test
    void shouldRejectNullMonthlyPaymentAndPreserveCurrentValue()
    {
        BigDecimal valorAtual = new BigDecimal("1200.00");
        Internacao internacao = InternacaoTestBuilder.newInternacao().setValorMensalidade(valorAtual).build();

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> internacao.setValorMensalidade(null));

        assertAll(
                () -> assertEquals("Valor da mensalidade não pode ser nula.", exception.getMessage()),
                () -> assertEquals(valorAtual, internacao.getValorMensalidade()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.00", "-10.00", "0.004"})
    void shouldRejectNonPositiveRoundedMonthlyPaymentForPaidModalidade(String valorInformado)
    {
        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> InternacaoTestBuilder.newInternacao().setValorMensalidade(new BigDecimal(valorInformado)).buildForCreate());

        assertEquals("Valor da mensalidade deve ser maior que zero.", exception.getMessage());
    }

    @Test
    void shouldAcceptZeroMonthlyPaymentForFreeModalidadeUsingValueConstructor()
    {
        Modalidade modalidade = new ModalidadeTestBuilder().setPagamento(false).build();

        Internacao internacao = InternacaoTestBuilder.newInternacao().setModalidade(modalidade).setValorMensalidade(BigDecimal.ZERO).buildForCreate();

        assertEquals(new BigDecimal("0.00"), internacao.getValorMensalidade());
    }

    @Test
    void shouldCreateFreeInternacaoWithoutMonthlyPayment()
    {
        Modalidade modalidade = new ModalidadeTestBuilder().setPagamento(false).build();

        Internacao internacao = InternacaoTestBuilder.newInternacao().setModalidade(modalidade).buildForCreateWithoutPayment();

        assertEquals(new BigDecimal("0.00"), internacao.getValorMensalidade());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.004", "-0.004"})
    void shouldAcceptValueThatRoundsToZeroForFreeModalidade(String valorInformado)
    {
        Modalidade modalidade = new ModalidadeTestBuilder().setPagamento(false).build();

        Internacao internacao = InternacaoTestBuilder.newInternacao().setModalidade(modalidade).setValorMensalidade(new BigDecimal(valorInformado)).buildForCreate();

        assertEquals(new BigDecimal("0.00"), internacao.getValorMensalidade());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.005", "-0.005", "10.00"})
    void shouldRejectValueThatRoundsToNonZeroForFreeModalidade(String valorInformado)
    {
        Modalidade modalidade = new ModalidadeTestBuilder().setPagamento(false).build();

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> InternacaoTestBuilder.newInternacao().setModalidade(modalidade).setValorMensalidade(new BigDecimal(valorInformado)).buildForCreate());

        assertEquals("Modalidade não aceita valor da mensalidade da internação diferente de 0", exception.getMessage());
    }

    @Test
    void shouldRejectPaidModalidadeWhenMonthlyPaymentIsOmitted()
    {
        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> InternacaoTestBuilder.newInternacao().buildForCreateWithoutPayment());

        assertEquals("Modalidade exige valor de mensalidade.", exception.getMessage());
    }

    @Test
    void shouldPreserveMonthlyPaymentAfterInvalidUpdate()
    {
        BigDecimal valorAtual = new BigDecimal("1200.00");
        Internacao internacao = InternacaoTestBuilder.newInternacao().setValorMensalidade(valorAtual).build();

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> internacao.setValorMensalidade(BigDecimal.ZERO));

        assertAll(
                () -> assertEquals("Valor da mensalidade deve ser maior que zero.", exception.getMessage()),
                () -> assertEquals(valorAtual, internacao.getValorMensalidade()));
    }

}
