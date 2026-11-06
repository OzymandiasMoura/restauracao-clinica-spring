package org.example.clinicarestauracao.Application.Dtos.ResponsavelDtos;

import org.example.clinicarestauracao.Application.Dtos.EnderecoDtos.EnderecoDto;

public record ResponsavelResponseDto(Long id, String nome, String cpf, String email, EnderecoDto endereco, String cep, String telefone, boolean ativo)
{
}
