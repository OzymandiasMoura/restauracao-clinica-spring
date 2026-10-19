package org.example.clinicarestauracao.Application.Services;

import org.example.clinicarestauracao.Application.Exceptions.ConvenioNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.ConvenioWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.ConvenioRepository;
import org.example.clinicarestauracao.Application.Interfaces.InternacaoRepository;
import org.example.clinicarestauracao.Builders.ConvenioTestBuilder;
import org.example.clinicarestauracao.Domain.Entities.Convenio;
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
class ConvenioServiceTest
{
    private final ConvenioTestBuilder builder = new ConvenioTestBuilder();
    @Mock
    private ConvenioRepository repository;
    @Mock
    private InternacaoRepository internacaoRepository;
    @InjectMocks
    private ConvenioService service;

    @Test
    void shouldCreateConvenioSuccessfully()
    {
        Convenio salva = builder.build();
        Convenio entrada = builder.buildForCreate();

        Mockito.when(repository.save(Mockito.any(Convenio.class))).thenReturn(salva);
        Mockito.when(repository.findConvenioByCnpj(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(repository.findConvenioByDescricaoIgnoreCase(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(repository.findConvenioByCor(Mockito.anyString())).thenReturn(Optional.empty());



        Convenio response = service.createConvenio(entrada);

        assertNotNull(response);
        assertEquals(salva.getId(), response.getId());
        assertEquals(salva.getDescricao(), response.getDescricao());
        assertEquals(salva.getCnpj(), response.getCnpj());
        assertEquals(salva.getMaxVagas(), response.getMaxVagas());
        assertEquals(salva.isAtivo(), response.isAtivo());
        assertEquals(salva.isPagamento(), response.isPagamento());
        assertEquals(salva.getCor(), response.getCor());

        Mockito.verify(repository).save(entrada);
        Mockito.verify(repository).findConvenioByCnpj(entrada.getCnpj());
        Mockito.verify(repository).findConvenioByDescricaoIgnoreCase(entrada.getDescricao());
        Mockito.verify(repository).findConvenioByCor(entrada.getCor());
    }

    @Test
    void shouldThrowExceptionWhenCNPJExists()
    {
        Convenio retornoCnpj = builder.build();
        Convenio entrada = builder.buildForCreate();

        Mockito.when(repository.findConvenioByCnpj(Mockito.anyString())).thenReturn(Optional.of(retornoCnpj));

        ConvenioWithInvalidInformationException e = assertThrows(ConvenioWithInvalidInformationException.class, () -> service.createConvenio(entrada));

        assertEquals("CNPJ já cadastrado.", e.getMessage());

        Mockito.verify(repository).findConvenioByCnpj(entrada.getCnpj());
        Mockito.verify(repository, Mockito.never()).findConvenioByDescricaoIgnoreCase(Mockito.anyString());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Convenio.class));
    }

    @Test
    void shouldThrowExceptionWhenDescricaoExists()
    {
        Convenio retornoDescricao = builder.build();
        Convenio entrada = builder.buildForCreate();

        Mockito.when(repository.findConvenioByCnpj(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(repository.findConvenioByDescricaoIgnoreCase(Mockito.anyString())).thenReturn(Optional.of(retornoDescricao));

        ConvenioWithInvalidInformationException e = assertThrows(ConvenioWithInvalidInformationException.class, () -> service.createConvenio(entrada));

        assertEquals("Descrição já cadastrada.", e.getMessage());

        Mockito.verify(repository).findConvenioByCnpj(entrada.getCnpj());
        Mockito.verify(repository).findConvenioByDescricaoIgnoreCase(entrada.getDescricao());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Convenio.class));
    }

    @Test
    void shouldRejectDuplicatedDescricaoIgnoringCase()
    {
        Convenio entrada = new ConvenioTestBuilder()
                .setDescricao("pilates")
                .buildForCreate();
        Convenio existente = new ConvenioTestBuilder()
                .setDescricao("Pilates")
                .build();

        Mockito.when(repository.findConvenioByCnpj(entrada.getCnpj())).thenReturn(Optional.empty());
        Mockito.when(repository.findConvenioByDescricaoIgnoreCase("pilates")).thenReturn(Optional.of(existente));

        ConvenioWithInvalidInformationException exception = assertThrows(
                ConvenioWithInvalidInformationException.class,
                () -> service.createConvenio(entrada)
        );

        assertEquals("Descrição já cadastrada.", exception.getMessage());
        Mockito.verify(repository).findConvenioByDescricaoIgnoreCase("pilates");
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void shouldCreateConvenioWithoutCnpjSuccessfully()
    {
        Convenio entrada = builder.setCNPJ(null).buildForCreate();
        Convenio salva = builder.build();

        Mockito.when(repository.save(Mockito.any(Convenio.class))).thenReturn(salva);
        Mockito.when(repository.findConvenioByDescricaoIgnoreCase(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(repository.findConvenioByCor(entrada.getCor())).thenReturn(Optional.empty());


        Convenio response = service.createConvenio(entrada);

        assertNotNull(response);
        assertNull(response.getCnpj());
        assertEquals(salva.getDescricao(), response.getDescricao());

        Mockito.verify(repository).save(entrada);
        Mockito.verify(repository, Mockito.never()).findConvenioByCnpj(Mockito.any());
        Mockito.verify(repository).findConvenioByDescricaoIgnoreCase(entrada.getDescricao());
        Mockito.verify(repository).findConvenioByCor(entrada.getCor());
    }

    @Test
    void shouldFindConvenioByIdSuccessfully()
    {
        Convenio entrada = builder.build();

        Mockito.when(repository.findById(entrada.getId())).thenReturn(Optional.of(entrada));

        Convenio response = service.findConvenioById(entrada.getId());

        assertNotNull(response);
        assertEquals(entrada.getId(), response.getId());
        assertSame(entrada, response);

        Mockito.verify(repository).findById(entrada.getId());
    }

    @Test
    void shouldThrowExceptionWhenConvenioIsNotFoundById()
    {
        Convenio entrada = builder.build();

        Mockito.when(repository.findById(entrada.getId())).thenReturn(Optional.empty());

        ConvenioNotFoundException e = assertThrows(ConvenioNotFoundException.class, () -> service.findConvenioById(entrada.getId()));

        assertEquals("Convênio não encontrado.", e.getMessage());

        Mockito.verify(repository).findById(entrada.getId());
    }

    @Test
    void shouldFindAllConveniosSuccessfully()
    {
        Convenio entrada = builder.build();
        Convenio entrada2 = builder.setId(2L).setDescricao("descricao2").setCNPJ(null).build();

        List<Convenio> entradas = List.of(entrada, entrada2);

        Mockito.when(repository.findAll()).thenReturn(entradas);

        List<Convenio> response = service.findAllConvenios();

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(entradas, response);
        assertSame(entradas, response);
        assertSame(entrada, response.get(0));
        assertSame(entrada2, response.get(1));
        Mockito.verify(repository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoConveniosExist()
    {
        Mockito.when(repository.findAll()).thenReturn(List.of());

        List<Convenio> response = service.findAllConvenios();

        assertNotNull(response);
        assertTrue(response.isEmpty());

        Mockito.verify(repository).findAll();
    }

    @Test
    void shouldUpdateConvenioSuccessfully()
    {
        Convenio existente = new ConvenioTestBuilder().build();
        Convenio dadosAtualizados = new ConvenioTestBuilder().setDescricao("Nova Descrição").setCNPJ("12.ABC.345/01DE-35").setMaxVagas(30).setPagamento(false).setCor("#FF5733").build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));
        Mockito.when(repository.findConvenioByDescricaoIgnoreCase(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(repository.findConvenioByCnpj(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(repository.findConvenioByCor(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(repository.save(Mockito.same(existente))).thenReturn(existente);

        Convenio response = service.updateConvenio(existente.getId(), dadosAtualizados);

        assertNotNull(response);
        assertSame(existente, response);
        assertEquals("Nova Descrição", response.getDescricao());
        assertEquals("12ABC34501DE35", response.getCnpj());
        assertEquals(30, response.getMaxVagas());
        assertFalse(response.isPagamento());
        assertTrue(response.isAtivo());
        assertEquals("#FF5733", response.getCor());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository).findConvenioByDescricaoIgnoreCase(dadosAtualizados.getDescricao());
        Mockito.verify(repository).findConvenioByCnpj(dadosAtualizados.getCnpj());
        Mockito.verify(repository).findConvenioByCor("#FF5733");
        Mockito.verify(repository).save(Mockito.same(existente));
    }

    @Test
    void shouldThrowExceptionWhenIdIsInvalid()
    {
        Convenio existente = new ConvenioTestBuilder().build();
        Convenio dadosAtualizados = new ConvenioTestBuilder().setDescricao("Nova Descrição").setCNPJ("12.ABC.345/01DE-35").build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.empty());

        ConvenioNotFoundException response = assertThrows(ConvenioNotFoundException.class, () -> service.updateConvenio(existente.getId(), dadosAtualizados));

        assertEquals("Convênio não encontrado.", response.getMessage());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository, Mockito.never()).findConvenioByDescricaoIgnoreCase(Mockito.anyString());
        Mockito.verify(repository, Mockito.never()).findConvenioByCnpj(Mockito.any());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Convenio.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatedDescricaoAlreadyExists()
    {
        Convenio existente = new ConvenioTestBuilder().build();
        Convenio dadosAtualizados = new ConvenioTestBuilder().setId(2L).setDescricao("Nova Descrição").setCNPJ("12.ABC.345/01DE-35").build();
        Convenio duplicado = new ConvenioTestBuilder().setId(3L).setDescricao("Nova Descrição").build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));
        Mockito.when(repository.findConvenioByDescricaoIgnoreCase(dadosAtualizados.getDescricao())).thenReturn(Optional.of(duplicado));

        ConvenioWithInvalidInformationException e = assertThrows(ConvenioWithInvalidInformationException.class, () -> service.updateConvenio(existente.getId(), dadosAtualizados));

        assertEquals("Descrição já cadastrada.", e.getMessage());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository).findConvenioByDescricaoIgnoreCase(dadosAtualizados.getDescricao());
        Mockito.verify(repository, Mockito.never()).findConvenioByCnpj(Mockito.anyString());
        Mockito.verify(repository, Mockito.never()).findConvenioByCor(Mockito.anyString());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Convenio.class));
    }

    @Test
    void shouldRejectUpdatingDescricaoUsedByAnotherConvenioIgnoringCase()
    {
        Convenio existente = new ConvenioTestBuilder()
                .setId(1L)
                .setDescricao("Alongamento")
                .build();
        Convenio dadosAtualizados = new ConvenioTestBuilder()
                .setDescricao("pilates")
                .build();
        Convenio outraConvenio = new ConvenioTestBuilder()
                .setId(2L)
                .setDescricao("Pilates")
                .build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(existente));
        Mockito.when(repository.findConvenioByDescricaoIgnoreCase("pilates"))
                .thenReturn(Optional.of(outraConvenio));

        ConvenioWithInvalidInformationException exception = assertThrows(
                ConvenioWithInvalidInformationException.class,
                () -> service.updateConvenio(1L, dadosAtualizados)
        );

        assertEquals("Descrição já cadastrada.", exception.getMessage());
        assertEquals("Alongamento", existente.getDescricao());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void shouldAllowChangingOnlyDescricaoCaseForSameConvenio()
    {
        Convenio existente = new ConvenioTestBuilder()
                .setId(1L)
                .setDescricao("Pilates")
                .build();
        Convenio dadosAtualizados = new ConvenioTestBuilder()
                .setId(1L)
                .setDescricao("pilates")
                .build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(existente));
        Mockito.when(repository.findConvenioByDescricaoIgnoreCase("pilates"))
                .thenReturn(Optional.of(existente));
        Mockito.when(repository.save(existente)).thenReturn(existente);

        Convenio response = service.updateConvenio(1L, dadosAtualizados);

        assertSame(existente, response);
        assertEquals("pilates", response.getDescricao());
        Mockito.verify(repository).save(existente);
    }

    @Test
    void shouldThrowExceptionWhenUpdatedCnpjAlreadyExists()
    {
        Convenio existente = new ConvenioTestBuilder().build();
        Convenio dadosAtualizados = new ConvenioTestBuilder().setId(2L).setDescricao("Nova Descrição").setCNPJ("12.ABC.345/01DE-35").build();
        Convenio duplicado = new ConvenioTestBuilder().setId(3L).setCNPJ("12.ABC.345/01DE-35").build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));
        Mockito.when(repository.findConvenioByDescricaoIgnoreCase(dadosAtualizados.getDescricao())).thenReturn(Optional.empty());
        Mockito.when(repository.findConvenioByCnpj(dadosAtualizados.getCnpj())).thenReturn(Optional.of(duplicado));

        ConvenioWithInvalidInformationException e = assertThrows(ConvenioWithInvalidInformationException.class, () -> service.updateConvenio(existente.getId(), dadosAtualizados));

        assertEquals("CNPJ já cadastrado.", e.getMessage());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository).findConvenioByDescricaoIgnoreCase(dadosAtualizados.getDescricao());
        Mockito.verify(repository).findConvenioByCnpj(dadosAtualizados.getCnpj());
        Mockito.verify(repository, Mockito.never()).findConvenioByCor(Mockito.anyString());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Convenio.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingToDuplicatedColor()
    {
        Convenio existente = new ConvenioTestBuilder().setCor("#2E86C1").build();
        Convenio dadosAtualizados = new ConvenioTestBuilder().setCor("#FF5733").build();
        Convenio outraConvenio = new ConvenioTestBuilder().setId(2L).setCor("#FF5733").build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));
        Mockito.when(repository.findConvenioByCor("#FF5733")).thenReturn(Optional.of(outraConvenio));

