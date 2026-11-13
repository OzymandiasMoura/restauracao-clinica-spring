package org.example.clinicarestauracao.Application.Controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.clinicarestauracao.Application.Controllers.Mappers.TriagemMapper;
import org.example.clinicarestauracao.Application.Dtos.TriagemDtos.TriagemReagendamentoDto;
import org.example.clinicarestauracao.Application.Dtos.TriagemDtos.TriagemRequestDto;
import org.example.clinicarestauracao.Application.Dtos.TriagemDtos.TriagemResponseDto;
import org.example.clinicarestauracao.Application.Services.ConvenioService;
import org.example.clinicarestauracao.Application.Services.TriagemService;
import org.example.clinicarestauracao.Domain.Entities.Convenio;
import org.example.clinicarestauracao.Domain.Entities.Triagem;
import org.example.clinicarestauracao.Domain.Enums.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/triagens")
@AllArgsConstructor
public class TriagemController {

    private final TriagemService service;
    private final ConvenioService convenioService;

    @GetMapping
    public ResponseEntity<List<TriagemResponseDto>> findAll()
    {
        List<Triagem> triagens = service.findAllTriagens();
        List<TriagemResponseDto> response =  triagens.stream().map(TriagemMapper::entityToResponseDto).toList();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TriagemResponseDto> findById(@PathVariable Long id)
    {
        Triagem triagem = service.findTriagemById(id);
        return ResponseEntity.ok().body(TriagemMapper.entityToResponseDto(triagem));
    }

    @GetMapping("/search/nome")
    public ResponseEntity<List<TriagemResponseDto>> findByName(@RequestParam String nome)
    {
        List<Triagem> triagens = service.findTriagemByName(nome);
        List<TriagemResponseDto> response = triagens.stream().map(TriagemMapper::entityToResponseDto).toList();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/status/status")
    public ResponseEntity<List<TriagemResponseDto>> findByStatus(@RequestParam Status status)
    {
        List<Triagem> triagens = service.findTriagemByStatus(status);
        List<TriagemResponseDto> response = triagens.stream().map(TriagemMapper::entityToResponseDto).toList();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<TriagemResponseDto> createTriagem(@RequestBody @Valid TriagemRequestDto dto)
    {
        Convenio convenio = convenioService.findConvenioById(dto.convenioId());
        Triagem triagem = TriagemMapper.requestDtoToEntity(dto, convenio);
        Triagem created = service.createTriagem(triagem);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri();

        return ResponseEntity.created(location).body(TriagemMapper.entityToResponseDto(triagem));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TriagemResponseDto> update(@PathVariable Long id, @RequestBody @Valid TriagemRequestDto dto)
    {
        Convenio convenio = convenioService.findConvenioById(dto.convenioId());
        Triagem triagem = TriagemMapper.requestDtoToEntity(dto, convenio);
        return ResponseEntity.ok().body(TriagemMapper.entityToResponseDto(triagem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id)
    {
        service.deleteTriagem(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<TriagemResponseDto> concluir(@PathVariable Long id)
    {
        Triagem triagem = service.concluir(id);
        return ResponseEntity.ok().body(TriagemMapper.entityToResponseDto(triagem));
    }
    @PatchMapping("/{id}/desconcluir")
    public ResponseEntity<TriagemResponseDto> desconcluir(@PathVariable Long id)
    {
        Triagem triagem = service.desconcluir(id);
        return ResponseEntity.ok().body(TriagemMapper.entityToResponseDto(triagem));
    }
    
    @PatchMapping("/{id}/reagendar")
    public ResponseEntity<TriagemResponseDto> reagendar(@PathVariable Long id, @RequestBody @Valid TriagemReagendamentoDto dto)
    {
        Triagem triagem = service.reagendar(id, dto.novaData());
        return ResponseEntity.ok().body(TriagemMapper.entityToResponseDto(triagem));
    }
}

