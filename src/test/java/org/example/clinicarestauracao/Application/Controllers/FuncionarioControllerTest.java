package org.example.clinicarestauracao.Application.Controllers;

import org.example.clinicarestauracao.Application.Dtos.FuncionarioDtos.FuncionarioDismissalRequestDto;
import org.example.clinicarestauracao.Application.Dtos.FuncionarioDtos.FuncionarioRequestDto;
import org.example.clinicarestauracao.Application.Dtos.FuncionarioDtos.FuncionarioResponseDto;
import org.example.clinicarestauracao.Application.Dtos.FuncionarioDtos.FuncionarioUserRequestDto;
import org.example.clinicarestauracao.Application.Services.CargoService;
import org.example.clinicarestauracao.Application.Services.FuncionarioService;
import org.example.clinicarestauracao.Application.Services.UserService;
import org.example.clinicarestauracao.Builders.CargoTestBuilder;
import org.example.clinicarestauracao.Builders.FuncionarioTestBuilder;
import org.example.clinicarestauracao.Domain.Entities.Cargo;
import org.example.clinicarestauracao.Domain.Entities.Funcionario;
import org.example.clinicarestauracao.Domain.Entities.User;
import org.example.clinicarestauracao.Domain.Enums.UserRoles;
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
class FuncionarioControllerTest
{
    @Mock
    private FuncionarioService service;

    @Mock
    private UserService userService;

    @Mock
    private CargoService cargoService;

    @InjectMocks
    private FuncionarioController controller;

    @BeforeEach
    void setUpRequestContext()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/funcionarios");

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void clearRequestContext()
    {
        RequestContextHolder.resetRequestAttributes();
    }

    //Teste endpoint FindAll

    @Test
    void shouldReturnAllFuncionarios()
    {
        LocalDate today = LocalDate.now();
        LocalDate dataDemissao = today.minusDays(1);
        User user = new User(1L, "pedro", "senha123", UserRoles.USER);

        Funcionario ativo = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(user).setDataAdmissao(today.minusYears(2)).setAtivo(true).build();
        Funcionario demitido = FuncionarioTestBuilder.newFuncionario().setId(2L).setNome("Maria Silva").setCpf("11144477735").setEmail("maria@email.com").setUser(null).setDataAdmissao(today.minusYears(1)).setDataDemissao(dataDemissao).setAtivo(false).build();

        Mockito.when(service.findAllFuncionarios()).thenReturn(List.of(ativo, demitido));

        ResponseEntity<List<FuncionarioResponseDto>> response = controller.findAllFuncionarios();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        FuncionarioResponseDto firstResponse = response.getBody().getFirst();

        assertEquals(ativo.getId(), firstResponse.id());
        assertEquals(ativo.getNome(), firstResponse.nome());
        assertTrue(firstResponse.ativo());
        assertEquals(ativo.getDataAdmissao(), firstResponse.dataAdmissao());
        assertNull(firstResponse.dataDemissao());
        assertNotNull(firstResponse.cargo());
        assertNotNull(firstResponse.user());
        assertEquals(user.getId(), firstResponse.user().id());
        assertEquals(user.getUsername(), firstResponse.user().username());

        FuncionarioResponseDto segundo = response.getBody().get(1);

        assertEquals(demitido.getId(), segundo.id());
        assertEquals(demitido.getNome(), segundo.nome());
        assertFalse(segundo.ativo());
        assertEquals(demitido.getDataAdmissao(), segundo.dataAdmissao());
        assertEquals(dataDemissao, segundo.dataDemissao());
        assertNotNull(segundo.cargo());
        assertNull(segundo.user());

        Mockito.verify(service).findAllFuncionarios();
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoFuncionarios()
    {
        Mockito.when(service.findAllFuncionarios()).thenReturn(List.of());

        ResponseEntity<List<FuncionarioResponseDto>> response = controller.findAllFuncionarios();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        Mockito.verify(service).findAllFuncionarios();
    }

    //Testes findById
    @Test
    void shouldFindFuncionarioByIdSuccessfully()
    {
        LocalDate hoje = LocalDate.now();
        LocalDate dataAdmissao = hoje.minusYears(1);
        LocalDate dataDemissao = hoje.minusDays(1);
        User user = new User(1L, "pedro", "senha123", UserRoles.USER);

        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setId(1L).setUser(user).setAtivo(false).setDataAdmissao(dataAdmissao).setDataDemissao(dataDemissao).build();

        Mockito.when(service.findFuncionarioById(1L)).thenReturn(funcionario);

        ResponseEntity<FuncionarioResponseDto> response = controller.findFuncionarioById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(funcionario.getId(), response.getBody().id());
        assertEquals(funcionario.getNome(), response.getBody().nome());
        assertFalse(response.getBody().ativo());
        assertEquals(dataAdmissao, response.getBody().dataAdmissao());
        assertEquals(dataDemissao, response.getBody().dataDemissao());
        assertNotNull(response.getBody().cargo());
        assertNotNull(response.getBody().user());
        assertEquals(user.getId(), response.getBody().user().id());

        Mockito.verify(service).findFuncionarioById(1L);
    }

    //Testes findByEmail
    @Test
    void shouldFindFuncionarioByEmailSuccessfully()
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setId(1L).build();

        Mockito.when(service.findFuncionarioByEmail(funcionario.getEmail())).thenReturn(funcionario);

        ResponseEntity<FuncionarioResponseDto> response = controller.findFuncionarioByEmail(funcionario.getEmail());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(funcionario.getId(), response.getBody().id());
        assertEquals(funcionario.getNome(), response.getBody().nome());
        assertEquals(funcionario.getEmail(), response.getBody().email());

        Mockito.verify(service).findFuncionarioByEmail(funcionario.getEmail());
    }

