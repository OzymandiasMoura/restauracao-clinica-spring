package org.example.clinicarestauracao.Application.Exceptions.User;

public class UserWithInvalidInformationException extends RuntimeException
{
    public UserWithInvalidInformationException(String message)
    {
        super(message);
    }
}
