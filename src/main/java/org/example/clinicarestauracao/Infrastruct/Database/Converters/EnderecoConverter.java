package org.example.clinicarestauracao.Infrastruct.Database.Converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.example.clinicarestauracao.Domain.ValueObjects.Endereco;

@Converter
public class EnderecoConverter implements AttributeConverter<Endereco, String>
{
    @Override
    public String convertToDatabaseColumn(Endereco endereco)
    {
        if (endereco == null)
        {
            return null;
        }

        return endereco.toDatabaseString();
    }

    @Override
    public Endereco convertToEntityAttribute(String endereco)
    {
        if (endereco == null)
        {
            return null;
        }

        return Endereco.fromDatabaseString(endereco);
    }
}
