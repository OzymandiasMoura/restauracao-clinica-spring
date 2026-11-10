package org.example.clinicarestauracao.Infrastruct.Database;

import org.example.clinicarestauracao.Application.Dtos.TriagemDtos.TriagemRequestDto;
import org.example.clinicarestauracao.Application.Dtos.TriagemDtos.TriagemResponseDto;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.YearMonth;

@Component
@Profile("dev")
public class DevDataInitializer implements ApplicationRunner {

    private static final String TEST_PASSWORD = "teste123";

    private final AcolhidoRepository acolhidoRepository;
    private final CargoRepository cargoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ConvenioRepository convenioRepository;
    private final TriagemRepository triagemRepository;
    private final UserRepository userRepository;
    private final MedicamentoRepository medicamentoRepository;

    private final AcolhidoService acolhidoService;
    private final CargoService cargoService;
    private final FuncionarioService funcionarioService;
    private final ConvenioService convenioService;
    private final TriagemService triagemService;
    private final UserService userService;
    private final MedicamentoService medicamentoService;
    private final PagamentoRepository pagamentoRepository;
    private final InternacaoRepository internacaoRepository;
    private final JdbcTemplate jdbcTemplate;

    public DevDataInitializer(
            AcolhidoRepository acolhidoRepository,
            CargoRepository cargoRepository,
            FuncionarioRepository funcionarioRepository,
            ConvenioRepository convenioRepository,
            UserRepository userRepository,
            MedicamentoRepository medicamentoRepository,
            TriagemRepository triagemRepository,
            AcolhidoService acolhidoService,
            CargoService cargoService,
            FuncionarioService funcionarioService,
            ConvenioService convenioService,
            UserService userService,
            MedicamentoService medicamentoService,
            TriagemService triagemService, PagamentoRepository pagamentoRepository,
            InternacaoRepository internacaoRepository, JdbcTemplate jdbcTemplate
    ) {
        this.acolhidoRepository = acolhidoRepository;
        this.cargoRepository = cargoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.convenioRepository = convenioRepository;
        this.triagemRepository = triagemRepository;
        this.userRepository = userRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.acolhidoService = acolhidoService;
        this.cargoService = cargoService;
        this.funcionarioService = funcionarioService;
        this.convenioService = convenioService;
        this.triagemService = triagemService;
        this.userService = userService;
        this.medicamentoService = medicamentoService;
        this.pagamentoRepository = pagamentoRepository;
        this.internacaoRepository = internacaoRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        // Evita duplicar dados no ambiente de desenvolvimento
        if (acolhidoRepository.count() > 0
                || cargoRepository.count() > 0
                || funcionarioRepository.count() > 0
                || convenioRepository.count() > 0
                || userRepository.count() > 0
                || medicamentoRepository.count() > 0
                || triagemRepository.count() > 0) {
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

        userService.registerUser(new User("admin.dev", TEST_PASSWORD, UserRoles.ADMIN));

        // =========================
        // CONVÊNIOS
        // =========================



        Convenio atendimentoSocial = convenioService.createConvenio(
                new Convenio(
                        "Atendimento Social",
                        null,
                        40,
                        false,
                        "#2E86C1"
                )
        );

        Convenio clinicaParceira = convenioService.createConvenio(
                new Convenio(
                        "Clínica Parceira",
                        "11222333000181",
                        20,
                        true,
                        "#28B463"
                )
        );

        Convenio convenioComunitario = convenioService.createConvenio(
                new Convenio(
                        "Convênio Comunitário",
                        "11444777000161",
                        15,
                        true,
                        "#AF7AC5"
                )
        );

        convenioService.createConvenio(
                new Convenio(
                        "Lista de Espera",
                        null,
                        0,
                        false,
                        "#F39C12"
                )
        );

        // Testa desativação de convênio
        convenioService.deactivateConvenioById(
                convenioComunitario.getId()
        );

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
                                "Praça da Liberdade",
                                20,
                                "Funcionários",
                                "Belo Horizonte",
                                "MG"
                        ),
                        "30140010",
                        "31987654321",
                        new User(
                                "carla.dev",
                                TEST_PASSWORD,
                                UserRoles.USER
                        ),
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



        medicamentoService.create(
                new Medicamento(
                        "Dipirona 500mg",
                        new BigDecimal("12.50"),
                        50,
                        10
                )
        );

        medicamentoService.create(
                new Medicamento(
                        "Paracetamol 750mg",
                        new BigDecimal("8.90"),
                        30,
                        5
                )
        );

        medicamentoService.create(
                new Medicamento(
                        "Ibuprofeno 600mg",
                        new BigDecimal("15.75"),
                        20,
                        10
                )
        );

        Medicamento medicamentoInativo = medicamentoService.create(
                new Medicamento("Cetirizina 10mg", new BigDecimal("11.90"), 0, 0)
        );

        medicamentoService.adicionarEstoque(
                medicamentoInativo.getId(), 15
        );

        medicamentoService.delete(medicamentoInativo.getId());

        // =========================
        // TRIAGENS
        // =========================

        LocalDateTime agora = LocalDateTime.now();

        // Triagem 1 - AGENDADA
        triagemService.create(
                new TriagemRequestDto(
                        atendimentoSocial.getId(),
                        "João Pedro da Silva",
                        "11987654321",
                        agora.plusDays(1),
                        "Primeiro contato realizado por telefone."
                )
        );

        // Triagem 2 - Será CONCLUIDA
        TriagemResponseDto triagemConcluida = triagemService.create(
                new TriagemRequestDto(
                        clinicaParceira.getId(),
                        "Maria Eduarda Santos",
                        "11976543210",
                        agora.plusDays(2),
                        "Possível acolhida demonstrou interesse."
                )
        );

        triagemService.concluir(triagemConcluida.id());

        // Triagem 3 - AGENDADA
        triagemService.create(
                new TriagemRequestDto(
                        atendimentoSocial.getId(),
                        "Carlos Henrique Souza",
                        "11999887766",
                        agora.plusHours(4),
                        "Entrevista inicial agendada."
                )
        );

        // Triagem 4 - AGENDADA
        triagemService.create(
                new TriagemRequestDto(
                        clinicaParceira.getId(),
                        "Fernanda Oliveira",
                        "11988776655",
                        agora.plusDays(3),
                        "Entrevista de acompanhamento."
                )
        );

        // Triagem 5 - Teste do Scheduler
        // Após um minuto deve mudar para ATRASADA
        triagemService.create(
                new TriagemRequestDto(
                        atendimentoSocial.getId(),
                        "Teste Scheduler",
                        "11999998888",
                        agora.plusMinutes(1),
                        "Teste de atualização automática do status."
                )
        );
        // =========================
        // INTERNAÇÕES MOCKADAS
        // =========================
        // Em um H2 novo, as criações acima atribuem normalmente:
        // 1 = Gabriel Santos; 2 = Juliana Oliveira; 3 = Marcos Pereira (desativado).
        // Os nomes são conferidos para impedir associação a um acolhido errado.
        Acolhido gabriel = buscarAcolhidoMock(1L, "Gabriel Santos");
        Acolhido juliana = buscarAcolhidoMock(2L, "Juliana Oliveira");

        Internacao internacaoGabriel = criarInternacaoMock(
                gabriel, clinicaParceira, new BigDecimal("950.00"), 45
        );

        Internacao internacaoJuliana = criarInternacaoMock(
                juliana, clinicaParceira, new BigDecimal("1000.00"), 35
        );

        // =========================
        // PAGAMENTOS MOCKADOS
        // =========================

        // Gabriel: 300 + 250 + 400 = R$ 950,00

        // =========================
        // PAGAMENTOS MOCKADOS
        // =========================

        // Gabriel: 300 + 250 + 400 = R$ 950,00

        LocalDate dataPagamentoGabriel1 = LocalDate.now().minusDays(20);

        pagamentoRepository.save(new Pagamento(
                new BigDecimal("300.00"),
                dataPagamentoGabriel1,
                internacaoGabriel,
                YearMonth.from(dataPagamentoGabriel1).toString(),
                "Primeiro pagamento parcial da mensalidade."
        ));

        LocalDate dataPagamentoGabriel2 = LocalDate.now().minusDays(10);

        pagamentoRepository.save(new Pagamento(
                new BigDecimal("250.00"),
                dataPagamentoGabriel2,
                internacaoGabriel,
                YearMonth.from(dataPagamentoGabriel2).toString(),
                "Segundo pagamento parcial."
        ));

        LocalDate dataPagamentoGabriel3 = LocalDate.now().minusDays(2);

        pagamentoRepository.save(new Pagamento(
                new BigDecimal("400.00"),
                dataPagamentoGabriel3,
                internacaoGabriel,
                YearMonth.from(dataPagamentoGabriel3).toString(),
                "Complemento do pagamento da mensalidade."
        ));

        // Juliana: 500 + 500 = R$ 1.000,00

        LocalDate dataPagamentoJuliana1 = LocalDate.now().minusDays(15);

        pagamentoRepository.save(new Pagamento(
                new BigDecimal("500.00"),
                dataPagamentoJuliana1,
                internacaoJuliana,
                YearMonth.from(dataPagamentoJuliana1).toString(),
                "Pagamento parcial da mensalidade."
        ));

        LocalDate dataPagamentoJuliana2 = LocalDate.now().minusDays(5);

        pagamentoRepository.save(new Pagamento(
                new BigDecimal("500.00"),
                dataPagamentoJuliana2,
                internacaoJuliana,
                YearMonth.from(dataPagamentoJuliana2).toString(),
                "Complemento do pagamento da mensalidade."
        ));

    }

