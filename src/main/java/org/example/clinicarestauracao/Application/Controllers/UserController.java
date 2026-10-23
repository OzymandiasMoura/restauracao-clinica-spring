package org.example.clinicarestauracao.Application.Controllers;

import org.example.clinicarestauracao.Application.Dtos.UserDtos.UserPasswordUpdateRequestDto;
import org.example.clinicarestauracao.Application.Dtos.UserDtos.UserRoleUpdateRequestDto;
import org.example.clinicarestauracao.Application.Services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
