package org.example.clinicarestauracao.Application.Controllers;

import org.example.clinicarestauracao.Application.Dtos.CargoDtos.CargoRequestDto;
import org.example.clinicarestauracao.Application.Dtos.CargoDtos.CargoResponseDto;
import org.example.clinicarestauracao.Application.Services.CargoService;
import org.example.clinicarestauracao.Builders.CargoTestBuilder;
import org.example.clinicarestauracao.Domain.Entities.Cargo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CargoControllerTest
{
    @Mock
    private CargoService service;
    @InjectMocks
    private CargoController controller;

    @BeforeEach
    void setUpRequestContext()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8080);
        request.setRequestURI("/cargos");

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void clearRequestContext()
    {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldCreateCargoAndReturnCreated()
    {
        CargoRequestDto request = new CargoRequestDto("Monitor");
        Cargo created = CargoTestBuilder.newCargo().setId(1L).setNome("Monitor").setAtivo(true).build();

        Mockito.when(service.createCargo(Mockito.any(Cargo.class))).thenReturn(created);

        ResponseEntity<CargoResponseDto> response = controller.createCargo(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(URI.create("http://localhost:8080/cargos/1"), response.getHeaders().getLocation());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("Monitor", response.getBody().nome());
        assertTrue(response.getBody().ativo());

        ArgumentCaptor<Cargo> captor = ArgumentCaptor.forClass(Cargo.class);
        Mockito.verify(service).createCargo(captor.capture());

        Cargo cargo = captor.getValue();
        assertNull(cargo.getId());
        assertEquals("Monitor", cargo.getNome());
        assertTrue(cargo.isAtivo());
    }
}