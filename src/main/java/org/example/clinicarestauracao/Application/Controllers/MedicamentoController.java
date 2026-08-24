package org.example.clinicarestauracao.Application.Controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clinicarestauracao.Application.Dtos.Medicamento.MedicamentoRequestDTO;
import org.example.clinicarestauracao.Application.Services.MedicamentoService;
import org.example.clinicarestauracao.Domain.Entities.Medicamento;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicamento")
@RequiredArgsConstructor
public class MedicamentoController {

    private final MedicamentoService medicamentoService;

    @PostMapping
    public ResponseEntity<Medicamento> create(@RequestBody @Valid MedicamentoRequestDTO medicamento){
        Medicamento novoMedicamento = new Medicamento();
        novoMedicamento.setNome(medicamento.nome());
        novoMedicamento.setPreco(medicamento.preco());

        Medicamento save = medicamentoService.create(novoMedicamento);

        return ResponseEntity.status(HttpStatus.CREATED).body(save);

    }

    @PutMapping
    public ResponseEntity<Medicamento> update(@PathVariable Long id, @RequestBody @Valid MedicamentoRequestDTO medicamento){
        Medicamento dadosAtualizados = new Medicamento();
        dadosAtualizados.setNome(medicamento.nome());
        dadosAtualizados.setPreco(medicamento.preco());

        Medicamento save = medicamentoService.update(id, dadosAtualizados);

        return ResponseEntity.ok(save);

    }


    @GetMapping
    public ResponseEntity<List<Medicamento>> listAll(){
        return ResponseEntity.ok(medicamentoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Medicamento> findByID(@PathVariable Long id){
        return ResponseEntity.ok(medicamentoService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        medicamentoService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
