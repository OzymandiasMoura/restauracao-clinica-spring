package org.example.clinicarestauracao.Application.Exceptions.Internacao;

public class InternacaoNotFoundException extends RuntimeException
{
    public InternacaoNotFoundException(String message)
    {
        super(message);
    }
}
