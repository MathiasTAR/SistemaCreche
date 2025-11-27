USE CRECHE;

-- =======================
-- 👥 POPULANDO PESSOA
-- =======================
INSERT INTO PESSOA (NOME, CPF, RG, TELEFONE, OUTRO_CONTATO) VALUES
('João Silva', '12345678901', '1234567', '(11) 9999-8888', '(11) 3333-4444'),
('Maria Santos', '23456789012', '2345678', '(11) 8888-7777', '(11) 2222-3333'),
('Pedro Oliveira', '34567890123', '3456789', '(11) 7777-6666', NULL),
('Ana Costa', '45678901234', '4567890', '(11) 6666-5555', '(11) 1111-2222'),
('Carlos Souza', '56789012345', '5678901', '(11) 5555-4444', NULL),
('Juliana Lima', '67890123456', '6789012', '(11) 4444-3333', '(11) 9999-1111'),
('Ricardo Martins', '78901234567', '7890123', '(11) 3333-2222', NULL),
('Fernanda Rocha', '89012345678', '8901234', '(11) 2222-1111', '(11) 8888-9999'),
('Bruno Almeida', '90123456789', '9012345', '(11) 1111-0000', NULL),
('Patrícia Nunes', '01234567890', '0123456', '(11) 0000-9999', '(11) 7777-8888');

-- =======================
-- 🏠 POPULANDO ENDERECO
-- =======================
INSERT INTO ENDERECO (LOGRADOURO, NUMERO, COMPLEMENTO, BAIRRO, MUNICIPIO, CEP, UF, PONTO_REFERENCIA, TELEFONE_RESIDENCIAL) VALUES
('Rua das Flores', '123', 'Apto 101', 'Centro', 'São Paulo', '01234-567', 'SP', 'Próximo ao mercado', '(11) 2222-3333'),
('Avenida Brasil', '456', NULL, 'Jardim América', 'São Paulo', '02345-678', 'SP', 'Em frente à praça', '(11) 3333-4444'),
('Rua Augusta', '789', 'Sala 5', 'Consolação', 'São Paulo', '03456-789', 'SP', 'Próximo ao metrô', '(11) 4444-5555'),
('Alameda Santos', '321', 'Apto 302', 'Cerqueira César', 'São Paulo', '04567-890', 'SP', 'Ao lado do shopping', '(11) 5555-6666'),
('Rua Xavier de Toledo', '654', NULL, 'República', 'São Paulo', '05678-901', 'SP', 'Próximo ao teatro', '(11) 6666-7777'),
('Avenida Paulista', '987', 'Conjunto 12', 'Bela Vista', 'São Paulo', '06789-012', 'SP', 'Em frente ao MASP', '(11) 7777-8888'),
('Rua da Consolação', '147', 'Apto 701', 'Consolação', 'São Paulo', '07890-123', 'SP', 'Próximo à faculdade', '(11) 8888-9999'),
('Alameda Jaú', '258', NULL, 'Jardins', 'São Paulo', '08901-234', 'SP', 'Ao lado do parque', '(11) 9999-0000'),
('Rua Haddock Lobo', '369', 'Sala 10', 'Cerqueira César', 'São Paulo', '09012-345', 'SP', 'Próximo à livraria', '(11) 0000-1111'),
('Avenida Rebouças', '741', 'Apto 1501', 'Pinheiros', 'São Paulo', '10123-456', 'SP', 'Em frente ao hospital', '(11) 1111-2222');

-- =======================
-- 👨‍💼 POPULANDO RESPONSAVEL
-- =======================
INSERT INTO RESPONSAVEL (ID_TIPO_RESPONSAVEL, ID_PESSOA, TELEFONE, LOCAL_TRABALHO, AUXILIO_GOV, ID_TIPO_AUXILIO, NUMERO_NIS) VALUES
(1, 1, '(11) 9999-8888', 'Empresa XYZ', TRUE, 1, '12345678901'),
(2, 2, '(11) 8888-7777', 'Escola ABC', FALSE, NULL, NULL),
(1, 3, '(11) 7777-6666', 'Hospital Municipal', TRUE, 2, '23456789012'),
(2, 4, '(11) 6666-5555', 'Loja de Departamentos', FALSE, NULL, NULL),
(1, 5, '(11) 5555-4444', 'Construtora Beta', TRUE, 3, '34567890123'),
(2, 6, '(11) 4444-3333', 'Salão de Beleza', FALSE, NULL, NULL),
(9, 7, '(11) 3333-2222', 'Oficina Mecânica', TRUE, 4, '45678901234'),
(2, 8, '(11) 2222-1111', 'Restaurante', FALSE, NULL, NULL),
(1, 9, '(11) 1111-0000', 'Mercado Local', TRUE, 5, '56789012345'),
(2, 10, '(11) 0000-9999', 'Creche Municipal', FALSE, NULL, NULL);

