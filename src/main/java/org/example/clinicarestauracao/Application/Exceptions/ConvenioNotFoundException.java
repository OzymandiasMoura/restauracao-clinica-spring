package org.example.clinicarestauracao.Application.Exceptions;

public class ConvenioNotFoundException extends RuntimeException
{
    public ConvenioNotFoundException(String message)
    {
        super(message);
    }
}
