package org.example.clinicarestauracao.Application.Exceptions.Funcionario;

public class FuncionarioNotFoundException extends RuntimeException
{
    public FuncionarioNotFoundException(String message)
    {
        super(message);
    }
}
