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
        ModalidadeRequestDto request = new ModalidadeRequestDto(criada.getDescricao(), criada.getCnpj(), criada.getMaxVagas(), criada.isPagamento());

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

        ArgumentCaptor<Modalidade> captor = ArgumentCaptor.forClass(Modalidade.class);

        Mockito.verify(service).createModalidade(captor.capture());

        Modalidade enviadaAoService = captor.getValue();

        assertNull(enviadaAoService.getId());
        assertEquals(request.descricao(), enviadaAoService.getDescricao());
        assertEquals(request.cnpj(), enviadaAoService.getCnpj());
        assertEquals(request.maxVagas(), enviadaAoService.getMaxVagas());
        assertEquals(request.pagamento(), enviadaAoService.isPagamento());
        assertTrue(enviadaAoService.isAtivo());
    }

    @Test
    void shouldCreateModalidadeWithoutCnpj()
    {
        ModalidadeRequestDto request = new ModalidadeRequestDto("Modalidade", null, 20, true);

        Modalidade criada  = new Modalidade(1L, "Modalidade", 20, true, true);

        Mockito.when(service.createModalidade(Mockito.any(Modalidade.class))).thenReturn(criada);

        ResponseEntity<ModalidadeResponseDto> response = controller.createModalidade(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().cnpj());

        ArgumentCaptor<Modalidade> captor = ArgumentCaptor.forClass(Modalidade.class);
        Mockito.verify(service).createModalidade(captor.capture());

        assertNull(captor.getValue().getCnpj());
    }


}