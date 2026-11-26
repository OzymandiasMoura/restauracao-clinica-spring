package org.example.clinicarestauracao.Infrastruct.Database.Audit;



import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuditRetentionSchedulerTest
{
    @Mock
    private JdbcTemplate jdbcTemplate;

    @DisplayName("Deve excluir registros de auditoria com mais de um ano")
    @Test
    void shouldDeleteAuditRecordsOlderThanOneYear()
    {
        AuditRetentionScheduler scheduler = new AuditRetentionScheduler(jdbcTemplate);

        OffsetDateTime before = OffsetDateTime.now(ZoneOffset.UTC).minusYears(1);

        scheduler.removeExpiredAuditRecords();

        OffsetDateTime after = OffsetDateTime.now(ZoneOffset.UTC).minusYears(1);
        ArgumentCaptor<OffsetDateTime> cutoffCaptor =
                ArgumentCaptor.forClass(OffsetDateTime.class);

        verify(jdbcTemplate).update(eq("DELETE FROM AuditoriaExclusoes WHERE excluido_em < ?"), cutoffCaptor.capture());

        OffsetDateTime cutoff = cutoffCaptor.getValue();

        assertFalse(cutoff.isBefore(before));
        assertFalse(cutoff.isAfter(after));
        assertEquals(ZoneOffset.UTC, cutoff.getOffset());
    }

    @DisplayName("Deve excluir expirado auditoria registros quando aplicação é pronto")
    @Test
    void shouldDeleteExpiredAuditRecordsWhenApplicationIsReady()
    {
        AuditRetentionScheduler scheduler = new AuditRetentionScheduler(jdbcTemplate);

        scheduler.removeExpiredAuditRecordsOnStartup(null);

        verify(jdbcTemplate).update(
                eq("DELETE FROM AuditoriaExclusoes WHERE excluido_em < ?"),
                any(OffsetDateTime.class)
        );
    }
}
