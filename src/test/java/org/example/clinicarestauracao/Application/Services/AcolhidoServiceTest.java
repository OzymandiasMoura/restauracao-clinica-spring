package org.example.clinicarestauracao.Application.Services;

import org.example.clinicarestauracao.Application.Exceptions.Acolhido.AcolhidoNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Acolhido.AcolhidoWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.AcolhidoRepository;
import org.example.clinicarestauracao.Application.Interfaces.InternacaoRepository;
import org.example.clinicarestauracao.Application.Interfaces.ResponsavelRepository;
import org.example.clinicarestauracao.Builders.AcolhidoTestBuilder;
import org.example.clinicarestauracao.Builders.EnderecoTestBuilder;
import org.example.clinicarestauracao.Builders.ResponsavelTestBuilder;
import org.example.clinicarestauracao.Domain.Entities.Acolhido;
import org.example.clinicarestauracao.Domain.Entities.Responsavel;
import org.example.clinicarestauracao.Domain.ValueObjects.Endereco;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AcolhidoServiceTest
{
    @Mock
    private AcolhidoRepository repository;

    @Mock
    private InternacaoRepository internacaoRepository;

    @Mock
    private ResponsavelRepository responsavelRepository;

    @InjectMocks
    private AcolhidoService service;

    // Testes findAll

    @Test
    void shouldFindAllAcolhidosSuccessfully()
    {
        Acolhido primeiroAcolhido = AcolhidoTestBuilder.newAcolhido().setId(1L).build();
        Acolhido segundoAcolhido = AcolhidoTestBuilder.newAcolhido().setId(2L).setNome("João da Silva").build();
        List<Acolhido> acolhidos = List.of(primeiroAcolhido, segundoAcolhido);

        Mockito.when(repository.findAll()).thenReturn(acolhidos);

        List<Acolhido> response = service.findAllAcolhidos();

        assertSame(acolhidos, response);
        assertEquals(2, response.size());
        assertSame(primeiroAcolhido, response.getFirst());
        assertSame(segundoAcolhido, response.get(1));

        Mockito.verify(repository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoAcolhidos()
    {
        List<Acolhido> acolhidos = List.of();

        Mockito.when(repository.findAll()).thenReturn(acolhidos);

        List<Acolhido> response = service.findAllAcolhidos();

        assertSame(acolhidos, response);
        assertTrue(response.isEmpty());

        Mockito.verify(repository).findAll();
    }

    //Testes findById

    @Test
    void shouldFindAcolhidoByIdSuccessfully()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(acolhido));

        Acolhido response = service.findAcolhidoById(1L);

        assertSame(acolhido, response);

        Mockito.verify(repository).findById(1L);
    }

    @Test
    void shouldThrowAcolhidoNotFoundExceptionWhenAcolhidoDoesNotExist()
    {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        AcolhidoNotFoundException exception = assertThrows(AcolhidoNotFoundException.class, () -> service.findAcolhidoById(1L));

        assertEquals("Acolhido não encontrado.", exception.getMessage());

        Mockito.verify(repository).findById(1L);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldThrowAcolhidoNotFoundExceptionWhenIdIsInvalid(Long id)
    {
        AcolhidoNotFoundException exception = assertThrows(AcolhidoNotFoundException.class, () -> service.findAcolhidoById(id));

        assertEquals("Acolhido não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

    // Testes findByNome

    @Test
    void shouldFindAcolhidosByPartialNameSuccessfully()
    {
        Acolhido primeiroAcolhido = AcolhidoTestBuilder.newAcolhido().setNome("Maria Silva").build();
        Acolhido segundoAcolhido = AcolhidoTestBuilder.newAcolhido().setId(2L).setNome("Maria Souza").build();
        List<Acolhido> acolhidos = List.of(primeiroAcolhido, segundoAcolhido);

        Mockito.when(repository.findAcolhidoByNomeContainingIgnoreCase("maria")).thenReturn(acolhidos);

        List<Acolhido> response = service.findAcolhidoByNome("  maria  ");

        assertSame(acolhidos, response);
        assertEquals(2, response.size());

        Mockito.verify(repository).findAcolhidoByNomeContainingIgnoreCase("maria");
    }

    @Test
    void shouldThrowAcolhidoNotFoundExceptionWhenNoAcolhidoMatchesName()
    {
        Mockito.when(repository.findAcolhidoByNomeContainingIgnoreCase("Maria")).thenReturn(List.of());

        AcolhidoNotFoundException exception = assertThrows(AcolhidoNotFoundException.class, () -> service.findAcolhidoByNome("Maria"));

        assertEquals("Acolhido não encontrado.", exception.getMessage());

        Mockito.verify(repository).findAcolhidoByNomeContainingIgnoreCase("Maria");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t"})
    void shouldThrowAcolhidoNotFoundExceptionWhenNameIsInvalid(String nome)
    {
        AcolhidoNotFoundException exception = assertThrows(AcolhidoNotFoundException.class, () -> service.findAcolhidoByNome(nome));

        assertEquals("Acolhido não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

    // Testes findByEmail

    @Test
    void shouldFindAcolhidoByEmailSuccessfully()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        Mockito.when(repository.findAcolhidoByEmail("maria@email.com")).thenReturn(Optional.of(acolhido));

        Acolhido response = service.findAcolhidoByEmail("  MARIA@EMAIL.COM  ");

        assertSame(acolhido, response);

        Mockito.verify(repository).findAcolhidoByEmail("maria@email.com");
    }

    @Test
    void shouldThrowAcolhidoNotFoundExceptionWhenEmailDoesNotExist()
    {
        Mockito.when(repository.findAcolhidoByEmail("inexistente@email.com")).thenReturn(Optional.empty());

        AcolhidoNotFoundException exception = assertThrows(AcolhidoNotFoundException.class, () -> service.findAcolhidoByEmail("  INEXISTENTE@EMAIL.COM  "));

        assertEquals("Acolhido não encontrado.", exception.getMessage());

        Mockito.verify(repository).findAcolhidoByEmail("inexistente@email.com");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   ", "\t"})
    void shouldThrowAcolhidoNotFoundExceptionWhenEmailIsInvalid(String email)
    {
        AcolhidoNotFoundException exception = assertThrows(AcolhidoNotFoundException.class, () -> service.findAcolhidoByEmail(email));

        assertEquals("Acolhido não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

    // Testes findByCpf

    @Test
    void shouldFindAcolhidoByCpfSuccessfully()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        Mockito.when(repository.findAcolhidoByCpf("52998224725")).thenReturn(Optional.of(acolhido));

        Acolhido response = service.findAcolhidoByCpf("  529.982.247-25  ");

        assertSame(acolhido, response);

        Mockito.verify(repository).findAcolhidoByCpf("52998224725");
    }

    @Test
    void shouldThrowAcolhidoNotFoundExceptionWhenCpfDoesNotExist()
    {
        Mockito.when(repository.findAcolhidoByCpf("11144477735")).thenReturn(Optional.empty());

        AcolhidoNotFoundException exception = assertThrows(AcolhidoNotFoundException.class, () -> service.findAcolhidoByCpf("111.444.777-35"));

        assertEquals("Acolhido não encontrado.", exception.getMessage());

        Mockito.verify(repository).findAcolhidoByCpf("11144477735");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "\t", "...", "---", ". -"})
    void shouldThrowAcolhidoNotFoundExceptionWhenCpfIsInvalid(String cpf)
    {
        AcolhidoNotFoundException exception = assertThrows(AcolhidoNotFoundException.class, () -> service.findAcolhidoByCpf(cpf));

        assertEquals("Acolhido não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

    // Testes createAcolhido

    @Test
    void shouldCreateAcolhidoSuccessfully()
    {
        Acolhido receivedAcolhido = AcolhidoTestBuilder.newAcolhido().buildForCreate();
        Acolhido savedAcolhido = AcolhidoTestBuilder.newAcolhido().build();

        Mockito.when(repository.findAcolhidoByCpf(receivedAcolhido.getCpf())).thenReturn(Optional.empty());
        Mockito.when(repository.findAcolhidoByEmail(receivedAcolhido.getEmail())).thenReturn(Optional.empty());
        Mockito.when(repository.save(receivedAcolhido)).thenReturn(savedAcolhido);

        Acolhido response = service.createAcolhido(receivedAcolhido);

        assertSame(savedAcolhido, response);

        Mockito.verify(repository).findAcolhidoByCpf(receivedAcolhido.getCpf());
        Mockito.verify(repository).findAcolhidoByEmail(receivedAcolhido.getEmail());
        Mockito.verify(repository).save(receivedAcolhido);
    }

    @Test
    void shouldRejectDuplicatedCpfWhenCreatingAcolhido()
    {
        Acolhido receivedAcolhido = AcolhidoTestBuilder.newAcolhido().buildForCreate();
        Acolhido existingAcolhido = AcolhidoTestBuilder.newAcolhido().build();

        Mockito.when(repository.findAcolhidoByCpf(receivedAcolhido.getCpf())).thenReturn(Optional.of(existingAcolhido));

        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> service.createAcolhido(receivedAcolhido));

        assertEquals("CPF já cadastrado.", exception.getMessage());

        Mockito.verify(repository).findAcolhidoByCpf(receivedAcolhido.getCpf());
        Mockito.verify(repository, Mockito.never()).findAcolhidoByEmail(Mockito.anyString());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Acolhido.class));
    }

    @Test
    void shouldRejectDuplicatedEmailWhenCreatingAcolhido()
    {
        Acolhido receivedAcolhido = AcolhidoTestBuilder.newAcolhido().buildForCreate();
        Acolhido existingAcolhido = AcolhidoTestBuilder.newAcolhido().build();

        Mockito.when(repository.findAcolhidoByCpf(receivedAcolhido.getCpf())).thenReturn(Optional.empty());
        Mockito.when(repository.findAcolhidoByEmail(receivedAcolhido.getEmail())).thenReturn(Optional.of(existingAcolhido));

        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> service.createAcolhido(receivedAcolhido));

        assertEquals("Email já cadastrado.", exception.getMessage());

        Mockito.verify(repository).findAcolhidoByCpf(receivedAcolhido.getCpf());
        Mockito.verify(repository).findAcolhidoByEmail(receivedAcolhido.getEmail());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Acolhido.class));
    }

    // Testes updateAcolhido

    @Test
    void shouldUpdateAcolhidoSuccessfully()
    {
        Responsavel responsavelAtual = ResponsavelTestBuilder.newResponsavel().setId(1L).build();
        Responsavel responsavelRecebido = ResponsavelTestBuilder.newResponsavel().setId(2L).build();
        Acolhido existingAcolhido = AcolhidoTestBuilder.newAcolhido().setResponsavel(responsavelAtual).build();
        Endereco updatedEndereco = EnderecoTestBuilder.newEndereco().setLogradouro("Avenida Paulista").setNumero(1000).setBairro("Bela Vista").setCidade("São Paulo").setEstado("SP").setComplemento("Sala 10").build();
        Acolhido receivedAcolhido = AcolhidoTestBuilder.newAcolhido().setNome("Maria Atualizada").setEmail("maria.atualizada@email.com").setCpf("11144477735").setCep("01310100").setEndereco(updatedEndereco).setTelefone("1133334444").setDataNascimento(LocalDate.of(1990, 1, 10)).setResponsavel(responsavelRecebido).buildForCreate();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(existingAcolhido));
        Mockito.when(repository.findAcolhidoByCpf(receivedAcolhido.getCpf())).thenReturn(Optional.empty());
        Mockito.when(repository.findAcolhidoByEmail(receivedAcolhido.getEmail())).thenReturn(Optional.empty());
        Mockito.when(repository.save(existingAcolhido)).thenReturn(existingAcolhido);

        Acolhido response = service.updateAcolhido(1L, receivedAcolhido);

        assertSame(existingAcolhido, response);
        assertEquals(1L, response.getId());
        assertEquals("Maria Atualizada", response.getNome());
        assertEquals("maria.atualizada@email.com", response.getEmail());
        assertEquals("11144477735", response.getCpf());
        assertEquals("01310100", response.getCep());
        assertSame(updatedEndereco, response.getEndereco());
        assertEquals("1133334444", response.getTelefone());
        assertEquals(LocalDate.of(1990, 1, 10), response.getDataNascimento());
        assertSame(responsavelRecebido, response.getResponsavel());
        assertTrue(response.getAtivo());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).findAcolhidoByCpf(receivedAcolhido.getCpf());
        Mockito.verify(repository).findAcolhidoByEmail(receivedAcolhido.getEmail());
        Mockito.verify(repository).save(existingAcolhido);
    }

    @Test
    void shouldReplaceResponsavelWhenUpdatingAcolhido()
    {
        Responsavel responsavelAtual = ResponsavelTestBuilder.newResponsavel().setId(1L).build();
        Responsavel novoResponsavel = ResponsavelTestBuilder.newResponsavel().setId(2L).build();
        Acolhido existingAcolhido = AcolhidoTestBuilder.newAcolhido().setResponsavel(responsavelAtual).build();
        Acolhido receivedAcolhido = AcolhidoTestBuilder.newAcolhido().setResponsavel(novoResponsavel).buildForCreate();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(existingAcolhido));
        Mockito.when(repository.findAcolhidoByCpf(receivedAcolhido.getCpf())).thenReturn(Optional.of(existingAcolhido));
        Mockito.when(repository.findAcolhidoByEmail(receivedAcolhido.getEmail())).thenReturn(Optional.of(existingAcolhido));
        Mockito.when(repository.save(existingAcolhido)).thenReturn(existingAcolhido);

        Acolhido response = service.updateAcolhido(1L, receivedAcolhido);

        assertAll(
                () -> assertSame(existingAcolhido, response),
                () -> assertSame(novoResponsavel, response.getResponsavel())
        );

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).findAcolhidoByCpf(receivedAcolhido.getCpf());
        Mockito.verify(repository).findAcolhidoByEmail(receivedAcolhido.getEmail());
        Mockito.verify(repository).save(existingAcolhido);
    }

    @Test
    void shouldAllowKeepingOwnCpfAndEmailAndPreserveInactiveState()
    {
        Acolhido existingAcolhido = AcolhidoTestBuilder.newAcolhido().build();
        existingAcolhido.deactivate();
        Acolhido receivedAcolhido = AcolhidoTestBuilder.newAcolhido().setNome("Maria Atualizada").buildForCreate();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(existingAcolhido));
        Mockito.when(repository.findAcolhidoByCpf(receivedAcolhido.getCpf())).thenReturn(Optional.of(existingAcolhido));
        Mockito.when(repository.findAcolhidoByEmail(receivedAcolhido.getEmail())).thenReturn(Optional.of(existingAcolhido));
        Mockito.when(repository.save(existingAcolhido)).thenReturn(existingAcolhido);

        Acolhido response = service.updateAcolhido(1L, receivedAcolhido);

        assertSame(existingAcolhido, response);
        assertEquals("Maria Atualizada", response.getNome());
        assertEquals(1L, response.getId());
        assertFalse(response.getAtivo());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).findAcolhidoByCpf(receivedAcolhido.getCpf());
        Mockito.verify(repository).findAcolhidoByEmail(receivedAcolhido.getEmail());
        Mockito.verify(repository).save(existingAcolhido);
    }

    @Test
    void shouldRejectCpfBelongingToAnotherAcolhidoWhenUpdating()
    {
        Acolhido existingAcolhido = AcolhidoTestBuilder.newAcolhido().build();
        Acolhido anotherAcolhido = AcolhidoTestBuilder.newAcolhido().setId(2L).setCpf("11144477735").build();
        Acolhido receivedAcolhido = AcolhidoTestBuilder.newAcolhido().setNome("Maria Atualizada").setCpf("11144477735").buildForCreate();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(existingAcolhido));
        Mockito.when(repository.findAcolhidoByCpf(receivedAcolhido.getCpf())).thenReturn(Optional.of(anotherAcolhido));

        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> service.updateAcolhido(1L, receivedAcolhido));

        assertEquals("CPF já cadastrado.", exception.getMessage());
        assertEquals("Maria Silva", existingAcolhido.getNome());
        assertEquals("52998224725", existingAcolhido.getCpf());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).findAcolhidoByCpf(receivedAcolhido.getCpf());
        Mockito.verify(repository, Mockito.never()).findAcolhidoByEmail(Mockito.anyString());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Acolhido.class));
    }

    @Test
    void shouldRejectEmailBelongingToAnotherAcolhidoWhenUpdating()
    {
        Acolhido existingAcolhido = AcolhidoTestBuilder.newAcolhido().build();
        Acolhido anotherAcolhido = AcolhidoTestBuilder.newAcolhido().setId(2L).setEmail("duplicado@email.com").build();
        Acolhido receivedAcolhido = AcolhidoTestBuilder.newAcolhido().setNome("Maria Atualizada").setCpf("11144477735").setEmail("duplicado@email.com").buildForCreate();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(existingAcolhido));
        Mockito.when(repository.findAcolhidoByCpf(receivedAcolhido.getCpf())).thenReturn(Optional.empty());
        Mockito.when(repository.findAcolhidoByEmail(receivedAcolhido.getEmail())).thenReturn(Optional.of(anotherAcolhido));

        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> service.updateAcolhido(1L, receivedAcolhido));

        assertEquals("Email já cadastrado.", exception.getMessage());
        assertEquals("Maria Silva", existingAcolhido.getNome());
        assertEquals("maria@email.com", existingAcolhido.getEmail());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).findAcolhidoByCpf(receivedAcolhido.getCpf());
        Mockito.verify(repository).findAcolhidoByEmail(receivedAcolhido.getEmail());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Acolhido.class));
    }

    @Test
    void shouldThrowAcolhidoNotFoundExceptionWhenUpdatingUnknownId()
    {
        Acolhido receivedAcolhido = AcolhidoTestBuilder.newAcolhido().buildForCreate();

        Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());

        AcolhidoNotFoundException exception = assertThrows(AcolhidoNotFoundException.class, () -> service.updateAcolhido(99L, receivedAcolhido));

        assertEquals("Acolhido não encontrado.", exception.getMessage());

        Mockito.verify(repository).findById(99L);
        Mockito.verify(repository, Mockito.never()).findAcolhidoByCpf(Mockito.anyString());
        Mockito.verify(repository, Mockito.never()).findAcolhidoByEmail(Mockito.anyString());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Acolhido.class));
    }

    // Testes deactivateAcolhido

    @Test
    void shouldDeactivateAcolhidoAndResponsavelWhenThereIsNoOtherActiveAcolhido()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();
        Responsavel responsavel = acolhido.getResponsavel();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(acolhido));
        Mockito.when(internacaoRepository.existsByAcolhidoAndAtivoTrue(Mockito.same(acolhido))).thenReturn(false);
        Mockito.when(repository.existsByResponsavelIdAndAtivoTrueAndIdNot(responsavel.getId(), acolhido.getId())).thenReturn(false);

        service.deactivateAcolhido(1L);

        assertAll(
                () -> assertFalse(acolhido.getAtivo()),
                () -> assertFalse(responsavel.isAtivo())
        );

        Mockito.verify(repository).findById(1L);
        Mockito.verify(internacaoRepository).existsByAcolhidoAndAtivoTrue(Mockito.same(acolhido));
        Mockito.verify(repository).existsByResponsavelIdAndAtivoTrueAndIdNot(responsavel.getId(), acolhido.getId());
        Mockito.verify(responsavelRepository).save(Mockito.same(responsavel));
        Mockito.verify(repository).save(Mockito.same(acolhido));
    }

    @Test
    void shouldDeactivateOnlyAcolhidoWhenResponsavelHasAnotherActiveAcolhido()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();
        Responsavel responsavel = acolhido.getResponsavel();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(acolhido));
        Mockito.when(internacaoRepository.existsByAcolhidoAndAtivoTrue(Mockito.same(acolhido))).thenReturn(false);
        Mockito.when(repository.existsByResponsavelIdAndAtivoTrueAndIdNot(responsavel.getId(), acolhido.getId())).thenReturn(true);

        service.deactivateAcolhido(1L);

        assertAll(
                () -> assertFalse(acolhido.getAtivo()),
                () -> assertTrue(responsavel.isAtivo())
        );

        Mockito.verify(repository).existsByResponsavelIdAndAtivoTrueAndIdNot(responsavel.getId(), acolhido.getId());
        Mockito.verifyNoInteractions(responsavelRepository);
        Mockito.verify(repository).save(Mockito.same(acolhido));
    }

    @Test
    void shouldNotDeactivateAlreadyInactiveResponsavelAgain()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();
        Responsavel responsavel = acolhido.getResponsavel();
        responsavel.deactivateResponsavel();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(acolhido));
        Mockito.when(internacaoRepository.existsByAcolhidoAndAtivoTrue(Mockito.same(acolhido))).thenReturn(false);
        Mockito.when(repository.existsByResponsavelIdAndAtivoTrueAndIdNot(responsavel.getId(), acolhido.getId())).thenReturn(false);

        service.deactivateAcolhido(1L);

        assertAll(
                () -> assertFalse(acolhido.getAtivo()),
                () -> assertFalse(responsavel.isAtivo())
        );

        Mockito.verifyNoInteractions(responsavelRepository);
        Mockito.verify(repository).save(Mockito.same(acolhido));
    }

    @Test
    void shouldRejectDeactivationWhenAcolhidoHasActiveInternacao()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(acolhido));
        Mockito.when(internacaoRepository.existsByAcolhidoAndAtivoTrue(Mockito.same(acolhido))).thenReturn(true);

        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> service.deactivateAcolhido(1L));

        assertAll(
                () -> assertEquals("Acolhido não pode ser desativado enquanto possuir internação ativa.", exception.getMessage()),
                () -> assertTrue(acolhido.getAtivo())
        );

        Mockito.verify(repository).findById(1L);
        Mockito.verify(internacaoRepository).existsByAcolhidoAndAtivoTrue(Mockito.same(acolhido));
        Mockito.verify(repository, Mockito.never()).existsByResponsavelIdAndAtivoTrueAndIdNot(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verifyNoInteractions(responsavelRepository);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Acolhido.class));
    }

    @Test
    void shouldNotCheckInternacoesOrSaveWhenAcolhidoIsAlreadyInactive()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();
        acolhido.deactivate();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(acolhido));

        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> service.deactivateAcolhido(1L));

        assertAll(
                () -> assertEquals("Acolhido já desativado.", exception.getMessage()),
                () -> assertFalse(acolhido.getAtivo())
        );

        Mockito.verify(repository).findById(1L);
        Mockito.verifyNoInteractions(internacaoRepository);
        Mockito.verify(repository, Mockito.never()).existsByResponsavelIdAndAtivoTrueAndIdNot(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verifyNoInteractions(responsavelRepository);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Acolhido.class));
    }


    @Test
    void shouldNotSaveWhenDeactivatingUnknownAcolhido()
    {
        Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());

        AcolhidoNotFoundException exception = assertThrows(AcolhidoNotFoundException.class, () -> service.deactivateAcolhido(99L));

        assertEquals("Acolhido não encontrado.", exception.getMessage());

        Mockito.verify(repository).findById(99L);
        Mockito.verifyNoInteractions(internacaoRepository, responsavelRepository);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Acolhido.class));
    }

    // Testes reactivateAcolhido

    @Test
    void shouldReactivateAcolhidoAndInactiveResponsavel()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();
        Responsavel responsavel = acolhido.getResponsavel();
        acolhido.deactivate();
        responsavel.deactivateResponsavel();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(acolhido));

        service.reactivateAcolhido(1L);

        assertAll(
                () -> assertTrue(acolhido.getAtivo()),
                () -> assertTrue(responsavel.isAtivo())
        );

        Mockito.verify(repository).findById(1L);
        Mockito.verify(responsavelRepository).save(Mockito.same(responsavel));
        Mockito.verify(repository).save(Mockito.same(acolhido));
    }

    @Test
    void shouldReactivateOnlyAcolhidoWhenResponsavelIsAlreadyActive()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();
        Responsavel responsavel = acolhido.getResponsavel();
        acolhido.deactivate();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(acolhido));

        service.reactivateAcolhido(1L);

        assertAll(
                () -> assertTrue(acolhido.getAtivo()),
                () -> assertTrue(responsavel.isAtivo())
        );

        Mockito.verify(repository).findById(1L);
        Mockito.verifyNoInteractions(responsavelRepository);
        Mockito.verify(repository).save(acolhido);
    }

    @Test
    void shouldNotSaveWhenReactivatingAlreadyActiveAcolhido()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(acolhido));

        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> service.reactivateAcolhido(1L));

        assertEquals("Acolhido já ativado.", exception.getMessage());
        assertTrue(acolhido.getAtivo());

        Mockito.verify(repository).findById(1L);
        Mockito.verifyNoInteractions(responsavelRepository);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Acolhido.class));
    }

    @Test
    void shouldNotSaveWhenReactivatingUnknownAcolhido()
    {
        Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());

        AcolhidoNotFoundException exception = assertThrows(AcolhidoNotFoundException.class, () -> service.reactivateAcolhido(99L));

        assertEquals("Acolhido não encontrado.", exception.getMessage());

        Mockito.verify(repository).findById(99L);
        Mockito.verifyNoInteractions(responsavelRepository);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Acolhido.class));
    }

    // Testes deleteAcolhidoById

    @Test
    void shouldDeleteAcolhidoAndDeactivateResponsavelWhenThereAreNoOtherActiveAcolhidos()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setId(1L).build();
        Responsavel responsavel = acolhido.getResponsavel();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(acolhido));
        Mockito.when(internacaoRepository.existsByAcolhido(acolhido)).thenReturn(false);
        Mockito.when(repository.existsByResponsavelIdAndAtivoTrueAndIdNot(responsavel.getId(), acolhido.getId())).thenReturn(false);

        service.deleteAcolhidoById(1L);

        assertFalse(responsavel.isAtivo());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(internacaoRepository).existsByAcolhido(Mockito.same(acolhido));
        Mockito.verify(repository).delete(Mockito.same(acolhido));
        Mockito.verify(repository).existsByResponsavelIdAndAtivoTrueAndIdNot(responsavel.getId(), acolhido.getId());
        Mockito.verify(responsavelRepository).save(Mockito.same(responsavel));
    }

    @Test
    void shouldDeleteAcolhidoWithoutDeactivatingResponsavelWhenThereIsAnotherActiveAcolhido()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setId(1L).build();
        Responsavel responsavel = acolhido.getResponsavel();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(acolhido));
        Mockito.when(internacaoRepository.existsByAcolhido(acolhido)).thenReturn(false);
        Mockito.when(repository.existsByResponsavelIdAndAtivoTrueAndIdNot(responsavel.getId(), acolhido.getId())).thenReturn(true);

        service.deleteAcolhidoById(1L);

        assertTrue(responsavel.isAtivo());

        Mockito.verify(repository).delete(Mockito.same(acolhido));
        Mockito.verify(repository).existsByResponsavelIdAndAtivoTrueAndIdNot(responsavel.getId(), acolhido.getId());
        Mockito.verifyNoInteractions(responsavelRepository);
    }

    @Test
    void shouldNotDeleteUnknownAcolhido()
    {
        Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());

        AcolhidoNotFoundException exception = assertThrows(AcolhidoNotFoundException.class, () -> service.deleteAcolhidoById(99L));

        assertEquals("Acolhido não encontrado.", exception.getMessage());

        Mockito.verify(repository).findById(99L);
        Mockito.verify(repository, Mockito.never()).delete(Mockito.any(Acolhido.class));
        Mockito.verifyNoInteractions(internacaoRepository, responsavelRepository);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldNotDeleteAcolhidoWhenIdIsInvalid(Long id)
    {
        AcolhidoNotFoundException exception = assertThrows(AcolhidoNotFoundException.class, () -> service.deleteAcolhidoById(id));

        assertEquals("Acolhido não encontrado.", exception.getMessage());

        Mockito.verifyNoInteractions(repository, internacaoRepository, responsavelRepository);
    }

    @Test
    void shouldNotDeleteAcolhidoWhenThereAreInternacoes()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setId(1L).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(acolhido));
        Mockito.when(internacaoRepository.existsByAcolhido(acolhido)).thenReturn(true);

        AcolhidoWithInvalidInformationException exception = assertThrows(AcolhidoWithInvalidInformationException.class, () -> service.deleteAcolhidoById(1L));

        assertEquals("Acolhido não pode ser excluído enquanto possuir internações.", exception.getMessage());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(internacaoRepository).existsByAcolhido(Mockito.same(acolhido));
        Mockito.verify(repository, Mockito.never()).existsByResponsavelIdAndAtivoTrueAndIdNot(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verifyNoInteractions(responsavelRepository);
        Mockito.verify(repository, Mockito.never()).delete(Mockito.any(Acolhido.class));
    }

}
