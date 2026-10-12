package org.example.clinicarestauracao.Application.Exceptions.User;

public class UserNotFoundException extends RuntimeException
{
    public UserNotFoundException(String message)
    {
        super(message);
    }
}
