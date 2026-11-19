-- Carga exclusiva do perfil dev para o H2 em memória.
-- O Hibernate recria o schema antes desta carga. SQL não executa as validações
-- do domínio: preservar as regras de datas, acesso e relacionamentos ao editar.
-- Todos os usuários de exemplo usam a senha teste123, armazenada com BCrypt.

INSERT INTO cargos (id, nome, ativo) VALUES
    (1, 'Coordenador', TRUE),
    (2, 'Psicólogo', TRUE),
    (3, 'Assistente Social', TRUE),
    (4, 'Recepcionista', TRUE),
    (5, 'Estagiário', FALSE);

INSERT INTO usuarios (id, username, password, role, previous_role) VALUES
    (1, 'admin.dev', '$2a$10$vPnzB18TdWaQANbTfYHGM.E4d9wFDuZ083fGZU2jD25iG5zZiTEau', 'ADMIN', NULL),
    (2, 'ana.dev', '$2a$10$vPnzB18TdWaQANbTfYHGM.E4d9wFDuZ083fGZU2jD25iG5zZiTEau', 'USER', NULL),
    (3, 'bruno.dev', '$2a$10$vPnzB18TdWaQANbTfYHGM.E4d9wFDuZ083fGZU2jD25iG5zZiTEau', 'USER', NULL),
    (4, 'carla.dev', '$2a$10$vPnzB18TdWaQANbTfYHGM.E4d9wFDuZ083fGZU2jD25iG5zZiTEau', 'NO_ACCESS', 'USER');

INSERT INTO convenios (id, descricao, cnpj, max_vagas, ativo, pagamento, cor) VALUES
    (1, 'Atendimento Social', NULL, 40, TRUE, FALSE, '#2E86C1'),
    (2, 'Clínica Parceira', '11222333000181', 20, TRUE, TRUE, '#28B463'),
    (3, 'Convênio Comunitário', '11444777000161', 15, FALSE, TRUE, '#AF7AC5'),
    (4, 'Lista de Espera', NULL, 0, TRUE, FALSE, '#F39C12');

INSERT INTO funcionarios
    (id, nome, cpf, email, data_nascimento, endereco, cep, telefone,
     ativo, user_id, cargo_id, data_admissao, data_demissao)
VALUES
    (1, 'Ana Silva', '52998224725', 'ana.silva@teste.local', DATE '1985-04-12',
     'Praça da Sé, 1 - Centro - São Paulo/SP', '01001000', '11987654321',
     TRUE, 2, 1, DATE '2020-02-03', NULL),
    (2, 'Bruno Costa', '11144477735', 'bruno.costa@teste.local', DATE '1992-08-20',
     'Praça Mauá, 10 - Centro - Rio de Janeiro/RJ', '20040002', '21987654321',
     TRUE, 3, 2, DATE '2023-01-09', NULL),
    (3, 'Carla Souza', '12345678909', 'carla.souza@teste.local', DATE '1988-11-02',
     'Praça da Liberdade, 20 - Funcionários - Belo Horizonte/MG', '30140010', '31987654321',
     FALSE, 4, 3, DATE '2021-05-10', DATE '2025-06-30');

INSERT INTO responsaveis (id, nome, email, cpf, telefone, endereco, cep, ativo) VALUES
    (1, 'Roberto Santos', 'roberto.santos@teste.local', '15350946056', '11955554567',
     'Rua Vergueiro, 500 - Liberdade - São Paulo/SP', '01504000', TRUE),
    (2, 'Helena Oliveira', 'helena.oliveira@teste.local', '27438165035', '31955555678',
     'Rua da Bahia, 1200 - Centro - Belo Horizonte/MG', '30160011', TRUE);

INSERT INTO acolhidos
    (id, nome, email, cpf, cep, endereco, telefone, data_nascimento, responsavel_id, ativo)
