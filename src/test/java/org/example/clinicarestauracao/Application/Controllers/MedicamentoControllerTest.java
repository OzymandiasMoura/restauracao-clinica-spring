package org.example.clinicarestauracao.Application.Controllers;

import org.example.clinicarestauracao.Application.Services.MedicamentoService;
import org.example.clinicarestauracao.Application.Services.MovimentacaoEstoqueService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MedicamentoControllerTest
{
    @Mock
    private MedicamentoService medicamentoService;

    @Mock
    private MovimentacaoEstoqueService movimentacaoEstoqueService;

    @InjectMocks
    private MedicamentoController controller;

    // Testes countAllMedicamentos
    @DisplayName("Deve retornar a quantidade total de medicamentos cadastrados")
    @Test
    void shouldReturnTotalMedicamentosCount()
    {
        Mockito.when(medicamentoService.countAllMedicamentos()).thenReturn(12L);

        ResponseEntity<Long> response = controller.countAllMedicamentos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(12L, response.getBody());
        Mockito.verify(medicamentoService).countAllMedicamentos();
        Mockito.verifyNoMoreInteractions(medicamentoService);
        Mockito.verifyNoInteractions(movimentacaoEstoqueService);
    }

    // Testes countMedicamentosBelowMinimio
    @DisplayName("Deve retornar a quantidade de medicamentos abaixo do estoque mínimo")
    @Test
    void shouldReturnMedicamentosBelowMinimumStockCount()
    {
        Mockito.when(medicamentoService.countMedicamentosBelowMinimo()).thenReturn(3L);

        ResponseEntity<Long> response = controller.countMedicamentosBelowMinimio();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3L, response.getBody());
        Mockito.verify(medicamentoService).countMedicamentosBelowMinimo();
        Mockito.verifyNoMoreInteractions(medicamentoService);
        Mockito.verifyNoInteractions(movimentacaoEstoqueService);
    }
}
