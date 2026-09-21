package org.example.clinicarestauracao.Domain.Entities;

import org.example.clinicarestauracao.Application.Exceptions.Cargo.CargoWithInvalidInformationException;
import org.example.clinicarestauracao.Builders.CargoTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CargoTest
{
    @Test
    void shouldCreateCargoWithIdAndValidName()
    {
        Cargo cargo = CargoTestBuilder.newCargo().build();

        assertEquals(1L, cargo.getId());
        assertEquals("Monitor", cargo.getNome());
        assertTrue(cargo.isAtivo());
    }

    @Test
    void shouldCreateCargoWithoutId()
    {
        Cargo cargo = CargoTestBuilder.newCargo().buildForCreate();

        assertNull(cargo.getId());
        assertEquals("Monitor", cargo.getNome());
        assertTrue(cargo.isAtivo());
    }

    @Test
    void shouldCreateInactiveCargo()
    {
        Cargo cargo = CargoTestBuilder.newCargo().setAtivo(false).build();

        assertFalse(cargo.isAtivo());
    }

    @Test
    void shouldRejectNullName()
    {
        CargoWithInvalidInformationException ex = assertThrows(CargoWithInvalidInformationException.class, () -> CargoTestBuilder.newCargo().setNome(null).build());

        assertEquals("Nome do cargo não pode ser nulo ou vazio.", ex.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "",
            " ",
            "       "
    })
    void shouldRejectEmptyOrBlankName(String nome)
    {
        CargoWithInvalidInformationException ex = assertThrows(CargoWithInvalidInformationException.class, () -> CargoTestBuilder.newCargo().setNome(nome).build());

        assertEquals("Nome do cargo não pode ser nulo ou vazio.", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "As",
            "   AS   ",
            "a",
            " a "
    })
    void shouldRejectNameWithLessThanThreeCharacters(String nome)
    {
        CargoWithInvalidInformationException ex = assertThrows(CargoWithInvalidInformationException.class, () -> CargoTestBuilder.newCargo().setNome(nome).build());

        assertEquals("Nome deve ter pelo menos 3 caracteres.", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "asa",
            "   asa  "
    })
    void shouldAcceptNameWithExactlyThreeCharacters(String nome)
    {
        Cargo cargo = CargoTestBuilder.newCargo().setNome(nome).buildForCreate();

        assertEquals("asa", cargo.getNome());
    }

    @Test
    void shouldTrimName()
    {
        Cargo cargo = CargoTestBuilder.newCargo().setNome("   Monitor  ").buildForCreate();

        assertEquals("Monitor", cargo.getNome());
    }

    @Test
    void shouldUpdateCargoWithValidName()
    {
        Cargo cargo = CargoTestBuilder.newCargo().build();

        cargo.setNome("Medico");

        assertEquals("Medico", cargo.getNome());
    }

    @ParameterizedTest
    @CsvSource({
            "'', 'Nome do cargo não pode ser nulo ou vazio.'",
            "'  ', 'Nome do cargo não pode ser nulo ou vazio.'",
            ", 'Nome do cargo não pode ser nulo ou vazio.'",
            "'TI', 'Nome deve ter pelo menos 3 caracteres.'"
    })
    void shouldRejectInvalidNameWhenUpdatingCargo(String nome, String message)
    {
        Cargo cargo = CargoTestBuilder.newCargo().build();
        CargoWithInvalidInformationException ex = assertThrows(CargoWithInvalidInformationException.class, () -> cargo.setNome(nome));

        assertEquals(message, ex.getMessage());
        assertEquals("Monitor", cargo.getNome());
    }

    @Test
    void shouldDeactivateCargo()
    {
        Cargo cargo = CargoTestBuilder.newCargo().setAtivo(true).build();

        cargo.deactivate();

        assertFalse(cargo.isAtivo());
    }

    @Test
    void shouldActivateCargo()
    {
        Cargo cargo = CargoTestBuilder.newCargo().setAtivo(false).build();

        cargo.activate();

        assertTrue(cargo.isAtivo());
    }

    @Test
    void shouldRemainInactiveWhenDeactivatedMoreThanOnce()
    {
        Cargo cargo = CargoTestBuilder.newCargo().setAtivo(false).build();

        cargo.deactivate();
        cargo.deactivate();

        assertFalse(cargo.isAtivo());
    }

}