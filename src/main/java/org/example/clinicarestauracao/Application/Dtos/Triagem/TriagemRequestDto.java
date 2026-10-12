package org.example.clinicarestauracao.Application.Dtos.Triagem;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TriagemRequestDto(

        @NotNull(message = "O ID da modalidade é obrigatório.")
        @Positive(message = "O ID da modalidade deve ser válido.")
        Long modalidadeId,

        @NotBlank(message = "O nome do possível acolhido não pode ser vazio.")
        @Size(min = 3, message = "O nome deve ter pelo menos 3 caracteres.")
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

        String observacaoEntrevista

) {
}