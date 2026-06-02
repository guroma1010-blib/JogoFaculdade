-- 1. CRIAÇÃO DO BANCO DE DADOS COM O NOVO NOME
CREATE DATABASE IF NOT EXISTS QuimQuest;
USE QuimQuest;

-- 2. LIMPEZA DE TABELAS ANTIGAS (Caso já existam no novo esquema)
DROP TABLE IF EXISTS materiais;
DROP TABLE IF EXISTS usuarios;

-- 3. TABELA DE USUÁRIOS
CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    codigo_num_individual VARCHAR(20) UNIQUE NOT NULL, -- RM ou CPF
    nome VARCHAR(100) NOT NULL,
    email_completo VARCHAR(100) UNIQUE NOT NULL,       -- Gerado no cadastro
    senha VARCHAR(50) NOT NULL,
    tipo_usuario ENUM('ALUNO', 'PROFESSOR') NOT NULL
);

-- 4. TABELA DE MATERIAIS E SISTEMAS
CREATE TABLE materiais (
    id_material INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    descricao TEXT,
    imagem_path VARCHAR(255) DEFAULT NULL
);

-- 5. INSERÇÃO DE USUÁRIOS DE TESTE
INSERT INTO usuarios (codigo_num_individual, nome, email_completo, senha, tipo_usuario) VALUES
('11111', 'Aluno ETEC Teste', '11111@aluno.cps.sp.gov.br', '123', 'ALUNO'),
('22222', 'Professor ETEC Teste', '22222@cps.sp.gov.br', 'mestre', 'PROFESSOR');

