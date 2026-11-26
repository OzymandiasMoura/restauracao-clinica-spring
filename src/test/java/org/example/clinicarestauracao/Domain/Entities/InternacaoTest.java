package org.example.clinicarestauracao.Domain.Entities;


import org.junit.jupiter.api.DisplayName;
import org.example.clinicarestauracao.Application.Exceptions.Internacao.InternacaoWithInvalidInformationException;
import org.example.clinicarestauracao.Builders.AcolhidoTestBuilder;
import org.example.clinicarestauracao.Builders.ConvenioTestBuilder;
import org.example.clinicarestauracao.Builders.InternacaoTestBuilder;
import org.example.clinicarestauracao.Domain.Enums.AltaTypes;
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

    @DisplayName("Deve criar internação com fornecido informações")
    @Test
    void shouldCreateInternacaoWithProvidedInformation()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();
        Convenio convenio = new ConvenioTestBuilder().build();
        LocalDate dataEntrada = LocalDate.of(2026, 1, 10);
        LocalDate dataSaidaPrevista = dataEntrada.plusMonths(9);
        BigDecimal valorMensalidade = new BigDecimal("1200.00");

        Internacao internacao = InternacaoTestBuilder.newInternacao().setAcolhido(acolhido).setConvenio(convenio).setDataEntrada(dataEntrada).setDataSaidaPrevista(dataSaidaPrevista).setValorMensalidade(valorMensalidade).buildForCreate();

        assertAll(
                () -> assertNull(internacao.getId()),
                () -> assertSame(acolhido, internacao.getAcolhido()),
                () -> assertSame(convenio, internacao.getConvenio()),
                () -> assertEquals(dataEntrada, internacao.getDataEntrada()),
                () -> assertEquals(dataSaidaPrevista, internacao.getDataSaidaPrevista()),
                () -> assertEquals(TreatmentStage.EM_TRATAMENTO, internacao.getEtapaTratamento()),
                () -> assertEquals(valorMensalidade, internacao.getValorMensalidade()),
                () -> assertTrue(internacao.getAtivo()),
                () -> assertNull(internacao.getDataSaida()),
                () -> assertNull(internacao.getAlta())
        );
    }

    @DisplayName("Deve sempre criar internação como ativo")
    @Test
    void shouldAlwaysCreateInternacaoAsActive()
    {
        Internacao internacao = InternacaoTestBuilder.newInternacao().setAtivo(false).buildForCreate();

        assertTrue(internacao.getAtivo());
    }

    // Testes do construtor de recuperação

    @DisplayName("Deve restaurar existente internação com fornecido informações")
    @Test
    void shouldRestoreExistingInternacaoWithProvidedInformation()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();
        Convenio convenio = new ConvenioTestBuilder().build();
        LocalDate dataEntrada = LocalDate.of(2026, 1, 10);
        LocalDate dataSaidaPrevista = LocalDate.of(2026, 10, 10);
        LocalDate dataSaida = LocalDate.of(2026, 8, 10);
        BigDecimal valorMensalidade = new BigDecimal("1200.00");

        Internacao internacao = InternacaoTestBuilder.newInternacao().setId(10L).setAcolhido(acolhido).setConvenio(convenio).setDataEntrada(dataEntrada).setDataSaidaPrevista(dataSaidaPrevista).setDataSaida(dataSaida).setEtapaTratamento(TreatmentStage.ALTA_CONCEDIDA).setValorMensalidade(valorMensalidade).setAtivo(false).setAlta(AltaTypes.CONCLUSAO).build();

        assertAll(
                () -> assertEquals(10L, internacao.getId()),
                () -> assertSame(acolhido, internacao.getAcolhido()),
                () -> assertSame(convenio, internacao.getConvenio()),
                () -> assertEquals(dataEntrada, internacao.getDataEntrada()),
                () -> assertEquals(dataSaidaPrevista, internacao.getDataSaidaPrevista()),
                () -> assertEquals(dataSaida, internacao.getDataSaida()),
                () -> assertEquals(TreatmentStage.ALTA_CONCEDIDA, internacao.getEtapaTratamento()),
                () -> assertEquals(valorMensalidade, internacao.getValorMensalidade()),
                () -> assertFalse(internacao.getAtivo()),
                () -> assertEquals(AltaTypes.CONCLUSAO, internacao.getAlta())
        );
    }


    @DisplayName("Deve rejeitar ID inválido")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1})
    void shouldRejectInvalidId(Long id)
    {
        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> InternacaoTestBuilder.newInternacao().setId(id).build());

        assertEquals("Id da internação é inválido.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar nulo ativo quando restaurando internação")
    @Test
    void shouldRejectNullActiveWhenRestoringInternacao()
    {
        InternacaoWithInvalidInformationException exception = assertThrows(
                InternacaoWithInvalidInformationException.class,
                () -> InternacaoTestBuilder.newInternacao().setAtivo(null).build()
        );

        assertEquals("Ativo não pode ser nulo.", exception.getMessage());
    }

    // Testes para acolhido

    @DisplayName("Deve rejeitar acolhido nulo")
    @Test
    void shouldRejectNullAcolhido()
    {
        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> InternacaoTestBuilder.newInternacao().setAcolhido(null).buildForCreate());

        assertEquals("Acolhido é obrigatório.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar inativo acolhido quando criando internação")
    @Test
    void shouldRejectInactiveAcolhidoWhenCreatingInternacao()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();
        acolhido.deactivate();

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> InternacaoTestBuilder.newInternacao().setAcolhido(acolhido).buildForCreate());

        assertEquals("Acolhido não pode estar desativado.", exception.getMessage());
    }

    @DisplayName("Deve permitir inativo acolhido quando restaurando existente internação")
    @Test
    void shouldAllowInactiveAcolhidoWhenRestoringExistingInternacao()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();
        acolhido.deactivate();
        Internacao internacao = InternacaoTestBuilder.newInternacao().setAcolhido(acolhido).build();

        assertSame(acolhido, internacao.getAcolhido());
        assertFalse(internacao.getAcolhido().getAtivo());
    }

    // Testes para convênio

    @DisplayName("Deve rejeitar convênio nulo")
    @Test
    void shouldRejectNullConvenio()
    {
        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> InternacaoTestBuilder.newInternacao().setConvenio(null).buildForCreate());

        assertEquals("Convênio é obrigatório.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar inativo convênio quando criando internação")
    @Test
    void shouldRejectInactiveConvenioWhenCreatingInternacao()
    {
        Convenio convenio = new ConvenioTestBuilder().setAtivo(false).build();

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> InternacaoTestBuilder.newInternacao().setConvenio(convenio).buildForCreate());

        assertEquals("Convênio não pode estar desativado.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar convênio sem vagas ao criar uma internação")
    @Test
    void shouldRejectConvenioWithZeroMaxVagasWhenCreatingInternacao()
    {
        Convenio convenio = new ConvenioTestBuilder().setMaxVagas(0).build();

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> InternacaoTestBuilder.newInternacao().setConvenio(convenio).buildForCreate());

        assertEquals("Convênio deve possuir numero de vagas maior que zero.", exception.getMessage());
    }


    @DisplayName("Deve permitir inativo convênio quando restaurando existente internação")
    @Test
    void shouldAllowInactiveConvenioWhenRestoringExistingInternacao()
    {
        Convenio convenio = new ConvenioTestBuilder().setAtivo(false).build();

        Internacao internacao = InternacaoTestBuilder.newInternacao().setConvenio(convenio).build();

        assertSame(convenio, internacao.getConvenio());
        assertFalse(internacao.getConvenio().isAtivo());
    }

    // Testes para data de entrada

    @DisplayName("Deve definir uma data de entrada válida")
    @Test
    void shouldSetValidEntryDate()
    {
        LocalDate dataEntrada = LocalDate.of(2026, 1, 10);

        Internacao internacao = InternacaoTestBuilder.newInternacao().setDataEntrada(dataEntrada).buildForCreate();

        assertEquals(dataEntrada, internacao.getDataEntrada());
    }

    @DisplayName("Deve aceitar a data atual como data de entrada")
    @Test
    void shouldAcceptCurrentDateAsEntryDate()
    {
        LocalDate dataEntrada = LocalDate.now();

        Internacao internacao = InternacaoTestBuilder.newInternacao().setDataEntrada(dataEntrada).buildForCreate();

        assertEquals(dataEntrada, internacao.getDataEntrada());
    }

    @DisplayName("Deve aceitar data de entrada igual à data de nascimento do acolhido")
    @Test
    void shouldAcceptEntryDateEqualToAcolhidoBirthDate()
    {
        LocalDate dataNascimento = LocalDate.of(2000, 1, 10);
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setDataNascimento(dataNascimento).build();
        Internacao internacao = InternacaoTestBuilder.newInternacao().setAcolhido(acolhido).setDataEntrada(dataNascimento).buildForCreate();

        assertEquals(dataNascimento, internacao.getDataEntrada());
    }

    @DisplayName("Deve rejeitar data de entrada nula")
    @Test
    void shouldRejectNullEntryDate()
    {
        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> InternacaoTestBuilder.newInternacao().setDataEntrada(null).buildForCreate());

        assertEquals("Data de entrada é obrigatória.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar uma data de entrada futura")
    @Test
    void shouldRejectFutureEntryDate()
    {
        LocalDate dataEntrada = LocalDate.now().plusDays(1);

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> InternacaoTestBuilder.newInternacao().setDataEntrada(dataEntrada).buildForCreate());

        assertEquals("Data de entrada não pode ser futura.", exception.getMessage());
    }

    @DisplayName("Deve rejeitar data de entrada anterior à data de nascimento do acolhido")
    @Test
    void shouldRejectEntryDateBeforeAcolhidoBirthDate()
    {
        LocalDate dataNascimento = LocalDate.of(2000, 1, 10);
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setDataNascimento(dataNascimento).build();

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> InternacaoTestBuilder.newInternacao().setAcolhido(acolhido).setDataEntrada(dataNascimento.minusDays(1)).buildForCreate());

        assertEquals("Data de entrada não pode ser anterior ao nascimento do acolhido.", exception.getMessage());
    }


    // Testes para data de saída prevista

    @DisplayName("Deve definir inicialmente a data de saída prevista para nove meses após a data de entrada")
    @Test
    void shouldInitiallySetExpectedExitDateNineMonthsAfterEntryDate()
    {
        LocalDate dataEntrada = LocalDate.of(2026, 1, 10);

        Internacao internacao = InternacaoTestBuilder.newInternacao().setDataEntrada(dataEntrada).setDataSaida(dataEntrada).buildForCreate();

        assertEquals(LocalDate.of(2026, 10, 10), internacao.getDataSaidaPrevista());
    }

    @DisplayName("Deve ajustar a data de saída prevista para o último dia válido do mês")
    @Test
    void shouldAdjustExpectedExitDateToLastValidDayOfMonth()
    {
        LocalDate dataEntrada = LocalDate.of(2026, 5, 31);

        Internacao internacao = InternacaoTestBuilder.newInternacao().setDataEntrada(dataEntrada).setDataSaida(dataEntrada).buildForCreate();

        assertEquals(LocalDate.of(2027, 2, 28), internacao.getDataSaidaPrevista());
    }

    @DisplayName("Deve definir uma data de saída prevista válida")
    @Test
    void shouldSetValidExpectedExitDate()
    {
        LocalDate dataEntrada = LocalDate.of(2026, 1, 10);
        LocalDate novaDataSaidaPrevista = LocalDate.of(2026, 11, 10);

        Internacao internacao = InternacaoTestBuilder.newInternacao().setDataEntrada(dataEntrada).setDataSaida(dataEntrada).build();

        internacao.setDataSaidaPrevista(novaDataSaidaPrevista);

        assertEquals(novaDataSaidaPrevista, internacao.getDataSaidaPrevista());
    }

    @DisplayName("Deve aceitar data de saída prevista igual à data de entrada")
    @Test
    void shouldAcceptExpectedExitDateEqualToEntryDate()
    {
        LocalDate dataEntrada = LocalDate.of(2026, 1, 10);

        Internacao internacao = InternacaoTestBuilder.newInternacao().setDataEntrada(dataEntrada).setDataSaidaPrevista(dataEntrada).setDataSaida(dataEntrada).build();

        assertEquals(dataEntrada, internacao.getDataSaidaPrevista());
    }

    @DisplayName("Deve rejeitar data de saída prevista nula e preservar atual valor")
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

    @DisplayName("Deve rejeitar data de saída prevista anterior à data de entrada e preservar o valor atual")
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


