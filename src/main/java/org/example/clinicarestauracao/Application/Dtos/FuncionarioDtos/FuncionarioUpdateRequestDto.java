package org.example.clinicarestauracao.Application.Dtos.FuncionarioDtos;

import org.example.clinicarestauracao.Application.Dtos.EnderecoDtos.EnderecoDto;

import java.time.LocalDate;

public record FuncionarioUpdateRequestDto(String nome, String cpf, String email, LocalDate dataNascimento, EnderecoDto endereco, String cep, String telefone, Long cargoId, LocalDate dataAdmissao, FuncionarioUserUpdateDataDto user)
{
}
