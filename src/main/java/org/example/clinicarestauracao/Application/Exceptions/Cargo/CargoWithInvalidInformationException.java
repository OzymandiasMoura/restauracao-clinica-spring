package org.example.clinicarestauracao.Application.Exceptions.Cargo;

public class CargoWithInvalidInformationException extends RuntimeException
{
    public CargoWithInvalidInformationException(String message)
    {
        super(message);
    }
}
