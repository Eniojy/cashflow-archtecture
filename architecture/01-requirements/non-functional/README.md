# Catálogo de Requisitos Não Funcionais

Este catálogo transforma os atributos de qualidade da solução em requisitos mensuráveis e mostra
quem é responsável por atendê-los. Os RNFs 001 e 002 vêm diretamente do desafio. Os demais foram
derivados da arquitetura para tornar explícitas as garantias necessárias a um fluxo financeiro.

"Owner" indica a responsabilidade organizacional sugerida. "Quem resolve" indica os componentes e
mecanismos técnicos que implementam o requisito. As ADRs explicam por que esses mecanismos foram
escolhidos.

| RNF | Requisito e meta | Owner principal | Quem resolve | ADRs principais |
|---|---|---|---|---|
| [RNF 001](RNF-001-independencia-de-lancamentos.md) | Lançamentos permanece disponível se o Consolidado cair; disponibilidade mensal alvo de 99,9% | Time de Lançamentos | Lançamentos API, PostgreSQL/Outbox e Pub/Sub | [ADR 001](../../03-adrs/ADR-001-microsservicos.md), [ADR 002](../../03-adrs/ADR-002-integracao-assincrona-eventos.md), [ADR 003](../../03-adrs/ADR-003-transactional-outbox.md), [ADR 005](../../03-adrs/ADR-005-stack-dotnet-postgresql-mongodb-gcp.md) |
| [RNF 002](RNF-002-desempenho-do-consolidado.md) | 50 req/s por 15 min, sucesso mínimo de 95% e p95 abaixo de 200 ms | Time de Consolidado | Consolidado API, MongoDB Atlas, índice composto e autoscaling | [ADR 001](../../03-adrs/ADR-001-microsservicos.md), [ADR 005](../../03-adrs/ADR-005-stack-dotnet-postgresql-mongodb-gcp.md), [ADR 006](../../03-adrs/ADR-006-mongodb-sem-cache.md) |
| [RNF 003](RNF-003-durabilidade-e-integridade.md) | Nenhum lançamento confirmado perdido e nenhum efeito financeiro duplicado | Times de Lançamentos e Consolidado | Transações PostgreSQL/MongoDB, Outbox, idempotência e `processed_events` | [ADR 003](../../03-adrs/ADR-003-transactional-outbox.md), [ADR 005](../../03-adrs/ADR-005-stack-dotnet-postgresql-mongodb-gcp.md), [ADR 007](../../03-adrs/ADR-007-idempotencia-ponta-a-ponta.md) |
| [RNF 004](RNF-004-consistencia-do-consolidado.md) | Consistência forte no lançamento e eventual no saldo, com lag p95 abaixo de 5 s em operação normal | Time de Consolidado | Pub/Sub, Consolidado Worker e MongoDB read model | [ADR 002](../../03-adrs/ADR-002-integracao-assincrona-eventos.md), [ADR 006](../../03-adrs/ADR-006-mongodb-sem-cache.md), [ADR 007](../../03-adrs/ADR-007-idempotencia-ponta-a-ponta.md) |
| [RNF 005](RNF-005-recuperacao.md) | Serviço essencial restaurado em até 30 min; backups testados trimestralmente | Plataforma/SRE e DBA | Cloud SQL HA, MongoDB Atlas, retenção do Pub/Sub, DLQ e runbooks | [ADR 003](../../03-adrs/ADR-003-transactional-outbox.md), [ADR 005](../../03-adrs/ADR-005-stack-dotnet-postgresql-mongodb-gcp.md), [ADR 007](../../03-adrs/ADR-007-idempotencia-ponta-a-ponta.md) |
| [RNF 006](RNF-006-seguranca.md) | Identidade, autorização por lojista, proteção de dados e cadeia de entrega segura | Segurança/Identidade e times das APIs | IdP, Gateway, IAM, APIs, Secret Manager e pipeline CI/CD | [ADR 004](../../03-adrs/ADR-004-cqrs-mediatr-sem-event-sourcing.md), [ADR 005](../../03-adrs/ADR-005-stack-dotnet-postgresql-mongodb-gcp.md) |
| [RNF 007](RNF-007-observabilidade-e-operacao.md) | Detectar violações dos SLOs e permitir rastreamento ponta a ponta | Plataforma/SRE com instrumentação dos times | OpenTelemetry, logs, métricas, traces, alertas e runbooks | [ADR 002](../../03-adrs/ADR-002-integracao-assincrona-eventos.md), [ADR 003](../../03-adrs/ADR-003-transactional-outbox.md), [ADR 004](../../03-adrs/ADR-004-cqrs-mediatr-sem-event-sourcing.md), [ADR 006](../../03-adrs/ADR-006-mongodb-sem-cache.md), [ADR 007](../../03-adrs/ADR-007-idempotencia-ponta-a-ponta.md) |

O limite de 5% do RNF 002 representa requisições de consulta que podem falhar durante o teste de
carga. Ele não autoriza perder 5% dos lançamentos financeiros. Todo lançamento confirmado continua
sujeito ao RNF 003.

[Voltar aos requisitos](../README.md)
