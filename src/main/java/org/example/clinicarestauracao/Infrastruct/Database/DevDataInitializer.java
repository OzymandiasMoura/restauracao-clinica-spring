
package org.example.clinicarestauracao.Infrastruct.Database;

import org.example.clinicarestauracao.Application.Interfaces.*;
import org.example.clinicarestauracao.Application.Services.*;
import org.example.clinicarestauracao.Domain.Entities.*;
import org.example.clinicarestauracao.Domain.Enums.UserRoles;
import org.example.clinicarestauracao.Domain.ValueObjects.Endereco;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@Profile("dev")
public class DevDataInitializer implements ApplicationRunner {

    private static final String TEST_PASSWORD = "teste123";

    private final AcolhidoRepository acolhidoRepository;
    private final CargoRepository cargoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ConvenioRepository convenioRepository;
    private final UserRepository userRepository;
    private final MedicamentoRepository medicamentoRepository;

    private final AcolhidoService acolhidoService;
    private final CargoService cargoService;
    private final FuncionarioService funcionarioService;
    private final ConvenioService convenioService;
    private final UserService userService;
    private final MedicamentoService medicamentoService;

    public DevDataInitializer(
            AcolhidoRepository acolhidoRepository,
            CargoRepository cargoRepository,
            FuncionarioRepository funcionarioRepository,
            ConvenioRepository convenioRepository,
            UserRepository userRepository,
            MedicamentoRepository medicamentoRepository,
            AcolhidoService acolhidoService,
            CargoService cargoService,
            FuncionarioService funcionarioService,
            ConvenioService convenioService,
            UserService userService,
            MedicamentoService medicamentoService
    ) {
        this.acolhidoRepository = acolhidoRepository;
        this.cargoRepository = cargoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.convenioRepository = convenioRepository;
        this.userRepository = userRepository;
        this.medicamentoRepository = medicamentoRepository;

        this.acolhidoService = acolhidoService;
        this.cargoService = cargoService;
        this.funcionarioService = funcionarioService;
        this.convenioService = convenioService;
        this.userService = userService;
        this.medicamentoService = medicamentoService;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        if (acolhidoRepository.count() > 0
                || cargoRepository.count() > 0
                || funcionarioRepository.count() > 0
                || convenioRepository.count() > 0
                || userRepository.count() > 0
                || medicamentoRepository.count() > 0) {
            return;
        }

        // =========================
        // CARGOS
        // =========================

        Cargo coordenador = cargoService.createCargo(
                new Cargo("Coordenador")
        );

        Cargo psicologo = cargoService.createCargo(
                new Cargo("Psicólogo")
        );

        Cargo assistenteSocial = cargoService.createCargo(
                new Cargo("Assistente Social")
        );

        cargoService.createCargo(new Cargo("Recepcionista"));

        Cargo estagiario = cargoService.createCargo(
                new Cargo("Estagiário")
        );

        cargoService.softDeleteCargoById(estagiario.getId());

        // =========================
        // USUÁRIO ADMIN
        // =========================

        userService.registerUser(
                new User("admin.dev", TEST_PASSWORD, UserRoles.ADMIN)
        );

        // =========================
        // CONVÊNIOS
        // =========================

        convenioService.createConvenio(new Convenio(
                "Atendimento Social", null, 40, false, "#2E86C1"
        ));

        convenioService.createConvenio(new Convenio(
                "Clínica Parceira", "11222333000181", 20, true, "#28B463"
        ));

        Convenio convenio = convenioService.createConvenio(new Convenio(
                "Convênio Comunitário", "11444777000161", 15, true, "#AF7AC5"
        ));

        convenioService.createConvenio(new Convenio(
                "Lista de Espera", null, 0, false, "#F39C12"
        ));

        convenioService.deleteConvenioById(convenio.getId());

        // =========================
        // FUNCIONÁRIOS
        // =========================

        funcionarioService.createFuncionario(new Funcionario(
                "Ana Silva",
                "52998224725",
                "ana.silva@teste.local",
                LocalDate.of(1985, 4, 12),
                new Endereco(
                        "Praça da Sé", 1, "Centro",
                        "São Paulo", "SP"
                ),
                "01001000",
                "11987654321",
                new User("ana.dev", TEST_PASSWORD, UserRoles.USER),
                coordenador,
                LocalDate.of(2020, 2, 3)
        ));

        funcionarioService.createFuncionario(new Funcionario(
                "Bruno Costa",
                "11144477735",
                "bruno.costa@teste.local",
                LocalDate.of(1992, 8, 20),
                new Endereco(
                        "Praça Mauá", 10, "Centro",
                        "Rio de Janeiro", "RJ"
                ),
                "20040002",
                "21987654321",
                new User("bruno.dev", TEST_PASSWORD, UserRoles.USER),
                psicologo,
                LocalDate.of(2023, 1, 9)
        ));

        Funcionario carla = funcionarioService.createFuncionario(
                new Funcionario(
                        "Carla Souza",
                        "12345678909",
                        "carla.souza@teste.local",
                        LocalDate.of(1988, 11, 2),
                        new Endereco(
                                "Praça da Liberdade", 20,
                                "Funcionários", "Belo Horizonte", "MG"
                        ),
                        "30140010",
                        "31987654321",
                        new User("carla.dev", TEST_PASSWORD, UserRoles.USER),
                        assistenteSocial,
                        LocalDate.of(2021, 5, 10)
                )
        );

        funcionarioService.dismissFuncionarioById(
                carla.getId(),
                LocalDate.of(2025, 6, 30)
        );

        // =========================
        // ACOLHIDOS
        // =========================

        acolhidoService.createAcolhido(new Acolhido(
                "Gabriel Santos",
                "gabriel.santos@teste.local",
                "39053344705",
                "01310100",
                new Endereco(
                        "Avenida Paulista", 1000,
                        "Bela Vista", "São Paulo", "SP",
                        "Apartamento 42"
                ),
                "11955551234",
                LocalDate.of(1990, 3, 15)
        ));

        acolhidoService.createAcolhido(new Acolhido(
                "Juliana Oliveira",
                "juliana.oliveira@teste.local",
                "16899535009",
                "30140071",
                new Endereco(
                        "Avenida Afonso Pena", 1500,
                        "Centro", "Belo Horizonte", "MG"
                ),
                "31955552345",
                LocalDate.of(1987, 7, 22)
        ));

        Acolhido marcos = acolhidoService.createAcolhido(new Acolhido(
                "Marcos Pereira",
                "marcos.pereira@teste.local",
                "98765432100",
                "20040002",
                new Endereco(
                        "Rua da Assembleia", 50,
                        "Centro", "Rio de Janeiro", "RJ"
                ),
                "21955553456",
                LocalDate.of(1979, 12, 3)
        ));

        acolhidoService.deactivateAcolhido(marcos.getId());



        Medicamento dipirona = medicamentoService.create(
                new Medicamento("Dipirona 500mg", 12.50)
        );

        Medicamento paracetamol = medicamentoService.create(
                new Medicamento("Paracetamol 750mg", 8.90)
        );

        Medicamento ibuprofeno = medicamentoService.create(
                new Medicamento("Ibuprofeno 600mg", 15.90)
        );

        Medicamento amoxicilina = medicamentoService.create(
                new Medicamento("Amoxicilina 500mg", 22.50)
        );

        Medicamento omeprazol = medicamentoService.create(
                new Medicamento("Omeprazol 20mg", 10.90)
        );

        Medicamento loratadina = medicamentoService.create(
                new Medicamento("Loratadina 10mg", 9.50)
        );

        dipirona.setQuantidadeMinima(10);
        paracetamol.setQuantidadeMinima(10);
        ibuprofeno.setQuantidadeMinima(5);
        amoxicilina.setQuantidadeMinima(10);
        omeprazol.setQuantidadeMinima(5);
        loratadina.setQuantidadeMinima(5);

        medicamentoService.adicionarEstoque(dipirona.getId(), 50);
        medicamentoService.adicionarEstoque(paracetamol.getId(), 3);
        medicamentoService.adicionarEstoque(ibuprofeno.getId(), 25);
        medicamentoService.adicionarEstoque(amoxicilina.getId(), 8);
        medicamentoService.adicionarEstoque(omeprazol.getId(), 40);


        medicamentoService.baixarEstoque(dipirona.getId(), 10);
        medicamentoService.baixarEstoque(ibuprofeno.getId(), 5);

        Medicamento medicamentoInativo = medicamentoService.create(
                new Medicamento("Cetirizina 10mg", 11.90)
        );

        medicamentoService.adicionarEstoque(
                medicamentoInativo.getId(), 15
        );

        medicamentoService.delete(medicamentoInativo.getId());
    }
}
