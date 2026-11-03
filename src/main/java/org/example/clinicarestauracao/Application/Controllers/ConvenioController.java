package org.example.clinicarestauracao.Application.Controllers;

import org.example.clinicarestauracao.Application.Controllers.Mappers.ConvenioMapper;
import org.example.clinicarestauracao.Application.Dtos.ConvenioDtos.ConvenioRequestDto;
import org.example.clinicarestauracao.Application.Dtos.ConvenioDtos.ConvenioResponseDto;
import org.example.clinicarestauracao.Application.Services.ConvenioService;
import org.example.clinicarestauracao.Domain.Entities.Convenio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/convenios")
public class ConvenioController
{
    private final ConvenioService service;

    public ConvenioController(ConvenioService service)
    {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ConvenioResponseDto> createConvenio(@RequestBody ConvenioRequestDto dto)
    {
        Convenio convenio = service.createConvenio(ConvenioMapper.requestDtoToEntity(dto));

        ConvenioResponseDto response = ConvenioMapper.entityToResponseDto(convenio);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(convenio.getId()).toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ConvenioResponseDto>> findAllConvenios()
    {
        List<Convenio> convenios = service.findAllConvenios();

        List<ConvenioResponseDto> dtos = convenios.stream().map(ConvenioMapper::entityToResponseDto).toList();

        return ResponseEntity.ok().body(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConvenioResponseDto> findConvenioById(@PathVariable Long id)
    {
        Convenio convenio = service.findConvenioById(id);

        ConvenioResponseDto response = ConvenioMapper.entityToResponseDto(convenio);

        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConvenioResponseDto> updateConvenioById(@PathVariable Long id, @RequestBody ConvenioRequestDto dto)
    {
        Convenio updated = service.updateConvenio(id,  ConvenioMapper.requestDtoToEntity(dto));

        ConvenioResponseDto response = ConvenioMapper.entityToResponseDto(updated);

        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateConvenioById(@PathVariable Long id)
    {
        service.deactivateConvenioById(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateConvenioById(@PathVariable Long id)
    {
        service.reabrirConvenioById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<ConvenioResponseDto> findConvenioByDescricao(@RequestParam String descricao)
    {
        Convenio convenio = service.findConvenioByDescricao(descricao);

        ConvenioResponseDto response = ConvenioMapper.entityToResponseDto(convenio);

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConvenioById(@PathVariable Long id)
    {
        service.deleteConvenioById(id);

        return ResponseEntity.noContent().build();
    }
}
