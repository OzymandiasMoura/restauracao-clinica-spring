package org.example.clinicarestauracao.Application.Dtos.ConvenioDtos;

public record ConvenioResponseDto(Long id, String descricao, String cnpj, Integer maxVagas, boolean ativo, boolean pagamento, String cor)
{
}
