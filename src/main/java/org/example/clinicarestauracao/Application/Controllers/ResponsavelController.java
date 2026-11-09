package org.example.clinicarestauracao.Application.Controllers;

import lombok.AllArgsConstructor;
import org.example.clinicarestauracao.Application.Controllers.Mappers.ResponsavelMapper;
import org.example.clinicarestauracao.Application.Dtos.ResponsavelDtos.ResponsavelCreateRequestDto;
import org.example.clinicarestauracao.Application.Dtos.ResponsavelDtos.ResponsavelResponseDto;
import org.example.clinicarestauracao.Application.Dtos.ResponsavelDtos.ResponsavelUpdateRequestDto;
import org.example.clinicarestauracao.Application.Services.ResponsavelService;
import org.example.clinicarestauracao.Domain.Entities.Responsavel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/responsaveis")
public class ResponsavelController
{
    private final ResponsavelService service;

    @GetMapping("/{id}")
    public ResponseEntity<ResponsavelResponseDto> findResponsavelById(@PathVariable Long id)
    {
        Responsavel responsavel = service.findResponsavelById(id);

        return ResponseEntity.ok().body(ResponsavelMapper.entityToResponseDto(responsavel));
    }

    @GetMapping("/search/email")
    public ResponseEntity<ResponsavelResponseDto> findResponsavelByEmail(@RequestParam String email)
    {
        Responsavel responsavel = service.findResponsavelByEmail(email);

        return ResponseEntity.ok().body(ResponsavelMapper.entityToResponseDto(responsavel));
    }

    @GetMapping("/search/cpf")
    public ResponseEntity<ResponsavelResponseDto> findResponsavelByCpf(@RequestParam String cpf)
    {
        Responsavel responsavel = service.findResponsavelByCpf(cpf);

        return ResponseEntity.ok().body(ResponsavelMapper.entityToResponseDto(responsavel));
    }

    @GetMapping("/search/nome")
    public ResponseEntity<List<ResponsavelResponseDto>> findResponsaveisByNome(@RequestParam String nome)
    {
        List<Responsavel> responsaveis = service.findResponsavelByNome(nome);
        List<ResponsavelResponseDto> response = responsaveis.stream().map(ResponsavelMapper::entityToResponseDto).toList();

        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<ResponsavelResponseDto> createResponsavel(@RequestBody ResponsavelCreateRequestDto dto)
    {
        Responsavel responsavel = ResponsavelMapper.createRequestDtoToEntity(dto);
        Responsavel created = service.createResponsavel(responsavel);

        ResponsavelResponseDto response = ResponsavelMapper.entityToResponseDto(created);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponsavelResponseDto> updateResponsavel(@PathVariable Long id, @RequestBody ResponsavelUpdateRequestDto dto)
    {
        Responsavel updated = service.updateResponsavel(id, ResponsavelMapper.updateRequestDtoToEntity(dto));

        return ResponseEntity.ok().body(ResponsavelMapper.entityToResponseDto(updated));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ResponsavelResponseDto> deactivateResponsavel(@PathVariable Long id)
    {
        Responsavel deactivated = service.deactivateResponsavel(id);

        return  ResponseEntity.ok().body(ResponsavelMapper.entityToResponseDto(deactivated));
    }

    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<ResponsavelResponseDto> reactivateResponsavel(@PathVariable Long id)
    {
        Responsavel reactivated = service.reactivateResponsavel(id);

        return ResponseEntity.ok().body(ResponsavelMapper.entityToResponseDto(reactivated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResponsavel(@PathVariable Long id)
    {
        service.deleteResponsavel(id);

        return ResponseEntity.noContent().build();
    }
}
