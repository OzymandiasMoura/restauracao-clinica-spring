package org.example.clinicarestauracao.Infrastruct.Database.Audit;


import org.example.clinicarestauracao.Application.Exceptions.User.UserWithInvalidInformationException;
import org.example.clinicarestauracao.Domain.Entities.User;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class DatabaseAuditContext
{
    private final JdbcTemplate jdbcTemplate;

    public DatabaseAuditContext(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setCurrentActor()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof User user) || user.getId() == null)
        {
            throw new UserWithInvalidInformationException("Usuário autenticado é obrigatório para exclusão física.");
        }

        DatabaseType databaseType = databaseType();
        clearActor(databaseType);

        try
        {
            if (databaseType == DatabaseType.H2)
            {
                jdbcTemplate.queryForObject("SELECT SET(@audit_actor_id, ?)", Long.class, user.getId());
                jdbcTemplate.queryForObject("SELECT SET(@audit_actor_username, ?)", String.class, user.getUsername());
            }
            else
            {
                jdbcTemplate.queryForObject("SELECT set_config('app.audit_actor_id', ?, true)", String.class, user.getId().toString());
                jdbcTemplate.queryForObject("SELECT set_config('app.audit_actor_username', ?, true)", String.class, user.getUsername());
            }
        }
        catch (RuntimeException exception)
        {
            clearActor(databaseType);
            throw exception;
        }
    }

    public void clearActor()
    {
        clearActor(databaseType());
    }

    private void clearActor(DatabaseType databaseType)
    {
        if (databaseType == DatabaseType.H2)
        {
            jdbcTemplate.execute("SET @audit_actor_id = NULL");
            jdbcTemplate.execute("SET @audit_actor_username = NULL");
        }
        // PostgreSQL recebe set_config(..., true), cujo escopo termina automaticamente
        // no commit ou rollback da transação. Evita executar SQL em uma transação já abortada.
    }

    private DatabaseType databaseType()
    {
        String productName = jdbcTemplate.execute((ConnectionCallback<String>) connection ->
        {
            try
            {
                return connection.getMetaData().getDatabaseProductName();
            }
            catch (SQLException exception)
            {
                throw new IllegalStateException("Não foi possível identificar o banco de dados para auditoria.", exception);
            }
        });

        if (productName != null && productName.toLowerCase().contains("h2"))
        {
            return DatabaseType.H2;
        }
        if (productName != null && productName.toLowerCase().contains("postgresql"))
        {
            return DatabaseType.POSTGRESQL;
        }
        throw new IllegalStateException("Banco de dados não suportado pela auditoria: " + productName);
    }

    private enum DatabaseType
    {
        H2,
        POSTGRESQL
    }
}
