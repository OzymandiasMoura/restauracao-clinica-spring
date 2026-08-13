package org.example.clinicarestauracao.Application.Exceptions;

public class UsernameAlredyInUseException extends RuntimeException
{
    public UsernameAlredyInUseException(String message)
    {
        super(message);
    }
}
