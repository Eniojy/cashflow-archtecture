# RF 002 - Consultar lançamentos

**Status:** Aceito  
**Origem:** Refinamento da capacidade de controle de lançamentos  
**Owner principal:** Time de Lançamentos

## Objetivo

Permitir que o comerciante consulte os próprios lançamentos para conferência e rastreabilidade do
fluxo de caixa.

## Ator

- Comerciante, por meio do sistema de caixa autenticado.

## Entradas

| Campo | Regra |
|---|---|
| `dataInicial` e `dataFinal` | Período opcional da consulta, limitado por política da API |
| `tipo` | Filtro opcional por `CREDITO` ou `DEBITO` |
| `cursor` e `limite` | Paginação opcional; o limite deve respeitar o máximo definido pela API |

O `comercianteId` deve ser derivado do token autenticado.

## Fluxo principal

1. O comerciante envia `GET /lancamentos` com filtros e paginação opcionais.
2. O sistema identifica o comerciante e valida os filtros.
3. O sistema consulta somente os lançamentos pertencentes ao comerciante autenticado.
4. A API devolve os itens em ordem determinística e os dados para solicitar a próxima página.

## Regras de negócio

- Um comerciante nunca pode consultar lançamentos de outro comerciante.
- A consulta não altera o histórico financeiro.
- A paginação é obrigatória quando a quantidade ultrapassa o limite da página.
- A ausência de itens deve retornar uma coleção vazia, não um erro de negócio.

## Critérios de aceite

| Cenário | Resultado esperado |
|---|---|
| Existem lançamentos no período | A API devolve `200 OK` com apenas os registros do comerciante autenticado |
| Não existem lançamentos | A API devolve `200 OK` com coleção vazia |
| Resultado maior que o limite | A API devolve `200 OK` com uma página e um cursor para continuar a consulta |
| Tentativa de indicar outro comerciante | A API devolve `403 Forbidden`; nunca há exposição de dados de terceiros |
| Filtro inválido | A API devolve `400 Bad Request` com os erros de validação |

## Quem resolve

- `GET /lancamentos` e uma `ConsultarLancamentosQuery`.
- Query Handler e repositório de leitura do domínio Lançamentos.
- PostgreSQL com índice por comerciante, data e identificador para ordenação estável.
- Gateway, JWT e autorização por escopo para isolamento entre comerciantes.

## Rastreabilidade

- Diagramas: páginas 2 e 3 do Draw.io.
- RNFs: [RNF 006](../non-functional/RNF-006-seguranca.md) e
  [RNF 007](../non-functional/RNF-007-observabilidade-e-operacao.md).
- ADRs: [ADR 004](../../03-adrs/ADR-004-cqrs-mediatr-sem-event-sourcing.md) e
  [ADR 005](../../03-adrs/ADR-005-stack-dotnet-postgresql-mongodb-gcp.md).

[Voltar ao catálogo de RFs](README.md)
