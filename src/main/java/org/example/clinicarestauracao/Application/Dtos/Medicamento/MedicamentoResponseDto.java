package org.example.clinicarestauracao.Application.Dtos.Medicamento;

public record MedicamentoResponseDto(
        Long id,
        String nome,
        double preco,
        int quantidadeEstoque,
        int quantidadeMinima,
        String status) {
}
