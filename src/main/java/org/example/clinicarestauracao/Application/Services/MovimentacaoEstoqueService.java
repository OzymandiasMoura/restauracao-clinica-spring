package org.example.clinicarestauracao.Application.Services;

import lombok.RequiredArgsConstructor;
import org.example.clinicarestauracao.Application.Controllers.Mappers.MovimentacaoEstoqueMapper;
import org.example.clinicarestauracao.Application.Dtos.MovimentacaoEstoqueDtos.MovimentacaoEstoqueResponseDto;
import org.example.clinicarestauracao.Application.Exceptions.Medicamento.MedicamentoWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.MedicamentoRepository;
import org.example.clinicarestauracao.Application.Interfaces.MovimentacaoEstoqueRepository;
import org.example.clinicarestauracao.Domain.Entities.Medicamento;
import org.example.clinicarestauracao.Domain.Entities.MovimentacaoEstoque;
import org.example.clinicarestauracao.Domain.Enums.TipoMovimentacao;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MovimentacaoEstoqueService {

    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final MedicamentoRepository medicamentoRepository;

    @Transactional
    public MovimentacaoEstoqueResponseDto registrarEntrada(
            Long medicamentoId,
            Integer quantidade,
            String justificativa
    ) {
        return movimentar(
                medicamentoId,
                quantidade,
                justificativa,
                TipoMovimentacao.ENTRADA
        );
    }

    @Transactional
    public MovimentacaoEstoqueResponseDto registrarSaida(
            Long medicamentoId,
            Integer quantidade,
            String justificativa
    ) {
        return movimentar(
                medicamentoId,
                quantidade,
                justificativa,
                TipoMovimentacao.SAIDA
        );
    }


    private MovimentacaoEstoqueResponseDto movimentar(
            Long medicamentoId,
            Integer quantidade,
            String justificativa,
            TipoMovimentacao tipo
    ) {
        if (quantidade == null || quantidade <= 0) {
            throw new MedicamentoWithInvalidInformationException(
                    "A quantidade movimentada deve ser maior que zero."
            );
        }

        if (justificativa == null
                || justificativa.isBlank()
                || justificativa.trim().length() > 500) {

            throw new MedicamentoWithInvalidInformationException(
                    "A justificativa deve conter entre 1 e 500 caracteres."
            );
        }

        String usuarioResponsavel = obterUsuarioAutenticado();

        Medicamento medicamento = medicamentoRepository
                .buscarParaMovimentacao(medicamentoId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Medicamento não encontrado."
                ));

        int saldoAnterior = medicamento.getQuantidadeEstoque();

        if (tipo == TipoMovimentacao.ENTRADA) {
            medicamento.adicionarEstoque(quantidade);

        } else if (tipo == TipoMovimentacao.SAIDA) {
            medicamento.baixarEstoque(quantidade);

        } else {
            throw new MedicamentoWithInvalidInformationException(
                    "Tipo de movimentação inválido."
            );
        }

        int saldoPosterior = medicamento.getQuantidadeEstoque();

        MovimentacaoEstoque movimentacao =
                new MovimentacaoEstoque(
                        medicamento,
                        tipo,
                        quantidade,
                        usuarioResponsavel,
                        saldoAnterior,
                        saldoPosterior,
                        justificativa
                );


        medicamentoRepository.save(medicamento);

        MovimentacaoEstoque movimentacaoSalva =
                movimentacaoRepository.save(movimentacao);
        return MovimentacaoEstoqueMapper.entityToResponseDto(
                movimentacaoSalva
        );
    }


    @Transactional(readOnly = true)
    public List<MovimentacaoEstoqueResponseDto> listarPorMedicamento(
            Long medicamentoId
    ) {
        if (!medicamentoRepository.existsById(medicamentoId)) {
            throw new NoSuchElementException(
                    "Medicamento não encontrado."
            );
        }

        return movimentacaoRepository
                .findByMedicamentoIdOrderByDataMovimentacaoDescIdDesc(
                        medicamentoId
                )
                .stream()
                .map(this::converterParaDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public MovimentacaoEstoqueResponseDto buscarPorId(Long id) {

        MovimentacaoEstoque movimentacao =
                movimentacaoRepository.findById(id)
                        .orElseThrow(() -> new NoSuchElementException(
                                "Movimentação não encontrada."
                        ));

        return converterParaDto(movimentacao);
    }

    private String obterUsuarioAutenticado() {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {

            throw new AccessDeniedException(
                    "Usuário não autenticado."
            );
        }

        return authentication.getName();
    }

    private MovimentacaoEstoqueResponseDto converterParaDto(
            MovimentacaoEstoque movimentacao
    ) {
        return new MovimentacaoEstoqueResponseDto(
                movimentacao.getId(),
                movimentacao.getMedicamento().getId(),
                movimentacao.getTipo(),
                movimentacao.getQuantidade(),
                movimentacao.getDataMovimentacao(),
                movimentacao.getUsuarioResponsavel(),
                movimentacao.getSaldoAnterior(),
                movimentacao.getSaldoPosterior(),
                movimentacao.getJustificativa()
        );
    }
}
