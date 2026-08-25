CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    password VARCHAR(150) NOT NULL,
    role VARCHAR(20) NOT NULL,
    agente_id BIGINT NOT NULL REFERENCES agentes(id)
);