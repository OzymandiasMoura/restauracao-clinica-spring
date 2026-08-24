package org.example.clinicarestauracao.Application.Exceptions;

public class FuncionarioWithInvalidInformationException extends RuntimeException
{
    public FuncionarioWithInvalidInformationException(String message)
    {
        super(message);
    }
}
