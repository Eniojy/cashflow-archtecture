# RF 004 - Consultar consolidado diário

**Status:** Aceito  
**Origem:** Requisito explícito do desafio - relatório com saldo diário consolidado  
**Owner principal:** Time de Consolidado

## Objetivo

Permitir que o comerciante consulte, para uma data, o total de créditos, o total de débitos e o
saldo consolidado do seu fluxo de caixa.

## Ator

- Comerciante, por meio do sistema de caixa autenticado.

## Entrada e saída

- Entrada: data do consolidado em `GET /consolidado/{data}`.
- Identidade: `comercianteId` obtido do token autenticado.
- Saída: `data`, `totalCreditos`, `totalDebitos`, `saldo` e
  `dataHoraUltimaAtualizacao`.

## Fluxo principal

1. O comerciante informa a data desejada.
2. O sistema identifica o comerciante pelo token e valida a data.
3. A API busca em `daily_balances` o documento identificado por `comercianteId + data`.
4. A API devolve o consolidado e o instante da última atualização.

## Regras de negócio

- `saldo = totalCreditos - totalDebitos`.
- O comerciante só pode consultar o próprio consolidado.
- O relatório é eventualmente consistente e deve informar `dataHoraUltimaAtualizacao` para tornar
  visível a ultima atualizacao.


## Critérios de aceite

| Cenário | Resultado esperado |
|---|---|
| Existem movimentos na data | A API devolve `200 OK` com os totais e o saldo do comerciante autenticado |
| Não existem movimentos na data | A API devolve `200 OK` com totais iguais a zero |
| Data inválida | A API devolve `400 Bad Request` com o erro de validação |
| Tentativa de consultar outro comerciante | A API devolve `403 Forbidden`; a identidade do token prevalece e nenhum dado de terceiro é exposto |
| Consolidação temporariamente atrasada | A API devolve `200 OK` com `dataHoraUltimaAtualizacao`; o atraso não altera lançamentos já confirmados |

## Quem resolve

- `GET /consolidado/{data}`, `ObterSaldoDiarioQuery` e seu handler.
- `ReadModelRepository` e MongoDB `daily_balances` com índice único por comerciante e data.
- Gateway, JWT e autorização por escopo para isolamento entre comerciantes.

[Voltar ao catálogo de RFs](README.md)
