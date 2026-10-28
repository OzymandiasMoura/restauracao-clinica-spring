package org.example.clinicarestauracao.Application.Controllers;

import lombok.AllArgsConstructor;
import org.example.clinicarestauracao.Application.Controllers.Mappers.InternacaoMapper;
import org.example.clinicarestauracao.Application.Dtos.InternacaoDtos.InternacaoCreateRequestDto;
import org.example.clinicarestauracao.Application.Dtos.InternacaoDtos.InternacaoEndRequestDto;
import org.example.clinicarestauracao.Application.Dtos.InternacaoDtos.InternacaoResponseDto;
import org.example.clinicarestauracao.Application.Dtos.InternacaoDtos.InternacaoUpdateRequestDto;
import org.example.clinicarestauracao.Application.Services.AcolhidoService;
import org.example.clinicarestauracao.Application.Services.ConvenioService;
import org.example.clinicarestauracao.Application.Services.InternacaoService;
import org.example.clinicarestauracao.Domain.Entities.Internacao;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/internacoes")
@AllArgsConstructor
public class InternacaoController
{
    private final InternacaoService service;
    private final AcolhidoService acolhidoService;
    private final ConvenioService convenioService;

    @GetMapping
    public ResponseEntity<List<InternacaoResponseDto>> findAll()
    {
        List<InternacaoResponseDto> response = service.findAllInternacoes().stream().map(InternacaoMapper::entityToResponseDto).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InternacaoResponseDto> findById(@PathVariable Long id)
    {
        Internacao internacao = service.findInternacaoById(id);

        return ResponseEntity.ok().body(InternacaoMapper.entityToResponseDto(internacao));
    }

    @GetMapping("/search/acolhido/{id}")
    public ResponseEntity<List<InternacaoResponseDto>> findInternacaoByAcolhidoId(@PathVariable Long id)
    {
        List<InternacaoResponseDto> response = service.findInternacoesByAcolhidoId(id).stream().map(InternacaoMapper::entityToResponseDto).toList();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/search/convenio/{id}")
    public ResponseEntity<List<InternacaoResponseDto>> findInternacaoByConvenioId(@PathVariable Long id)
    {
        List<InternacaoResponseDto> response = service.findInternacoesByConvenioId(id).stream().map(InternacaoMapper::entityToResponseDto).toList();

        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<InternacaoResponseDto> createInternacao(@RequestBody InternacaoCreateRequestDto dto)
    {
        Internacao internacao = InternacaoMapper.createRequestDtoToEntity(dto, acolhidoService.findAcolhidoById(dto.acolhidoId()), convenioService.findConvenioById(dto.convenioId()));
        Internacao created = service.createInternacao(internacao);

        InternacaoResponseDto response = InternacaoMapper.entityToResponseDto(created);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri();

        return  ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InternacaoResponseDto> updateInternacao(@PathVariable Long id, @RequestBody InternacaoUpdateRequestDto dto)
    {
        Internacao internacaoUpdated = service.updateInternacao(id, dto.dataSaidaPrevista(), dto.etapaTratamento(), dto.valorMensalidade());

        return ResponseEntity.ok().body(InternacaoMapper.entityToResponseDto(internacaoUpdated));
    }

    @PatchMapping("/{id}/end")
    public ResponseEntity<InternacaoResponseDto> endInternacao(@PathVariable Long id, @RequestBody InternacaoEndRequestDto dto)
    {
        InternacaoResponseDto response = InternacaoMapper.entityToResponseDto(service.endInternacaoById(id, dto.alta(), dto.dataSaida()));

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInternacao(@PathVariable Long id)
    {
        service.deleteInternacaoById(id);

        return ResponseEntity.noContent().build();
    }
}
