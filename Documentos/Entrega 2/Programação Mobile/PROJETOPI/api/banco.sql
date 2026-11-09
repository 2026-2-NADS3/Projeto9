-- Banco de dados do app Próxima Etapa (MySQL / XAMPP)
-- Execute este arquivo no phpMyAdmin (aba Importar) ou no MySQL Workbench.
-- Todos os dados são fictícios, para demonstração.

DROP DATABASE IF EXISTS proxima_etapa;
CREATE DATABASE proxima_etapa CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE proxima_etapa;

CREATE TABLE alunos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(120) NOT NULL,
  email VARCHAR(120) NOT NULL UNIQUE,
  senha_hash CHAR(64) NOT NULL,
  telefone VARCHAR(20),
  escola VARCHAR(120),
  serie VARCHAR(20),
  perfil_vocacional VARCHAR(10) DEFAULT ''
);

CREATE TABLE cursos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  titulo VARCHAR(120) NOT NULL,
  descricao TEXT,
  instituicao VARCHAR(60) NOT NULL,
  area VARCHAR(10),
  carga_horaria INT NOT NULL,
  data_inicio DATE NOT NULL,
  data_fim DATE NOT NULL,
  horario VARCHAR(40),
  local VARCHAR(150),
  imagem VARCHAR(40),
  total_aulas INT NOT NULL DEFAULT 1
);

CREATE TABLE agenda (
  id INT AUTO_INCREMENT PRIMARY KEY,
  aluno_id INT NOT NULL,
  curso_id INT NOT NULL,
  titulo VARCHAR(150) NOT NULL,
  data DATE NOT NULL,
  horario VARCHAR(5) NOT NULL,
  local VARCHAR(150),
  codigo_qr VARCHAR(30) NOT NULL UNIQUE,
  FOREIGN KEY (aluno_id) REFERENCES alunos(id),
  FOREIGN KEY (curso_id) REFERENCES cursos(id)
);

CREATE TABLE inscricoes (
  id INT AUTO_INCREMENT PRIMARY KEY,
  aluno_id INT NOT NULL,
  curso_id INT NOT NULL,
  aulas_assistidas INT NOT NULL DEFAULT 0,
  total_aulas INT NOT NULL DEFAULT 1,
  concluido TINYINT(1) NOT NULL DEFAULT 0,
  data_conclusao DATE NULL,
  codigo_certificado VARCHAR(30) NULL,
  UNIQUE KEY uk_inscricao (aluno_id, curso_id),
  FOREIGN KEY (aluno_id) REFERENCES alunos(id),
  FOREIGN KEY (curso_id) REFERENCES cursos(id)
);

-- Presença: uma por aluno em cada encontro (evita duplicidade)
CREATE TABLE checkins (
  id INT AUTO_INCREMENT PRIMARY KEY,
  aluno_id INT NOT NULL,
  agenda_id INT NOT NULL,
  codigo_qr VARCHAR(30) NOT NULL,
  data_hora DATETIME NOT NULL,
  UNIQUE KEY uk_presenca (aluno_id, agenda_id),
  FOREIGN KEY (aluno_id) REFERENCES alunos(id),
  FOREIGN KEY (agenda_id) REFERENCES agenda(id)
);

CREATE TABLE notificacoes (
  id INT AUTO_INCREMENT PRIMARY KEY,
  titulo VARCHAR(150) NOT NULL,
  mensagem TEXT NOT NULL,
  tipo VARCHAR(20) NOT NULL DEFAULT 'aviso',
  data DATETIME NOT NULL,
  curso_id INT NULL,
  FOREIGN KEY (curso_id) REFERENCES cursos(id)
);

CREATE TABLE mensagens (
  id INT AUTO_INCREMENT PRIMARY KEY,
  aluno_id INT NOT NULL,
  texto TEXT NOT NULL,
  autor VARCHAR(10) NOT NULL,
  data_hora DATETIME NOT NULL,
  FOREIGN KEY (aluno_id) REFERENCES alunos(id)
);

-- Conta de demonstração: aluno@proximaetapa.org / 123456 (senha guardada como hash SHA-256)
INSERT INTO alunos (id, nome, email, senha_hash, telefone, escola, serie, perfil_vocacional) VALUES
  (1, 'Ana Beatriz Souza', 'aluno@proximaetapa.org', SHA2('123456', 256), '(11) 98888-7777', 'E.E. Jardim das Flores', '3º ano', '');

