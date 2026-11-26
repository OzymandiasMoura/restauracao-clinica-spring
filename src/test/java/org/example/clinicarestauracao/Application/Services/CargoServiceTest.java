package org.example.clinicarestauracao.Application.Services;


import org.junit.jupiter.api.DisplayName;
import org.example.clinicarestauracao.Application.Exceptions.Cargo.CargoNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Cargo.CargoWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.CargoRepository;
import org.example.clinicarestauracao.Application.Interfaces.FuncionarioRepository;
import org.example.clinicarestauracao.Builders.CargoTestBuilder;
import org.example.clinicarestauracao.Domain.Entities.Cargo;
import org.example.clinicarestauracao.Infrastruct.Database.Audit.DatabaseAuditContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CargoServiceTest
{
    @Mock
    private CargoRepository repository;
    @Mock
    private FuncionarioRepository funcionarioRepository;
    @Mock
    private DatabaseAuditContext audit;
    @InjectMocks
    private CargoService service;

    //Testes para o createCargo


    @DisplayName("Deve criar cargo com sucesso")
    @Test
    void shouldCreateCargoSuccessfully()
    {
        Cargo entrada = CargoTestBuilder.newCargo().buildForCreate();
        Cargo cargoSalvo = CargoTestBuilder.newCargo().build();

        Mockito.when(repository.findCargoByNomeIgnoreCase(entrada.getNome())).thenReturn(Optional.empty());
        Mockito.when(repository.save(Mockito.any())).thenReturn(cargoSalvo);

        Cargo response = service.createCargo(entrada);

        assertNotNull(response);
        assertSame(cargoSalvo, response);
        assertEquals(1L, response.getId());
        assertEquals("Monitor", response.getNome());
        assertTrue(response.isAtivo());

        Mockito.verify(repository).findCargoByNomeIgnoreCase(entrada.getNome());

        ArgumentCaptor<Cargo> captor = ArgumentCaptor.forClass(Cargo.class);
        Mockito.verify(repository).save(captor.capture());

        Cargo cargo = captor.getValue();
        assertNull(cargo.getId());
        assertEquals("Monitor", cargo.getNome());
        assertTrue(cargo.isAtivo());
    }

    @DisplayName("Deve lançar exceção quando criando cargo com duplicado nome")
    @Test
    void shouldThrowExceptionWhenCreatingCargoWithDuplicatedName()
    {
        Cargo entry = CargoTestBuilder.newCargo().buildForCreate();
        Cargo existed = CargoTestBuilder.newCargo().build();

        Mockito.when(repository.findCargoByNomeIgnoreCase(entry.getNome())).thenReturn(Optional.of(existed));

        CargoWithInvalidInformationException ex = assertThrows(CargoWithInvalidInformationException.class, () -> service.createCargo(entry));

        assertEquals("Nome do cargo já existente.", ex.getMessage());

        Mockito.verify(repository).findCargoByNomeIgnoreCase(entry.getNome());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Cargo.class));
    }

    @DisplayName("Deve rejeitar cargo nome duplicado ignorando caso")
    @Test
    void shouldRejectCargoNameDuplicatedIgnoringCase()
    {
        Cargo entrada = CargoTestBuilder.newCargo().setNome("recepcionista").buildForCreate();
        Cargo existente = CargoTestBuilder.newCargo().setNome("Recepcionista").build();

        Mockito.when(repository.findCargoByNomeIgnoreCase("recepcionista")).thenReturn(Optional.of(existente));

        CargoWithInvalidInformationException exception = assertThrows(CargoWithInvalidInformationException.class, () -> service.createCargo(entrada));

        assertEquals("Nome do cargo já existente.", exception.getMessage());

        Mockito.verify(repository).findCargoByNomeIgnoreCase("recepcionista");
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }


    //Testes para findByNome

    @DisplayName("Deve buscar cargo por nome com sucesso")
    @Test
    void shouldFindCargoByNameSuccessfully()
    {
        String name = "Monitor";
        Cargo existed = CargoTestBuilder.newCargo().build();

        Mockito.when(repository.findCargoByNomeIgnoreCase(name)).thenReturn(Optional.of(existed));

        Cargo response = service.findCargoByNome(name);

        assertNotNull(response);
        assertSame(existed, response);
        assertEquals(1L, response.getId());
        assertEquals("Monitor", response.getNome());

        Mockito.verify(repository).findCargoByNomeIgnoreCase(name);
    }

    @DisplayName("Deve lançar exceção quando cargo é não encontrado por nome")
    @Test
    void shouldThrowExceptionWhenCargoIsNotFoundByName()
    {
        Mockito.when(repository.findCargoByNomeIgnoreCase("Monitor")).thenReturn(Optional.empty());

        CargoNotFoundException ex = assertThrows(CargoNotFoundException.class, () -> service.findCargoByNome("Monitor"));

        assertEquals("Cargo não encontrado.", ex.getMessage());

        Mockito.verify(repository).findCargoByNomeIgnoreCase("Monitor");
    }

    @DisplayName("Deve normalizar nome quando buscando cargo por nome")
    @Test
    void shouldNormalizeNameWhenFindingCargoByName()
    {
        Cargo existed = CargoTestBuilder.newCargo().build();

        Mockito.when(repository.findCargoByNomeIgnoreCase("Monitor")).thenReturn(Optional.of(existed));

        Cargo response = service.findCargoByNome("  Monitor  ");

        assertSame(existed, response);
        Mockito.verify(repository).findCargoByNomeIgnoreCase("Monitor");

    }

    @DisplayName("Deve lançar exceção quando nome é nulo vazio ou em branco")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "    "})
    void shouldThrowExceptionWhenNameIsNullEmptyOrBlank(String nome)
    {
        CargoNotFoundException exception = assertThrows(CargoNotFoundException.class, () -> service.findCargoByNome(nome));

        assertEquals("Nome do cargo não pode ser nulo ou vazio.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

    //Testes findById

    @DisplayName("Deve buscar cargo por ID com sucesso")
    @Test
    void shouldFindCargoByIdSuccessfully()
    {
        Cargo existed = CargoTestBuilder.newCargo().build();

        Mockito.when(repository.findCargoById(1L)).thenReturn(Optional.of(existed));

        Cargo response = service.findCargoById(1L);

        assertSame(existed, response);
        Mockito.verify(repository).findCargoById(1L);
    }

    @DisplayName("Deve lançar exceção quando cargo é não encontrado por ID")
    @Test
    void shouldThrowExceptionWhenCargoIsNotFoundById()
    {
        Mockito.when(repository.findCargoById(1L)).thenReturn(Optional.empty());

        CargoNotFoundException ex = assertThrows(CargoNotFoundException.class, () -> service.findCargoById(1L));

        assertEquals("Cargo não encontrado.", ex.getMessage());
        Mockito.verify(repository).findCargoById(1L);
    }

    @DisplayName("Deve lançar exceção quando cargo ID é inválido")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldThrowExceptionWhenCargoIdIsInvalid(Long id)
    {
        CargoNotFoundException exception = assertThrows(CargoNotFoundException.class, () -> service.findCargoById(id));

        assertEquals("Cargo não encontrado.", exception.getMessage());
        Mockito.verifyNoInteractions(repository);
    }

    @DisplayName("Deve buscar todos os cargos com sucesso")
    @Test
    void shouldFindAllCargosSuccessfully()
    {
        Cargo first = CargoTestBuilder.newCargo().setId(1L).setNome("Monitor").build();
        Cargo second = CargoTestBuilder.newCargo().setId(2L).setNome("Recepcionista").build();

        List<Cargo> cargoList = List.of(first, second);

        Mockito.when(repository.findAll()).thenReturn(cargoList);

        List<Cargo> response = service.findAllCargo();

        assertSame(cargoList, response);
        assertEquals(2, response.size());
        assertSame(first, response.get(0));
        assertSame(second, response.get(1));

        Mockito.verify(repository).findAll();
    }

    @DisplayName("Deve retornar uma lista vazia quando não há cargos")
    @Test
    void  shouldReturnEmptyListWhenThereAreNoCargos()
    {
        Mockito.when(repository.findAll()).thenReturn(List.of());

        List<Cargo> response = service.findAllCargo();

        assertNotNull(response);
        assertTrue(response.isEmpty());

        Mockito.verify(repository).findAll();
    }

    //Testes updateCargo


    @DisplayName("Deve atualizar cargo com sucesso")
    @Test
    void shouldUpdateCargoSuccessfully()
    {
        Cargo existing = CargoTestBuilder.newCargo().setId(1L).setNome("Monitor").setAtivo(false).build();

        Cargo input = CargoTestBuilder.newCargo().setNome("Recepcionista").buildForCreate();

        Mockito.when(repository.findCargoById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(repository.findCargoByNomeIgnoreCase(input.getNome())).thenReturn(Optional.empty());
        Mockito.when(repository.save(existing)).thenReturn(existing);

        Cargo response = service.updateCargo(input, 1L);

        assertSame(existing, response);
        assertEquals(1L, response.getId());
        assertEquals("Recepcionista", response.getNome());
        assertFalse(response.isAtivo());

        Mockito.verify(repository).findCargoById(1L);
        Mockito.verify(repository).findCargoByNomeIgnoreCase(input.getNome());
        Mockito.verify(repository).save(existing);
    }

    @DisplayName("Deve lançar exceção quando atualizando cargo não encontrado")
    @Test
    void shouldThrowExceptionWhenUpdatingCargoNotFound()
    {
        Cargo input = CargoTestBuilder.newCargo().setNome("Recepcionista").buildForCreate();

        Mockito.when(repository.findCargoById(1L)).thenReturn(Optional.empty());

        CargoNotFoundException exception = assertThrows(CargoNotFoundException.class, () -> service.updateCargo(input, 1L));

        assertEquals("Cargo não encontrado.", exception.getMessage());
        Mockito.verify(repository).findCargoById(1L);
        Mockito.verify(repository, Mockito.never()).findCargoByNomeIgnoreCase(Mockito.anyString());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @DisplayName("Deve lançar exceção quando atualizando cargo com ID inválido")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1})
    void shouldThrowExceptionWhenUpdatingCargoWithInvalidId(Long id)
    {
        Cargo input = CargoTestBuilder.newCargo().buildForCreate();

        CargoNotFoundException exception = assertThrows(CargoNotFoundException.class, () -> service.updateCargo(input, id));

        assertEquals("Cargo não encontrado.", exception.getMessage());
        Mockito.verifyNoInteractions(repository);
    }

    @DisplayName("Deve lançar exceção quando atualizando cargo com nome usado por outro cargo")
    @Test
    void shouldThrowExceptionWhenUpdatingCargoWithNameUsedByAnotherCargo()
    {
        Cargo existing = CargoTestBuilder.newCargo().setId(1L).setNome("Monitor").build();
        Cargo input = CargoTestBuilder.newCargo().setNome("Recepcionista").buildForCreate();
        Cargo another = CargoTestBuilder.newCargo().setId(2L).setNome("Recepcionista").build();

        Mockito.when(repository.findCargoById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(repository.findCargoByNomeIgnoreCase("Recepcionista")).thenReturn(Optional.of(another));

        CargoWithInvalidInformationException ex = assertThrows(CargoWithInvalidInformationException.class, () -> service.updateCargo(input, 1L));

        assertEquals("Nome do cargo já existente.", ex.getMessage());
        assertEquals("Monitor", existing.getNome());

        Mockito.verify(repository).findCargoById(1L);
        Mockito.verify(repository).findCargoByNomeIgnoreCase("Recepcionista");
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @DisplayName("Deve atualizar cargo quando nome pertence para mesmo cargo")
    @Test
    void shouldUpdateCargoWhenNameBelongsToSameCargo()
    {
        Cargo existing = CargoTestBuilder.newCargo().setId(1L).setNome("Monitor").build();
        Cargo input = CargoTestBuilder.newCargo().setNome("Monitor").buildForCreate();

        Mockito.when(repository.findCargoById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(repository.findCargoByNomeIgnoreCase("Monitor")).thenReturn(Optional.of(existing));
        Mockito.when(repository.save(existing)).thenReturn(existing);

        Cargo response = service.updateCargo(input, 1L);

        assertSame(existing, response);
        assertEquals(1L, response.getId());
        assertEquals("Monitor", response.getNome());
        assertTrue(response.isAtivo());

        Mockito.verify(repository).findCargoById(1L);
        Mockito.verify(repository).findCargoByNomeIgnoreCase("Monitor");
        Mockito.verify(repository).save(existing);
    }

    @DisplayName("Deve rejeitar atualizando cargo com nome usado por outro cargo ignorando caso")
    @Test
    void shouldRejectUpdatingCargoWithNameUsedByAnotherCargoIgnoringCase()
    {
        Cargo cargoAtual = CargoTestBuilder.newCargo().setId(1L).setNome("Monitor").build();
        Cargo entrada = CargoTestBuilder.newCargo().setNome("recepcionista").buildForCreate();
        Cargo outroCargo = CargoTestBuilder.newCargo().setId(2L).setNome("Recepcionista").build();

        Mockito.when(repository.findCargoById(1L)).thenReturn(Optional.of(cargoAtual));
        Mockito.when(repository.findCargoByNomeIgnoreCase("recepcionista")).thenReturn(Optional.of(outroCargo));

        CargoWithInvalidInformationException exception = assertThrows(CargoWithInvalidInformationException.class, () -> service.updateCargo(entrada, 1L));

        assertEquals("Nome do cargo já existente.", exception.getMessage());
        assertEquals("Monitor", cargoAtual.getNome());

        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    //Testes softDelete

    @DisplayName("Deve desativar cargo com sucesso")
    @Test
    void shouldDeactivateCargoSuccessfully()
    {
        Cargo existing = CargoTestBuilder.newCargo().setId(1L).setAtivo(true).build();

        Mockito.when(repository.findCargoById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(funcionarioRepository.existsByCargoAndAtivoTrue(existing)).thenReturn(false);
        Mockito.when(repository.save(existing)).thenReturn(existing);

        service.softDeleteCargoById(1L);

        assertFalse(existing.isAtivo());

        Mockito.verify(repository).findCargoById(1L);
        Mockito.verify(repository).save(existing);
        Mockito.verify(funcionarioRepository).existsByCargoAndAtivoTrue(existing);
        Mockito.verify(repository, Mockito.never()).delete(Mockito.any());
        Mockito.verify(repository, Mockito.never()).deleteById(Mockito.anyLong());
    }


    @DisplayName("Não deve salvar quando cargo é já inativo")
    @Test
    void shouldNotSaveWhenCargoIsAlreadyInactive()
    {
        Cargo existing = CargoTestBuilder.newCargo().setId(1L).setAtivo(false).build();

        Mockito.when(repository.findCargoById(1L)).thenReturn(Optional.of(existing));

        service.softDeleteCargoById(1L);

        assertFalse(existing.isAtivo());

        Mockito.verify(repository).findCargoById(1L);
        Mockito.verifyNoInteractions(funcionarioRepository);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @DisplayName("Deve lançar exceção quando lógica excluindo inexistente cargo")
    @Test
    void shouldThrowExceptionWhenSoftDeletingNonexistentCargo()
    {
        Mockito.when(repository.findCargoById(1L)).thenReturn(Optional.empty());

        CargoNotFoundException exception = assertThrows(CargoNotFoundException.class, () -> service.softDeleteCargoById(1L));

        assertEquals("Cargo não encontrado.", exception.getMessage());

        Mockito.verify(repository).findCargoById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @DisplayName("Deve lançar exceção quando lógica excluindo cargo com ID inválido")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldThrowExceptionWhenSoftDeletingCargoWithInvalidId(Long id)
    {
        CargoNotFoundException exception = assertThrows(CargoNotFoundException.class, () -> service.softDeleteCargoById(id));

        assertEquals("Cargo não encontrado.", exception.getMessage());
        Mockito.verifyNoInteractions(repository);
    }

    //Testes de reativação de cargo

    @DisplayName("Deve reativar cargo com sucesso")
    @Test
    void shouldReactivateCargoSuccessfully()
    {
        Cargo existing = CargoTestBuilder.newCargo().setId(1L).setAtivo(false).build();

        Mockito.when(repository.findCargoById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(repository.save(existing)).thenReturn(existing);

        service.reactivateCargoById(1L);

        assertTrue(existing.isAtivo());

        Mockito.verify(repository).findCargoById(1L);
        Mockito.verify(repository).save(existing);
    }

    @DisplayName("Não deve salvar quando cargo é já ativo")
    @Test
    void shouldNotSaveWhenCargoIsAlreadyActive()
    {
        Cargo existing = CargoTestBuilder.newCargo().setId(1L).setAtivo(true).build();

        Mockito.when(repository.findCargoById(1L)).thenReturn(Optional.of(existing));

        service.reactivateCargoById(1L);

        assertTrue(existing.isAtivo());

        Mockito.verify(repository).findCargoById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @DisplayName("Deve lançar exceção quando reativando inexistente cargo")
    @Test
    void shouldThrowExceptionWhenReactivatingNonexistentCargo()
    {
        Mockito.when(repository.findCargoById(1L)).thenReturn(Optional.empty());

        CargoNotFoundException exception = assertThrows(CargoNotFoundException.class, () -> service.reactivateCargoById(1L));

        assertEquals("Cargo não encontrado.", exception.getMessage());

        Mockito.verify(repository).findCargoById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @DisplayName("Deve lançar exceção quando reativando cargo com ID inválido")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldThrowExceptionWhenReactivatingCargoWithInvalidId(Long id)
    {
        CargoNotFoundException exception = assertThrows(CargoNotFoundException.class, () -> service.reactivateCargoById(id));

        assertEquals("Cargo não encontrado.", exception.getMessage());
        Mockito.verifyNoInteractions(repository);
    }

    @DisplayName("Não deve desativar cargo com ativo funcionários")
    @Test
    void shouldNotDeactivateCargoWithActiveFuncionarios()
    {
        Cargo existing =  CargoTestBuilder.newCargo().setId(1L).setAtivo(true).build();

        Mockito.when(repository.findCargoById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(funcionarioRepository.existsByCargoAndAtivoTrue(existing)).thenReturn(true);

        CargoWithInvalidInformationException exception = assertThrows(CargoWithInvalidInformationException.class, () -> service.softDeleteCargoById(existing.getId()));

        assertEquals("Cargo possui funcionários ativos.", exception.getMessage());
        assertTrue(existing.isAtivo());

        Mockito.verify(repository).findCargoById(1L);
        Mockito.verify(funcionarioRepository).existsByCargoAndAtivoTrue(existing);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    // Testes deleteCargo

    @DisplayName("Deve excluir cargo sem vinculado funcionários")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldDeleteCargoWithoutLinkedFuncionarios(boolean ativo)
    {
        Cargo cargo = CargoTestBuilder.newCargo().setId(1L).setAtivo(ativo).build();

        Mockito.when(repository.findCargoById(1L)).thenReturn(Optional.of(cargo));
        Mockito.when(funcionarioRepository.existsByCargo(Mockito.same(cargo))).thenReturn(false);

        service.deleteCargo(1L);

        InOrder inOrder = Mockito.inOrder(repository, funcionarioRepository, audit);
        inOrder.verify(repository).findCargoById(1L);
        inOrder.verify(funcionarioRepository).existsByCargo(Mockito.same(cargo));
        inOrder.verify(audit).setCurrentActor();
        inOrder.verify(repository).delete(Mockito.same(cargo));
        inOrder.verify(repository).flush();
        inOrder.verify(audit).clearActor();
        Mockito.verifyNoMoreInteractions(repository, funcionarioRepository, audit);
    }

    @DisplayName("Deve limpar auditoria contexo quando cargo exclusão falha")
    @Test
    void shouldClearAuditContextWhenCargoDeletionFails()
    {
        Cargo cargo = CargoTestBuilder.newCargo().setId(1L).build();

        Mockito.when(repository.findCargoById(1L)).thenReturn(Optional.of(cargo));
        Mockito.when(funcionarioRepository.existsByCargo(Mockito.same(cargo))).thenReturn(false);
        Mockito.doThrow(new IllegalStateException("Falha ao excluir cargo.")).when(repository).flush();

        assertThrows(IllegalStateException.class, () -> service.deleteCargo(1L));

        InOrder inOrder = Mockito.inOrder(repository, funcionarioRepository, audit);
        inOrder.verify(repository).findCargoById(1L);
        inOrder.verify(funcionarioRepository).existsByCargo(Mockito.same(cargo));
        inOrder.verify(audit).setCurrentActor();
        inOrder.verify(repository).delete(Mockito.same(cargo));
        inOrder.verify(repository).flush();
        inOrder.verify(audit).clearActor();
        Mockito.verifyNoMoreInteractions(repository, funcionarioRepository, audit);
    }

    @DisplayName("Deve rejeitar excluindo cargo com vinculado funcionários")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldRejectDeletingCargoWithLinkedFuncionarios(boolean ativo)
    {
        Cargo cargo = CargoTestBuilder.newCargo().setId(1L).setAtivo(ativo).build();

        Mockito.when(repository.findCargoById(1L)).thenReturn(Optional.of(cargo));
        Mockito.when(funcionarioRepository.existsByCargo(Mockito.same(cargo))).thenReturn(true);

        CargoWithInvalidInformationException exception = assertThrows(CargoWithInvalidInformationException.class, () -> service.deleteCargo(1L));

        assertAll(
                () -> assertEquals("Cargo possui funcionário cadastrado.", exception.getMessage()),
                () -> assertEquals(ativo, cargo.isAtivo())
        );

        Mockito.verify(repository).findCargoById(1L);
        Mockito.verify(funcionarioRepository).existsByCargo(Mockito.same(cargo));
        Mockito.verify(repository, Mockito.never()).delete(Mockito.any(Cargo.class));
        Mockito.verifyNoInteractions(audit);
        Mockito.verifyNoMoreInteractions(repository, funcionarioRepository);
    }

    @DisplayName("Deve rejeitar excluindo inexistente cargo")
    @Test
    void shouldRejectDeletingNonexistentCargo()
    {
        Mockito.when(repository.findCargoById(1L)).thenReturn(Optional.empty());

        CargoNotFoundException exception = assertThrows(CargoNotFoundException.class, () -> service.deleteCargo(1L));

        assertEquals("Cargo não encontrado.", exception.getMessage());

        Mockito.verify(repository).findCargoById(1L);
        Mockito.verifyNoMoreInteractions(repository);
        Mockito.verifyNoInteractions(funcionarioRepository, audit);
    }

    @DisplayName("Deve rejeitar excluindo cargo com ID inválido")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldRejectDeletingCargoWithInvalidId(Long id)
    {
        CargoNotFoundException exception = assertThrows(CargoNotFoundException.class, () -> service.deleteCargo(id));

        assertEquals("Cargo não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository, funcionarioRepository, audit);
    }
}
