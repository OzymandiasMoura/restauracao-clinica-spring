package org.example.clinicarestauracao.Application.Controllers;

import org.example.clinicarestauracao.Application.Dtos.UserDtos.UserPasswordUpdateRequestDto;
import org.example.clinicarestauracao.Application.Dtos.UserDtos.UserRoleUpdateRequestDto;
import org.example.clinicarestauracao.Application.Services.UserService;
import org.example.clinicarestauracao.Domain.Enums.UserRoles;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController
{
    private final UserService service;

    public UserController(UserService service)
    {
        this.service = service;
    }


    @PatchMapping("/{userId}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long userId, @RequestBody UserPasswordUpdateRequestDto dto)
    {
        service.updatePassword(userId, dto.newPassword());

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{userId}/role")
    public ResponseEntity<Void> updateRole(@PathVariable Long userId, @RequestBody UserRoleUpdateRequestDto dto)
    {
        service.updateRole(userId, dto.role());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/roles")
    public ResponseEntity<List<UserRoles>> findAllRoles()
    {
        List<UserRoles> roles = Arrays.stream(UserRoles.values()).filter(role -> role != UserRoles.NO_ACCESS).toList();

        return ResponseEntity.ok(roles);
    }
}
