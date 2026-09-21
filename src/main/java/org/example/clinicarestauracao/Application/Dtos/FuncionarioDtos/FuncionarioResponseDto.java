package org.example.clinicarestauracao.Application.Dtos.FuncionarioDtos;

import java.time.LocalDate;

public record FuncionarioResponseDto(Long id, String nome, String cpf, String email, LocalDate dataNascimento, String endereco, String cep, boolean ativo, Long cargoId, String cargoNome, Long userId)
{
}
