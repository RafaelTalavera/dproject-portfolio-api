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

O token retornado deve ser informado como `Bearer token` na autorização dos
endpoints de negócio. A variável `PORTFOLIO_JWT_SECRET` do `.env` assina os
tokens. O valor de exemplo serve apenas para desenvolvimento local e deve ser
substituído em ambientes compartilhados ou de produção.

## API externa mockada de membros

O Docker Compose também inicia uma API externa mockada de membros na porta
`8083` por padrão. Ela representa o sistema que é a fonte de verdade para o
cadastro de membros; a aplicação não disponibilizará CRUD local de membros.

- `GET /api/v1/members/employee-001` retorna um funcionário de demonstração.
- `GET /api/v1/members/consultant-001` retorna um consultor de demonstração.
- `POST /api/v1/members` recebe `name` e `assignment` e retorna um membro
  externo criado.

Exemplo de criação direta no mock:

```bash
curl -X POST http://localhost:8083/api/v1/members \
  -H "Content-Type: application/json" \
  -d '{"name":"Ana Silva","assignment":"funcionário"}'
```

## Testes

```bash
./mvnw clean test
```

Os testes de contexto usam o perfil `test`, sem depender de uma instância local
do PostgreSQL. A aplicação em execução usa PostgreSQL obrigatoriamente.
