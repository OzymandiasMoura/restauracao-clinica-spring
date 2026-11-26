package org.example.clinicarestauracao.Application.Controllers;


import org.junit.jupiter.api.DisplayName;
import org.example.clinicarestauracao.Application.Dtos.UserDtos.UserPasswordUpdateRequestDto;
import org.example.clinicarestauracao.Application.Dtos.UserDtos.UserRoleUpdateRequestDto;
import org.example.clinicarestauracao.Application.Services.UserService;
import org.example.clinicarestauracao.Domain.Enums.UserRoles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest
{
    @Mock
    private UserService service;

    @InjectMocks
    private UserController controller;

    @DisplayName("Deve atualizar senha e retornar HTTP 200")
    @Test
    void shouldUpdatePasswordAndReturnOk()
    {
        Long userId = 1L;
        UserPasswordUpdateRequestDto dto = new UserPasswordUpdateRequestDto("nova-senha");

        ResponseEntity<Void> response = controller.updatePassword(userId, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(service).updatePassword(userId, "nova-senha");
    }

    @DisplayName("Deve atualizar perfil e retornar HTTP 200")
    @Test
    void shouldUpdateRoleAndReturnOk()
    {
        Long userId = 1L;
        UserRoleUpdateRequestDto dto = new UserRoleUpdateRequestDto(UserRoles.ADMIN);

        ResponseEntity<Void> response = controller.updateRole(userId, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(service).updateRole(userId, UserRoles.ADMIN);
    }

    @DisplayName("Deve retornar os perfis disponíveis")
    @Test
    void shouldReturnAssignableRoles()
    {
        ResponseEntity<List<UserRoles>> response = controller.findAllRoles();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(UserRoles.ADMIN, UserRoles.USER), response.getBody());

        Mockito.verifyNoInteractions(service);
    }

    @DisplayName("Deve excluir o usuário e retornar HTTP 204")
    @Test
    void shouldDeleteUserAndReturnNoContent()
    {
        Long userId = 1L;

        ResponseEntity<Void> response = controller.deleteUser(userId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(service).deleteUser(userId);
        Mockito.verifyNoMoreInteractions(service);
    }
}