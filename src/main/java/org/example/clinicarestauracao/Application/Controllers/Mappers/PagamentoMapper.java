package org.example.clinicarestauracao.Application.Controllers.Mappers;
import org.example.clinicarestauracao.Application.Dtos.PagamentoDtos.PagamentoRequestDto;
import org.example.clinicarestauracao.Application.Dtos.PagamentoDtos.PagamentoResponseDto;
import org.example.clinicarestauracao.Domain.Entities.*;


public class PagamentoMapper {

    public static Pagamento requestDtoToEntity(
            PagamentoRequestDto dto,
            Internacao internacao
    ) {
        return new Pagamento(
                dto.valor(),
                dto.dataPagamento(),
                internacao
        );
    }

    public static PagamentoResponseDto entityToResponseDto(
            Pagamento pagamento
    ) {
        return new PagamentoResponseDto(
                pagamento.getId(),
                pagamento.getValor(),
                pagamento.getDataPagamento(),
                pagamento.getInternacao().getId()
        );
    }
}