package org.example.clinicarestauracao.Application.Controllers;

import org.example.clinicarestauracao.Application.Controllers.Mappers.CargoMapper;
import org.example.clinicarestauracao.Application.Dtos.CargoDtos.CargoRequestDto;
import org.example.clinicarestauracao.Application.Dtos.CargoDtos.CargoResponseDto;
import org.example.clinicarestauracao.Application.Services.CargoService;
import org.example.clinicarestauracao.Domain.Entities.Cargo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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

        Cargo created =  service.createCargo(cargo);

        CargoResponseDto response = CargoMapper.entityToResponseDto(created);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri();

        return ResponseEntity.created(location).body(response);
    }
}
