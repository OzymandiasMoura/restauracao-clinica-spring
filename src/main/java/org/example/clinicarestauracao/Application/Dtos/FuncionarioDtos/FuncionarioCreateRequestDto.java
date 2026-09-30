package org.example.clinicarestauracao.Application.Dtos.FuncionarioDtos;


import java.time.LocalDate;

public record FuncionarioCreateRequestDto(String nome, String cpf, String email, LocalDate dataNascimento, String endereco, String cep, Long cargoId, LocalDate dataAdmissao, FuncionarioUserDataDto user)
{
}
