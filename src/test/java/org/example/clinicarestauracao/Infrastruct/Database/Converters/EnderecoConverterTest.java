package org.example.clinicarestauracao.Infrastruct.Database.Converters;

import org.example.clinicarestauracao.Builders.EnderecoTestBuilder;
import org.example.clinicarestauracao.Domain.ValueObjects.Endereco;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EnderecoConverterTest
{
    private final EnderecoConverter converter = new EnderecoConverter();

    @Test
    void shouldConvertEnderecoToDatabaseColumn()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().build();

        String resultado = converter.convertToDatabaseColumn(endereco);

        assertEquals("Rua das Flores, 123 - Centro - São Paulo/SP - Apartamento 42", resultado);
    }

    @Test
    void shouldReturnNullDatabaseColumnWhenEnderecoIsNull()
    {
        String resultado = converter.convertToDatabaseColumn(null);

        assertNull(resultado);
    }

    @Test
    void shouldConvertDatabaseColumnToEndereco()
    {
        Endereco resultado = converter.convertToEntityAttribute(
                "Rua das Flores, 123 - Centro - São Paulo/SP - Apartamento 42"
        );

        Endereco enderecoEsperado = EnderecoTestBuilder.newEndereco().build();
        assertEquals(enderecoEsperado, resultado);
    }

    @Test
    void shouldReturnNullEnderecoWhenDatabaseColumnIsNull()
    {
        Endereco resultado = converter.convertToEntityAttribute(null);

        assertNull(resultado);
    }
}
