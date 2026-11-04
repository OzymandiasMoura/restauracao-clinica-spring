package org.example.clinicarestauracao.Application.Exceptions.Pagamento;

public class PagamentoNotFoundException extends RuntimeException {
    public PagamentoNotFoundException(String message) {
        super(message);
    }
}
