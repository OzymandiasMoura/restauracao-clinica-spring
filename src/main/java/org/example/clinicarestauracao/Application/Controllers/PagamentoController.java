package org.example.clinicarestauracao.Application.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clinicarestauracao.Application.Dtos.PagamentoDtos.PagamentoEstornoRequestDto;
import org.example.clinicarestauracao.Application.Dtos.PagamentoDtos.PagamentoRequestDto;
import org.example.clinicarestauracao.Application.Dtos.PagamentoDtos.PagamentoResponseDto;
import org.example.clinicarestauracao.Application.Dtos.PagamentoDtos.PagamentoResumoResponseDto;
import org.example.clinicarestauracao.Application.Services.PagamentoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PagamentoResponseDto create(
            @Valid @RequestBody PagamentoRequestDto dto
    ) {
        return pagamentoService.create(dto);
    }

    @GetMapping
    public List<PagamentoResponseDto> listAll() {
        return pagamentoService.listAll();
    }

    @GetMapping("/{id}")
    public PagamentoResponseDto findById(
            @PathVariable Long id
    ) {
        return pagamentoService.findById(id);
    }

    @GetMapping("/internacao/{internacaoId}")
    public List<PagamentoResponseDto> findByInternacao(
            @PathVariable Long internacaoId
    ) {
        return pagamentoService.findByInternacao(internacaoId);
    }
    @GetMapping("/internacao/{internacaoId}/total")
    public PagamentoResumoResponseDto calcularTotalPago(
            @PathVariable Long internacaoId
    ) {
        return new PagamentoResumoResponseDto(
                internacaoId,
                pagamentoService.calcularTotalPago(internacaoId)
        );
    }
    @PatchMapping("/{id}/estornar")
    public ResponseEntity<PagamentoResponseDto> estornar(
            @PathVariable Long id,
            @Valid @RequestBody PagamentoEstornoRequestDto dto
    ) {
        return ResponseEntity.ok(
                pagamentoService.estornar(id, dto)
        );
    }
}
