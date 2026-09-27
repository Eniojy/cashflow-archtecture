# ADR 007 - Idempotência na API e no consumidor de eventos

**Data:** 2026-09-03  
**Responsável:** Enio Jun Yonashiro  
**Status:** Aceita

## Contexto

Clientes podem repetir uma requisição após timeout, e o Pub/Sub pode entregar novamente uma mensagem
que já foi processada. Em um fluxo financeiro, retries não podem criar outro lançamento nem somar o
mesmo evento novamente. A garantia precisa sobreviver a reinícios, escala horizontal, concorrência e
perda do cache.

## Decisão

Nós iremos exigir `Idempotency-Key` nas escritas e persistir a chave no PostgreSQL de Lançamentos, com escopo por
`comercianteId`, hash do payload, status e resposta original. A mesma chave e hash devolverão o
resultado anterior; a mesma chave com payload diferente será rejeitada. Nós iremos gerar um
`EventId` estável e inseri-lo na collection `processed_events`, com índice único, na mesma transação
MongoDB que executa o upsert do documento em `daily_balances`. O Worker somente enviará ACK depois
do commit.

## Justificativa

- **Retries seguros:** uma nova tentativa produz o mesmo efeito de negócio.
- **Concorrência:** índices únicos resolvem corridas entre instâncias.
- **Durabilidade:** a evidência de processamento permanece após reinício do processo.
- **Atomicidade:** `EventId` processado e atualização do saldo confirmam ou revertem juntos.
- **Efeito exatamente uma vez:** o transporte continua pelo menos uma vez, mas a transação local
  impede aplicação duplicada do efeito financeiro.

## RNFs atendidos

| RNF | Como esta decisão contribui | Quem resolve na solução |
|---|---|---|
| [RNF 003](../01-requirements/non-functional/RNF-003-durabilidade-e-integridade.md) | Impede duplicação da requisição e do efeito financeiro mesmo sob retry e concorrência. | Lançamentos API/PostgreSQL e Consolidado Worker/MongoDB |
| [RNF 004](../01-requirements/non-functional/RNF-004-consistencia-do-consolidado.md) | Garante que reentregas e reprocessamentos façam o saldo convergir sem dupla soma. | `processed_events`, índice único e transação MongoDB do Worker |
| [RNF 005](../01-requirements/non-functional/RNF-005-recuperacao.md) | Torna seguro o replay de mensagens retidas ou enviadas à DLQ. | Consolidado Worker, MongoDB, Pub/Sub e operação |
| [RNF 007](../01-requirements/non-functional/RNF-007-observabilidade-e-operacao.md) | Exige visibilidade sobre duplicatas, conflitos de chave e reprocessamentos. | Telemetria das APIs/Worker e Plataforma/SRE |

## Alternativas Consideradas

- **Confiar em entrega exatamente uma vez:** rejeitada porque falhas entre processamento e ACK ainda
  podem provocar reentrega.
- **Deduplicar por valor, data ou hash:** rejeitada porque lançamentos legítimos podem ter o mesmo
  conteúdo.
- **Guardar idempotência somente em memória:** rejeitada porque reinício e escala horizontal
  removeriam a garantia.
- **Ignorar duplicatas no consumidor:** rejeitada porque produziria saldo incorreto.

## Trade-offs

### Benefícios

- Repetições da mesma requisição não criam novos lançamentos.
- Reentregas do Pub/Sub não somam novamente o saldo.
- A garantia funciona com múltiplas instâncias concorrentes.

### Custos e riscos

- Clientes precisam gerar e reutilizar corretamente a `Idempotency-Key`.
- Tabelas de idempotência e eventos processados consomem armazenamento.
- É necessária uma política de retenção compatível com retries, replay e auditoria.
- Conflitos e violações de unicidade precisam ser tratados de forma explícita.
