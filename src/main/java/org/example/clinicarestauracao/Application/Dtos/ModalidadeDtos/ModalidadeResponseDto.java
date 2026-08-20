package org.example.clinicarestauracao.Application.Dtos.ModalidadeDtos;

public record ModalidadeResponseDto(Long id, String descricao, String cnpj, int maxVagas, boolean ativo, boolean pagamento)
{
}
