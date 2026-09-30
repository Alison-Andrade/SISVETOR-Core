ALTER TABLE users
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ATIVO';

-- Cadastros antigos de agentes de campo com papel privilegiado foram possíveis
-- pela rota pública. Eles voltam a depender de aprovação da coordenação.
UPDATE users u
SET role = 'ROLE_CAMPO', status = 'PENDENTE'
FROM agentes a
WHERE u.agente_id = a.id
  AND a.funcao = 'CAMPO'
  AND u.role <> 'ROLE_CAMPO';

UPDATE users
SET role = 'ROLE_CAMPO', status = 'PENDENTE'
WHERE role NOT IN ('ROLE_CAMPO', 'ROLE_SUPERVISOR', 'ROLE_COORDENADOR', 'ROLE_ADMIN');

ALTER TABLE users
    ADD CONSTRAINT uk_users_agente UNIQUE (agente_id),
    ADD CONSTRAINT chk_users_role CHECK (role IN ('ROLE_CAMPO', 'ROLE_SUPERVISOR', 'ROLE_COORDENADOR', 'ROLE_ADMIN')),
    ADD CONSTRAINT chk_users_status CHECK (status IN ('PENDENTE', 'ATIVO', 'BLOQUEADO'));
