# API de Portfólio de Projetos

API REST para gestão de um portfólio de projetos, desenvolvida com Spring Boot,
PostgreSQL, Flyway e Swagger/OpenAPI.

## Pré-requisitos

- Java 17;
- Docker e Docker Compose.

## Execução local

1. Crie o arquivo `.env` a partir de `.env.example` e ajuste os valores locais
   quando necessário.
2. Inicie o PostgreSQL:

   ```bash
   docker compose up -d
   ```

3. Execute a aplicação:

   ```bash
   ./mvnw spring-boot:run
   ```

No Windows, use `mvnw.cmd spring-boot:run`.

As migrações Flyway são executadas automaticamente na inicialização. A interface
Swagger está disponível em `http://localhost:8081/swagger-ui/index.html`.
O PostgreSQL do contêiner usa a porta 5432 internamente e é exposto na porta
5433 do host por padrão, evitando conflito com instalações locais de PostgreSQL.

## Autenticação

O Swagger está disponível em `http://localhost:8081/swagger-ui/index.html`.
Use o endpoint `POST /api/v1/auth/login` com as credenciais de demonstração:

- Usuário: `portfolio.admin`
- Senha: `password`

No Swagger, informe somente o valor de `accessToken` em **Authorize**; o esquema
Bearer acrescenta o prefixo automaticamente. A variável `PORTFOLIO_JWT_SECRET` do `.env` assina os
tokens. O valor de exemplo serve apenas para desenvolvimento local e deve ser
substituído em ambientes compartilhados ou de produção.

## Teste manual pelo Swagger

1. Acesse `http://localhost:8081/swagger-ui/index.html` e execute `POST /api/v1/auth/login` com `portfolio.admin` e `password`.
2. Copie o campo `accessToken`. Em **Authorize**, informe somente esse token para autenticar os endpoints de neg&oacute;cio.
3. Crie um projeto em `POST /api/v1/projects` usando `employee-001` como `managerExternalId` e como &uacute;nico item de `memberExternalIds`.
4. Copie o `id` retornado pelo `201 Created` e informe esse valor em `GET /api/v1/projects/{id}`. O UUID exibido inicialmente no Swagger &eacute; apenas um exemplo e deve ser substitu&iacute;do pelo identificador real retornado pela cria&ccedil;&atilde;o.
5. Valide a altera&ccedil;&atilde;o via `PUT /api/v1/projects/{id}`, as transi&ccedil;&otilde;es via `PATCH /api/v1/projects/{id}/status`, a exclus&atilde;o via `DELETE /api/v1/projects/{id}` e o resumo em `GET /api/v1/reports/portfolio-summary`.

## API externa mockada de membros

O Docker Compose também inicia uma API externa mockada de membros na porta
`8083` por padrão. Ela representa o sistema que é a fonte de verdade para o
cadastro de membros; a aplicação não disponibilizará CRUD local de membros.

- `GET /api/v1/members/employee-001` retorna um funcionário de demonstração.
- `GET /api/v1/members/consultant-001` retorna um consultor de demonstração.
- `POST /api/v1/members` recebe `name` e `assignment` e retorna um membro
  externo criado. Os membros criados retornam um identificador consultável por
  `GET /api/v1/members/{id}`, para que possam ser associados a projetos.

Exemplo de criação direta no mock:

```bash
curl -X POST http://localhost:8083/api/v1/members \
  -H "Content-Type: application/json" \
  -d '{"name":"Ana Silva","assignment":"funcionário"}'
```

Use o `id` retornado na consulta `GET /api/v1/members/{id}` e, para um
funcionário, nos campos `managerExternalId` e `memberExternalIds` do projeto.

## Testes

Na valida&ccedil;&atilde;o de encerramento realizada em 2026-09-30, a suite passou e o relat&oacute;rio JaCoCo registrou 78,40% de cobertura de instru&ccedil;&otilde;es, acima da meta de 70% para as regras de neg&oacute;cio do desafio.

```bash
./mvnw clean test
```

Os testes de contexto usam o perfil `test`, sem depender de uma instância local
do PostgreSQL. A aplicação em execução usa PostgreSQL obrigatoriamente.
