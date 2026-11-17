package org.example.clinicarestauracao.Application.Exceptions.User;

public class UsernameAlredyInUseException extends RuntimeException
{
    public UsernameAlredyInUseException(String message)
    {
        super(message);
    }
}
