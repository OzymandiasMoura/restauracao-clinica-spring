package org.example.clinicarestauracao.Application.Exceptions;

public class ModalidadeNotFoundException extends RuntimeException
{
    public ModalidadeNotFoundException(String message)
    {
        super(message);
    }
}
