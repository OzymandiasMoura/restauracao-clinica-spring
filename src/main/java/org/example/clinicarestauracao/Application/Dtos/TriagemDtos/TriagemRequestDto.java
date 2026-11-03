package org.example.clinicarestauracao.Application.Dtos.TriagemDtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TriagemRequestDto(

        @NotNull(message = "O ID do convênio é obrigatório.")
        @Positive(message = "O ID do convênio deve ser válido.")
        Long convenioId,

        @NotBlank(message = "O nome do possível acolhido é obrigatório.")
        @Size(
                min = 3,
                max = 255,
                message = "O nome deve conter entre 3 e 255 caracteres."
        )
        String nomePossivelAcolhido,

        @NotBlank(message = "O telefone é obrigatório.")
        @Pattern(
                regexp = "^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$",
                message = "Formato de telefone inválido."
        )
        String telefone,

        @NotNull(message = "A data é obrigatória.")
        @Future(message = "A data agendada deve estar no futuro.")
        LocalDateTime dataAgendada,

        @Size(
                max = 1000,
                message = "A observação da entrevista não pode ultrapassar 1000 caracteres."
        )
        String observacaoEntrevista

) {
}