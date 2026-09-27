# ADR 006 - MongoDB como read model sem cache distribuído

**Data:** 2026-09-03  
**Responsável:** Enio Jun Yonashiro  
**Status:** Aceita

## Contexto

O Consolidado precisa sustentar 50 consultas por segundo sobre um saldo diário identificado por
lojista e data. O documento já estará pré-calculado no MongoDB e será consultado por uma chave
composta indexada.

## Decisão

Nós iremos utilizar somente MongoDB Atlas como fonte persistente e mecanismo de leitura do
Consolidado no MVP. A collection `daily_balances` terá índice único composto por
`comercianteId + data`. A Consolidado API consultará diretamente esse documento pelo driver oficial
do MongoDB para .NET. Não utilizaremos cache distribuído nesta fase.

## Justificativa

- **Simplicidade:** uma única fonte de leitura elimina invalidação, TTL, cache stampede e fallback.
- **Consulta direta:** o read model já contém totais e saldo prontos, sem agregação durante o GET.
- **Capacidade inicial:** 50 req/s é uma carga leve.
- **Consistência:** a resposta reflete o último documento confirmado no MongoDB, sem defasagem extra
  causada por cache.


## RNFs atendidos

| RNF | Como esta decisão contribui | Quem resolve na solução |
|---|---|---|
| [RNF 002](../01-requirements/non-functional/RNF-002-desempenho-do-consolidado.md) | Usa um documento pré-calculado e índice composto para atender a consulta sem joins. | Consolidado API, MongoDB Atlas e índice de `daily_balances` |
| [RNF 004](../01-requirements/non-functional/RNF-004-consistencia-do-consolidado.md) | Evita que TTL ou invalidação acrescentem defasagem ao read model. | Consolidado Worker, MongoDB e Consolidado API |
| [RNF 007](../01-requirements/non-functional/RNF-007-observabilidade-e-operacao.md) | Concentra métricas de consulta, conexões e saturação em um único banco de leitura. | Telemetria da API, MongoDB Atlas e Plataforma/SRE |

## Alternativas Consideradas

- **Cache distribuído com Redis:** rejeitado no MVP porque não existe evidência de que MongoDB
  indexado não sustente 50 req/s; aumentaria custo e complexidade operacional.
- **Cache local em memória:** rejeitado porque cada instância teria uma cópia
  diferente e descartável.
- **PostgreSQL como read model:** rejeitado pela decisão de representar o Consolidado como documento, porem mais nada impediria.

## Trade-offs

### Benefícios

- Menos componentes, falhas, conexões, alertas e custo operacional.
- Não existe política de TTL ou invalidação para manter.
- A leitura sempre usa a fonte persistente do Consolidado.
- A arquitetura continua permitindo escala horizontal da API.

### Custos e riscos

- Toda consulta alcança o MongoDB, que precisa ser dimensionado e protegido contra picos.
- O pool de conexões e o autoscaling da API precisam de limites para não saturar o cluster.