INSERT INTO cursos (id, titulo, descricao, instituicao, area, carga_horaria, data_inicio, data_fim, horario, local, imagem, total_aulas) VALUES
  (1, 'Empreendedorismo', 'Como transformar uma ideia em negócio: modelo de negócio, público-alvo e primeiros passos para empreender.', 'FECAP', 'ADM', 8, '2026-08-18', '2026-08-25', '19h00 às 21h00', 'FECAP - Liberdade, Sala 302', 'curso1', 2),
  (2, 'Técnicas de RH', 'Recrutamento, entrevistas e como se destacar em processos seletivos.', 'ESPM', 'ADM', 4, '2026-08-29', '2026-08-29', '09h00 às 13h00', 'ESPM - Vila Mariana', 'curso1', 1),
  (3, 'Simulação da ONU', 'Um dia inteiro representando países em um comitê da ONU: negociação, oratória e argumentação.', 'Insper', 'COM', 8, '2026-11-14', '2026-11-14', '08h30 às 17h00', 'Insper - Vila Olímpia, Auditório', 'curso3', 1),
  (4, 'Direito Constitucional', 'Introdução aos direitos e deveres do cidadão e à organização do Estado brasileiro.', 'FAAP', 'ADM', 6, '2026-12-01', '2026-12-08', '19h00 às 21h00', 'FAAP - Higienópolis', 'curso1', 2),
  (5, 'Softskills', 'Comunicação, trabalho em equipe, empatia e gestão do tempo.', 'Oswaldo Cruz', 'COM', 6, '2026-09-05', '2026-09-05', '09h00 às 15h00', 'Faculdades Oswaldo Cruz - Barra Funda', 'curso3', 1),
  (6, 'Educação Financeira', 'Orçamento pessoal, juros, crédito consciente e primeiros investimentos.', 'FECAP', 'FIN', 12, '2026-10-27', '2026-11-17', '19h00 às 21h00', 'FECAP - Liberdade, Laboratório 2', 'curso2', 6),
  (7, 'Comunicação Eficaz', 'Como falar em público, escrever com clareza e se comunicar em entrevistas e apresentações.', 'ESPM', 'COM', 8, '2026-11-04', '2026-11-25', '19h00 às 21h00', 'ESPM - Vila Mariana, Sala 12', 'curso3', 4),
  (8, 'Carreira 2.0', 'Currículo, LinkedIn e planejamento de carreira para o primeiro emprego.', 'FECAP', 'ADM', 4, '2026-12-12', '2026-12-12', '09h00 às 13h00', 'FECAP - Liberdade, Auditório', 'curso1', 1),
  (9, 'Oratória', 'Técnicas de voz, postura e organização das ideias para falar com segurança.', 'FECAP', 'COM', 8, '2026-09-15', '2026-09-22', '19h00 às 21h00', 'FECAP - Liberdade, Sala 105', 'curso3', 2),
  (10, 'Tecnologia', 'Panorama das carreiras de tecnologia: programação, dados, design de produtos e segurança.', 'FECAP', 'TEC', 6, '2026-09-26', '2026-09-26', '09h00 às 15h00', 'FECAP - Liberdade, Laboratório de Informática', 'curso2', 1),
  (11, 'Imagem Pessoal e Networking', 'Como construir uma boa imagem profissional e criar uma rede de contatos.', 'FECAP', 'COM', 4, '2026-10-03', '2026-10-03', '09h00 às 13h00', 'FECAP - Liberdade, Sala 302', 'curso3', 1),
  (12, 'Intercâmbio', 'Bolsas, programas e dicas para estudar fora do país.', 'Insper', 'ADM', 3, '2026-12-05', '2026-12-05', '10h00 às 13h00', 'Insper - Vila Olímpia', 'curso1', 1),
  (13, 'Inteligência Emocional', 'Autoconhecimento, controle da ansiedade e relações saudáveis na escola e no trabalho.', 'FSA', 'ADM', 6, '2026-11-24', '2026-12-01', '19h00 às 21h00', 'Centro Universitário FSA - Santo André', 'curso1', 2),
  (14, 'Liderança', 'Estilos de liderança, tomada de decisão e como motivar uma equipe.', 'FECAP', 'ADM', 8, '2026-10-06', '2026-10-13', '19h00 às 21h00', 'FECAP - Liberdade, Sala 210', 'curso1', 2),
  (15, 'Inteligência Artificial', 'O que é IA, como ela funciona e como usá-la de forma ética nos estudos e no trabalho.', 'Inteli', 'TEC', 6, '2026-11-28', '2026-11-28', '09h00 às 15h00', 'Inteli - Butantã', 'curso2', 1),
  (16, 'Inovação e Criatividade', 'Design thinking na prática: do problema à solução em um dia de oficina.', 'FECAP', 'TEC', 6, '2026-10-17', '2026-10-17', '09h00 às 15h00', 'FECAP - Liberdade, Sala Maker', 'curso2', 1);