VALUES
    (1, 'Gabriel Santos', 'gabriel.santos@teste.local', '39053344705', '01310100',
     'Avenida Paulista, 1000 - Bela Vista - São Paulo/SP - Apartamento 42',
     '11955551234', DATE '1990-03-15', 1, TRUE),
    (2, 'Juliana Oliveira', 'juliana.oliveira@teste.local', '16899535009', '30140071',
     'Avenida Afonso Pena, 1500 - Centro - Belo Horizonte/MG',
     '31955552345', DATE '1987-07-22', 2, TRUE),
    (3, 'Marcos Pereira', 'marcos.pereira@teste.local', '98765432100', '20040002',
     'Rua da Assembleia, 50 - Centro - Rio de Janeiro/RJ',
     '21955553456', DATE '1979-12-03', 1, FALSE),
    (4, 'Lucas Almeida', 'lucas.almeida@teste.local', '71460293878', '01310100',
     'Avenida Paulista, 1100 - Bela Vista - São Paulo/SP',
     '11988881111', DATE '1991-05-10', 1, TRUE),
    (5, 'Pedro Henrique', 'pedro.henrique@teste.local', '82571304941', '30140071',
     'Avenida Afonso Pena, 1600 - Centro - Belo Horizonte/MG',
     '31988882222', DATE '1989-08-21', 2, TRUE),
    (6, 'Rafael Martins', 'rafael.martins@teste.local', '93682415033', '01504000',
     'Rua Vergueiro, 600 - Liberdade - São Paulo/SP',
     '11988883333', DATE '1985-02-14', 1, TRUE),
    (7, 'Felipe Rocha', 'felipe.rocha@teste.local', '14793526160', '30160011',
     'Rua da Bahia, 1300 - Centro - Belo Horizonte/MG',
     '31988884444', DATE '1993-10-07', 2, TRUE),
    (8, 'Acolhido para teste de exclusão', 'acolhido.exclusao@teste.local', '93541134780', '01310100',
     'Avenida Paulista, 1200 - Bela Vista - São Paulo/SP',
     '11977778888', DATE '1995-06-12', 1, TRUE);

INSERT INTO medicamento (id, nome, preco, quantidade_estoque, quantidade_minima) VALUES
    (1, 'Dipirona 500mg', 12.50, 50, 10),
    (2, 'Paracetamol 750mg', 8.90, 30, 5),
    (3, 'Ibuprofeno 600mg', 15.75, 20, 10);

INSERT INTO triagem
    (id, id_convenio, nome_possivel_acolhido, telefone, data_agendada,
     observacao_entrevista, status, data_conclusao)
VALUES
    (1, 1, 'João Pedro da Silva', '11987654321', DATEADD('DAY', 1, LOCALTIMESTAMP),
     'Primeiro contato realizado por telefone.', 'AGENDADA', NULL),
    (2, 2, 'Maria Eduarda Santos', '11976543210', DATEADD('DAY', 2, LOCALTIMESTAMP),
     'Possível acolhida demonstrou interesse.', 'AGENDADA', NULL),
    (3, 1, 'Carlos Henrique Souza', '11999887766', DATEADD('HOUR', 4, LOCALTIMESTAMP),
     'Entrevista inicial agendada.', 'AGENDADA', NULL),
    (4, 2, 'Fernanda Oliveira', '11988776655', DATEADD('DAY', 3, LOCALTIMESTAMP),
     'Entrevista de acompanhamento.', 'AGENDADA', NULL),
    (5, 1, 'Teste Scheduler', '11999998888', DATEADD('MINUTE', 1, LOCALTIMESTAMP),
     'Teste de atualização automática do status.', 'AGENDADA', NULL),
    (6, 1, 'Paulo Almeida', '11988887777', DATEADD('DAY', -2, LOCALTIMESTAMP),
     'Entrevista realizada.', 'CONCLUIDA', DATEADD('DAY', -1, LOCALTIMESTAMP)),
    (7, 2, 'Luciana Costa', '11977776666', DATEADD('DAY', 4, LOCALTIMESTAMP),
     'Cancelada a pedido da família.', 'CANCELADA', NULL);

-- 20 internações: 6 ativas e 14 encerradas, sem sobreposição por acolhido.
-- Ativas: 2 em tratamento, 2 próximas da alta e 2 com alta vencida.
-- Marcos possui apenas histórico encerrado; seu convênio pode estar inativo
-- hoje sem invalidar a internação histórica. A Lista de Espera não tem internações.
INSERT INTO internacao
    (id, acolhido_id, convenio_id, data_entrada, data_saida_prevista, data_saida,
     etapa_tratamento, valor_mensalidade, ativo, alta)
