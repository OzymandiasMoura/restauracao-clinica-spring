package org.example.clinicarestauracao.Application.Services;

import lombok.RequiredArgsConstructor;
import org.example.clinicarestauracao.Application.Controllers.Mappers.PagamentoMapper;
import org.example.clinicarestauracao.Application.Dtos.PagamentoDtos.PagamentoRequestDto;
import org.example.clinicarestauracao.Application.Dtos.PagamentoDtos.PagamentoResponseDto;
import org.example.clinicarestauracao.Application.Interfaces.*;
import org.example.clinicarestauracao.Domain.Entities.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class    PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final InternacaoRepository internacaoRepository;

    @Transactional
    public PagamentoResponseDto create(PagamentoRequestDto dto) {

        Internacao internacao = internacaoRepository
                .findById(dto.internacaoId())
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Internação não encontrada."
                        )
                );

        Pagamento pagamento =
                PagamentoMapper.requestDtoToEntity(dto, internacao);

        return PagamentoMapper.entityToResponseDto(
                pagamentoRepository.save(pagamento)
        );
    }

    @Transactional(readOnly = true)
    public List<PagamentoResponseDto> listAll() {

        return pagamentoRepository.findAll()
                .stream()
                .map(PagamentoMapper::entityToResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public PagamentoResponseDto findById(Long id) {

        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Pagamento não encontrado."
                        )
                );

        return PagamentoMapper.entityToResponseDto(pagamento);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPago(Long internacaoId) {

        if (!internacaoRepository.existsById(internacaoId)) {
            throw new NoSuchElementException(
                    "Internação não encontrada."
            );
        }

        return pagamentoRepository.calcularTotalPago(internacaoId);
    }

    @Transactional(readOnly = true)
    public List<PagamentoResponseDto> findByInternacao(
            Long internacaoId
    ) {
        if (!internacaoRepository.existsById(internacaoId)) {
            throw new NoSuchElementException(
                    "Internação não encontrada."
            );
        }

        return pagamentoRepository
                .findByInternacaoId(internacaoId)
                .stream()
                .map(PagamentoMapper::entityToResponseDto)
                .toList();
    }

}
