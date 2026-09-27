# Custos de Implementação

O desafio não fornece volumetria diária, armazenamento acumulado, quantidade mensal de consultas
ou requisitos de residência de dados. Por isso, os valores abaixo são uma **estimativa de ordem de
grandeza**

Foram comparados GCP, Azure e AWS usando serviços gerenciados equivalentes e o menor porte razoável
para uma produção pequena que possa escalar. A arquitetura de referência continua sendo GCP; as
outras colunas demonstram portabilidade e ajudam na decisão comercial.

## Premissas da estimativa

- Valores mensais em dólares americanos, antes de impostos, suporte e descontos contratuais.
- Região de menor custo nos Estados Unidos; regiões brasileiras normalmente custam mais.
- Aproximadamente 1 milhão de lançamentos e 2 milhões de consultas por mês.
- Pico de 50 req/s no Consolidado, mas não 50 req/s durante o mês inteiro.
- Eventos de aproximadamente 1 KB, uma assinatura e retenção de sete dias.
- Três workloads .NET: Lançamentos API, Consolidado API e Consolidado Worker.
- PostgreSQL com alta disponibilidade, 2 vCPU, memória mínima de produção e 20 GB de armazenamento.
- MongoDB Atlas dedicado de pequeno porte, aproximadamente 10 GB, réplica e backup.
- O MediatR tem licensa paga dependendo do uso, pode se substituir pelo wolverine sem alterar a arquitetura porem o mediatR tem suporte enterprise.
- Free tiers não foram descontados do total, pois não devem sustentar uma estimativa de produção.

## Mapa de serviços

| Capacidade | GCP | Azure | AWS |
|---|---|---|---|
| APIs e Worker | Cloud Run | Azure Container Apps | ECS com AWS Fargate |
| Mensageria | Pub/Sub | Service Bus Standard | SNS + SQS |
| Banco de Lançamentos | Cloud SQL for PostgreSQL HA | Azure Database for PostgreSQL Flexible Server HA | RDS for PostgreSQL Multi-AZ |
| Banco do Consolidado | MongoDB Atlas sobre GCP | MongoDB Atlas sobre Azure | MongoDB Atlas sobre AWS |
| Segredos | Secret Manager | Key Vault | Secrets Manager |
| API Management | Apigee/API Gateway | API Management | API Gateway |

## Estimativa mensal

| Camada | GCP | Azure | AWS |
|---|---:|---:|---:|
| Computação dos três workloads | US$ 25-60 | US$ 25-65 | US$ 35-80 |
| Mensageria no volume assumido | US$ 0-5 | US$ 10-20 | US$ 0-5 |
| PostgreSQL HA com 20 GB | US$ 140-220 | US$ 180-300 | US$ 90-180 |
| MongoDB Atlas dedicado + backup | US$ 60-100 | US$ 65-110 | US$ 60-100 |
| Segredos e telemetria básica | US$ 10-40 | US$ 10-40 | US$ 10-40 |
| Cache distribuído | US$ 0 | US$ 0 | US$ 0 |
| **Total mensal estimado** | **US$ 235-425** | **US$ 290-535** | **US$ 195-405** |

As faixas foram arredondadas para evitar uma falsa precisão. O banco relacional HA é o maior custo
fixo do cenário; computação e mensageria tendem a variar com o uso. Por exemplo, o Pub/Sub cobra por
volume e oferece os primeiros 10 GiB mensais sem custo, enquanto Cloud Run, Container Apps e
Fargate cobram recursos consumidos ou provisionados conforme seus respectivos modelos.

## Não incluído no total

- Assinatura corporativa do Apigee ou de outro API Management.
- Provedor de identidade corporativo.
- Frontend, CDN, domínio e certificados privados.
- Ambientes adicionais de desenvolvimento, homologação e disaster recovery.


## Fontes 

- GCP: [Cloud Run](https://cloud.google.com/run/pricing),
  [Pub/Sub](https://cloud.google.com/pubsub/pricing) e
  [Cloud SQL](https://cloud.google.com/sql/pricing/).
- Azure: [Container Apps](https://azure.microsoft.com/en-us/pricing/details/container-apps/),
  [Service Bus](https://azure.microsoft.com/en-us/pricing/details/service-bus/) e
  [Azure Database for PostgreSQL](https://azure.microsoft.com/en-us/pricing/details/postgresql/flexible-server/).
- AWS: [Fargate](https://aws.amazon.com/fargate/pricing/),
  [SQS](https://aws.amazon.com/sqs/pricing/) e
  [RDS for PostgreSQL](https://aws.amazon.com/rds/postgresql/pricing/).
- MongoDB: [Atlas Pricing Calculator](https://www.mongodb.com/pricing/calculator/).
- Calculators: [GCP](https://cloud.google.com/products/calculator),
  [Azure](https://azure.microsoft.com/en-us/pricing/calculator/) e
  [AWS](https://calculator.aws/).

As decisões de stack estão na
[ADR 005](../03-adrs/ADR-005-stack-dotnet-postgresql-mongodb-gcp.md), e as metas de recuperação e
disponibilidade estão nos [RNFs](../01-requirements/non-functional/README.md).

[Voltar à documentação de arquitetura](../README.md)
