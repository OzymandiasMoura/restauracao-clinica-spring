package org.example.clinicarestauracao.Application.Dtos.EstoqueMedicamento;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record EstoqueMedicamentoRequestDto(
        @NotNull(message = "O ID do medicamento é obrigatório")
        Long medicamentoId,

        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 1, message = "A quantidade mínima para adicionar ao estoque é 1")
        Integer quantidadeEstoque


) {
}
