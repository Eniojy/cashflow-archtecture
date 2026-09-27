# RNF 004 - Consistência e atualidade do Consolidado

**Origem:** derivado da integração assíncrona escolhida  
**Owner principal:** Time de Consolidado  
**Status:** Atendido no desenho; 

## Requisito

O registro do lançamento terá consistência forte local. O saldo será eventualmente consistente e,
em operação normal, o intervalo entre a confirmação do lançamento e a atualização do read model
terá p95 abaixo de 5 segundos.

## Critérios de aceite

- Medir o lag usando timestamps correlacionados do lançamento, evento e atualização do saldo.
- Manter o p95 do lag abaixo de 5 segundos durante carga normal e alertar sobre violação do SLO.
- Expor `dataHoraUltimaAtualizacao` para que o consumidor reconheça a atualidade da resposta.
- A API deve consultar diretamente o documento confirmado no MongoDB, sem defasagem adicional de cache.
- Após reprocessamento ou reentrega, o saldo deve convergir sem duplicação.

## Quem resolve

| Responsável na solução | Como atende |
|---|---|
| Pub/Sub | Entrega os eventos e expõe backlog e idade da assinatura. |
| Consolidado Worker | Processa eventos, atualiza o read model e confirma somente após o commit. |
| MongoDB do Consolidado | Mantém `daily_balances` e `processed_events` como fonte persistente. |
| Consolidado API | Consulta o MongoDB diretamente e informa a última atualização. |
| Plataforma/SRE | Mede o lag e aciona alertas quando o SLO é violado. |

## ADRs relacionadas

- [ADR 002 - Integração assíncrona](../../03-adrs/ADR-002-integracao-assincrona-eventos.md)
- [ADR 006 - MongoDB sem cache distribuído](../../03-adrs/ADR-006-mongodb-sem-cache.md)
- [ADR 007 - Idempotência ponta a ponta](../../03-adrs/ADR-007-idempotencia-ponta-a-ponta.md)

[Voltar ao catálogo de RNFs](README.md)