VALUES
    (1, 1, 1, DATEADD('DAY', -30, CURRENT_DATE),
     DATEADD('MONTH', 9, DATEADD('DAY', -30, CURRENT_DATE)), NULL,
     'EM_TRATAMENTO', 0.00, TRUE, NULL),
    (2, 2, 2, DATEADD('DAY', -60, CURRENT_DATE),
     DATEADD('MONTH', 9, DATEADD('DAY', -60, CURRENT_DATE)), NULL,
     'EM_TRATAMENTO', 1200.00, TRUE, NULL),
    (3, 3, 3, DATEADD('DAY', -300, CURRENT_DATE),
     DATEADD('MONTH', 9, DATEADD('DAY', -300, CURRENT_DATE)), DATEADD('DAY', -30, CURRENT_DATE),
     'ALTA_CONCEDIDA', 900.00, FALSE, 'CONCLUSAO'),
    (4, 4, 1, DATEADD('MONTH', -9, DATEADD('DAY', 15, CURRENT_DATE)),
     DATEADD('DAY', 15, CURRENT_DATE), NULL,
     'PROXIMO_ALTA', 0.00, TRUE, NULL),
    (5, 5, 2, DATEADD('MONTH', -9, DATEADD('DAY', 20, CURRENT_DATE)),
     DATEADD('DAY', 20, CURRENT_DATE), NULL,
     'PROXIMO_ALTA', 1350.00, TRUE, NULL),
    (6, 6, 1, DATEADD('MONTH', -9, DATEADD('DAY', -15, CURRENT_DATE)),
     DATEADD('DAY', -15, CURRENT_DATE), NULL,
     'ALTA_VENCIDA', 0.00, TRUE, NULL),
    (7, 7, 2, DATEADD('MONTH', -9, DATEADD('DAY', -10, CURRENT_DATE)),
     DATEADD('DAY', -10, CURRENT_DATE), NULL,
     'ALTA_VENCIDA', 1500.00, TRUE, NULL),
    (8, 1, 1, DATEADD('YEAR', -6, CURRENT_DATE),
     DATEADD('MONTH', 9, DATEADD('YEAR', -6, CURRENT_DATE)),
     DATEADD('MONTH', 9, DATEADD('YEAR', -6, CURRENT_DATE)),
     'ALTA_CONCEDIDA', 0.00, FALSE, 'CONCLUSAO'),
    (9, 2, 1, DATEADD('YEAR', -6, CURRENT_DATE),
     DATEADD('MONTH', 9, DATEADD('YEAR', -6, CURRENT_DATE)),
     DATEADD('MONTH', 3, DATEADD('YEAR', -6, CURRENT_DATE)),
     'ALTA_CONCEDIDA', 0.00, FALSE, 'DESISTENCIA'),
    (10, 3, 1, DATEADD('YEAR', -6, CURRENT_DATE),
     DATEADD('MONTH', 9, DATEADD('YEAR', -6, CURRENT_DATE)),
     DATEADD('MONTH', 4, DATEADD('YEAR', -6, CURRENT_DATE)),
     'ALTA_CONCEDIDA', 0.00, FALSE, 'DESLIGAMENTO'),
    (11, 1, 1, DATEADD('YEAR', -4, CURRENT_DATE),
     DATEADD('MONTH', 9, DATEADD('YEAR', -4, CURRENT_DATE)),
     DATEADD('MONTH', 9, DATEADD('YEAR', -4, CURRENT_DATE)),
     'ALTA_CONCEDIDA', 0.00, FALSE, 'CONCLUSAO'),
    (12, 2, 1, DATEADD('YEAR', -4, CURRENT_DATE),
     DATEADD('MONTH', 9, DATEADD('YEAR', -4, CURRENT_DATE)),
     DATEADD('MONTH', 9, DATEADD('YEAR', -4, CURRENT_DATE)),
     'ALTA_CONCEDIDA', 0.00, FALSE, 'CONCLUSAO'),
    (13, 3, 1, DATEADD('YEAR', -4, CURRENT_DATE),
     DATEADD('MONTH', 9, DATEADD('YEAR', -4, CURRENT_DATE)),
     DATEADD('MONTH', 3, DATEADD('YEAR', -4, CURRENT_DATE)),
     'ALTA_CONCEDIDA', 0.00, FALSE, 'DESISTENCIA'),
    (14, 1, 2, DATEADD('YEAR', -5, CURRENT_DATE),
     DATEADD('MONTH', 9, DATEADD('YEAR', -5, CURRENT_DATE)),
     DATEADD('MONTH', 9, DATEADD('YEAR', -5, CURRENT_DATE)),
     'ALTA_CONCEDIDA', 1000.00, FALSE, 'CONCLUSAO'),
    (15, 2, 2, DATEADD('YEAR', -5, CURRENT_DATE),
     DATEADD('MONTH', 9, DATEADD('YEAR', -5, CURRENT_DATE)),
     DATEADD('MONTH', 4, DATEADD('YEAR', -5, CURRENT_DATE)),
     'ALTA_CONCEDIDA', 1100.00, FALSE, 'DESLIGAMENTO'),
    (16, 3, 2, DATEADD('YEAR', -5, CURRENT_DATE),
     DATEADD('MONTH', 9, DATEADD('YEAR', -5, CURRENT_DATE)),
     DATEADD('MONTH', 3, DATEADD('YEAR', -5, CURRENT_DATE)),
     'ALTA_CONCEDIDA', 950.00, FALSE, 'DESISTENCIA'),
    (17, 1, 2, DATEADD('YEAR', -3, CURRENT_DATE),
     DATEADD('MONTH', 9, DATEADD('YEAR', -3, CURRENT_DATE)),
     DATEADD('MONTH', 9, DATEADD('YEAR', -3, CURRENT_DATE)),
     'ALTA_CONCEDIDA', 1200.00, FALSE, 'CONCLUSAO'),
    (18, 2, 2, DATEADD('YEAR', -3, CURRENT_DATE),
     DATEADD('MONTH', 9, DATEADD('YEAR', -3, CURRENT_DATE)),
     DATEADD('MONTH', 9, DATEADD('YEAR', -3, CURRENT_DATE)),
     'ALTA_CONCEDIDA', 1250.00, FALSE, 'CONCLUSAO'),
    (19, 3, 2, DATEADD('YEAR', -3, CURRENT_DATE),
     DATEADD('MONTH', 9, DATEADD('YEAR', -3, CURRENT_DATE)),
     DATEADD('MONTH', 4, DATEADD('YEAR', -3, CURRENT_DATE)),
     'ALTA_CONCEDIDA', 1150.00, FALSE, 'DESLIGAMENTO'),
    (20, 3, 3, DATEADD('YEAR', -2, CURRENT_DATE),
     DATEADD('MONTH', 9, DATEADD('YEAR', -2, CURRENT_DATE)),
     DATEADD('MONTH', 9, DATEADD('YEAR', -2, CURRENT_DATE)),
     'ALTA_CONCEDIDA', 850.00, FALSE, 'CONCLUSAO');

-- IDs explícitos não avançam automaticamente as identidades no H2.
-- Os próximos cadastros da API devem começar depois dos registros desta carga.
ALTER TABLE cargos ALTER COLUMN id RESTART WITH 6;
ALTER TABLE usuarios ALTER COLUMN id RESTART WITH 5;
ALTER TABLE convenios ALTER COLUMN id RESTART WITH 5;
ALTER TABLE funcionarios ALTER COLUMN id RESTART WITH 4;
ALTER TABLE responsaveis ALTER COLUMN id RESTART WITH 3;
ALTER TABLE acolhidos ALTER COLUMN id RESTART WITH 9;
ALTER TABLE medicamento ALTER COLUMN id RESTART WITH 4;
ALTER TABLE triagem ALTER COLUMN id RESTART WITH 8;
ALTER TABLE internacao ALTER COLUMN id RESTART WITH 21;
