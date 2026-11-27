package org.example.clinicarestauracao.Application.Dtos.AcolhidoDtos;

import org.example.clinicarestauracao.Application.Dtos.EnderecoDtos.EnderecoDto;
import org.example.clinicarestauracao.Domain.Enums.Etnia;

import java.time.LocalDate;

public record AcolhidoUpdateRequestDto(String nome, String cpf, String email, LocalDate dataNascimento, EnderecoDto endereco, String cep, String telefone, Long responsavelId, Boolean moradorRua, String telefone2, Etnia etnia, String genero, String orientacaoSexual, String grauParentesco)
{
}
