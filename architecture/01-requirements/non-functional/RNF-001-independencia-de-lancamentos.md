# RNF 001 - Independência e disponibilidade de Lançamentos

**Origem:** requisito explícito do desafio  
**Owner principal:** Time de Lançamentos  
**Status:** Atendido no desenho; pendente de validação por teste de resiliência

## Requisito

O serviço de controle de Lançamentos não deve ficar indisponível quando o Consolidado estiver
indisponível. O alvo proposto para Lançamentos é 99,9% de disponibilidade mensal.

## Critérios de aceite

- Durante 30 minutos de indisponibilidade do Consolidado, o `POST /lancamentos` continua registrando
  operações sem chamar o Consolidado de forma síncrona.
- Nenhum lançamento confirmado é perdido; eventos não publicados permanecem na Outbox.
- O teste acompanha taxa de sucesso, p95/p99, erros 5xx, pendências e idade da Outbox.
- Após a recuperação, as pendências são publicadas e consolidadas sem duplicar efeitos financeiros.

## Quem resolve

| Responsável na solução | Como atende |
|---|---|
| Lançamentos API | Confirma a operação usando apenas seu próprio banco. |
| PostgreSQL de Lançamentos e Outbox | Persiste o lançamento e a intenção de publicação na mesma transação. |
| Outbox Publisher | Retoma a publicação em segundo plano com retry, backoff e jitter. |
| Pub/Sub | Desacopla produtor e consumidor e retém mensagens durante indisponibilidades. |
| Cloud Run e Cloud SQL HA | Fornecem escala horizontal e redundância para o caminho de escrita. |

## ADRs relacionadas

- [ADR 001 - Microsserviços](../../03-adrs/ADR-001-microsservicos.md)
- [ADR 002 - Integração assíncrona](../../03-adrs/ADR-002-integracao-assincrona-eventos.md)
- [ADR 003 - Transactional Outbox](../../03-adrs/ADR-003-transactional-outbox.md)
- [ADR 005 - Stack .NET, PostgreSQL, MongoDB e serviços gerenciados](../../03-adrs/ADR-005-stack-dotnet-postgresql-mongodb-gcp.md)

[Voltar ao catálogo de RNFs](README.md)
