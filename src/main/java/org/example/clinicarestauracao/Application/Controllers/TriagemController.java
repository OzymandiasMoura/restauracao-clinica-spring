package org.example.clinicarestauracao.Application.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clinicarestauracao.Application.Dtos.Triagem.TriagemReagendamentoDto;
import org.example.clinicarestauracao.Application.Dtos.Triagem.TriagemRequestDto;
import org.example.clinicarestauracao.Application.Dtos.Triagem.TriagemResponseDto;
import org.example.clinicarestauracao.Application.Services.TriagemService;
import org.example.clinicarestauracao.Domain.Enums.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/triagens")
@RequiredArgsConstructor
public class TriagemController {

    private final TriagemService triagemService;

    @PostMapping
    public ResponseEntity<TriagemResponseDto> create(
            @RequestBody @Valid TriagemRequestDto dto
    ) {
        TriagemResponseDto triagem = triagemService.create(dto);

        URI location = URI.create("/triagens/" + triagem.id());

        return ResponseEntity.created(location).body(triagem);
    }

    @GetMapping
    public ResponseEntity<List<TriagemResponseDto>> listAll() {
        return ResponseEntity.ok(triagemService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TriagemResponseDto> findById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(triagemService.findById(id));
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<TriagemResponseDto> findByName(
            @PathVariable String nome
    ) {
        return ResponseEntity.ok(triagemService.findByName(nome));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TriagemResponseDto>> findByStatus(
            @PathVariable Status status
    ) {
        return ResponseEntity.ok(triagemService.findByStatus(status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TriagemResponseDto> update(
            @PathVariable Long id,
            @RequestBody @Valid TriagemRequestDto dto
    ) {
        return ResponseEntity.ok(triagemService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        triagemService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<TriagemResponseDto> concluir(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(triagemService.concluir(id));
    }
    @PatchMapping("/{id}/desconcluir")
    public ResponseEntity<TriagemResponseDto> desconcluir(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                triagemService.desconcluir(id)
        );
    }
    
    @PatchMapping("/{id}/reagendar")
    public ResponseEntity<TriagemResponseDto> reagendar(
            @PathVariable Long id,
            @RequestBody @Valid TriagemReagendamentoDto dto
    ) {
        return ResponseEntity.ok(
                triagemService.reagendar(id, dto.novaData())
        );
    }
}

