package org.example.clinicarestauracao.Application.Services;


import org.junit.jupiter.api.DisplayName;
import org.example.clinicarestauracao.Application.Exceptions.Convenio.ConvenioNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Internacao.InternacaoInvalidQueryException;
import org.example.clinicarestauracao.Application.Exceptions.Internacao.InternacaoNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Internacao.InternacaoWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.InternacaoRepository;
import org.example.clinicarestauracao.Application.Interfaces.PagamentoRepository;
import org.example.clinicarestauracao.Application.Reports.InternacaoMonthlyCountProjection;
import org.example.clinicarestauracao.Application.Reports.InternacaoMonthlyMovement;
import org.example.clinicarestauracao.Application.Reports.InternacoesAtivasByConvenio;
import org.example.clinicarestauracao.Application.Reports.TreatmentStageByConvenio;
import org.example.clinicarestauracao.Builders.AcolhidoTestBuilder;
import org.example.clinicarestauracao.Builders.ConvenioTestBuilder;
import org.example.clinicarestauracao.Builders.InternacaoTestBuilder;
import org.example.clinicarestauracao.Domain.Entities.Acolhido;
import org.example.clinicarestauracao.Domain.Entities.Convenio;
import org.example.clinicarestauracao.Domain.Entities.Internacao;
import org.example.clinicarestauracao.Domain.Enums.AltaTypes;
import org.example.clinicarestauracao.Domain.Enums.TreatmentStage;
import org.example.clinicarestauracao.Infrastruct.Database.Audit.DatabaseAuditContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InternacaoServiceTest
{
    @Mock
    private InternacaoRepository repository;
    @Mock
    private PagamentoRepository pagamentoRepository;
    @Mock
    private AcolhidoService acolhidoService;
    @Mock
    private ConvenioService convenioService;
    @Mock
    private DatabaseAuditContext audit;
    @InjectMocks
    private InternacaoService service;

    // Testes findById

    @DisplayName("Deve buscar internação por ID com sucesso")
    @Test
    void shouldFindInternacaoByIdSuccessfully()
    {
        Internacao internacao = InternacaoTestBuilder.newInternacao().build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(internacao));

        Internacao response = service.findInternacaoById(1L);

        assertSame(internacao, response);

        Mockito.verify(repository).findById(1L);
    }

    @DisplayName("Deve lançar exceção quando a internação não for encontrada quando internação não existir")
    @Test
    void shouldThrowInternacaoNotFoundExceptionWhenInternacaoDoesNotExist()
    {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        InternacaoNotFoundException exception = assertThrows(InternacaoNotFoundException.class, () -> service.findInternacaoById(1L));

        assertEquals("Internação não encontrada.", exception.getMessage());

        Mockito.verify(repository).findById(1L);
    }

    @DisplayName("Deve lançar exceção quando a internação não for encontrada quando o ID é inválido")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldThrowInternacaoNotFoundExceptionWhenIdIsInvalid(Long id)
    {
        InternacaoNotFoundException exception = assertThrows(InternacaoNotFoundException.class, () -> service.findInternacaoById(id));

        assertEquals("Internação não encontrada.", exception.getMessage());

        Mockito.verifyNoInteractions(repository);
    }

    // Testes findAll

    @DisplayName("Deve buscar todas as internações com sucesso")
    @Test
    void shouldFindAllInternacoesSuccessfully()
    {
        Internacao primeiraInternacao = InternacaoTestBuilder.newInternacao().setId(1L).build();
        Internacao segundaInternacao = InternacaoTestBuilder.newInternacao().setId(2L).build();
        List<Internacao> internacoes = List.of(primeiraInternacao, segundaInternacao);

        Mockito.when(repository.findAll()).thenReturn(internacoes);

        List<Internacao> response = service.findAllInternacoes();

        assertAll(
                () -> assertSame(internacoes, response),
                () -> assertEquals(2, response.size()),
                () -> assertSame(primeiraInternacao, response.getFirst()),
                () -> assertSame(segundaInternacao, response.get(1))
        );

        Mockito.verify(repository).findAll();
    }

    @DisplayName("Deve retornar uma lista vazia quando não há internações")
    @Test
    void shouldReturnEmptyListWhenThereAreNoInternacoes()
    {
        List<Internacao> internacoes = List.of();

        Mockito.when(repository.findAll()).thenReturn(internacoes);

        List<Internacao> response = service.findAllInternacoes();

        assertAll(
                () -> assertSame(internacoes, response),
                () -> assertTrue(response.isEmpty())
        );

        Mockito.verify(repository).findAll();
    }

    // Testes findByAcolhidoId

    @DisplayName("Deve buscar internações pelo ID do acolhido com sucesso")
    @Test
    void shouldFindInternacoesByAcolhidoIdSuccessfully()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setId(1L).build();
        Internacao primeiraInternacao = InternacaoTestBuilder.newInternacao().setId(1L).setAcolhido(acolhido).build();
        Internacao segundaInternacao = InternacaoTestBuilder.newInternacao().setId(2L).setAcolhido(acolhido).build();
        List<Internacao> internacoes = List.of(primeiraInternacao, segundaInternacao);

        Mockito.when(acolhidoService.findAcolhidoById(1L)).thenReturn(acolhido);
        Mockito.when(repository.findByAcolhido(acolhido)).thenReturn(internacoes);

        List<Internacao> response = service.findInternacoesByAcolhidoId(1L);

        assertAll(
                () -> assertSame(internacoes, response),
                () -> assertEquals(2, response.size()),
                () -> assertSame(primeiraInternacao, response.getFirst()),
                () -> assertSame(segundaInternacao, response.get(1))
        );

        Mockito.verify(acolhidoService).findAcolhidoById(1L);
        Mockito.verify(repository).findByAcolhido(acolhido);
    }

    @DisplayName("Deve retornar uma lista vazia quando o acolhido não tem internações")
    @Test
    void shouldReturnEmptyListWhenAcolhidoHasNoInternacoes()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setId(1L).build();
        List<Internacao> internacoes = List.of();

        Mockito.when(acolhidoService.findAcolhidoById(1L)).thenReturn(acolhido);

        Mockito.when(repository.findByAcolhido(acolhido)).thenReturn(internacoes);

        List<Internacao> response = service.findInternacoesByAcolhidoId(1L);

        assertAll(
                () -> assertSame(internacoes, response),
                () -> assertTrue(response.isEmpty())
        );

        Mockito.verify(acolhidoService).findAcolhidoById(1L);
        Mockito.verify(repository).findByAcolhido(acolhido);
    }

    @DisplayName("Deve rejeitar ID inválido do acolhido quando buscando internações")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldRejectInvalidAcolhidoIdWhenFindingInternacoes(Long id)
    {
        InternacaoNotFoundException exception = assertThrows(InternacaoNotFoundException.class, () -> service.findInternacoesByAcolhidoId(id));

        assertEquals("Internações não encontradas.", exception.getMessage());

        Mockito.verifyNoInteractions(acolhidoService, repository);
    }

    // Testes findByConvenioId

    @DisplayName("Deve buscar internações pelo ID do convênio com sucesso")
    @Test
    void shouldFindInternacoesByConvenioIdSuccessfully()
    {
        Convenio convenio = new ConvenioTestBuilder().setId(1L).build();
        Internacao primeiraInternacao = InternacaoTestBuilder.newInternacao().setId(1L).setConvenio(convenio).build();
        Internacao segundaInternacao = InternacaoTestBuilder.newInternacao().setId(2L).setConvenio(convenio).build();
        List<Internacao> internacoes = List.of(primeiraInternacao, segundaInternacao);

        Mockito.when(convenioService.findConvenioById(1L)).thenReturn(convenio);
        Mockito.when(repository.findByConvenio(convenio)).thenReturn(internacoes);

        List<Internacao> response = service.findInternacoesByConvenioId(1L);

        assertAll(
                () -> assertSame(internacoes, response),
                () -> assertEquals(2, response.size()),
                () -> assertSame(primeiraInternacao, response.getFirst()),
                () -> assertSame(segundaInternacao, response.get(1))
        );

        Mockito.verify(convenioService).findConvenioById(1L);
        Mockito.verify(repository).findByConvenio(convenio);
    }

    @DisplayName("Deve retornar uma lista vazia quando o convênio não tem internações")
    @Test
    void shouldReturnEmptyListWhenConvenioHasNoInternacoes()
    {
        Convenio convenio = new ConvenioTestBuilder().setId(1L).build();
        List<Internacao> internacoes = List.of();

        Mockito.when(convenioService.findConvenioById(1L)).thenReturn(convenio);
        Mockito.when(repository.findByConvenio(convenio)).thenReturn(internacoes);

        List<Internacao> response = service.findInternacoesByConvenioId(1L);

        assertAll(
                () -> assertSame(internacoes, response),
                () -> assertTrue(response.isEmpty())
        );

        Mockito.verify(convenioService).findConvenioById(1L);
        Mockito.verify(repository).findByConvenio(convenio);
    }

    @DisplayName("Deve rejeitar ID inválido do convênio quando buscando internações")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldRejectInvalidConvenioIdWhenFindingInternacoes(Long id)
    {
        InternacaoNotFoundException exception = assertThrows(InternacaoNotFoundException.class, () -> service.findInternacoesByConvenioId(id));

        assertEquals("Internações não encontradas.", exception.getMessage());

        Mockito.verifyNoInteractions(convenioService, repository);
    }

    // Testes create

    @DisplayName("Deve criar internação com sucesso")
    @Test
    void shouldCreateInternacaoSuccessfully()
    {
        Acolhido acolhidoRecebido = AcolhidoTestBuilder.newAcolhido().setId(1L).build();
        Convenio convenioRecebido = new ConvenioTestBuilder().setId(1L).build();
        Internacao internacao = InternacaoTestBuilder.newInternacao().setAcolhido(acolhidoRecebido).setConvenio(convenioRecebido).buildForCreate();
        Acolhido acolhidoPersistido = AcolhidoTestBuilder.newAcolhido().setId(1L).build();
        Convenio convenioPersistido = new ConvenioTestBuilder().setId(1L).build();
        Internacao internacaoSalva = InternacaoTestBuilder.newInternacao().setId(1L).setAcolhido(acolhidoPersistido).setConvenio(convenioPersistido).build();

        Mockito.when(convenioService.findConvenioById(1L)).thenReturn(convenioPersistido);
        Mockito.when(acolhidoService.findAcolhidoById(1L)).thenReturn(acolhidoPersistido);
        Mockito.when(repository.existsByAcolhidoAndAlta(Mockito.same(acolhidoPersistido), Mockito.eq(AltaTypes.OBITO))).thenReturn(false);
        Mockito.when(repository.existsByAcolhidoAndAtivoTrue(Mockito.same(acolhidoPersistido))).thenReturn(false);
        Mockito.when(repository.countByConvenioAndAtivoTrue(Mockito.same(convenioPersistido))).thenReturn(19L);
        Mockito.when(repository.save(internacao)).thenReturn(internacaoSalva);

        Internacao response = service.createInternacao(internacao);

        assertSame(internacaoSalva, response);

        Mockito.verify(convenioService).findConvenioById(1L);
        Mockito.verify(acolhidoService).findAcolhidoById(1L);
        Mockito.verify(repository).existsByAcolhidoAndAlta(Mockito.same(acolhidoPersistido), Mockito.eq(AltaTypes.OBITO));
        Mockito.verify(repository).existsByAcolhidoAndAtivoTrue(Mockito.same(acolhidoPersistido));
        Mockito.verify(repository).countByConvenioAndAtivoTrue(Mockito.same(convenioPersistido));
        Mockito.verify(repository).save(Mockito.same(internacao));
    }

    @DisplayName("Deve rejeitar a criação da internação quando o acolhido tiver recebido alta por óbito")
    @Test
    void shouldRejectCreatingInternacaoWhenAcolhidoHasDeathDischarge()
    {
        Acolhido acolhidoRecebido = AcolhidoTestBuilder.newAcolhido().setId(1L).build();
        Acolhido acolhidoPersistido = AcolhidoTestBuilder.newAcolhido().setId(1L).build();
        Convenio convenioRecebido = new ConvenioTestBuilder().setId(1L).build();
        Convenio convenioPersistido = new ConvenioTestBuilder().setId(1L).build();
        Internacao internacao = InternacaoTestBuilder.newInternacao()
                .setAcolhido(acolhidoRecebido)
                .setConvenio(convenioRecebido)
                .buildForCreate();

        Mockito.when(convenioService.findConvenioById(1L)).thenReturn(convenioPersistido);
        Mockito.when(acolhidoService.findAcolhidoById(1L)).thenReturn(acolhidoPersistido);
        Mockito.when(repository.existsByAcolhidoAndAlta(acolhidoPersistido, AltaTypes.OBITO)).thenReturn(true);

        InternacaoWithInvalidInformationException exception = assertThrows(
                InternacaoWithInvalidInformationException.class,
                () -> service.createInternacao(internacao)
        );

        assertEquals("Acolhido se encontra em óbito.", exception.getMessage());

        Mockito.verify(convenioService).findConvenioById(1L);
        Mockito.verify(acolhidoService).findAcolhidoById(1L);
        Mockito.verify(repository).existsByAcolhidoAndAlta(acolhidoPersistido, AltaTypes.OBITO);
        Mockito.verify(repository, Mockito.never()).existsByAcolhidoAndAtivoTrue(Mockito.any(Acolhido.class));
        Mockito.verify(repository, Mockito.never()).countByConvenioAndAtivoTrue(Mockito.any(Convenio.class));
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Internacao.class));
        Mockito.verifyNoMoreInteractions(repository);
    }

    @DisplayName("Deve rejeitar internação nula sem interagir com as dependências")
    @Test
    void shouldRejectNullInternacaoWithoutInteractingWithDependencies()
    {
        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> service.createInternacao(null));

        assertEquals("Internação não pode ser nula.", exception.getMessage());

        Mockito.verifyNoInteractions(repository, acolhidoService, convenioService);
    }

    @DisplayName("Deve rejeitar internação quando convênio persistido é inativo")
    @Test
    void shouldRejectInternacaoWhenPersistedConvenioIsInactive()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setId(1L).build();
        Convenio convenioRecebido = new ConvenioTestBuilder().setId(1L).build();
        Convenio convenioPersistido = new ConvenioTestBuilder().setId(1L).setAtivo(false).build();
        Internacao internacao = InternacaoTestBuilder.newInternacao().setAcolhido(acolhido).setConvenio(convenioRecebido).buildForCreate();

        Mockito.when(convenioService.findConvenioById(1L)).thenReturn(convenioPersistido);
        Mockito.when(acolhidoService.findAcolhidoById(1L)).thenReturn(acolhido);
        Mockito.when(repository.existsByAcolhidoAndAlta(acolhido, AltaTypes.OBITO)).thenReturn(false);

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> service.createInternacao(internacao));

        assertEquals("Convênio inválido.", exception.getMessage());

        Mockito.verify(convenioService).findConvenioById(1L);
        Mockito.verify(acolhidoService).findAcolhidoById(1L);
        Mockito.verify(repository).existsByAcolhidoAndAlta(acolhido, AltaTypes.OBITO);
        Mockito.verifyNoMoreInteractions(repository);
    }

    @DisplayName("Deve rejeitar internação quando acolhido persistido é inativo")
    @Test
    void shouldRejectInternacaoWhenPersistedAcolhidoIsInactive()
    {
        Acolhido acolhidoRecebido = AcolhidoTestBuilder.newAcolhido().setId(1L).build();
        Acolhido acolhidoPersistido = AcolhidoTestBuilder.newAcolhido().setId(1L).build();
        acolhidoPersistido.deactivate();
        Convenio convenio = new ConvenioTestBuilder().setId(1L).build();
        Internacao internacao = InternacaoTestBuilder.newInternacao().setAcolhido(acolhidoRecebido).setConvenio(convenio).buildForCreate();

        Mockito.when(convenioService.findConvenioById(1L)).thenReturn(convenio);
        Mockito.when(acolhidoService.findAcolhidoById(1L)).thenReturn(acolhidoPersistido);
        Mockito.when(repository.existsByAcolhidoAndAlta(acolhidoPersistido, AltaTypes.OBITO)).thenReturn(false);

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> service.createInternacao(internacao));

        assertEquals("Acolhido inválido.", exception.getMessage());

        Mockito.verify(convenioService).findConvenioById(1L);
        Mockito.verify(acolhidoService).findAcolhidoById(1L);
        Mockito.verify(repository).existsByAcolhidoAndAlta(acolhidoPersistido, AltaTypes.OBITO);
        Mockito.verifyNoMoreInteractions(repository);
    }

    @DisplayName("Deve rejeitar internação quando acolhido já tem internação ativa")
    @Test
    void shouldRejectInternacaoWhenAcolhidoAlreadyHasActiveInternacao()
    {
        Acolhido acolhidoRecebido = AcolhidoTestBuilder.newAcolhido().setId(1L).build();
        Acolhido acolhidoPersistido = AcolhidoTestBuilder.newAcolhido().setId(1L).build();
        Convenio convenioRecebido = new ConvenioTestBuilder().setId(1L).build();
        Convenio convenioPersistido = new ConvenioTestBuilder().setId(1L).build();
        Internacao internacao = InternacaoTestBuilder.newInternacao().setAcolhido(acolhidoRecebido).setConvenio(convenioRecebido).buildForCreate();

        Mockito.when(convenioService.findConvenioById(1L)).thenReturn(convenioPersistido);
        Mockito.when(acolhidoService.findAcolhidoById(1L)).thenReturn(acolhidoPersistido);
        Mockito.when(repository.existsByAcolhidoAndAlta(acolhidoPersistido, AltaTypes.OBITO)).thenReturn(false);
        Mockito.when(repository.existsByAcolhidoAndAtivoTrue(Mockito.same(acolhidoPersistido))).thenReturn(true);

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> service.createInternacao(internacao));

        assertEquals("Acolhido já possui internação ativa.", exception.getMessage());

        Mockito.verify(convenioService).findConvenioById(1L);
        Mockito.verify(acolhidoService).findAcolhidoById(1L);
        Mockito.verify(repository).existsByAcolhidoAndAlta(acolhidoPersistido, AltaTypes.OBITO);
        Mockito.verify(repository).existsByAcolhidoAndAtivoTrue(Mockito.same(acolhidoPersistido));
        Mockito.verify(repository, Mockito.never()).countByConvenioAndAtivoTrue(Mockito.any(Convenio.class));
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Internacao.class));
    }

    @DisplayName("Deve rejeitar a internação quando o convênio atingir ou exceder a capacidade máxima")
    @ParameterizedTest
    @ValueSource(longs = {20L, 21L})
    void shouldRejectInternacaoWhenConvenioHasReachedOrExceededMaximumCapacity(long numeroInternacoesAtivas)
    {
        Acolhido acolhidoRecebido = AcolhidoTestBuilder.newAcolhido().setId(1L).build();
        Acolhido acolhidoPersistido = AcolhidoTestBuilder.newAcolhido().setId(1L).build();
        Convenio convenioRecebido = new ConvenioTestBuilder().setId(1L).setMaxVagas(20).build();
        Convenio convenioPersistido = new ConvenioTestBuilder().setId(1L).setMaxVagas(20).build();
        Internacao internacao = InternacaoTestBuilder.newInternacao().setAcolhido(acolhidoRecebido).setConvenio(convenioRecebido).buildForCreate();

        Mockito.when(convenioService.findConvenioById(1L)).thenReturn(convenioPersistido);
        Mockito.when(acolhidoService.findAcolhidoById(1L)).thenReturn(acolhidoPersistido);
        Mockito.when(repository.existsByAcolhidoAndAlta(acolhidoPersistido, AltaTypes.OBITO)).thenReturn(false);
        Mockito.when(repository.existsByAcolhidoAndAtivoTrue(Mockito.same(acolhidoPersistido))).thenReturn(false);
        Mockito.when(repository.countByConvenioAndAtivoTrue(Mockito.same(convenioPersistido))).thenReturn(numeroInternacoesAtivas);

        InternacaoWithInvalidInformationException exception = assertThrows(
                InternacaoWithInvalidInformationException.class,
                () -> service.createInternacao(internacao)
        );

        assertEquals("Vagas maxima excedente.", exception.getMessage());

        Mockito.verify(convenioService).findConvenioById(1L);
        Mockito.verify(acolhidoService).findAcolhidoById(1L);
        Mockito.verify(repository).existsByAcolhidoAndAlta(acolhidoPersistido, AltaTypes.OBITO);
        Mockito.verify(repository).existsByAcolhidoAndAtivoTrue(Mockito.same(acolhidoPersistido));
        Mockito.verify(repository).countByConvenioAndAtivoTrue(Mockito.same(convenioPersistido));
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Internacao.class));
    }

    // Testes update

    @DisplayName("Deve atualizar internação com sucesso e preservar a data imutável")
    @Test
    void shouldUpdateInternacaoSuccessfullyAndPreserveImmutableData()
    {
        Acolhido acolhidoPersistido = AcolhidoTestBuilder.newAcolhido().setId(1L).build();
        Convenio convenioPersistido = new ConvenioTestBuilder().setId(1L).build();
        LocalDate dataEntradaPersistida = LocalDate.of(2026, 1, 10);
        LocalDate novaDataSaidaPrevista = LocalDate.of(2026, 12, 10);
        BigDecimal novoValorMensalidade = new BigDecimal("1500.50");
        Internacao internacaoPersistida = InternacaoTestBuilder.newInternacao().setId(1L).setAcolhido(acolhidoPersistido).setConvenio(convenioPersistido).setDataEntrada(dataEntradaPersistida).setEtapaTratamento(TreatmentStage.EM_TRATAMENTO).setValorMensalidade(new BigDecimal("1200.00")).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(internacaoPersistida));
        Mockito.when(repository.save(Mockito.same(internacaoPersistida))).thenReturn(internacaoPersistida);

        Internacao response = service.updateInternacao(1L, novaDataSaidaPrevista, novoValorMensalidade);

        assertAll(
                () -> assertSame(internacaoPersistida, response),
                () -> assertEquals(novaDataSaidaPrevista, response.getDataSaidaPrevista()),
                () -> assertEquals(TreatmentStage.EM_TRATAMENTO, response.getEtapaTratamento()),
                () -> assertEquals(novoValorMensalidade, response.getValorMensalidade()),
                () -> assertSame(acolhidoPersistido, response.getAcolhido()),
                () -> assertSame(convenioPersistido, response.getConvenio()),
                () -> assertEquals(dataEntradaPersistida, response.getDataEntrada()),
                () -> assertNull(response.getDataSaida()),
                () -> assertNull(response.getAlta()),
                () -> assertTrue(response.getAtivo())
        );

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).save(Mockito.same(internacaoPersistida));
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    @DisplayName("Deve rejeitar a atualização quando internação é inativo")
    @Test
    void shouldRejectUpdateWhenInternacaoIsInactive()
    {
        Internacao internacaoPersistida = InternacaoTestBuilder.newInternacao().setId(1L).setAtivo(false).build();
        LocalDate novaDataSaidaPrevista = LocalDate.of(2026, 12, 10);
        BigDecimal novoValorMensalidade = new BigDecimal("1500.00");

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(internacaoPersistida));

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> service.updateInternacao(1L, novaDataSaidaPrevista, novoValorMensalidade));

        assertEquals("Internação encerrada não pode ser atualizada.", exception.getMessage());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Internacao.class));
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    @DisplayName("Deve rejeitar data de saída prevista nula durante a atualização")
    @Test
    void shouldRejectNullExpectedExitDateWhenUpdating()
    {
        Internacao internacaoPersistida = InternacaoTestBuilder.newInternacao().setId(1L).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(internacaoPersistida));

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> service.updateInternacao(1L, null, new BigDecimal("1500.00")));

        assertEquals("Os dados da internação são obrigatórios.", exception.getMessage());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Internacao.class));
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    @DisplayName("Deve rejeitar valor de mensalidade nulo durante a atualização")
    @Test
    void shouldRejectNullMonthlyPaymentWhenUpdating()
    {
        Internacao internacaoPersistida = InternacaoTestBuilder.newInternacao().setId(1L).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(internacaoPersistida));

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> service.updateInternacao(1L, LocalDate.of(2026, 12, 10), null));

        assertEquals("Os dados da internação são obrigatórios.", exception.getMessage());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Internacao.class));
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    @DisplayName("Deve rejeitar a atualização quando a internação não existir")
    @Test
    void shouldRejectUpdateWhenInternacaoDoesNotExist()
    {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        InternacaoNotFoundException exception = assertThrows(InternacaoNotFoundException.class, () -> service.updateInternacao(1L, LocalDate.of(2026, 12, 10), new BigDecimal("1500.00")));

        assertEquals("Internação não encontrada.", exception.getMessage());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Internacao.class));
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    // Testes endInternacao

    @DisplayName("Deve encerrar internação com sucesso")
    @Test
    void shouldEndInternacaoSuccessfully()
    {
        LocalDate dataEntrada = LocalDate.now().minusMonths(1);
        LocalDate dataSaida = LocalDate.now();
        Internacao internacao = InternacaoTestBuilder.newInternacao().setId(1L).setDataEntrada(dataEntrada).setDataSaidaPrevista(dataEntrada.plusMonths(9)).setAtivo(true).setDataSaida(null).setAlta(null).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(internacao));
        Mockito.when(repository.save(Mockito.same(internacao))).thenReturn(internacao);

        Internacao response = service.endInternacaoById(1L, AltaTypes.CONCLUSAO, dataSaida);

        assertAll(
                () -> assertSame(internacao, response),
                () -> assertEquals(dataSaida, internacao.getDataSaida()),
                () -> assertEquals(AltaTypes.CONCLUSAO, internacao.getAlta()),
                () -> assertFalse(internacao.getAtivo())
        );

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).save(Mockito.same(internacao));
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    @DisplayName("Deve completar uma internação inativa com informações de encerramento ausentes")
    @Test
    void shouldCompleteInactiveInternacaoWithMissingEndInformation()
    {
        LocalDate dataEntrada = LocalDate.now().minusMonths(1);
        LocalDate dataSaida = LocalDate.now();
        Internacao internacao = InternacaoTestBuilder.newInternacao().setId(1L).setDataEntrada(dataEntrada).setDataSaidaPrevista(dataEntrada.plusMonths(9)).setDataSaida(null).setAlta(null).setAtivo(false).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(internacao));
        Mockito.when(repository.save(Mockito.same(internacao))).thenReturn(internacao);

        Internacao response = service.endInternacaoById(1L, AltaTypes.DESISTENCIA, dataSaida);

        assertAll(
                () -> assertSame(internacao, response),
                () -> assertEquals(dataSaida, internacao.getDataSaida()),
                () -> assertEquals(AltaTypes.DESISTENCIA, internacao.getAlta()),
                () -> assertFalse(internacao.getAtivo())
        );

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).save(Mockito.same(internacao));
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    @DisplayName("Não deve salvar internação quando isso é já encerrado")
    @Test
    void shouldNotSaveInternacaoWhenItIsAlreadyEnded()
    {
        LocalDate dataEntrada = LocalDate.now().minusMonths(2);
        LocalDate primeiraDataSaida = LocalDate.now().minusDays(10);
        LocalDate novaDataSaida = LocalDate.now();

        Internacao internacao = InternacaoTestBuilder.newInternacao().setId(1L).setDataEntrada(dataEntrada).setDataSaidaPrevista(dataEntrada.plusMonths(9)).setDataSaida(primeiraDataSaida).setAlta(AltaTypes.CONCLUSAO).setAtivo(false).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(internacao));

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> service.endInternacaoById(1L, AltaTypes.DESLIGAMENTO, novaDataSaida));

        assertAll(
                () -> assertEquals(
                        "Internação já foi encerrada.",
                        exception.getMessage()
                ),
                () -> assertEquals(primeiraDataSaida, internacao.getDataSaida()),
                () -> assertEquals(AltaTypes.CONCLUSAO, internacao.getAlta()),
                () -> assertFalse(internacao.getAtivo())
        );

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Internacao.class));
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    @DisplayName("Não deve salvar a internação quando a data de saída é nula")
    @Test
    void shouldNotSaveInternacaoWhenExitDateIsNull()
    {
        Internacao internacao = InternacaoTestBuilder.newInternacao().setId(1L).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(internacao));

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> service.endInternacaoById(1L, AltaTypes.CONCLUSAO, null));

        assertAll(
                () -> assertEquals("Data de saída é obrigatória para gerar a alta.", exception.getMessage()),
                () -> assertNull(internacao.getDataSaida()),
                () -> assertNull(internacao.getAlta()),
                () -> assertTrue(internacao.getAtivo())
        );

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Internacao.class));
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    @DisplayName("Não deve salvar internação quando alta é nulo")
    @Test
    void shouldNotSaveInternacaoWhenAltaIsNull()
    {
        LocalDate dataSaida = LocalDate.now();
        Internacao internacao = InternacaoTestBuilder.newInternacao().setId(1L).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(internacao));

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> service.endInternacaoById(1L, null, dataSaida));

        assertAll(
                () -> assertEquals("Tipo é obrigatório para gerar a alta.", exception.getMessage()),
                () -> assertNull(internacao.getDataSaida()),
                () -> assertNull(internacao.getAlta()),
                () -> assertTrue(internacao.getAtivo())
        );

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Internacao.class));
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    @DisplayName("Deve rejeitar encerramento internação quando isso não existir")
    @Test
    void shouldRejectEndingInternacaoWhenItDoesNotExist()
    {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        InternacaoNotFoundException exception = assertThrows(InternacaoNotFoundException.class, () -> service.endInternacaoById(1L, AltaTypes.CONCLUSAO, LocalDate.now()));

        assertEquals("Internação não encontrada.", exception.getMessage());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Internacao.class));
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    @DisplayName("Deve rejeitar encerramento internação quando o ID é inválido")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldRejectEndingInternacaoWhenIdIsInvalid(Long id)
    {
        InternacaoNotFoundException exception = assertThrows(InternacaoNotFoundException.class, () -> service.endInternacaoById(id, AltaTypes.CONCLUSAO, LocalDate.now()));

        assertEquals("Internação não encontrada.", exception.getMessage());

        Mockito.verifyNoInteractions(repository, acolhidoService, convenioService);
    }

    // Testes delete

    @DisplayName("Deve excluir internação ativa com sucesso")
    @Test
    void shouldDeleteActiveInternacaoSuccessfully()
    {
        Internacao internacao = InternacaoTestBuilder.newInternacao().setId(1L).setAtivo(true).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(internacao));
        Mockito.when(pagamentoRepository.existsByInternacaoId(1L)).thenReturn(false);

        service.deleteInternacaoById(1L);

        var inOrder = Mockito.inOrder(repository, pagamentoRepository, audit);
        inOrder.verify(repository).findById(1L);
        inOrder.verify(pagamentoRepository).existsByInternacaoId(1L);
        inOrder.verify(audit).setCurrentActor();
        inOrder.verify(repository).delete(Mockito.same(internacao));
        inOrder.verify(repository).flush();
        inOrder.verify(audit).clearActor();
        Mockito.verifyNoMoreInteractions(repository, pagamentoRepository, audit);
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    @DisplayName("Deve excluir encerrado internação com sucesso")
    @Test
    void shouldDeleteEndedInternacaoSuccessfully()
    {
        LocalDate dataEntrada = LocalDate.now().minusMonths(2);
        LocalDate dataSaida = LocalDate.now().minusDays(1);

        Internacao internacao = InternacaoTestBuilder.newInternacao().setId(1L).setDataEntrada(dataEntrada).setDataSaidaPrevista(dataEntrada.plusMonths(9)).setDataSaida(dataSaida).setAlta(AltaTypes.CONCLUSAO).setAtivo(false).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(internacao));
        Mockito.when(pagamentoRepository.existsByInternacaoId(1L)).thenReturn(false);

        service.deleteInternacaoById(1L);

        var inOrder = Mockito.inOrder(repository, pagamentoRepository, audit);
        inOrder.verify(repository).findById(1L);
        inOrder.verify(pagamentoRepository).existsByInternacaoId(1L);
        inOrder.verify(audit).setCurrentActor();
        inOrder.verify(repository).delete(Mockito.same(internacao));
        inOrder.verify(repository).flush();
        inOrder.verify(audit).clearActor();
        Mockito.verifyNoMoreInteractions(repository, pagamentoRepository, audit);
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    @DisplayName("Deve rejeitar excluindo internação quando isso tem vinculado pagamentos")
    @Test
    void shouldRejectDeletingInternacaoWhenItHasLinkedPayments()
    {
        Internacao internacao = InternacaoTestBuilder.newInternacao().setId(1L).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(internacao));
        Mockito.when(pagamentoRepository.existsByInternacaoId(1L)).thenReturn(true);

        InternacaoWithInvalidInformationException exception = assertThrows(
                InternacaoWithInvalidInformationException.class,
                () -> service.deleteInternacaoById(1L)
        );

        assertEquals("Não é possível excluir a internação porque existem pagamentos vinculados.", exception.getMessage());

        var inOrder = Mockito.inOrder(repository, pagamentoRepository);
        inOrder.verify(repository).findById(1L);
        inOrder.verify(pagamentoRepository).existsByInternacaoId(1L);
        Mockito.verify(repository, Mockito.never()).delete(Mockito.any(Internacao.class));
        Mockito.verify(repository, Mockito.never()).flush();
        Mockito.verifyNoMoreInteractions(repository, pagamentoRepository);
        Mockito.verifyNoInteractions(audit, acolhidoService, convenioService);
    }

    @DisplayName("Deve rejeitar excluindo internação quando isso não existir")
    @Test
    void shouldRejectDeletingInternacaoWhenItDoesNotExist()
    {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        InternacaoNotFoundException exception = assertThrows(InternacaoNotFoundException.class, () -> service.deleteInternacaoById(1L));

        assertEquals("Internação não encontrada.", exception.getMessage());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).delete(Mockito.any(Internacao.class));
        Mockito.verifyNoInteractions(pagamentoRepository);
        Mockito.verifyNoInteractions(audit);
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    @DisplayName("Deve rejeitar excluindo internação quando o ID é inválido")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldRejectDeletingInternacaoWhenIdIsInvalid(Long id)
    {
        InternacaoNotFoundException exception = assertThrows(InternacaoNotFoundException.class, () -> service.deleteInternacaoById(id));

        assertEquals("Internação não encontrada.", exception.getMessage());

        Mockito.verifyNoInteractions(repository, pagamentoRepository, acolhidoService, convenioService, audit);
    }

    // Testes updateTreatmentStage

    @DisplayName("Deve atualizar tratamento etapa de todos ativo internações")
    @Test
    void shouldUpdateTreatmentStageOfAllActiveInternacoes()
    {
        LocalDate dataReferencia = LocalDate.of(2026, 10, 1);
        Internacao primeiraInternacao = Mockito.mock(Internacao.class);
        Internacao segundaInternacao = Mockito.mock(Internacao.class);
        List<Internacao> internacoesAtivas = List.of(primeiraInternacao, segundaInternacao);

        Mockito.when(repository.findByAtivoTrue()).thenReturn(internacoesAtivas);

        service.updateTreatmentStage(dataReferencia);

        Mockito.verify(repository).findByAtivoTrue();
        Mockito.verify(primeiraInternacao).updateTreatmentStage(dataReferencia);
        Mockito.verify(segundaInternacao).updateTreatmentStage(dataReferencia);
        Mockito.verify(repository).saveAll(Mockito.same(internacoesAtivas));
        Mockito.verifyNoMoreInteractions(repository, primeiraInternacao, segundaInternacao);

        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    @DisplayName("Deve salvar vazio listar quando não há ativo internações")
    @Test
    void shouldSaveEmptyListWhenThereAreNoActiveInternacoes()
    {
        LocalDate dataReferencia = LocalDate.of(2026, 10, 1);
        List<Internacao> internacoesAtivas = List.of();

        Mockito.when(repository.findByAtivoTrue()).thenReturn(internacoesAtivas);

        service.updateTreatmentStage(dataReferencia);

        Mockito.verify(repository).findByAtivoTrue();
        Mockito.verify(repository).saveAll(Mockito.same(internacoesAtivas));
        Mockito.verifyNoMoreInteractions(repository);
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    @DisplayName("Deve rejeitar nulo referência data sem interagir com as dependências")
    @Test
    void shouldRejectNullReferenceDateWithoutInteractingWithDependencies()
    {
        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> service.updateTreatmentStage(null));

        assertEquals("Data de referência é obrigatória para atualização so estagio de tratamento.", exception.getMessage());

        Mockito.verifyNoInteractions(repository, acolhidoService, convenioService);
    }

    // Testes countInternacoesAtivasByConvenio

    @DisplayName("Deve contar ativo internações por convênio")
    @ParameterizedTest
    @ValueSource(longs = {0, 1, 15})
    void shouldCountActiveInternacoesByConvenio(Long quantidade)
    {
        Convenio convenio = new ConvenioTestBuilder().setId(1L).build();

        Mockito.when(convenioService.findConvenioById(1L)).thenReturn(convenio);
        Mockito.when(repository.countByConvenioAndAtivoTrue(Mockito.same(convenio))).thenReturn(quantidade);

        Long response = service.countInternacoesAtivasByConvenio(1L);

        assertEquals(quantidade, response);

        Mockito.verify(convenioService).findConvenioById(1L);
        Mockito.verify(repository).countByConvenioAndAtivoTrue(Mockito.same(convenio));
        Mockito.verifyNoMoreInteractions(repository, convenioService);
        Mockito.verifyNoInteractions(acolhidoService);
    }

    @DisplayName("Deve contar ativo internações para inativo convênio")
    @Test
    void shouldCountActiveInternacoesForInactiveConvenio()
    {
        Convenio convenio = new ConvenioTestBuilder().setId(1L).setAtivo(false).build();

        Mockito.when(convenioService.findConvenioById(1L)).thenReturn(convenio);
        Mockito.when(repository.countByConvenioAndAtivoTrue(Mockito.same(convenio))).thenReturn(0L);

        Long response = service.countInternacoesAtivasByConvenio(1L);

        assertEquals(0L, response);

        Mockito.verify(convenioService).findConvenioById(1L);
        Mockito.verify(repository).countByConvenioAndAtivoTrue(Mockito.same(convenio));
        Mockito.verifyNoMoreInteractions(repository, convenioService);
        Mockito.verifyNoInteractions(acolhidoService);
    }

    @DisplayName("Não deve contar quando convênio não existir")
    @Test
    void shouldNotCountWhenConvenioDoesNotExist()
    {
        ConvenioNotFoundException expected = new ConvenioNotFoundException("Convênio não encontrado.");

        Mockito.when(convenioService.findConvenioById(99L)).thenThrow(expected);

        ConvenioNotFoundException exception = assertThrows(ConvenioNotFoundException.class, () -> service.countInternacoesAtivasByConvenio(99L));

        assertSame(expected, exception);

        Mockito.verify(convenioService).findConvenioById(99L);
        Mockito.verifyNoMoreInteractions(convenioService);
        Mockito.verifyNoInteractions(repository, acolhidoService);
    }

    @DisplayName("Não deve contar quando convênio ID é inválido")
    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldNotCountWhenConvenioIdIsInvalid(Long id)
    {
        Mockito.when(convenioService.findConvenioById(id)).thenThrow(new ConvenioNotFoundException("Convênio não encontrado."));

        ConvenioNotFoundException exception = assertThrows(ConvenioNotFoundException.class, () -> service.countInternacoesAtivasByConvenio(id));

        assertEquals("Convênio não encontrado.", exception.getMessage());

        Mockito.verify(convenioService).findConvenioById(id);
        Mockito.verifyNoMoreInteractions(convenioService);
        Mockito.verifyNoInteractions(repository, acolhidoService);
    }

    // Testes countTotalInternacao

    @DisplayName("Deve retornar total internação contar de repositório")
    @ParameterizedTest
    @ValueSource(longs = {0L, 1L, 12L})
    void shouldReturnTotalInternacaoCountFromRepository(long quantidade)
    {
        Mockito.when(repository.count()).thenReturn(quantidade);

        Long response = service.countTotalInternacao();

        assertEquals(quantidade, response);
        Mockito.verify(repository).count();
        Mockito.verifyNoMoreInteractions(repository);
        Mockito.verifyNoInteractions(pagamentoRepository, acolhidoService, convenioService, audit);
    }

    @DisplayName("Deve retornar internação ativa contar de repositório")
    @ParameterizedTest
    @ValueSource(longs = {0L, 1L, 12L})
    void shouldReturnActiveInternacaoCountFromRepository(long quantidade)
    {
        Mockito.when(repository.countByAtivoTrue()).thenReturn(quantidade);

        Long response = service.countTotalInternacaoAtiva();

        assertEquals(quantidade, response);
        Mockito.verify(repository).countByAtivoTrue();
        Mockito.verifyNoMoreInteractions(repository);
        Mockito.verifyNoInteractions(pagamentoRepository, acolhidoService, convenioService, audit);
    }

    @DisplayName("Deve retornar internação ativa contar por tratamento etapa")
    @ParameterizedTest
    @EnumSource(TreatmentStage.class)
    void shouldReturnActiveInternacaoCountByTreatmentStage(TreatmentStage stage)
    {
        Mockito.when(repository.countByEtapaTratamentoAndAtivoTrue(stage)).thenReturn(3L);

        Long response = service.countTotalIntenacoesByTreatmentStage(stage);

        assertEquals(3L, response);
        Mockito.verify(repository).countByEtapaTratamentoAndAtivoTrue(stage);
        Mockito.verifyNoMoreInteractions(repository);
        Mockito.verifyNoInteractions(pagamentoRepository, acolhidoService, convenioService, audit);
    }

    @DisplayName("Deve lançar internação inválido consulta exceção quando contagem por nulo tratamento etapa")
    @Test
    void shouldThrowInternacaoInvalidQueryExceptionWhenCountingByNullTreatmentStage()
    {
        InternacaoInvalidQueryException exception = assertThrows(
                InternacaoInvalidQueryException.class,
                () -> service.countTotalIntenacoesByTreatmentStage(null)
        );

        assertEquals("Estagio de tratamento deve ser informado.", exception.getMessage());
        Mockito.verifyNoInteractions(repository, pagamentoRepository, acolhidoService, convenioService, audit);
    }

    // Testes findReportMonthlyMovement

    @DisplayName("Deve combinar mensalidade entradas e altas e incluir meses sem movimentação")
    @Test
    void shouldCombineMonthlyEntriesAndDischargesAndIncludeMonthsWithoutMovement()
    {
        LocalDate start = LocalDate.of(2025, 12, 10);
        LocalDate end = LocalDate.of(2026, 3, 20);

        Mockito.when(repository.countEntradasGroupByMonth(start, end)).thenReturn(List.of(
                new InternacaoMonthlyCountProjection(2025, 12, 3L),
                new InternacaoMonthlyCountProjection(2026, 2, 2L)
        ));
        Mockito.when(repository.countSaidasGroupByMonth(start, end)).thenReturn(List.of(
                new InternacaoMonthlyCountProjection(2026, 1, 1L),
                new InternacaoMonthlyCountProjection(2026, 2, 4L)
        ));

        List<InternacaoMonthlyMovement> response = service.findReportMonthlyMovement(start, end);

        assertEquals(List.of(
                new InternacaoMonthlyMovement(2025, 12, 3L, 0L),
                new InternacaoMonthlyMovement(2026, 1, 0L, 1L),
                new InternacaoMonthlyMovement(2026, 2, 2L, 4L),
                new InternacaoMonthlyMovement(2026, 3, 0L, 0L)
        ), response);

        Mockito.verify(repository).countEntradasGroupByMonth(start, end);
        Mockito.verify(repository).countSaidasGroupByMonth(start, end);
        Mockito.verifyNoMoreInteractions(repository);
        Mockito.verifyNoInteractions(pagamentoRepository, acolhidoService, convenioService, audit);
    }

    @DisplayName("Deve retornar todos meses com zero conta quando não há mensalidade movimentações")
    @Test
    void shouldReturnAllMonthsWithZeroCountsWhenThereAreNoMonthlyMovements()
    {
        LocalDate start = LocalDate.of(2026, 5, 1);
        LocalDate end = LocalDate.of(2026, 7, 31);

        Mockito.when(repository.countEntradasGroupByMonth(start, end)).thenReturn(List.of());
        Mockito.when(repository.countSaidasGroupByMonth(start, end)).thenReturn(List.of());

        List<InternacaoMonthlyMovement> response = service.findReportMonthlyMovement(start, end);

        assertEquals(List.of(
                new InternacaoMonthlyMovement(2026, 5, 0L, 0L),
                new InternacaoMonthlyMovement(2026, 6, 0L, 0L),
                new InternacaoMonthlyMovement(2026, 7, 0L, 0L)
        ), response);

        Mockito.verify(repository).countEntradasGroupByMonth(start, end);
        Mockito.verify(repository).countSaidasGroupByMonth(start, end);
        Mockito.verifyNoMoreInteractions(repository);
        Mockito.verifyNoInteractions(pagamentoRepository, acolhidoService, convenioService, audit);
    }

    @DisplayName("Deve lançar internação inválido consulta exceção quando mensalidade movimentação período contém nulo")
    @Test
    void shouldThrowInternacaoInvalidQueryExceptionWhenMonthlyMovementPeriodContainsNull()
    {
        LocalDate date = LocalDate.of(2026, 1, 1);

        InternacaoInvalidQueryException nullStartException = assertThrows(
                InternacaoInvalidQueryException.class,
                () -> service.findReportMonthlyMovement(null, date)
        );
        InternacaoInvalidQueryException nullEndException = assertThrows(
                InternacaoInvalidQueryException.class,
                () -> service.findReportMonthlyMovement(date, null)
        );

        assertAll(
                () -> assertEquals("Datas não podem ser nulas.", nullStartException.getMessage()),
                () -> assertEquals("Datas não podem ser nulas.", nullEndException.getMessage())
        );
        Mockito.verifyNoInteractions(repository);
    }

    @DisplayName("Deve lançar internação inválido consulta exceção quando mensalidade movimentação inicia antes 1900")
    @Test
    void shouldThrowInternacaoInvalidQueryExceptionWhenMonthlyMovementStartsBefore1900()
    {
        LocalDate start = LocalDate.of(1899, 12, 31);
        LocalDate end = LocalDate.of(1900, 1, 1);

        InternacaoInvalidQueryException exception = assertThrows(
                InternacaoInvalidQueryException.class,
                () -> service.findReportMonthlyMovement(start, end)
        );

        assertEquals("Data de início não pode ser anterior a 1900.", exception.getMessage());
        Mockito.verifyNoInteractions(repository);
    }

    @DisplayName("Deve lançar internação inválido consulta exceção quando mensalidade movimentação iniciar é depois encerrar")
    @Test
    void shouldThrowInternacaoInvalidQueryExceptionWhenMonthlyMovementStartIsAfterEnd()
    {
        LocalDate start = LocalDate.of(2026, 3, 1);
        LocalDate end = LocalDate.of(2026, 2, 28);

        InternacaoInvalidQueryException exception = assertThrows(
                InternacaoInvalidQueryException.class,
                () -> service.findReportMonthlyMovement(start, end)
        );

        assertEquals("Data de início não pode ser posterior à data de fim.", exception.getMessage());
        Mockito.verifyNoInteractions(repository);
    }

    // Testes findRelatorioInternacoesAtivasPorConvenio

    @DisplayName("Deve buscar ativo internações relatório por convênio")
    @Test
    void shouldFindActiveInternacoesReportByConvenio()
    {
        List<InternacoesAtivasByConvenio> relatorio = List.of(
                new InternacoesAtivasByConvenio(1L, "Social", 12L),
                new InternacoesAtivasByConvenio(2L, "Particular", 0L)
        );

        Mockito.when(repository.countInternacoesAtivasPorConvenio()).thenReturn(relatorio);

        List<InternacoesAtivasByConvenio> response = service.findRelatorioInternacoesAtivasPorConvenio();

        assertEquals(relatorio, response);

        Mockito.verify(repository).countInternacoesAtivasPorConvenio();
        Mockito.verifyNoMoreInteractions(repository);
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    @DisplayName("Deve retornar vazio relatório quando não há convênios")
    @Test
    void shouldReturnEmptyReportWhenThereAreNoConvenios()
    {
        Mockito.when(repository.countInternacoesAtivasPorConvenio()).thenReturn(List.of());

        List<InternacoesAtivasByConvenio> response = service.findRelatorioInternacoesAtivasPorConvenio();

        assertTrue(response.isEmpty());

        Mockito.verify(repository).countInternacoesAtivasPorConvenio();
        Mockito.verifyNoMoreInteractions(repository);
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    // Testes findRelatorioSituacaoInternacoesPorConvenio

    @DisplayName("Deve buscar internações situação relatório por convênio")
    @Test
    void shouldFindInternacoesSituationReportByConvenio()
    {
        List<TreatmentStageByConvenio> relatorio = List.of(
                new TreatmentStageByConvenio(1L, "Social", 8L, 3L, 1L, 12L, 24L),
                new TreatmentStageByConvenio(2L, "Particular", 0L, 0L, 0L, 0L, 0L)
        );

        Mockito.when(repository.findTreatmentStageByConvenio()).thenReturn(relatorio);

        List<TreatmentStageByConvenio> response = service.findRelatorioSituacaoInternacoesPorConvenio();

        assertEquals(relatorio, response);

        Mockito.verify(repository).findTreatmentStageByConvenio();
        Mockito.verifyNoMoreInteractions(repository);
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    @DisplayName("Deve retornar vazio situação relatório quando não há convênios")
    @Test
    void shouldReturnEmptySituationReportWhenThereAreNoConvenios()
    {
        Mockito.when(repository.findTreatmentStageByConvenio()).thenReturn(List.of());

        List<TreatmentStageByConvenio> response = service.findRelatorioSituacaoInternacoesPorConvenio();

        assertTrue(response.isEmpty());

        Mockito.verify(repository).findTreatmentStageByConvenio();
        Mockito.verifyNoMoreInteractions(repository);
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    // Testes findInternacaoByAcolhidoName

    @DisplayName("Deve buscar internações por acolhido nome e retornar página")
    @Test
    void shouldFindInternacoesByAcolhidoNameAndReturnPage()
    {
        Pageable pageable = PageRequest.of(1, 2);
        List<Internacao> internacoes = List.of(InternacaoTestBuilder.newInternacao().setId(3L).build(), InternacaoTestBuilder.newInternacao().setId(4L).build());
        Page<Internacao> paginaEsperada = new PageImpl<>(internacoes, pageable, 5);

        Mockito.when(repository.findInternacaoByAcolhido_NomeContainingIgnoreCase("Ana", pageable)).thenReturn(paginaEsperada);

        Page<Internacao> response = service.findInternacaoByAcolhidoName(" Ana ", pageable);

        assertAll(
                () -> assertSame(paginaEsperada, response),
                () -> assertEquals(2, response.getContent().size()),
                () -> assertEquals(5, response.getTotalElements()),
                () -> assertEquals(3, response.getTotalPages()),
                () -> assertEquals(1, response.getNumber()),
                () -> assertEquals(2, response.getSize())
        );

        Mockito.verify(repository).findInternacaoByAcolhido_NomeContainingIgnoreCase("Ana", pageable);
        Mockito.verifyNoMoreInteractions(repository);
    }

    @DisplayName("Deve lançar exceção quando a internação não for encontrada quando acolhido nome é nulo ou em branco")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "\t"})
    void shouldThrowInternacaoNotFoundExceptionWhenAcolhidoNameIsNullOrBlank(String nome)
    {
        InternacaoNotFoundException exception = assertThrows(InternacaoNotFoundException.class, () -> service.findInternacaoByAcolhidoName(nome, PageRequest.of(0, 10)));

        assertEquals("Internação não encontrada.", exception.getMessage());
        Mockito.verifyNoInteractions(repository);
    }

    @DisplayName("Deve retornar uma página vazia quando nenhuma internação corresponder ao nome do acolhido")
    @Test
    void shouldReturnEmptyPageWhenNoInternacoesMatchAcolhidoName()
    {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Internacao> paginaVazia = Page.empty(pageable);

        Mockito.when(repository.findInternacaoByAcolhido_NomeContainingIgnoreCase("Maria", pageable)).thenReturn(paginaVazia);

        Page<Internacao> response = service.findInternacaoByAcolhidoName("Maria", pageable);

        assertAll(
                () -> assertSame(paginaVazia, response),
                () -> assertTrue(response.isEmpty()),
                () -> assertEquals(0, response.getTotalElements()),
                () -> assertEquals(0, response.getTotalPages())
        );

        Mockito.verify(repository).findInternacaoByAcolhido_NomeContainingIgnoreCase("Maria", pageable);
        Mockito.verifyNoMoreInteractions(repository);
    }

    // Testes findInternacaoByAcolhidoCpf

    @DisplayName("Deve buscar internações por parcial acolhido CPF e retornar página")
    @Test
    void shouldFindInternacoesByPartialAcolhidoCpfAndReturnPage()
    {
        String cpf = "390.533";
        Pageable pageable = PageRequest.of(1, 2);
        List<Internacao> internacoes = List.of(InternacaoTestBuilder.newInternacao().setId(3L).build(), InternacaoTestBuilder.newInternacao().setId(4L).build());
        Page<Internacao> paginaEsperada = new PageImpl<>(internacoes, pageable, 5);

        Mockito.when(repository.findInternacaoByAcolhido_CpfContainingIgnoreCase("390533", pageable)).thenReturn(paginaEsperada);

        Page<Internacao> response = service.findInternacaoByAcolhidoCpf(cpf, pageable);

        assertAll(
                () -> assertSame(paginaEsperada, response),
                () -> assertEquals(2, response.getContent().size()),
                () -> assertEquals(5, response.getTotalElements()),
                () -> assertEquals(3, response.getTotalPages()),
                () -> assertEquals(1, response.getNumber()),
                () -> assertEquals(2, response.getSize())
        );

        Mockito.verify(repository).findInternacaoByAcolhido_CpfContainingIgnoreCase("390533", pageable);
        Mockito.verifyNoMoreInteractions(repository);
    }

    @DisplayName("Deve lançar exceção quando a internação não for encontrada quando acolhido CPF é nulo em branco ou normaliza para em branco")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "\t", ".", "-", " .- "})
    void shouldThrowInternacaoNotFoundExceptionWhenAcolhidoCpfIsNullBlankOrNormalizesToBlank(String cpf)
    {
        InternacaoNotFoundException exception = assertThrows(InternacaoNotFoundException.class, () -> service.findInternacaoByAcolhidoCpf(cpf, PageRequest.of(0, 10)));

        assertEquals("Internação não encontrada.", exception.getMessage());
        Mockito.verifyNoInteractions(repository);
    }

    @DisplayName("Deve retornar uma página vazia quando nenhuma internação corresponder ao CPF do acolhido")
    @Test
    void shouldReturnEmptyPageWhenNoInternacoesMatchAcolhidoCpf()
    {
        String cpf = "390.533";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Internacao> paginaVazia = Page.empty(pageable);

        Mockito.when(repository.findInternacaoByAcolhido_CpfContainingIgnoreCase("390533", pageable)).thenReturn(paginaVazia);

        Page<Internacao> response = service.findInternacaoByAcolhidoCpf(cpf, pageable);

        assertAll(
                () -> assertSame(paginaVazia, response),
                () -> assertTrue(response.isEmpty()),
                () -> assertEquals(0, response.getTotalElements()),
                () -> assertEquals(0, response.getTotalPages())
        );

        Mockito.verify(repository).findInternacaoByAcolhido_CpfContainingIgnoreCase("390533", pageable);
        Mockito.verifyNoMoreInteractions(repository);
    }

    // Testes findInternacaoByTreatmentStage

    @DisplayName("Deve buscar internações por tratamento etapa e retornar página")
    @Test
    void shouldFindInternacoesByTreatmentStageAndReturnPage()
    {
        TreatmentStage stage = TreatmentStage.PROXIMO_ALTA;
        Pageable pageable = PageRequest.of(1, 2);
        List<Internacao> internacoes = List.of(
                InternacaoTestBuilder.newInternacao().setId(3L).setEtapaTratamento(stage).build(),
                InternacaoTestBuilder.newInternacao().setId(4L).setEtapaTratamento(stage).build()
        );
        Page<Internacao> paginaEsperada = new PageImpl<>(internacoes, pageable, 5);

        Mockito.when(repository.findInternacaoByEtapaTratamento(stage, pageable)).thenReturn(paginaEsperada);

        Page<Internacao> response = service.findInternacaoByTreatmentStage(stage, pageable);

        assertAll(
                () -> assertSame(paginaEsperada, response),
                () -> assertEquals(2, response.getContent().size()),
                () -> assertEquals(5, response.getTotalElements()),
                () -> assertEquals(3, response.getTotalPages()),
                () -> assertEquals(1, response.getNumber()),
                () -> assertEquals(2, response.getSize()),
                () -> assertTrue(response.getContent().stream().allMatch(internacao -> internacao.getEtapaTratamento() == stage))
        );

        Mockito.verify(repository).findInternacaoByEtapaTratamento(stage, pageable);
        Mockito.verifyNoMoreInteractions(repository);
    }

    @DisplayName("Deve lançar internação inválido consulta exceção quando tratamento etapa é nulo")
    @Test
    void shouldThrowInternacaoInvalidQueryExceptionWhenTreatmentStageIsNull()
    {
        InternacaoInvalidQueryException exception = assertThrows(
                InternacaoInvalidQueryException.class,
                () -> service.findInternacaoByTreatmentStage(null, PageRequest.of(0, 10))
        );

        assertEquals("Estagio do tratamento não pode ser nulo.", exception.getMessage());
        Mockito.verifyNoInteractions(repository);
    }

    @DisplayName("Deve retornar uma página vazia quando nenhuma internação corresponder à etapa do tratamento")
    @Test
    void shouldReturnEmptyPageWhenNoInternacoesMatchTreatmentStage()
    {
        TreatmentStage stage = TreatmentStage.ALTA_CONCEDIDA;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Internacao> paginaVazia = Page.empty(pageable);

        Mockito.when(repository.findInternacaoByEtapaTratamento(stage, pageable)).thenReturn(paginaVazia);

        Page<Internacao> response = service.findInternacaoByTreatmentStage(stage, pageable);

        assertAll(
                () -> assertSame(paginaVazia, response),
                () -> assertTrue(response.isEmpty()),
                () -> assertEquals(0, response.getTotalElements()),
                () -> assertEquals(0, response.getTotalPages())
        );

        Mockito.verify(repository).findInternacaoByEtapaTratamento(stage, pageable);
        Mockito.verifyNoMoreInteractions(repository);
    }

    // Testes findInternacaoByDataEntrada

    @DisplayName("Deve buscar internações dentro data entrada intervalo e retornar página")
    @Test
    void shouldFindInternacoesWithinDataEntradaRangeAndReturnPage()
    {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fim = LocalDate.of(2026, 3, 31);
        Pageable pageable = PageRequest.of(0, 10);
        List<Internacao> internacoes = List.of(
                InternacaoTestBuilder.newInternacao().setId(3L).build(),
                InternacaoTestBuilder.newInternacao().setId(4L).build()
        );
        Page<Internacao> paginaEsperada = new PageImpl<>(internacoes, pageable, 2);

        Mockito.when(repository.findInternacaoByDataEntradaBetween(inicio, fim, pageable))
                .thenReturn(paginaEsperada);

        Page<Internacao> response = service.findInternacaoByDataEntrada(inicio, fim, pageable);

        assertAll(
                () -> assertSame(paginaEsperada, response),
                () -> assertEquals(2, response.getContent().size()),
                () -> assertEquals(2, response.getTotalElements()),
                () -> assertEquals(1, response.getTotalPages()),
                () -> assertEquals(0, response.getNumber()),
                () -> assertEquals(10, response.getSize())
        );

        Mockito.verify(repository).findInternacaoByDataEntradaBetween(inicio, fim, pageable);
        Mockito.verifyNoMoreInteractions(repository);
    }

    @DisplayName("Deve lançar internação inválido consulta exceção quando data intervalo contém nulo")
    @Test
    void shouldThrowInternacaoInvalidQueryExceptionWhenDateRangeContainsNull()
    {
        LocalDate data = LocalDate.of(2026, 1, 1);
        Pageable pageable = PageRequest.of(0, 10);

        InternacaoInvalidQueryException nullStartException = assertThrows(
                InternacaoInvalidQueryException.class,
                () -> service.findInternacaoByDataEntrada(null, data, pageable)
        );
        InternacaoInvalidQueryException nullEndException = assertThrows(
                InternacaoInvalidQueryException.class,
                () -> service.findInternacaoByDataEntrada(data, null, pageable)
        );

        assertAll(
                () -> assertEquals("Datas não podem ser nulas.", nullStartException.getMessage()),
                () -> assertEquals("Datas não podem ser nulas.", nullEndException.getMessage())
        );
        Mockito.verifyNoInteractions(repository);
    }

    @DisplayName("Deve lançar internação inválido consulta exceção quando iniciar data é antes 1900")
    @Test
    void shouldThrowInternacaoInvalidQueryExceptionWhenStartDateIsBefore1900()
    {
        LocalDate inicio = LocalDate.of(1899, 12, 31);
        LocalDate fim = LocalDate.of(1900, 1, 1);

        InternacaoInvalidQueryException exception = assertThrows(
                InternacaoInvalidQueryException.class,
                () -> service.findInternacaoByDataEntrada(inicio, fim, PageRequest.of(0, 10))
        );

        assertEquals("Data de inicio não pode ser anterior a 1900.", exception.getMessage());
        Mockito.verifyNoInteractions(repository);
    }

    @DisplayName("Deve lançar internação inválido consulta exceção quando iniciar data é depois encerrar data")
    @Test
    void shouldThrowInternacaoInvalidQueryExceptionWhenStartDateIsAfterEndDate()
    {
        LocalDate inicio = LocalDate.of(2026, 3, 1);
        LocalDate fim = LocalDate.of(2026, 2, 28);

        InternacaoInvalidQueryException exception = assertThrows(
                InternacaoInvalidQueryException.class,
                () -> service.findInternacaoByDataEntrada(inicio, fim, PageRequest.of(0, 10))
        );

        assertEquals("Data de inicio não pode ser posterior a data de fim.", exception.getMessage());
        Mockito.verifyNoInteractions(repository);
    }

    @DisplayName("Deve retornar uma página vazia quando nenhuma internação corresponder ao intervalo de datas de entrada")
    @Test
    void shouldReturnEmptyPageWhenNoInternacoesMatchDataEntradaRange()
    {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fim = LocalDate.of(2026, 3, 31);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Internacao> paginaVazia = Page.empty(pageable);

        Mockito.when(repository.findInternacaoByDataEntradaBetween(inicio, fim, pageable))
                .thenReturn(paginaVazia);

        Page<Internacao> response = service.findInternacaoByDataEntrada(inicio, fim, pageable);

        assertAll(
                () -> assertSame(paginaVazia, response),
                () -> assertTrue(response.isEmpty()),
                () -> assertEquals(0, response.getTotalElements()),
                () -> assertEquals(0, response.getTotalPages())
        );

        Mockito.verify(repository).findInternacaoByDataEntradaBetween(inicio, fim, pageable);
        Mockito.verifyNoMoreInteractions(repository);
    }


}
