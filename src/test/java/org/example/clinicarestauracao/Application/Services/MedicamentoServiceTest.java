package org.example.clinicarestauracao.Application.Services;


import org.junit.jupiter.api.DisplayName;
import org.example.clinicarestauracao.Application.Exceptions.Medicamento.MedicamentoNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Medicamento.MedicamentoWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.MedicamentoRepository;
import org.example.clinicarestauracao.Application.Interfaces.MovimentacaoEstoqueRepository;
import org.example.clinicarestauracao.Domain.Entities.Medicamento;
import org.example.clinicarestauracao.Infrastruct.Database.Audit.DatabaseAuditContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class MedicamentoServiceTest
{
    @Mock
    private MedicamentoRepository repository;

    @Mock
    private MovimentacaoEstoqueRepository movimentacaoRepository;

    @Mock
    private DatabaseAuditContext audit;

    @InjectMocks
    private MedicamentoService service;

    @DisplayName("Deve auditoria exclusão de medicamento")
    @Test
    void shouldAuditDeletionOfMedicamento()
    {
        Medicamento medicamento = new Medicamento("Medicamento teste", new BigDecimal("12.50"), 10, 2);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(medicamento));
        Mockito.when(movimentacaoRepository.existsByMedicamentoId(1L)).thenReturn(false);

        service.delete(1L);

        var inOrder = Mockito.inOrder(repository, movimentacaoRepository, audit);
        inOrder.verify(repository).findById(1L);
        inOrder.verify(movimentacaoRepository).existsByMedicamentoId(1L);
        inOrder.verify(audit).setCurrentActor();
        inOrder.verify(repository).delete(Mockito.same(medicamento));
        inOrder.verify(repository).flush();
        inOrder.verify(audit).clearActor();
        Mockito.verifyNoMoreInteractions(repository, movimentacaoRepository, audit);
    }

    @DisplayName("Não deve definir auditoria ator quando medicamento não existir")
    @Test
    void shouldNotSetAuditActorWhenMedicamentoDoesNotExist()
    {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(MedicamentoNotFoundException.class, () -> service.delete(1L));

        Mockito.verify(repository).findById(1L);
        Mockito.verifyNoMoreInteractions(repository);
        Mockito.verifyNoInteractions(movimentacaoRepository);
        Mockito.verifyNoInteractions(audit);
    }

    @DisplayName("Não deve excluir medicamento com movimentações")
    @Test
    void shouldNotDeleteMedicamentoWithMovements()
    {
        Medicamento medicamento = new Medicamento("Medicamento teste", new BigDecimal("12.50"), 10, 2);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(medicamento));
        Mockito.when(movimentacaoRepository.existsByMedicamentoId(1L)).thenReturn(true);

        assertThrows(MedicamentoWithInvalidInformationException.class, () -> service.delete(1L));

        Mockito.verify(repository).findById(1L);
        Mockito.verify(movimentacaoRepository).existsByMedicamentoId(1L);
        Mockito.verify(repository, Mockito.never()).delete(Mockito.any());
        Mockito.verify(repository, Mockito.never()).flush();
        Mockito.verifyNoInteractions(audit);
    }

    @DisplayName("Deve limpar auditoria contexto quando medicamento exclusão falha")
    @Test
    void shouldClearAuditContextWhenMedicamentoDeletionFails()
    {
        Medicamento medicamento = new Medicamento("Medicamento teste", new BigDecimal("12.50"), 10, 2);
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(medicamento));
        Mockito.when(movimentacaoRepository.existsByMedicamentoId(1L)).thenReturn(false);
        Mockito.doThrow(new IllegalStateException("Falha ao excluir medicamento.")).when(repository).flush();

        assertThrows(IllegalStateException.class, () -> service.delete(1L));

        var inOrder = Mockito.inOrder(repository, movimentacaoRepository, audit);
        inOrder.verify(repository).findById(1L);
        inOrder.verify(movimentacaoRepository).existsByMedicamentoId(1L);
        inOrder.verify(audit).setCurrentActor();
        inOrder.verify(repository).delete(Mockito.same(medicamento));
        inOrder.verify(repository).flush();
        inOrder.verify(audit).clearActor();
        Mockito.verifyNoMoreInteractions(repository, movimentacaoRepository, audit);
    }

    // Testes countAllMedicamentos
    @DisplayName("Deve retornar a quantidade total de medicamentos cadastrados")
    @Test
    void shouldReturnTotalMedicamentosCountFromRepository()
    {
        Mockito.when(repository.count()).thenReturn(5L);

        Long resultado = service.countAllMedicamentos();

        assertEquals(5L, resultado);
        Mockito.verify(repository).count();
        Mockito.verifyNoMoreInteractions(repository);
        Mockito.verifyNoInteractions(movimentacaoRepository, audit);
    }

    // Testes countMedicamentosBelowMinimo
    @DisplayName("Deve retornar a quantidade de medicamentos abaixo do estoque mínimo")
    @Test
    void shouldReturnMedicamentosBelowMinimumStockCountFromRepository()
    {
        Mockito.when(repository.countMedicamentosAbaixoDoEstoqueMinimo()).thenReturn(2L);

        Long resultado = service.countMedicamentosBelowMinimo();

        assertEquals(2L, resultado);
        Mockito.verify(repository).countMedicamentosAbaixoDoEstoqueMinimo();
        Mockito.verifyNoMoreInteractions(repository);
        Mockito.verifyNoInteractions(movimentacaoRepository, audit);
    }
}
