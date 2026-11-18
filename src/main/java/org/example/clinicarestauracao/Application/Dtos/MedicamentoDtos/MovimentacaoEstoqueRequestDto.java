package org.example.clinicarestauracao.Application.Dtos.MedicamentoDtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MovimentacaoEstoqueRequestDto(

        @NotNull(message = "A quantidade é obrigatória.")
        @Min(value = 1, message = "A quantidade deve ser maior que zero.")
        Integer quantidade,

        @NotBlank(message = "A justificativa é obrigatória.")
        @Size(
                max = 500,
                message = "A justificativa deve ter no máximo 500 caracteres."
        )
        String justificativa

) {}