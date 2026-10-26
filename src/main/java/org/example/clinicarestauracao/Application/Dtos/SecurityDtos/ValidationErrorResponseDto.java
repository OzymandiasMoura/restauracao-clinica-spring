package org.example.clinicarestauracao.Application.Dtos.SecurityDtos;

import java.util.List;

public record ValidationErrorResponseDto(String mensagem, List<FieldErrorDto> erros) {
}
