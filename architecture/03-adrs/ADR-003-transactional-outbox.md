# ADR 003 - Publicação confiável com Transactional Outbox

**Data:** 2026-09-03  
**Responsável:** Enio Jun Yonashiro  
**Status:** Aceita

## Contexto

Gravar o lançamento no PostgreSQL e publicar diretamente no Pub/Sub constitui um dual-write. Não há
uma única transação ACID envolvendo os dois recursos. Publicar antes do commit pode gerar um evento
sem lançamento; publicar depois pode deixar um lançamento confirmado sem evento caso a aplicação ou
o broker falhe. O requisito não permite perder o evento de uma operação confirmada.

## Decisão

Nós iremos gravar Lançamento, Idempotency-Key e Outbox na mesma transação do PostgreSQL de
Lançamentos. Um HostedService irá reivindicar lotes pendentes com lease e bloqueio seguro, publicar
no Pub/Sub e marcar as linhas como publicadas. Em falhas transitórias, nós iremos utilizar retry com
backoff e jitter. Leases expirados poderão ser assumidos por outra instância.

## Justificativa

- **Atomicidade local:** lançamento e intenção de publicação são confirmados ou revertidos juntos.
- **Recuperação:** se o Pub/Sub estiver indisponível, o evento permanece persistido para tentativa
  posterior.
- **Concorrência segura:** `FOR UPDATE SKIP LOCKED`, ou mecanismo equivalente, permite vários
  publicadores sem disputar a mesma linha.
- **Simplicidade:** a solução utiliza a transação do banco já necessária para o lançamento.

## RNFs atendidos

| RNF | Como esta decisão contribui | Quem resolve na solução |
|---|---|---|
| [RNF 001](../01-requirements/non-functional/RNF-001-independencia-de-lancamentos.md) | Mantém o evento pendente quando o Pub/Sub ou o Consolidado está indisponível. | PostgreSQL de Lançamentos e Outbox Publisher |
| [RNF 003](../01-requirements/non-functional/RNF-003-durabilidade-e-integridade.md) | Confirma lançamento e intenção de publicação na mesma transação local. | Lançamentos API, PostgreSQL e Outbox |
| [RNF 005](../01-requirements/non-functional/RNF-005-recuperacao.md) | Permite retomar pendências persistidas depois de falha ou reinício. | Outbox Publisher, PostgreSQL e runbook operacional |
| [RNF 007](../01-requirements/non-functional/RNF-007-observabilidade-e-operacao.md) | Exige métricas e alertas para quantidade e idade das pendências. | Outbox Publisher e Plataforma/SRE |

## Alternativas Consideradas

- **Publicar antes de gravar:** rejeitado porque poderia existir evento sem lançamento confirmado.
- **Publicar depois de gravar sem Outbox:** rejeitado porque uma falha poderia perder o evento.
- **Transação distribuída em duas fases(2PC pattern):** rejeitada por complexidade, acoplamento e suporte limitado.


## Trade-offs

### Benefícios

- Todo lançamento confirmado possui uma intenção de publicação persistida.
- Uma falha do Pub/Sub não desfaz nem perde o lançamento.
- O processamento pode ser retomado depois de reinícios ou indisponibilidades.

### Custos e riscos

- A entrega continua sendo pelo menos uma vez e pode produzir duplicações.
- Consumidores precisam ser idempotentes.
- A Outbox acrescenta escrita, índices, armazenamento, retenção e limpeza.
- Quantidade de pendências e idade da linha mais antiga precisam de alertas.

