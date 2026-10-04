package org.example.clinicarestauracao.Application.Dtos.EstoqueMedicamento;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MovimentacaoEstoqueRequestDto(

        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
        int quantidade
) {

}
