package org.example.clinicarestauracao.Application.Controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clinicarestauracao.Application.Controllers.Mappers.EstoqueMedicamentoMapper;
import org.example.clinicarestauracao.Application.Controllers.Mappers.MedicamentoMapper;
import org.example.clinicarestauracao.Application.Dtos.EstoqueMedicamento.EstoqueMedicamentoRequestDto;
import org.example.clinicarestauracao.Application.Dtos.EstoqueMedicamento.EstoqueMedicamentoResponseDto;
import org.example.clinicarestauracao.Application.Dtos.EstoqueMedicamento.MovimentacaoEstoqueRequestDto;
import org.example.clinicarestauracao.Application.Dtos.Medicamento.MedicamentoResponseDto;
import org.example.clinicarestauracao.Application.Services.EstoqueMedicamentoService;
import org.example.clinicarestauracao.Domain.Entities.EstoqueMedicamento;
import org.example.clinicarestauracao.Domain.Entities.Medicamento;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estoque-medicamento")
@RequiredArgsConstructor
public class EstoqueMedicamentoController {

    private final EstoqueMedicamentoService estoqueMedicamentoService;

    @GetMapping
    public ResponseEntity<List<EstoqueMedicamentoResponseDto>> listAll(){
        List<EstoqueMedicamento> estoque = estoqueMedicamentoService.findAll();

        List<EstoqueMedicamentoResponseDto> dtos = estoque.stream()
                .map(EstoqueMedicamentoMapper::entityToResponseDto)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstoqueMedicamentoResponseDto> findByID(@PathVariable Long id){
        EstoqueMedicamento estoque = estoqueMedicamentoService.findByMedicamentoId(id);

        EstoqueMedicamentoResponseDto response = EstoqueMedicamentoMapper.entityToResponseDto(estoque);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/alertas")
    public ResponseEntity<List<EstoqueMedicamentoResponseDto>> listarAlertas(){
        List<EstoqueMedicamento> estoquesEmAlerta = estoqueMedicamentoService.listarEstoquesEmAlerta();

        List<EstoqueMedicamentoResponseDto> dtos = estoquesEmAlerta.stream()
                .map(EstoqueMedicamentoMapper::entityToResponseDto)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<EstoqueMedicamentoResponseDto> inicializarEstoque(@Valid @RequestBody EstoqueMedicamentoRequestDto request){
        EstoqueMedicamento novoEstoque = estoqueMedicamentoService.inicializarEstoque(
                request.idMedicamento(),
                request.quantidadeEstoque(),
                request.quantidadeMinimaAlerta());

        EstoqueMedicamentoResponseDto response = EstoqueMedicamentoMapper.entityToResponseDto(novoEstoque);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @PutMapping("/{idMedicamento}/adicionar")
    public ResponseEntity<EstoqueMedicamentoResponseDto> adicionar(@PathVariable Long idMedicamento, @Valid @RequestBody MovimentacaoEstoqueRequestDto request){
        EstoqueMedicamento estoqueAtualizado = estoqueMedicamentoService.adicionarEstoque(idMedicamento, request.quantidade());
        EstoqueMedicamentoResponseDto response = EstoqueMedicamentoMapper.entityToResponseDto(estoqueAtualizado);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{idMedicamento}/baixar")
    public ResponseEntity<EstoqueMedicamentoResponseDto> baixar(@PathVariable Long idMedicamento, @Valid @RequestBody MovimentacaoEstoqueRequestDto request){
        EstoqueMedicamento estoqueAtualizado = estoqueMedicamentoService.baixarEstoque(idMedicamento, request.quantidade());
        EstoqueMedicamentoResponseDto response = EstoqueMedicamentoMapper.entityToResponseDto(estoqueAtualizado);

        return ResponseEntity.ok(response);
    }



}
