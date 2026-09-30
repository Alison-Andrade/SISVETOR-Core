# Cadastro e autenticação

## Configuração

Defina `JWT_SECRET` com pelo menos 32 caracteres antes de iniciar a aplicação. O servidor falha ao iniciar se o segredo estiver ausente ou for curto.

Para o frontend web, defina `AUTH_WEB_ALLOWED_ORIGINS` com as origens exatas, separadas por vírgula (por exemplo, `https://app.exemplo.gov.br`). A lista vazia permite somente requisições de mesma origem. Em produção, use HTTPS e mantenha `AUTH_COOKIE_SECURE=true`, que é o padrão. Para desenvolvimento local em HTTP, configure `AUTH_COOKIE_SECURE=false` e inclua a origem local, como `http://localhost:5173`. Use o mesmo hostname no navegador para frontend e API (`localhost` em ambos), pois `localhost` e `127.0.0.1` são sites diferentes para cookies `SameSite`.

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

## Login Android

`POST /api/v1/auth/login` recebe `{"username":"CPF, email ou matrícula","password":"..."}` e retorna `token`, `tokenType` (`Bearer`), `refreshToken` e `expiresIn` (900 segundos). Envie o access token em `Authorization: Bearer <token>` e guarde o refresh token no armazenamento seguro do dispositivo.

Antes da expiração do access token, chame `POST /api/v1/auth/refresh` com `{"refreshToken":"..."}`. A resposta tem o mesmo formato do login. Substitua **ambos** os tokens: o refresh token anterior não poderá ser usado de novo. `POST /api/v1/auth/logout` recebe o mesmo corpo e responde `204`; o access token dessa sessão deixa de funcionar imediatamente. Cada login cria uma sessão independente.

## Login web

O frontend deve usar `credentials: "include"` nas requisições à API. Mesmo em subdomínios diferentes, os cookies permanecem restritos ao host da API. O fluxo é:

1. Chamar `GET /api/v1/auth/web/csrf`; a resposta JSON contém `token` e `headerName` (`X-XSRF-TOKEN`).
2. Chamar `POST /api/v1/auth/web/login` com o mesmo corpo do login Android, `credentials: "include"` e o header CSRF retornado. A resposta é `204` e define cookies de acesso e renovação `HttpOnly`; não há token no corpo.
3. Chamar `GET /api/v1/auth/me` para obter os dados da conta. Em operações `POST`, `PUT`, `PATCH` e `DELETE` feitas com cookies, enviar o header CSRF.
4. Quando o acesso expirar, chamar `POST /api/v1/auth/web/refresh` com cookies e CSRF. A resposta é `204` e substitui os dois cookies. Serializar a renovação entre abas para que duas chamadas não reutilizem o mesmo refresh token.
5. Chamar `POST /api/v1/auth/web/logout` com cookies e CSRF. A resposta é `204`, revoga a sessão e remove os cookies.

Obtenha um novo token em `/api/v1/auth/web/csrf` **depois de login, refresh e logout** antes da próxima operação que altera dados. O cookie CSRF também é `HttpOnly`; o frontend usa o valor recebido no JSON, sem tentar lê-lo pelo JavaScript. Os cookies de autenticação usam `SameSite=Lax`, `Path=/`, não definem `Domain` e usam `Secure` em produção.

O access token dura 15 minutos e a sessão renovável dura no máximo 30 dias desde o login. Somente hashes SHA-256 dos refresh tokens são armazenados no banco; cada renovação rotaciona o token. A reutilização de um token consumido revoga a sessão. Requisições com `Authorization` usam somente o Bearer informado, mesmo que cookies estejam presentes. JWTs emitidos antes desta mudança são aceitos até a expiração natural de 24 horas; não podem ser renovados.

## Migração de contas anteriores

A migration `V6` adiciona status e unicidade por agente. Contas antigas continuam ativas, exceto contas de agentes de campo que tinham papel privilegiado ou desconhecido: estas passam a `ROLE_CAMPO` e `PENDENTE`, pois o cadastro público anterior permitia informar o papel. Revise contas existentes antes de aplicar a migration em um banco com dados reais. A migration `V8` cria as tabelas de sessões e histórico de refresh tokens.
