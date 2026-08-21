package org.example.clinicarestauracao.Application.Controllers;

import org.example.clinicarestauracao.Application.Controllers.Mappers.ModalidadeMapper;
import org.example.clinicarestauracao.Application.Dtos.ModalidadeDtos.ModalidadeRequestDto;
import org.example.clinicarestauracao.Application.Dtos.ModalidadeDtos.ModalidadeResponseDto;
import org.example.clinicarestauracao.Application.Services.ModalidadeService;
import org.example.clinicarestauracao.Domain.Entities.Modalidade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/modalidades")
public class ModalidadeController
{
    private final ModalidadeService service;

    public ModalidadeController(ModalidadeService service)
    {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ModalidadeResponseDto> createModalidade(@RequestBody ModalidadeRequestDto dto)
    {
        Modalidade m = service.createModalidade(ModalidadeMapper.requestDtoToEntity(dto));

        ModalidadeResponseDto response = ModalidadeMapper.entityToResponseDto(m);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(m.getId()).toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ModalidadeResponseDto>> findAllModalidades()
    {
        List<Modalidade> modalidades = service.findAllModalidades();

        List<ModalidadeResponseDto> dtos = modalidades.stream().map(ModalidadeMapper::entityToResponseDto).toList();

        return ResponseEntity.ok().body(dtos);
    }
}
