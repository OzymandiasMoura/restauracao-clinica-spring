package org.example.clinicarestauracao.Application.Services;

import lombok.RequiredArgsConstructor;
import org.example.clinicarestauracao.Application.Controllers.Mappers.PagamentoMapper;
import org.example.clinicarestauracao.Application.Dtos.PagamentoDtos.PagamentoRequestDto;
import org.example.clinicarestauracao.Application.Dtos.PagamentoDtos.PagamentoResponseDto;
import org.example.clinicarestauracao.Application.Exceptions.Pagamento.PagamentoWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.*;
import org.example.clinicarestauracao.Domain.Entities.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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

        validarPagamento(internacao, dto.dataPagamento());

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


    private void validarPagamento(
            Internacao internacao,
            LocalDate dataPagamento
    ) {
        if (!internacao.getConvenio().isPagamento()) {
            throw new PagamentoWithInvalidInformationException(
                    "O convênio desta internação não exige pagamento."
            );
        }

        if (!Boolean.TRUE.equals(internacao.getAtivo())) {
            throw new PagamentoWithInvalidInformationException(
                    "Não é possível registrar pagamentos em uma internação encerrada."
            );
        }

        if (dataPagamento == null) {
            throw new PagamentoWithInvalidInformationException(
                    "A data do pagamento é obrigatória."
            );
        }

        if (dataPagamento.isAfter(LocalDate.now())) {
            throw new PagamentoWithInvalidInformationException(
                    "A data do pagamento não pode ser futura."
            );
        }

        if (dataPagamento.isBefore(internacao.getDataEntrada())) {
            throw new PagamentoWithInvalidInformationException(
                    "A data do pagamento não pode ser anterior à data de entrada."
            );
        }

        if (internacao.getDataSaida() != null
                && dataPagamento.isAfter(internacao.getDataSaida())) {
            throw new PagamentoWithInvalidInformationException(
                    "A data do pagamento não pode ser posterior à data de saída."
            );
        }
    }


}
