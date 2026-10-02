package org.example.clinicarestauracao.Application.Dtos.ModalidadeDtos;

public record ModalidadeRequestDto(String descricao, String cnpj, Integer maxVagas, boolean pagamento, String cor)
{
}
