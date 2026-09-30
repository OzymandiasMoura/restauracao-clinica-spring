package org.example.clinicarestauracao.Application.Controllers;

import org.example.clinicarestauracao.Application.Controllers.Mappers.FuncionarioMapper;
import org.example.clinicarestauracao.Application.Dtos.FuncionarioDtos.*;
import org.example.clinicarestauracao.Application.Services.CargoService;
import org.example.clinicarestauracao.Application.Services.FuncionarioService;
import org.example.clinicarestauracao.Application.Services.UserService;
import org.example.clinicarestauracao.Domain.Entities.Cargo;
import org.example.clinicarestauracao.Domain.Entities.Funcionario;
import org.example.clinicarestauracao.Domain.Entities.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController
{
    private final FuncionarioService service;
    private final UserService userService;
    private final CargoService cargoService;

    public FuncionarioController(FuncionarioService service, UserService userService, CargoService cargoService)
    {
        this.service = service;
        this.userService = userService;
        this.cargoService = cargoService;
    }

    @GetMapping
    public ResponseEntity<List<FuncionarioResponseDto>> findAllFuncionarios()
    {
        List<Funcionario> funcionarios = service.findAllFuncionarios();
        List<FuncionarioResponseDto> response = funcionarios.stream().map(FuncionarioMapper::entityToResponseDto).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioResponseDto> findFuncionarioById(@PathVariable Long id)
    {
        Funcionario funcionario = service.findFuncionarioById(id);
        FuncionarioResponseDto response = FuncionarioMapper.entityToResponseDto(funcionario);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<FuncionarioResponseDto> findFuncionarioByEmail(@RequestParam String email)
    {
        Funcionario  funcionario = service.findFuncionarioByEmail(email);
        FuncionarioResponseDto response = FuncionarioMapper.entityToResponseDto(funcionario);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/{cpf}")
    public ResponseEntity<FuncionarioResponseDto> findFuncionarioByCpf(@PathVariable String cpf)
    {
        Funcionario funcionario = service.findFuncionarioByCpf(cpf);
        FuncionarioResponseDto response = FuncionarioMapper.entityToResponseDto(funcionario);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<FuncionarioResponseDto> createFuncionario(@RequestBody FuncionarioCreateRequestDto dto)
    {
        Cargo cargo = cargoService.findCargoById(dto.cargoId());
        Funcionario funcionario = FuncionarioMapper.createRequestDtoToEntity(dto, cargo);
        Funcionario created = service.createFuncionario(funcionario);

        FuncionarioResponseDto response = FuncionarioMapper.entityToResponseDto(created);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri();

        return ResponseEntity.created(location).body(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioResponseDto> updateFuncionario(@PathVariable Long id, @RequestBody FuncionarioUpdateRequestDto dto)
    {
        Cargo cargo = cargoService.findCargoById(dto.cargoId());
        Funcionario funcionario = FuncionarioMapper.updateRequestDtoToEntity(dto, cargo);


        Funcionario updated = service.updateFuncionario(id, funcionario);

        FuncionarioResponseDto response = FuncionarioMapper.entityToResponseDto(updated);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/dismiss")
    public ResponseEntity<Void> dismissFuncionario(@PathVariable Long id, @RequestBody FuncionarioDismissalRequestDto dto)
    {
        service.dismissFuncionarioById(id, dto.dataDemissao());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<Void> reactivateFuncionario(@PathVariable Long id)
    {
        service.reactivateFuncionarioById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/user")
    public ResponseEntity<Void> setFuncionarioUser(@PathVariable Long id, @RequestBody FuncionarioUserRequestDto dto)
    {
        User user = userService.findUserById(dto.userId());

        service.linkUserToFuncionario(id, user);

        return ResponseEntity.noContent().build();
    }
}
