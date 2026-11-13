package org.example.clinicarestauracao.Application.Controllers;

import org.example.clinicarestauracao.Application.Dtos.EnderecoDtos.EnderecoDto;
import org.example.clinicarestauracao.Application.Dtos.ResponsavelDtos.ResponsavelCreateRequestDto;
import org.example.clinicarestauracao.Application.Dtos.ResponsavelDtos.ResponsavelResponseDto;
import org.example.clinicarestauracao.Application.Dtos.ResponsavelDtos.ResponsavelUpdateRequestDto;
import org.example.clinicarestauracao.Application.Services.ResponsavelService;
import org.example.clinicarestauracao.Builders.EnderecoTestBuilder;
import org.example.clinicarestauracao.Builders.ResponsavelTestBuilder;
import org.example.clinicarestauracao.Domain.Entities.Responsavel;
import org.example.clinicarestauracao.Domain.ValueObjects.Endereco;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ResponsavelControllerTest
{
    @Mock
    private ResponsavelService service;

    @InjectMocks
    private ResponsavelController controller;

    @BeforeEach
    void setUpRequestContext()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/responsaveis");

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request)
        );
    }

    @AfterEach
    void clearRequestContext()
    {
        RequestContextHolder.resetRequestAttributes();
    }

    // Testes do findAllResponsaveis

    @Test
    void shouldFindAllResponsaveisSuccessfully()
    {
        Responsavel primeiroResponsavel = ResponsavelTestBuilder.newResponsavel().setId(1L).setNome("João da Silva").build();
        Responsavel segundoResponsavel = ResponsavelTestBuilder.newResponsavel().setId(2L).setNome("Maria da Silva").setAtivo(false).build();

        Mockito.when(service.findAll()).thenReturn(List.of(primeiroResponsavel, segundoResponsavel));

        ResponseEntity<List<ResponsavelResponseDto>> response = controller.findAllResponsaveis();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        List<ResponsavelResponseDto> responseBody = response.getBody();

        assertAll(
                () -> assertEquals(2, responseBody.size()),
                () -> assertEquals(1L, responseBody.getFirst().id()),
                () -> assertEquals("João da Silva", responseBody.getFirst().nome()),
                () -> assertTrue(responseBody.getFirst().ativo()),
                () -> assertEquals(2L, responseBody.get(1).id()),
                () -> assertEquals("Maria da Silva", responseBody.get(1).nome()),
                () -> assertFalse(responseBody.get(1).ativo()),
                () -> assertNotNull(responseBody.getFirst().endereco()),
                () -> assertNotNull(responseBody.get(1).endereco())
        );

        Mockito.verify(service).findAll();
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoResponsaveis()
    {
        Mockito.when(service.findAll()).thenReturn(List.of());

        ResponseEntity<List<ResponsavelResponseDto>> response = controller.findAllResponsaveis();

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertTrue(response.getBody().isEmpty())
        );

        Mockito.verify(service).findAll();
        Mockito.verifyNoMoreInteractions(service);
    }


    // Testes do findResponsavelById

    @Test
    void shouldFindResponsavelByIdSuccessfully()
    {
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setId(1L).build();

        Mockito.when(service.findResponsavelById(1L)).thenReturn(responsavel);

        ResponseEntity<ResponsavelResponseDto> response = controller.findResponsavelById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ResponsavelResponseDto responseBody = response.getBody();

        assertAll(
                () -> assertEquals(responsavel.getId(), responseBody.id()),
                () -> assertEquals(responsavel.getNome(), responseBody.nome()),
                () -> assertEquals(responsavel.getCpf(), responseBody.cpf()),
                () -> assertEquals(responsavel.getEmail(), responseBody.email()),
                () -> assertEquals(responsavel.getCep(), responseBody.cep()),
                () -> assertEquals(responsavel.getTelefone(), responseBody.telefone()),
                () -> assertEquals(responsavel.isAtivo(), responseBody.ativo()),
                () -> assertNotNull(responseBody.endereco()),
                () -> assertEquals(responsavel.getEndereco().getLogradouro(), responseBody.endereco().logradouro()),
                () -> assertEquals(responsavel.getEndereco().getNumero(), responseBody.endereco().numero()),
                () -> assertEquals(responsavel.getEndereco().getBairro(), responseBody.endereco().bairro()),
                () -> assertEquals(responsavel.getEndereco().getCidade(), responseBody.endereco().cidade()),
                () -> assertEquals(responsavel.getEndereco().getEstado(), responseBody.endereco().estado()),
                () -> assertEquals(responsavel.getEndereco().getComplemento(), responseBody.endereco().complemento())
        );

        Mockito.verify(service).findResponsavelById(1L);
        Mockito.verifyNoMoreInteractions(service);
    }

    // Testes do findResponsavelByEmail

    @Test
    void shouldFindResponsavelByEmailSuccessfully()
    {
        String emailRecebido = "  JOAO.SILVA@EMAIL.COM  ";
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setId(1L).setEmail("joao.silva@email.com").build();

        Mockito.when(service.findResponsavelByEmail(emailRecebido)).thenReturn(responsavel);

        ResponseEntity<ResponsavelResponseDto> response = controller.findResponsavelByEmail(emailRecebido);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ResponsavelResponseDto responseBody = response.getBody();

        assertAll(
                () -> assertEquals(responsavel.getId(), responseBody.id()),
                () -> assertEquals(responsavel.getNome(), responseBody.nome()),
                () -> assertEquals(responsavel.getEmail(), responseBody.email()),
                () -> assertEquals(responsavel.isAtivo(), responseBody.ativo()),
                () -> assertNotNull(responseBody.endereco())
        );

        Mockito.verify(service).findResponsavelByEmail(emailRecebido);
        Mockito.verifyNoMoreInteractions(service);
    }

    // Testes do findResponsavelByCpf

    @Test
    void shouldFindResponsavelByCpfSuccessfully()
    {
        String cpfRecebido = "  168.995.350-09  ";
        Responsavel responsavel = ResponsavelTestBuilder.newResponsavel().setId(1L).setCpf("16899535009").build();

        Mockito.when(service.findResponsavelByCpf(cpfRecebido)).thenReturn(responsavel);

        ResponseEntity<ResponsavelResponseDto> response = controller.findResponsavelByCpf(cpfRecebido);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ResponsavelResponseDto responseBody = response.getBody();

        assertAll(
                () -> assertEquals(responsavel.getId(), responseBody.id()),
                () -> assertEquals(responsavel.getNome(), responseBody.nome()),
                () -> assertEquals(responsavel.getCpf(), responseBody.cpf()),
                () -> assertEquals(responsavel.isAtivo(), responseBody.ativo()),
                () -> assertNotNull(responseBody.endereco())
        );

        Mockito.verify(service).findResponsavelByCpf(cpfRecebido);
        Mockito.verifyNoMoreInteractions(service);
    }


    // Testes do findResponsaveisByNome

    @Test
    void shouldFindResponsaveisByNomeSuccessfully()
    {
        String nomeRecebido = "  maria  ";
        Responsavel primeiroResponsavel = ResponsavelTestBuilder.newResponsavel().setId(1L).setNome("Maria Silva").build();
        Responsavel segundoResponsavel = ResponsavelTestBuilder.newResponsavel().setId(2L).setNome("Maria Souza").build();
        List<Responsavel> responsaveis = List.of(primeiroResponsavel, segundoResponsavel);

        Mockito.when(service.findResponsavelByNome(nomeRecebido)).thenReturn(responsaveis);

        ResponseEntity<List<ResponsavelResponseDto>> response = controller.findResponsaveisByNome(nomeRecebido);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        ResponsavelResponseDto primeiroResponse = response.getBody().getFirst();
        ResponsavelResponseDto segundoResponse = response.getBody().get(1);

        assertAll(
                () -> assertEquals(primeiroResponsavel.getId(), primeiroResponse.id()),
                () -> assertEquals(primeiroResponsavel.getNome(), primeiroResponse.nome()),
                () -> assertEquals(primeiroResponsavel.getCpf(), primeiroResponse.cpf()),
                () -> assertNotNull(primeiroResponse.endereco()),
                () -> assertEquals(segundoResponsavel.getId(), segundoResponse.id()),
                () -> assertEquals(segundoResponsavel.getNome(), segundoResponse.nome()),
                () -> assertEquals(segundoResponsavel.getEmail(), segundoResponse.email()),
                () -> assertNotNull(segundoResponse.endereco())
        );

        Mockito.verify(service).findResponsavelByNome(nomeRecebido);
        Mockito.verifyNoMoreInteractions(service);
    }

    // Testes do createResponsavel

    @Test
    void shouldCreateResponsavelSuccessfully()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().build();
        EnderecoDto enderecoDto = new EnderecoDto(endereco.getLogradouro(), endereco.getNumero(), endereco.getBairro(), endereco.getCidade(), endereco.getEstado(), endereco.getComplemento());
        ResponsavelCreateRequestDto requestDto = new ResponsavelCreateRequestDto("João da Silva", "16899535009", "joao.silva@email.com", enderecoDto, "01001000", "11999999999");
        Responsavel responsavelCriado = ResponsavelTestBuilder.newResponsavel().setId(10L).setNome(requestDto.nome()).setCpf(requestDto.cpf()).setEmail(requestDto.email()).setEndereco(endereco).setCep(requestDto.cep()).setTelefone(requestDto.telefone()).build();

        Mockito.when(service.createResponsavel(Mockito.any(Responsavel.class))).thenReturn(responsavelCriado);

        ResponseEntity<ResponsavelResponseDto> response = controller.createResponsavel(requestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(URI.create("http://localhost/responsaveis/10"), response.getHeaders().getLocation());
        assertNotNull(response.getBody());

        ResponsavelResponseDto responseBody = response.getBody();

        assertAll(
                () -> assertEquals(responsavelCriado.getId(), responseBody.id()),
                () -> assertEquals(responsavelCriado.getNome(), responseBody.nome()),
                () -> assertEquals(responsavelCriado.getCpf(), responseBody.cpf()),
                () -> assertEquals(responsavelCriado.getEmail(), responseBody.email()),
                () -> assertEquals(responsavelCriado.getCep(), responseBody.cep()),
                () -> assertEquals(responsavelCriado.getTelefone(), responseBody.telefone()),
                () -> assertTrue(responseBody.ativo()),
                () -> assertNotNull(responseBody.endereco())
        );

        ArgumentCaptor<Responsavel> responsavelCaptor = ArgumentCaptor.forClass(Responsavel.class);

        Mockito.verify(service).createResponsavel(responsavelCaptor.capture());

        Responsavel responsavelEnviado = responsavelCaptor.getValue();

        assertAll(
                () -> assertNull(responsavelEnviado.getId()),
                () -> assertTrue(responsavelEnviado.isAtivo()),
                () -> assertEquals(requestDto.nome(), responsavelEnviado.getNome()),
                () -> assertEquals(requestDto.cpf(), responsavelEnviado.getCpf()),
                () -> assertEquals(requestDto.email(), responsavelEnviado.getEmail()),
                () -> assertEquals(requestDto.cep(), responsavelEnviado.getCep()),
                () -> assertEquals(requestDto.telefone(), responsavelEnviado.getTelefone()),
                () -> assertEquals(enderecoDto.logradouro(), responsavelEnviado.getEndereco().getLogradouro()),
                () -> assertEquals(enderecoDto.numero(), responsavelEnviado.getEndereco().getNumero()),
                () -> assertEquals(enderecoDto.bairro(), responsavelEnviado.getEndereco().getBairro()),
                () -> assertEquals(enderecoDto.cidade(), responsavelEnviado.getEndereco().getCidade()),
                () -> assertEquals(enderecoDto.estado(), responsavelEnviado.getEndereco().getEstado()),
                () -> assertEquals(enderecoDto.complemento(), responsavelEnviado.getEndereco().getComplemento())
        );

        Mockito.verifyNoMoreInteractions(service);
    }

    // Testes do updateResponsavel

    @Test
    void shouldUpdateResponsavelSuccessfully()
    {
        Endereco enderecoAtualizado = EnderecoTestBuilder.newEndereco().setLogradouro("Avenida Paulista").setNumero(1000).setBairro("Bela Vista").setCidade("São Paulo").setEstado("SP").setComplemento("Apartamento 10").build();
        EnderecoDto enderecoDto = new EnderecoDto(enderecoAtualizado.getLogradouro(), enderecoAtualizado.getNumero(), enderecoAtualizado.getBairro(), enderecoAtualizado.getCidade(), enderecoAtualizado.getEstado(), enderecoAtualizado.getComplemento());
        ResponsavelUpdateRequestDto requestDto = new ResponsavelUpdateRequestDto("Maria Atualizada", "52998224725", "maria.atualizada@email.com", enderecoDto, "01310100", "11988887777");
        Responsavel responsavelAtualizado = ResponsavelTestBuilder.newResponsavel().setId(1L).setNome(requestDto.nome()).setCpf(requestDto.cpf()).setEmail(requestDto.email()).setEndereco(enderecoAtualizado).setCep(requestDto.cep()).setTelefone(requestDto.telefone()).setAtivo(false).build();

        Mockito.when(service.updateResponsavel(Mockito.eq(1L), Mockito.any(Responsavel.class))).thenReturn(responsavelAtualizado);

        ResponseEntity<ResponsavelResponseDto> response = controller.updateResponsavel(1L, requestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ResponsavelResponseDto responseBody = response.getBody();

        assertAll(
                () -> assertEquals(responsavelAtualizado.getId(), responseBody.id()),
                () -> assertEquals(requestDto.nome(), responseBody.nome()),
                () -> assertEquals(requestDto.cpf(), responseBody.cpf()),
                () -> assertEquals(requestDto.email(), responseBody.email()),
                () -> assertEquals(requestDto.cep(), responseBody.cep()),
                () -> assertEquals(requestDto.telefone(), responseBody.telefone()),
                () -> assertFalse(responseBody.ativo()),
                () -> assertNotNull(responseBody.endereco())
        );

        ArgumentCaptor<Responsavel> responsavelCaptor = ArgumentCaptor.forClass(Responsavel.class);

        Mockito.verify(service).updateResponsavel(Mockito.eq(1L), responsavelCaptor.capture());

        Responsavel responsavelEnviado = responsavelCaptor.getValue();

        assertAll(
                () -> assertNull(responsavelEnviado.getId()),
                () -> assertTrue(responsavelEnviado.isAtivo()),
                () -> assertEquals(requestDto.nome(), responsavelEnviado.getNome()),
                () -> assertEquals(requestDto.cpf(), responsavelEnviado.getCpf()),
                () -> assertEquals(requestDto.email(), responsavelEnviado.getEmail()),
                () -> assertEquals(requestDto.cep(), responsavelEnviado.getCep()),
                () -> assertEquals(requestDto.telefone(), responsavelEnviado.getTelefone()),
                () -> assertEquals(enderecoDto.logradouro(), responsavelEnviado.getEndereco().getLogradouro()),
                () -> assertEquals(enderecoDto.numero(), responsavelEnviado.getEndereco().getNumero()),
                () -> assertEquals(enderecoDto.bairro(), responsavelEnviado.getEndereco().getBairro()),
                () -> assertEquals(enderecoDto.cidade(), responsavelEnviado.getEndereco().getCidade()),
                () -> assertEquals(enderecoDto.estado(), responsavelEnviado.getEndereco().getEstado()),
                () -> assertEquals(enderecoDto.complemento(), responsavelEnviado.getEndereco().getComplemento())
        );

        Mockito.verifyNoMoreInteractions(service);
    }

    // Testes do deactivateResponsavel

    @Test
    void shouldDeactivateResponsavelSuccessfully()
    {
        Responsavel responsavelDesativado = ResponsavelTestBuilder.newResponsavel().setId(1L).setAtivo(false).build();

        Mockito.when(service.deactivateResponsavel(1L)).thenReturn(responsavelDesativado);

        ResponseEntity<ResponsavelResponseDto> response = controller.deactivateResponsavel(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ResponsavelResponseDto responseBody = response.getBody();

        assertAll(
                () -> assertEquals(responsavelDesativado.getId(), responseBody.id()),
                () -> assertEquals(responsavelDesativado.getNome(), responseBody.nome()),
                () -> assertEquals(responsavelDesativado.getCpf(), responseBody.cpf()),
                () -> assertEquals(responsavelDesativado.getEmail(), responseBody.email()),
                () -> assertFalse(responseBody.ativo()),
                () -> assertNotNull(responseBody.endereco())
        );

        Mockito.verify(service).deactivateResponsavel(1L);
        Mockito.verifyNoMoreInteractions(service);
    }

    // Testes do reactivateResponsavel

    @Test
    void shouldReactivateResponsavelSuccessfully()
    {
        Responsavel responsavelReativado = ResponsavelTestBuilder.newResponsavel().setId(1L).setAtivo(true).build();

        Mockito.when(service.reactivateResponsavel(1L)).thenReturn(responsavelReativado);

        ResponseEntity<ResponsavelResponseDto> response = controller.reactivateResponsavel(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ResponsavelResponseDto responseBody = response.getBody();

        assertAll(
                () -> assertEquals(responsavelReativado.getId(), responseBody.id()),
                () -> assertEquals(responsavelReativado.getNome(), responseBody.nome()),
                () -> assertEquals(responsavelReativado.getCpf(), responseBody.cpf()),
                () -> assertEquals(responsavelReativado.getEmail(), responseBody.email()),
                () -> assertTrue(responseBody.ativo()),
                () -> assertNotNull(responseBody.endereco())
        );

        Mockito.verify(service).reactivateResponsavel(1L);
        Mockito.verifyNoMoreInteractions(service);
    }

    // Testes do deleteResponsavel

    @Test
    void shouldDeleteResponsavelSuccessfully()
    {
        ResponseEntity<Void> response = controller.deleteResponsavel(1L);

        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()),
                () -> assertNull(response.getBody())
        );

        Mockito.verify(service).deleteResponsavel(1L);
        Mockito.verifyNoMoreInteractions(service);
    }

}