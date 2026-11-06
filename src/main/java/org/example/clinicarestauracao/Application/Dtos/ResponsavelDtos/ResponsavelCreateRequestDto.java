package org.example.clinicarestauracao.Application.Dtos.ResponsavelDtos;

import org.example.clinicarestauracao.Application.Dtos.EnderecoDtos.EnderecoDto;

public record ResponsavelCreateRequestDto(String nome, String cpf, String email, EnderecoDto endereco, String cep, String telefone)
{
}
