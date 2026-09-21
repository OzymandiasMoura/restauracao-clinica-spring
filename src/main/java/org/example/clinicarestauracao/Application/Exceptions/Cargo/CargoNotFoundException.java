package org.example.clinicarestauracao.Application.Exceptions.Cargo;

public class CargoNotFoundException extends RuntimeException
{
    public CargoNotFoundException(String message)
    {
        super(message);
    }
}
