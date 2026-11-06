package org.example.clinicarestauracao.Application.Dtos.Triagem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TriagemCancelamentoRequestDto(

        @NotBlank(message = "O motivo do cancelamento é obrigatório.")
        @Size(max = 500, message = "O motivo deve ter no máximo 500 caracteres.")
        String motivo

) {}