-- 6. INSERÇÃO COMPLETA DOS MATERIAIS E SISTEMAS DO SEU DOCUMENTO
INSERT INTO materiais (nome, categoria, descricao) VALUES
('Almofariz e pistilo', 'Vidraria/Porcelana', 'Usado para moer e pulverizar sólidos em pequenas quantidades.'),
('Alonga de borracha', 'Acessório', 'Usada para conectar funis ou conexões de vidro em sistemas de laboratório.'),
('Argola', 'Suporte', 'Anel de metal fixado ao suporte universal para apoiar funis e outros equipamentos.'),
('Agitador mecânico', 'Equipamento', 'Dispositivo elétrico usado para a agitação vigorosa de misturas e soluções.'),
('Balança analítica', 'Equipamento', 'Equipamento de altíssima precisão utilizado para pesagem de pequenas massas.'),
('Balança semi-analítica', 'Equipamento', 'Usada para pesagens gerais onde a precisão extrema de quatro casas decimais não é necessária.'),
('Balão de fundo chato', 'Vidraria', 'Utilizado para destilações, aquecimento e armazenamento de líquidos.'),
('Balão de fundo redondo', 'Vidraria', 'Muito utilizado em processos de destilação e aquecimento sob refluxo.'),
('Balão volumétrico', 'Vidraria', 'Possui um volume rigorosamente exato, sendo essencial para o preparo de soluções.'),
('Banho Maria', 'Equipamento', 'Usado para o aquecimento indireto de substâncias imersas em água aquecida.'),
('Barra magnética', 'Acessório', 'Pequeno ímã revestido de teflon inserido em soluções para promover agitação magnética.'),
('Bastão de vidro/baqueta', 'Vidraria', 'Usado para agitar misturas e auxiliar na transferência guiada de líquidos.'),
('Béquer', 'Vidraria', 'Recipiente de uso geral, serve para reações, dissoluções, cristalizações e aquecimento.'),
('Bico de Bunsen', 'Equipamento/Aquecimento', 'Queimador a gás usado para aquecimento direto de amostras e esterilização.'),
('Bureta', 'Vidraria', 'Equipamento cilíndrico de alta precisão com torneira na ponta, indispensável para titulações.'),
('Cadinho de porcelana', 'Porcelana', 'Suporta altíssimas temperaturas, usado para calcinação e fusão de sólidos.'),
('Capela de exaustão', 'Equipamento', 'Cabine ventilada para manipulação segura de gases, vapores e substâncias tóxicas.'),
('Cápsula de porcelana', 'Porcelana', 'Usada para a evaporação controlada de líquidos em soluções.'),
('Centrífuga', 'Equipamento', 'Aparelho que acelera a sedimentação e separa misturas por ação da força centrífuga.'),
('Coluna de Vigreux', 'Conexão', 'Coluna de vidro com indentações internas usada para separações em destilações fracionadas.'),
('Condensador de bolas', 'Vidraria', 'Usado para condensar vapores em destilações ou sistemas de refluxo contínuo.'),
('Condensador de serpentina', 'Vidraria', 'Promove a condensação eficiente através de um tubo interno em formato espiral.'),
('Condensador de tubo reto (Liebig)', 'Vidraria', 'Condensador padrão de fluxo reto para destilações simples.'),
('Condensador para Soxhlet', 'Vidraria', 'Condensador específico para acoplamento em montagens de extração contínua.'),
('Cubeta para espectrofotômetro', 'Acessório', 'Pequeno tubo de quartzo, vidro ou plástico para análise de absorção de luz.'),
('Dessecador', 'Vidraria', 'Recipiente fechado com agente dessecante para armazenar substâncias sob baixa humidade.'),
('Erlenmeyer', 'Vidraria', 'Formato cônico ideal para agitação em titulações e aquecimento sem risco de projeções.'),
('Espátula de metal', 'Utensílio', 'Usada para a transferência segura de sólidos granulados ou em pó.'),
('Espátula plástica', 'Utensílio', 'Usada para a transferência de sólidos químicos que possam reagir com o metal.'),
('Estante para tubos de ensaio', 'Suporte', 'Estrutura usada para manter os tubos de ensaio organizados na vertical.'),
('Estufa', 'Equipamento', 'Usada para a secagem de vidrarias, esterilização de materiais ou aquecimento prolongado.'),
('Extrator para Soxhlet', 'Conexão', 'Peça de vidro intermediária usada em extrações contínuas de sólidos por solvente.'),
('Forno Mufla', 'Equipamento', 'Alcança altíssimas temperaturas, essencial para a calcinação de amostras e análises térmicas.'),
('Funil comum', 'Vidraria/Plástico', 'Usado na transferência de líquidos ou em processos simples de filtração por gravidade.'),
('Funil de Büchner', 'Porcelana', 'Utilizado em conjunto com o Kitassato para a realização de filtrações a vácuo.'),
('Funil de separação', 'Vidraria', 'Usado para a separação de misturas heterogêneas de líquidos imiscíveis.'),
('Garra Castaloy', 'Suporte', 'Garra metálica articulada e versátil para prender vidrarias diversas na haste.'),
('Garra para condensador', 'Suporte', 'Garra de tamanho específico para fixar condensadores ao suporte universal.'),
('Kitassato', 'Vidraria', 'Erlenmeyer com saída lateral, acoplado a uma trompa ou bomba de vácuo para filtrações.'),
('Lâmina para microscopia', 'Vidraria', 'Placa de vidro retangular que serve de suporte para amostras biológicas.'),
('Lamínula para microscopia', 'Vidraria', 'Pequena e fina película de vidro para cobrir a amostra disposta na lâmina.'),
('Mangueira de silicone', 'Acessório', 'Condução de água de refrigeração para condensadores ou conexões de vácuo.'),
('Manta de aquecimento', 'Equipamento', 'Equipamento elétrico que envolve perfeitamente balões de fundo redondo para aquecimento.'),
('Mufa', 'Suporte', 'Peça metálica de fixação dupla para prender as garras na haste do suporte universal.'),
('Papel de filtro', 'Acessório', 'Meio poroso de celulose usado para reter sólidos em processos de filtração.'),
('Pera de sucção', 'Acessório', 'Bulbo de borracha com válvulas acoplado a pipetas para aspirar líquidos com segurança.'),
('Pesa-filtro', 'Vidraria', 'Pequeno recipiente cilíndrico com tampa esmerilhada para pesagem de substâncias higroscópicas.'),
('Picnômetro', 'Vidraria', 'Instrumento de vidro calibrado de alta precisão usado para determinar a densidade de líquidos.'),
('Pipeta de Pasteur', 'Vidraria/Plástico', 'Usada para transferências rápidas de pequenas gotas de líquido, sem função de precisão.'),
('Pipeta graduada', 'Vidraria', 'Permite a medição e transferência de volumes variáveis e fracionados de líquido.'),
('Pipeta volumétrica', 'Vidraria', 'Mede e transfere um único volume fixo com altíssimo grau de precisão analítica.'),
('Pipeta Automática (micropipeta)', 'Equipamento', 'Equipamento mecânico de alta precisão para medição de volumes microscópicos em microlitros.'),
('Pipetador tipo Pump', 'Acessório', 'Dispositivo mecânico de roldana acoplado para aspiração precisa em pipetas graduadas e volumétricas.'),
('Pisseta', 'Plástico', 'Frasco plástico lavador compressível, preenchido com água destilada ou álcool.'),
('Placa aquecedora/agitadora', 'Equipamento', 'Superfície aquecida eletricamente que também possui um sistema de agitação magnética integrado.'),
('Placa de Petri', 'Vidraria/Plástico', 'Usada para o cultivo de microrganismos e observações em meios de cultura.'),
('Proveta', 'Vidraria/Plástico', 'Tubo cilíndrico vertical graduado para medições de volumes de líquidos com precisão média.'),
('Rolhas de borracha', 'Acessório', 'Usadas para a vedação hermética de balões, tubos de ensaio e frascos de reagentes.'),
('Suporte universal', 'Suporte', 'Haste metálica vertical sobre uma base pesada que serve de base estrutural para montagens.'),
('Tela de amianto', 'Acessório', 'Malha metálica com centro refratário para distribuir uniformemente o calor gerado pelo Bico de Bunsen.'),
('Termômetro', 'Acessório', 'Instrumento para medição da temperatura de reações.'),
('Triângulo de Porcelana', 'Suporte', 'Usado para apoiar cadinhos de porcelana diretamente sobre o tripé durante o aquecimento.'),
('Tripé de ferro/aço', 'Suporte', 'Suporte de três pernas usado para apoiar a tela de amianto acima do bico.'),
('Trompa de vácuo', 'Acessório', 'Dispositivo conectado a uma torneira hidráulica para gerar vácuo utilizando o efeito Venturi.'),
('Tubo conectante 3 vias', 'Conexão', 'Conector de vidro angular para interligar simultaneamente o balão, o termômetro e o condensador.'),
('Tubo de ensaio', 'Vidraria', 'Usado para a realização de testes e reações químicas rápidas em pequena escala.'),
('Tubo tipo Falcon', 'Plástico', 'Tubo plástico cônico graduado com tampa de rosca, muito utilizado em centrifugações.'),
('Vidro de relógio', 'Vidraria', 'Usado para pegar pequenas quantidades de sólidos estáveis ou cobrir béqueres.'),
-- Sistemas e Montagens Padrão
('Filtração Simples', 'Sistema', 'Montagem padrão para a separação de misturas sólido-líquido heterogêneas utilizando a gravidade.'),
('Filtração a Vácuo', 'Sistema', 'Montagem acelerada de separação sólido-líquido usando Kitassato, funil de Büchner e vácuo.'),
('Destilação Simples', 'Sistema', 'Montagem para a separação de um líquido volátil de um sólido dissolvido ou de líquidos com PE distantes.'),
('Destilação Fracionada', 'Sistema', 'Montagem equipada com coluna de fracionamento para separar líquidos com PE próximos.'),
('Refluxo', 'Sistema', 'Montagem vertical que permite aquecer uma reação sem perder os solventes por evaporação.'),
('Extração Soxhlet', 'Sistema', 'Montagem para a extração contínua e automatizada de compostos solúveis a partir de uma matriz.'),
('Destilação por arraste a vapor', 'Sistema', 'Montagem para a extração de óleos essenciais e isolamento de compostos voláteis.'),
('Titulação', 'Sistema', 'Montagem de análise volumétrica combinando bureta e erlenmeyer para determinação de concentrações.'),
('Extração Líquido-Líquido', 'Sistema', 'Montagem para a partição de solutos entre duas fases líquidas imiscíveis de densidades diferentes.');

-- 7. CONFERIR DADOS SALVOS
SELECT * FROM materiais;