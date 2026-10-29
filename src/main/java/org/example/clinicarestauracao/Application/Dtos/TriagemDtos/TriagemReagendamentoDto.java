package org.example.clinicarestauracao.Application.Dtos.TriagemDtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TriagemReagendamentoDto(

        @NotNull(message = "A nova data é obrigatória.")
        @Future(message = "A nova data deve estar no futuro.")
        LocalDateTime novaData

) {
}