    //Testes findByCpf
    @Test
    void shouldFindFuncionarioByCpfSuccessfully()
    {
        Funcionario funcionario = FuncionarioTestBuilder.newFuncionario().setId(1L).build();
        Mockito.when(service.findFuncionarioByCpf(funcionario.getCpf())).thenReturn(funcionario);

        ResponseEntity<FuncionarioResponseDto> response = controller.findFuncionarioByCpf(funcionario.getCpf());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(funcionario.getId(), response.getBody().id());
        assertEquals(funcionario.getNome(), response.getBody().nome());
        assertEquals(funcionario.getCpf(), response.getBody().cpf());

        Mockito.verify(service).findFuncionarioByCpf(funcionario.getCpf());
    }

    //Testes create
    @Test
    void shouldCreateFuncionarioWithUserSuccessfully()
    {
        Cargo cargo = CargoTestBuilder.newCargo().setId(1L).build();
        User user = new User(2L, "pedro", "senha123", UserRoles.USER);

        FuncionarioRequestDto dto = new FuncionarioRequestDto(
                "Pedro Moura",
                "52998224725",
                "pedro@email.com",
                LocalDate.of(1990, 1, 10),
                "Praça da Sé, 1 - São Paulo",
                "01001000",
                cargo.getId(),
                user.getId(),
                LocalDate.of(2026, 1, 10)
        );

        Funcionario created = FuncionarioTestBuilder.newFuncionario().setId(10L).setCargo(cargo).setUser(user).build();

        Mockito.when(cargoService.findCargoById(cargo.getId())).thenReturn(cargo);
        Mockito.when(userService.findUserById(user.getId())).thenReturn(user);
        Mockito.when(service.createFuncionario(Mockito.any(Funcionario.class))).thenReturn(created);

        ResponseEntity<FuncionarioResponseDto> response = controller.createFuncionario(dto);


        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(URI.create("http://localhost/funcionarios/10"), response.getHeaders().getLocation());

        assertNotNull(response.getBody());
        assertEquals(created.getId(), response.getBody().id());
        assertEquals(created.getEmail(), response.getBody().email());
        assertNotNull(response.getBody().cargo());
        assertEquals(cargo.getId(), response.getBody().cargo().id());
        assertNotNull(response.getBody().user());
        assertEquals(user.getId(), response.getBody().user().id());

        Mockito.verify(cargoService).findCargoById(cargo.getId());
        Mockito.verify(userService).findUserById(user.getId());

        ArgumentCaptor<Funcionario> captor = ArgumentCaptor.forClass(Funcionario.class);

        Mockito.verify(service).createFuncionario(captor.capture());

        Funcionario sentToService = captor.getValue();

        assertNull(sentToService.getId());
        assertEquals(dto.nome(), sentToService.getNome());
        assertEquals(dto.cpf(), sentToService.getCpf());
        assertEquals(dto.email(), sentToService.getEmail());
        assertSame(cargo, sentToService.getCargo());
        assertSame(user, sentToService.getUser());
    }