    private Acolhido buscarAcolhidoMock(Long id, String nomeEsperado) {
        Acolhido acolhido = acolhidoRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException(
                        "Acolhido mockado não encontrado: ID " + id
                ));
        if (!nomeEsperado.equals(acolhido.getNome())) {
            throw new IllegalStateException(
                    "ID " + id + " pertence a " + acolhido.getNome()
                            + ", mas era esperado " + nomeEsperado
            );
        }
        return acolhido;
    }

    /** Inserção SQL restrita ao perfil dev/H2; não substitui o fluxo de domínio. */
    private Internacao criarInternacaoMock(
            Acolhido acolhido,
            Convenio convenio,
            BigDecimal valorMensalidade,
            int diasDesdeEntrada
    ) {
        String sql = """
                INSERT INTO internacao
                (ativo, data_entrada, data_saida_prevista,
                 etapa_tratamento, valor_mensalidade, acolhido_id, convenio_id)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        LocalDate entrada = LocalDate.now().minusDays(diasDesdeEntrada);
        LocalDate saidaPrevista = entrada.plusMonths(6);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setBoolean(1, true);
            ps.setDate(2, Date.valueOf(entrada));
            ps.setDate(3, Date.valueOf(saidaPrevista));
            ps.setString(4, "EM_TRATAMENTO");
            ps.setBigDecimal(5, valorMensalidade);
            ps.setLong(6, acolhido.getId());
            ps.setLong(7, convenio.getId());
            return ps;
        }, keyHolder);

        Number id = keyHolder.getKey();
        if (id == null) {
            throw new IllegalStateException("Não foi gerado ID para a internação mockada.");
        }

        return internacaoRepository.findById(id.longValue())
                .orElseThrow(() -> new IllegalStateException(
                        "Internação mockada não encontrada: ID " + id
                ));
    }
}
