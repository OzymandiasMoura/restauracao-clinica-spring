package org.example.clinicarestauracao.Domain.Entities;

import org.example.clinicarestauracao.Application.Exceptions.Triagem.TriagemWithInvalidInformationException;
import org.example.clinicarestauracao.Builders.ConvenioTestBuilder;
import org.example.clinicarestauracao.Builders.TriagemTestBuilder;
import org.example.clinicarestauracao.Domain.Enums.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TriagemTest
{
    // Testes do construtor de criação

    @Test
    void shouldCreateTriagemWithValidInformation()
    {
        Convenio convenio = new ConvenioTestBuilder().build();
        LocalDateTime dataAgendada = LocalDateTime.now().plusDays(7);

        Triagem triagem = TriagemTestBuilder.newTriagem()
                .setConvenio(convenio)
                .setNomePossivelAcolhido("  Maria da Silva  ")
                .setTelefone("(11) 99999-9999")
                .setDataAgendada(dataAgendada)
                .setObservacaoEntrevista("  Entrevista inicial  ")
                .buildForCreate();

        assertAll(
                () -> assertNull(triagem.getId()),
                () -> assertSame(convenio, triagem.getConvenio()),
                () -> assertEquals("Maria da Silva", triagem.getNomePossivelAcolhido()),
                () -> assertEquals("11999999999", triagem.getTelefone()),
                () -> assertEquals(dataAgendada, triagem.getDataAgendada()),
                () -> assertEquals("Entrevista inicial", triagem.getObservacaoEntrevista()),
                () -> assertEquals(Status.AGENDADA, triagem.getStatus()),
                () -> assertNull(triagem.getDataConclusao())
        );
    }

    // Testes do construtor de recuperação

    @Test
    void shouldRestoreConcludedTriagemWithValidInformation()
    {
        Convenio convenio = new ConvenioTestBuilder().build();
        LocalDateTime dataAgendada = LocalDateTime.now().minusDays(2);
        LocalDateTime dataConclusao = LocalDateTime.now().minusDays(1);

        Triagem triagem = TriagemTestBuilder.newTriagem()
                .setId(10L)
                .setConvenio(convenio)
                .setDataAgendada(dataAgendada)
                .setStatus(Status.CONCLUIDA)
                .setDataConclusao(dataConclusao)
                .build();

        assertAll(
                () -> assertEquals(10L, triagem.getId()),
                () -> assertSame(convenio, triagem.getConvenio()),
                () -> assertEquals(dataAgendada, triagem.getDataAgendada()),
                () -> assertEquals(Status.CONCLUIDA, triagem.getStatus()),
                () -> assertEquals(dataConclusao, triagem.getDataConclusao())
        );
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldRejectInvalidId(Long id)
    {
        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> TriagemTestBuilder.newTriagem().setId(id).build()
        );

        assertEquals("Id da triagem inválido", exception.getMessage());
    }

    // Testes do convênio

    @Test
    void shouldRejectNullConvenioWhenCreatingTriagem()
    {
        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> TriagemTestBuilder.newTriagem().setConvenio(null).buildForCreate()
        );

        assertEquals("A triagem precisa estar vinculada a um convenio.", exception.getMessage());
    }

    @Test
    void shouldRejectNullConvenioWhenRestoringTriagem()
    {
        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> TriagemTestBuilder.newTriagem().setConvenio(null).build()
        );

        assertEquals("A triagem precisa estar vinculada a um convenio.", exception.getMessage());
    }

    @Test
    void shouldRejectInactiveConvenioWhenCreatingTriagem()
    {
        Convenio convenio = new ConvenioTestBuilder().setAtivo(false).build();

        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> TriagemTestBuilder.newTriagem().setConvenio(convenio).buildForCreate()
        );

        assertEquals("Não é possível agendar triagem para um convenio inativo.", exception.getMessage());
    }

    @Test
    void shouldAllowInactiveConvenioWhenRestoringTriagem()
    {
        Convenio convenio = new ConvenioTestBuilder().setAtivo(false).build();

        Triagem triagem = TriagemTestBuilder.newTriagem().setConvenio(convenio).build();

        assertSame(convenio, triagem.getConvenio());
        assertFalse(triagem.getConvenio().isAtivo());
    }

    @Test
    void shouldUpdateConvenioWhenTriagemCanBeChanged()
    {
        Triagem triagem = TriagemTestBuilder.newTriagem().build();
        Convenio novoConvenio = new ConvenioTestBuilder().setId(2L).setDescricao("Novo convênio").setCor("#123456").build();

        triagem.setConvenio(novoConvenio);

        assertSame(novoConvenio, triagem.getConvenio());
    }

    @Test
    void shouldPreserveConvenioAfterInvalidUpdate()
    {
        Triagem triagem = TriagemTestBuilder.newTriagem().build();
        Convenio convenioAtual = triagem.getConvenio();
        Convenio convenioInativo = new ConvenioTestBuilder().setId(2L).setAtivo(false).build();

        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> triagem.setConvenio(convenioInativo)
        );

        assertAll(
                () -> assertEquals("Não é possível agendar triagem para um convenio inativo.", exception.getMessage()),
                () -> assertSame(convenioAtual, triagem.getConvenio())
        );
    }

    // Testes do nome

    @Test
    void shouldNormalizePossibleAcolhidoName()
    {
        Triagem triagem = TriagemTestBuilder.newTriagem()
                .setNomePossivelAcolhido("  Maria da Silva  ")
                .buildForCreate();

        assertEquals("Maria da Silva", triagem.getNomePossivelAcolhido());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "\t", "\n"})
    void shouldRejectNullOrBlankPossibleAcolhidoName(String nome)
    {
        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> TriagemTestBuilder.newTriagem().setNomePossivelAcolhido(nome).buildForCreate()
        );

        assertEquals("O nome não pode ser nulo ou vazio.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "AB", " A ", " AB "})
    void shouldRejectPossibleAcolhidoNameShorterThanThreeCharacters(String nome)
    {
        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> TriagemTestBuilder.newTriagem().setNomePossivelAcolhido(nome).buildForCreate()
        );

        assertEquals("O nome do possível acolhido deve ter pelo menos 3 caracteres.", exception.getMessage());
    }

    @Test
    void shouldAcceptPossibleAcolhidoNameWithExactlyThreeCharacters()
    {
        Triagem triagem = TriagemTestBuilder.newTriagem().setNomePossivelAcolhido("Ana").buildForCreate();

        assertEquals("Ana", triagem.getNomePossivelAcolhido());
    }

    @Test
    void shouldAcceptPossibleAcolhidoNameWithExactlyTwoHundredAndFiftyFiveCharacters()
    {
        String nome = "A".repeat(255);

        Triagem triagem = TriagemTestBuilder.newTriagem().setNomePossivelAcolhido(nome).buildForCreate();

        assertEquals(nome, triagem.getNomePossivelAcolhido());
    }

    @Test
    void shouldRejectPossibleAcolhidoNameLongerThanTwoHundredAndFiftyFiveCharacters()
    {
        String nome = "A".repeat(256);

        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> TriagemTestBuilder.newTriagem().setNomePossivelAcolhido(nome).buildForCreate()
        );

        assertEquals("O nome do possível acolhido não pode ultrapassar 255 caracteres.", exception.getMessage());
    }

    @Test
    void shouldPreservePossibleAcolhidoNameAfterInvalidUpdate()
    {
        Triagem triagem = TriagemTestBuilder.newTriagem().setNomePossivelAcolhido("Maria da Silva").build();

        assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> triagem.setNomePossivelAcolhido("AB")
        );

        assertEquals("Maria da Silva", triagem.getNomePossivelAcolhido());
    }

    // Testes do telefone

    @Test
    void shouldNormalizeTelefone()
    {
        Triagem triagem = TriagemTestBuilder.newTriagem().setTelefone("(11) 99999-9999").buildForCreate();

        assertEquals("11999999999", triagem.getTelefone());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "\t", "\n"})
    void shouldRejectNullOrBlankTelefone(String telefone)
    {
        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> TriagemTestBuilder.newTriagem().setTelefone(telefone).buildForCreate()
        );

        assertEquals("O telefone é obrigatório.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"119999999", "1169999999", "119999999999", "telefone"})
    void shouldRejectInvalidTelefone(String telefone)
    {
        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> TriagemTestBuilder.newTriagem().setTelefone(telefone).buildForCreate()
        );

        assertEquals("Telefone inválido.", exception.getMessage());
    }

    @Test
    void shouldPreserveTelefoneAfterInvalidUpdate()
    {
        Triagem triagem = TriagemTestBuilder.newTriagem().setTelefone("11999999999").build();

        assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> triagem.setTelefone("119999999")
        );

        assertEquals("11999999999", triagem.getTelefone());
    }

    // Testes da data agendada

    @Test
    void shouldRejectNullScheduledDateWhenCreatingTriagem()
    {
        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> TriagemTestBuilder.newTriagem().setDataAgendada(null).buildForCreate()
        );

        assertEquals("A data agendada não pode ser nula.", exception.getMessage());
    }

    @Test
    void shouldRejectScheduledDateThatIsNotInTheFutureWhenCreatingTriagem()
    {
        LocalDateTime dataAgendada = LocalDateTime.now().minusSeconds(1);

        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> TriagemTestBuilder.newTriagem().setDataAgendada(dataAgendada).buildForCreate()
        );

        assertEquals("A data agendada deve estar no futuro.", exception.getMessage());
    }

    @Test
    void shouldRejectNullScheduledDateWhenRestoringTriagem()
    {
        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> TriagemTestBuilder.newTriagem().setDataAgendada(null).build()
        );

        assertEquals("A data agendada não pode ser nula.", exception.getMessage());
    }

    @Test
    void shouldAllowPastScheduledDateWhenRestoringTriagem()
    {
        LocalDateTime dataAgendada = LocalDateTime.now().minusDays(1);

        Triagem triagem = TriagemTestBuilder.newTriagem().setDataAgendada(dataAgendada).build();

        assertEquals(dataAgendada, triagem.getDataAgendada());
        assertEquals(Status.AGENDADA, triagem.getStatus());
    }

    // Testes da observação

    @Test
    void shouldAllowNullInterviewObservation()
    {
        Triagem triagem = TriagemTestBuilder.newTriagem().setObservacaoEntrevista(null).buildForCreate();

        assertNull(triagem.getObservacaoEntrevista());
    }

    @Test
    void shouldNormalizeInterviewObservation()
    {
        Triagem triagem = TriagemTestBuilder.newTriagem()
                .setObservacaoEntrevista("  Entrevista inicial  ")
                .buildForCreate();

        assertEquals("Entrevista inicial", triagem.getObservacaoEntrevista());
    }

    @Test
    void shouldAcceptInterviewObservationWithExactlyOneThousandCharacters()
    {
        String observacao = "A".repeat(1000);

        Triagem triagem = TriagemTestBuilder.newTriagem().setObservacaoEntrevista(observacao).buildForCreate();

        assertEquals(observacao, triagem.getObservacaoEntrevista());
    }

    @Test
    void shouldRejectInterviewObservationLongerThanOneThousandCharacters()
    {
        String observacao = "A".repeat(1001);

        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> TriagemTestBuilder.newTriagem().setObservacaoEntrevista(observacao).buildForCreate()
        );

        assertEquals("A observação da entrevista não pode ultrapassar 1000 caracteres.", exception.getMessage());
    }

    @Test
    void shouldPreserveInterviewObservationAfterInvalidUpdate()
    {
        Triagem triagem = TriagemTestBuilder.newTriagem().setObservacaoEntrevista("Observação atual").build();

        assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> triagem.setObservacaoEntrevista("A".repeat(1001))
        );

        assertEquals("Observação atual", triagem.getObservacaoEntrevista());
    }

    // Testes da coerência entre status e data de conclusão

    @Test
    void shouldRejectNullStatusWhenRestoringTriagem()
    {
        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> TriagemTestBuilder.newTriagem().setStatus(null).build()
        );

        assertEquals("Status da triagem não pode ser nulo.", exception.getMessage());
    }

    @Test
    void shouldRejectConcludedTriagemWithoutCompletionDate()
    {
        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> TriagemTestBuilder.newTriagem().setStatus(Status.CONCLUIDA).setDataConclusao(null).build()
        );

        assertEquals("Triagem concluída deve possuir data de conclusão.", exception.getMessage());
    }

    @ParameterizedTest
    @EnumSource(value = Status.class, names = {"AGENDADA", "ATRASADA", "CANCELADA"})
    void shouldRejectCompletionDateForNonConcludedTriagem(Status status)
    {
        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> TriagemTestBuilder.newTriagem()
                        .setStatus(status)
                        .setDataConclusao(LocalDateTime.now().minusHours(1))
                        .build()
        );

        assertEquals("Triagem não concluída não pode possuir data de conclusão.", exception.getMessage());
    }

    @Test
    void shouldRejectCompletionDateBeforeScheduledDate()
    {
        LocalDateTime dataAgendada = LocalDateTime.now().minusDays(2);
        LocalDateTime dataConclusao = dataAgendada.minusHours(1);

        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> TriagemTestBuilder.newTriagem()
                        .setDataAgendada(dataAgendada)
                        .setStatus(Status.CONCLUIDA)
                        .setDataConclusao(dataConclusao)
                        .build()
        );

        assertEquals("Data de conclusão não pode ser anterior a data de agendamento.", exception.getMessage());
    }

    @Test
    void shouldRejectFutureCompletionDate()
    {
        LocalDateTime dataAgendada = LocalDateTime.now().minusDays(1);
        LocalDateTime dataConclusao = LocalDateTime.now().plusDays(1);

        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> TriagemTestBuilder.newTriagem()
                        .setDataAgendada(dataAgendada)
                        .setStatus(Status.CONCLUIDA)
                        .setDataConclusao(dataConclusao)
                        .build()
        );

        assertEquals("Data de concluão não pode ser futura.", exception.getMessage());
    }

    @Test
    void shouldAllowCompletionAtScheduledDate()
    {
        LocalDateTime dataAgendada = LocalDateTime.now().minusDays(1);

        Triagem triagem = TriagemTestBuilder.newTriagem()
                .setDataAgendada(dataAgendada)
                .setStatus(Status.CONCLUIDA)
                .setDataConclusao(dataAgendada)
                .build();

        assertEquals(dataAgendada, triagem.getDataConclusao());
    }

    // Testes da proteção contra alterações em estados finais

    @ParameterizedTest
    @EnumSource(value = Status.class, names = {"CONCLUIDA", "CANCELADA"})
    void shouldRejectConvenioUpdateForFinalStatus(Status status)
    {
        Triagem triagem = buildTriagemWithStatus(status);
        Convenio convenioAtual = triagem.getConvenio();
        Convenio novoConvenio = new ConvenioTestBuilder().setId(2L).setDescricao("Novo convênio").setCor("#123456").build();

        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> triagem.setConvenio(novoConvenio)
        );

        assertAll(
                () -> assertEquals("Triagem concluída ou cancelada não pode ser alterada.", exception.getMessage()),
                () -> assertSame(convenioAtual, triagem.getConvenio())
        );
    }

    @ParameterizedTest
    @EnumSource(value = Status.class, names = {"CONCLUIDA", "CANCELADA"})
    void shouldRejectNameUpdateForFinalStatus(Status status)
    {
        Triagem triagem = buildTriagemWithStatus(status);
        String nomeAtual = triagem.getNomePossivelAcolhido();

        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> triagem.setNomePossivelAcolhido("Novo nome")
        );

        assertAll(
                () -> assertEquals("Triagem concluída ou cancelada não pode ser alterada.", exception.getMessage()),
                () -> assertEquals(nomeAtual, triagem.getNomePossivelAcolhido())
        );
    }

    @ParameterizedTest
    @EnumSource(value = Status.class, names = {"CONCLUIDA", "CANCELADA"})
    void shouldRejectTelefoneUpdateForFinalStatus(Status status)
    {
        Triagem triagem = buildTriagemWithStatus(status);
        String telefoneAtual = triagem.getTelefone();

        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> triagem.setTelefone("1133334444")
        );

        assertAll(
                () -> assertEquals("Triagem concluída ou cancelada não pode ser alterada.", exception.getMessage()),
                () -> assertEquals(telefoneAtual, triagem.getTelefone())
        );
    }

    @ParameterizedTest
    @EnumSource(value = Status.class, names = {"CONCLUIDA", "CANCELADA"})
    void shouldRejectObservationUpdateForFinalStatus(Status status)
    {
        Triagem triagem = buildTriagemWithStatus(status);
        String observacaoAtual = triagem.getObservacaoEntrevista();

        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> triagem.setObservacaoEntrevista("Nova observação")
        );

        assertAll(
                () -> assertEquals("Triagem concluída ou cancelada não pode ser alterada.", exception.getMessage()),
                () -> assertEquals(observacaoAtual, triagem.getObservacaoEntrevista())
        );
    }

    // Testes da conclusão

    @Test
    void shouldConcludeTriagemWithProvidedCompletionDate()
    {
        LocalDateTime dataAgendada = LocalDateTime.now().minusDays(2);
        LocalDateTime dataConclusao = LocalDateTime.now().minusDays(1);
        Triagem triagem = TriagemTestBuilder.newTriagem().setDataAgendada(dataAgendada).build();

        triagem.concluir(dataConclusao);

        assertAll(
                () -> assertEquals(Status.CONCLUIDA, triagem.getStatus()),
                () -> assertEquals(dataConclusao, triagem.getDataConclusao())
        );
    }

    @Test
    void shouldConcludeTriagemWithCurrentDate()
    {
        Triagem triagem = TriagemTestBuilder.newTriagem()
                .setDataAgendada(LocalDateTime.now().minusDays(1))
                .build();
        LocalDateTime antesDaConclusao = LocalDateTime.now();

        triagem.concluir();

        LocalDateTime depoisDaConclusao = LocalDateTime.now();
        assertAll(
                () -> assertEquals(Status.CONCLUIDA, triagem.getStatus()),
                () -> assertFalse(triagem.getDataConclusao().isBefore(antesDaConclusao)),
                () -> assertFalse(triagem.getDataConclusao().isAfter(depoisDaConclusao))
        );
    }

    @ParameterizedTest
    @EnumSource(value = Status.class, names = {"CONCLUIDA", "CANCELADA"})
    void shouldRejectConclusionForFinalStatus(Status status)
    {
        Triagem triagem = buildTriagemWithStatus(status);
        LocalDateTime dataConclusaoAtual = triagem.getDataConclusao();

        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> triagem.concluir(LocalDateTime.now())
        );

        assertAll(
                () -> assertEquals("Triagem já concluida ou cancelada.", exception.getMessage()),
                () -> assertEquals(status, triagem.getStatus()),
                () -> assertEquals(dataConclusaoAtual, triagem.getDataConclusao())
        );
    }

    @Test
    void shouldRejectNullCompletionDateWhenConcludingTriagem()
    {
        Triagem triagem = TriagemTestBuilder.newTriagem()
                .setDataAgendada(LocalDateTime.now().minusDays(1))
                .build();

        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> triagem.concluir(null)
        );

        assertAll(
                () -> assertEquals("Data de conclusão não pode ser inválida.", exception.getMessage()),
                () -> assertEquals(Status.AGENDADA, triagem.getStatus()),
                () -> assertNull(triagem.getDataConclusao())
        );
    }

    @Test
    void shouldPreserveTriagemAfterCompletionDateBeforeScheduledDate()
    {
        LocalDateTime dataAgendada = LocalDateTime.now().minusDays(1);
        Triagem triagem = TriagemTestBuilder.newTriagem().setDataAgendada(dataAgendada).build();

        assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> triagem.concluir(dataAgendada.minusHours(1))
        );

        assertAll(
                () -> assertEquals(Status.AGENDADA, triagem.getStatus()),
                () -> assertNull(triagem.getDataConclusao())
        );
    }

    @Test
    void shouldPreserveTriagemAfterFutureCompletionDate()
    {
        Triagem triagem = TriagemTestBuilder.newTriagem()
                .setDataAgendada(LocalDateTime.now().minusDays(1))
                .build();

        assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> triagem.concluir(LocalDateTime.now().plusDays(1))
        );

        assertAll(
                () -> assertEquals(Status.AGENDADA, triagem.getStatus()),
                () -> assertNull(triagem.getDataConclusao())
        );
    }

    // Testes da desconclusão

    @Test
    void shouldUndoConclusionAndMarkTriagemAsOverdue()
    {
        Triagem triagem = buildTriagemWithStatus(Status.CONCLUIDA);

        triagem.desconcluir();

        assertAll(
                () -> assertEquals(Status.ATRASADA, triagem.getStatus()),
                () -> assertNull(triagem.getDataConclusao())
        );
    }

    @ParameterizedTest
    @EnumSource(value = Status.class, names = {"AGENDADA", "ATRASADA", "CANCELADA"})
    void shouldRejectUndoConclusionWhenTriagemIsNotConcluded(Status status)
    {
        Triagem triagem = buildTriagemWithStatus(status);

        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                triagem::desconcluir
        );

        assertAll(
                () -> assertEquals("Triagem ainda não concluida.", exception.getMessage()),
                () -> assertEquals(status, triagem.getStatus()),
                () -> assertNull(triagem.getDataConclusao())
        );
    }

    // Testes do cancelamento

    @ParameterizedTest
    @EnumSource(value = Status.class, names = {"AGENDADA", "ATRASADA"})
    void shouldCancelEditableTriagem(Status status)
    {
        Triagem triagem = buildTriagemWithStatus(status);

        triagem.cancelarTriagem();

        assertAll(
                () -> assertEquals(Status.CANCELADA, triagem.getStatus()),
                () -> assertNull(triagem.getDataConclusao())
        );
    }

    @Test
    void shouldRejectCancellationOfConcludedTriagem()
    {
        Triagem triagem = buildTriagemWithStatus(Status.CONCLUIDA);
        LocalDateTime dataConclusao = triagem.getDataConclusao();

        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                triagem::cancelarTriagem
        );

        assertAll(
                () -> assertEquals("Não é possivel cancelar triagem já concluida.", exception.getMessage()),
                () -> assertEquals(Status.CONCLUIDA, triagem.getStatus()),
                () -> assertEquals(dataConclusao, triagem.getDataConclusao())
        );
    }

    @Test
    void shouldRejectCancellationOfAlreadyCancelledTriagem()
    {
        Triagem triagem = buildTriagemWithStatus(Status.CANCELADA);

        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                triagem::cancelarTriagem
        );

        assertAll(
                () -> assertEquals("Triagem já cancelada.", exception.getMessage()),
                () -> assertEquals(Status.CANCELADA, triagem.getStatus())
        );
    }

    // Testes da marcação como atrasada

    @Test
    void shouldMarkScheduledTriagemAsOverdueWhenScheduledDateHasPassed()
    {
        Triagem triagem = TriagemTestBuilder.newTriagem()
                .setDataAgendada(LocalDateTime.now().minusHours(1))
                .build();

        triagem.marcarComoVencida();

        assertEquals(Status.ATRASADA, triagem.getStatus());
    }

    @Test
    void shouldKeepScheduledTriagemWhenScheduledDateIsInTheFuture()
    {
        Triagem triagem = TriagemTestBuilder.newTriagem().build();

        triagem.marcarComoVencida();

        assertEquals(Status.AGENDADA, triagem.getStatus());
    }

    @ParameterizedTest
    @EnumSource(value = Status.class, names = {"ATRASADA", "CONCLUIDA", "CANCELADA"})
    void shouldNotChangeNonScheduledTriagemWhenMarkingAsOverdue(Status status)
    {
        Triagem triagem = buildTriagemWithStatus(status);
        LocalDateTime dataConclusao = triagem.getDataConclusao();

        triagem.marcarComoVencida();

        assertAll(
                () -> assertEquals(status, triagem.getStatus()),
                () -> assertEquals(dataConclusao, triagem.getDataConclusao())
        );
    }

    // Testes do reagendamento

    @ParameterizedTest
    @EnumSource(value = Status.class, names = {"AGENDADA", "ATRASADA"})
    void shouldRescheduleEditableTriagem(Status status)
    {
        Triagem triagem = buildTriagemWithStatus(status);
        LocalDateTime novaData = LocalDateTime.now().plusDays(10);

        triagem.reagendar(novaData);

        assertAll(
                () -> assertEquals(novaData, triagem.getDataAgendada()),
                () -> assertEquals(Status.AGENDADA, triagem.getStatus())
        );
    }

    @Test
    void shouldRejectNullDateWhenReschedulingTriagem()
    {
        Triagem triagem = TriagemTestBuilder.newTriagem().build();
        LocalDateTime dataAtual = triagem.getDataAgendada();

        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> triagem.reagendar(null)
        );

        assertAll(
                () -> assertEquals("A data agendada não pode ser nula.", exception.getMessage()),
                () -> assertEquals(dataAtual, triagem.getDataAgendada()),
                () -> assertEquals(Status.AGENDADA, triagem.getStatus())
        );
    }

    @Test
    void shouldRejectPastDateWhenReschedulingTriagem()
    {
        Triagem triagem = TriagemTestBuilder.newTriagem().build();
        LocalDateTime dataAtual = triagem.getDataAgendada();

        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> triagem.reagendar(LocalDateTime.now().minusDays(1))
        );

        assertAll(
                () -> assertEquals("A data agendada deve estar no futuro.", exception.getMessage()),
                () -> assertEquals(dataAtual, triagem.getDataAgendada()),
                () -> assertEquals(Status.AGENDADA, triagem.getStatus())
        );
    }

    @ParameterizedTest
    @EnumSource(value = Status.class, names = {"CONCLUIDA", "CANCELADA"})
    void shouldRejectReschedulingForFinalStatus(Status status)
    {
        Triagem triagem = buildTriagemWithStatus(status);
        LocalDateTime dataAtual = triagem.getDataAgendada();

        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> triagem.reagendar(LocalDateTime.now().plusDays(10))
        );

        assertAll(
                () -> assertEquals("Triagem já concluida ou cancelada.", exception.getMessage()),
                () -> assertEquals(dataAtual, triagem.getDataAgendada()),
                () -> assertEquals(status, triagem.getStatus())
        );
    }

    private Triagem buildTriagemWithStatus(Status status)
    {
        LocalDateTime now = LocalDateTime.now();
        TriagemTestBuilder builder = TriagemTestBuilder.newTriagem().setStatus(status);

        return switch (status)
        {
            case CONCLUIDA -> builder
                    .setDataAgendada(now.minusDays(2))
                    .setDataConclusao(now.minusDays(1))
                    .build();
            case ATRASADA -> builder
                    .setDataAgendada(now.minusDays(1))
                    .build();
            default -> builder.build();
        };
    }

}