INSERT INTO agenda (id, aluno_id, curso_id, titulo, data, horario, local, codigo_qr) VALUES
  (1, 1, 1, 'Empreendedorismo - Encontro 1', '2026-08-18', '19:00', 'FECAP - Liberdade, Sala 302', 'PE-ENC-001'),
  (2, 1, 1, 'Empreendedorismo - Encontro 2', '2026-08-25', '19:00', 'FECAP - Liberdade, Sala 302', 'PE-ENC-002'),
  (3, 1, 2, 'Técnicas de RH', '2026-08-29', '09:00', 'ESPM - Vila Mariana', 'PE-ENC-003'),
  (4, 1, 3, 'Simulação da ONU', '2026-11-14', '08:30', 'Insper - Vila Olímpia, Auditório', 'PE-ENC-004'),
  (5, 1, 5, 'Softskills', '2026-09-05', '09:00', 'Faculdades Oswaldo Cruz - Barra Funda', 'PE-ENC-005'),
  (6, 1, 6, 'Educação Financeira - Encontro 1', '2026-10-27', '19:00', 'FECAP - Liberdade, Laboratório 2', 'PE-ENC-006'),
  (7, 1, 6, 'Educação Financeira - Encontro 2', '2026-10-29', '19:00', 'FECAP - Liberdade, Laboratório 2', 'PE-ENC-007'),
  (8, 1, 6, 'Educação Financeira - Encontro 3', '2026-11-03', '19:00', 'FECAP - Liberdade, Laboratório 2', 'PE-ENC-008'),
  (9, 1, 6, 'Educação Financeira - Encontro 4', '2026-11-10', '19:00', 'FECAP - Liberdade, Laboratório 2', 'PE-ENC-009'),
  (10, 1, 6, 'Educação Financeira - Encontro 5', '2026-11-12', '19:00', 'FECAP - Liberdade, Laboratório 2', 'PE-ENC-010'),
  (11, 1, 6, 'Educação Financeira - Encontro 6', '2026-11-17', '19:00', 'FECAP - Liberdade, Laboratório 2', 'PE-ENC-011'),
  (12, 1, 7, 'Comunicação Eficaz - Encontro 1', '2026-11-04', '19:00', 'ESPM - Vila Mariana, Sala 12', 'PE-ENC-012'),
  (13, 1, 7, 'Comunicação Eficaz - Encontro 2', '2026-11-11', '19:00', 'ESPM - Vila Mariana, Sala 12', 'PE-ENC-013'),
  (14, 1, 7, 'Comunicação Eficaz - Encontro 3', '2026-11-18', '19:00', 'ESPM - Vila Mariana, Sala 12', 'PE-ENC-014'),
  (15, 1, 7, 'Comunicação Eficaz - Encontro 4', '2026-11-25', '19:00', 'ESPM - Vila Mariana, Sala 12', 'PE-ENC-015'),
  (16, 1, 9, 'Oratória - Encontro 1', '2026-09-15', '19:00', 'FECAP - Liberdade, Sala 105', 'PE-ENC-016'),
  (17, 1, 9, 'Oratória - Encontro 2', '2026-09-22', '19:00', 'FECAP - Liberdade, Sala 105', 'PE-ENC-017'),
  (18, 1, 10, 'Tecnologia', '2026-09-26', '09:00', 'FECAP - Liberdade, Laboratório de Informática', 'PE-ENC-018'),
  (19, 1, 11, 'Imagem Pessoal e Networking', '2026-10-03', '09:00', 'FECAP - Liberdade, Sala 302', 'PE-ENC-019'),
  (20, 1, 14, 'Liderança - Encontro 1', '2026-10-06', '19:00', 'FECAP - Liberdade, Sala 210', 'PE-ENC-020'),
  (21, 1, 14, 'Liderança - Encontro 2', '2026-10-13', '19:00', 'FECAP - Liberdade, Sala 210', 'PE-ENC-021'),
  (22, 1, 16, 'Inovação e Criatividade', '2026-10-17', '09:00', 'FECAP - Liberdade, Sala Maker', 'PE-ENC-022');

