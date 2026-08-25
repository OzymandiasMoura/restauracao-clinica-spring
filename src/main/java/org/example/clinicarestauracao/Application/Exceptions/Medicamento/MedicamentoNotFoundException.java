package org.example.clinicarestauracao.Application.Exceptions.Medicamento;

public class MedicamentoNotFoundException extends RuntimeException {
    public MedicamentoNotFoundException(String message) {

        super(message);
    }
}
