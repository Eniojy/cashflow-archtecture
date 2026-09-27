# ADR 004 - CQRS leve e MediatR sem Event Sourcing

**Data:** 2026-09-03  
**Responsável:** Enio Jun Yonashiro  
**Status:** Aceita

## Contexto

Os casos de uso precisam separar operações que alteram estado das consultas e aplicar validação,
autorização, tracing, idempotência e transação de maneira uniforme. Os handlers devem ser testáveis
sem depender de HTTP. O desafio não exige reconstrução de agregados a partir do histórico de eventos
e não justifica a complexidade de uma implementação completa de CQRS com Event Sourcing.

## Decisão

Nós iremos utilizar CQRS leve dentro das aplicações. Escritas serão representadas por Commands e
leituras por Queries, cada uma com seu Handler. Nós iremos utilizar MediatR para despachar essas
operações dentro do processo e Behaviors para responsabilidades transversais. O Consolidado manterá
um read model pronto para consulta, sem reconstruir estado por reprodução de eventos.

## Justificativa

- **Separação de responsabilidades:** Commands alteram estado e Queries apenas consultam.
- **Testabilidade:** handlers permanecem independentes do transporte HTTP.
- **Reuso:** Behaviors centralizam validação, autorização, observabilidade, idempotência e transação.
- **Simplicidade proporcional:** o read model atende às consultas sem Event Store, snapshots ou
  replay para reconstrução de agregados.


## RNFs atendidos

| RNF | Como esta decisão contribui | Quem resolve na solução |
|---|---|---|
| [RNF 006](../01-requirements/non-functional/RNF-006-seguranca.md) | Centraliza autorização e validação como comportamentos transversais, sem substituir as políticas do Gateway. | Behaviors do MediatR, handlers e API Gateway |
| [RNF 007](../01-requirements/non-functional/RNF-007-observabilidade-e-operacao.md) | Padroniza tracing, métricas, logs e correlação ao redor dos casos de uso. | Behaviors do MediatR e instrumentação OpenTelemetry |

## Alternativas Consideradas

- **Controllers chamando repositórios diretamente:** rejeitada porque repetiria responsabilidades
  transversais e dificultaria testes e padronização.
- **Event Sourcing:** rejeitado por adicionar Event Store, snapshots e evolução de eventos sem
  requisito que compense o custo.
- **Wolverine, mediator com source generator ou implementação própria:** permanecem alternativas caso
  licença impeçam o uso do MediatR.

## Trade-offs

### Benefícios

- Endpoints menores e casos de uso organizados por intenção.
- Behaviors reutilizáveis e handlers testáveis.
- Modelo de leitura otimizado sem afetar o modelo de escrita.

### Custos e riscos

- A solução ganha mais tipos, arquivos e fluxo indireto.
- Commands e Queries triviais podem gerar deixar verboso.
- A equipe precisa conhecer a ordem e o comportamento do pipeline.
- O MediatR cria uma dependência e uma decisão de licenciamento a ser acompanhada.
