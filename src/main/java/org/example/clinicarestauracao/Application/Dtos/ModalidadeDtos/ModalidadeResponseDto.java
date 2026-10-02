package org.example.clinicarestauracao.Application.Dtos.ModalidadeDtos;

public record ModalidadeResponseDto(Long id, String descricao, String cnpj, Integer maxVagas, boolean ativo, boolean pagamento, String cor)
{
}
