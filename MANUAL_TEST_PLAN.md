# Plano de testes manuais

Este roteiro valida a API do desafio do zero usando Swagger em `http://localhost:8081/swagger-ui/index.html`.

## Prepara&ccedil;&atilde;o

1. Inicie a infraestrutura com `docker compose --env-file .env.example up -d`.
2. Inicie a API com `mvnw.cmd spring-boot:run`.
3. Confirme Flyway V1 a V5 e a porta 8081 no log.
4. Execute `POST /api/v1/auth/login` com `portfolio.admin` / `password`.
5. Copie `accessToken`; em **Authorize**, informe `Bearer <accessToken>`.

Esperado: login `200`; rota de neg&oacute;cio sem token retorna `401` e com token &eacute; aceita.

## Membros externos

### Evid&ecirc;ncia de execu&ccedil;&atilde;o

Em 2026-10-02, a associa&ccedil;&atilde;o positiva de membros foi validada manualmente. O projeto `691ef0dc-6edf-47e1-b9b9-67cff3dbf453` foi criado com `employee-001` como gerente e com `employee-001` e `employee-002` como membros. A resposta `201 Created` retornou ambos com atribui&ccedil;&atilde;o `funcion&aacute;rio`, preservou o gerente dentro da lista e calculou risco `LOW`.

O mock fornece `employee-001` (funcion&aacute;rio) e `consultant-001` (consultor).

Para validar um membro novo, crie-o diretamente em `POST http://localhost:8083/api/v1/members`, consulte o `id` retornado em `GET http://localhost:8083/api/v1/members/{id}` e use esse mesmo identificador na criação de um projeto. Um membro com atribuição `funcionário` deve ser aceito; `consultor` deve retornar `422`.

| Caso | A&ccedil;&atilde;o | Esperado |
|---|---|---|
| Funcion&aacute;rio | Usar `employee-001` como gerente e membro | `201` |
| Consultor | Usar `consultant-001` como gerente ou membro | `422` |
| Gerente fora da lista | Gerente n&atilde;o alocado | `422` |

## Cria&ccedil;&atilde;o e persist&ecirc;ncia

Em `POST /api/v1/projects`, envie e guarde o `id` retornado:

```json
{
  "name": "Projeto manual A",
  "startDate": "2026-10-01",
  "expectedEndDate": "2027-01-15",
  "actualEndDate": null,
  "totalBudget": 250000.00,
  "description": "Valida&ccedil;&atilde;o manual",
  "managerExternalId": "employee-001",
  "status": "ANALYSIS",
  "memberExternalIds": ["employee-001"]
}
```

| Caso | Altera&ccedil;&atilde;o | Esperado |
|---|---|---|
| Consulta | `GET /api/v1/projects/{id}` com ID real do `201` | `200`, gerente, membro e risco `MEDIUM` |
| Status inicial | Criar com `PLANNED` | `422` |
| Or&ccedil;amento | Enviar `0` | `400` |
| Datas | T&eacute;rmino previsto antes do in&iacute;cio | `422` |
| Duplicidade | Repetir membro | `422` |
| Faixa | Lista vazia ou acima de 10 membros | `400` ou `422` |

## Risco, filtros e pagina&ccedil;&atilde;o

| Risco | Condi&ccedil;&atilde;o | Esperado |
|---|---|---|
| LOW | `100000.00` e at&eacute; 3 meses | `LOW` |
| MEDIUM | `500000.00` e at&eacute; 6 meses | `MEDIUM` |
| HIGH | `500000.01` ou acima de 6 meses | `HIGH` |

Em `GET /api/v1/projects`, valide `name`, `status`, `managerId`, `startDateFrom`, `startDateTo`, `risk`, `page` e `size`. Datas invertidas devem retornar `422`.

## Atualiza&ccedil;&atilde;o, status e exclus&atilde;o

1. Use `PUT /api/v1/projects/{id}` com os campos de cria&ccedil;&atilde;o, sem `status`; confirme via `GET` que o status foi preservado.
2. Em `PATCH /api/v1/projects/{id}/status`, execute:

```text
ANALYSIS -> ANALYSIS_COMPLETED -> ANALYSIS_APPROVED -> STARTED -> PLANNED -> IN_PROGRESS -> CLOSED
```

3. Para `CLOSED`, envie `actualEndDate` igual ou posterior ao in&iacute;cio.

| Caso | Esperado |
|---|---|
| Salto ou retrocesso | `422` |
| CLOSED sem data real ou data anterior | `422` |
| DELETE em ANALYSIS, PLANNED ou CANCELED | `204` |
| DELETE em STARTED, IN_PROGRESS ou CLOSED | `422` |
| CANCELED em qualquer estado, inclusive CLOSED | aceito; depois pode excluir |
| Transi&ccedil;&atilde;o ap&oacute;s CANCELED | `422` |

## Limites, relat&oacute;rio e auditoria

### Evid&ecirc;ncia de execu&ccedil;&atilde;o

Em 2026-10-02, o cen&aacute;rio do limite de aloca&ccedil;&atilde;o foi validado manualmente com `employee-001`: havia tr&ecirc;s projetos no status `ANALYSIS` e um no status `CLOSED`. A tentativa seguinte de criar um quarto projeto ativo com esse membro foi rejeitada com `422`, como previsto. O projeto fechado n&atilde;o foi contabilizado como ativo.

Em 2026-10-02, a regra de exclus&atilde;o tamb&eacute;m foi validada manualmente. `DELETE /api/v1/projects/d7e8f480-aed7-4758-8d01-f27b86bb030e`, em `ANALYSIS`, retornou `204 No Content` e removeu o projeto. A exclus&atilde;o de `fcc5987e-9bcf-490c-a6fc-3f8c7d9fd94d`, em `CLOSED`, foi bloqueada com `422`, como exige a regra de neg&oacute;cio.

1. Crie tr&ecirc;s projetos ativos com `employee-001`; o quarto deve retornar `422`. Feche, cancele ou exclua um e confirme que uma nova aloca&ccedil;&atilde;o &eacute; aceita.
2. Em `GET /api/v1/reports/portfolio-summary`, valide contagem/or&ccedil;amento por status, dura&ccedil;&atilde;o m&eacute;dia dos fechados e membros distintos.
3. Confira auditoria no PostgreSQL:

```sql
SELECT event_type, actor, details, occurred_at
FROM portfolio.project_audit_events
WHERE project_id = '<ID_DO_PROJETO>'
ORDER BY occurred_at;
```

Esperado: eventos `CREATED`, `UPDATED`, `MEMBERS_CHANGED`, `STATUS_CHANGED` e `DELETED`; ap&oacute;s excluir o projeto, os eventos permanecem.

## Registro

### Resultado final

Em 2026-10-02, todas as provas manuais deste roteiro foram conclu&iacute;das com sucesso. Foram validados autentica&ccedil;&atilde;o JWT, CRUD, persist&ecirc;ncia, filtros, pagina&ccedil;&atilde;o, riscos, membros externos, limites de aloca&ccedil;&atilde;o, fluxo de status, exclus&atilde;o, relat&oacute;rio e auditoria. Tamb&eacute;m foi confirmado que `actualEndDate` n&atilde;o &eacute; preenchida automaticamente e &eacute; obrigat&oacute;ria, com data v&aacute;lida, somente na transi&ccedil;&atilde;o para `CLOSED`.

Para cada caso, registre data, endpoint, corpo resumido, status HTTP e resultado. Ao terminar, opcionalmente execute `docker compose --env-file .env.example down`.
