package org.example.clinicarestauracao.Infrastruct.Database.Audit;

import jakarta.transaction.Transactional;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class AuditRetentionScheduler
{
    private final JdbcTemplate jdbcTemplate;

    public AuditRetentionScheduler(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void removeExpiredAuditRecordsOnStartup(ApplicationReadyEvent event)
    {
        deleteExpiredAuditRecords();
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "UTC")
    @Transactional
    public void removeExpiredAuditRecords()
    {
        deleteExpiredAuditRecords();
    }

    private void deleteExpiredAuditRecords()
    {
        OffsetDateTime cutoff = OffsetDateTime.now(ZoneOffset.UTC).minusYears(1);
        jdbcTemplate.update("DELETE FROM AuditoriaExclusoes WHERE excluido_em < ?", cutoff);
    }
}
