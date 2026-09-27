# ADR 002 - Integração assíncrona orientada a eventos

**Data:** 2026-09-03  
**Responsável:** Enio Jun Yonashiro  
**Status:** Aceita

## Contexto

O registro financeiro precisa responder ao cliente sem depender da disponibilidade do Consolidado.
O saldo pode aceitar uma defasagem controlada, com lag definido, e o processamento deve
absorver picos e indisponibilidades temporárias. Uma chamada síncrona entre os serviços propagaria
latência e falhas para o caminho crítico do lançamento.

## Decisão

Nós iremos manter a entrada do lançamento síncrona por HTTP e integrar os bounded contexts de forma
assíncrona pelo Pub/Sub. Após confirmar o lançamento localmente, o outbox publisher irá publicar o contrato
versionado `LancamentoRegistrado.v1`. O Consolidado Worker irá consumir o evento, atualizar o read
model de forma idempotente e confirmar a mensagem(ack) somente depois do commit.

## Justificativa

- **Disponibilidade desacoplada:** o Consolidado pode ficar indisponível sem bloquear novos
  lançamentos.
- **Absorção de picos:** o broker retém mensagens e permite processamento conforme a capacidade do
  consumidor.
- **Escalabilidade:** várias instâncias do Worker podem consumir a mesma assinatura.
- **Extensibilidade:** novos consumidores podem reagir ao evento sem alterar a API de Lançamentos.


## RNFs atendidos

| RNF | Como esta decisão contribui | Quem resolve na solução |
|---|---|---|
| [RNF 001](../01-requirements/non-functional/RNF-001-independencia-de-lancamentos.md) | Remove a chamada síncrona ao Consolidado do caminho crítico do lançamento. | Pub/Sub, Lançamentos API e Consolidado Worker |
| [RNF 004](../01-requirements/non-functional/RNF-004-consistencia-do-consolidado.md) | Define consistência eventual mensurada por lag de processamento. | Pub/Sub, Consolidado Worker e read model |
| [RNF 007](../01-requirements/non-functional/RNF-007-observabilidade-e-operacao.md) | Torna backlog, idade da assinatura, retries e DLQ sinais operacionais obrigatórios. | Pub/Sub, telemetria dos serviços e Plataforma/SRE |

## Alternativas Consideradas

- **REST síncrono entre Lançamentos e Consolidado:** rejeitado porque acoplaria disponibilidade e
  latência e violaria o requisito principal de resiliência.
- **Banco compartilhado:** rejeitado porque quebraria ownership e independência dos bounded contexts.
- **Polling direto das tabelas de Lançamentos:** rejeitado por acoplar o consumidor ao schema interno
  do produtor.

## Trade-offs

### Benefícios

- O cliente recebe a confirmação sem esperar a atualização do saldo.
- Backlog e consumidores podem ser escalados independentemente.
- Uma indisponibilidade temporária resulta em atraso, não em perda do lançamento confirmado.

### Custos e riscos

- O saldo torna-se eventualmente consistente.
- Mensagens podem ser duplicadas, atrasadas ou chegar fora de ordem.
- Contratos precisam de versionamento e compatibilidade retroativa.
- Backlog, lag, retries e DLQ precisam ser monitorados.
