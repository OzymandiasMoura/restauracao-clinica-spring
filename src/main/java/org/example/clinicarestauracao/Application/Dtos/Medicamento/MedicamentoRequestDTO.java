package org.example.clinicarestauracao.Application.Dtos.Medicamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MedicamentoRequestDTO(

        @NotBlank(message = "O nome não pode estar em branco")
        String nome,

        @NotNull(message = "O preço é obrigatório")
        @Positive(message = "O preço deve ser maior que zero")
        Double preco
) {
}
