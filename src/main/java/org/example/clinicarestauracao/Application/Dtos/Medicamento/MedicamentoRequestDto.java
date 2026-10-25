package org.example.clinicarestauracao.Application.Dtos.Medicamento;

import jakarta.validation.constraints.*;

public record MedicamentoRequestDto(

        @NotBlank(message = "O nome não pode ser nulo ou vazio.")
        @Size(min = 3, message = "O nome não pode ter menos de 3 caracteres.")
        String nome,

        @NotNull(message = "O preço é obrigatório")
        @Positive(message = "O preço não pode ser negativo.")
        Double preco,

        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 1, message = "A quantidade mínima para adicionar ao estoque é 1")
        Integer quantidadeEstoque,

        @NotNull(message = "A quantidade mínima para alerta é obrigatória")
        @Min(value = 0, message = "A quantidade mínima não pode ser negativa")
        Integer quantidadeMinimaAlerta
) {
}
