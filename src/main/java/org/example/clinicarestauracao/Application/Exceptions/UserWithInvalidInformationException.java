package org.example.clinicarestauracao.Application.Exceptions;

public class UserWithInvalidInformationException extends RuntimeException
{
    public UserWithInvalidInformationException(String message)
    {
        super(message);
    }
}
