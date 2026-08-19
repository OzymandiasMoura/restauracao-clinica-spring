package org.example.clinicarestauracao.Application.Services;

import org.example.clinicarestauracao.Application.Exceptions.ModalidadeNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.ModalidadeWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.ModalidadeRepository;
import org.example.clinicarestauracao.Builders.ModalidadeTestBuilder;
import org.example.clinicarestauracao.Domain.Entities.Modalidade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ModalidadeServiceTest
{
    private ModalidadeTestBuilder builder = new ModalidadeTestBuilder();
    @Mock
    private ModalidadeRepository repository;
    @InjectMocks
    private ModalidadeService service;

    @Test
    void shouldCreateModalidadeSuccessfully()
    {
        Modalidade salva = builder.build();
        Modalidade entrada = builder.buildForCreate();

        Mockito.when(repository.save(Mockito.any(Modalidade.class))).thenReturn(salva);
        Mockito.when(repository.findModalidadeByCnpj(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(repository.findModalidadeByDescricao(Mockito.anyString())).thenReturn(Optional.empty());


        Modalidade response = service.createModalidade(entrada);

        assertNotNull(response);
        assertEquals(salva.getId(), response.getId());
        assertEquals(salva.getDescricao(), response.getDescricao());
        assertEquals(salva.getCnpj(), response.getCnpj());
        assertEquals(salva.getMaxVagas(), response.getMaxVagas());
        assertEquals(salva.isAtivo(), response.isAtivo());
        assertEquals(salva.isPagamento(), response.isPagamento());

        Mockito.verify(repository).save(entrada);
        Mockito.verify(repository).findModalidadeByCnpj(entrada.getCnpj());
        Mockito.verify(repository).findModalidadeByDescricao(entrada.getDescricao());
    }

    @Test
    void shouldThrowExceptionWhenCNPJExists()
    {
        Modalidade retornoCnpj = builder.build();
        Modalidade entrada = builder.buildForCreate();

        Mockito.when(repository.findModalidadeByCnpj(Mockito.anyString())).thenReturn(Optional.of(retornoCnpj));

        ModalidadeWithInvalidInformationException e = assertThrows(ModalidadeWithInvalidInformationException.class, () -> service.createModalidade(entrada));

        assertEquals("CNPJ já cadastrado.", e.getMessage());

        Mockito.verify(repository).findModalidadeByCnpj(entrada.getCnpj());
        Mockito.verify(repository, Mockito.never()).findModalidadeByDescricao(Mockito.anyString());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Modalidade.class));
    }

    @Test
    void shouldThrowExceptionWhenDescricaoExists()
    {
        Modalidade retornoDescricao = builder.build();
        Modalidade entrada = builder.buildForCreate();

        Mockito.when(repository.findModalidadeByCnpj(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(repository.findModalidadeByDescricao(Mockito.anyString())).thenReturn(Optional.of(retornoDescricao));

        ModalidadeWithInvalidInformationException e = assertThrows(ModalidadeWithInvalidInformationException.class, () -> service.createModalidade(entrada));

        assertEquals("Descrição já cadastrada.", e.getMessage());

        Mockito.verify(repository).findModalidadeByCnpj(entrada.getCnpj());
        Mockito.verify(repository).findModalidadeByDescricao(entrada.getDescricao());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Modalidade.class));
    }

    @Test
    void shouldCreateModalidadeWithoutCnpjSuccessfully()
    {
        Modalidade entrada = builder.setCNPJ(null).buildForCreate();
        Modalidade salva = builder.build();

        Mockito.when(repository.save(Mockito.any(Modalidade.class))).thenReturn(salva);
        Mockito.when(repository.findModalidadeByDescricao(Mockito.anyString())).thenReturn(Optional.empty());

        Modalidade response = service.createModalidade(entrada);

        assertNotNull(response);
        assertNull(response.getCnpj());
        assertEquals(salva.getDescricao(), response.getDescricao());

        Mockito.verify(repository).save(entrada);
        Mockito.verify(repository, Mockito.never()).findModalidadeByCnpj(Mockito.any());
        Mockito.verify(repository).findModalidadeByDescricao(entrada.getDescricao());
    }

    @Test
    void shouldFindModalidadeByIdSuccessfully()
    {
        Modalidade entrada = builder.build();

        Mockito.when(repository.findById(entrada.getId())).thenReturn(Optional.of(entrada));

        Modalidade response = service.findModalidadeById(entrada.getId());

        assertNotNull(response);
        assertEquals(entrada.getId(), response.getId());
        assertSame(entrada, response);

        Mockito.verify(repository).findById(entrada.getId());
    }

    @Test
    void shouldThrowExceptionWhenModalidadeIsNotFoundById()
    {
        Modalidade entrada = builder.build();

        Mockito.when(repository.findById(entrada.getId())).thenReturn(Optional.empty());

        ModalidadeNotFoundException e = assertThrows(ModalidadeNotFoundException.class, () -> service.findModalidadeById(entrada.getId()));

        assertEquals("Modalidade não encontrada.", e.getMessage());

        Mockito.verify(repository).findById(entrada.getId());
    }

    @Test
    void shouldFindAllModalidadesSuccessfully()
    {
        Modalidade entrada = builder.build();
        Modalidade entrada2 = builder.setId(2L).setDescricao("descricao2").setCNPJ(null).build();

        List<Modalidade> entradas = List.of(entrada, entrada2);

        Mockito.when(repository.findAll()).thenReturn(entradas);

        List<Modalidade> response = service.findAllModalidades();

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(entradas, response);
        assertSame(entradas, response);
        assertSame(entrada, response.get(0));
        assertSame(entrada2, response.get(1));
        Mockito.verify(repository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoModalidadesExist()
    {
        Mockito.when(repository.findAll()).thenReturn(List.of());

        List<Modalidade> response = service.findAllModalidades();

        assertNotNull(response);
        assertTrue(response.isEmpty());

        Mockito.verify(repository).findAll();
    }

    @Test
    void shouldUpdateModalidadeSuccessfully()
    {
        Modalidade existente = new ModalidadeTestBuilder().build();
        Modalidade dadosAtualizados = new ModalidadeTestBuilder().setDescricao("Nova Descrição").setCNPJ("12.ABC.345/01DE-35").setMaxVagas(30).setPagamento(false).build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));
        Mockito.when(repository.findModalidadeByDescricao(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(repository.findModalidadeByCnpj(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(repository.save(Mockito.same(existente))).thenReturn(existente);

        Modalidade response = service.updateModalidade(existente.getId(), dadosAtualizados);

        assertNotNull(response);
        assertSame(existente, response);
        assertEquals("Nova Descrição", response.getDescricao());
        assertEquals("12ABC34501DE35", response.getCnpj());
        assertEquals(30, response.getMaxVagas());
        assertFalse(response.isPagamento());
        assertTrue(response.isAtivo());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository).findModalidadeByDescricao(dadosAtualizados.getDescricao());
        Mockito.verify(repository).findModalidadeByCnpj(dadosAtualizados.getCnpj());
        Mockito.verify(repository).save(Mockito.same(existente));
    }

    @Test
    void shouldThrowExceptionWhenIdIsInvalid()
    {
        Modalidade existente = new ModalidadeTestBuilder().build();
        Modalidade dadosAtualizados = new ModalidadeTestBuilder().setDescricao("Nova Descrição").setCNPJ("12.ABC.345/01DE-35").build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.empty());

        ModalidadeNotFoundException response = assertThrows(ModalidadeNotFoundException.class, () -> service.updateModalidade(existente.getId(), dadosAtualizados));

        assertEquals("Modalidade não encontrada.", response.getMessage());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository, Mockito.never()).findModalidadeByDescricao(Mockito.anyString());
        Mockito.verify(repository, Mockito.never()).findModalidadeByCnpj(Mockito.any());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Modalidade.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatedDescricaoAlreadyExists()
    {
        Modalidade existente = new ModalidadeTestBuilder().build();
        Modalidade dadosAtualizados = new ModalidadeTestBuilder().setId(2L).setDescricao("Nova Descrição").setCNPJ("12.ABC.345/01DE-35").build();
        Modalidade duplicado = new ModalidadeTestBuilder().setId(3L).setDescricao("Nova Descrição").build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));
        Mockito.when(repository.findModalidadeByDescricao(dadosAtualizados.getDescricao())).thenReturn(Optional.of(duplicado));

        ModalidadeWithInvalidInformationException e = assertThrows(ModalidadeWithInvalidInformationException.class, () -> service.updateModalidade(existente.getId(), dadosAtualizados));

        assertEquals("Descrição já cadastrada.", e.getMessage());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository).findModalidadeByDescricao(dadosAtualizados.getDescricao());
        Mockito.verify(repository, Mockito.never()).findModalidadeByCnpj(Mockito.anyString());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Modalidade.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatedCnpjAlreadyExists()
    {
        Modalidade existente = new ModalidadeTestBuilder().build();
        Modalidade dadosAtualizados = new ModalidadeTestBuilder().setId(2L).setDescricao("Nova Descrição").setCNPJ("12.ABC.345/01DE-35").build();
        Modalidade duplicado = new ModalidadeTestBuilder().setId(3L).setCNPJ("12.ABC.345/01DE-35").build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));
        Mockito.when(repository.findModalidadeByDescricao(dadosAtualizados.getDescricao())).thenReturn(Optional.empty());
        Mockito.when(repository.findModalidadeByCnpj(dadosAtualizados.getCnpj())).thenReturn(Optional.of(duplicado));

        ModalidadeWithInvalidInformationException e = assertThrows(ModalidadeWithInvalidInformationException.class, () -> service.updateModalidade(existente.getId(), dadosAtualizados));

        assertEquals("CNPJ já cadastrado.", e.getMessage());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository).findModalidadeByDescricao(dadosAtualizados.getDescricao());
        Mockito.verify(repository).findModalidadeByCnpj(dadosAtualizados.getCnpj());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Modalidade.class));
    }

    @Test
    void shouldUpdateMaxVagasAndPagamentoWithoutCheckingDuplicates()
    {
        Modalidade existente = new ModalidadeTestBuilder().build();
        Modalidade dadosAtualizados = new ModalidadeTestBuilder().setMaxVagas(30).setPagamento(false).build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));
        Mockito.when(repository.save(Mockito.same(existente))).thenReturn(existente);

        Modalidade response = service.updateModalidade(existente.getId(), dadosAtualizados);

        assertSame(existente, response);
        assertEquals(30, response.getMaxVagas());
        assertFalse(response.isPagamento());
        assertEquals(dadosAtualizados.getDescricao(), response.getDescricao());
        assertEquals(dadosAtualizados.getCnpj(), response.getCnpj());
        assertTrue(response.isAtivo());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository, Mockito.never()).findModalidadeByDescricao(Mockito.anyString());
        Mockito.verify(repository, Mockito.never()).findModalidadeByCnpj(Mockito.any());
        Mockito.verify(repository).save(Mockito.same(existente));
    }

    @Test
    void shouldRemoveCnpjSuccessfully()
    {
        Modalidade existente = new ModalidadeTestBuilder().build();
        Modalidade dadosAtualizados = new ModalidadeTestBuilder().setCNPJ(null).build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));
        Mockito.when(repository.save(Mockito.same(existente))).thenReturn(existente);

        Modalidade response = service.updateModalidade(existente.getId(), dadosAtualizados);

        assertSame(existente, response);
        assertNull(response.getCnpj());
        assertEquals(dadosAtualizados.getDescricao(), response.getDescricao());
        assertEquals(dadosAtualizados.getMaxVagas(), response.getMaxVagas());
        assertEquals(dadosAtualizados.isPagamento(), response.isPagamento());
        assertTrue(response.isAtivo());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository, Mockito.never()).findModalidadeByDescricao(Mockito.anyString());
        Mockito.verify(repository, Mockito.never()).findModalidadeByCnpj(Mockito.any());
        Mockito.verify(repository).save(Mockito.same(existente));
    }

    @Test
    void shouldAddCnpjSuccessfully()
    {
        Modalidade existente = new ModalidadeTestBuilder().setCNPJ(null).build();
        Modalidade dadosAtualizados = new ModalidadeTestBuilder().setCNPJ("12.ABC.345/01DE-35").build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));
        Mockito.when(repository.save(Mockito.same(existente))).thenReturn(existente);
        Mockito.when(repository.findModalidadeByCnpj(dadosAtualizados.getCnpj())).thenReturn(Optional.empty());

        Modalidade response = service.updateModalidade(existente.getId(), dadosAtualizados);

        assertSame(existente, response);
        assertEquals("12ABC34501DE35", response.getCnpj());
        assertEquals(dadosAtualizados.getDescricao(), response.getDescricao());
        assertEquals(dadosAtualizados.getMaxVagas(), response.getMaxVagas());
        assertEquals(dadosAtualizados.isPagamento(), response.isPagamento());
        assertTrue(response.isAtivo());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository, Mockito.never()).findModalidadeByDescricao(Mockito.anyString());
        Mockito.verify(repository).findModalidadeByCnpj(dadosAtualizados.getCnpj());
        Mockito.verify(repository).save(Mockito.same(existente));
    }

    @Test
    void shouldUpdateModalidadeWithoutChangingInactiveStatus()
    {
        Modalidade existente = new ModalidadeTestBuilder().setAtivo(false).build();
        Modalidade dadosAtualizados = new ModalidadeTestBuilder().setMaxVagas(30).setAtivo(true).build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));
        Mockito.when(repository.save(Mockito.same(existente))).thenReturn(existente);

        Modalidade response = service.updateModalidade(existente.getId(), dadosAtualizados);

        assertSame(existente, response);
        assertEquals(30, response.getMaxVagas());
        assertFalse(response.isAtivo());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository, Mockito.never()).findModalidadeByDescricao(Mockito.anyString());
        Mockito.verify(repository, Mockito.never()).findModalidadeByCnpj(Mockito.any());
        Mockito.verify(repository).save(Mockito.same(existente));
    }

    @Test
    void shouldKeepCnpjNullWithoutCheckingDuplicates()
    {
        Modalidade existente = new ModalidadeTestBuilder().setCNPJ(null).build();
        Modalidade dadosAtualizados = new ModalidadeTestBuilder().setCNPJ(null).setMaxVagas(30).build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));
        Mockito.when(repository.save(Mockito.same(existente))).thenReturn(existente);

        Modalidade response = service.updateModalidade(existente.getId(), dadosAtualizados);

        assertSame(existente, response);
        assertNull(response.getCnpj());
        assertEquals(30, response.getMaxVagas());
        assertTrue(response.isAtivo());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository, Mockito.never()).findModalidadeByDescricao(Mockito.anyString());
        Mockito.verify(repository, Mockito.never()).findModalidadeByCnpj(Mockito.any());
        Mockito.verify(repository).save(Mockito.same(existente));
    }

    @Test
    void shouldUpdateOnlyDescricaoSuccessfully()
    {
        Modalidade existente = new ModalidadeTestBuilder().build();
        Modalidade dadosAtualizados = new ModalidadeTestBuilder().setDescricao("Nova Descrição").build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));
        Mockito.when(repository.findModalidadeByDescricao(dadosAtualizados.getDescricao())).thenReturn(Optional.empty());
        Mockito.when(repository.save(Mockito.same(existente))).thenReturn(existente);

        Modalidade response = service.updateModalidade(existente.getId(), dadosAtualizados);

        assertSame(existente, response);
        assertEquals("Nova Descrição", response.getDescricao());
        assertEquals(dadosAtualizados.getCnpj(), response.getCnpj());
        assertEquals(dadosAtualizados.getMaxVagas(), response.getMaxVagas());
        assertEquals(dadosAtualizados.isPagamento(), response.isPagamento());
        assertTrue(response.isAtivo());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository).findModalidadeByDescricao(dadosAtualizados.getDescricao());
        Mockito.verify(repository, Mockito.never()).findModalidadeByCnpj(Mockito.any());
        Mockito.verify(repository).save(Mockito.same(existente));
    }

    @Test
    void shouldUpdateOnlyCnpjSuccessfully()
    {
        Modalidade existente = new ModalidadeTestBuilder().build();
        Modalidade dadosAtualizados = new ModalidadeTestBuilder().setCNPJ("12.ABC.345/01DE-35").build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));
        Mockito.when(repository.findModalidadeByCnpj(dadosAtualizados.getCnpj())).thenReturn(Optional.empty());
        Mockito.when(repository.save(Mockito.same(existente))).thenReturn(existente);

        Modalidade response = service.updateModalidade(existente.getId(), dadosAtualizados);

        assertSame(existente, response);
        assertEquals("12ABC34501DE35", response.getCnpj());
        assertEquals(dadosAtualizados.getDescricao(), response.getDescricao());
        assertEquals(dadosAtualizados.getMaxVagas(), response.getMaxVagas());
        assertEquals(dadosAtualizados.isPagamento(), response.isPagamento());
        assertTrue(response.isAtivo());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository, Mockito.never()).findModalidadeByDescricao(Mockito.anyString());
        Mockito.verify(repository).findModalidadeByCnpj(dadosAtualizados.getCnpj());
        Mockito.verify(repository).save(Mockito.same(existente));
    }

    @Test
    void shouldNotPartiallyUpdateModalidadeWhenCnpjAlreadyExists()
    {
        Modalidade existente = new ModalidadeTestBuilder().build();
        Modalidade dadosAtualizados = new ModalidadeTestBuilder().setDescricao("Nova Descrição").setCNPJ("12.ABC.345/01DE-35").build();
        Modalidade duplicado = new ModalidadeTestBuilder().setId(2L).setCNPJ("12.ABC.345/01DE-35").build();

        String descricaoOriginal = existente.getDescricao();
        String cnpjOriginal = existente.getCnpj();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));

        Mockito.when(repository.findModalidadeByDescricao(dadosAtualizados.getDescricao())).thenReturn(Optional.empty());

        Mockito.when(repository.findModalidadeByCnpj(dadosAtualizados.getCnpj())).thenReturn(Optional.of(duplicado));

        assertThrows(ModalidadeWithInvalidInformationException.class, () -> service.updateModalidade(existente.getId(), dadosAtualizados));

        assertEquals(descricaoOriginal, existente.getDescricao());
        assertEquals(cnpjOriginal, existente.getCnpj());

        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Modalidade.class));
    }

    @Test
    void shouldDeactivateModalidadeSuccessfully()
    {
        Modalidade existente = new ModalidadeTestBuilder().setAtivo(true).build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));
        Mockito.when(repository.save(Mockito.same(existente))).thenReturn(existente);

        service.deleteModalidadeById(existente.getId());

        assertFalse(existente.isAtivo());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository).save(Mockito.same(existente));
        Mockito.verify(repository, Mockito.never()).delete(Mockito.any());
        Mockito.verify(repository, Mockito.never()).deleteById(Mockito.anyLong());
    }

    @Test
    void shouldNotSaveWhenModalidadeIsAlreadyInactive()
    {
        Modalidade existente = new ModalidadeTestBuilder().setAtivo(false).build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));

        service.deleteModalidadeById(existente.getId());

        assertFalse(existente.isAtivo());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Modalidade.class));
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonexistentModalidade()
    {
        Long idModalidade = 99L;

        Mockito.when(repository.findById(idModalidade)).thenReturn(Optional.empty());

        ModalidadeNotFoundException e = assertThrows(ModalidadeNotFoundException.class, () -> service.deleteModalidadeById(idModalidade));

        assertEquals("Modalidade não encontrada.", e.getMessage());

        Mockito.verify(repository).findById(idModalidade);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Modalidade.class));
    }

    @Test
    void shouldReopenInactiveModalidadeSuccessfully()
    {
        Modalidade existente = new ModalidadeTestBuilder().setAtivo(false).build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));

        service.reabrirModalidadeById(existente.getId());

        assertTrue(existente.isAtivo());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository).save(Mockito.same(existente));
    }

    @Test
    void shouldNotSaveWhenModalidadeIsAlreadyActive()
    {
        Modalidade existente = new ModalidadeTestBuilder().setAtivo(true).build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));

        service.reabrirModalidadeById(existente.getId());

        assertTrue(existente.isAtivo());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Modalidade.class));
    }

    @Test
    void shouldThrowExceptionWhenReopeningNonexistentModalidade()
    {
        Long idModalidade = 99L;

        Mockito.when(repository.findById(idModalidade)).thenReturn(Optional.empty());

        ModalidadeNotFoundException e = assertThrows(ModalidadeNotFoundException.class, () -> service.reabrirModalidadeById(idModalidade));

        assertEquals("Modalidade não encontrada.", e.getMessage());

        Mockito.verify(repository).findById(idModalidade);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Modalidade.class));
    }

    @Test
    void shouldFindModalidadeByCnpjSuccessfully()
    {
        Modalidade existente = new ModalidadeTestBuilder().build();
        String cnpj = existente.getCnpj();

        Mockito.when(repository.findModalidadeByCnpj(cnpj)).thenReturn(Optional.of(existente));

        Modalidade response = service.findModalidadeByCnpj(cnpj);

        assertNotNull(response);
        assertSame(existente, response);
        assertEquals(cnpj, response.getCnpj());

        Mockito.verify(repository).findModalidadeByCnpj(cnpj);
    }

    @Test
    void shouldThrowExceptionWhenModalidadeIsNotFoundByCnpj()
    {
        String cnpj = "12ABC34501DE35";

        Mockito.when(repository.findModalidadeByCnpj(cnpj)).thenReturn(Optional.empty());

        ModalidadeNotFoundException e = assertThrows(ModalidadeNotFoundException.class, () -> service.findModalidadeByCnpj(cnpj));

        assertEquals("Modalidade não encontrada.", e.getMessage());

        Mockito.verify(repository).findModalidadeByCnpj(cnpj);
    }

    @Test
    void shouldFindModalidadeByDescricaoSuccessfully()
    {
        Modalidade existente = new ModalidadeTestBuilder().build();
        String descricao = existente.getDescricao();

        Mockito.when(repository.findModalidadeByDescricao(descricao)).thenReturn(Optional.of(existente));

        Modalidade response = service.findModalidadeByDescricao(descricao);

        assertNotNull(response);
        assertSame(existente, response);
        assertEquals(descricao, response.getDescricao());

        Mockito.verify(repository).findModalidadeByDescricao(descricao);
    }

    @Test
    void shouldThrowExceptionWhenModalidadeIsNotFoundByDescricao()
    {
        String descricao = "ababa";

        Mockito.when(repository.findModalidadeByDescricao(descricao)).thenReturn(Optional.empty());

        ModalidadeNotFoundException e = assertThrows(ModalidadeNotFoundException.class, () -> service.findModalidadeByDescricao(descricao));

        assertEquals("Modalidade não encontrada.", e.getMessage());

        Mockito.verify(repository).findModalidadeByDescricao(descricao);
    }
}