# RNF 002 - Desempenho e capacidade do Consolidado

**Origem:** requisito explícito do desafio  
**Owner principal:** Time de Consolidado  
**Status:** Atendido no desenho; pendente de validação por teste de carga

## Requisito

Em dias de pico, o serviço do Consolidado deve sustentar 50 requisições por segundo com, no máximo,
5% de requisições malsucedidas. Como meta de latência da solução, o p95 deve permanecer abaixo de
200 ms durante o teste sustentado.

## Critérios de aceite

- Executar 50 req/s por 15 minutos com uma distribuição representativa de lojistas e datas.
- Obter taxa de sucesso mínima de 95% e latência p95 abaixo de 200 ms.
- Não esgotar o pool de conexões nem levar CPU, memória ou banco a uma saturação sem controle.
- A consulta ao documento indexado no MongoDB deve atender ao SLO sem uma camada de cache.
- O limite de falhas mede requisições de consulta, não perda de dados financeiros.

## Quem resolve

| Responsável na solução | Como atende |
|---|---|
| Consolidado API | Expõe uma consulta stateless e pode escalar horizontalmente. |
| MongoDB Atlas | Mantém um documento pré-calculado com índice único por lojista e data. |
| Cloud Run | Escala instâncias conforme concorrência, dentro de limites que protegem o banco. |
| Plataforma/SRE | Executa o teste de carga, dimensiona limites e acompanha saturação. |

## ADRs relacionadas

- [ADR 001 - Microsserviços](../../03-adrs/ADR-001-microsservicos.md)
- [ADR 005 - Stack .NET, PostgreSQL, MongoDB e serviços gerenciados](../../03-adrs/ADR-005-stack-dotnet-postgresql-mongodb-gcp.md)
- [ADR 006 - MongoDB sem cache distribuído](../../03-adrs/ADR-006-mongodb-sem-cache.md)

[Voltar ao catálogo de RNFs](README.md)
