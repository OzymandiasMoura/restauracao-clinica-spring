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
import java.util.List;

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

    //Testes findAll

    @Test
    void shouldReturnAllCargos()
    {
        Cargo activeCargo = CargoTestBuilder.newCargo().setId(1L).setNome("Monitor").setAtivo(true).build();
        Cargo inactiveCargo = CargoTestBuilder.newCargo().setId(2L).setNome("Recepcionista").setAtivo(false).build();

        Mockito.when(service.findAllCargo()).thenReturn(List.of(activeCargo, inactiveCargo));

        ResponseEntity<List<CargoResponseDto>> response = controller.findAllCargos();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        CargoResponseDto first = response.getBody().getFirst();

        assertEquals(1L, first.id());
        assertEquals("Monitor", first.nome());
        assertTrue(first.ativo());

        CargoResponseDto second = response.getBody().get(1);
        assertEquals(2L, second.id());
        assertEquals("Recepcionista", second.nome());
        assertFalse(second.ativo());

        Mockito.verify(service).findAllCargo();
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoCargos()
    {
        Mockito.when(service.findAllCargo()).thenReturn(List.of());

        ResponseEntity<List<CargoResponseDto>> response = controller.findAllCargos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        Mockito.verify(service).findAllCargo();
    }

    //Testes FindById

    @Test
    void shouldFindCargoByIdSuccessfully()
    {
        Cargo existing = CargoTestBuilder.newCargo().setId(1L).build();

        Mockito.when(service.findCargoById(1L)).thenReturn(existing);

        ResponseEntity<CargoResponseDto> response = controller.findCargoById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("Monitor", response.getBody().nome());
        assertTrue(response.getBody().ativo());

        Mockito.verify(service).findCargoById(1L);
    }

    //Teste FindByName

    @Test
    void shouldFindCargoByNameSuccessfully()
    {
        Cargo existing = CargoTestBuilder.newCargo().setId(1L).setNome("Monitor").setAtivo(true).build();

        Mockito.when(service.findCargoByNome("Monitor")).thenReturn(existing);

        ResponseEntity<CargoResponseDto> response = controller.findCargoByNome("Monitor");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("Monitor", response.getBody().nome());
        assertTrue(response.getBody().ativo());

        Mockito.verify(service).findCargoByNome("Monitor");
    }

    @Test
    void shouldUpdateCargoSuccessfully()
    {
        CargoRequestDto request = new CargoRequestDto("Recepcionista");

        Cargo updated = CargoTestBuilder.newCargo().setId(1L).setNome("Recepcionista").setAtivo(false).build();

        Mockito.when(service.updateCargo(Mockito.any(Cargo.class), Mockito.eq(1L))).thenReturn(updated);

        ResponseEntity<CargoResponseDto> response = controller.updateCargoById(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("Recepcionista", response.getBody().nome());
        assertFalse(response.getBody().ativo());

        ArgumentCaptor<Cargo> captor = ArgumentCaptor.forClass(Cargo.class);

        Mockito.verify(service).updateCargo(captor.capture(), Mockito.eq(1L));

        Cargo sentToService = captor.getValue();

        assertNull(sentToService.getId());
        assertEquals("Recepcionista", sentToService.getNome());
    }

    //Teste softDelete

    @Test
    void shouldSoftDeleteCargoAndReturnNoContent()
    {
        ResponseEntity<Void> response = controller.softDeleteCargoById(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        assertNull(response.getBody());

        Mockito.verify(service).softDeleteCargoById(1L);
    }

    //Teste activateCargo

    @Test
    void shouldActivateCargoAndReturnNoContent()
    {
        ResponseEntity<Void> response = controller.activateCargoById(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        assertNull(response.getBody());

        Mockito.verify(service).reactivateCargoById(1L);
    }

}