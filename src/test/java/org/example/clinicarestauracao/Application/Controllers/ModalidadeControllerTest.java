package org.example.clinicarestauracao.Application.Controllers;

import org.example.clinicarestauracao.Application.Dtos.ModalidadeDtos.ModalidadeRequestDto;
import org.example.clinicarestauracao.Application.Dtos.ModalidadeDtos.ModalidadeResponseDto;
import org.example.clinicarestauracao.Application.Services.ModalidadeService;
import org.example.clinicarestauracao.Builders.ModalidadeTestBuilder;
import org.example.clinicarestauracao.Domain.Entities.Modalidade;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ModalidadeControllerTest
{
    private final ModalidadeTestBuilder builder = new ModalidadeTestBuilder();

    @Mock
    private ModalidadeService service;
    @InjectMocks
    private ModalidadeController controller;

    @BeforeEach
    void setUpRequestContext()
    {
        // Simula uma requisição HTTP para que o ServletUriComponentsBuilder consiga construir dinamicamente a URI do recurso criado.
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8080);
        request.setRequestURI("/modalidades");

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void clearRequestContext()
    {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldCreateModalidadeAndReturnCreated()
    {
        Modalidade criada = builder.build();
        ModalidadeRequestDto request = new ModalidadeRequestDto(criada.getDescricao(), criada.getCnpj(), criada.getMaxVagas(), criada.isPagamento(), criada.getCor());

        Mockito.when(service.createModalidade(Mockito.any(Modalidade.class))).thenReturn(criada);

        ResponseEntity<ModalidadeResponseDto> response = controller.createModalidade(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(URI.create("http://localhost:8080/modalidades/" + criada.getId()), response.getHeaders().getLocation());
        assertNotNull(response.getBody());
        assertEquals(criada.getId(), response.getBody().id());
        assertEquals(criada.getDescricao(), response.getBody().descricao());
        assertEquals(criada.getCnpj(), response.getBody().cnpj());
        assertEquals(criada.getMaxVagas(), response.getBody().maxVagas());
        assertEquals(criada.isPagamento(), response.getBody().pagamento());
        assertEquals(criada.isAtivo(), response.getBody().ativo());
        assertEquals(criada.getCor(), response.getBody().cor());

        ArgumentCaptor<Modalidade> captor = ArgumentCaptor.forClass(Modalidade.class);

        Mockito.verify(service).createModalidade(captor.capture());

        Modalidade enviadaAoService = captor.getValue();

        assertNull(enviadaAoService.getId());
        assertEquals(request.descricao(), enviadaAoService.getDescricao());
        assertEquals(request.cnpj(), enviadaAoService.getCnpj());
        assertEquals(request.maxVagas(), enviadaAoService.getMaxVagas());
        assertEquals(request.pagamento(), enviadaAoService.isPagamento());
        assertTrue(enviadaAoService.isAtivo());
        assertEquals(request.cor(), enviadaAoService.getCor());
    }

    @Test
    void shouldCreateModalidadeWithoutCnpj()
    {
        ModalidadeRequestDto request = new ModalidadeRequestDto("Modalidade", null, 20, true, "#FF5733");

        Modalidade criada = new Modalidade(1L, "Modalidade", 20, true, true, "#FF5733");

        Mockito.when(service.createModalidade(Mockito.any(Modalidade.class))).thenReturn(criada);

        ResponseEntity<ModalidadeResponseDto> response = controller.createModalidade(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().cnpj());

        ArgumentCaptor<Modalidade> captor = ArgumentCaptor.forClass(Modalidade.class);
        Mockito.verify(service).createModalidade(captor.capture());

        assertNull(captor.getValue().getCnpj());
    }

    @Test
    void shouldFindAllModalidadesAndReturnOk()
    {
        Modalidade primeira = builder.build();
        Modalidade segunda = new ModalidadeTestBuilder().setId(2L).setDescricao("Prefeitura").setCNPJ("11444777000161").setMaxVagas(15).setPagamento(false).setAtivo(true).build();

        Mockito.when(service.findAllModalidades()).thenReturn(List.of(primeira, segunda));

        ResponseEntity<List<ModalidadeResponseDto>> response = controller.findAllModalidades();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        ModalidadeResponseDto responseDto = response.getBody().getFirst();

        assertEquals(primeira.getId(), responseDto.id());
        assertEquals(primeira.getDescricao(), responseDto.descricao());
        assertEquals(primeira.getCnpj(), responseDto.cnpj());
        assertEquals(primeira.getMaxVagas(), responseDto.maxVagas());
        assertEquals(primeira.isAtivo(), responseDto.ativo());
        assertEquals(primeira.isPagamento(), responseDto.pagamento());
        assertEquals(primeira.getCor(), responseDto.cor());

        ModalidadeResponseDto segundoDto = response.getBody().get(1);

        assertEquals(segunda.getId(), segundoDto.id());
        assertEquals(segunda.getDescricao(), segundoDto.descricao());

        Mockito.verify(service).findAllModalidades();
    }

    @Test
    void shouldReturnEmptyListWhenNoModalidadesExist()
    {
        Mockito.when(service.findAllModalidades()).thenReturn(List.of());

        ResponseEntity<List<ModalidadeResponseDto>> response = controller.findAllModalidades();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        Mockito.verify(service).findAllModalidades();
    }

    @Test
    void shouldFindModalidadeByIdAndReturnOk()
    {
        Modalidade modalidade = builder.build();

        Mockito.when(service.findModalidadeById(modalidade.getId())).thenReturn(modalidade);

        ResponseEntity<ModalidadeResponseDto> response = controller.findModalidadeById(modalidade.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ModalidadeResponseDto responseDto = response.getBody();
        assertEquals(modalidade.getId(), responseDto.id());
        assertEquals(modalidade.getDescricao(), responseDto.descricao());
        assertEquals(modalidade.getCnpj(), responseDto.cnpj());
        assertEquals(modalidade.getMaxVagas(), responseDto.maxVagas());
        assertEquals(modalidade.isAtivo(), responseDto.ativo());
        assertEquals(modalidade.isPagamento(), responseDto.pagamento());
        assertEquals(modalidade.getCor(), responseDto.cor());

        Mockito.verify(service).findModalidadeById(modalidade.getId());
    }

    @Test
    void shouldUpdateModalidadeAndReturnOk()
    {
        Long id = 1L;
        ModalidadeRequestDto request = new ModalidadeRequestDto("Prefeitura atualizada", "11222333000181", 30, false, "#FF5733");
        Modalidade atualizada = builder.setId(id).setDescricao("Prefeitura atualizada").setCNPJ(request.cnpj()).setMaxVagas(30).setPagamento(false).setCor("#FF5733").build();

        Mockito.when(service.updateModalidade(Mockito.eq(id), Mockito.any(Modalidade.class))). thenReturn(atualizada);

        ResponseEntity<ModalidadeResponseDto> response = controller.updateModalidadeById(id, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ModalidadeResponseDto responseDto = response.getBody();

        assertEquals(atualizada.getId(), responseDto.id());
        assertEquals(atualizada.getDescricao(), responseDto.descricao());
        assertEquals(atualizada.getCnpj(), responseDto.cnpj());
        assertEquals(atualizada.getMaxVagas(), responseDto.maxVagas());
        assertEquals(atualizada.isAtivo(), responseDto.ativo());
        assertEquals(atualizada.isPagamento(), responseDto.pagamento());
        assertEquals(atualizada.getCor(), responseDto.cor());

        ArgumentCaptor<Modalidade> captor = ArgumentCaptor.forClass(Modalidade.class);
        Mockito.verify(service).updateModalidade(Mockito.eq(id),  captor.capture());

        Modalidade modalidade = captor.getValue();

        assertNull(modalidade.getId());
        assertEquals(request.descricao(), modalidade.getDescricao());
        assertEquals(request.cnpj(), modalidade.getCnpj());
        assertEquals(request.maxVagas(), modalidade.getMaxVagas());
        assertEquals(request.pagamento(), modalidade.isPagamento());
        assertEquals(request.cor(), modalidade.getCor());

        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void shouldDeleteModalidadeAndReturnNoContent()
    {
        Long id = 1L;

        ResponseEntity<Void> response = controller.softDeleteModalidadeById(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(service).deleteModalidadeById(id);
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void shouldReactivateModalidadeAndReturnNoContent()
    {
        Long id = 1L;

        ResponseEntity<Void> response = controller.activateModalidadeById(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(service).reabrirModalidadeById(id);
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void shouldFindModalidadeByDescricao()
    {
        String descricao = "Prefeitura";
        Modalidade modalidade = builder.setDescricao(descricao).build();

        Mockito.when(service.findModalidadeByDescricao(descricao)).thenReturn(modalidade);

        ResponseEntity<ModalidadeResponseDto> response = controller.findModalidadeByDescricao(descricao);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ModalidadeResponseDto responseDto = response.getBody();

        assertEquals(modalidade.getId(), responseDto.id());
        assertEquals(modalidade.getDescricao(), responseDto.descricao());
        assertEquals(modalidade.getCnpj(), responseDto.cnpj());
        assertEquals(modalidade.getMaxVagas(), responseDto.maxVagas());
        assertEquals(modalidade.isAtivo(), responseDto.ativo());
        assertEquals(modalidade.isPagamento(), responseDto.pagamento());
        assertEquals(modalidade.getCor(), responseDto.cor());

        Mockito.verify(service).findModalidadeByDescricao(descricao);
        Mockito.verifyNoMoreInteractions(service);
    }
}