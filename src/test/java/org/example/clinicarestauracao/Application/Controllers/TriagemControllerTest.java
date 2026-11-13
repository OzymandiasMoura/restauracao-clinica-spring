package org.example.clinicarestauracao.Application.Controllers;

import org.example.clinicarestauracao.Application.Dtos.TriagemDtos.TriagemReagendamentoDto;
import org.example.clinicarestauracao.Application.Dtos.TriagemDtos.TriagemRequestDto;
import org.example.clinicarestauracao.Application.Dtos.TriagemDtos.TriagemResponseDto;
import org.example.clinicarestauracao.Application.Services.ConvenioService;
import org.example.clinicarestauracao.Application.Services.TriagemService;
import org.example.clinicarestauracao.Builders.ConvenioTestBuilder;
import org.example.clinicarestauracao.Builders.TriagemTestBuilder;
import org.example.clinicarestauracao.Domain.Entities.Convenio;
import org.example.clinicarestauracao.Domain.Entities.Triagem;
import org.example.clinicarestauracao.Domain.Enums.Status;
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

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TriagemControllerTest
{
    @Mock
    private TriagemService service;

    @Mock
    private ConvenioService convenioService;

    @InjectMocks
    private TriagemController controller;

    @BeforeEach
    void setUpRequestContext()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/triagens");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void clearRequestContext()
    {
        RequestContextHolder.resetRequestAttributes();
    }

    // Testes do findAll

    @Test
    void shouldFindAllTriagensSuccessfully()
    {
        Triagem primeiraTriagem = TriagemTestBuilder.newTriagem().setId(1L).build();
        Triagem segundaTriagem = TriagemTestBuilder.newTriagem()
                .setId(2L)
                .setNomePossivelAcolhido("Ana Souza")
                .build();
        List<Triagem> triagens = List.of(primeiraTriagem, segundaTriagem);
        Mockito.when(service.findAllTriagens()).thenReturn(triagens);

        ResponseEntity<List<TriagemResponseDto>> response = controller.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertDtoMatchesTriagem(primeiraTriagem, response.getBody().getFirst());
        assertDtoMatchesTriagem(segundaTriagem, response.getBody().get(1));
        Mockito.verify(service).findAllTriagens();
        Mockito.verifyNoMoreInteractions(service);
        Mockito.verifyNoInteractions(convenioService);
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoTriagens()
    {
        Mockito.when(service.findAllTriagens()).thenReturn(List.of());

        ResponseEntity<List<TriagemResponseDto>> response = controller.findAll();

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertTrue(response.getBody().isEmpty())
        );
        Mockito.verify(service).findAllTriagens();
        Mockito.verifyNoMoreInteractions(service);
        Mockito.verifyNoInteractions(convenioService);
    }

    // Testes do findById

    @Test
    void shouldFindTriagemByIdSuccessfully()
    {
        Triagem triagem = TriagemTestBuilder.newTriagem().setId(1L).build();
        Mockito.when(service.findTriagemById(1L)).thenReturn(triagem);

        ResponseEntity<TriagemResponseDto> response = controller.findById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertDtoMatchesTriagem(triagem, response.getBody());
        Mockito.verify(service).findTriagemById(1L);
        Mockito.verifyNoMoreInteractions(service);
        Mockito.verifyNoInteractions(convenioService);
    }

    // Testes do findByName

    @Test
    void shouldFindTriagensByNameSuccessfully()
    {
        String nome = "  maria  ";
        Triagem primeiraTriagem = TriagemTestBuilder.newTriagem().setId(1L).setNomePossivelAcolhido("Maria Silva").build();
        Triagem segundaTriagem = TriagemTestBuilder.newTriagem().setId(2L).setNomePossivelAcolhido("Maria Souza").build();
        Mockito.when(service.findTriagemByName(nome)).thenReturn(List.of(primeiraTriagem, segundaTriagem));

        ResponseEntity<List<TriagemResponseDto>> response = controller.findByName(nome);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertDtoMatchesTriagem(primeiraTriagem, response.getBody().getFirst());
        assertDtoMatchesTriagem(segundaTriagem, response.getBody().get(1));
        Mockito.verify(service).findTriagemByName(nome);
        Mockito.verifyNoMoreInteractions(service);
        Mockito.verifyNoInteractions(convenioService);
    }

    // Testes do findByStatus

    @Test
    void shouldFindTriagensByStatusSuccessfully()
    {
        Triagem primeiraTriagem = TriagemTestBuilder.newTriagem().setId(1L).build();
        Triagem segundaTriagem = TriagemTestBuilder.newTriagem().setId(2L).build();
        Mockito.when(service.findTriagemByStatus(Status.AGENDADA)).thenReturn(List.of(primeiraTriagem, segundaTriagem));

        ResponseEntity<List<TriagemResponseDto>> response = controller.findByStatus(Status.AGENDADA);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertDtoMatchesTriagem(primeiraTriagem, response.getBody().getFirst());
        assertDtoMatchesTriagem(segundaTriagem, response.getBody().get(1));
        Mockito.verify(service).findTriagemByStatus(Status.AGENDADA);
        Mockito.verifyNoMoreInteractions(service);
        Mockito.verifyNoInteractions(convenioService);
    }

    // Testes do createTriagem

    @Test
    void shouldCreateTriagemSuccessfully()
    {
        Long convenioId = 1L;
        LocalDateTime dataAgendada = LocalDateTime.now().plusDays(7);
        Convenio convenio = new ConvenioTestBuilder().setId(convenioId).build();
        TriagemRequestDto request = new TriagemRequestDto(
                convenioId,
                "Maria da Silva",
                "11999999999",
                dataAgendada,
                "Entrevista inicial"
        );
        Triagem created = TriagemTestBuilder.newTriagem()
                .setId(10L)
                .setConvenio(convenio)
                .setNomePossivelAcolhido(request.nomePossivelAcolhido())
                .setTelefone(request.telefone())
                .setDataAgendada(dataAgendada)
                .setObservacaoEntrevista(request.observacaoEntrevista())
                .build();
        Mockito.when(convenioService.findConvenioById(convenioId)).thenReturn(convenio);
        Mockito.when(service.createTriagem(Mockito.any(Triagem.class))).thenReturn(created);

        ResponseEntity<TriagemResponseDto> response = controller.createTriagem(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(URI.create("http://localhost/triagens/10"), response.getHeaders().getLocation());
        assertNotNull(response.getBody());
        assertDtoMatchesTriagem(created, response.getBody());

        ArgumentCaptor<Triagem> captor = ArgumentCaptor.forClass(Triagem.class);
        Mockito.verify(convenioService).findConvenioById(convenioId);
        Mockito.verify(service).createTriagem(captor.capture());

        Triagem enviadaAoService = captor.getValue();
        assertAll(
                () -> assertNull(enviadaAoService.getId()),
                () -> assertSame(convenio, enviadaAoService.getConvenio()),
                () -> assertEquals(request.nomePossivelAcolhido(), enviadaAoService.getNomePossivelAcolhido()),
                () -> assertEquals(request.telefone(), enviadaAoService.getTelefone()),
                () -> assertEquals(request.dataAgendada(), enviadaAoService.getDataAgendada()),
                () -> assertEquals(request.observacaoEntrevista(), enviadaAoService.getObservacaoEntrevista()),
                () -> assertEquals(Status.AGENDADA, enviadaAoService.getStatus()),
                () -> assertNull(enviadaAoService.getDataConclusao())
        );
        Mockito.verifyNoMoreInteractions(service, convenioService);
    }

    // Testes do update

    @Test
    void shouldUpdateTriagemSuccessfully()
    {
        Long triagemId = 1L;
        Long convenioId = 2L;
        LocalDateTime novaData = LocalDateTime.now().plusDays(10);
        Convenio convenio = new ConvenioTestBuilder().setId(convenioId).setDescricao("Novo convênio").setCor("#123456").build();
        TriagemRequestDto request = new TriagemRequestDto(
                convenioId,
                "Ana Souza",
                "1133334444",
                novaData,
                "Observação atualizada"
        );
        Triagem updated = TriagemTestBuilder.newTriagem()
                .setId(triagemId)
                .setConvenio(convenio)
                .setNomePossivelAcolhido(request.nomePossivelAcolhido())
                .setTelefone(request.telefone())
                .setDataAgendada(novaData)
                .setObservacaoEntrevista(request.observacaoEntrevista())
                .build();
        Mockito.when(convenioService.findConvenioById(convenioId)).thenReturn(convenio);
        Mockito.when(service.updateTriagem(Mockito.eq(triagemId), Mockito.any(Triagem.class))).thenReturn(updated);

        ResponseEntity<TriagemResponseDto> response = controller.update(triagemId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertDtoMatchesTriagem(updated, response.getBody());

        ArgumentCaptor<Triagem> captor = ArgumentCaptor.forClass(Triagem.class);
        Mockito.verify(convenioService).findConvenioById(convenioId);
        Mockito.verify(service).updateTriagem(Mockito.eq(triagemId), captor.capture());

        Triagem enviadaAoService = captor.getValue();
        assertAll(
                () -> assertNull(enviadaAoService.getId()),
                () -> assertSame(convenio, enviadaAoService.getConvenio()),
                () -> assertEquals(request.nomePossivelAcolhido(), enviadaAoService.getNomePossivelAcolhido()),
                () -> assertEquals(request.telefone(), enviadaAoService.getTelefone()),
                () -> assertEquals(request.dataAgendada(), enviadaAoService.getDataAgendada()),
                () -> assertEquals(request.observacaoEntrevista(), enviadaAoService.getObservacaoEntrevista())
        );
        Mockito.verifyNoMoreInteractions(service, convenioService);
    }

    // Testes do delete

    @Test
    void shouldDeleteTriagemSuccessfully()
    {
        ResponseEntity<Void> response = controller.delete(1L);

        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()),
                () -> assertNull(response.getBody())
        );
        Mockito.verify(service).deleteTriagem(1L);
        Mockito.verifyNoMoreInteractions(service);
        Mockito.verifyNoInteractions(convenioService);
    }

    // Testes da conclusão

    @Test
    void shouldConcludeTriagemSuccessfully()
    {
        Triagem triagem = buildTriagemWithStatus(Status.CONCLUIDA);
        Mockito.when(service.concluir(1L)).thenReturn(triagem);

        ResponseEntity<TriagemResponseDto> response = controller.concluir(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertDtoMatchesTriagem(triagem, response.getBody());
        Mockito.verify(service).concluir(1L);
        Mockito.verifyNoMoreInteractions(service);
        Mockito.verifyNoInteractions(convenioService);
    }

    // Testes da desconclusão

    @Test
    void shouldUndoTriagemConclusionSuccessfully()
    {
        Triagem triagem = buildTriagemWithStatus(Status.ATRASADA);
        Mockito.when(service.desconcluir(1L)).thenReturn(triagem);

        ResponseEntity<TriagemResponseDto> response = controller.desconcluir(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertDtoMatchesTriagem(triagem, response.getBody());
        Mockito.verify(service).desconcluir(1L);
        Mockito.verifyNoMoreInteractions(service);
        Mockito.verifyNoInteractions(convenioService);
    }

    // Testes do reagendamento

    @Test
    void shouldRescheduleTriagemSuccessfully()
    {
        LocalDateTime novaData = LocalDateTime.now().plusDays(10);
        TriagemReagendamentoDto request = new TriagemReagendamentoDto(novaData);
        Triagem triagem = TriagemTestBuilder.newTriagem().setDataAgendada(novaData).build();
        Mockito.when(service.reagendar(1L, novaData)).thenReturn(triagem);

        ResponseEntity<TriagemResponseDto> response = controller.reagendar(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertDtoMatchesTriagem(triagem, response.getBody());
        Mockito.verify(service).reagendar(1L, novaData);
        Mockito.verifyNoMoreInteractions(service);
        Mockito.verifyNoInteractions(convenioService);
    }

    // Testes do cancelamento

    @Test
    void shouldCancelTriagemSuccessfully()
    {
        Triagem triagem = buildTriagemWithStatus(Status.CANCELADA);
        Mockito.when(service.cancelarTriagem(1L)).thenReturn(triagem);

        ResponseEntity<TriagemResponseDto> response = controller.cancelarTriagem(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertDtoMatchesTriagem(triagem, response.getBody());
        Mockito.verify(service).cancelarTriagem(1L);
        Mockito.verifyNoMoreInteractions(service);
        Mockito.verifyNoInteractions(convenioService);
    }

    // Testes do descancelamento

    @Test
    void shouldUndoTriagemCancellationSuccessfully()
    {
        Triagem triagem = buildTriagemWithStatus(Status.AGENDADA);
        Mockito.when(service.descancelarTriagem(1L)).thenReturn(triagem);

        ResponseEntity<TriagemResponseDto> response = controller.descancelarTriagem(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertDtoMatchesTriagem(triagem, response.getBody());
        Mockito.verify(service).descancelarTriagem(1L);
        Mockito.verifyNoMoreInteractions(service);
        Mockito.verifyNoInteractions(convenioService);
    }

    private Triagem buildTriagemWithStatus(Status status)
    {
        LocalDateTime now = LocalDateTime.now();
        TriagemTestBuilder builder = TriagemTestBuilder.newTriagem().setStatus(status);

        return switch (status)
        {
            case CONCLUIDA -> builder
                    .setDataAgendada(now.minusDays(2))
                    .setDataConclusao(now.minusDays(1))
                    .build();
            case ATRASADA -> builder
                    .setDataAgendada(now.minusDays(1))
                    .build();
            default -> builder.build();
        };
    }

    private void assertDtoMatchesTriagem(Triagem triagem, TriagemResponseDto dto)
    {
        assertAll(
                () -> assertEquals(triagem.getId(), dto.id()),
                () -> assertNotNull(dto.convenio()),
                () -> assertEquals(triagem.getConvenio().getId(), dto.convenio().id()),
                () -> assertEquals(triagem.getNomePossivelAcolhido(), dto.nomePossivelAcolhido()),
                () -> assertEquals(triagem.getTelefone(), dto.telefone()),
                () -> assertEquals(triagem.getDataAgendada(), dto.dataAgendada()),
                () -> assertEquals(triagem.getDataConclusao(), dto.dataConclusao()),
                () -> assertEquals(triagem.getObservacaoEntrevista(), dto.observacaoEntrevista()),
                () -> assertEquals(triagem.getStatus(), dto.status())
        );
    }
}
