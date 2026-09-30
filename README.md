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
Swagger será disponibilizada posteriormente junto com os endpoints da API.
O PostgreSQL do contêiner usa a porta 5432 internamente e é exposto na porta
5433 do host por padrão, evitando conflito com instalações locais de PostgreSQL.

## Testes

```bash
./mvnw clean test
```

Os testes de contexto usam o perfil `test`, sem depender de uma instância local
do PostgreSQL. A aplicação em execução usa PostgreSQL obrigatoriamente.
