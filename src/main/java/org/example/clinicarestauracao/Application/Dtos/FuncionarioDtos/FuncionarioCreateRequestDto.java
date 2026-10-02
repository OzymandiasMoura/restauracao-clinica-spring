package org.example.clinicarestauracao.Application.Dtos.FuncionarioDtos;


import org.example.clinicarestauracao.Application.Dtos.EnderecoDtos.EnderecoDto;

import java.time.LocalDate;

public record FuncionarioCreateRequestDto(String nome, String cpf, String email, LocalDate dataNascimento, EnderecoDto endereco, String cep, Long cargoId, LocalDate dataAdmissao, FuncionarioUserDataDto user)
{
}
