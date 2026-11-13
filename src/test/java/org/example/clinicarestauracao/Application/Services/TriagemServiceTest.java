package org.example.clinicarestauracao.Application.Services;

import org.example.clinicarestauracao.Application.Exceptions.Triagem.TriagemNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Triagem.TriagemWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.TriagemRepository;
import org.example.clinicarestauracao.Builders.ConvenioTestBuilder;
import org.example.clinicarestauracao.Builders.TriagemTestBuilder;
import org.example.clinicarestauracao.Domain.Entities.Convenio;
import org.example.clinicarestauracao.Domain.Entities.Triagem;
import org.example.clinicarestauracao.Domain.Enums.Status;
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

    @InjectMocks
    private TriagemService service;

    // Testes do findAllTriagens

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

    @Test
    void shouldFindTriagemByIdSuccessfully()
    {
        Triagem triagem = TriagemTestBuilder.newTriagem().setId(1L).build();
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(triagem));

        Triagem response = service.findTriagemById(1L);

        assertSame(triagem, response);
        Mockito.verify(repository).findById(1L);
    }

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

    @Test
    void shouldThrowWhenTriagemToUpdateDoesNotExist()
    {
        Triagem toUpdate = TriagemTestBuilder.newTriagem().buildForCreate();
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TriagemNotFoundException.class, () -> service.updateTriagem(1L, toUpdate));

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(any());
    }

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

    @Test
    void shouldDeleteTriagemSuccessfully()
    {
        Triagem triagem = TriagemTestBuilder.newTriagem().setId(1L).build();
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(triagem));

        service.deleteTriagem(1L);

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).delete(triagem);
    }

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
        Mockito.verifyNoInteractions(repository);
    }

    @Test
    void shouldThrowWhenTriagemToDeleteDoesNotExist()
    {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TriagemNotFoundException.class, () -> service.deleteTriagem(1L));

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).delete(any());
    }

    // Testes da conclusão

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