    @Test
    void shouldCreateFuncionarioWithoutUserSuccessfully()
    {
        Cargo cargo = CargoTestBuilder.newCargo().setId(1L).build();

        FuncionarioRequestDto dto = new FuncionarioRequestDto(
                "Pedro Moura",
                "52998224725",
                "pedro@email.com",
                LocalDate.of(1990, 1, 10),
                "Praça da Sé, 1 - São Paulo",
                "01001000",
                cargo.getId(),
                null,
                LocalDate.of(2026, 1, 10)
        );

        Funcionario created = FuncionarioTestBuilder.newFuncionario().setId(10L).setCargo(cargo).setUser(null).build();

        Mockito.when(cargoService.findCargoById(cargo.getId())).thenReturn(cargo);
        Mockito.when(service.createFuncionario(Mockito.any(Funcionario.class))).thenReturn(created);

        ResponseEntity<FuncionarioResponseDto> response = controller.createFuncionario(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(
                URI.create("http://localhost/funcionarios/10"),
                response.getHeaders().getLocation()
        );
        assertNotNull(response.getBody());
        assertEquals(created.getId(), response.getBody().id());
        assertNotNull(response.getBody().cargo());
        assertNull(response.getBody().user());

        Mockito.verify(cargoService).findCargoById(cargo.getId());
        Mockito.verifyNoInteractions(userService);

        ArgumentCaptor<Funcionario> captor = ArgumentCaptor.forClass(Funcionario.class);

        Mockito.verify(service).createFuncionario(captor.capture());

        Funcionario sentToService = captor.getValue();

        assertNull(sentToService.getId());
        assertSame(cargo, sentToService.getCargo());
        assertNull(sentToService.getUser());
    }

    //Testes update
    @Test
    void shouldUpdateFuncionarioAndPreserveUserAndEmploymentDates()
    {
        LocalDate hoje = LocalDate.now();
        LocalDate dataAdmissaoExistente = hoje.minusYears(2);
        LocalDate dataDemissaoExistente = hoje.minusDays(1);
        LocalDate dataAdmissaoRecebida = hoje.minusYears(1);

        Cargo cargoAtualizado = CargoTestBuilder.newCargo().setId(2L).setNome("Fisioterapeuta").build();

        User usuarioExistente = new User(1L, "pedro", "senha123", UserRoles.USER);

        FuncionarioRequestDto dto = new FuncionarioRequestDto(
                "Pedro Moura Atualizado",
                "12345678909",
                "pedro.atualizado@email.com",
                LocalDate.of(1991, 5, 20),
                "Avenida Paulista, 1000",
                "01310100",
                cargoAtualizado.getId(),
                99L,
                dataAdmissaoRecebida
        );

        Funcionario updated = FuncionarioTestBuilder.newFuncionario().setId(1L).setNome(dto.nome()).setCpf(dto.cpf()).setEmail(dto.email()).setDataNascimento(dto.dataNascimento()).setEndereco(dto.endereco()).setCep(dto.cep()).setCargo(cargoAtualizado).setUser(usuarioExistente).setDataAdmissao(dataAdmissaoExistente).setDataDemissao(dataDemissaoExistente).setAtivo(false).build();

        Mockito.when(cargoService.findCargoById(cargoAtualizado.getId())).thenReturn(cargoAtualizado);

        Mockito.when(service.updateFuncionario(Mockito.eq(1L), Mockito.any(Funcionario.class))).thenReturn(updated);

        ResponseEntity<FuncionarioResponseDto> response = controller.updateFuncionario(1L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updated.getId(), response.getBody().id());
        assertEquals(dto.nome(), response.getBody().nome());
        assertEquals(dto.cpf(), response.getBody().cpf());
        assertEquals(dto.email(), response.getBody().email());
        assertNotNull(response.getBody().cargo());
        assertEquals(cargoAtualizado.getId(), response.getBody().cargo().id());
        assertNotNull(response.getBody().user());
        assertEquals(usuarioExistente.getId(), response.getBody().user().id());
        assertEquals(dataAdmissaoExistente, response.getBody().dataAdmissao());
        assertEquals(dataDemissaoExistente, response.getBody().dataDemissao());

        Mockito.verify(cargoService).findCargoById(cargoAtualizado.getId());
        Mockito.verifyNoInteractions(userService);

        ArgumentCaptor<Funcionario> captor = ArgumentCaptor.forClass(Funcionario.class);

        Mockito.verify(service).updateFuncionario(Mockito.eq(1L), captor.capture());

        Funcionario sentToService = captor.getValue();

        assertNull(sentToService.getId());
        assertEquals(dto.nome(), sentToService.getNome());
        assertEquals(dto.cpf(), sentToService.getCpf());
        assertEquals(dto.email(), sentToService.getEmail());
        assertSame(cargoAtualizado, sentToService.getCargo());
        assertNull(sentToService.getUser());
    }

    //Testes dismissFuncionario
    @Test
    void shouldDismissFuncionarioSuccessfully()
    {
        Long id = 1L;
        LocalDate dataDemissao = LocalDate.now().minusDays(1);
        FuncionarioDismissalRequestDto dto = new FuncionarioDismissalRequestDto(dataDemissao);

        ResponseEntity<Void> response = controller.dismissFuncionario(id, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(service).dismissFuncionarioById(id, dataDemissao);
        Mockito.verifyNoInteractions(cargoService, userService);
    }

    @Test
    void shouldReactivateFuncionarioSuccessfully()
    {
        Long id = 1L;

        ResponseEntity<Void> response = controller.reactivateFuncionario(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(service).reactivateFuncionarioById(id);
        Mockito.verifyNoInteractions(cargoService, userService);
    }

    //Teste linkUser

    @Test
    void shouldSetFuncionarioUserSuccessfully()
    {
        Long funcionarioId = 1L;
        User user = new User(2L, "maria", "senha456", UserRoles.USER);

        FuncionarioUserRequestDto dto = new FuncionarioUserRequestDto(user.getId());

        Mockito.when(userService.findUserById(user.getId()))
                .thenReturn(user);

        ResponseEntity<Void> response = controller.setFuncionarioUser(funcionarioId, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(userService).findUserById(user.getId());
        Mockito.verify(service).linkUserToFuncionario(funcionarioId, user);

        Mockito.verifyNoInteractions(cargoService);
    }

    @Test
    void shouldUnlinkUserFromFuncionarioSuccessfully()
    {
        Long funcionarioId = 1L;

        ResponseEntity<Void> response = controller.unlinkFuncionarioUser(funcionarioId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        Mockito.verify(service).unlinkUserFromFuncionario(funcionarioId);

        Mockito.verifyNoInteractions(userService, cargoService);
    }

}
