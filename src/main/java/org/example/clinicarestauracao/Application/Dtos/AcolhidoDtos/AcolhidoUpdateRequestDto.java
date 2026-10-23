package org.example.clinicarestauracao.Application.Dtos.AcolhidoDtos;

import org.example.clinicarestauracao.Application.Dtos.EnderecoDtos.EnderecoDto;

import java.time.LocalDate;

public record AcolhidoUpdateRequestDto(String nome, String cpf, String email, LocalDate dataNascimento, EnderecoDto endereco, String cep, String telefone)
{
}