-- =======================
-- 👶 POPULANDO CRIANCA
-- =======================
INSERT INTO CRIANCA (FOTO, NOME, CPF, RG, DATA_NASCIMENTO, SEXO, COR_RACA, POSSUI_IRMAO_CRECHE, POSSUI_IRMAO_GEMEO, CARTSUS, UNIDADE_SAUDE, MUNICIPIO_NASCIMENTO, CARTORIO_REGISTRO, CERTIDAO_NASCIMENTO_NUM, DATA_EMISSAO_CERTIDAO, ORG_EMISSOR_CERTIDAO, RESTRICAO_ALIMENTAR, DESCRICAO_RESTRICOES_ALIMENTARES, ALERGIA, PROBLEMA_SAUDE, RESTRI_ALIMENTAR, MOB_RED, DEF_MULTI, EDUC_ESPECIAL, RESPONSAVEL_BENEFICIARIO_AUXILIO_GOV, ID_RESPONSAVEL, ID_TIPO_AUXILIO, ID_CLASSIFICACAO_ESPECIAL, STATUS_CLASSIFICACAO_ESPECIAL, ID_MAE, ID_PAI) VALUES 
('foto1.jpg', 'Lucas Silva', "12345678901", '2234567', '2020-03-10', 'MASCULINO', 'BRANCA', FALSE, FALSE, '123456789012345', 'UBS Centro', 'São Paulo', '1º Ofício', '123456789', '2020-04-15', 'SP', FALSE, NULL, TRUE, 'Asma leve', 'Nenhuma', 'NENHUMA', FALSE, FALSE, TRUE, 1, 1, NULL, FALSE, 2, 1),
('foto2.jpg', 'Sophia Santos', "12345678902", '3234567', '2020-07-22', 'FEMININO', 'PARDA', TRUE, FALSE, '234567890123456', 'UBS Jardim', 'São Paulo', '2º Ofício', '234567890', '2020-08-30', 'SP', TRUE, 'Intolerância à lactose', FALSE, NULL, 'Sem lactose', 'NENHUMA', FALSE, FALSE, FALSE, 2, NULL, 2, FALSE, 4, 3),
('foto3.jpg', 'Miguel Oliveira', "12345678903", '1234567', '2019-11-05', 'MASCULINO', 'BRANCA', FALSE, TRUE, '345678901234567', 'Hospital Municipal', 'São Paulo', '3º Ofício', '345678901', '2019-12-10', 'SP', FALSE, NULL, TRUE, 'Rinite alérgica', 'Nenhuma', 'NENHUMA', FALSE, TRUE, TRUE, 3, 2, 3, TRUE, 6, 5),
('foto4.jpg', 'Alice Costa', "12345678904", '4234567', '2021-01-15', 'FEMININO', 'PRETA', TRUE, FALSE, '456789012345678', 'UBS Leste', 'São Paulo', '4º Ofício', '456789012', '2021-02-20', 'SP', FALSE, NULL, FALSE, NULL, 'Nenhuma', 'NENHUMA', FALSE, FALSE, FALSE, 4, NULL, NULL, FALSE, 8, 7),
('foto5.jpg', 'Arthur Souza', "12345678905", '5234567', '2020-05-30', 'MASCULINO', 'AMARELA', FALSE, FALSE, '567890123456789', 'UBS Norte', 'São Paulo', '5º Ofício', '567890123', '2020-07-05', 'SP', TRUE, 'Alergia a glúten', TRUE, 'Dermatite', 'Sem glúten', 'NENHUMA', FALSE, FALSE, TRUE, 5, 3, 5, TRUE, 10, 9),
('foto6.jpg', 'Helena Lima', "12345678906", '7234567', '2019-09-12', 'FEMININO', 'INDIGENA', FALSE, FALSE, '678901234567890', 'UBS Oeste', 'São Paulo', '6º Ofício', '678901234', '2019-10-18', 'SP', FALSE, NULL, FALSE, NULL, 'Nenhuma', 'TEMPORARIA', TRUE, FALSE, FALSE, 6, NULL, 6, FALSE, 2, 1),
('foto7.jpg', 'Davi Martins', "12345678907", '6234567', '2021-02-28', 'MASCULINO', 'PARDA', TRUE, FALSE, '789012345678901', 'Hospital Estadual', 'São Paulo', '7º Ofício', '789012345', '2021-04-05', 'SP', FALSE, NULL, TRUE, 'Bronquite', 'Nenhuma', 'NENHUMA', FALSE, TRUE, TRUE, 7, 4, 7, TRUE, 4, 3),
('foto8.jpg', 'Valentina Rocha', "12345678908", '8234567', '2020-08-17', 'FEMININO', 'BRANCA', FALSE, FALSE, '890123456789012', 'UBS Sul', 'São Paulo', '8º Ofício', '890123456', '2020-09-25', 'SP', TRUE, 'Alergia a corantes', FALSE, NULL, 'Sem corantes', 'NENHUMA', FALSE, FALSE, FALSE, 8, NULL, 8, FALSE, 6, 5),
('foto9.jpg', 'Gabriel Almeida', "12345678909", '9234567', '2019-12-03', 'MASCULINO', 'PRETA', TRUE, TRUE, '901234567890123', 'UBS Centro', 'São Paulo', '9º Ofício', '901234567', '2020-01-08', 'SP', FALSE, NULL, FALSE, NULL, 'Nenhuma', 'PERMANENTE', FALSE, FALSE, TRUE, 9, 5, 9, TRUE, 8, 7),
('foto10.jpg', 'Laura Nunes', "12345678900", '0234567', '2021-04-20', 'FEMININO', 'PARDA', FALSE, FALSE, '012345678901234', 'Hospital Particular', 'São Paulo', '10º Ofício', '012345678', '2021-05-30', 'SP', FALSE, NULL, TRUE, 'Alergia alimentar múltipla', 'Dieta restritiva', 'NENHUMA', TRUE, TRUE, FALSE, 10, NULL, 10, FALSE, 10, 9);

