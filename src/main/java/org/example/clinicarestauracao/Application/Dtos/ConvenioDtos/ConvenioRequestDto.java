package org.example.clinicarestauracao.Application.Dtos.ConvenioDtos;

public record ConvenioRequestDto(String descricao, String cnpj, Integer maxVagas, boolean pagamento, String cor)
{
}
