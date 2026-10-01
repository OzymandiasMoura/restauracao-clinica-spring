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
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@Profile("dev")
public class DevDataInitializer implements ApplicationRunner
{
    private static final String TEST_PASSWORD = "teste123";

    private final CargoRepository cargoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ModalidadeRepository modalidadeRepository;
    private final UserRepository userRepository;
    private final CargoService cargoService;
    private final FuncionarioService funcionarioService;
    private final ModalidadeService modalidadeService;
    private final UserService userService;

    public DevDataInitializer(CargoRepository cargoRepository, FuncionarioRepository funcionarioRepository, ModalidadeRepository modalidadeRepository, UserRepository userRepository, CargoService cargoService, FuncionarioService funcionarioService, ModalidadeService modalidadeService, UserService userService)
    {
        this.cargoRepository = cargoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.modalidadeRepository = modalidadeRepository;
        this.userRepository = userRepository;
        this.cargoService = cargoService;
        this.funcionarioService = funcionarioService;
        this.modalidadeService = modalidadeService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args)
    {
        if (cargoRepository.count() > 0
                || funcionarioRepository.count() > 0
                || modalidadeRepository.count() > 0
                || userRepository.count() > 0)
        {
            return;
        }

        Cargo coordenador = cargoService.createCargo(new Cargo("Coordenador"));
        Cargo psicologo = cargoService.createCargo(new Cargo("Psicólogo"));
        Cargo assistenteSocial = cargoService.createCargo(new Cargo("Assistente Social"));
        cargoService.createCargo(new Cargo("Recepcionista"));
        Cargo estagiario = cargoService.createCargo(new Cargo("Estagiário"));
        cargoService.softDeleteCargoById(estagiario.getId());

        userService.registerUser(new User("admin.dev", TEST_PASSWORD, UserRoles.ADMIN));

        modalidadeService.createModalidade(new Modalidade(
                "Atendimento Social", null, 40, false, "#2E86C1"));
        modalidadeService.createModalidade(new Modalidade(
                "Clínica Parceira", "11222333000181", 20, true, "#28B463"));
        Modalidade convenio = modalidadeService.createModalidade(new Modalidade(
                "Convênio Comunitário", "11444777000161", 15, true, "#AF7AC5"));
        modalidadeService.createModalidade(new Modalidade(
                "Lista de Espera", null, 0, false, "#F39C12"));
        modalidadeService.deleteModalidadeById(convenio.getId());

        funcionarioService.createFuncionario(new Funcionario(
                "Ana Silva",
                "52998224725",
                "ana.silva@teste.local",
                LocalDate.of(1985, 4, 12),
                "Praça da Sé, 1 - São Paulo - SP",
                "01001000",
                new User("ana.dev", TEST_PASSWORD, UserRoles.USER),
                coordenador,
                LocalDate.of(2020, 2, 3)));

        funcionarioService.createFuncionario(new Funcionario(
                "Bruno Costa",
                "11144477735",
                "bruno.costa@teste.local",
                LocalDate.of(1992, 8, 20),
                "Praça Mauá, 10 - Rio de Janeiro - RJ",
                "20040002",
                new User("bruno.dev", TEST_PASSWORD, UserRoles.USER),
                psicologo,
                LocalDate.of(2023, 1, 9)));

        Funcionario carla = funcionarioService.createFuncionario(new Funcionario(
                "Carla Souza",
                "12345678909",
                "carla.souza@teste.local",
                LocalDate.of(1988, 11, 2),
                "Praça da Liberdade, 20 - Belo Horizonte - MG",
                "30140010",
                new User("carla.dev", TEST_PASSWORD, UserRoles.USER),
                assistenteSocial,
                LocalDate.of(2021, 5, 10)));
        funcionarioService.dismissFuncionarioById(carla.getId(), LocalDate.of(2025, 6, 30));
    }
}
