package org.example.clinicarestauracao.Application.Services;

import org.example.clinicarestauracao.Application.Exceptions.Cargo.CargoWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.CargoRepository;
import org.example.clinicarestauracao.Builders.CargoTestBuilder;
import org.example.clinicarestauracao.Domain.Entities.Cargo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CargoServiceTest
{
    @Mock
    private CargoRepository repository;
    @InjectMocks
    private CargoService service;

    @Test
    void shouldCreateCargoSuccessfully()
    {
        Cargo entrada = CargoTestBuilder.newCargo().buildForCreate();
        Cargo cargoSalvo = CargoTestBuilder.newCargo().build();

        Mockito.when(repository.findCargoByNome(entrada.getNome())).thenReturn(Optional.empty());
        Mockito.when(repository.save(Mockito.any())).thenReturn(cargoSalvo);

        Cargo response = service.createCargo(entrada);

        assertNotNull(response);
        assertSame(cargoSalvo, response);
        assertEquals(1L, response.getId());
        assertEquals("Monitor", response.getNome());

        Mockito.verify(repository).findCargoByNome(entrada.getNome());

        ArgumentCaptor<Cargo> captor = ArgumentCaptor.forClass(Cargo.class);
        Mockito.verify(repository).save(captor.capture());

        Cargo cargo = captor.getValue();
        assertNull(cargo.getId());
        assertEquals("Monitor", cargo.getNome());
    }

    @Test
    void shouldThrowExceptionWhenCreatingCargoWithDuplicatedName()
    {
        Cargo entry = CargoTestBuilder.newCargo().buildForCreate();
        Cargo existed = CargoTestBuilder.newCargo().build();

        Mockito.when(repository.findCargoByNome(entry.getNome())).thenReturn(Optional.of(existed));

        CargoWithInvalidInformationException ex =  assertThrows(CargoWithInvalidInformationException.class, () -> service.createCargo(entry));

        assertEquals("Nome do cargo já existente.", ex.getMessage());

        Mockito.verify(repository).findCargoByNome(entry.getNome());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Cargo.class));
    }
}