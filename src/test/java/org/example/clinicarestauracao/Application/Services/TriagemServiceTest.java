package org.example.clinicarestauracao.Application.Services;


import org.junit.jupiter.api.DisplayName;
import org.example.clinicarestauracao.Application.Exceptions.Triagem.TriagemNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Triagem.TriagemWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.TriagemRepository;
import org.example.clinicarestauracao.Builders.ConvenioTestBuilder;
import org.example.clinicarestauracao.Builders.TriagemTestBuilder;
import org.example.clinicarestauracao.Domain.Entities.Convenio;
import org.example.clinicarestauracao.Domain.Entities.Triagem;
import org.example.clinicarestauracao.Domain.Enums.Status;
import org.example.clinicarestauracao.Infrastruct.Database.Audit.DatabaseAuditContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class TriagemServiceTest
{
    @Mock
    private TriagemRepository repository;

    @Mock
    private DatabaseAuditContext audit;

    @InjectMocks
    private TriagemService service;

    // Testes do findAllTriagens

    @DisplayName("Deve buscar todas as triagens com sucesso")
    @Test
    void shouldFindAllTriagensSuccessfully()
    {
        Triagem primeiraTriagem = TriagemTestBuilder.newTriagem().setId(1L).build();
        Triagem segundaTriagem = TriagemTestBuilder.newTriagem().setId(2L).setNomePossivelAcolhido("Ana Souza").build();
        List<Triagem> triagens = List.of(primeiraTriagem, segundaTriagem);

        Mockito.when(repository.findAll()).thenReturn(triagens);

        List<Triagem> response = service.findAllTriagens();

        assertAll(
                () -> assertSame(triagens, response),
                () -> assertEquals(2, response.size()),
                () -> assertSame(primeiraTriagem, response.getFirst()),
                () -> assertSame(segundaTriagem, response.get(1))
        );
        Mockito.verify(repository).findAll();
    }

    @DisplayName("Deve retornar uma lista vazia quando não há triagens")
    @Test
    void shouldReturnEmptyListWhenThereAreNoTriagens()
    {
        List<Triagem> triagens = List.of();
        Mockito.when(repository.findAll()).thenReturn(triagens);

        List<Triagem> response = service.findAllTriagens();

        assertSame(triagens, response);
        assertTrue(response.isEmpty());
        Mockito.verify(repository).findAll();
    }

    // Testes do findTriagemById

    @DisplayName("Deve buscar triagem por ID com sucesso")
    @Test
    void shouldFindTriagemByIdSuccessfully()
    {
        Triagem triagem = TriagemTestBuilder.newTriagem().setId(1L).build();
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(triagem));

        Triagem response = service.findTriagemById(1L);

        assertSame(triagem, response);
        Mockito.verify(repository).findById(1L);
    }

    @DisplayName("Deve lançar quando triagem não existir")
    @Test
    void shouldThrowWhenTriagemDoesNotExist()
    {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        TriagemNotFoundException exception = assertThrows(
                TriagemNotFoundException.class,
                () -> service.findTriagemById(1L)
        );

        assertEquals("Triagem não encontrada.", exception.getMessage());
        Mockito.verify(repository).findById(1L);
    }

    @DisplayName("Deve rejeitar inválido triagem ID")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldRejectInvalidTriagemId(Long id)
    {
        TriagemNotFoundException exception = assertThrows(
                TriagemNotFoundException.class,
                () -> service.findTriagemById(id)
        );

        assertEquals("Triagem não encontrada.", exception.getMessage());
        Mockito.verifyNoInteractions(repository);
    }

    // Testes do findByName

    @DisplayName("Deve buscar triagens por normalizado parcial nome com sucesso")
    @Test
    void shouldFindTriagensByNormalizedPartialNameSuccessfully()
    {
        Triagem primeiraTriagem = TriagemTestBuilder.newTriagem().setId(1L).setNomePossivelAcolhido("Maria Silva").build();
        Triagem segundaTriagem = TriagemTestBuilder.newTriagem().setId(2L).setNomePossivelAcolhido("Maria Souza").build();
        List<Triagem> triagens = List.of(primeiraTriagem, segundaTriagem);
        Mockito.when(repository.findByNomePossivelAcolhidoContainingIgnoreCase("maria")).thenReturn(triagens);

        List<Triagem> response = service.findTriagemByName("  maria  ");

        assertAll(
                () -> assertSame(triagens, response),
                () -> assertEquals(2, response.size()),
                () -> assertSame(primeiraTriagem, response.getFirst()),
                () -> assertSame(segundaTriagem, response.get(1))
        );
        Mockito.verify(repository).findByNomePossivelAcolhidoContainingIgnoreCase("maria");
    }

    @DisplayName("Deve lançar quando nenhum triagem corresponde nome")
    @Test
    void shouldThrowWhenNoTriagemMatchesName()
    {
        Mockito.when(repository.findByNomePossivelAcolhidoContainingIgnoreCase("Maria")).thenReturn(List.of());

        TriagemNotFoundException exception = assertThrows(
                TriagemNotFoundException.class,
                () -> service.findTriagemByName("Maria")
        );

        assertEquals("Triagens não encontradas.", exception.getMessage());
        Mockito.verify(repository).findByNomePossivelAcolhidoContainingIgnoreCase("Maria");
    }

    @DisplayName("Deve rejeitar inválido triagem nome")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
    void shouldRejectInvalidTriagemName(String nome)
    {
        TriagemNotFoundException exception = assertThrows(
                TriagemNotFoundException.class,
                () -> service.findTriagemByName(nome)
        );

        assertEquals("Triagens não encontradas.", exception.getMessage());
        Mockito.verifyNoInteractions(repository);
    }

    // Testes do findByStatus

    @DisplayName("Deve buscar triagens por estado com sucesso")
    @Test
    void shouldFindTriagensByStatusSuccessfully()
    {
        Triagem primeiraTriagem = TriagemTestBuilder.newTriagem().setId(1L).build();
        Triagem segundaTriagem = TriagemTestBuilder.newTriagem().setId(2L).build();
        List<Triagem> triagens = List.of(primeiraTriagem, segundaTriagem);
        Mockito.when(repository.findByStatus(Status.AGENDADA)).thenReturn(triagens);

        List<Triagem> response = service.findTriagemByStatus(Status.AGENDADA);

        assertSame(triagens, response);
        assertEquals(2, response.size());
        Mockito.verify(repository).findByStatus(Status.AGENDADA);
    }

    @DisplayName("Deve lançar quando nenhum triagem corresponde estado")
    @Test
    void shouldThrowWhenNoTriagemMatchesStatus()
    {
        Mockito.when(repository.findByStatus(Status.AGENDADA)).thenReturn(List.of());

        TriagemNotFoundException exception = assertThrows(
                TriagemNotFoundException.class,
                () -> service.findTriagemByStatus(Status.AGENDADA)
        );

        assertEquals("Triagens não encontradas.", exception.getMessage());
        Mockito.verify(repository).findByStatus(Status.AGENDADA);
    }

    @DisplayName("Deve rejeitar estado nulo")
    @Test
    void shouldRejectNullStatus()
    {
        TriagemNotFoundException exception = assertThrows(
                TriagemNotFoundException.class,
                () -> service.findTriagemByStatus(null)
        );

        assertEquals("Triagem não encontrada.", exception.getMessage());
        Mockito.verifyNoInteractions(repository);
    }

    // Testes do createTriagem

    @DisplayName("Deve criar triagem com sucesso")
    @Test
    void shouldCreateTriagemSuccessfully()
    {
        Triagem toCreate = TriagemTestBuilder.newTriagem().buildForCreate();
        Triagem saved = TriagemTestBuilder.newTriagem().setId(10L).build();
        Mockito.when(repository.save(toCreate)).thenReturn(saved);

        Triagem response = service.createTriagem(toCreate);

        assertSame(saved, response);
        Mockito.verify(repository).save(toCreate);
    }

    @DisplayName("Deve rejeitar nulo triagem quando criando")
    @Test
    void shouldRejectNullTriagemWhenCreating()
    {
        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> service.createTriagem(null)
        );

        assertEquals("Informações de triagem não pode ser nula.", exception.getMessage());
        Mockito.verifyNoInteractions(repository);
    }

    // Testes do updateTriagem

    @DisplayName("Deve atualizar triagem com sucesso")
    @Test
    void shouldUpdateTriagemSuccessfully()
    {
        Triagem existing = TriagemTestBuilder.newTriagem().setId(1L).build();
        Convenio novoConvenio = new ConvenioTestBuilder()
                .setId(2L)
                .setDescricao("Novo convênio")
                .setCor("#123456")
                .build();
        LocalDateTime novaData = LocalDateTime.now().plusDays(15);
        Triagem toUpdate = TriagemTestBuilder.newTriagem()
                .setConvenio(novoConvenio)
                .setNomePossivelAcolhido("Ana Souza")
                .setTelefone("1133334444")
                .setDataAgendada(novaData)
                .setObservacaoEntrevista("Observação atualizada")
                .buildForCreate();
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(repository.save(existing)).thenReturn(existing);

        Triagem response = service.updateTriagem(1L, toUpdate);

        assertAll(
                () -> assertSame(existing, response),
                () -> assertSame(novoConvenio, response.getConvenio()),
                () -> assertEquals("Ana Souza", response.getNomePossivelAcolhido()),
                () -> assertEquals("1133334444", response.getTelefone()),
                () -> assertEquals(novaData, response.getDataAgendada()),
                () -> assertEquals("Observação atualizada", response.getObservacaoEntrevista()),
                () -> assertEquals(Status.AGENDADA, response.getStatus())
        );
        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).save(existing);
    }

    @DisplayName("Deve rejeitar nulo triagem quando atualizando")
    @Test
    void shouldRejectNullTriagemWhenUpdating()
    {
        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> service.updateTriagem(1L, null)
        );

        assertEquals("Informações de triagem não podem ser nulas.", exception.getMessage());
        Mockito.verifyNoInteractions(repository);
    }

    @DisplayName("Deve rejeitar ID inválido quando atualizando")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1})
    void shouldRejectInvalidIdWhenUpdating(Long id)
    {
        Triagem toUpdate = TriagemTestBuilder.newTriagem().buildForCreate();

        TriagemNotFoundException exception = assertThrows(
                TriagemNotFoundException.class,
                () -> service.updateTriagem(id, toUpdate)
        );

        assertEquals("Triagem não encontrada.", exception.getMessage());
        Mockito.verifyNoInteractions(repository);
    }

    @DisplayName("Deve lançar quando triagem para atualizar não existir")
    @Test
    void shouldThrowWhenTriagemToUpdateDoesNotExist()
    {
        Triagem toUpdate = TriagemTestBuilder.newTriagem().buildForCreate();
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TriagemNotFoundException.class, () -> service.updateTriagem(1L, toUpdate));

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(any());
    }

    @DisplayName("Deve rejeitar a atualização de concluído triagem")
    @Test
    void shouldRejectUpdateOfConcludedTriagem()
    {
        Triagem existing = buildTriagemWithStatus(Status.CONCLUIDA);
        Triagem toUpdate = TriagemTestBuilder.newTriagem().buildForCreate();
        LocalDateTime dataConclusao = existing.getDataConclusao();
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(existing));

        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> service.updateTriagem(1L, toUpdate)
        );

        assertAll(
                () -> assertEquals("Uma triagem concluída deve ser desconcluída antes de ser alterada.", exception.getMessage()),
                () -> assertEquals(Status.CONCLUIDA, existing.getStatus()),
                () -> assertEquals(dataConclusao, existing.getDataConclusao())
        );
        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(any());
    }

    @DisplayName("Deve rejeitar a atualização de cancelado triagem")
    @Test
    void shouldRejectUpdateOfCancelledTriagem()
    {
        Triagem existing = buildTriagemWithStatus(Status.CANCELADA);
        Triagem toUpdate = TriagemTestBuilder.newTriagem().buildForCreate();
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(existing));

        TriagemWithInvalidInformationException exception = assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> service.updateTriagem(1L, toUpdate)
        );

        assertAll(
                () -> assertEquals("Triagem concluída ou cancelada não pode ser alterada.", exception.getMessage()),
                () -> assertEquals(Status.CANCELADA, existing.getStatus())
        );
        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(any());
    }

    // Testes do deleteTriagem

    @DisplayName("Deve excluir triagem com sucesso")
    @Test
    void shouldDeleteTriagemSuccessfully()
    {
        Triagem triagem = TriagemTestBuilder.newTriagem().setId(1L).build();
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(triagem));

        service.deleteTriagem(1L);

        var inOrder = Mockito.inOrder(repository, audit);
        inOrder.verify(repository).findById(1L);
        inOrder.verify(audit).setCurrentActor();
        inOrder.verify(repository).delete(triagem);
        inOrder.verify(repository).flush();
        inOrder.verify(audit).clearActor();
    }

    @DisplayName("Deve rejeitar ID inválido quando excluindo")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1})
    void shouldRejectInvalidIdWhenDeleting(Long id)
    {
        TriagemNotFoundException exception = assertThrows(
                TriagemNotFoundException.class,
                () -> service.deleteTriagem(id)
        );

        assertEquals("Triagem não encontrada.", exception.getMessage());
        Mockito.verifyNoInteractions(repository, audit);
    }

    @DisplayName("Deve lançar quando triagem para excluir não existir")
    @Test
    void shouldThrowWhenTriagemToDeleteDoesNotExist()
    {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TriagemNotFoundException.class, () -> service.deleteTriagem(1L));

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).delete(any());
        Mockito.verifyNoInteractions(audit);
    }

    // Testes da conclusão

    @DisplayName("Deve concluir triagem com atual data com sucesso")
    @Test
    void shouldConcludeTriagemWithCurrentDateSuccessfully()
    {
        Triagem triagem = TriagemTestBuilder.newTriagem()
                .setDataAgendada(LocalDateTime.now().minusDays(1))
                .build();
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(triagem));
        Mockito.when(repository.save(triagem)).thenReturn(triagem);
        LocalDateTime before = LocalDateTime.now();

        Triagem response = service.concluir(1L);

        LocalDateTime after = LocalDateTime.now();
        assertAll(
                () -> assertSame(triagem, response),
                () -> assertEquals(Status.CONCLUIDA, response.getStatus()),
                () -> assertFalse(response.getDataConclusao().isBefore(before)),
                () -> assertFalse(response.getDataConclusao().isAfter(after))
        );
        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).save(triagem);
    }

    @DisplayName("Deve concluir triagem com fornecido data com sucesso")
    @Test
    void shouldConcludeTriagemWithProvidedDateSuccessfully()
    {
        LocalDateTime dataAgendada = LocalDateTime.now().minusDays(2);
        LocalDateTime dataConclusao = LocalDateTime.now().minusDays(1);
        Triagem triagem = TriagemTestBuilder.newTriagem().setDataAgendada(dataAgendada).build();
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(triagem));
        Mockito.when(repository.save(triagem)).thenReturn(triagem);

        Triagem response = service.concluir(1L, dataConclusao);

        assertAll(
                () -> assertSame(triagem, response),
                () -> assertEquals(Status.CONCLUIDA, response.getStatus()),
                () -> assertEquals(dataConclusao, response.getDataConclusao())
        );
        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).save(triagem);
    }

    @DisplayName("Não deve salvar quando conclusão é rejeitado")
    @Test
    void shouldNotSaveWhenConclusionIsRejected()
    {
        Triagem triagem = buildTriagemWithStatus(Status.CONCLUIDA);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(triagem));

        assertThrows(TriagemWithInvalidInformationException.class, () -> service.concluir(1L));

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(any());
    }

    // Testes da desconclusão

    @DisplayName("Deve reabrir a triagem concluída com sucesso")
    @Test
    void shouldUndoTriagemConclusionSuccessfully()
    {
        Triagem triagem = buildTriagemWithStatus(Status.CONCLUIDA);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(triagem));
        Mockito.when(repository.save(triagem)).thenReturn(triagem);

        Triagem response = service.desconcluir(1L);

        assertAll(
                () -> assertSame(triagem, response),
                () -> assertEquals(Status.ATRASADA, response.getStatus()),
                () -> assertNull(response.getDataConclusao())
        );
        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).save(triagem);
    }

    @DisplayName("Não deve salvar quando desfazer conclusão é rejeitado")
    @Test
    void shouldNotSaveWhenUndoConclusionIsRejected()
    {
        Triagem triagem = buildTriagemWithStatus(Status.AGENDADA);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(triagem));

        assertThrows(TriagemWithInvalidInformationException.class, () -> service.desconcluir(1L));

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(any());
    }

    // Testes do reagendamento

    @DisplayName("Deve reagendar triagem com sucesso")
    @Test
    void shouldRescheduleTriagemSuccessfully()
    {
        Triagem triagem = buildTriagemWithStatus(Status.ATRASADA);
        LocalDateTime novaData = LocalDateTime.now().plusDays(10);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(triagem));
        Mockito.when(repository.save(triagem)).thenReturn(triagem);

        Triagem response = service.reagendar(1L, novaData);

        assertAll(
                () -> assertSame(triagem, response),
                () -> assertEquals(novaData, response.getDataAgendada()),
                () -> assertEquals(Status.AGENDADA, response.getStatus())
        );
        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).save(triagem);
    }

    @DisplayName("Não deve salvar quando reagendamento final triagem é rejeitado")
    @ParameterizedTest
    @EnumSource(value = Status.class, names = {"CONCLUIDA", "CANCELADA"})
    void shouldNotSaveWhenReschedulingFinalTriagemIsRejected(Status status)
    {
        Triagem triagem = buildTriagemWithStatus(status);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(triagem));

        assertThrows(
                TriagemWithInvalidInformationException.class,
                () -> service.reagendar(1L, LocalDateTime.now().plusDays(10))
        );

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(any());
    }

    // Testes do cancelamento

    @DisplayName("Deve cancelar triagem com sucesso")
    @Test
    void shouldCancelTriagemSuccessfully()
    {
        Triagem triagem = buildTriagemWithStatus(Status.AGENDADA);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(triagem));
        Mockito.when(repository.save(triagem)).thenReturn(triagem);

        Triagem response = service.cancelarTriagem(1L);

        assertSame(triagem, response);
        assertEquals(Status.CANCELADA, response.getStatus());
        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).save(triagem);
    }

    @DisplayName("Não deve salvar quando cancelamento é rejeitado")
    @ParameterizedTest
    @EnumSource(value = Status.class, names = {"CONCLUIDA", "CANCELADA"})
    void shouldNotSaveWhenCancellationIsRejected(Status status)
    {
        Triagem triagem = buildTriagemWithStatus(status);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(triagem));

        assertThrows(TriagemWithInvalidInformationException.class, () -> service.cancelarTriagem(1L));

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(any());
    }

    // Testes do descancelamento

    @DisplayName("Deve desfazer cancelamento como agendado com sucesso")
    @Test
    void shouldUndoCancellationAsScheduledSuccessfully()
    {
        Triagem triagem = buildTriagemWithStatus(Status.CANCELADA);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(triagem));
        Mockito.when(repository.save(triagem)).thenReturn(triagem);

        Triagem response = service.descancelarTriagem(1L);

        assertSame(triagem, response);
        assertEquals(Status.AGENDADA, response.getStatus());
        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).save(triagem);
    }

    @DisplayName("Deve desfazer cancelamento como vencida com sucesso")
    @Test
    void shouldUndoCancellationAsOverdueSuccessfully()
    {
        Triagem triagem = TriagemTestBuilder.newTriagem()
                .setDataAgendada(LocalDateTime.now().minusDays(1))
                .setStatus(Status.CANCELADA)
                .build();
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(triagem));
        Mockito.when(repository.save(triagem)).thenReturn(triagem);

        Triagem response = service.descancelarTriagem(1L);

        assertSame(triagem, response);
        assertEquals(Status.ATRASADA, response.getStatus());
        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).save(triagem);
    }

    @DisplayName("Não deve salvar quando desfazer cancelamento é rejeitado")
    @ParameterizedTest
    @EnumSource(value = Status.class, names = {"AGENDADA", "ATRASADA", "CONCLUIDA"})
    void shouldNotSaveWhenUndoCancellationIsRejected(Status status)
    {
        Triagem triagem = buildTriagemWithStatus(status);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(triagem));

        assertThrows(TriagemWithInvalidInformationException.class, () -> service.descancelarTriagem(1L));

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(any());
    }

    // Testes da atualização de triagens vencidas

    @DisplayName("Deve atualizar todos vencida triagens com sucesso")
    @Test
    void shouldUpdateAllOverdueTriagensSuccessfully()
    {
        Triagem primeiraTriagem = TriagemTestBuilder.newTriagem()
                .setId(1L)
                .setDataAgendada(LocalDateTime.now().minusHours(2))
                .build();
        Triagem segundaTriagem = TriagemTestBuilder.newTriagem()
                .setId(2L)
                .setDataAgendada(LocalDateTime.now().minusHours(1))
                .build();
        List<Triagem> triagens = List.of(primeiraTriagem, segundaTriagem);
        Mockito.when(repository.findByStatusAndDataAgendadaBefore(eq(Status.AGENDADA), any(LocalDateTime.class)))
                .thenReturn(triagens);

        service.atualizarTriagensVencidas();

        assertAll(
                () -> assertEquals(Status.ATRASADA, primeiraTriagem.getStatus()),
                () -> assertEquals(Status.ATRASADA, segundaTriagem.getStatus())
        );
        Mockito.verify(repository).findByStatusAndDataAgendadaBefore(eq(Status.AGENDADA), any(LocalDateTime.class));
        Mockito.verify(repository).saveAll(triagens);
    }

    @DisplayName("Deve salvar vazio listar quando não há vencida triagens")
    @Test
    void shouldSaveEmptyListWhenThereAreNoOverdueTriagens()
    {
        List<Triagem> triagens = List.of();
        Mockito.when(repository.findByStatusAndDataAgendadaBefore(eq(Status.AGENDADA), any(LocalDateTime.class)))
                .thenReturn(triagens);

        service.atualizarTriagensVencidas();

        Mockito.verify(repository).findByStatusAndDataAgendadaBefore(eq(Status.AGENDADA), any(LocalDateTime.class));
        Mockito.verify(repository).saveAll(triagens);
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
