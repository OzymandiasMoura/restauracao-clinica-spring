package org.example.clinicarestauracao.Application.Exceptions.Funcionario;

public class FuncionarioWithInvalidInformationException extends RuntimeException
{
    public FuncionarioWithInvalidInformationException(String message)
    {
        super(message);
    }
}
