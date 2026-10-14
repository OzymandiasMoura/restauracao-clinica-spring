package org.example.clinicarestauracao.Application.Controllers;

import lombok.AllArgsConstructor;
import org.example.clinicarestauracao.Application.Controllers.Mappers.AcolhidoMapper;
import org.example.clinicarestauracao.Application.Dtos.AcolhidoDtos.AcolhidoCreateRequestDto;
import org.example.clinicarestauracao.Application.Dtos.AcolhidoDtos.AcolhidoResponseDto;
import org.example.clinicarestauracao.Application.Dtos.AcolhidoDtos.AcolhidoUpdateRequestDto;
import org.example.clinicarestauracao.Application.Services.AcolhidoService;
import org.example.clinicarestauracao.Domain.Entities.Acolhido;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/acolhidos")
public class AcolhidoController
{
    private AcolhidoService service;

    @GetMapping
    public ResponseEntity<List<AcolhidoResponseDto>> findAllAcolhidos()
    {
        List<Acolhido> acolhidos= service.findAllAcolhidos();
        List<AcolhidoResponseDto> response = acolhidos.stream().map(AcolhidoMapper::entityToResponseDto).toList();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AcolhidoResponseDto> findAcolhidoById(@PathVariable Long id)
    {
        Acolhido acolhido = service.findAcolhidoById(id);

        return ResponseEntity.ok().body(AcolhidoMapper.entityToResponseDto(acolhido));
    }

    @GetMapping("/search/nome")
    public ResponseEntity<List<AcolhidoResponseDto>> findAcolhidoByNome(@RequestParam String nome)
    {
        List<Acolhido> acolhidos= service.findAcolhidoByNome(nome);
        List<AcolhidoResponseDto> response = acolhidos.stream().map(AcolhidoMapper::entityToResponseDto).toList();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/search/email")
    public ResponseEntity<AcolhidoResponseDto> findAcolhidoByEmail(@RequestParam String email)
    {
        Acolhido acolhido = service.findAcolhidoByEmail(email);

        return ResponseEntity.ok().body(AcolhidoMapper.entityToResponseDto(acolhido));
    }

    @GetMapping("/search/cpf")
    public ResponseEntity<AcolhidoResponseDto> findAcolhidoByCpf(@RequestParam String cpf)
    {
        Acolhido acolhido = service.findAcolhidoByCpf(cpf);

        return ResponseEntity.ok().body(AcolhidoMapper.entityToResponseDto(acolhido));
    }

    @PostMapping
    public ResponseEntity<AcolhidoResponseDto> createAcolhido(@RequestBody AcolhidoCreateRequestDto dto)
    {
        Acolhido acolhido = AcolhidoMapper.createRequestDtoToEntity(dto);
        Acolhido created = service.createAcolhido(acolhido);

        AcolhidoResponseDto response = AcolhidoMapper.entityToResponseDto(created);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AcolhidoResponseDto> updateAcolhido(@PathVariable Long id, @RequestBody AcolhidoUpdateRequestDto dto)
    {
        Acolhido acolhidoUpdated = service.updateAcolhido(id, AcolhidoMapper.updateRequestDtoToEntity(dto));

        return ResponseEntity.ok().body(AcolhidoMapper.entityToResponseDto(acolhidoUpdated));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateAcolhido(@PathVariable Long id)
    {
        service.deactivateAcolhido(id);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<Void> reactivateAcolhido(@PathVariable Long id)
    {
        service.reactivateAcolhido(id);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAcolhidoById(@PathVariable Long id)
    {
        service.deleteAcolhidoById(id);

        return ResponseEntity.noContent().build();
    }
}
