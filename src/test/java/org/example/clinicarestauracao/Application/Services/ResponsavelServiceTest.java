package org.example.clinicarestauracao.Application.Services;

import org.example.clinicarestauracao.Application.Exceptions.Responsavel.ResponsavelNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Responsavel.ResponsavelWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.ResponsavelRepository;
import org.example.clinicarestauracao.Builders.ResponsavelTestBuilder;
import org.example.clinicarestauracao.Domain.Entities.Responsavel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ResponsavelServiceTest
{
    @Mock
    private ResponsavelRepository repository;

    @InjectMocks
    private ResponsavelService service;

    // Testes do findAll

    @Test
    void shouldFindAllResponsaveisSuccessfully()
    {
        Responsavel primeiroResponsavel = ResponsavelTestBuilder.newResponsavel().setId(1L).build();
        Responsavel segundoResponsavel = ResponsavelTestBuilder.newResponsavel().setId(2L).setNome("Maria da Silva").build();
        List<Responsavel> responsaveis = List.of(primeiroResponsavel, segundoResponsavel);

        Mockito.when(repository.findAll()).thenReturn(responsaveis);

        List<Responsavel> response = service.findAll();

        assertAll(
                () -> assertSame(responsaveis, response),
                () -> assertEquals(2, response.size()),
                () -> assertSame(primeiroResponsavel, response.getFirst()),
                () -> assertSame(segundoResponsavel, response.get(1))
        );

        Mockito.verify(repository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoResponsaveis()
    {
        List<Responsavel> responsaveis = List.of();

        Mockito.when(repository.findAll()).thenReturn(responsaveis);

        List<Responsavel> response = service.findAll();

        assertAll(
                () -> assertSame(responsaveis, response),
                () -> assertTrue(response.isEmpty())
        );

        Mockito.verify(repository).findAll();
    }
    // Testes do findResponsavelById

    @Test
    void shouldFindResponsavelByIdSuccessfully()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setId(1L).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(responsavel));

        Responsavel response = service.findResponsavelById(1L);

        assertSame(responsavel, response);
        Mockito.verify(repository).findById(1L);
    }

    @Test
    void shouldThrowWhenResponsavelDoesNotExist()
    {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        ResponsavelNotFoundException exception = assertThrows(ResponsavelNotFoundException.class, () -> service.findResponsavelById(1L));

        assertEquals("Responsável não encontrado.", exception.getMessage());

        Mockito.verify(repository).findById(1L);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldRejectInvalidResponsavelId(Long id)
    {
        ResponsavelNotFoundException exception = assertThrows(ResponsavelNotFoundException.class, () -> service.findResponsavelById(id));

        assertEquals("Responsável não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

    // Testes do findResponsavelByNome

    @Test
    void shouldFindResponsaveisByPartialNameSuccessfully()
    {
        Responsavel primeiroResponsavel = ResponsavelTestBuilder.newResponsavel().setId(1L).setNome("Maria Silva").build();
        Responsavel segundoResponsavel = ResponsavelTestBuilder.newResponsavel().setId(2L).setNome("Maria Souza").build();
        List<Responsavel> responsaveis = List.of(primeiroResponsavel, segundoResponsavel);

        Mockito.when(repository.findByNomeContainingIgnoreCase("maria")).thenReturn(responsaveis);

        List<Responsavel> response = service.findResponsavelByNome("  maria  ");

        assertAll(
                () -> assertSame(responsaveis, response),
                () -> assertEquals(2, response.size()),
                () -> assertSame(primeiroResponsavel, response.getFirst()),
                () -> assertSame(segundoResponsavel, response.get(1))
        );

        Mockito.verify(repository).findByNomeContainingIgnoreCase("maria");
    }

    @Test
    void shouldThrowWhenNoResponsavelMatchesName()
    {
        Mockito.when(repository.findByNomeContainingIgnoreCase("Maria")).thenReturn(List.of());

        ResponsavelNotFoundException exception = assertThrows(ResponsavelNotFoundException.class, () -> service.findResponsavelByNome("Maria"));

        assertEquals("Responsável não encontrado.", exception.getMessage());

        Mockito.verify(repository).findByNomeContainingIgnoreCase("Maria");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
    void shouldRejectInvalidResponsavelName(String nome)
    {
        ResponsavelNotFoundException exception = assertThrows(ResponsavelNotFoundException.class, () -> service.findResponsavelByNome(nome));

        assertEquals("Responsável não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

    // Testes do findResponsavelByEmail

    @Test
    void shouldFindResponsavelByNormalizedEmailSuccessfully()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setEmail("joao.silva@email.com").build();

        Mockito.when(repository.findByEmail("joao.silva@email.com")).thenReturn(Optional.of(responsavel));

        Responsavel response = service.findResponsavelByEmail("  JOAO.SILVA@EMAIL.COM  ");

        assertSame(responsavel, response);

        Mockito.verify(repository).findByEmail("joao.silva@email.com");
    }

    @Test
    void shouldThrowWhenResponsavelEmailDoesNotExist()
    {
        Mockito.when(repository.findByEmail("inexistente@email.com")).thenReturn(Optional.empty());

        ResponsavelNotFoundException exception = assertThrows(ResponsavelNotFoundException.class, () -> service.findResponsavelByEmail("  INEXISTENTE@EMAIL.COM  "));

        assertEquals("Responsável não encontrado.", exception.getMessage());

        Mockito.verify(repository).findByEmail("inexistente@email.com");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
    void shouldRejectInvalidResponsavelEmail(String email)
    {
        ResponsavelNotFoundException exception = assertThrows(ResponsavelNotFoundException.class, () -> service.findResponsavelByEmail(email));

        assertEquals("Responsável não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }
    // Testes do findResponsavelByCpf

    @Test
    void shouldFindResponsavelByNormalizedCpfSuccessfully()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setCpf("16899535009").build();

        Mockito.when(repository.findByCpf("16899535009")).thenReturn(Optional.of(responsavel));

        Responsavel response = service.findResponsavelByCpf("  168.995.350-09  ");

        assertSame(responsavel, response);

        Mockito.verify(repository).findByCpf("16899535009");
    }

    @Test
    void shouldThrowWhenResponsavelCpfDoesNotExist()
    {
        Mockito.when(repository.findByCpf("52998224725")).thenReturn(Optional.empty());

        ResponsavelNotFoundException exception = assertThrows(ResponsavelNotFoundException.class, () -> service.findResponsavelByCpf("  529.982.247-25  "));

        assertEquals("Responsável não encontrado.", exception.getMessage());

        Mockito.verify(repository).findByCpf("52998224725");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
    void shouldRejectInvalidResponsavelCpf(String cpf)
    {
        ResponsavelNotFoundException exception = assertThrows(ResponsavelNotFoundException.class, () -> service.findResponsavelByCpf(cpf));

        assertEquals("Responsável não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }


    // Testes do createResponsavel

    @Test
    void shouldCreateResponsavelSuccessfully()
    {
        Responsavel responsavelRecebido = ResponsavelTestBuilder.newResponsavel().buildForCreate();
        Responsavel responsavelSalvo = ResponsavelTestBuilder.newResponsavel().setId(10L).build();

        Mockito.when(repository.existsByCpf(responsavelRecebido.getCpf())).thenReturn(false);
        Mockito.when(repository.existsByEmail(responsavelRecebido.getEmail())).thenReturn(false);
        Mockito.when(repository.save(responsavelRecebido)).thenReturn(responsavelSalvo);

        Responsavel response = service.createResponsavel(responsavelRecebido);

        assertSame(responsavelSalvo, response);

        var inOrder = Mockito.inOrder(repository);

        inOrder.verify(repository).existsByCpf(responsavelRecebido.getCpf());
        inOrder.verify(repository).existsByEmail(responsavelRecebido.getEmail());
        inOrder.verify(repository).save(responsavelRecebido);

        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldRejectNullResponsavelOnCreation()
    {
        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> service.createResponsavel(null));

        assertEquals("Responsável não pode ser nulo.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

    @Test
    void shouldRejectCreationWhenCpfIsAlreadyRegistered()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().buildForCreate();

        Mockito.when(repository.existsByCpf(responsavel.getCpf())).thenReturn(true);

        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> service.createResponsavel(responsavel));

        assertEquals("CPF já cadastrado.", exception.getMessage());

        Mockito.verify(repository).existsByCpf(responsavel.getCpf());
        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldRejectCreationWhenEmailIsAlreadyRegistered()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().buildForCreate();

        Mockito.when(repository.existsByCpf(responsavel.getCpf())).thenReturn(false);
        Mockito.when(repository.existsByEmail(responsavel.getEmail())).thenReturn(true);

        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> service.createResponsavel(responsavel));

        assertEquals("Email já cadastrado.", exception.getMessage());

        var inOrder = Mockito.inOrder(repository);

        inOrder.verify(repository).existsByCpf(responsavel.getCpf());
        inOrder.verify(repository).existsByEmail(responsavel.getEmail());
        Mockito.verifyNoMoreInteractions(repository);
    }

    // Testes do updateResponsavel

    @Test
    void shouldUpdateResponsavelSuccessfully()
    {
        Responsavel existente = ResponsavelTestBuilder.newResponsavel().setId(1L).setAtivo(false).build();
        Responsavel dadosRecebidos = ResponsavelTestBuilder.newResponsavel().setNome("Maria Atualizada").setCpf("52998224725").setEmail("maria.atualizada@email.com").setCep("01310100").setTelefone("1133334444").buildForCreate();
        Responsavel responsavelSalvo = ResponsavelTestBuilder.newResponsavel().setId(1L).setNome(dadosRecebidos.getNome()).setCpf(dadosRecebidos.getCpf()).setEmail(dadosRecebidos.getEmail()).setCep(dadosRecebidos.getCep()).setEndereco(dadosRecebidos.getEndereco()).setTelefone(dadosRecebidos.getTelefone()).setAtivo(false).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(existente));
        Mockito.when(repository.findByCpf(dadosRecebidos.getCpf())).thenReturn(Optional.empty());
        Mockito.when(repository.findByEmail(dadosRecebidos.getEmail())).thenReturn(Optional.empty());
        Mockito.when(repository.save(existente)).thenReturn(responsavelSalvo);

        Responsavel response = service.updateResponsavel(1L, dadosRecebidos);

        assertAll(
                () -> assertSame(responsavelSalvo, response),
                () -> assertEquals(1L, existente.getId()),
                () -> assertFalse(existente.isAtivo()),
                () -> assertEquals(dadosRecebidos.getNome(), existente.getNome()),
                () -> assertEquals(dadosRecebidos.getCpf(), existente.getCpf()),
                () -> assertEquals(dadosRecebidos.getEmail(), existente.getEmail()),
                () -> assertEquals(dadosRecebidos.getCep(), existente.getCep()),
                () -> assertSame(dadosRecebidos.getEndereco(), existente.getEndereco()),
                () -> assertEquals(dadosRecebidos.getTelefone(), existente.getTelefone())
        );

        var inOrder = Mockito.inOrder(repository);

        inOrder.verify(repository).findById(1L);
        inOrder.verify(repository).findByCpf(dadosRecebidos.getCpf());
        inOrder.verify(repository).findByEmail(dadosRecebidos.getEmail());
        inOrder.verify(repository).save(existente);

        Mockito.verifyNoMoreInteractions(repository);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldRejectInvalidIdOnResponsavelUpdate(Long id)
    {
        Responsavel dadosRecebidos = ResponsavelTestBuilder.newResponsavel().buildForCreate();

        ResponsavelNotFoundException exception = assertThrows(ResponsavelNotFoundException.class, () -> service.updateResponsavel(id, dadosRecebidos));

        assertEquals("Responsável não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

    @Test
    void shouldRejectNullInformationOnResponsavelUpdate()
    {
        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> service.updateResponsavel(1L, null));

        assertEquals("As informações para atualização não podem ser nulas.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

    @Test
    void shouldRejectUpdateWhenResponsavelDoesNotExist()
    {
        Responsavel dadosRecebidos = ResponsavelTestBuilder.newResponsavel().buildForCreate();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        ResponsavelNotFoundException exception = assertThrows(ResponsavelNotFoundException.class, () -> service.updateResponsavel(1L, dadosRecebidos));

        assertEquals("Responsável não encontrado.", exception.getMessage());

        Mockito.verify(repository).findById(1L);
        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldRejectUpdateWhenCpfBelongsToAnotherResponsavel()
    {
        Responsavel existente = ResponsavelTestBuilder.newResponsavel().setId(1L).build();
        Responsavel outroResponsavel = ResponsavelTestBuilder.newResponsavel().setId(2L).setCpf("52998224725").setEmail("outro@email.com").build();
        Responsavel dadosRecebidos = ResponsavelTestBuilder.newResponsavel().setNome("Nome Atualizado").setCpf("52998224725").setEmail("atualizado@email.com").buildForCreate();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(existente));
        Mockito.when(repository.findByCpf("52998224725")).thenReturn(Optional.of(outroResponsavel));

        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> service.updateResponsavel(1L, dadosRecebidos));

        assertAll(
                () -> assertEquals("CPF já cadastrado.", exception.getMessage()),
                () -> assertEquals("João da Silva", existente.getNome()),
                () -> assertEquals("16899535009", existente.getCpf()),
                () -> assertEquals("joao.silva@email.com", existente.getEmail())
        );

        var inOrder = Mockito.inOrder(repository);

        inOrder.verify(repository).findById(1L);
        inOrder.verify(repository).findByCpf("52998224725");

        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldRejectUpdateWhenEmailBelongsToAnotherResponsavel()
    {
        Responsavel existente = ResponsavelTestBuilder.newResponsavel().setId(1L).build();
        Responsavel outroResponsavel = ResponsavelTestBuilder.newResponsavel().setId(2L).setCpf("52998224725").setEmail("duplicado@email.com").build();
        Responsavel dadosRecebidos = ResponsavelTestBuilder.newResponsavel().setNome("Nome Atualizado").setEmail("duplicado@email.com").buildForCreate();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(existente));
        Mockito.when(repository.findByCpf(existente.getCpf())).thenReturn(Optional.of(existente));
        Mockito.when(repository.findByEmail("duplicado@email.com")).thenReturn(Optional.of(outroResponsavel));

        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> service.updateResponsavel(1L, dadosRecebidos));

        assertAll(
                () -> assertEquals("Email já cadastrado.", exception.getMessage()),
                () -> assertEquals("João da Silva", existente.getNome()),
                () -> assertEquals("16899535009", existente.getCpf()),
                () -> assertEquals("joao.silva@email.com", existente.getEmail())
        );

        var inOrder = Mockito.inOrder(repository);

        inOrder.verify(repository).findById(1L);
        inOrder.verify(repository).findByCpf(existente.getCpf());
        inOrder.verify(repository).findByEmail("duplicado@email.com");

        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldAllowUpdateKeepingOwnCpfAndEmail()
    {
        Responsavel existente = ResponsavelTestBuilder.newResponsavel().setId(1L).setAtivo(false).build();

        Responsavel dadosRecebidos = ResponsavelTestBuilder.newResponsavel().setNome("João Atualizado").setCpf(existente.getCpf()).setEmail(existente.getEmail()).buildForCreate();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(existente));
        Mockito.when(repository.findByCpf(existente.getCpf())).thenReturn(Optional.of(existente));
        Mockito.when(repository.findByEmail(existente.getEmail())).thenReturn(Optional.of(existente));
        Mockito.when(repository.save(existente)).thenReturn(existente);

        Responsavel response = service.updateResponsavel(1L, dadosRecebidos);

        assertAll(
                () -> assertSame(existente, response),
                () -> assertEquals("João Atualizado", response.getNome()),
                () -> assertEquals(1L, response.getId()),
                () -> assertFalse(response.isAtivo())
        );

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).findByCpf(existente.getCpf());
        Mockito.verify(repository).findByEmail(existente.getEmail());
        Mockito.verify(repository).save(existente);
    }

    // Testes do deactivateResponsavel

    @Test
    void shouldDeactivateResponsavelSuccessfully()
    {
        Responsavel existente = ResponsavelTestBuilder.newResponsavel().setId(1L).setAtivo(true).build();
        Responsavel salvo = ResponsavelTestBuilder.newResponsavel().setId(1L).setAtivo(false).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(existente));
        Mockito.when(repository.save(existente)).thenReturn(salvo);

        Responsavel response = service.deactivateResponsavel(1L);

        assertAll(
                () -> assertFalse(existente.isAtivo()),
                () -> assertSame(salvo, response)
        );

        var inOrder = Mockito.inOrder(repository);

        inOrder.verify(repository).findById(1L);
        inOrder.verify(repository).save(existente);

        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldRejectDeactivationOfInactiveResponsavel()
    {
        Responsavel existente = ResponsavelTestBuilder.newResponsavel().setId(1L).setAtivo(false).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(existente));

        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> service.deactivateResponsavel(1L));

        assertAll(
                () -> assertEquals("Responsável já está desativado.", exception.getMessage()),
                () -> assertFalse(existente.isAtivo())
        );

        Mockito.verify(repository).findById(1L);
        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldNotSaveWhenDeactivatingUnknownResponsavel()
    {
        Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());

        ResponsavelNotFoundException exception = assertThrows(ResponsavelNotFoundException.class, () -> service.deactivateResponsavel(99L));

        assertEquals("Responsável não encontrado.", exception.getMessage());

        Mockito.verify(repository).findById(99L);
        Mockito.verifyNoMoreInteractions(repository);
    }

    // Testes do reactivateResponsavel

    @Test
    void shouldReactivateResponsavelSuccessfully()
    {
        Responsavel existente = ResponsavelTestBuilder.newResponsavel().setId(1L).setAtivo(false).build();
        Responsavel salvo = ResponsavelTestBuilder.newResponsavel().setId(1L).setAtivo(true).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(existente));
        Mockito.when(repository.save(existente)).thenReturn(salvo);

        Responsavel response = service.reactivateResponsavel(1L);

        assertAll(
                () -> assertTrue(existente.isAtivo()),
                () -> assertSame(salvo, response)
        );

        var inOrder = Mockito.inOrder(repository);

        inOrder.verify(repository).findById(1L);
        inOrder.verify(repository).save(existente);

        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldRejectReactivationOfActiveResponsavel()
    {
        Responsavel existente = ResponsavelTestBuilder.newResponsavel().setId(1L).setAtivo(true).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(existente));

        ResponsavelWithInvalidInformationException exception = assertThrows(ResponsavelWithInvalidInformationException.class, () -> service.reactivateResponsavel(1L));

        assertAll(
                () -> assertEquals("Responsável já está ativado.", exception.getMessage()),
                () -> assertTrue(existente.isAtivo())
        );

        Mockito.verify(repository).findById(1L);
        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldNotSaveWhenReactivatingUnknownResponsavel()
    {
        Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());

        ResponsavelNotFoundException exception = assertThrows(ResponsavelNotFoundException.class, () -> service.reactivateResponsavel(99L));

        assertEquals("Responsável não encontrado.", exception.getMessage());

        Mockito.verify(repository).findById(99L);
        Mockito.verifyNoMoreInteractions(repository);
    }

    // Testes do deleteResponsavel

    @Test
    void shouldDeleteResponsavelSuccessfully()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setId(1L).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(responsavel));

        service.deleteResponsavel(1L);

        var inOrder = Mockito.inOrder(repository);

        inOrder.verify(repository).findById(1L);
        inOrder.verify(repository).delete(responsavel);

        Mockito.verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldNotDeleteUnknownResponsavel()
    {
        Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());

        ResponsavelNotFoundException exception = assertThrows(ResponsavelNotFoundException.class, () -> service.deleteResponsavel(99L));

        assertEquals("Responsável não encontrado.", exception.getMessage());

        Mockito.verify(repository).findById(99L);
        Mockito.verifyNoMoreInteractions(repository);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldNotDeleteResponsavelWhenIdIsInvalid(Long id)
    {
        ResponsavelNotFoundException exception = assertThrows(ResponsavelNotFoundException.class, () -> service.deleteResponsavel(id));

        assertEquals("Responsável não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

}