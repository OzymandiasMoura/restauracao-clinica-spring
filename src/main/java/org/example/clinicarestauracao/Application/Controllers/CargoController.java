package org.example.clinicarestauracao.Application.Controllers;

import org.example.clinicarestauracao.Application.Controllers.Mappers.CargoMapper;
import org.example.clinicarestauracao.Application.Dtos.CargoDtos.CargoRequestDto;
import org.example.clinicarestauracao.Application.Dtos.CargoDtos.CargoResponseDto;
import org.example.clinicarestauracao.Application.Services.CargoService;
import org.example.clinicarestauracao.Domain.Entities.Cargo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/cargos")
public class CargoController
{
    private final CargoService service;

    public CargoController(CargoService service)
    {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CargoResponseDto> createCargo(@RequestBody CargoRequestDto cargoRequestDto)
    {
        Cargo cargo = CargoMapper.requestDtoToEntity(cargoRequestDto);

        Cargo created = service.createCargo(cargo);

        CargoResponseDto response = CargoMapper.entityToResponseDto(created);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CargoResponseDto>> findAllCargos()
    {
        List<Cargo> cargos = service.findAllCargo();

        List<CargoResponseDto> response = cargos.stream().map(CargoMapper::entityToResponseDto).toList();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CargoResponseDto> findCargoById(@PathVariable Long id)
    {
        Cargo cargo = service.findCargoById(id);

        CargoResponseDto response = CargoMapper.entityToResponseDto(cargo);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/search/{nome}")
    public ResponseEntity<CargoResponseDto> findCargoByName(@PathVariable String nome)
    {
        Cargo cargo = service.findCargoByNome(nome);

        CargoResponseDto response = CargoMapper.entityToResponseDto(cargo);

        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CargoResponseDto> updateCargoById(@PathVariable Long id, @RequestBody CargoRequestDto cargoRequestDto)
    {
        Cargo cargo = CargoMapper.requestDtoToEntity(cargoRequestDto);

        Cargo updated = service.updateCargo(cargo, id);

        CargoResponseDto response = CargoMapper.entityToResponseDto(updated);

        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> softDeleteCargoById(@PathVariable Long id)
    {
        service.softDeleteCargoById(id);

        return ResponseEntity.noContent().build();
    }
}
