package org.example.clinicarestauracao.Application.Dtos.FuncionarioDtos;

import org.example.clinicarestauracao.Application.Dtos.CargoDtos.CargoResponseDto;
import org.example.clinicarestauracao.Application.Dtos.EnderecoDtos.EnderecoDto;
import org.example.clinicarestauracao.Application.Dtos.SecurityDtos.UserSummaryDto;

import java.time.LocalDate;

public record FuncionarioResponseDto(Long id, String nome, String cpf, String email, LocalDate dataNascimento, EnderecoDto endereco, String cep, boolean ativo, CargoResponseDto cargo, UserSummaryDto user, LocalDate dataAdmissao, LocalDate dataDemissao)
{
}
