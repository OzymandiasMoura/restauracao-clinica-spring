package org.example.clinicarestauracao.Application.Scheduler;

import lombok.RequiredArgsConstructor;
import org.example.clinicarestauracao.Application.Services.TriagemService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TriagemScheduler {

    private final TriagemService triagemService;

    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "0 0 0 * * *", zone = "America/Sao_Paulo")
    public void verificarTriagensVencidas()
    {
        triagemService.atualizarTriagensVencidas();
    }
}

