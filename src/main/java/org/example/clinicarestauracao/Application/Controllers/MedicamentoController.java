package org.example.clinicarestauracao.Application.Controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clinicarestauracao.Application.Controllers.Mappers.MedicamentoMapper;
import org.example.clinicarestauracao.Application.Dtos.Medicamento.MedicamentoRequestDto;
import org.example.clinicarestauracao.Application.Dtos.Medicamento.MedicamentoResponseDto;
import org.example.clinicarestauracao.Application.Services.MedicamentoService;
import org.example.clinicarestauracao.Domain.Entities.Medicamento;
import org.springframework.http.HttpStatus;
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

    @PostMapping
    public ResponseEntity<MedicamentoResponseDto> create(@RequestBody @Valid MedicamentoRequestDto dto){
        Medicamento m = medicamentoService.create(MedicamentoMapper.requestDtoToEntity(dto));

        MedicamentoResponseDto response = MedicamentoMapper.entityToResponseDto(m);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(m.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);

    }


    @PutMapping("/{id}")
    public ResponseEntity<MedicamentoResponseDto> update(@PathVariable Long id, @RequestBody @Valid MedicamentoRequestDto medicamento){
            Medicamento updated = medicamentoService.update(id, MedicamentoMapper.requestDtoToEntity(medicamento));

            MedicamentoResponseDto response = MedicamentoMapper.entityToResponseDto(updated);

            return ResponseEntity.ok().body(response);

    }

    @GetMapping
    public ResponseEntity<List<MedicamentoResponseDto>> listAll(){
        List<Medicamento> medicamento = medicamentoService.findAll();

        List<MedicamentoResponseDto> dtos = medicamento.stream()
                .map(MedicamentoMapper::entityToResponseDto)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicamentoResponseDto> findByID(@PathVariable Long id){
        Medicamento medicamento = medicamentoService.findMedicamentoById(id);

        MedicamentoResponseDto response = MedicamentoMapper.entityToResponseDto(medicamento);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<MedicamentoResponseDto> findByNome(@RequestParam String nome){
        Medicamento medicamento = medicamentoService.findMedicamentoByNome(nome);

        MedicamentoResponseDto response = MedicamentoMapper.entityToResponseDto(medicamento);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        medicamentoService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