INSERT INTO inscricoes (id, aluno_id, curso_id, aulas_assistidas, total_aulas, concluido, data_conclusao, codigo_certificado) VALUES
  (1, 1, 1, 2, 2, 1, '2026-08-25', 'PE-2026-0011037'),
  (2, 1, 2, 1, 1, 1, '2026-08-29', 'PE-2026-0021074'),
  (3, 1, 3, 0, 1, 0, NULL, NULL),
  (4, 1, 5, 1, 1, 1, '2026-09-05', 'PE-2026-0051185'),
  (5, 1, 6, 3, 6, 0, NULL, NULL),
  (6, 1, 7, 1, 4, 0, NULL, NULL),
  (7, 1, 9, 2, 2, 1, '2026-09-22', 'PE-2026-0091333'),
  (8, 1, 10, 1, 1, 1, '2026-09-26', 'PE-2026-0101370'),
  (9, 1, 11, 1, 1, 1, '2026-10-03', 'PE-2026-0111407'),
  (10, 1, 14, 2, 2, 1, '2026-10-13', 'PE-2026-0141518'),
  (11, 1, 16, 1, 1, 1, '2026-10-17', 'PE-2026-0161592');

INSERT INTO checkins (id, aluno_id, agenda_id, codigo_qr, data_hora) VALUES
  (1, 1, 1, 'PE-ENC-001', '2026-08-18 19:00:12'),
  (2, 1, 2, 'PE-ENC-002', '2026-08-25 19:00:12'),
  (3, 1, 3, 'PE-ENC-003', '2026-08-29 09:00:12'),
  (4, 1, 5, 'PE-ENC-005', '2026-09-05 09:00:12'),
  (5, 1, 6, 'PE-ENC-006', '2026-10-27 19:00:12'),
  (6, 1, 7, 'PE-ENC-007', '2026-10-29 19:00:12'),
  (7, 1, 8, 'PE-ENC-008', '2026-11-03 19:00:12'),
  (8, 1, 12, 'PE-ENC-012', '2026-11-04 19:00:12'),
  (9, 1, 16, 'PE-ENC-016', '2026-09-15 19:00:12'),
  (10, 1, 17, 'PE-ENC-017', '2026-09-22 19:00:12'),
  (11, 1, 18, 'PE-ENC-018', '2026-09-26 09:00:12'),
  (12, 1, 19, 'PE-ENC-019', '2026-10-03 09:00:12'),
  (13, 1, 20, 'PE-ENC-020', '2026-10-06 19:00:12'),
  (14, 1, 21, 'PE-ENC-021', '2026-10-13 19:00:12'),
  (15, 1, 22, 'PE-ENC-022', '2026-10-17 09:00:12');

INSERT INTO notificacoes (id, titulo, mensagem, tipo, data, curso_id) VALUES
  (1, 'Novo curso disponível: Inteligência Artificial', 'O Inteli abriu vagas para a oficina de IA no dia 28/11. É um curso de dia único!', 'novo_curso', '2026-11-05 10:00', 15),
  (2, 'Simulação da ONU é neste sábado', 'Curso de dia único no Insper em 14/11, das 8h30 às 17h. Leve documento com foto e faça o check-in na entrada.', 'aviso', '2026-11-07 18:30', 3),
  (3, 'Certificados disponíveis no app', 'Agora você pode ver e compartilhar seus certificados direto pelo Perfil.', 'aviso', '2026-11-02 09:00', NULL),
  (4, 'Novo curso disponível: Carreira 2.0', 'Monte seu currículo e seu LinkedIn com a FECAP em 12/12.', 'novo_curso', '2026-11-01 14:00', 8);

INSERT INTO mensagens (id, aluno_id, texto, autor, data_hora) VALUES
  (1, 1, 'Olá! Bem-vindo(a) ao app da Próxima Etapa. Use este canal para tirar dúvidas sobre cursos, encontros e certificados.', 'ong', '2026-11-01 09:00:00');

-- Para testar as notificações: com o app aberto, rode o comando abaixo e toque em sincronizar.
-- INSERT INTO notificacoes (titulo, mensagem, tipo, data, curso_id)
-- VALUES ('Novo curso disponível', 'Abrimos vagas para Inteligência Emocional na FSA.', 'novo_curso', NOW(), 13);

-- Para testar o chat: simule uma resposta da ONG.
-- INSERT INTO mensagens (aluno_id, texto, autor, data_hora) VALUES (1, 'Oi! Como podemos ajudar?', 'ong', NOW());
