# RF 001 - Registrar lançamento

**Status:** Aceito  
**Origem:** Requisito explícito do desafio - controle de lançamentos  
**Owner principal:** Time de Lançamentos

## Objetivo

Permitir que um comerciante registre um lançamento financeiro de crédito ou débito no seu fluxo de
caixa. Uma confirmação da API significa que o lançamento foi persistido e não será perdido.

## Ator

- Comerciante, por meio do sistema de caixa autenticado.

## Entradas

| Campo | Regra |
|---|---|
| `Idempotency-Key` | Cabeçalho obrigatório e único no escopo do comerciante |
| `tipo` | Deve ser `CREDITO` ou `DEBITO` |
| `valor` | Deve ser maior que zero e usar precisão monetária definida pelo contrato |
| `dataHoraOcorrencia` | Instante em que o movimento financeiro ocorreu |

O `comercianteId` não é aceito como fonte confiável no payload: ele deve ser obtido do token
autenticado.

## Fluxo principal

1. O comerciante envia `POST /lancamentos` com JWT, `Idempotency-Key` e os dados do lançamento.
2. A API identifica o comerciante pelo token e valida o contrato.
3. O sistema verifica a chave de idempotência e o hash do payload.
4. O `RegistrarLancamentoCommandHandler` cria o Aggregate `Lancamento` e aplica as invariantes de
   negócio.
5. Em uma única transação PostgreSQL, o sistema grava o lançamento, a chave de idempotência e o
   evento pendente na Outbox.
6. Após o commit, a API devolve `201 Created` com a identificação do lançamento.
7. Em segundo plano, o Outbox Publisher publica `LancamentoRegistrado.v1` no Pub/Sub para o domínio
   Consolidado.

## Regras de negócio

- O valor do lançamento deve ser positivo e seu tipo deve ser crédito ou débito.
- O comerciante somente pode registrar lançamentos em seu próprio contexto de autorização.
- A mesma `Idempotency-Key` com o mesmo payload deve devolver o resultado original sem criar outro novo lancamento.
- A mesma `Idempotency-Key` com payload diferente deve ser rejeitada como conflito.  409
- O lançamento somente é considerado confirmado depois do commit no PostgreSQL.
- Lançamentos confirmados são append-only.
- Uma indisponibilidade do Pub/Sub não desfaz o lançamento confirmado: o evento permanece na
  Outbox para publicação posterior.

## Critérios de aceite

| Cenário | Resultado esperado |
|---|---|
| Requisição válida e unica | Lançamento, idempotência e evento de Outbox são persistidos atomicamente; a API devolve `201 Created` |
| Mesma chave e mesmo payload | A API devolve novamente `201 Created` com o resultado original e não cria outro lançamento |
| Mesma chave e payload diferente | A API devolve `409 Conflict` e não altera os dados |
| Tipo inválido ou valor não positivo | A API devolve `400 Bad Request` e não persiste o lançamento |
| Falha antes do commit | Nenhuma gravação parcial permanece; a API devolve `500 Internal Server Error` e não confirma o lançamento |
| Pub/Sub indisponível após o commit | A API já devolveu `201 Created`; o lançamento continua confirmado e o evento permanece pendente na Outbox |

## Quem resolve

- `POST /lancamentos`, `RegistrarLancamentoCommand` e seu handler.
- Aggregate `Lancamento` para as invariantes de negócio.
- PostgreSQL para `lancamentos`, `idempotency_keys` e `outbox` na mesma transação.
- Outbox Publisher e Pub/Sub para a notificação assíncrona posterior.


[Voltar ao catálogo de RFs](README.md)
