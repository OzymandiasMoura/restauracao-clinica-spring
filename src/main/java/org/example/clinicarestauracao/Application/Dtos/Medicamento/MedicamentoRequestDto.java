package org.example.clinicarestauracao.Application.Dtos.Medicamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MedicamentoRequestDto(

        @NotBlank(message = "O nome não pode ser nulo ou vazio.")
        @Size(min = 3, message = "O nome não pode ter menos de 3 caracteres.")
        String nome,

        @NotNull(message = "O preço é obrigatório")
        @Positive(message = "O preço não pode ser negativo.")
        Double preco
) {
}
