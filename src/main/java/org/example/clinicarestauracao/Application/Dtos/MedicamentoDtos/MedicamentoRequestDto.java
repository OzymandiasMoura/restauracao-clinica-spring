package org.example.clinicarestauracao.Application.Dtos.MedicamentoDtos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record MedicamentoRequestDto(

        @NotBlank(message = "O nome é obrigatório.")
        @Size(
                min = 3,
                max = 255,
                message = "O nome deve possuir entre 3 e 255 caracteres."
        )
        String nome,

        @NotNull(message = "O preço é obrigatório.")
        @DecimalMin(
                value = "0.00",
                message = "O preço não pode ser negativo."
        )
        @Digits(
                integer = 10,
                fraction = 2,
                message = "O preço deve possuir no máximo duas casas decimais."
        )
        BigDecimal preco,

        @NotNull(message = "O estoque é obrigatório.")
        @PositiveOrZero(message = "O estoque não pode ser negativo.")
        Integer quantidadeEstoque,

        @NotNull(message = "A quantidade mínima é obrigatória.")
        @PositiveOrZero(message = "A quantidade mínima não pode ser negativa.")
        Integer quantidadeMinima
) {}
