package org.example.clinicarestauracao.Application.Scheduler;

import lombok.RequiredArgsConstructor;
import org.example.clinicarestauracao.Application.Services.InternacaoService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class InternacaoScheduler
{
    private static final ZoneId zoneId = ZoneId.of("America/Sao_Paulo");
    private final InternacaoService internacaoService;

    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "0 0 0 * * *", zone = "America/Sao_Paulo")
    public void atualizarEtapaTratamento()
    {
        internacaoService.updateTreatmentStage(LocalDate.now(zoneId));
    }
}
