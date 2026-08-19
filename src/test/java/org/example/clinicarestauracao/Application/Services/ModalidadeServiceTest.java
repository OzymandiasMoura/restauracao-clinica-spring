package org.example.clinicarestauracao.Application.Services;

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

        assertEquals("CNPJ ja cadastrado", e.getMessage());

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

        assertEquals("Descrição ja cadastrada", e.getMessage());

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

        Modalidade response  = service.createModalidade(entrada);

        assertNotNull(response);
        assertNull(response.getCnpj());
        assertEquals(salva.getDescricao(), response.getDescricao());

        Mockito.verify(repository).save(entrada);
        Mockito.verify(repository, Mockito.never()).findModalidadeByCnpj(Mockito.anyString());
        Mockito.verify(repository).findModalidadeByDescricao(Mockito.anyString());
    }
}