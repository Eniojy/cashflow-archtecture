# RF 003 - Atualizar consolidado diário

**Status:** Aceito  
**Origem:** Comportamento derivado do requisito de consolidado diário  
**Owner principal:** Time de Consolidado

## Objetivo

Incorporar cada lançamento confirmado ao consolidado do respectivo comerciante e dia, sem duplicar
o efeito financeiro quando uma mensagem for entregue novamente.

## Ator

- Sistema de mensageria, entregando um evento de lançamento confirmado.

## Entrada

Evento versionado `LancamentoRegistrado.v1`, contendo ao menos:

- `eventId` e `lancamentoId`;
- `comercianteId`;
- `tipo` (`CREDITO` ou `DEBITO`);
- `valor` e `dataHoraOcorrencia`;
- versão e data/hora de publicação do evento.

## Fluxo principal

1. O Pub/Sub entrega o evento ao Consolidado Worker.
2. O Worker valida o contrato e verifica o `eventId`.
3. Em uma transação MongoDB, o Worker registra o evento processado e faz `upsert` do documento
   diário identificado por `comercianteId + data`.
4. O Worker atualiza `totalCreditos`, `totalDebitos`, `saldo` e
   `dataHoraUltimaAtualizacao`.
5. Após o commit, o Worker confirma o processamento ao Pub/Sub.

## Regras de negócio

- Crédito aumenta `totalCreditos` e o saldo; débito aumenta `totalDebitos` e reduz o saldo.
- O mesmo `eventId` pode produzir no máximo um efeito financeiro.
- O dia do consolidado é determinado pela data do lançamento e pela regra de fuso horário definida
  para o negócio.
- Um erro antes do commit não pode marcar o evento como concluído.
- Mensagens inválidas ou que excedam a política de tentativas devem seguir para a DLQ.

## Critérios de aceite

| Cenário | Resultado esperado |
|---|---|
| Primeiro processamento de um crédito | O total de créditos e o saldo aumentam uma única vez; o endpoint devolve `204 No Content` para confirmar o ACK |
| Primeiro processamento de um débito | O total de débitos aumenta e o saldo diminui uma única vez; o endpoint devolve `204 No Content` para confirmar o ACK |
| Reentrega do mesmo `eventId` | O saldo não é alterado novamente; o endpoint devolve `204 No Content`, pois o evento já foi processado |
| Falha antes do commit | Nenhuma alteração parcial permanece; o endpoint devolve uma resposta não `2xx`, como `500 Internal Server Error`, para o Pub/Sub tentar novamente |
| Documento diário inexistente | O `upsert` cria o documento com os totais corretos e o endpoint devolve `204 No Content` |

## Quem resolve

- Pub/Sub, assinatura e política de retry/DLQ.
- Consolidado Worker, `AtualizarSaldoDiarioCommand` e seu handler.
- Collections MongoDB `processed_events` e `daily_balances`, atualizadas na mesma transação.

## Rastreabilidade

- Diagramas: páginas 2, 4 e 5 do Draw.io.
- RNFs: [RNF 003](../non-functional/RNF-003-durabilidade-e-integridade.md),
  [RNF 004](../non-functional/RNF-004-consistencia-do-consolidado.md),
  [RNF 005](../non-functional/RNF-005-recuperacao.md) e
  [RNF 007](../non-functional/RNF-007-observabilidade-e-operacao.md).
- ADRs: [ADR 002](../../03-adrs/ADR-002-integracao-assincrona-eventos.md),
  [ADR 005](../../03-adrs/ADR-005-stack-dotnet-postgresql-mongodb-gcp.md),
  [ADR 006](../../03-adrs/ADR-006-mongodb-sem-cache.md) e
  [ADR 007](../../03-adrs/ADR-007-idempotencia-ponta-a-ponta.md).

[Voltar ao catálogo de RFs](README.md)
