package org.example.clinicarestauracao.Application.Dtos.ModalidadeDtos;

public record ModalidadeRequestDto(String descricao, String cnpj, int maxVagas, boolean pagamento)
{
}
