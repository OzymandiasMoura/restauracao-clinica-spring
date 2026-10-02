package org.example.clinicarestauracao.Application.Dtos.EstoqueMedicamento;


    public record EstoqueMedicamentoResponseDto(
            Long idMedicamento,
            String nomeMedicamento,
            int quantidadeAtual,
            String status
    ) {}

