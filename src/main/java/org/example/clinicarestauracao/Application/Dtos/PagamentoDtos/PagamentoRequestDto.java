package org.example.clinicarestauracao.Application.Dtos.PagamentoDtos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PagamentoRequestDto(

        @NotNull(message = "O valor é obrigatório.")
        @DecimalMin(
                value = "0.01",
                message = "O valor deve ser maior que zero."
        )
        @Digits(integer = 10, fraction = 2)
        BigDecimal valor,

        @NotNull(message = "A data é obrigatória.")
        @PastOrPresent(message = "A data não pode ser futura.")
        LocalDate dataPagamento,

        @NotNull(message = "A internação é obrigatória.")
        Long internacaoId

) {}