        ConvenioWithInvalidInformationException exception = assertThrows(ConvenioWithInvalidInformationException.class, () -> service.updateConvenio(existente.getId(), dadosAtualizados));

        assertEquals("Cor já cadastrada.", exception.getMessage());

        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Convenio.class));
        Mockito.verify(repository).findConvenioByCor("#FF5733");
    }

    @Test
    void shouldThrowExceptionWhenCreatingConvenioWithDuplicatedColor()
    {
        Convenio entrada = builder.buildForCreate();
        Convenio existente = builder.build();

        Mockito.when(repository.findConvenioByCnpj(entrada.getCnpj())).thenReturn(Optional.empty());
        Mockito.when(repository.findConvenioByDescricaoIgnoreCase(entrada.getDescricao())).thenReturn(Optional.empty());
        Mockito.when(repository.findConvenioByCor(entrada.getCor())).thenReturn(Optional.of(existente));

        ConvenioWithInvalidInformationException exception = assertThrows(ConvenioWithInvalidInformationException.class, () -> service.createConvenio(entrada));

        assertEquals("Cor já cadastrada.", exception.getMessage());

        Mockito.verify(repository).findConvenioByCor(entrada.getCor());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Convenio.class));
    }

    @Test
    void shouldNotSearchDuplicatedColorWhenColorIsUnchanged()
    {
        Convenio existente = new ConvenioTestBuilder().setCor("#2E86C1").build();
        Convenio dadosAtualizados = new ConvenioTestBuilder().setCor("#2E86C1").setMaxVagas(30).build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));
        Mockito.when(repository.save(existente)).thenReturn(existente);

        Convenio response = service.updateConvenio(existente.getId(), dadosAtualizados);

        assertEquals("#2E86C1", response.getCor());

        Mockito.verify(repository, Mockito.never()).findConvenioByCor(Mockito.anyString());
        Mockito.verify(repository).save(existente);
    }

    @Test
    void shouldUpdateMaxVagasAndPagamentoWithoutCheckingDuplicates()
    {
        Convenio existente = new ConvenioTestBuilder().build();
        Convenio dadosAtualizados = new ConvenioTestBuilder().setMaxVagas(30).setPagamento(false).build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));
        Mockito.when(repository.save(Mockito.same(existente))).thenReturn(existente);

        Convenio response = service.updateConvenio(existente.getId(), dadosAtualizados);

        assertSame(existente, response);
        assertEquals(30, response.getMaxVagas());
        assertFalse(response.isPagamento());
        assertEquals(dadosAtualizados.getDescricao(), response.getDescricao());
        assertEquals(dadosAtualizados.getCnpj(), response.getCnpj());
        assertTrue(response.isAtivo());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository, Mockito.never()).findConvenioByDescricaoIgnoreCase(Mockito.anyString());
        Mockito.verify(repository, Mockito.never()).findConvenioByCnpj(Mockito.any());
        Mockito.verify(repository).save(Mockito.same(existente));
    }

    @Test
    void shouldRemoveCnpjSuccessfully()
    {
        Convenio existente = new ConvenioTestBuilder().build();
        Convenio dadosAtualizados = new ConvenioTestBuilder().setCNPJ(null).build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));
        Mockito.when(repository.save(Mockito.same(existente))).thenReturn(existente);

        Convenio response = service.updateConvenio(existente.getId(), dadosAtualizados);

        assertSame(existente, response);
        assertNull(response.getCnpj());
        assertEquals(dadosAtualizados.getDescricao(), response.getDescricao());
        assertEquals(dadosAtualizados.getMaxVagas(), response.getMaxVagas());
        assertEquals(dadosAtualizados.isPagamento(), response.isPagamento());
        assertTrue(response.isAtivo());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository, Mockito.never()).findConvenioByDescricaoIgnoreCase(Mockito.anyString());
        Mockito.verify(repository, Mockito.never()).findConvenioByCnpj(Mockito.any());
        Mockito.verify(repository).save(Mockito.same(existente));
    }

    @Test
    void shouldAddCnpjSuccessfully()
    {
        Convenio existente = new ConvenioTestBuilder().setCNPJ(null).build();
        Convenio dadosAtualizados = new ConvenioTestBuilder().setCNPJ("12.ABC.345/01DE-35").build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));
        Mockito.when(repository.save(Mockito.same(existente))).thenReturn(existente);
        Mockito.when(repository.findConvenioByCnpj(dadosAtualizados.getCnpj())).thenReturn(Optional.empty());

        Convenio response = service.updateConvenio(existente.getId(), dadosAtualizados);

        assertSame(existente, response);
        assertEquals("12ABC34501DE35", response.getCnpj());
        assertEquals(dadosAtualizados.getDescricao(), response.getDescricao());
        assertEquals(dadosAtualizados.getMaxVagas(), response.getMaxVagas());
        assertEquals(dadosAtualizados.isPagamento(), response.isPagamento());
        assertTrue(response.isAtivo());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository, Mockito.never()).findConvenioByDescricaoIgnoreCase(Mockito.anyString());
        Mockito.verify(repository).findConvenioByCnpj(dadosAtualizados.getCnpj());
        Mockito.verify(repository).save(Mockito.same(existente));
    }

    @Test
    void shouldUpdateConvenioWithoutChangingInactiveStatus()
    {
        Convenio existente = new ConvenioTestBuilder().setAtivo(false).build();
        Convenio dadosAtualizados = new ConvenioTestBuilder().setMaxVagas(30).setAtivo(true).build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));
        Mockito.when(repository.save(Mockito.same(existente))).thenReturn(existente);

        Convenio response = service.updateConvenio(existente.getId(), dadosAtualizados);

        assertSame(existente, response);
        assertEquals(30, response.getMaxVagas());
        assertFalse(response.isAtivo());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository, Mockito.never()).findConvenioByDescricaoIgnoreCase(Mockito.anyString());
        Mockito.verify(repository, Mockito.never()).findConvenioByCnpj(Mockito.any());
        Mockito.verify(repository).save(Mockito.same(existente));
    }

    @Test
    void shouldKeepCnpjNullWithoutCheckingDuplicates()
    {
        Convenio existente = new ConvenioTestBuilder().setCNPJ(null).build();
        Convenio dadosAtualizados = new ConvenioTestBuilder().setCNPJ(null).setMaxVagas(30).build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));
        Mockito.when(repository.save(Mockito.same(existente))).thenReturn(existente);

        Convenio response = service.updateConvenio(existente.getId(), dadosAtualizados);

        assertSame(existente, response);
        assertNull(response.getCnpj());
        assertEquals(30, response.getMaxVagas());
        assertTrue(response.isAtivo());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository, Mockito.never()).findConvenioByDescricaoIgnoreCase(Mockito.anyString());
        Mockito.verify(repository, Mockito.never()).findConvenioByCnpj(Mockito.any());
        Mockito.verify(repository).save(Mockito.same(existente));
    }

    @Test
    void shouldUpdateOnlyDescricaoSuccessfully()
    {
        Convenio existente = new ConvenioTestBuilder().build();
        Convenio dadosAtualizados = new ConvenioTestBuilder().setDescricao("Nova Descrição").build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));
        Mockito.when(repository.findConvenioByDescricaoIgnoreCase(dadosAtualizados.getDescricao())).thenReturn(Optional.empty());
        Mockito.when(repository.save(Mockito.same(existente))).thenReturn(existente);

        Convenio response = service.updateConvenio(existente.getId(), dadosAtualizados);

        assertSame(existente, response);
        assertEquals("Nova Descrição", response.getDescricao());
        assertEquals(dadosAtualizados.getCnpj(), response.getCnpj());
        assertEquals(dadosAtualizados.getMaxVagas(), response.getMaxVagas());
        assertEquals(dadosAtualizados.isPagamento(), response.isPagamento());
        assertTrue(response.isAtivo());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository).findConvenioByDescricaoIgnoreCase(dadosAtualizados.getDescricao());
        Mockito.verify(repository, Mockito.never()).findConvenioByCnpj(Mockito.any());
        Mockito.verify(repository).save(Mockito.same(existente));
    }

    @Test
    void shouldUpdateOnlyCnpjSuccessfully()
    {
        Convenio existente = new ConvenioTestBuilder().build();
        Convenio dadosAtualizados = new ConvenioTestBuilder().setCNPJ("12.ABC.345/01DE-35").build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));
        Mockito.when(repository.findConvenioByCnpj(dadosAtualizados.getCnpj())).thenReturn(Optional.empty());
        Mockito.when(repository.save(Mockito.same(existente))).thenReturn(existente);

        Convenio response = service.updateConvenio(existente.getId(), dadosAtualizados);

        assertSame(existente, response);
        assertEquals("12ABC34501DE35", response.getCnpj());
        assertEquals(dadosAtualizados.getDescricao(), response.getDescricao());
        assertEquals(dadosAtualizados.getMaxVagas(), response.getMaxVagas());
        assertEquals(dadosAtualizados.isPagamento(), response.isPagamento());
        assertTrue(response.isAtivo());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository, Mockito.never()).findConvenioByDescricaoIgnoreCase(Mockito.anyString());
        Mockito.verify(repository).findConvenioByCnpj(dadosAtualizados.getCnpj());
        Mockito.verify(repository).save(Mockito.same(existente));
    }

    @Test
    void shouldNotPartiallyUpdateConvenioWhenCnpjAlreadyExists()
    {
        Convenio existente = new ConvenioTestBuilder().build();
        Convenio dadosAtualizados = new ConvenioTestBuilder().setDescricao("Nova Descrição").setCNPJ("12.ABC.345/01DE-35").build();
        Convenio duplicado = new ConvenioTestBuilder().setId(2L).setCNPJ("12.ABC.345/01DE-35").build();

        String descricaoOriginal = existente.getDescricao();
        String cnpjOriginal = existente.getCnpj();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));

        Mockito.when(repository.findConvenioByDescricaoIgnoreCase(dadosAtualizados.getDescricao())).thenReturn(Optional.empty());

        Mockito.when(repository.findConvenioByCnpj(dadosAtualizados.getCnpj())).thenReturn(Optional.of(duplicado));

        assertThrows(ConvenioWithInvalidInformationException.class, () -> service.updateConvenio(existente.getId(), dadosAtualizados));

        assertEquals(descricaoOriginal, existente.getDescricao());
        assertEquals(cnpjOriginal, existente.getCnpj());

        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Convenio.class));
    }

    @Test
    void shouldDeactivateConvenioSuccessfullyWhenThereAreNoInternacoes()
    {
        Convenio existente = new ConvenioTestBuilder().setAtivo(true).build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));
        Mockito.when(internacaoRepository.existsByConvenio(existente)).thenReturn(false);
        Mockito.when(repository.save(Mockito.same(existente))).thenReturn(existente);

        service.deactivateConvenioById(existente.getId());

        assertFalse(existente.isAtivo());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(internacaoRepository).existsByConvenio(Mockito.same(existente));
        Mockito.verify(repository).save(Mockito.same(existente));
        Mockito.verify(repository, Mockito.never()).delete(Mockito.any());
        Mockito.verify(repository, Mockito.never()).deleteById(Mockito.anyLong());
    }

    @Test
    void shouldNotConsultInternacoesWhenConvenioIsAlreadyInactive()
    {
        Convenio existente = new ConvenioTestBuilder().setAtivo(false).build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));

        service.deactivateConvenioById(existente.getId());

        assertFalse(existente.isAtivo());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Convenio.class));
        Mockito.verifyNoInteractions(internacaoRepository);
    }

    @Test
    void shouldNotDeactivateConvenioWhenThereAreInternacoes()
    {
        Convenio existente = new ConvenioTestBuilder().setAtivo(true).build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));
        Mockito.when(internacaoRepository.existsByConvenio(existente)).thenReturn(true);

        ConvenioWithInvalidInformationException exception = assertThrows(
                ConvenioWithInvalidInformationException.class,
                () -> service.deactivateConvenioById(existente.getId())
        );

        assertAll(
                () -> assertEquals("Convênio não pode ser desativado enquanto possuir internações.", exception.getMessage()),
                () -> assertTrue(existente.isAtivo())
        );

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(internacaoRepository).existsByConvenio(Mockito.same(existente));
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Convenio.class));
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonexistentConvenio()
    {
        Long idConvenio = 99L;

        Mockito.when(repository.findById(idConvenio)).thenReturn(Optional.empty());

        ConvenioNotFoundException e = assertThrows(ConvenioNotFoundException.class, () -> service.deleteConvenioById(idConvenio));

        assertEquals("Convênio não encontrado.", e.getMessage());

        Mockito.verify(repository).findById(idConvenio);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Convenio.class));
        Mockito.verify(repository, Mockito.never()).delete(Mockito.any(Convenio.class));
        Mockito.verifyNoInteractions(internacaoRepository);
    }

    @Test
    void shouldReopenInactiveConvenioSuccessfully()
    {
        Convenio existente = new ConvenioTestBuilder().setAtivo(false).build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));

        service.reabrirConvenioById(existente.getId());

        assertTrue(existente.isAtivo());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository).save(Mockito.same(existente));
    }

    @Test
    void shouldNotSaveWhenConvenioIsAlreadyActive()
    {
        Convenio existente = new ConvenioTestBuilder().setAtivo(true).build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));

        service.reabrirConvenioById(existente.getId());

        assertTrue(existente.isAtivo());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Convenio.class));
    }

    @Test
    void shouldThrowExceptionWhenReopeningNonexistentConvenio()
    {
        Long idConvenio = 99L;

        Mockito.when(repository.findById(idConvenio)).thenReturn(Optional.empty());

        ConvenioNotFoundException e = assertThrows(ConvenioNotFoundException.class, () -> service.reabrirConvenioById(idConvenio));

        assertEquals("Convênio não encontrado.", e.getMessage());

        Mockito.verify(repository).findById(idConvenio);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Convenio.class));
    }

    @Test
    void shouldFindConvenioByCnpjSuccessfully()
    {
        Convenio existente = new ConvenioTestBuilder().build();
        String cnpj = existente.getCnpj();

        Mockito.when(repository.findConvenioByCnpj(cnpj)).thenReturn(Optional.of(existente));

        Convenio response = service.findConvenioByCnpj(cnpj);

        assertNotNull(response);
        assertSame(existente, response);
        assertEquals(cnpj, response.getCnpj());

        Mockito.verify(repository).findConvenioByCnpj(cnpj);
    }

    @Test
    void shouldThrowExceptionWhenConvenioIsNotFoundByCnpj()
    {
        String cnpj = "12ABC34501DE35";

        Mockito.when(repository.findConvenioByCnpj(cnpj)).thenReturn(Optional.empty());

        ConvenioNotFoundException e = assertThrows(ConvenioNotFoundException.class, () -> service.findConvenioByCnpj(cnpj));

        assertEquals("Convênio não encontrado.", e.getMessage());

        Mockito.verify(repository).findConvenioByCnpj(cnpj);
    }

    @Test
    void shouldFindConvenioByDescricaoIgnoringCase()
    {
        Convenio existente = new ConvenioTestBuilder().build();
        String descricaoPesquisada = "convênio";

        Mockito.when(repository.findConvenioByDescricaoIgnoreCase(descricaoPesquisada)).thenReturn(Optional.of(existente));

        Convenio response = service.findConvenioByDescricao(descricaoPesquisada);

        assertNotNull(response);
        assertSame(existente, response);
        assertEquals("Convênio", response.getDescricao());

        Mockito.verify(repository).findConvenioByDescricaoIgnoreCase(descricaoPesquisada);
    }

    @Test
    void shouldThrowExceptionWhenConvenioIsNotFoundByDescricao()
    {
        String descricao = "ababa";

        Mockito.when(repository.findConvenioByDescricaoIgnoreCase(descricao)).thenReturn(Optional.empty());

        ConvenioNotFoundException e = assertThrows(ConvenioNotFoundException.class, () -> service.findConvenioByDescricao(descricao));

        assertEquals("Convênio não encontrado.", e.getMessage());

        Mockito.verify(repository).findConvenioByDescricaoIgnoreCase(descricao);
    }

    @Test
    void shouldFindConvenioByCorSuccessfully()
    {
        Convenio existente = new ConvenioTestBuilder().setCor("#2E86C1").build();

        Mockito.when(repository.findConvenioByCor("#2E86C1")).thenReturn(Optional.of(existente));

        Convenio response = service.findConvenioByCor("#2E86C1");

        assertNotNull(response);
        assertSame(existente, response);
        assertEquals("#2E86C1", response.getCor());

        Mockito.verify(repository).findConvenioByCor("#2E86C1");
    }

    @Test
    void shouldThrowExceptionWhenConvenioIsNotFoundByCor()
    {
        String cor = "#FF5733";

        Mockito.when(repository.findConvenioByCor(cor)).thenReturn(Optional.empty());

        ConvenioNotFoundException exception = assertThrows(ConvenioNotFoundException.class, () -> service.findConvenioByCor(cor));

        assertEquals("Convênio não encontrado.", exception.getMessage());

        Mockito.verify(repository).findConvenioByCor(cor);
    }

    // Testes delete físico

    @Test
    void shouldDeleteActiveConvenioWhenThereAreNoInternacoes()
    {
        Convenio existente = new ConvenioTestBuilder()
                .setAtivo(true)
                .build();

        Mockito.when(repository.findById(existente.getId()))
                .thenReturn(Optional.of(existente));

        Mockito.when(internacaoRepository.existsByConvenio(existente))
                .thenReturn(false);

        service.deleteConvenioById(existente.getId());

        assertTrue(existente.isAtivo());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(internacaoRepository)
                .existsByConvenio(Mockito.same(existente));
        Mockito.verify(repository).delete(Mockito.same(existente));
        Mockito.verify(repository, Mockito.never())
                .save(Mockito.any(Convenio.class));
    }

    @Test
    void shouldDeleteInactiveConvenioWhenThereAreNoInternacoes()
    {
        Convenio existente = new ConvenioTestBuilder().setAtivo(false).build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));
        Mockito.when(internacaoRepository.existsByConvenio(existente)).thenReturn(false);

        service.deleteConvenioById(existente.getId());

        assertFalse(existente.isAtivo());

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(internacaoRepository).existsByConvenio(Mockito.same(existente));
        Mockito.verify(repository).delete(Mockito.same(existente));
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Convenio.class));
    }

    @Test
    void shouldNotDeleteConvenioWhenThereAreInternacoes()
    {
        Convenio existente = new ConvenioTestBuilder().setAtivo(true).build();

        Mockito.when(repository.findById(existente.getId())).thenReturn(Optional.of(existente));

        Mockito.when(internacaoRepository.existsByConvenio(existente)).thenReturn(true);

        ConvenioWithInvalidInformationException exception = assertThrows(ConvenioWithInvalidInformationException.class, () -> service.deleteConvenioById(existente.getId()));

        assertAll(
                () -> assertEquals("Convênio não pode ser deletado enquanto possuir internações.", exception.getMessage()),
                () -> assertTrue(existente.isAtivo())
        );

        Mockito.verify(repository).findById(existente.getId());
        Mockito.verify(internacaoRepository).existsByConvenio(Mockito.same(existente));
        Mockito.verify(repository, Mockito.never()).delete(Mockito.any(Convenio.class));
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Convenio.class));
    }
}
