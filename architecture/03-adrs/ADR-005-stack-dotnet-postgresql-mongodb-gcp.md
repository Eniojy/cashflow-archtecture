# ADR 005 - Stack .NET, PostgreSQL, MongoDB e serviços gerenciados

**Data:** 2026-09-03  
**Responsável:** Enio Jun Yonashiro  
**Status:** Aceita

## Contexto

A solução precisa executar APIs e Workers, preservar integridade transacional, escalar
horizontalmente e reduzir o esforço de operação. Os dois bounded contexts possuem necessidades de
persistência diferentes: Lançamentos precisa de transação relacional para lançamento, idempotência e
Outbox; Consolidado mantém um read model com um documento por lojista e data. 

## Decisão

Nós iremos implementar APIs e Workers em .NET 10 LTS e executá-los em contêineres no Cloud Run.
Lançamentos utilizará Cloud SQL for PostgreSQL como fonte da verdade. Consolidado utilizará MongoDB
Atlas implantado no Google Cloud como fonte persistente do read model, acessado pelo driver oficial
do MongoDB para .NET. Em produção, o cluster MongoDB terá réplica, write concern `majority`, backup e
restauração para um instante específico. O Worker utilizará uma transação MongoDB para inserir o `EventId` em `processed_events` e fazer
upsert do documento em `daily_balances`. Nós iremos utilizar Pub/Sub para eventos. A Consolidado API
consultará o MongoDB diretamente, sem cache distribuído.

## Justificativa

- **Suporte:** .NET 10 possui suporte LTS ativo até novembro de 2028.
- **Integridade em Lançamentos:** PostgreSQL oferece ACID, constraints, índices únicos, UPSERT e
  `SKIP LOCKED` para lançamento, idempotência e Outbox.
- **Modelo de leitura documental:** MongoDB representa o saldo diário como um documento identificado
  por lojista e data, sem joins no caminho de consulta.
- **Idempotência transacional:** MongoDB suporta transações entre `processed_events` e
  `daily_balances`; índices únicos protegem `EventId` e a chave do saldo. A implementação seguirá a
  documentação oficial de [transações no driver .NET](https://www.mongodb.com/docs/drivers/csharp/current/crud/transactions/)
  e de [upsert com UpdateOne](https://www.mongodb.com/docs/drivers/csharp/current/crud/update-one/).
- **Operação gerenciada:** Cloud Run reduz administração de servidores e clusters.
- **Escalabilidade:** os contêineres podem escalar horizontalmente por carga ou backlog.
- **Aderência corporativa:** a GCP tem um dos menores custos e foi levado em consideracao a infraestrutura do cliente.
- **Recuperação gerenciada:** MongoDB Atlas oferece backup contínuo e recuperação point-in-time,
  conforme sua [orientação oficial de disaster recovery](https://www.mongodb.com/docs/atlas/architecture/current/disaster-recovery/).

## RNFs atendidos

| RNF | Como esta decisão contribui | Quem resolve na solução |
|---|---|---|
| [RNF 001](../01-requirements/non-functional/RNF-001-independencia-de-lancamentos.md) | Fornece deploys separados, redundância do banco e escala do caminho de escrita. | Cloud Run e Cloud SQL HA de Lançamentos |
| [RNF 002](../01-requirements/non-functional/RNF-002-desempenho-do-consolidado.md) | Fornece autoscaling e read model documental com índice composto. | Cloud Run e MongoDB Atlas |
| [RNF 003](../01-requirements/non-functional/RNF-003-durabilidade-e-integridade.md) | Combina a transação PostgreSQL de Lançamentos com a transação e o write concern `majority` do MongoDB. | Cloud SQL, MongoDB Atlas e times das aplicações |
| [RNF 005](../01-requirements/non-functional/RNF-005-recuperacao.md) | Disponibiliza alta disponibilidade, backup e restauração para as duas fontes persistentes. | Cloud SQL, MongoDB Atlas, Plataforma/SRE e DBA |
| [RNF 006](../01-requirements/non-functional/RNF-006-seguranca.md) | Disponibiliza identidades IAM, criptografia e gestão de segredos da plataforma. | IAM, Secret Manager, serviços GCP e Segurança/Identidade |

## Alternativas Consideradas

- **PostgreSQL também no Consolidado:** é a alternativa mais simples operacionalmente, mas foi
  rejeitada para favorecer um read model documental, isolado e preparado para evolução do relatório.
- **MongoDB também em Lançamentos:** rejeitado porque PostgreSQL atende melhor ao fluxo já desenhado
  de transação, constraints, Outbox e processamento com `SKIP LOCKED`.
- **MongoDB autogerenciado:** rejeitado porque operação de réplica, backup, correções e failover
  aumentaria o custo operacional; será usado MongoDB Atlas no Google Cloud.
- **GKE/Kubernetes próprio:** rejeitado porque os requisitos não justificam o custo operacional de um
  cluster.
- **Máquinas virtuais:** rejeitadas por exigirem mais provisionamento, patching e capacidade ociosa.
- **Stack totalmente agnóstica de nuvem:** rejeitada porque abrir mão de serviços gerenciados também
  possui custo; serão mantidas abstrações somente nos pontos em que troca seja plausível.

## Trade-offs

### Benefícios

- Stack gerenciada e alinhada ao ambiente de referência.
- Deploys independentes e escala horizontal dos processos stateless.
- PostgreSQL fornece as garantias transacionais do registro e da Outbox.
- MongoDB oferece um read model documental com consulta direta por lojista e data.
- Contêineres, PostgreSQL e MongoDB preservam parte da portabilidade.

### Custos e riscos

- Cloud Run, Pub/Sub, Cloud SQL e MongoDB Atlas geram vendor lock-in operacional.
- A persistência poliglota exige conhecimento, observabilidade, testes e procedimentos para dois
  bancos diferentes.
- Transações MongoDB exigem um cluster com réplica e devem permanecer curtas.
- Autoscaling precisa ser limitado para não sobrecarregar conexões e escrita nos bancos.
- HA, instâncias mínimas, observabilidade e Atlas geram custos recorrentes.
- A aplicação precisará migrar para uma versão suportada antes do fim do .NET 10 LTS.
