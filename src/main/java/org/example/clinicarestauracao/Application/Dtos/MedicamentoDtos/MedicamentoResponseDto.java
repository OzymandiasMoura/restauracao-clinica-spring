package org.example.clinicarestauracao.Application.Dtos.MedicamentoDtos;

public record MedicamentoResponseDto(
        Long id,
        String nome,
        java.math.BigDecimal preco,
        int quantidadeEstoque,
        int quantidadeMinima,
        String status) {
}
