ALTER TABLE imoveis DROP CONSTRAINT imoveis_ordem_key;

ALTER TABLE imoveis
    ADD CONSTRAINT uq_imoveis_lado_ordem UNIQUE (lado_id, ordem) DEFERRABLE INITIALLY DEFERRED,
    ADD CONSTRAINT uq_imoveis_localidade_ordem UNIQUE (localidade_id, ordem) DEFERRABLE INITIALLY DEFERRED;