// Testes para data de saída

    @DisplayName("Deve permitir data de saída e tipo de alta nulos ao restaurar uma internação ativa")
    @Test
    void shouldAllowNullExitDateAndAltaWhenRestoringActiveInternacao()
    {
        Internacao internacao = InternacaoTestBuilder.newInternacao().setDataSaida(null).setAlta(null).setAtivo(true).build();

        assertAll(
                () -> assertNull(internacao.getDataSaida()),
                () -> assertNull(internacao.getAlta()),
                () -> assertTrue(internacao.getAtivo())
        );
    }

    @DisplayName("Deve aceitar a data atual como data de saída")
    @Test
    void shouldAcceptCurrentDateAsExitDate()
    {
        LocalDate dataEntrada = LocalDate.now().minusDays(1);
        LocalDate dataSaida = LocalDate.now();

        Internacao internacao = InternacaoTestBuilder.newInternacao().setDataEntrada(dataEntrada).buildForCreate();

        internacao.endInternacao(AltaTypes.CONCLUSAO, dataSaida);

        assertAll(
                () -> assertEquals(dataSaida, internacao.getDataSaida()),
                () -> assertEquals(AltaTypes.CONCLUSAO, internacao.getAlta()),
                () -> assertFalse(internacao.getAtivo())
        );
    }

    @DisplayName("Deve rejeitar uma data de saída futura e preservar o estado atual")
    @Test
    void shouldRejectFutureExitDateAndPreserveCurrentState()
    {
        LocalDate dataEntrada = LocalDate.now().minusDays(1);
        LocalDate dataSaidaFutura = LocalDate.now().plusDays(1);

        Internacao internacao = InternacaoTestBuilder.newInternacao().setDataEntrada(dataEntrada).buildForCreate();

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> internacao.endInternacao(AltaTypes.CONCLUSAO, dataSaidaFutura));

        assertAll(
                () -> assertEquals("Não é possível adicionar uma data de saída futura.", exception.getMessage()),
                () -> assertNull(internacao.getDataSaida()),
                () -> assertNull(internacao.getAlta()),
                () -> assertTrue(internacao.getAtivo())
        );
    }

    @DisplayName("Deve rejeitar data de saída anterior à data de entrada e preservar o estado atual")
    @Test
    void shouldRejectExitDateBeforeEntryDateAndPreserveCurrentState()
    {
        LocalDate dataEntrada = LocalDate.of(2026, 1, 10);
        LocalDate dataSaidaInvalida = dataEntrada.minusDays(1);

        Internacao internacao = InternacaoTestBuilder.newInternacao().setDataEntrada(dataEntrada).buildForCreate();

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> internacao.endInternacao(AltaTypes.CONCLUSAO, dataSaidaInvalida));

        assertAll(
                () -> assertEquals("Data de saída não pode ser anterior a data de entrada.", exception.getMessage()),
                () -> assertNull(internacao.getDataSaida()),
                () -> assertNull(internacao.getAlta()),
                () -> assertTrue(internacao.getAtivo())
        );
    }

    @DisplayName("Deve rejeitar data de saída nula ao encerrar a internação e preservar o estado atual")
    @Test
    void shouldRejectNullExitDateWhenEndingInternacaoAndPreserveCurrentState()
    {
        Internacao internacao = InternacaoTestBuilder.newInternacao()
                .buildForCreate();

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> internacao.endInternacao(AltaTypes.CONCLUSAO, null));

        assertAll(
                () -> assertEquals("Data de saída é obrigatória para gerar a alta.", exception.getMessage()),
                () -> assertNull(internacao.getDataSaida()),
                () -> assertNull(internacao.getAlta()),
                () -> assertTrue(internacao.getAtivo())
        );
    }

    @DisplayName("Deve rejeitar nulo alta quando encerramento internação e preservar atual estado")
    @Test
    void shouldRejectNullAltaWhenEndingInternacaoAndPreserveCurrentState()
    {
        LocalDate dataEntrada = LocalDate.now().minusDays(1);
        LocalDate dataSaida = LocalDate.now();

        Internacao internacao = InternacaoTestBuilder.newInternacao().setDataEntrada(dataEntrada).buildForCreate();

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> internacao.endInternacao(null, dataSaida)
        );

        assertAll(
                () -> assertEquals("Tipo é obrigatório para gerar a alta.", exception.getMessage()),
                () -> assertNull(internacao.getDataSaida()),
                () -> assertNull(internacao.getAlta()),
                () -> assertTrue(internacao.getAtivo())
        );
    }

    @DisplayName("Deve aceitar data de saída igual à data de entrada")
    @Test
    void shouldAcceptExitDateEqualToEntryDate()
    {
        LocalDate dataEntrada = LocalDate.now().minusDays(1);

        Internacao internacao = InternacaoTestBuilder.newInternacao().setDataEntrada(dataEntrada).buildForCreate();

        internacao.endInternacao(AltaTypes.DESISTENCIA, dataEntrada);

        assertAll(
                () -> assertEquals(dataEntrada, internacao.getDataSaida()),
                () -> assertEquals(AltaTypes.DESISTENCIA, internacao.getAlta()),
                () -> assertFalse(internacao.getAtivo())
        );
    }

    @DisplayName("Deve completar uma internação inativa quando a data de saída e o tipo de alta estão ausentes")
    @Test
    void shouldCompleteInactiveInternacaoWhenExitDateAndAltaAreMissing()
    {
        LocalDate dataEntrada = LocalDate.now().minusDays(10);
        LocalDate dataSaida = LocalDate.now().minusDays(1);

        Internacao internacao = InternacaoTestBuilder.newInternacao()
                .setDataEntrada(dataEntrada)
                .setDataSaidaPrevista(dataEntrada.plusMonths(9))
                .setDataSaida(null)
                .setAlta(null)
                .setAtivo(false)
                .build();

        internacao.endInternacao(AltaTypes.CONCLUSAO, dataSaida);

        assertAll(
                () -> assertEquals(dataSaida, internacao.getDataSaida()),
                () -> assertEquals(AltaTypes.CONCLUSAO, internacao.getAlta()),
                () -> assertFalse(internacao.getAtivo())
        );
    }

    @DisplayName("Deve completar uma internação inativa quando somente a data de saída está ausente")
    @Test
    void shouldCompleteInactiveInternacaoWhenOnlyExitDateIsMissing()
    {
        LocalDate dataEntrada = LocalDate.now().minusDays(10);
        LocalDate dataSaida = LocalDate.now().minusDays(1);

        Internacao internacao = InternacaoTestBuilder.newInternacao()
                .setDataEntrada(dataEntrada)
                .setDataSaidaPrevista(dataEntrada.plusMonths(9))
                .setDataSaida(null)
                .setAlta(AltaTypes.DESISTENCIA)
                .setAtivo(false)
                .build();

        internacao.endInternacao(AltaTypes.DESISTENCIA, dataSaida);

        assertAll(
                () -> assertEquals(dataSaida, internacao.getDataSaida()),
                () -> assertEquals(AltaTypes.DESISTENCIA, internacao.getAlta()),
                () -> assertFalse(internacao.getAtivo())
        );
    }

    @DisplayName("Deve completar uma internação inativa quando somente alta é ausente")
    @Test
    void shouldCompleteInactiveInternacaoWhenOnlyAltaIsMissing()
    {
        LocalDate dataEntrada = LocalDate.now().minusDays(10);
        LocalDate dataSaida = LocalDate.now().minusDays(1);

        Internacao internacao = InternacaoTestBuilder.newInternacao()
                .setDataEntrada(dataEntrada)
                .setDataSaidaPrevista(dataEntrada.plusMonths(9))
                .setDataSaida(dataSaida)
                .setAlta(null)
                .setAtivo(false)
                .build();

        internacao.endInternacao(AltaTypes.CONCLUSAO, dataSaida);

        assertAll(
                () -> assertEquals(dataSaida, internacao.getDataSaida()),
                () -> assertEquals(AltaTypes.CONCLUSAO, internacao.getAlta()),
                () -> assertFalse(internacao.getAtivo())
        );
    }

    @DisplayName("Deve rejeitar encerramento completo internação novamente e preservar original estado")
    @Test
    void shouldRejectEndingCompleteInternacaoAgainAndPreserveOriginalState()
    {
        LocalDate dataEntrada = LocalDate.now().minusDays(10);
        LocalDate primeiraDataSaida = LocalDate.now().minusDays(5);
        LocalDate segundaDataSaida = LocalDate.now().minusDays(1);

        Internacao internacao = InternacaoTestBuilder.newInternacao()
                .setDataEntrada(dataEntrada)
                .buildForCreate();

        internacao.endInternacao(AltaTypes.CONCLUSAO, primeiraDataSaida);

        InternacaoWithInvalidInformationException exception = assertThrows(
                InternacaoWithInvalidInformationException.class,
                () -> internacao.endInternacao(AltaTypes.DESLIGAMENTO, segundaDataSaida)
        );

        assertAll(
                () -> assertEquals("Internação já foi encerrada.", exception.getMessage()),
                () -> assertEquals(primeiraDataSaida, internacao.getDataSaida()),
                () -> assertEquals(AltaTypes.CONCLUSAO, internacao.getAlta()),
                () -> assertFalse(internacao.getAtivo())
        );
    }

    @DisplayName("Deve relatório já encerrado antes validando novo encerrar informações")
    @Test
    void shouldReportAlreadyEndedBeforeValidatingNewEndInformation()
    {
        LocalDate dataEntrada = LocalDate.now().minusDays(10);
        LocalDate dataSaida = LocalDate.now().minusDays(1);

        Internacao internacao = InternacaoTestBuilder.newInternacao()
                .setDataEntrada(dataEntrada)
                .buildForCreate();

        internacao.endInternacao(AltaTypes.CONCLUSAO, dataSaida);

        InternacaoWithInvalidInformationException exception = assertThrows(
                InternacaoWithInvalidInformationException.class,
                () -> internacao.endInternacao(null, null)
        );

        assertAll(
                () -> assertEquals("Internação já foi encerrada.", exception.getMessage()),
                () -> assertEquals(dataSaida, internacao.getDataSaida()),
                () -> assertEquals(AltaTypes.CONCLUSAO, internacao.getAlta()),
                () -> assertFalse(internacao.getAtivo())
        );
    }


    // Testes para valor da mensalidade

    @DisplayName("Deve aceitar positivo mensalidade pagamento para pago convênio")
    @Test
    void shouldAcceptPositiveMonthlyPaymentForPaidConvenio()
    {
        BigDecimal valorMensalidade = new BigDecimal("1200.00");

        Internacao internacao = InternacaoTestBuilder.newInternacao().setValorMensalidade(valorMensalidade).buildForCreate();

        assertEquals(valorMensalidade, internacao.getValorMensalidade());
    }

    @DisplayName("Deve arredondar mensalidade pagamento metade acima para dois decimal casas")
    @Test
    void shouldRoundMonthlyPaymentHalfUpToTwoDecimalPlaces()
    {
        Internacao internacao = InternacaoTestBuilder.newInternacao().setValorMensalidade(new BigDecimal("10.995")).buildForCreate();

        assertEquals(new BigDecimal("11.00"), internacao.getValorMensalidade());
    }

    @DisplayName("Deve aceitar pago mensalidade pagamento que arredonda para um centavos")
    @Test
    void shouldAcceptPaidMonthlyPaymentThatRoundsToOneCent()
    {
        Internacao internacao = InternacaoTestBuilder.newInternacao().setValorMensalidade(new BigDecimal("0.005")).buildForCreate();

        assertEquals(new BigDecimal("0.01"), internacao.getValorMensalidade());
    }

    @DisplayName("Deve rejeitar valor de mensalidade nulo e preservar atual valor")
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

    @DisplayName("Deve rejeitar não positivo arredondado mensalidade pagamento para pago convênio")
    @ParameterizedTest
    @ValueSource(strings = {"0.00", "-10.00", "0.004"})
    void shouldRejectNonPositiveRoundedMonthlyPaymentForPaidConvenio(String valorInformado)
    {
        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> InternacaoTestBuilder.newInternacao().setValorMensalidade(new BigDecimal(valorInformado)).buildForCreate());

        assertEquals("Valor da mensalidade deve ser maior que zero.", exception.getMessage());
    }

    @DisplayName("Deve aceitar zero mensalidade pagamento para gratuito convênio usando valor construtor")
    @Test
    void shouldAcceptZeroMonthlyPaymentForFreeConvenioUsingValueConstructor()
    {
        Convenio convenio = new ConvenioTestBuilder().setPagamento(false).build();

        Internacao internacao = InternacaoTestBuilder.newInternacao().setConvenio(convenio).setValorMensalidade(BigDecimal.ZERO).buildForCreate();

        assertEquals(new BigDecimal("0.00"), internacao.getValorMensalidade());
    }

    @DisplayName("Deve criar uma internação gratuita sem pagamento mensal")
    @Test
    void shouldCreateFreeInternacaoWithoutMonthlyPayment()
    {
        Convenio convenio = new ConvenioTestBuilder().setPagamento(false).build();

        Internacao internacao = InternacaoTestBuilder.newInternacao().setConvenio(convenio).buildForCreateWithoutPayment();

        assertEquals(new BigDecimal("0.00"), internacao.getValorMensalidade());
    }

    @DisplayName("Deve aceitar valor que arredonda para zero para gratuito convênio")
    @ParameterizedTest
    @ValueSource(strings = {"0.004", "-0.004"})
    void shouldAcceptValueThatRoundsToZeroForFreeConvenio(String valorInformado)
    {
        Convenio convenio = new ConvenioTestBuilder().setPagamento(false).build();

        Internacao internacao = InternacaoTestBuilder.newInternacao().setConvenio(convenio).setValorMensalidade(new BigDecimal(valorInformado)).buildForCreate();

        assertEquals(new BigDecimal("0.00"), internacao.getValorMensalidade());
    }

    @DisplayName("Deve rejeitar valor que arredonda para não zero para gratuito convênio")
    @ParameterizedTest
    @ValueSource(strings = {"0.005", "-0.005", "10.00"})
    void shouldRejectValueThatRoundsToNonZeroForFreeConvenio(String valorInformado)
    {
        Convenio convenio = new ConvenioTestBuilder().setPagamento(false).build();

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> InternacaoTestBuilder.newInternacao().setConvenio(convenio).setValorMensalidade(new BigDecimal(valorInformado)).buildForCreate());

        assertEquals("Convênio não aceita valor da mensalidade da internação diferente de 0", exception.getMessage());
    }

    @DisplayName("Deve rejeitar pago convênio quando mensalidade pagamento é omitido")
    @Test
    void shouldRejectPaidConvenioWhenMonthlyPaymentIsOmitted()
    {
        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> InternacaoTestBuilder.newInternacao().buildForCreateWithoutPayment());

        assertEquals("Convênio exige valor de mensalidade.", exception.getMessage());
    }

    @DisplayName("Deve preservar mensalidade pagamento após uma atualização inválida")
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

    @DisplayName("Deve aceitar mensalidade pagamento igual para máximo valor")
    @Test
    void shouldAcceptMonthlyPaymentEqualToMaximumValue()
    {
        BigDecimal valorMaximo = new BigDecimal("1000000.00");

        Internacao internacao = InternacaoTestBuilder.newInternacao().setValorMensalidade(valorMaximo).buildForCreate();

        assertEquals(valorMaximo, internacao.getValorMensalidade());
    }

    @DisplayName("Deve aceitar mensalidade pagamento que arredonda para máximo valor")
    @Test
    void shouldAcceptMonthlyPaymentThatRoundsToMaximumValue()
    {
        Internacao internacao = InternacaoTestBuilder.newInternacao().setValorMensalidade(new BigDecimal("1000000.004")).buildForCreate();

        assertEquals(new BigDecimal("1000000.00"), internacao.getValorMensalidade());
    }

    @DisplayName("Deve rejeitar mensalidade pagamento acima máximo depois arredondamento e preservar atual valor")
    @Test
    void shouldRejectMonthlyPaymentAboveMaximumAfterRoundingAndPreserveCurrentValue()
    {
        BigDecimal valorAtual = new BigDecimal("1200.00");
        Internacao internacao = InternacaoTestBuilder.newInternacao().setValorMensalidade(valorAtual).build();
        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> internacao.setValorMensalidade(new BigDecimal("1000000.005")));

        assertAll(
                () -> assertEquals("Valor da mensalidade não pode ser maior que 1.000.000,00.", exception.getMessage()),
                () -> assertEquals(valorAtual, internacao.getValorMensalidade())
        );
    }


    // Testes updateTreatmentStage

    @DisplayName("Deve rejeitar nulo referência data e preservar tratamento etapa")
    @Test
    void shouldRejectNullReferenceDateAndPreserveTreatmentStage()
    {
        Internacao internacao = InternacaoTestBuilder.newInternacao().setEtapaTratamento(TreatmentStage.EM_TRATAMENTO).build();
        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> internacao.updateTreatmentStage(null));

        assertAll(
                () -> assertEquals("Data de referência é obrigatória.", exception.getMessage()),
                () -> assertEquals(TreatmentStage.EM_TRATAMENTO, internacao.getEtapaTratamento())
        );
    }

    @DisplayName("Não deve atualizar tratamento etapa quando internação é inativo")
    @Test
    void shouldNotUpdateTreatmentStageWhenInternacaoIsInactive()
    {
        LocalDate dataSaidaPrevista = LocalDate.of(2026, 12, 31);
        Internacao internacao = InternacaoTestBuilder.newInternacao().setDataSaidaPrevista(dataSaidaPrevista).setEtapaTratamento(TreatmentStage.ALTA_CONCEDIDA).setAtivo(false).build();
        internacao.updateTreatmentStage(dataSaidaPrevista.plusDays(1));

        assertEquals(TreatmentStage.ALTA_CONCEDIDA, internacao.getEtapaTratamento());
    }

    @DisplayName("Deve definir a etapa do tratamento como alta vencida quando a data de saída prevista já passou")
    @Test
    void shouldSetTreatmentStageAsExpiredWhenExpectedExitDateHasPassed()
    {
        LocalDate dataSaidaPrevista = LocalDate.of(2026, 12, 31);
        Internacao internacao = InternacaoTestBuilder.newInternacao().setDataSaidaPrevista(dataSaidaPrevista).setEtapaTratamento(TreatmentStage.EM_TRATAMENTO).setAtivo(true).build();

        internacao.updateTreatmentStage(dataSaidaPrevista.plusDays(1));

        assertEquals(TreatmentStage.ALTA_VENCIDA, internacao.getEtapaTratamento());
    }

    @DisplayName("Deve definir tratamento etapa como próxima alta quando há são em maioria trinta dias restante")
    @ParameterizedTest
    @ValueSource(longs = {30, 29, 1, 0})
    void shouldSetTreatmentStageAsUpcomingDischargeWhenThereAreAtMostThirtyDaysRemaining(long diasAntesDaSaida)
    {
        LocalDate dataSaidaPrevista = LocalDate.of(2026, 12, 31);
        Internacao internacao = InternacaoTestBuilder.newInternacao().setDataSaidaPrevista(dataSaidaPrevista).setEtapaTratamento(TreatmentStage.EM_TRATAMENTO).setAtivo(true).build();
        LocalDate dataReferencia = dataSaidaPrevista.minusDays(diasAntesDaSaida);

        internacao.updateTreatmentStage(dataReferencia);

        assertEquals(TreatmentStage.PROXIMO_ALTA, internacao.getEtapaTratamento());
    }

    @DisplayName("Deve manter tratamento etapa como em tratamento quando mais que trinta dias permanecer")
    @Test
    void shouldKeepTreatmentStageAsInTreatmentWhenMoreThanThirtyDaysRemain()
    {
        LocalDate dataSaidaPrevista = LocalDate.of(2026, 12, 31);

        Internacao internacao = InternacaoTestBuilder.newInternacao().setDataSaidaPrevista(dataSaidaPrevista).setEtapaTratamento(TreatmentStage.PROXIMO_ALTA).setAtivo(true).build();

        internacao.updateTreatmentStage(dataSaidaPrevista.minusDays(31));

        assertEquals(TreatmentStage.EM_TRATAMENTO, internacao.getEtapaTratamento());
    }

}