-- =======================
-- 🤧 POPULANDO CRIANCA_ALERGIA
-- =======================
INSERT INTO CRIANCA_ALERGIA (ID_CRIANCA, ID_ALERGIA) VALUES
(1, 1), (1, 2),  -- Lucas: Leite e Ovo
(2, 3),          -- Sophia: Amendoim
(3, 4), (3, 5),  -- Miguel: Frutos do mar e Glúten
(5, 5),          -- Arthur: Glúten
(7, 6), (7, 7),  -- Davi: Soja e Corante
(8, 7),          -- Valentina: Corante
(10, 1), (10, 2), (10, 3);  -- Laura: Leite, Ovo e Amendoim

-- =======================
-- 👨‍👩‍👧‍👦 POPULANDO MEMBRO_FAMILIA
-- =======================
INSERT INTO MEMBRO_FAMILIA (ID_CRIANCA, NOME, IDADE, PARENTESCO, SITUACAO_ESCOLAR, SITUACAO_EMPREGO, RENDA) VALUES
(1, 'Maria Silva Santos', 35, 'MAE', 'ENSINO_SUPERIOR_COMPLETO', 'EMPREGADO_FORMAL', 2500.00),
(1, 'João Silva Santos', 38, 'PAI', 'ENSINO_SUPERIOR_COMPLETO', 'EMPREGADO_FORMAL', 3000.00),
(2, 'Ana Oliveira Costa', 28, 'MAE', 'ENSINO_MEDIO_COMPLETO', 'AUTONOMO', 1800.00),
(2, 'Pedro Oliveira Costa', 8, 'IRMAO', 'ENSINO_FUNDAMENTAL_1_1_5_ANO', 'ESTUDANTE', 0.00),
(3, 'Carla Pereira Lima', 32, 'MAE', 'ENSINO_SUPERIOR_INCOMPLETO', 'EMPREGADO_INFORMAL', 1500.00),
(3, 'Roberto Pereira Lima', 35, 'PAI', 'ENSINO_MEDIO_COMPLETO', 'EMPREGADO_FORMAL', 2200.00),
(4, 'Antônia Rodrigues Souza', 68, 'AVO', 'ENSINO_FUNDAMENTAL_COMPLETO', 'APOSENTADO', 1200.00),
(4, 'Patrícia Rodrigues Souza', 30, 'MAE', 'ENSINO_MEDIO_COMPLETO', 'DO_LAR', 0.00),
(5, 'Carlos Alberto Ferreira', 40, 'PAI', 'ENSINO_SUPERIOR_COMPLETO', 'SERVIDOR_PUBLICO', 4000.00),
(5, 'Fernanda Almeida Ferreira', 38, 'MAE', 'ENSINO_SUPERIOR_COMPLETO', 'EMPREGADO_FORMAL', 3500.00);

