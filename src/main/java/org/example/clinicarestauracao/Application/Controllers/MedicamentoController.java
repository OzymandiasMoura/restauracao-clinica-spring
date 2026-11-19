
package org.example.clinicarestauracao.Application.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.example.clinicarestauracao.Application.Controllers.Mappers.MedicamentoMapper;
import org.example.clinicarestauracao.Application.Dtos.MedicamentoDtos.MedicamentoRequestDto;
import org.example.clinicarestauracao.Application.Dtos.MedicamentoDtos.MedicamentoResponseDto;
import org.example.clinicarestauracao.Application.Dtos.MedicamentoDtos.MovimentacaoEstoqueRequestDto;
import org.example.clinicarestauracao.Application.Dtos.MovimentacaoEstoqueDtos.MovimentacaoEstoqueResponseDto;

import org.example.clinicarestauracao.Application.Services.MedicamentoService;
import org.example.clinicarestauracao.Application.Services.MovimentacaoEstoqueService;

import org.example.clinicarestauracao.Domain.Entities.Medicamento;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/medicamentos")
@RequiredArgsConstructor
public class MedicamentoController {

    private final MedicamentoService medicamentoService;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;

    @PostMapping
    public ResponseEntity<MedicamentoResponseDto> create(
            @RequestBody @Valid MedicamentoRequestDto dto
    ) {
        Medicamento medicamento = medicamentoService.create(
                MedicamentoMapper.requestDtoToEntity(dto)
        );

        MedicamentoResponseDto response =
                MedicamentoMapper.entityToResponseDto(medicamento);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(medicamento.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<MedicamentoResponseDto> update(
            @PathVariable Long id,
            @RequestBody @Valid MedicamentoRequestDto dto
    ) {
        Medicamento medicamento = medicamentoService.update(
                id,
                MedicamentoMapper.requestDtoToEntity(dto)
        );

        return ResponseEntity.ok(
                MedicamentoMapper.entityToResponseDto(medicamento)
        );
    }

    @GetMapping
    public ResponseEntity<List<MedicamentoResponseDto>> listAll() {

        List<MedicamentoResponseDto> dtos =
                medicamentoService.findAll()
                        .stream()
                        .map(MedicamentoMapper::entityToResponseDto)
                        .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicamentoResponseDto> findByID(
            @PathVariable Long id
    ) {
        Medicamento medicamento =
                medicamentoService.findMedicamentoById(id);

        return ResponseEntity.ok(
                MedicamentoMapper.entityToResponseDto(medicamento)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<MedicamentoResponseDto> findByNome(
            @RequestParam String nome
    ) {
        Medicamento medicamento =
                medicamentoService.findMedicamentoByNome(nome);

        return ResponseEntity.ok(
                MedicamentoMapper.entityToResponseDto(medicamento)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        medicamentoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{idMedicamento}/adicionar")
    public ResponseEntity<MovimentacaoEstoqueResponseDto> adicionar(
            @PathVariable Long idMedicamento,
            @RequestBody @Valid MovimentacaoEstoqueRequestDto request
    ) {
        return ResponseEntity.ok(
                movimentacaoEstoqueService.registrarEntrada(
                        idMedicamento,
                        request.quantidade(),
                        request.justificativa()
                )
        );
    }

    @PutMapping("/{idMedicamento}/baixar")
    public ResponseEntity<MovimentacaoEstoqueResponseDto> baixar(
            @PathVariable Long idMedicamento,
            @RequestBody @Valid MovimentacaoEstoqueRequestDto request
    ) {
        return ResponseEntity.ok(
                movimentacaoEstoqueService.registrarSaida(
                        idMedicamento,
                        request.quantidade(),
                        request.justificativa()
                )
        );
    }

    @GetMapping("/{idMedicamento}/movimentacoes")
    public ResponseEntity<List<MovimentacaoEstoqueResponseDto>>
    listarMovimentacoes(
            @PathVariable Long idMedicamento
    ) {
        return ResponseEntity.ok(
                movimentacaoEstoqueService.listarPorMedicamento(
                        idMedicamento
                )
        );
    }

    @GetMapping("/movimentacoes/{id}")
    public ResponseEntity<MovimentacaoEstoqueResponseDto>
    buscarMovimentacao(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                movimentacaoEstoqueService.buscarPorId(id)
        );
    }

    @GetMapping("/alertas")
    public ResponseEntity<List<MedicamentoResponseDto>> listarAlertas() {

        List<MedicamentoResponseDto> dtos =
                medicamentoService.listarEstoquesEmAlerta()
                        .stream()
                        .map(MedicamentoMapper::entityToResponseDto)
                        .toList();

        return ResponseEntity.ok(dtos);
    }
}
