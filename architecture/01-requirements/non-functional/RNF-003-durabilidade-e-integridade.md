# RNF 003 - Durabilidade e integridade financeira

**Origem:** derivado da criticidade financeira e das garantias declaradas pela arquitetura  
**Owner principal:** Times de Lançamentos e Consolidado  
**Status:** Atendido no desenho; pendente de testes de falha e concorrência

## Requisito

Todo lançamento confirmado deve permanecer durável e produzir, no máximo, um efeito financeiro no
Consolidado. Nenhum lançamento confirmado pode ser perdido dentro da topologia de alta
disponibilidade definida.

## Critérios de aceite

- A API só responde sucesso depois do commit de `lancamentos`, `idempotency_keys` e `outbox`.
- Uma falha entre o commit e a publicação não perde o evento; a Outbox permite retomada.
- Repetir a mesma `Idempotency-Key` e o mesmo payload devolve o resultado original.
- Reentregar o mesmo `EventId` não altera novamente o saldo.
- Testes de concorrência comprovam as restrições únicas e a atomicidade das atualizações.
- Um desastre além da topologia de alta disponibilidade exige uma estratégia de recuperação
  compatível; caso contrário, a tolerância à perda de dados deve ser renegociada explicitamente.

## Quem resolve

| Responsável na solução | Como atende |
|---|---|
| Lançamentos API e PostgreSQL | Executam lançamento, chave idempotente e Outbox em uma transação ACID local. |
| Outbox Publisher | Publica novamente as intenções persistidas sem depender da requisição HTTP. |
| Consolidado Worker e MongoDB | Inserem `EventId` e atualizam o documento em `daily_balances` na mesma transação MongoDB. |
| Constraints e índices únicos | Impedem duplicação mesmo com instâncias concorrentes. |
| Cloud SQL HA e MongoDB Atlas | Protegem a durabilidade dentro das topologias de alta disponibilidade. |

## ADRs relacionadas

- [ADR 003 - Transactional Outbox](../../03-adrs/ADR-003-transactional-outbox.md)
- [ADR 005 - Stack .NET, PostgreSQL, MongoDB e serviços gerenciados](../../03-adrs/ADR-005-stack-dotnet-postgresql-mongodb-gcp.md)
- [ADR 007 - Idempotência ponta a ponta](../../03-adrs/ADR-007-idempotencia-ponta-a-ponta.md)

[Voltar ao catálogo de RNFs](README.md)
