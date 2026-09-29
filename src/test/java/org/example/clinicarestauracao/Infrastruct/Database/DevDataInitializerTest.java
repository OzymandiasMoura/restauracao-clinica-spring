package org.example.clinicarestauracao.Infrastruct.Database;

import org.example.clinicarestauracao.Application.Interfaces.CargoRepository;
import org.example.clinicarestauracao.Application.Interfaces.FuncionarioRepository;
import org.example.clinicarestauracao.Application.Interfaces.ModalidadeRepository;
import org.example.clinicarestauracao.Application.Interfaces.UserRepository;
import org.example.clinicarestauracao.Application.Services.CargoService;
import org.example.clinicarestauracao.Application.Services.FuncionarioService;
import org.example.clinicarestauracao.Application.Services.ModalidadeService;
import org.example.clinicarestauracao.Application.Services.UserService;
import org.example.clinicarestauracao.Domain.Entities.Cargo;
import org.example.clinicarestauracao.Domain.Entities.Funcionario;
import org.example.clinicarestauracao.Domain.Entities.Modalidade;
import org.example.clinicarestauracao.Domain.Entities.User;
import org.example.clinicarestauracao.Domain.Enums.UserRoles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DevDataInitializerTest
{
    @Mock
    private CargoRepository cargoRepository;
    @Mock
    private FuncionarioRepository funcionarioRepository;
    @Mock
    private ModalidadeRepository modalidadeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CargoService cargoService;
    @Mock
    private FuncionarioService funcionarioService;
    @Mock
    private ModalidadeService modalidadeService;
    @Mock
    private UserService userService;
    @InjectMocks
    private DevDataInitializer initializer;

    @Test
    void shouldNotPopulateDatabaseWhenItAlreadyContainsData()
    {
        when(cargoRepository.count()).thenReturn(1L);

        initializer.run(null);

        verify(cargoRepository).count();
        verifyNoInteractions(funcionarioRepository, modalidadeRepository, userRepository);
        verifyNoInteractions(cargoService, funcionarioService, modalidadeService, userService);
    }

    @Test
    void shouldPopulateEmptyDevelopmentDatabaseWithValidScenarios()
    {
        Cargo coordenador = new Cargo(1L, "Coordenador", true);
        Cargo psicologo = new Cargo(2L, "Psicólogo", true);
        Cargo assistenteSocial = new Cargo(3L, "Assistente Social", true);
        Cargo recepcionista = new Cargo(4L, "Recepcionista", true);
        Cargo estagiario = new Cargo(5L, "Estagiário", true);

        when(cargoService.createCargo(Mockito.any(Cargo.class)))
                .thenReturn(coordenador, psicologo, assistenteSocial, recepcionista, estagiario);

        User admin = new User(1L, "admin.dev", "senha-criptografada", UserRoles.ADMIN);
        User profissional = new User(2L, "profissional.dev", "senha-criptografada", UserRoles.USER);
        when(userRepository.findUserByUsername("admin.dev")).thenReturn(admin);
        when(userRepository.findUserByUsername("profissional.dev")).thenReturn(profissional);

        Modalidade atendimentoSocial = new Modalidade(
                1L, "Atendimento Social", 40, false, true, "#2E86C1");
        Modalidade clinicaParceira = new Modalidade(
                2L, "Clínica Parceira", "11222333000181", 20, true, true, "#28B463");
        Modalidade convenio = new Modalidade(
                3L, "Convênio Comunitário", "11444777000161", 15, true, true, "#AF7AC5");
        Modalidade listaEspera = new Modalidade(
                4L, "Lista de Espera", 0, false, true, "#F39C12");
        when(modalidadeService.createModalidade(Mockito.any(Modalidade.class)))
                .thenReturn(atendimentoSocial, clinicaParceira, convenio, listaEspera);

        Funcionario anaSalva = new Funcionario(
                1L, "Ana Silva", "52998224725", "ana.silva@teste.local",
                LocalDate.of(1985, 4, 12), "Praça da Sé, 1 - São Paulo - SP",
                "01001000", true, admin, coordenador, LocalDate.of(2020, 2, 3));
        Funcionario brunoSalvo = new Funcionario(
                2L, "Bruno Costa", "11144477735", "bruno.costa@teste.local",
                LocalDate.of(1992, 8, 20), "Praça Mauá, 10 - Rio de Janeiro - RJ",
                "20040002", true, psicologo, LocalDate.of(2023, 1, 9));
        Funcionario carlaSalva = new Funcionario(
                3L, "Carla Souza", "12345678909", "carla.souza@teste.local",
                LocalDate.of(1988, 11, 2), "Praça da Liberdade, 20 - Belo Horizonte - MG",
                "30140010", true, profissional, assistenteSocial, LocalDate.of(2021, 5, 10));
        when(funcionarioService.createFuncionario(Mockito.any(Funcionario.class)))
                .thenReturn(anaSalva, brunoSalvo, carlaSalva);

        initializer.run(null);

        verify(cargoRepository).count();
        verify(funcionarioRepository).count();
        verify(modalidadeRepository).count();
        verify(userRepository).count();

        ArgumentCaptor<Cargo> cargoCaptor = ArgumentCaptor.forClass(Cargo.class);
        verify(cargoService, Mockito.times(5)).createCargo(cargoCaptor.capture());
        assertEquals(
                List.of("Coordenador", "Psicólogo", "Assistente Social", "Recepcionista", "Estagiário"),
                cargoCaptor.getAllValues().stream().map(Cargo::getNome).toList());
        assertTrue(cargoCaptor.getAllValues().stream().allMatch(Cargo::isAtivo));
        verify(cargoService).softDeleteCargoById(5L);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService, Mockito.times(3)).registerUser(userCaptor.capture());
        List<User> users = userCaptor.getAllValues();
        assertEquals(List.of("admin.dev", "profissional.dev", "usuario.livre"),
                users.stream().map(User::getUsername).toList());
        assertEquals(List.of(UserRoles.ADMIN, UserRoles.USER, UserRoles.USER),
                users.stream().map(User::getRole).toList());
        assertTrue(users.stream().allMatch(user -> user.getPassword().equals("teste123")));

        ArgumentCaptor<Modalidade> modalidadeCaptor = ArgumentCaptor.forClass(Modalidade.class);
        verify(modalidadeService, Mockito.times(4)).createModalidade(modalidadeCaptor.capture());
        List<Modalidade> modalidades = modalidadeCaptor.getAllValues();
        assertEquals(List.of("Atendimento Social", "Clínica Parceira", "Convênio Comunitário", "Lista de Espera"),
                modalidades.stream().map(Modalidade::getDescricao).toList());
        assertEquals(List.of("#2E86C1", "#28B463", "#AF7AC5", "#F39C12"),
                modalidades.stream().map(Modalidade::getCor).toList());
        assertNull(modalidades.get(0).getCnpj());
        assertEquals(0, modalidades.get(3).getMaxVagas());
        assertFalse(modalidades.get(0).isPagamento());
        assertTrue(modalidades.get(1).isPagamento());
        verify(modalidadeService).deleteModalidadeById(3L);

        ArgumentCaptor<Funcionario> funcionarioCaptor = ArgumentCaptor.forClass(Funcionario.class);
        verify(funcionarioService, Mockito.times(3)).createFuncionario(funcionarioCaptor.capture());
        List<Funcionario> funcionarios = funcionarioCaptor.getAllValues();
        assertEquals(List.of("Ana Silva", "Bruno Costa", "Carla Souza"),
                funcionarios.stream().map(Funcionario::getNome).toList());
        assertEquals(List.of("52998224725", "11144477735", "12345678909"),
                funcionarios.stream().map(Funcionario::getCpf).toList());
        assertSame(admin, funcionarios.get(0).getUser());
        assertNull(funcionarios.get(1).getUser());
        assertSame(profissional, funcionarios.get(2).getUser());
        assertTrue(funcionarios.stream().allMatch(Funcionario::isAtivo));
        verify(funcionarioService).dismissFuncionarioById(3L, LocalDate.of(2025, 6, 30));
    }
}