-- =======================
-- 👤 POPULANDO PESSOA_AUTORIZADA
-- =======================
INSERT INTO PESSOA_AUTORIZADA (ID_PESSOA, ID_CRIANCA, PARENTESCO, TELEFONE) VALUES
(1, 1, 'PAI', '(11) 9999-8888'),
(2, 1, 'MAE', '(11) 8888-7777'),
(3, 2, 'TIO', '(11) 7777-6666'),
(4, 2, 'AVO', '(11) 6666-5555'),
(5, 3, 'PAI', '(11) 5555-4444'),
(6, 3, 'MAE', '(11) 4444-3333'),
(7, 4, 'RESPONSAVEL_LEGAL', '(11) 3333-2222'),
(8, 4, 'TIA', '(11) 2222-1111'),
(9, 5, 'PAI', '(11) 1111-0000'),
(10, 5, 'MAE', '(11) 0000-9999');

-- =======================
-- 🏘️ POPULANDO SITUACAO_HABITACIONAL
-- =======================
INSERT INTO SITUACAO_HABITACIONAL 
(CASA_PROPRIA, CASA_CEDIDA, CASA_ALUGADA, VALOR_ALUGUEL, NUMERO_COMODOS, TIPO_PISO, TIPO_MORADIA, TIPO_COBERTURA, FOSSA, CIFON, ENERGIA_ELETRICA, AGUA_ENCANADA, COLETOR_PUBLICO) 
VALUES
(TRUE, FALSE, FALSE, NULL, 5, 'CERAMICA', 'ALVENARIA', 'TELHA', FALSE, TRUE, TRUE, TRUE, TRUE),
(FALSE, FALSE, TRUE, 800.00, 3, 'CIMENTO', 'TIJOLO', 'TELHA', TRUE, FALSE, TRUE, TRUE, TRUE),
(FALSE, TRUE, FALSE, NULL, 4, 'LAJOTA', 'ALVENARIA', 'LAJE', FALSE, TRUE, TRUE, TRUE, TRUE),
(TRUE, FALSE, FALSE, NULL, 6, 'CERAMICA', 'ALVENARIA', 'TELHA', FALSE, TRUE, TRUE, TRUE, TRUE),
(FALSE, FALSE, TRUE, 1200.00, 4, 'MADEIRA', 'MADEIRA', 'ZINCO', TRUE, FALSE, TRUE, TRUE, FALSE),
(TRUE, FALSE, FALSE, NULL, 5, 'CERAMICA', 'ALVENARIA', 'TELHA', FALSE, TRUE, TRUE, TRUE, TRUE),
(FALSE, FALSE, TRUE, 950.00, 3, 'CIMENTO', 'TIJOLO', 'TELHA', TRUE, FALSE, TRUE, TRUE, TRUE),
(FALSE, TRUE, FALSE, NULL, 4, 'LAJOTA', 'ALVENARIA', 'LAJE', FALSE, TRUE, TRUE, TRUE, TRUE),
(TRUE, FALSE, FALSE, NULL, 7, 'CERAMICA', 'ALVENARIA', 'TELHA', FALSE, TRUE, TRUE, TRUE, TRUE),
(FALSE, FALSE, TRUE, 700.00, 2, 'CHAO_BATIDO', 'TAIPA', 'ZINCO', TRUE, FALSE, TRUE, TRUE, FALSE);

