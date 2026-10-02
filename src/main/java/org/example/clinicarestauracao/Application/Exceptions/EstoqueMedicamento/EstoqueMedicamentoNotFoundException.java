package org.example.clinicarestauracao.Application.Exceptions.EstoqueMedicamento;

public class EstoqueMedicamentoNotFoundException extends RuntimeException {
    public EstoqueMedicamentoNotFoundException(String message) {
        super(message);
    }
}
