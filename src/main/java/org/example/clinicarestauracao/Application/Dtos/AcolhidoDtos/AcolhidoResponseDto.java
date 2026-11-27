package org.example.clinicarestauracao.Application.Dtos.AcolhidoDtos;

import org.example.clinicarestauracao.Application.Dtos.EnderecoDtos.EnderecoDto;
import org.example.clinicarestauracao.Application.Dtos.ResponsavelDtos.ResponsavelResponseDto;
import org.example.clinicarestauracao.Domain.Enums.Etnia;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AcolhidoResponseDto(Long id, LocalDateTime dataCriacao, String nome, String cpf, String email, LocalDate dataNascimento, EnderecoDto endereco, String cep, String telefone, ResponsavelResponseDto responsavel, boolean ativo, boolean moradorRua, String telefone2, Etnia etnia, String genero, String orientacaoSexual, String grauParentesco)
{
}
