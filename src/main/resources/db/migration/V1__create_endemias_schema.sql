-- CREATE TYPE tipo_agente AS ENUM ('CAMPO', 'SUPERVISOR', 'COORDENADOR');
-- CREATE TYPE status_visita AS ENUM ('TRABALHADO', 'RECUPERADO', 'FECHADO', 'RECUSADO');
-- CREATE TYPE tipo_imovel AS ENUM ('RESIDENCIA', 'COMERCIO', 'TERRENO_BALDIO', 'OUTRO');
-- CREATE TYPE tipo_deposito AS ENUM ('A1', 'A2', 'B', 'C', 'D1', 'D2', 'E');
-- CREATE TYPE categoria_localidade AS ENUM ('BR', 'PV', 'ST', 'FZ');
-- CREATE TYPE tipo_localidade AS ENUM ('SEDE', 'OUTRO');

CREATE TABLE agentes (
    id BIGSERIAL PRIMARY KEY,
    cpf CHAR(11) UNIQUE NOT NULL,
    matricula CHAR(7) UNIQUE NOT NULL,
    nome VARCHAR(150) NOT NULL,
    telefone VARCHAR(20),
    email VARCHAR(150) NOT NULL UNIQUE,
    funcao VARCHAR(20) NOT NULL DEFAULT 'CAMPO',
    supervisor_id BIGINT REFERENCES agentes(id) ON DELETE SET NULL
    CHECK (funcao IN ('CAMPO', 'SUPERVISOR', 'COORDENADOR'))
);

CREATE TABLE localidades (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    nome VARCHAR(50) NOT NULL,
    categoria VARCHAR(20) NOT NULL,
    tipo VARCHAR(20) NOT NULL
    CHECK (categoria IN ('BAIRRO', 'POVOADO', 'SITIO', 'FAZENDA')),
    CHECK (tipo IN ('SEDE', 'OUTRO'))
);

CREATE TABLE areas (
    id BIGSERIAL PRIMARY KEY,
    num_area VARCHAR(20) UNIQUE NOT NULL,
    agente_id BIGINT REFERENCES agentes(id)
);

CREATE TABLE quarteiroes (
    id BIGSERIAL PRIMARY KEY,
    numero INTEGER NOT NULL,
    sequencia INTEGER DEFAULT 0,
    localidade_id BIGINT REFERENCES localidades(id) NOT NULL,
    area_id BIGINT REFERENCES areas(id) ON DELETE SET NULL,
    UNIQUE(numero, sequencia, localidade_id)
);

CREATE TABLE lados (
    id BIGSERIAL PRIMARY KEY,
    numero INTEGER NOT NULL,
    logradouro VARCHAR(150) NOT NULL,
    quarteirao_id BIGINT NOT NULL REFERENCES quarteiroes(id),
    UNIQUE(quarteirao_id, numero)
);

CREATE TABLE imoveis (
    id BIGSERIAL PRIMARY KEY,
    placa VARCHAR(10),
    numero_sms INTEGER,
    sequencia INTEGER,
    num_residentes INTEGER DEFAULT 0,
    num_caes INTEGER DEFAULT 0,
    num_gatos INTEGER DEFAULT 0,
    tipo VARCHAR(20) NOT NULL,
    lado_id BIGINT REFERENCES lados(id),
    localidade_id BIGINT REFERENCES localidades(id),

    CONSTRAINT chk_placa_ou_sms CHECK (placa IS NOT NULL OR numero_sms IS NOT NULL),
    CONSTRAINT chk_lado_ou_localidade CHECK (
        (lado_id IS NOT NULL AND localidade_id IS NULL) OR
        (lado_id IS NULL AND localidade_id IS NOT NULL)
    ),
    CHECK (tipo IN ('RESIDENCIA', 'COMERCIO', 'TERRENO_BALDIO', 'OUTRO'))
);

CREATE TABLE ciclos (
    id BIGSERIAL PRIMARY KEY,
    numero_ciclo INTEGER NOT NULL,
    ano INTEGER NOT NULL,
    UNIQUE(numero_ciclo, ano)
);

CREATE TABLE visitas (
    id BIGSERIAL PRIMARY KEY,
    data_visita TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observacao TEXT,
    imovel_id BIGINT NOT NULL REFERENCES imoveis(id),
    agente_id BIGINT NOT NULL REFERENCES agentes(id),
    ciclo_id BIGINT NOT NULL REFERENCES ciclos(id)
);

CREATE TABLE tratamentos (
    visita_id BIGINT PRIMARY KEY REFERENCES visitas(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL,
    qntd_eliminados INTEGER DEFAULT 0,
    qntd_tratados INTEGER DEFAULT 0,
    qntd_larvicida NUMERIC(10,2),
    tipo_larvicida VARCHAR(50),
    focal BOOLEAN DEFAULT FALSE,
    CHECK (status IN ('TRABALHADO', 'RECUPERADO', 'FECHADO', 'RECUSADO'))
);

-- LIRAa: Dados específicos do Levantamento de Índice Rápido
CREATE TABLE liraas (
    visita_id BIGINT PRIMARY KEY REFERENCES visitas(id) ON DELETE CASCADE
);

-- Foco: Amostras coletadas (Relacionado ao LIRAa ou visitas)
CREATE TABLE focos (
    id BIGSERIAL PRIMARY KEY,
    visita_id BIGINT NOT NULL REFERENCES visitas(id) ON DELETE CASCADE,
    tipo_deposito VARCHAR(20) NOT NULL,
    numero_tubito VARCHAR(50),
    resultado_laboratorio VARCHAR(100)
    CHECK (tipo_deposito IN ('A1', 'A2', 'B', 'C', 'D1', 'D2', 'E'))
);

CREATE INDEX idx_imovel_lado ON imoveis(lado_id);
CREATE INDEX idx_visita_ciclo ON visitas(ciclo_id);
CREATE INDEX idx_visita_imovel ON visitas(imovel_id);
CREATE INDEX idx_visita_agente ON visitas(agente_id);


-- Em vez de salvar o número de imóveis na tabela Quarteirão (que pode desatualizar),
-- usamos esta View para ter o dado sempre correto:
CREATE VIEW vw_resumo_quarteirao AS
SELECT q.id, q.numero, COUNT(i.id) as total_imoveis
FROM quarteiroes q
LEFT JOIN lados l ON l.quarteirao_id = q.id
LEFT JOIN imoveis i ON i.lado_id = l.id
GROUP BY q.id, q.numero;