package org.example.clinicarestauracao.Application.Exceptions.Convenio;

public class ConvenioNotFoundException extends RuntimeException
{
    public ConvenioNotFoundException(String message)
    {
        super(message);
    }
}
