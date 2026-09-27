# RNF 005 - Recuperação de falhas e desastres

**Origem:** derivado da meta de recuperação declarada pela arquitetura  
**Owner principal:** Plataforma/SRE e DBA  
**Status:** Atendido no desenho; pendente de exercício de recuperação

## Requisito

A solução deverá restaurar o serviço essencial em até 30 minutos. Backups e restauração para um
instante anterior à falha serão testados trimestralmente. Mensagens e pendências devem permanecer
disponíveis durante a janela definida para recuperação e reprocessamento.

## Critérios de aceite

- Executar trimestralmente a restauração dos bancos em ambiente isolado e registrar tempo e ponto
  recuperado.
- Validar a retomada de pendências da Outbox, backlog do Pub/Sub e mensagens da DLQ.
- Manter runbooks para indisponibilidade do Cloud SQL, MongoDB, Pub/Sub, Worker e APIs.
- Confirmar que o serviço essencial é restaurado em até 30 minutos no cenário coberto.
- Tratar retenção como janela disponível para reprocessamento; ela não determina sozinha o tempo de
  recuperação.

## Quem resolve

| Responsável na solução | Como atende |
|---|---|
| Cloud SQL HA e MongoDB Atlas | Fornecem réplica, backup e restauração das fontes persistentes para um instante específico. |
| Outbox | Preserva intenções ainda não publicadas durante falhas do broker. |
| Pub/Sub e DLQ | Retêm mensagens não processadas para retry ou replay controlado. |
| Plataforma/SRE e DBA | Mantêm alertas, runbooks, backups e exercícios de recuperação. |
| Times de Lançamentos e Consolidado | Validam reconciliação e reprocessamento idempotente. |

## ADRs relacionadas

- [ADR 003 - Transactional Outbox](../../03-adrs/ADR-003-transactional-outbox.md)
- [ADR 005 - Stack .NET, PostgreSQL, MongoDB e serviços gerenciados](../../03-adrs/ADR-005-stack-dotnet-postgresql-mongodb-gcp.md)
- [ADR 007 - Idempotência ponta a ponta](../../03-adrs/ADR-007-idempotencia-ponta-a-ponta.md)

[Voltar ao catálogo de RNFs](README.md)
