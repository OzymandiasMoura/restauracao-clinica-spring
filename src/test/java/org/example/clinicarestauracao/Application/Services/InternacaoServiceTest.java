package org.example.clinicarestauracao.Application.Services;

import org.example.clinicarestauracao.Application.Exceptions.Internacao.InternacaoNotFoundException;
import org.example.clinicarestauracao.Application.Exceptions.Internacao.InternacaoWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Interfaces.InternacaoRepository;
import org.example.clinicarestauracao.Builders.AcolhidoTestBuilder;
import org.example.clinicarestauracao.Builders.ConvenioTestBuilder;
import org.example.clinicarestauracao.Builders.InternacaoTestBuilder;
import org.example.clinicarestauracao.Domain.Entities.Acolhido;
import org.example.clinicarestauracao.Domain.Entities.Convenio;
import org.example.clinicarestauracao.Domain.Entities.Internacao;
import org.example.clinicarestauracao.Domain.Enums.AltaTypes;
import org.example.clinicarestauracao.Domain.Enums.TreatmentStage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private AcolhidoService acolhidoService;
    @Mock
    private ConvenioService convenioService;
    @InjectMocks
    private InternacaoService service;

    // Testes findById

    @Test
    void shouldFindInternacaoByIdSuccessfully()
    {
        Internacao internacao = InternacaoTestBuilder.newInternacao().build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(internacao));

        Internacao response = service.findInternacaoById(1L);

        assertSame(internacao, response);

        Mockito.verify(repository).findById(1L);
    }

    @Test
    void shouldThrowInternacaoNotFoundExceptionWhenInternacaoDoesNotExist()
    {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        InternacaoNotFoundException exception = assertThrows(InternacaoNotFoundException.class, () -> service.findInternacaoById(1L));

        assertEquals("Internação não encontrada.", exception.getMessage());

        Mockito.verify(repository).findById(1L);
    }

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
        Mockito.when(repository.existsByAcolhidoAndAtivoTrue(Mockito.same(acolhidoPersistido))).thenReturn(false);
        Mockito.when(repository.save(internacao)).thenReturn(internacaoSalva);

        Internacao response = service.createInternacao(internacao);

        assertSame(internacaoSalva, response);

        Mockito.verify(convenioService).findConvenioById(1L);
        Mockito.verify(acolhidoService).findAcolhidoById(1L);
        Mockito.verify(repository).existsByAcolhidoAndAtivoTrue(Mockito.same(acolhidoPersistido));
        Mockito.verify(repository).save(Mockito.same(internacao));
    }

    @Test
    void shouldRejectNullInternacaoWithoutInteractingWithDependencies()
    {
        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> service.createInternacao(null));

        assertEquals("Internação não pode ser nula.", exception.getMessage());

        Mockito.verifyNoInteractions(repository, acolhidoService, convenioService);
    }

    @Test
    void shouldRejectInternacaoWhenPersistedConvenioIsInactive()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setId(1L).build();
        Convenio convenioRecebido = new ConvenioTestBuilder().setId(1L).build();
        Convenio convenioPersistido = new ConvenioTestBuilder().setId(1L).setAtivo(false).build();
        Internacao internacao = InternacaoTestBuilder.newInternacao().setAcolhido(acolhido).setConvenio(convenioRecebido).buildForCreate();

        Mockito.when(convenioService.findConvenioById(1L)).thenReturn(convenioPersistido);
        Mockito.when(acolhidoService.findAcolhidoById(1L)).thenReturn(acolhido);

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> service.createInternacao(internacao));

        assertEquals("Convênio inválido.", exception.getMessage());

        Mockito.verify(convenioService).findConvenioById(1L);
        Mockito.verify(acolhidoService).findAcolhidoById(1L);
        Mockito.verifyNoInteractions(repository);
    }

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

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> service.createInternacao(internacao));

        assertEquals("Acolhido inválido.", exception.getMessage());

        Mockito.verify(convenioService).findConvenioById(1L);
        Mockito.verify(acolhidoService).findAcolhidoById(1L);
        Mockito.verifyNoInteractions(repository);
    }

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
        Mockito.when(repository.existsByAcolhidoAndAtivoTrue(Mockito.same(acolhidoPersistido))).thenReturn(true);

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> service.createInternacao(internacao));

        assertEquals("Acolhido já possui internação ativa.", exception.getMessage());

        Mockito.verify(convenioService).findConvenioById(1L);
        Mockito.verify(acolhidoService).findAcolhidoById(1L);
        Mockito.verify(repository).existsByAcolhidoAndAtivoTrue(Mockito.same(acolhidoPersistido));
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Internacao.class));
    }

    // Testes update

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

        Internacao response = service.updateInternacao(1L, novaDataSaidaPrevista, TreatmentStage.PROXIMO_ALTA, novoValorMensalidade);

        assertAll(
                () -> assertSame(internacaoPersistida, response),
                () -> assertEquals(novaDataSaidaPrevista, response.getDataSaidaPrevista()),
                () -> assertEquals(TreatmentStage.PROXIMO_ALTA, response.getEtapaTratamento()),
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

    @Test
    void shouldRejectUpdateWhenInternacaoIsInactive()
    {
        Internacao internacaoPersistida = InternacaoTestBuilder.newInternacao().setId(1L).setAtivo(false).build();
        LocalDate novaDataSaidaPrevista = LocalDate.of(2026, 12, 10);
        BigDecimal novoValorMensalidade = new BigDecimal("1500.00");

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(internacaoPersistida));

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> service.updateInternacao(1L, novaDataSaidaPrevista, TreatmentStage.PROXIMO_ALTA, novoValorMensalidade));

        assertEquals("Internação encerrada não pode ser atualizada.", exception.getMessage());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Internacao.class));
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    @Test
    void shouldRejectNullExpectedExitDateWhenUpdating()
    {
        Internacao internacaoPersistida = InternacaoTestBuilder.newInternacao().setId(1L).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(internacaoPersistida));

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> service.updateInternacao(1L, null, TreatmentStage.PROXIMO_ALTA, new BigDecimal("1500.00")));

        assertEquals("Os dados da internação são obrigatórios.", exception.getMessage());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Internacao.class));
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    @Test
    void shouldRejectNullTreatmentStageWhenUpdating()
    {
        Internacao internacaoPersistida = InternacaoTestBuilder.newInternacao().setId(1L).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(internacaoPersistida));

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> service.updateInternacao(1L, LocalDate.of(2026, 12, 10), null, new BigDecimal("1500.00")));

        assertEquals("Os dados da internação são obrigatórios.", exception.getMessage());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Internacao.class));
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    @Test
    void shouldRejectNullMonthlyPaymentWhenUpdating()
    {
        Internacao internacaoPersistida = InternacaoTestBuilder.newInternacao().setId(1L).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(internacaoPersistida));

        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> service.updateInternacao(1L, LocalDate.of(2026, 12, 10), TreatmentStage.PROXIMO_ALTA, null));

        assertEquals("Os dados da internação são obrigatórios.", exception.getMessage());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Internacao.class));
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    @Test
    void shouldRejectUpdateWhenInternacaoDoesNotExist()
    {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        InternacaoNotFoundException exception = assertThrows(InternacaoNotFoundException.class, () -> service.updateInternacao(1L, LocalDate.of(2026, 12, 10), TreatmentStage.PROXIMO_ALTA, new BigDecimal("1500.00")));

        assertEquals("Internação não encontrada.", exception.getMessage());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Internacao.class));
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    // Testes endInternacao

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

    @Test
    void shouldDeleteActiveInternacaoSuccessfully()
    {
        Internacao internacao = InternacaoTestBuilder.newInternacao().setId(1L).setAtivo(true).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(internacao));

        service.deleteInternacaoById(1L);

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).delete(Mockito.same(internacao));
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    @Test
    void shouldDeleteEndedInternacaoSuccessfully()
    {
        LocalDate dataEntrada = LocalDate.now().minusMonths(2);
        LocalDate dataSaida = LocalDate.now().minusDays(1);

        Internacao internacao = InternacaoTestBuilder.newInternacao().setId(1L).setDataEntrada(dataEntrada).setDataSaidaPrevista(dataEntrada.plusMonths(9)).setDataSaida(dataSaida).setAlta(AltaTypes.CONCLUSAO).setAtivo(false).build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(internacao));

        service.deleteInternacaoById(1L);

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).delete(Mockito.same(internacao));
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    @Test
    void shouldRejectDeletingInternacaoWhenItDoesNotExist()
    {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        InternacaoNotFoundException exception = assertThrows(InternacaoNotFoundException.class, () -> service.deleteInternacaoById(1L));

        assertEquals("Internação não encontrada.", exception.getMessage());

        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository, Mockito.never()).delete(Mockito.any(Internacao.class));
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, -1, -10})
    void shouldRejectDeletingInternacaoWhenIdIsInvalid(Long id)
    {
        InternacaoNotFoundException exception = assertThrows(InternacaoNotFoundException.class, () -> service.deleteInternacaoById(id));

        assertEquals("Internação não encontrada.", exception.getMessage());

        Mockito.verifyNoInteractions(repository, acolhidoService, convenioService);
    }

    // Testes updateTreatmentStage

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

    @Test
    void shouldRejectNullReferenceDateWithoutInteractingWithDependencies()
    {
        InternacaoWithInvalidInformationException exception = assertThrows(InternacaoWithInvalidInformationException.class, () -> service.updateTreatmentStage(null));

        assertEquals("Data de referência é obrigatória para atualização so estagio de tratamento.", exception.getMessage());

        Mockito.verifyNoInteractions(repository, acolhidoService, convenioService);
    }


}
