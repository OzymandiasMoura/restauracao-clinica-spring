package org.example.clinicarestauracao.Application.Services;

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
}
