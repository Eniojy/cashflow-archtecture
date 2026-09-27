# RNF 007 - Observabilidade e operação

**Origem:** requisito diferencial de monitoramento e observabilidade  
**Owner principal:** Plataforma/SRE, com instrumentação pelos times dos serviços  
**Status:** Atendido no desenho; pendente de implantação e teste dos alertas

## Requisito

A solução deve permitir rastrear uma operação ponta a ponta, medir os SLIs dos RNFs e detectar falhas
antes que backlog, defasagem ou saturação causem impacto relevante ao negócio.

## Critérios de aceite

- Propagar `CorrelationId`, `IdempotencyKey` e `EventId` por logs e traces, sem expor dados sensíveis.
- Medir disponibilidade, taxa de 5xx, p95/p99 de POST e GET e saturação dos recursos.
- Medir quantidade e idade da Outbox, backlog e idade da assinatura, retries, DLQ e lag de
  consolidação.
- Medir latência das consultas, pool de conexões, consumo e saturação do MongoDB.
- Vincular cada alerta acionável a um runbook com diagnóstico, mitigação e escalonamento.
- Testar periodicamente a geração, o roteamento e a utilidade dos alertas.

## Quem resolve

| Responsável na solução | Como atende |
|---|---|
| APIs, Worker e Outbox Publisher | Produzem telemetria OpenTelemetry e propagam correlação. |
| Cloud Monitoring e Logging | Armazenam métricas, logs, traces, dashboards e alertas. |
| Pub/Sub, Cloud Run, Cloud SQL e MongoDB Atlas | Expõem métricas de backlog, saturação, erro e capacidade. |
| Plataforma/SRE | Define SLOs, alertas, runbooks, plantão e resposta a incidentes. |
| Times dos serviços | Mantêm instrumentação e corrigem causas de violações dos SLOs. |

## ADRs relacionadas

- [ADR 002 - Integração assíncrona](../../03-adrs/ADR-002-integracao-assincrona-eventos.md)
- [ADR 003 - Transactional Outbox](../../03-adrs/ADR-003-transactional-outbox.md)
- [ADR 004 - CQRS leve e MediatR](../../03-adrs/ADR-004-cqrs-mediatr-sem-event-sourcing.md)
- [ADR 006 - MongoDB sem cache distribuído](../../03-adrs/ADR-006-mongodb-sem-cache.md)
- [ADR 007 - Idempotência ponta a ponta](../../03-adrs/ADR-007-idempotencia-ponta-a-ponta.md)

[Voltar ao catálogo de RNFs](README.md)
