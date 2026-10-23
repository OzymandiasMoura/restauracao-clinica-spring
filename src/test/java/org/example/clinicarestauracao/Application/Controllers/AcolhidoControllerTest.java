package org.example.clinicarestauracao.Application.Controllers;

import org.example.clinicarestauracao.Application.Dtos.AcolhidoDtos.AcolhidoCreateRequestDto;
import org.example.clinicarestauracao.Application.Dtos.AcolhidoDtos.AcolhidoResponseDto;
import org.example.clinicarestauracao.Application.Dtos.AcolhidoDtos.AcolhidoUpdateRequestDto;
import org.example.clinicarestauracao.Application.Dtos.EnderecoDtos.EnderecoDto;
import org.example.clinicarestauracao.Application.Services.AcolhidoService;
import org.example.clinicarestauracao.Builders.AcolhidoTestBuilder;
import org.example.clinicarestauracao.Builders.EnderecoTestBuilder;
import org.example.clinicarestauracao.Domain.Entities.Acolhido;
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
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AcolhidoControllerTest
{
    @Mock
    private AcolhidoService service;

    @InjectMocks
    private AcolhidoController controller;

    @BeforeEach
    void setUpRequestContext()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/acolhidos");

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void clearRequestContext()
    {
        RequestContextHolder.resetRequestAttributes();
    }

    // Testes findAll

    @Test
    void shouldFindAllAcolhidosSuccessfully()
    {
        Acolhido primeiroAcolhido = AcolhidoTestBuilder.newAcolhido().setId(1L).setNome("Maria Silva").build();
        Acolhido segundoAcolhido = AcolhidoTestBuilder.newAcolhido().setId(2L).setNome("João da Silva").build();
        List<Acolhido> acolhidos = List.of(primeiroAcolhido, segundoAcolhido);

        Mockito.when(service.findAllAcolhidos()).thenReturn(acolhidos);

        ResponseEntity<List<AcolhidoResponseDto>> response = controller.findAllAcolhidos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        AcolhidoResponseDto primeiroResponse = response.getBody().getFirst();

        assertEquals(primeiroAcolhido.getId(), primeiroResponse.id());
        assertEquals(primeiroAcolhido.getNome(), primeiroResponse.nome());
        assertEquals(primeiroAcolhido.getCpf(), primeiroResponse.cpf());
        assertEquals(primeiroAcolhido.getEmail(), primeiroResponse.email());
        assertEquals(primeiroAcolhido.getDataNascimento(), primeiroResponse.dataNascimento());
        assertEquals(primeiroAcolhido.getCep(), primeiroResponse.cep());
        assertEquals(primeiroAcolhido.getTelefone(), primeiroResponse.telefone());
        assertEquals(primeiroAcolhido.getAtivo(), primeiroResponse.ativo());
        assertNotNull(primeiroResponse.endereco());

        AcolhidoResponseDto segundoResponse = response.getBody().get(1);

        assertEquals(segundoAcolhido.getId(), segundoResponse.id());
        assertEquals(segundoAcolhido.getNome(), segundoResponse.nome());
        assertNotNull(segundoResponse.endereco());

        Mockito.verify(service).findAllAcolhidos();
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoAcolhidos()
    {
        Mockito.when(service.findAllAcolhidos()).thenReturn(List.of());

        ResponseEntity<List<AcolhidoResponseDto>> response = controller.findAllAcolhidos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        Mockito.verify(service).findAllAcolhidos();
    }


    //Testes findById
    @Test
    void shouldFindAcolhidoByIdSuccessfully()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setId(1L).build();

        Mockito.when(service.findAcolhidoById(1L)).thenReturn(acolhido);

        ResponseEntity<AcolhidoResponseDto> response = controller.findAcolhidoById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        AcolhidoResponseDto responseBody = response.getBody();

        assertEquals(acolhido.getId(), responseBody.id());
        assertEquals(acolhido.getNome(), responseBody.nome());
        assertEquals(acolhido.getCpf(), responseBody.cpf());
        assertEquals(acolhido.getEmail(), responseBody.email());
        assertEquals(acolhido.getDataNascimento(), responseBody.dataNascimento());
        assertEquals(acolhido.getCep(), responseBody.cep());
        assertEquals(acolhido.getTelefone(), responseBody.telefone());
        assertEquals(acolhido.getAtivo(), responseBody.ativo());
        assertNotNull(responseBody.endereco());
        assertEquals(acolhido.getEndereco().getLogradouro(), responseBody.endereco().logradouro());
        assertEquals(acolhido.getEndereco().getNumero(), responseBody.endereco().numero());
        assertEquals(acolhido.getEndereco().getBairro(), responseBody.endereco().bairro());
        assertEquals(acolhido.getEndereco().getCidade(), responseBody.endereco().cidade());
        assertEquals(acolhido.getEndereco().getEstado(), responseBody.endereco().estado());
        assertEquals(acolhido.getEndereco().getComplemento(), responseBody.endereco().complemento());

        Mockito.verify(service).findAcolhidoById(1L);
    }

    // Testes findByNome

    @Test
    void shouldFindAcolhidosByNomeSuccessfully()
    {
        Acolhido primeiroAcolhido = AcolhidoTestBuilder.newAcolhido().setId(1L).setNome("Maria Silva").build();
        Acolhido segundoAcolhido = AcolhidoTestBuilder.newAcolhido().setId(2L).setNome("Maria Souza").build();
        List<Acolhido> acolhidos = List.of(primeiroAcolhido, segundoAcolhido);

        Mockito.when(service.findAcolhidoByNome("  maria  ")).thenReturn(acolhidos);

        ResponseEntity<List<AcolhidoResponseDto>> response = controller.findAcolhidoByNome("  maria  ");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        AcolhidoResponseDto primeiroResponse = response.getBody().getFirst();

        assertEquals(primeiroAcolhido.getId(), primeiroResponse.id());
        assertEquals(primeiroAcolhido.getNome(), primeiroResponse.nome());
        assertEquals(primeiroAcolhido.getCpf(), primeiroResponse.cpf());
        assertEquals(primeiroAcolhido.getEmail(), primeiroResponse.email());
        assertNotNull(primeiroResponse.endereco());

        AcolhidoResponseDto segundoResponse = response.getBody().get(1);

        assertEquals(segundoAcolhido.getId(), segundoResponse.id());
        assertEquals(segundoAcolhido.getNome(), segundoResponse.nome());
        assertNotNull(segundoResponse.endereco());

        Mockito.verify(service).findAcolhidoByNome("  maria  ");
    }

    // Testes findByEmail

    @Test
    void shouldFindAcolhidoByEmailSuccessfully()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setId(1L).setEmail("maria@email.com").build();
        String emailInformado = "  MARIA@EMAIL.COM  ";

        Mockito.when(service.findAcolhidoByEmail(emailInformado)).thenReturn(acolhido);

        ResponseEntity<AcolhidoResponseDto> response = controller.findAcolhidoByEmail(emailInformado);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        AcolhidoResponseDto responseBody = response.getBody();

        assertEquals(acolhido.getId(), responseBody.id());
        assertEquals(acolhido.getNome(), responseBody.nome());
        assertEquals(acolhido.getCpf(), responseBody.cpf());
        assertEquals(acolhido.getEmail(), responseBody.email());
        assertEquals(acolhido.getDataNascimento(), responseBody.dataNascimento());
        assertEquals(acolhido.getCep(), responseBody.cep());
        assertEquals(acolhido.getTelefone(), responseBody.telefone());
        assertEquals(acolhido.getAtivo(), responseBody.ativo());
        assertNotNull(responseBody.endereco());

        Mockito.verify(service).findAcolhidoByEmail(emailInformado);
    }

    // Testes findByCpf

    @Test
    void shouldFindAcolhidoByCpfSuccessfully()
    {
        Acolhido acolhido = AcolhidoTestBuilder.newAcolhido().setId(1L).setCpf("52998224725").build();
        String cpfInformado = "  529.982.247-25  ";

        Mockito.when(service.findAcolhidoByCpf(cpfInformado)).thenReturn(acolhido);

        ResponseEntity<AcolhidoResponseDto> response = controller.findAcolhidoByCpf(cpfInformado);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        AcolhidoResponseDto responseBody = response.getBody();

        assertEquals(acolhido.getId(), responseBody.id());
        assertEquals(acolhido.getNome(), responseBody.nome());
        assertEquals(acolhido.getCpf(), responseBody.cpf());
        assertEquals(acolhido.getEmail(), responseBody.email());
        assertEquals(acolhido.getDataNascimento(), responseBody.dataNascimento());
        assertEquals(acolhido.getCep(), responseBody.cep());
        assertEquals(acolhido.getTelefone(), responseBody.telefone());
        assertEquals(acolhido.getAtivo(), responseBody.ativo());
        assertNotNull(responseBody.endereco());

        Mockito.verify(service).findAcolhidoByCpf(cpfInformado);
    }

    // Testes create

    @Test
    void shouldCreateAcolhidoSuccessfully()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().build();
        EnderecoDto enderecoDto = new EnderecoDto(endereco.getLogradouro(), endereco.getNumero(), endereco.getBairro(), endereco.getCidade(), endereco.getEstado(), endereco.getComplemento());
        LocalDate dataNascimento = LocalDate.of(1999, 9, 9);
        AcolhidoCreateRequestDto requestDto = new AcolhidoCreateRequestDto("Maria Silva", "52998224725", "maria@email.com", dataNascimento, enderecoDto, "01001000", "11999999999");

        Acolhido acolhidoCriado = AcolhidoTestBuilder.newAcolhido().setId(10L).setNome(requestDto.nome()).setCpf(requestDto.cpf()).setEmail(requestDto.email()).setDataNascimento(requestDto.dataNascimento()).setEndereco(endereco).setCep(requestDto.cep()).setTelefone(requestDto.telefone()).build();

        Mockito.when(service.createAcolhido(Mockito.any(Acolhido.class))).thenReturn(acolhidoCriado);

        ResponseEntity<AcolhidoResponseDto> response = controller.createAcolhido(requestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(URI.create("http://localhost/acolhidos/10"), response.getHeaders().getLocation());
        assertNotNull(response.getBody());

        AcolhidoResponseDto responseBody = response.getBody();

        assertEquals(acolhidoCriado.getId(), responseBody.id());
        assertEquals(acolhidoCriado.getNome(), responseBody.nome());
        assertEquals(acolhidoCriado.getCpf(), responseBody.cpf());
        assertEquals(acolhidoCriado.getEmail(), responseBody.email());
        assertEquals(acolhidoCriado.getDataNascimento(), responseBody.dataNascimento());
        assertEquals(acolhidoCriado.getCep(), responseBody.cep());
        assertEquals(acolhidoCriado.getTelefone(), responseBody.telefone());
        assertTrue(responseBody.ativo());
        assertNotNull(responseBody.endereco());

        ArgumentCaptor<Acolhido> acolhidoCaptor = ArgumentCaptor.forClass(Acolhido.class);

        Mockito.verify(service).createAcolhido(acolhidoCaptor.capture());

        Acolhido acolhidoEnviado = acolhidoCaptor.getValue();

        assertNull(acolhidoEnviado.getId());
        assertEquals(requestDto.nome(), acolhidoEnviado.getNome());
        assertEquals(requestDto.cpf(), acolhidoEnviado.getCpf());
        assertEquals(requestDto.email(), acolhidoEnviado.getEmail());
        assertEquals(requestDto.dataNascimento(), acolhidoEnviado.getDataNascimento());
        assertEquals(requestDto.cep(), acolhidoEnviado.getCep());
        assertEquals(requestDto.telefone(), acolhidoEnviado.getTelefone());
        assertTrue(acolhidoEnviado.getAtivo());
        assertNotNull(acolhidoEnviado.getEndereco());
        assertEquals(enderecoDto.logradouro(), acolhidoEnviado.getEndereco().getLogradouro());
        assertEquals(enderecoDto.numero(), acolhidoEnviado.getEndereco().getNumero());
        assertEquals(enderecoDto.bairro(), acolhidoEnviado.getEndereco().getBairro());
        assertEquals(enderecoDto.cidade(), acolhidoEnviado.getEndereco().getCidade());
        assertEquals(enderecoDto.estado(), acolhidoEnviado.getEndereco().getEstado());
        assertEquals(enderecoDto.complemento(), acolhidoEnviado.getEndereco().getComplemento());
    }

    // Testes update

    @Test
    void shouldUpdateAcolhidoSuccessfully()
    {
        Endereco endereco = EnderecoTestBuilder.newEndereco().setLogradouro("Avenida Paulista").setNumero(1000).setBairro("Bela Vista").setCidade("São Paulo").setEstado("SP").setComplemento("Apartamento 10").build();
        EnderecoDto enderecoDto = new EnderecoDto(endereco.getLogradouro(), endereco.getNumero(), endereco.getBairro(), endereco.getCidade(), endereco.getEstado(), endereco.getComplemento());
        AcolhidoUpdateRequestDto requestDto = new AcolhidoUpdateRequestDto("Maria Souza", "11144477735", "maria.souza@email.com", LocalDate.of(1998, 5, 20), enderecoDto, "01310100", "11988887777");
        Acolhido acolhidoAtualizado = AcolhidoTestBuilder.newAcolhido().setId(1L).setNome(requestDto.nome()).setCpf(requestDto.cpf()).setEmail(requestDto.email()).setDataNascimento(requestDto.dataNascimento()).setEndereco(endereco).setCep(requestDto.cep()).setTelefone(requestDto.telefone()).build();

        Mockito.when(service.updateAcolhido(Mockito.eq(1L), Mockito.any(Acolhido.class))).thenReturn(acolhidoAtualizado);

        ResponseEntity<AcolhidoResponseDto> response = controller.updateAcolhido(1L, requestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        AcolhidoResponseDto responseBody = response.getBody();

        assertEquals(acolhidoAtualizado.getId(), responseBody.id());
        assertEquals(acolhidoAtualizado.getNome(), responseBody.nome());
        assertEquals(acolhidoAtualizado.getCpf(), responseBody.cpf());
        assertEquals(acolhidoAtualizado.getEmail(), responseBody.email());
        assertEquals(acolhidoAtualizado.getDataNascimento(), responseBody.dataNascimento());
        assertEquals(acolhidoAtualizado.getCep(), responseBody.cep());
        assertEquals(acolhidoAtualizado.getTelefone(), responseBody.telefone());
        assertEquals(acolhidoAtualizado.getAtivo(), responseBody.ativo());
        assertNotNull(responseBody.endereco());

        ArgumentCaptor<Acolhido> acolhidoCaptor = ArgumentCaptor.forClass(Acolhido.class);

        Mockito.verify(service).updateAcolhido(Mockito.eq(1L), acolhidoCaptor.capture());

        Acolhido acolhidoEnviado = acolhidoCaptor.getValue();

        assertNull(acolhidoEnviado.getId());
        assertEquals(requestDto.nome(), acolhidoEnviado.getNome());
        assertEquals(requestDto.cpf(), acolhidoEnviado.getCpf());
        assertEquals(requestDto.email(), acolhidoEnviado.getEmail());
        assertEquals(requestDto.dataNascimento(), acolhidoEnviado.getDataNascimento());
        assertEquals(requestDto.cep(), acolhidoEnviado.getCep());
        assertEquals(requestDto.telefone(), acolhidoEnviado.getTelefone());
        assertNotNull(acolhidoEnviado.getEndereco());
        assertEquals(enderecoDto.logradouro(), acolhidoEnviado.getEndereco().getLogradouro());
        assertEquals(enderecoDto.numero(), acolhidoEnviado.getEndereco().getNumero());
        assertEquals(enderecoDto.bairro(), acolhidoEnviado.getEndereco().getBairro());
        assertEquals(enderecoDto.cidade(), acolhidoEnviado.getEndereco().getCidade());
        assertEquals(enderecoDto.estado(), acolhidoEnviado.getEndereco().getEstado());
        assertEquals(enderecoDto.complemento(), acolhidoEnviado.getEndereco().getComplemento());
    }

    // Testes deactivate

    @Test
    void shouldDeactivateAcolhidoSuccessfully()
    {
        ResponseEntity<Void> response = controller.deactivateAcolhido(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(service).deactivateAcolhido(1L);
    }

    // Testes reactivate

    @Test
    void shouldReactivateAcolhidoSuccessfully()
    {
        ResponseEntity<Void> response = controller.reactivateAcolhido(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(service).reactivateAcolhido(1L);
    }

    // Testes delete

    @Test
    void shouldDeleteAcolhidoSuccessfully()
    {
        ResponseEntity<Void> response = controller.deleteAcolhidoById(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(service).deleteAcolhidoById(1L);
    }

}