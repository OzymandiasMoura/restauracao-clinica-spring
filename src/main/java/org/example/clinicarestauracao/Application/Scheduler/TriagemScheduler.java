package org.example.clinicarestauracao.Application.Scheduler;

import lombok.RequiredArgsConstructor;
import org.example.clinicarestauracao.Application.Services.TriagemService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TriagemScheduler {

    private final TriagemService triagemService;

    @Scheduled(fixedRate = 60000)
    public void verificarTriagensVencidas() {
        triagemService.atualizarTriagensVencidas();
    }
}

