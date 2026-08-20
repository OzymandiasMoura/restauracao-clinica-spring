package org.example.clinicarestauracao.Application.Dtos.ModalidadeDtos;

public record ModalidadeResponseDto(Long id, String descricao, String cnpj, String maxVagas, boolean ativo, boolean pagamento)
{
}
