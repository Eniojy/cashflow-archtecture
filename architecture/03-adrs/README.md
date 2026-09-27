# Índice de Architecture Decision Records

Os ADRs abaixo registram as decisões adotadas para a arquitetura de fluxo de caixa, incluindo seus
trade-offs, benefícios, custos e riscos. Cada ADR aponta os RNFs atendidos, explica sua contribuição
e identifica quem implementa a garantia na solução. Alterações relevantes devem gerar um novo ADR;
decisões antigas não devem ser apagadas, mas marcadas como Deprecadas ou Substituídas.

| ADR | Decisão | RNFs atendidos | Status |
|---|---|---|---|
| [ADR 001](ADR-001-microsservicos.md) | Escolha de uma arquitetura de microsserviços pragmática | [001](../01-requirements/non-functional/RNF-001-independencia-de-lancamentos.md), [002](../01-requirements/non-functional/RNF-002-desempenho-do-consolidado.md) | Aceita |
| [ADR 002](ADR-002-integracao-assincrona-eventos.md) | Integração assíncrona orientada a eventos | [001](../01-requirements/non-functional/RNF-001-independencia-de-lancamentos.md), [004](../01-requirements/non-functional/RNF-004-consistencia-do-consolidado.md), [007](../01-requirements/non-functional/RNF-007-observabilidade-e-operacao.md) | Aceita |
| [ADR 003](ADR-003-transactional-outbox.md) | Publicação confiável com Transactional Outbox | [001](../01-requirements/non-functional/RNF-001-independencia-de-lancamentos.md), [003](../01-requirements/non-functional/RNF-003-durabilidade-e-integridade.md), [005](../01-requirements/non-functional/RNF-005-recuperacao.md), [007](../01-requirements/non-functional/RNF-007-observabilidade-e-operacao.md) | Aceita |
| [ADR 004](ADR-004-cqrs-mediatr-sem-event-sourcing.md) | CQRS leve e MediatR sem Event Sourcing | [006](../01-requirements/non-functional/RNF-006-seguranca.md), [007](../01-requirements/non-functional/RNF-007-observabilidade-e-operacao.md) | Aceita |
| [ADR 005](ADR-005-stack-dotnet-postgresql-mongodb-gcp.md) | Stack .NET, PostgreSQL, MongoDB e serviços gerenciados | [001](../01-requirements/non-functional/RNF-001-independencia-de-lancamentos.md), [002](../01-requirements/non-functional/RNF-002-desempenho-do-consolidado.md), [003](../01-requirements/non-functional/RNF-003-durabilidade-e-integridade.md), [005](../01-requirements/non-functional/RNF-005-recuperacao.md), [006](../01-requirements/non-functional/RNF-006-seguranca.md) | Aceita |
| [ADR 006](ADR-006-mongodb-sem-cache.md) | MongoDB como read model sem cache distribuído | [002](../01-requirements/non-functional/RNF-002-desempenho-do-consolidado.md), [004](../01-requirements/non-functional/RNF-004-consistencia-do-consolidado.md), [007](../01-requirements/non-functional/RNF-007-observabilidade-e-operacao.md) | Aceita |
| [ADR 007](ADR-007-idempotencia-ponta-a-ponta.md) | Idempotência na API e no consumidor de eventos | [003](../01-requirements/non-functional/RNF-003-durabilidade-e-integridade.md), [004](../01-requirements/non-functional/RNF-004-consistencia-do-consolidado.md), [005](../01-requirements/non-functional/RNF-005-recuperacao.md), [007](../01-requirements/non-functional/RNF-007-observabilidade-e-operacao.md) | Aceita |

[Consultar o catálogo de RNFs](../01-requirements/non-functional/README.md)

[Voltar à documentação de arquitetura](../README.md)
