package org.example.clinicarestauracao.Application.Controllers.Mappers;


import org.example.clinicarestauracao.Application.Dtos.EstoqueMedicamento.EstoqueMedicamentoResponseDto;
import org.example.clinicarestauracao.Domain.Entities.EstoqueMedicamento;

public class EstoqueMedicamentoMapper {

        public static EstoqueMedicamentoResponseDto entityToResponseDto(EstoqueMedicamento entity) {

            String status = (entity.getQuantidadeEstoque() <= entity.getQuantidadeMinimaAlerta())
                    ? "ALERTA: ESTOQUE BAIXO"
                    : "ESTOQUE OK";

            return new EstoqueMedicamentoResponseDto(
                    entity.getMedicamento().getId(),
                    entity.getMedicamento().getNome(),
                    entity.getQuantidadeEstoque(),
                    status
            );
        }
    }


