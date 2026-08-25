ALTER TABLE imoveis
ADD COLUMN ordem INTEGER NOT NULL UNIQUE;

CREATE INDEX idx_imoveis_ordem ON imoveis(ordem);