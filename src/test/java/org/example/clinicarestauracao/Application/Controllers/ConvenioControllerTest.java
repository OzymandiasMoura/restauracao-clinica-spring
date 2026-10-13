package org.example.clinicarestauracao.Application.Controllers;

import org.example.clinicarestauracao.Application.Dtos.ConvenioDtos.ConvenioRequestDto;
import org.example.clinicarestauracao.Application.Dtos.ConvenioDtos.ConvenioResponseDto;
import org.example.clinicarestauracao.Application.Exceptions.ConvenioWithInvalidInformationException;
import org.example.clinicarestauracao.Application.Services.ConvenioService;
import org.example.clinicarestauracao.Builders.ConvenioTestBuilder;
import org.example.clinicarestauracao.Domain.Entities.Convenio;
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
class ConvenioControllerTest
{
    private final ConvenioTestBuilder builder = new ConvenioTestBuilder();

    @Mock
    private ConvenioService service;
    @InjectMocks
    private ConvenioController controller;

    @BeforeEach
    void setUpRequestContext()
    {
        // Simula uma requisição HTTP para que o ServletUriComponentsBuilder consiga construir dinamicamente a URI do recurso criado.
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8080);
        request.setRequestURI("/convenios");

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void clearRequestContext()
    {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldCreateConvenioAndReturnCreated()
    {
        Convenio criada = builder.build();
        ConvenioRequestDto request = new ConvenioRequestDto(criada.getDescricao(), criada.getCnpj(), criada.getMaxVagas(), criada.isPagamento(), criada.getCor());

        Mockito.when(service.createConvenio(Mockito.any(Convenio.class))).thenReturn(criada);

        ResponseEntity<ConvenioResponseDto> response = controller.createConvenio(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(URI.create("http://localhost:8080/convenios/" + criada.getId()), response.getHeaders().getLocation());
        assertNotNull(response.getBody());
        assertEquals(criada.getId(), response.getBody().id());
        assertEquals(criada.getDescricao(), response.getBody().descricao());
        assertEquals(criada.getCnpj(), response.getBody().cnpj());
        assertEquals(criada.getMaxVagas(), response.getBody().maxVagas());
        assertEquals(criada.isPagamento(), response.getBody().pagamento());
        assertEquals(criada.isAtivo(), response.getBody().ativo());
        assertEquals(criada.getCor(), response.getBody().cor());

        ArgumentCaptor<Convenio> captor = ArgumentCaptor.forClass(Convenio.class);

        Mockito.verify(service).createConvenio(captor.capture());

        Convenio enviadaAoService = captor.getValue();

        assertNull(enviadaAoService.getId());
        assertEquals(request.descricao(), enviadaAoService.getDescricao());
        assertEquals(request.cnpj(), enviadaAoService.getCnpj());
        assertEquals(request.maxVagas(), enviadaAoService.getMaxVagas());
        assertEquals(request.pagamento(), enviadaAoService.isPagamento());
        assertTrue(enviadaAoService.isAtivo());
        assertEquals(request.cor(), enviadaAoService.getCor());
    }

    @Test
    void shouldCreateConvenioWithoutCnpj()
    {
        ConvenioRequestDto request = new ConvenioRequestDto("Convênio", null, 20, true, "#FF5733");

        Convenio criada = new Convenio(1L, "Convênio", 20, true, true, "#FF5733");

        Mockito.when(service.createConvenio(Mockito.any(Convenio.class))).thenReturn(criada);

        ResponseEntity<ConvenioResponseDto> response = controller.createConvenio(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().cnpj());

        ArgumentCaptor<Convenio> captor = ArgumentCaptor.forClass(Convenio.class);
        Mockito.verify(service).createConvenio(captor.capture());

        assertNull(captor.getValue().getCnpj());
    }

    @Test
    void shouldFindAllConveniosAndReturnOk()
    {
        Convenio primeira = builder.build();
        Convenio segunda = new ConvenioTestBuilder().setId(2L).setDescricao("Prefeitura").setCNPJ("11444777000161").setMaxVagas(15).setPagamento(false).setAtivo(true).build();

        Mockito.when(service.findAllConvenios()).thenReturn(List.of(primeira, segunda));

        ResponseEntity<List<ConvenioResponseDto>> response = controller.findAllConvenios();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        ConvenioResponseDto responseDto = response.getBody().getFirst();

        assertEquals(primeira.getId(), responseDto.id());
        assertEquals(primeira.getDescricao(), responseDto.descricao());
        assertEquals(primeira.getCnpj(), responseDto.cnpj());
        assertEquals(primeira.getMaxVagas(), responseDto.maxVagas());
        assertEquals(primeira.isAtivo(), responseDto.ativo());
        assertEquals(primeira.isPagamento(), responseDto.pagamento());
        assertEquals(primeira.getCor(), responseDto.cor());

        ConvenioResponseDto segundoDto = response.getBody().get(1);

        assertEquals(segunda.getId(), segundoDto.id());
        assertEquals(segunda.getDescricao(), segundoDto.descricao());

        Mockito.verify(service).findAllConvenios();
    }

    @Test
    void shouldReturnEmptyListWhenNoConveniosExist()
    {
        Mockito.when(service.findAllConvenios()).thenReturn(List.of());

        ResponseEntity<List<ConvenioResponseDto>> response = controller.findAllConvenios();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        Mockito.verify(service).findAllConvenios();
    }

    @Test
    void shouldFindConvenioByIdAndReturnOk()
    {
        Convenio convenio = builder.build();

        Mockito.when(service.findConvenioById(convenio.getId())).thenReturn(convenio);

        ResponseEntity<ConvenioResponseDto> response = controller.findConvenioById(convenio.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ConvenioResponseDto responseDto = response.getBody();
        assertEquals(convenio.getId(), responseDto.id());
        assertEquals(convenio.getDescricao(), responseDto.descricao());
        assertEquals(convenio.getCnpj(), responseDto.cnpj());
        assertEquals(convenio.getMaxVagas(), responseDto.maxVagas());
        assertEquals(convenio.isAtivo(), responseDto.ativo());
        assertEquals(convenio.isPagamento(), responseDto.pagamento());
        assertEquals(convenio.getCor(), responseDto.cor());

        Mockito.verify(service).findConvenioById(convenio.getId());
    }

    @Test
    void shouldUpdateConvenioAndReturnOk()
    {
        Long id = 1L;
        ConvenioRequestDto request = new ConvenioRequestDto("Prefeitura atualizada", "11222333000181", 30, false, "#FF5733");
        Convenio atualizada = builder.setId(id).setDescricao("Prefeitura atualizada").setCNPJ(request.cnpj()).setMaxVagas(30).setPagamento(false).setCor("#FF5733").build();

        Mockito.when(service.updateConvenio(Mockito.eq(id), Mockito.any(Convenio.class))). thenReturn(atualizada);

        ResponseEntity<ConvenioResponseDto> response = controller.updateConvenioById(id, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ConvenioResponseDto responseDto = response.getBody();

        assertEquals(atualizada.getId(), responseDto.id());
        assertEquals(atualizada.getDescricao(), responseDto.descricao());
        assertEquals(atualizada.getCnpj(), responseDto.cnpj());
        assertEquals(atualizada.getMaxVagas(), responseDto.maxVagas());
        assertEquals(atualizada.isAtivo(), responseDto.ativo());
        assertEquals(atualizada.isPagamento(), responseDto.pagamento());
        assertEquals(atualizada.getCor(), responseDto.cor());

        ArgumentCaptor<Convenio> captor = ArgumentCaptor.forClass(Convenio.class);
        Mockito.verify(service).updateConvenio(Mockito.eq(id),  captor.capture());

        Convenio convenio = captor.getValue();

        assertNull(convenio.getId());
        assertEquals(request.descricao(), convenio.getDescricao());
        assertEquals(request.cnpj(), convenio.getCnpj());
        assertEquals(request.maxVagas(), convenio.getMaxVagas());
        assertEquals(request.pagamento(), convenio.isPagamento());
        assertEquals(request.cor(), convenio.getCor());

        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void shouldDeleteConvenioAndReturnNoContent()
    {
        Long id = 1L;

        ResponseEntity<Void> response = controller.softDeleteConvenioById(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(service).deleteConvenioById(id);
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void shouldReactivateConvenioAndReturnNoContent()
    {
        Long id = 1L;

        ResponseEntity<Void> response = controller.activateConvenioById(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(service).reabrirConvenioById(id);
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void shouldFindConvenioByDescricao()
    {
        String descricao = "Prefeitura";
        Convenio convenio = builder.setDescricao(descricao).build();

        Mockito.when(service.findConvenioByDescricao(descricao)).thenReturn(convenio);

        ResponseEntity<ConvenioResponseDto> response = controller.findConvenioByDescricao(descricao);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ConvenioResponseDto responseDto = response.getBody();

        assertEquals(convenio.getId(), responseDto.id());
        assertEquals(convenio.getDescricao(), responseDto.descricao());
        assertEquals(convenio.getCnpj(), responseDto.cnpj());
        assertEquals(convenio.getMaxVagas(), responseDto.maxVagas());
        assertEquals(convenio.isAtivo(), responseDto.ativo());
        assertEquals(convenio.isPagamento(), responseDto.pagamento());
        assertEquals(convenio.getCor(), responseDto.cor());

        Mockito.verify(service).findConvenioByDescricao(descricao);
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void shouldNotCreateConvenioWhenMaximumVacanciesIsNull()
    {
        ConvenioRequestDto request = new ConvenioRequestDto(
                builder.getDescricao(),
                builder.getCnpj(),
                null,
                builder.isPagamento(),
                builder.getCor()
        );

        ConvenioWithInvalidInformationException exception = assertThrows(ConvenioWithInvalidInformationException.class, () -> controller.createConvenio(request));

        assertEquals("Numero de vagas não pode ser nulo.", exception.getMessage());
        Mockito.verifyNoInteractions(service);
    }
}
