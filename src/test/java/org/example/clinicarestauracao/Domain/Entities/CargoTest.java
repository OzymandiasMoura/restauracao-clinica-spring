package org.example.clinicarestauracao.Domain.Entities;


import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Deve criar cargo com ID e nome válidos")
    @Test
    void shouldCreateCargoWithIdAndValidName()
    {
        Cargo cargo = CargoTestBuilder.newCargo().build();

        assertEquals(1L, cargo.getId());
        assertEquals("Monitor", cargo.getNome());
        assertTrue(cargo.isAtivo());
    }

    @DisplayName("Deve criar cargo sem ID")
    @Test
    void shouldCreateCargoWithoutId()
    {
        Cargo cargo = CargoTestBuilder.newCargo().buildForCreate();

        assertNull(cargo.getId());
        assertEquals("Monitor", cargo.getNome());
        assertTrue(cargo.isAtivo());
    }

    @DisplayName("Deve criar inativo cargo")
    @Test
    void shouldCreateInactiveCargo()
    {
        Cargo cargo = CargoTestBuilder.newCargo().setAtivo(false).build();

        assertFalse(cargo.isAtivo());
    }

    @DisplayName("Deve rejeitar nome nulo")
    @Test
    void shouldRejectNullName()
    {
        CargoWithInvalidInformationException ex = assertThrows(CargoWithInvalidInformationException.class, () -> CargoTestBuilder.newCargo().setNome(null).build());

        assertEquals("Nome do cargo não pode ser nulo ou vazio.", ex.getMessage());
    }

    @DisplayName("Deve rejeitar vazio ou em branco nome")
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

    @DisplayName("Deve rejeitar nome com menos de três caracteres")
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

    @DisplayName("Deve rejeitar nome com mais de 250 caracteres")
    @Test
    void shouldRejectNameWithMoreThan250Characters()
    {
        String nome = "A".repeat(251);

        CargoWithInvalidInformationException exception = assertThrows(
                CargoWithInvalidInformationException.class,
                () -> CargoTestBuilder.newCargo().setNome(nome).build()
        );

        assertEquals("Nome deve ter no máximo 250 caracteres.", exception.getMessage());
    }

    @DisplayName("Deve aceitar nome com exatamente três caracteres")
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

    @DisplayName("Deve remover espaços nome")
    @Test
    void shouldTrimName()
    {
        Cargo cargo = CargoTestBuilder.newCargo().setNome("   Monitor  ").buildForCreate();

        assertEquals("Monitor", cargo.getNome());
    }

    @DisplayName("Deve atualizar cargo com válido nome")
    @Test
    void shouldUpdateCargoWithValidName()
    {
        Cargo cargo = CargoTestBuilder.newCargo().build();

        cargo.setNome("Medico");

        assertEquals("Medico", cargo.getNome());
    }

    @DisplayName("Deve rejeitar nome inválido quando atualizando cargo")
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

    @DisplayName("Deve desativar cargo")
    @Test
    void shouldDeactivateCargo()
    {
        Cargo cargo = CargoTestBuilder.newCargo().setAtivo(true).build();

        cargo.deactivate();

        assertFalse(cargo.isAtivo());
    }

    @DisplayName("Deve ativar cargo")
    @Test
    void shouldActivateCargo()
    {
        Cargo cargo = CargoTestBuilder.newCargo().setAtivo(false).build();

        cargo.activate();

        assertTrue(cargo.isAtivo());
    }

    @DisplayName("Deve permanecer inativo quando desativado mais que uma vez")
    @Test
    void shouldRemainInactiveWhenDeactivatedMoreThanOnce()
    {
        Cargo cargo = CargoTestBuilder.newCargo().setAtivo(false).build();

        cargo.deactivate();
        cargo.deactivate();

        assertFalse(cargo.isAtivo());
    }

}
