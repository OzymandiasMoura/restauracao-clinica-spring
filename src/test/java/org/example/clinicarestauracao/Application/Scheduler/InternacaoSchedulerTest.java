package org.example.clinicarestauracao.Application.Scheduler;

import org.example.clinicarestauracao.Application.Services.InternacaoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InternacaoSchedulerTest
{
    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");

    @Mock
    private InternacaoService internacaoService;
    @InjectMocks
    private InternacaoScheduler scheduler;

    @Test
    void shouldUpdateTreatmentStagesUsingCurrentSaoPauloDate()
    {
        LocalDate dataAntesDaExecucao = LocalDate.now(ZONE_ID);
        scheduler.atualizarEtapaTratamento();
        LocalDate dataDepoisDaExecucao = LocalDate.now(ZONE_ID);

        ArgumentCaptor<LocalDate> captor = ArgumentCaptor.forClass(LocalDate.class);

        Mockito.verify(internacaoService).updateTreatmentStage(captor.capture());

        LocalDate dataEnviada = captor.getValue();

        assertAll(
                () -> assertNotNull(dataEnviada),
                () -> assertFalse(dataEnviada.isBefore(dataAntesDaExecucao)),
                () -> assertFalse(dataEnviada.isAfter(dataDepoisDaExecucao))
        );

        Mockito.verifyNoMoreInteractions(internacaoService);
    }
}
