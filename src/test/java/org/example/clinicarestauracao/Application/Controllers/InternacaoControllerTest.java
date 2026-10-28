package org.example.clinicarestauracao.Application.Controllers;

import org.example.clinicarestauracao.Application.Dtos.InternacaoDtos.InternacaoCreateRequestDto;
import org.example.clinicarestauracao.Application.Dtos.InternacaoDtos.InternacaoEndRequestDto;
import org.example.clinicarestauracao.Application.Dtos.InternacaoDtos.InternacaoResponseDto;
import org.example.clinicarestauracao.Application.Dtos.InternacaoDtos.InternacaoUpdateRequestDto;
import org.example.clinicarestauracao.Application.Services.AcolhidoService;
import org.example.clinicarestauracao.Application.Services.ConvenioService;
import org.example.clinicarestauracao.Application.Services.InternacaoService;
import org.example.clinicarestauracao.Builders.AcolhidoTestBuilder;
import org.example.clinicarestauracao.Builders.ConvenioTestBuilder;
import org.example.clinicarestauracao.Builders.InternacaoTestBuilder;
import org.example.clinicarestauracao.Domain.Entities.Acolhido;
import org.example.clinicarestauracao.Domain.Entities.Convenio;
import org.example.clinicarestauracao.Domain.Entities.Internacao;
import org.example.clinicarestauracao.Domain.Enums.AltaTypes;
import org.example.clinicarestauracao.Domain.Enums.TreatmentStage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InternacaoControllerTest
{
    @Mock
    private InternacaoService service;
    @Mock
    private AcolhidoService acolhidoService;
    @Mock
    private ConvenioService convenioService;
    @InjectMocks
    private InternacaoController controller;

    @BeforeEach
    void setUpRequestContext()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8080);
        request.setRequestURI("/internacoes");

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void clearRequestContext()
    {
        RequestContextHolder.resetRequestAttributes();
    }


    // Testes findAll

    @Test
    void shouldFindAllInternacoesSuccessfully()
    {
        Acolhido primeiroAcolhido = AcolhidoTestBuilder.newAcolhido().setId(1L).setNome("Maria Silva").build();
        Convenio primeiroConvenio = new ConvenioTestBuilder().setId(1L).setDescricao("Convênio particular").build();
        Internacao primeiraInternacao = InternacaoTestBuilder.newInternacao().setId(1L).setAcolhido(primeiroAcolhido).setConvenio(primeiroConvenio).build();
        Acolhido segundoAcolhido = AcolhidoTestBuilder.newAcolhido().setId(2L).setNome("João da Silva").build();
        Convenio segundoConvenio = new ConvenioTestBuilder().setId(2L).setDescricao("Convênio municipal").build();
        LocalDate dataSaida = LocalDate.of(2026, 9, 10);
        Internacao segundaInternacao = InternacaoTestBuilder.newInternacao().setId(2L).setAcolhido(segundoAcolhido).setConvenio(segundoConvenio).setDataSaida(dataSaida).setAtivo(false).setAlta(AltaTypes.CONCLUSAO).build();

        Mockito.when(service.findAllInternacoes()).thenReturn(List.of(primeiraInternacao, segundaInternacao));

        ResponseEntity<List<InternacaoResponseDto>> response = controller.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        InternacaoResponseDto primeiraResponse = response.getBody().getFirst();

        assertAll(
                () -> assertEquals(primeiraInternacao.getId(), primeiraResponse.id()),
                () -> assertEquals(primeiraInternacao.getDataEntrada(), primeiraResponse.dataEntrada()),
                () -> assertEquals(primeiraInternacao.getDataSaidaPrevista(), primeiraResponse.dataSaidaPrevista()),
                () -> assertNull(primeiraResponse.dataSaida()),
                () -> assertEquals(primeiraInternacao.getEtapaTratamento(), primeiraResponse.etapaTratamento()),
                () -> assertEquals(primeiraInternacao.getValorMensalidade(), primeiraResponse.valorMensalidade()),
                () -> assertTrue(primeiraResponse.ativo()),
                () -> assertNull(primeiraResponse.alta()),
                () -> assertNotNull(primeiraResponse.acolhido()),
                () -> assertEquals(primeiroAcolhido.getId(), primeiraResponse.acolhido().id()),
                () -> assertEquals(primeiroAcolhido.getNome(), primeiraResponse.acolhido().nome()),
                () -> assertNotNull(primeiraResponse.convenio()),
                () -> assertEquals(primeiroConvenio.getId(), primeiraResponse.convenio().id()),
                () -> assertEquals(primeiroConvenio.getDescricao(), primeiraResponse.convenio().descricao())
        );

        InternacaoResponseDto segundaResponse = response.getBody().get(1);

        assertAll(
                () -> assertEquals(segundaInternacao.getId(), segundaResponse.id()),
                () -> assertEquals(segundoAcolhido.getId(), segundaResponse.acolhido().id()),
                () -> assertEquals(segundoConvenio.getId(), segundaResponse.convenio().id()),
                () -> assertEquals(dataSaida, segundaResponse.dataSaida()),
                () -> assertFalse(segundaResponse.ativo()),
                () -> assertEquals(AltaTypes.CONCLUSAO, segundaResponse.alta())
        );

        Mockito.verify(service).findAllInternacoes();
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoInternacoes()
    {
        Mockito.when(service.findAllInternacoes()).thenReturn(List.of());

        ResponseEntity<List<InternacaoResponseDto>> response = controller.findAll();

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertTrue(response.getBody().isEmpty())
        );

        Mockito.verify(service).findAllInternacoes();
        Mockito.verifyNoMoreInteractions(service);
    }

    // Testes findById

    @Test
    void shouldFindInternacaoByIdSuccessfully()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setId(1L).setNome("Maria Silva").build();
        Convenio convenio = new ConvenioTestBuilder().setId(1L).setDescricao("Convênio particular").build();
        LocalDate dataSaida = LocalDate.of(2026, 9, 10);
        Internacao internacao = InternacaoTestBuilder.newInternacao().setId(1L).setAcolhido(acolhido).setConvenio(convenio).setDataSaida(dataSaida).setAtivo(false).setAlta(AltaTypes.CONCLUSAO).build();

        Mockito.when(service.findInternacaoById(1L)).thenReturn(internacao);

        ResponseEntity<InternacaoResponseDto> response = controller.findById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        InternacaoResponseDto responseBody = response.getBody();

        assertAll(
                () -> assertEquals(internacao.getId(), responseBody.id()),
                () -> assertEquals(internacao.getDataEntrada(), responseBody.dataEntrada()),
                () -> assertEquals(internacao.getDataSaidaPrevista(), responseBody.dataSaidaPrevista()),
                () -> assertEquals(internacao.getDataSaida(), responseBody.dataSaida()),
                () -> assertEquals(internacao.getEtapaTratamento(), responseBody.etapaTratamento()),
                () -> assertEquals(internacao.getValorMensalidade(), responseBody.valorMensalidade()),
                () -> assertEquals(internacao.getAtivo(), responseBody.ativo()),
                () -> assertEquals(internacao.getAlta(), responseBody.alta()),
                () -> assertNotNull(responseBody.acolhido()),
                () -> assertEquals(acolhido.getId(), responseBody.acolhido().id()),
                () -> assertEquals(acolhido.getNome(), responseBody.acolhido().nome()),
                () -> assertNotNull(responseBody.convenio()),
                () -> assertEquals(convenio.getId(), responseBody.convenio().id()),
                () -> assertEquals(convenio.getDescricao(), responseBody.convenio().descricao())
        );

        Mockito.verify(service).findInternacaoById(1L);
        Mockito.verifyNoMoreInteractions(service);
    }

    // Testes findInternacaoByAcolhidoId

    @Test
    void shouldFindInternacoesByAcolhidoIdSuccessfully()
    {
        Long acolhidoId = 1L;
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setId(acolhidoId).setNome("Maria Silva").build();
        Convenio primeiroConvenio = new ConvenioTestBuilder().setId(1L).setDescricao("Convênio particular").build();
        Convenio segundoConvenio = new ConvenioTestBuilder().setId(2L).setDescricao("Convênio municipal").build();
        Internacao primeiraInternacao = InternacaoTestBuilder.newInternacao().setId(1L).setAcolhido(acolhido).setConvenio(primeiroConvenio).build();
        Internacao segundaInternacao = InternacaoTestBuilder.newInternacao().setId(2L).setAcolhido(acolhido).setConvenio(segundoConvenio).build();

        Mockito.when(service.findInternacoesByAcolhidoId(acolhidoId)).thenReturn(List.of(primeiraInternacao, segundaInternacao));

        ResponseEntity<List<InternacaoResponseDto>> response = controller.findInternacaoByAcolhidoId(acolhidoId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        InternacaoResponseDto primeiraResponse = response.getBody().getFirst();
        InternacaoResponseDto segundaResponse = response.getBody().get(1);

        assertAll(
                () -> assertEquals(primeiraInternacao.getId(), primeiraResponse.id()),
                () -> assertEquals(acolhidoId, primeiraResponse.acolhido().id()),
                () -> assertEquals(primeiroConvenio.getId(), primeiraResponse.convenio().id()),
                () -> assertEquals(segundaInternacao.getId(), segundaResponse.id()),
                () -> assertEquals(acolhidoId, segundaResponse.acolhido().id()),
                () -> assertEquals(segundoConvenio.getId(), segundaResponse.convenio().id())
        );

        Mockito.verify(service).findInternacoesByAcolhidoId(acolhidoId);
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void shouldReturnEmptyListWhenAcolhidoHasNoInternacoes()
    {
        Long acolhidoId = 1L;

        Mockito.when(service.findInternacoesByAcolhidoId(acolhidoId)).thenReturn(List.of());

        ResponseEntity<List<InternacaoResponseDto>> response = controller.findInternacaoByAcolhidoId(acolhidoId);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertTrue(response.getBody().isEmpty())
        );

        Mockito.verify(service).findInternacoesByAcolhidoId(acolhidoId);
        Mockito.verifyNoMoreInteractions(service);
    }

    // Testes findInternacaoByConvenioId

    @Test
    void shouldFindInternacoesByConvenioIdSuccessfully()
    {
        Long convenioId = 1L;
        Convenio convenio = new ConvenioTestBuilder().setId(convenioId).setDescricao("Convênio particular").build();
        Acolhido primeiroAcolhido = AcolhidoTestBuilder.newAcolhido().setId(1L).setNome("Maria Silva").build();
        Acolhido segundoAcolhido = AcolhidoTestBuilder.newAcolhido().setId(2L).setNome("João da Silva").build();
        Internacao primeiraInternacao = InternacaoTestBuilder.newInternacao().setId(1L).setAcolhido(primeiroAcolhido).setConvenio(convenio).build();
        Internacao segundaInternacao = InternacaoTestBuilder.newInternacao().setId(2L).setAcolhido(segundoAcolhido).setConvenio(convenio).build();

        Mockito.when(service.findInternacoesByConvenioId(convenioId)).thenReturn(List.of(primeiraInternacao, segundaInternacao));

        ResponseEntity<List<InternacaoResponseDto>> response = controller.findInternacaoByConvenioId(convenioId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        InternacaoResponseDto primeiraResponse = response.getBody().getFirst();
        InternacaoResponseDto segundaResponse = response.getBody().get(1);

        assertAll(
                () -> assertEquals(primeiraInternacao.getId(), primeiraResponse.id()),
                () -> assertEquals(convenioId, primeiraResponse.convenio().id()),
                () -> assertEquals(primeiroAcolhido.getId(), primeiraResponse.acolhido().id()),
                () -> assertEquals(segundaInternacao.getId(), segundaResponse.id()),
                () -> assertEquals(convenioId, segundaResponse.convenio().id()),
                () -> assertEquals(segundoAcolhido.getId(), segundaResponse.acolhido().id())
        );

        Mockito.verify(service).findInternacoesByConvenioId(convenioId);
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void shouldReturnEmptyListWhenConvenioHasNoInternacoes()
    {
        Long convenioId = 1L;

        Mockito.when(service.findInternacoesByConvenioId(convenioId)).thenReturn(List.of());

        ResponseEntity<List<InternacaoResponseDto>> response = controller.findInternacaoByConvenioId(convenioId);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertTrue(response.getBody().isEmpty())
        );

        Mockito.verify(service).findInternacoesByConvenioId(convenioId);
        Mockito.verifyNoMoreInteractions(service);
    }

    // Testes createInternacao

    @Test
    void shouldCreatePaidInternacaoSuccessfully()
    {
        Long acolhidoId = 1L;
        Long convenioId = 1L;
        LocalDate dataEntrada = LocalDate.of(2026, 1, 10);
        BigDecimal valorMensalidade = new BigDecimal("1500.50");
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setId(acolhidoId).build();
        Convenio convenio = new ConvenioTestBuilder().setId(convenioId).setPagamento(true).build();
        InternacaoCreateRequestDto request = new InternacaoCreateRequestDto(acolhidoId, convenioId, dataEntrada, TreatmentStage.INICIO, valorMensalidade);
        Internacao created = InternacaoTestBuilder.newInternacao().setId(10L).setAcolhido(acolhido).setConvenio(convenio).setDataEntrada(dataEntrada).setDataSaidaPrevista(dataEntrada.plusMonths(9)).setEtapaTratamento(TreatmentStage.INICIO).setValorMensalidade(valorMensalidade).build();

        Mockito.when(acolhidoService.findAcolhidoById(acolhidoId)).thenReturn(acolhido);
        Mockito.when(convenioService.findConvenioById(convenioId)).thenReturn(convenio);
        Mockito.when(service.createInternacao(Mockito.any(Internacao.class))).thenReturn(created);

        ResponseEntity<InternacaoResponseDto> response = controller.createInternacao(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(URI.create("http://localhost:8080/internacoes/10"), response.getHeaders().getLocation());
        assertNotNull(response.getBody());

        InternacaoResponseDto responseBody = response.getBody();

        assertAll(
                () -> assertEquals(created.getId(), responseBody.id()),
                () -> assertEquals(acolhidoId, responseBody.acolhido().id()),
                () -> assertEquals(convenioId, responseBody.convenio().id()),
                () -> assertEquals(dataEntrada, responseBody.dataEntrada()),
                () -> assertEquals(dataEntrada.plusMonths(9), responseBody.dataSaidaPrevista()),
                () -> assertEquals(TreatmentStage.INICIO, responseBody.etapaTratamento()),
                () -> assertEquals(valorMensalidade, responseBody.valorMensalidade()),
                () -> assertTrue(responseBody.ativo()),
                () -> assertNull(responseBody.dataSaida()),
                () -> assertNull(responseBody.alta())
        );

        ArgumentCaptor<Internacao> captor = ArgumentCaptor.forClass(Internacao.class);

        Mockito.verify(acolhidoService).findAcolhidoById(acolhidoId);
        Mockito.verify(convenioService).findConvenioById(convenioId);
        Mockito.verify(service).createInternacao(captor.capture());

        Internacao enviadaAoService = captor.getValue();

        assertAll(
                () -> assertNull(enviadaAoService.getId()),
                () -> assertSame(acolhido, enviadaAoService.getAcolhido()),
                () -> assertSame(convenio, enviadaAoService.getConvenio()),
                () -> assertEquals(dataEntrada, enviadaAoService.getDataEntrada()),
                () -> assertEquals(dataEntrada.plusMonths(9), enviadaAoService.getDataSaidaPrevista()),
                () -> assertEquals(TreatmentStage.INICIO, enviadaAoService.getEtapaTratamento()),
                () -> assertEquals(valorMensalidade, enviadaAoService.getValorMensalidade()),
                () -> assertTrue(enviadaAoService.getAtivo()),
                () -> assertNull(enviadaAoService.getDataSaida()),
                () -> assertNull(enviadaAoService.getAlta())
        );

        Mockito.verifyNoMoreInteractions(service, acolhidoService, convenioService);
    }

    @Test
    void shouldCreateFreeInternacaoWithoutMonthlyPayment()
    {
        Long acolhidoId = 1L;
        Long convenioId = 2L;
        LocalDate dataEntrada = LocalDate.of(2026, 1, 10);
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setId(acolhidoId).build();
        Convenio convenio = new ConvenioTestBuilder().setId(convenioId).setPagamento(false).build();
        InternacaoCreateRequestDto request = new InternacaoCreateRequestDto(acolhidoId, convenioId, dataEntrada, TreatmentStage.INICIO, null);

        Internacao created = InternacaoTestBuilder.newInternacao().setId(11L).setAcolhido(acolhido).setConvenio(convenio).setDataEntrada(dataEntrada).setDataSaidaPrevista(dataEntrada.plusMonths(9)).setEtapaTratamento(TreatmentStage.INICIO).setValorMensalidade(new BigDecimal("0.00")).build();

        Mockito.when(acolhidoService.findAcolhidoById(acolhidoId)).thenReturn(acolhido);
        Mockito.when(convenioService.findConvenioById(convenioId)).thenReturn(convenio);
        Mockito.when(service.createInternacao(Mockito.any(Internacao.class))).thenReturn(created);

        ResponseEntity<InternacaoResponseDto> response = controller.createInternacao(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(URI.create("http://localhost:8080/internacoes/11"), response.getHeaders().getLocation());
        assertNotNull(response.getBody());
        assertEquals(new BigDecimal("0.00"), response.getBody().valorMensalidade());

        ArgumentCaptor<Internacao> captor = ArgumentCaptor.forClass(Internacao.class);

        Mockito.verify(acolhidoService).findAcolhidoById(acolhidoId);
        Mockito.verify(convenioService).findConvenioById(convenioId);
        Mockito.verify(service).createInternacao(captor.capture());

        Internacao enviadaAoService = captor.getValue();

        assertAll(
                () -> assertNull(enviadaAoService.getId()),
                () -> assertSame(acolhido, enviadaAoService.getAcolhido()),
                () -> assertSame(convenio, enviadaAoService.getConvenio()),
                () -> assertEquals(new BigDecimal("0.00"), enviadaAoService.getValorMensalidade()),
                () -> assertTrue(enviadaAoService.getAtivo()),
                () -> assertNull(enviadaAoService.getDataSaida()),
                () -> assertNull(enviadaAoService.getAlta())
        );

        Mockito.verifyNoMoreInteractions(service, acolhidoService, convenioService);
    }

    // Testes updateInternacao

    @Test
    void shouldUpdateInternacaoSuccessfully()
    {
        Long internacaoId = 1L;
        LocalDate dataEntrada = LocalDate.of(2026, 1, 10);
        LocalDate novaDataSaidaPrevista = LocalDate.of(2026, 12, 10);
        BigDecimal novoValorMensalidade = new BigDecimal("1500.50");
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setId(1L).build();
        Convenio convenio = new ConvenioTestBuilder().setId(1L).setPagamento(true).build();
        InternacaoUpdateRequestDto request = new InternacaoUpdateRequestDto(novaDataSaidaPrevista, TreatmentStage.MEIO, novoValorMensalidade);

        Internacao updated = InternacaoTestBuilder.newInternacao().setId(internacaoId).setAcolhido(acolhido).setConvenio(convenio).setDataEntrada(dataEntrada).setDataSaidaPrevista(novaDataSaidaPrevista).setEtapaTratamento(TreatmentStage.MEIO).setValorMensalidade(novoValorMensalidade).build();

        Mockito.when(service.updateInternacao(internacaoId, novaDataSaidaPrevista, TreatmentStage.MEIO, novoValorMensalidade)).thenReturn(updated);

        ResponseEntity<InternacaoResponseDto> response = controller.updateInternacao(internacaoId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        InternacaoResponseDto responseBody = response.getBody();

        assertAll(
                () -> assertEquals(internacaoId, responseBody.id()),
                () -> assertEquals(acolhido.getId(), responseBody.acolhido().id()),
                () -> assertEquals(convenio.getId(), responseBody.convenio().id()),
                () -> assertEquals(dataEntrada, responseBody.dataEntrada()),
                () -> assertEquals(novaDataSaidaPrevista, responseBody.dataSaidaPrevista()),
                () -> assertEquals(TreatmentStage.MEIO, responseBody.etapaTratamento()),
                () -> assertEquals(novoValorMensalidade, responseBody.valorMensalidade()),
                () -> assertTrue(responseBody.ativo()),
                () -> assertNull(responseBody.dataSaida()),
                () -> assertNull(responseBody.alta())
        );

        Mockito.verify(service).updateInternacao(internacaoId, novaDataSaidaPrevista, TreatmentStage.MEIO, novoValorMensalidade);

        Mockito.verifyNoMoreInteractions(service);
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

    // Testes endInternacao

    @Test
    void shouldEndInternacaoSuccessfully()
    {
        Long internacaoId = 1L;
        LocalDate dataEntrada = LocalDate.now().minusMonths(2);
        LocalDate dataSaida = LocalDate.now();
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setId(1L).build();
        Convenio convenio = new ConvenioTestBuilder().setId(1L).build();
        InternacaoEndRequestDto request = new InternacaoEndRequestDto(AltaTypes.CONCLUSAO, dataSaida);
        Internacao ended = InternacaoTestBuilder.newInternacao().setId(internacaoId).setAcolhido(acolhido).setConvenio(convenio).setDataEntrada(dataEntrada).setDataSaidaPrevista(dataEntrada.plusMonths(9)).setDataSaida(dataSaida).setAtivo(false).setAlta(AltaTypes.CONCLUSAO).build();

        Mockito.when(service.endInternacaoById(internacaoId, AltaTypes.CONCLUSAO, dataSaida)).thenReturn(ended);

        ResponseEntity<InternacaoResponseDto> response = controller.endInternacao(internacaoId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        InternacaoResponseDto responseBody = response.getBody();

        assertAll(
                () -> assertEquals(internacaoId, responseBody.id()),
                () -> assertEquals(acolhido.getId(), responseBody.acolhido().id()),
                () -> assertEquals(convenio.getId(), responseBody.convenio().id()),
                () -> assertEquals(dataEntrada, responseBody.dataEntrada()),
                () -> assertEquals(dataEntrada.plusMonths(9), responseBody.dataSaidaPrevista()),
                () -> assertEquals(dataSaida, responseBody.dataSaida()),
                () -> assertEquals(AltaTypes.CONCLUSAO, responseBody.alta()),
                () -> assertFalse(responseBody.ativo())
        );

        Mockito.verify(service).endInternacaoById(internacaoId, AltaTypes.CONCLUSAO, dataSaida);

        Mockito.verifyNoMoreInteractions(service);
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }

// Testes deleteInternacao

    @Test
    void shouldDeleteInternacaoSuccessfully()
    {
        Long internacaoId = 1L;

        ResponseEntity<Void> response = controller.deleteInternacao(internacaoId);

        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()),
                () -> assertNull(response.getBody())
        );

        Mockito.verify(service).deleteInternacaoById(internacaoId);
        Mockito.verifyNoMoreInteractions(service);
        Mockito.verifyNoInteractions(acolhidoService, convenioService);
    }
}