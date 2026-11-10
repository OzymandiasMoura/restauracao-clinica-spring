package org.example.clinicarestauracao.Application.Dtos.PagamentoDtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PagamentoEstornoRequestDto(

        @NotBlank(message = "O motivo do estorno é obrigatório.")
        @Size(max = 500)
        String motivo

) {}
