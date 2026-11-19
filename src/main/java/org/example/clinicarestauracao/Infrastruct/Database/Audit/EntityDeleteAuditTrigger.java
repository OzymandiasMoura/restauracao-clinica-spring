package org.example.clinicarestauracao.Infrastruct.Database.Audit;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class EntityDeleteAuditTrigger implements Trigger
{
    private record DescriptionField(String columnName, String label) {}

    private record AuditConfiguration(String tableName, List<DescriptionField> descriptionFields) {}

    private static final Map<String, AuditConfiguration> CONFIGURATIONS = Map.ofEntries(
            Map.entry("acolhidos", configuration("Acolhidos", field("nome", "Nome"))),
            Map.entry("cargos", configuration("Cargos", field("nome", "Nome"))),
            Map.entry("convenios", configuration("Convenios", field("descricao", "Descrição"))),
            Map.entry("funcionarios", configuration("Funcionarios", field("nome", "Nome"))),
            Map.entry("internacao", configuration("Internacao",
                    field("acolhido_id", "Acolhido ID"),
                    field("convenio_id", "Convênio ID"),
                    field("data_entrada", "Data de entrada"))),
            Map.entry("medicamento", configuration("Medicamento", field("nome", "Nome"))),
            Map.entry("responsaveis", configuration("Responsaveis", field("nome", "Nome"))),
            Map.entry("triagem", configuration("Triagem", field("nome_possivel_acolhido", "Nome possível do acolhido"))),
            Map.entry("usuarios", configuration("Usuarios", field("username", "Username")))
    );

    private AuditConfiguration configuration;
    private int idColumnIndex = -1;
    private Map<String, Integer> columnIndexes;

    private static AuditConfiguration configuration(String tableName, DescriptionField... descriptionFields)
    {
        return new AuditConfiguration(tableName, Arrays.asList(descriptionFields));
    }

    private static DescriptionField field(String columnName, String label)
    {
        return new DescriptionField(columnName, label);
    }

    @Override
    public void init(Connection connection, String schemaName, String triggerName, String tableName, boolean before, int type) throws SQLException
    {
        configuration = CONFIGURATIONS.get(tableName.toLowerCase(Locale.ROOT));
        if (configuration == null)
        {
            throw new SQLException("Não há configuração de auditoria para a tabela " + tableName);
        }

        columnIndexes = new java.util.HashMap<>();
        try (ResultSet columns = connection.getMetaData().getColumns(null, schemaName, tableName, null))
        {
            while (columns.next())
            {
                String columnName = columns.getString("COLUMN_NAME").toLowerCase(Locale.ROOT);
                columnIndexes.put(columnName, columns.getInt("ORDINAL_POSITION") - 1);
            }
        }

        idColumnIndex = columnIndexes.getOrDefault("id", -1);
        if (idColumnIndex < 0)
        {
            throw new SQLException("A coluna id não foi encontrada na tabela " + tableName);
        }

        for (DescriptionField field : configuration.descriptionFields())
        {
            if (!columnIndexes.containsKey(field.columnName()))
            {
                throw new SQLException("A coluna " + field.columnName() + " não foi encontrada na tabela " + tableName);
            }
        }
    }

    @Override
    public void fire(Connection connection, Object[] oldRow, Object[] newRow) throws SQLException
    {
        Object actorId;
        String actorUsername;

        try (PreparedStatement statement = connection.prepareStatement("SELECT @audit_actor_id, @audit_actor_username"); ResultSet result = statement.executeQuery())
        {
            result.next();
            actorId = result.getObject(1);
            actorUsername = result.getString(2);
        }

        if (actorId == null || actorUsername == null || actorUsername.isBlank())
        {
            throw new SQLException("A exclusão física exige um usuário autenticado.");
        }

        if (idColumnIndex < 0 || idColumnIndex >= oldRow.length)
        {
            throw new SQLException("Não foi possível localizar o id do registro excluído.");
        }

        StringBuilder description = new StringBuilder();
        for (DescriptionField field : configuration.descriptionFields())
        {
            int columnIndex = columnIndexes.get(field.columnName());
            if (columnIndex < 0 || columnIndex >= oldRow.length)
            {
                throw new SQLException("Não foi possível localizar a coluna " + field.columnName() + " do registro excluído.");
            }
            if (description.length() > 0)
            {
                description.append(" | ");
            }
            description.append(field.label()).append(": ").append(Objects.toString(oldRow[columnIndex], ""));
        }

        String sql = """
                INSERT INTO AuditoriaExclusoes
                    (tabela, registro_id, registro_descricao, excluido_em, ator_user_id, ator_username)
                VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setString(1, configuration.tableName());
            statement.setString(2, Objects.toString(oldRow[idColumnIndex]));
            statement.setString(3, description.toString());
            statement.setObject(4, actorId, Types.BIGINT);
            statement.setString(5, actorUsername);
            statement.executeUpdate();
        }
    }
}
