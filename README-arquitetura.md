# Desafio de Arquitetura - Fluxo de Caixa Diario

Entregavel principal: `arquitetura-fluxo-de-caixa-c4-bian.drawio`.

O arquivo contem somente desenhos e decisoes arquiteturais. Nao ha aplicacao, codigo-fonte ou
infraestrutura executavel. A proposta foi desenhada para ser explicada por um arquiteto junior,
mas inclui os pontos que normalmente sao explorados em uma entrevista: dominios, trade-offs,
resiliencia, seguranca, observabilidade, custo e testes.

## Como abrir

1. Acesse [diagrams.net](https://app.diagrams.net/) ou abra o aplicativo Draw.io Desktop.
2. Escolha **Arquivo > Abrir**.
3. Selecione `arquitetura-fluxo-de-caixa-c4-bian.drawio`.
4. Navegue pelas oito paginas na barra inferior.

## Paginas do arquivo

| # | Pagina | Objetivo |
|---|---|---|
| 1 | C4 N1 - Contexto | Mostra pessoas, sistema em escopo e IdP corporativo. |
| 2 | C4 N2 - Conteineres | Apresenta a arquitetura alvo em GCP, os tres processos .NET, bancos, Outbox, Pub/Sub e DLQ. |
| 3 | C4 N3 - Lancamentos | Explica Endpoint, MediatR, Behaviors, Handler, dominio, EF Core, Outbox e publicador. |
| 4 | C4 N3 - Consolidado | Separa o fluxo de projecao do saldo do fluxo de consulta do relatorio. |
| 5 | Dominios BIAN | Mapeia Position Keeping e Customer Position e identifica capacidades reutilizadas e futuras. |
| 6 | Fluxo do sistema | Conta a historia ponta a ponta, incluindo tecnologias e cenarios de falha. |
| 7 | Transicao opcional | Mostra Strangler Fig somente se a existencia de legado for confirmada. |
| 8 | Decisoes e RNFs | Consolida ADRs, criterios de aceite, seguranca, observabilidade, custos, testes e evolucoes. |

O C4 termina deliberadamente no nivel 3. O nivel 4 nao agrega valor para este desafio, pois nao ha
codigo a documentar e diagramas de classes envelheceriam rapidamente.

Essa selecao segue a orientacao oficial do [C4 Model](https://c4model.com/diagrams): contexto,
conteineres e componentes sao niveis de zoom diferentes, e so devem ser usados quando agregam valor.
A notacao mantem tipo, responsabilidade, tecnologia e relacionamentos rotulados, conforme o
[guia oficial de notacao](https://c4model.com/diagrams/notation).

## Resumo da arquitetura

Ha dois bounded contexts e tres processos:

- **Lancamentos / Position Keeping**: `Lancamentos API`, que tambem hospeda um publicador de Outbox.
- **Consolidado / Customer Position**: `Consolidado Worker`, que projeta o saldo, e
  `Consolidado API`, que consulta o read model.

O POST confirma o lancamento e a linha da Outbox na mesma transacao PostgreSQL. Depois do `201`, o
publicador envia `LancamentoRegistrado.v1` para o Pub/Sub. O Worker processa de forma idempotente e
atualiza `saldo_diario`. Assim, a indisponibilidade do Consolidado nao se propaga para Lancamentos.

### Por que nao ha Redis no MVP

O relatorio le uma unica linha indexada pela chave `(comerciante, data)`. Para apenas 50 req/s,
adicionar Redis antes do teste de carga criaria custo e mais um ponto operacional sem evidencia de
necessidade. O criterio proposto e mensuravel: 15 minutos a 50 req/s, sucesso minimo de 95% e p95
abaixo de 200 ms. Redis entra como evolucao se esse gate falhar.

## BIAN: uso correto e pragmatismo

O desenho usa o BIAN apenas para o dominio de Business Area
| BIAN 14.0 | Aplicacao no desafio |
|---|---|
| Operations & Execution > Account Management > **Position Keeping** | Registrar debitos/creditos e manter o log financeiro; bounded context Lancamentos. |
| Operations & Execution > Account Management > **Customer Position** | Manter e expor a posicao consolidada do 

Position Keeping e Customer Position foram verificadas no
[BIAN Service Landscape 14.0](https://bian.org/deliverables/service-landscape/), incluindo as paginas
oficiais de [Position Keeping](https://bian.org/servicelandscape-14-0-0/object_14.html?object=34193) e
[Customer Position](https://bian.org/servicelandscape-14-0-0/object_14.html?object=34382).

## MediatR: como defender a escolha

MediatR eh somente um mediador **dentro do processo**. Ele:

- resolve `Command/Query -> Handler`;
- concentra validacao, observabilidade, idempotencia e transacao em `IPipelineBehavior`;
- mantem endpoints pequenos e handlers testaveis sem HTTP;
- nao substitui Pub/Sub e nao desacopla servicos distribuidos.

## Decisoes que demonstram dominio sem superdimensionar

- `.NET 10 LTS`, ASP.NET Core, EF Core, Dapper e MediatR.
- Transactional Outbox para eliminar dual-write.
- CQRS leve com read model; sem Event Sourcing.
- Pub/Sub com entrega at-least-once, consumidor idempotente, retry e dead-letter topic.
- Bancos separados entre bounded contexts; nenhum acesso cruzado.
- `GET /consolidado/{data}` deriva `comercianteId` do claim para evitar IDOR.
- OpenTelemetry com SLI de lag de consolidacao.
- Sem Kubernetes, service mesh, GraphQL ou cache antes de existir requisito.

O `.NET 10` foi escolhido por estar em suporte LTS ativo ate novembro de 2028, conforme a
[politica oficial de suporte do .NET](https://dotnet.microsoft.com/en-us/platform/support/policy).
Para Pub/Sub, a proposta assume entrega at-least-once e dead-letter policy configurada na assinatura,
de acordo com a documentacao oficial de
[confiabilidade](https://docs.cloud.google.com/pubsub/docs/reliability-intro) e
[dead-letter topics](https://docs.cloud.google.com/pubsub/docs/handling-failures).

## Nota de custos

Os valores da pagina 8 sao ordem de grandeza para planejamento em agosto de 2026, nao cotacao. Devem
ser recalculados por regiao, volume, descontos e contrato no
[Google Cloud Pricing Calculator](https://cloud.google.com/products/calculator). A cobranca do Cloud
Run varia com configuracao request-based/instance-based e instancias minimas; consulte a
[pagina oficial de precos do Cloud Run](https://cloud.google.com/run/pricing).
