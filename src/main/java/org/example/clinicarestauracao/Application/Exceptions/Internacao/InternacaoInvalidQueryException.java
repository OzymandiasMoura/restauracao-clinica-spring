package org.example.clinicarestauracao.Application.Exceptions.Internacao;

public class InternacaoInvalidQueryException extends RuntimeException
{
    public InternacaoInvalidQueryException(String message)
    {
        super(message);
    }
}
