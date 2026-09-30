# Cadastro e autenticação

## Configuração

Defina `JWT_SECRET` com pelo menos 32 caracteres antes de iniciar a aplicação. O servidor falha ao iniciar se o segredo estiver ausente ou for curto.

Para criar o primeiro coordenador, configure as variáveis abaixo em um banco sem conta de coordenador ou administrador:

```text
BOOTSTRAP_COORDINATOR_ENABLED=true
BOOTSTRAP_COORDINATOR_NOME=...
BOOTSTRAP_COORDINATOR_CPF=...        # 11 dígitos
BOOTSTRAP_COORDINATOR_MATRICULA=...  # 7 caracteres
BOOTSTRAP_COORDINATOR_EMAIL=...
BOOTSTRAP_COORDINATOR_PASSWORD=...   # pelo menos 12 caracteres
```

Após a primeira inicialização, remova `BOOTSTRAP_COORDINATOR_ENABLED` e a senha do ambiente. O bootstrap falha se já existir um coordenador ou administrador.

## Cadastro próprio

`POST /api/v1/auth/register` aceita:

```json
{
  "agente": {
    "nome": "Nome do Agente",
    "cpf": "12345678909",
    "matricula": "1234567",
    "telefone": null,
    "email": "agente@example.com"
  },
  "password": "uma-senha-segura"
}
```

O servidor define `ROLE_CAMPO` e `PENDENTE`. Se o agente já existe, CPF, matrícula e email precisam conferir; a conta continua pendente até ser aprovada. O login de uma conta pendente responde com erro de autenticação.

## Cadastro pela coordenação

Um coordenador cria o agente pelo endpoint de agentes, se necessário, e depois chama `POST /api/v1/usuarios`:

```json
{
  "agenteId": 1,
  "password": "senha-inicial-segura",
  "role": "ROLE_CAMPO"
}
```

O papel deve corresponder à função cadastrada do agente. Coordenadores podem criar contas de campo e supervisor; somente um administrador pode criar outra conta de coordenador. A conta criada por esse fluxo fica ativa. A senha inicial precisa ser entregue ao usuário por um canal confiável; um fluxo de convite ou redefinição de senha ainda não foi implementado.

`GET /api/v1/usuarios/pendentes` lista contas que aguardam aprovação, com paginação. `PUT /api/v1/usuarios/{id}/aprovar` ativa uma conta de campo pendente. `PUT /api/v1/usuarios/{id}/bloquear` bloqueia uma conta e `PUT /api/v1/usuarios/{id}/desbloquear` a reativa. Um coordenador não pode bloquear ou desbloquear coordenadores e administradores. Esses endpoints exigem papel de coordenação ou administração.

O filtro de autenticação consulta o usuário no banco a cada requisição com JWT. Ele usa o papel e o status atuais, então uma conta bloqueada deixa de ter acesso mesmo com token ainda válido.

## Migração de contas anteriores

A migration `V6` adiciona status e unicidade por agente. Contas antigas continuam ativas, exceto contas de agentes de campo que tinham papel privilegiado ou desconhecido: estas passam a `ROLE_CAMPO` e `PENDENTE`, pois o cadastro público anterior permitia informar o papel. Revise contas existentes antes de aplicar a migration em um banco com dados reais.