-- =======================
-- 💰 POPULANDO COMPOSICAO_FAMILIAR
-- =======================
INSERT INTO COMPOSICAO_FAMILIAR (ID_CRIANCA, RENDA_FAMILIAR_TOTAL, RENDA_PER_CAPITA, TOTAL_MEMBROS) VALUES
(1, 5500.00, 1833.33, 3),
(2, 1800.00, 600.00, 3),
(3, 3700.00, 1233.33, 3),
(4, 1200.00, 600.00, 2),
(5, 7500.00, 2500.00, 3),
(6, 2800.00, 933.33, 3),
(7, 3200.00, 1066.67, 3),
(8, 1900.00, 633.33, 3),
(9, 5200.00, 1733.33, 3),
(10, 1500.00, 500.00, 3);

-- =======================
-- 📋 POPULANDO PRE_MATRICULA
-- =======================
INSERT INTO PRE_MATRICULA (ID_CRIANCA, DATA_PREMATRICULA, SITUACAO_HABITACIONAL, SITUACAO_PREMATRICULA, OBSERVACAO) VALUES
(1, '2024-01-15', 1, 'APROVADA', 'Documentação completa'),
(2, '2024-01-16', 2, 'EM_ANALISE', 'Aguardando comprovante de residência'),
(3, '2024-01-17', 3, 'APROVADA', 'Prioridade por deficiência'),
(4, '2024-01-18', 4, 'REPROVADA', 'Renda familiar acima do limite'),
(5, '2024-01-19', 5, 'APROVADA', 'Documentação em dia'),
(6, '2024-01-20', 6, 'EM_ANALISE', 'Necessita vaga especial'),
(7, '2024-01-21', 7, 'APROVADA', 'Encaminhamento do CRAS'),
(8, '2024-01-22', 8, 'CANCELADA', 'Família mudou de cidade'),
(9, '2024-01-23', 9, 'APROVADA', 'Irmão já matriculado'),
(10, '2024-01-24', 10, 'EM_ANALISE', 'Aguardando laudo médico');

-- =======================
-- 🎒 POPULANDO MATRICULA
-- =======================
INSERT INTO MATRICULA (ID_CRIANCA, ID_PRE_MATRICULA, DATA_MATRICULA, SERIE, ANO_LETIVO, ORIENT_RECEBIDA, DATA_DESLIGAMENTO, SITUACAO_MAT, DATA_VENCIMENTO) VALUES
(1, 1, '2024-02-01', 'BERCARIO_I', 2025, TRUE, NULL, 'ATIVA', '2026-12-15'),
(2, 2, '2024-02-02', 'BERCARIO_I', 2025, TRUE, NULL, 'ATIVA', '2026-12-15'),
(3, 3, '2024-02-03', 'BERCARIO_II', 2025, TRUE, NULL, 'ATIVA', '2026-12-15'),
(4, NULL, '2024-02-04', 'BERCARIO_I', 2025, TRUE, '2024-06-30', 'CANCELADA', '2024-12-15'),
(5, 5, '2024-02-05', 'MATERNAL_I', 2024, TRUE, NULL, 'ATIVA', '2025-12-15'),
(6, 6, '2024-02-06', 'BERCARIO_I', 2026, TRUE, NULL, 'ATIVA', '2026-12-15'),
(7, 7, '2024-02-07', 'MATERNAL_II', 2026, TRUE, NULL, 'ATIVA', '2026-12-15'),
(8, NULL, '2024-02-08', 'BERCARIO_II', 2026, TRUE, '2024-03-15', 'CANCELADA', '2024-12-15'),
(9, 9, '2024-02-09', 'BERCARIO_I', 2025, TRUE, NULL, 'ATIVA', '2024-12-15'),
(10, 10, '2024-02-10', 'BERCARIO_I', 2024, TRUE, NULL, 'ATIVA', '2024-12-15');

-- =======================
-- 📊 POPULANDO HISTORICO_MATRICULA
-- =======================
INSERT INTO HISTORICO_MATRICULA (ID_MATRICULA, ID_CRIANCA, ACAO) VALUES
(1, 1, 'CRIACAO'),
(2, 2, 'CRIACAO'),
(3, 3, 'CRIACAO'),
(4, 4, 'CRIACAO'),
(5, 5, 'CRIACAO'),
(6, 6, 'CRIACAO'),
(7, 7, 'CRIACAO'),
(8, 8, 'CRIACAO'),
(9, 9, 'CRIACAO'),
(10, 10, 'CRIACAO');