package org.example.clinicarestauracao.Application.Exceptions.Pagamento;

public class PagamentoWithInvalidInformationException extends RuntimeException {
    public PagamentoWithInvalidInformationException(String message) {
        super(message);
    }
}
