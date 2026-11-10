package org.example.clinicarestauracao.Application.Scheduler;

import lombok.RequiredArgsConstructor;
import org.example.clinicarestauracao.Application.Services.TriagemService;
import org.example.clinicarestauracao.Domain.Enums.TriagemStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class TriagemScheduler {

    private final TriagemService triagemService;
    private boolean ativo;
    private TriagemStatus triagemStatus;
    private Date dataAgendada;

    @Scheduled(cron = "0 0 0 * * *", zone = "America/Sao_Paulo")
    public void marcarComoVencida() {
        if (this.ativo
                && this.triagemStatus == TriagemStatus.AGENDADA
                && this.dataAgendada.toLocalDate().isBefore(LocalDate.now())) {

            this.triagemStatus = TriagemStatus.ATRASADA;
        }
    }
}

