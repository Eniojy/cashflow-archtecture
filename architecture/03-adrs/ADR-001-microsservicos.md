# ADR 001 - Escolha de uma arquitetura de microsserviços pragmática

**Data:** 2026-09-03  
**Responsável:** Enio Jun Yonashiro  
**Status:** Aceita

## Contexto

O sistema precisa registrar lançamentos mesmo quando a consolidação estiver indisponível. As
capacidades de Lançamentos e Consolidado possuem propriedade dos dados, consistência, disponibilidade e escala diferentes. A solução prevê três unidades de execução: Lançamentos API,
Consolidado Worker e Consolidado API. Entretanto, separar cada componente técnico em um bounded
context aumentaria o custo operacional sem criar uma nova responsabilidade de negócio.

## Decisão

Nós iremos adotar uma arquitetura de microsserviços pragmática, com dois serviços lógicos alinhados
aos bounded contexts Lançamentos e Consolidado. Lançamentos será responsável pelo registro financeiro
e sua Outbox. Consolidado será responsável pelo read model de saldo, utilizando Worker e API como
deploys independentes dentro do mesmo contexto. Cada domain terá seu próprio banco
persistente e a integração entre eles ocorrerá por eventos.

## Justificativa

- **Escalabilidade individual:** API de consulta, Worker e API de Lançamentos podem escalar conforme suas demandas específicas.
- **Isolamento de falhas:** falhas no Consolidado não interrompem o registro de lançamentos.
- **Ownership dos dados:** cada contexto possui e protege seu próprio modelo persistente.
- **Coesão de negócio:** Worker e API de Consolidado permanecem juntos porque mantêm e consultam o
  mesmo read model.
- **Custo controlado:** evitamos criar microsserviços sem um dominio de negócio que justifique
  pipelines, observabilidade, segurança e suporte adicionais.

## RNFs atendidos

| RNF | Como esta decisão contribui | Quem resolve na solução |
|---|---|---|
| [RNF 001](../01-requirements/non-functional/RNF-001-independencia-de-lancamentos.md) | Separa disponibilidade, dados e deploy de Lançamentos e Consolidado. | Lançamentos API, Consolidado API/Worker e bancos separados |
| [RNF 002](../01-requirements/non-functional/RNF-002-desempenho-do-consolidado.md) | Permite escalar a consulta e o Worker independentemente do caminho de escrita. | Consolidado API, Consolidado Worker e Cloud Run |

## Alternativas Consideradas

- **Monólito com banco único:** rejeitado porque aumentaria o raio de impacto e acoplaria deploy,
  disponibilidade e escala de Lançamentos e Consolidado.
- **Um microserviço por componente técnico:** rejeitado porque o Worker não representa uma nova
  capacidade de negócio nem possui dados próprios.


## Trade-offs

### Benefícios

- Falhas e escalabilidade ficam segregadas por responsabilidade.
- O Worker pode escalar pelo backlog enquanto a API escala pelas requisições de leitura.
- Cada contexto pode evoluir e ser implantado com menor impacto sobre o outro.
- As fronteiras de domínio e a propriedade dos dados ficam explícitas.

### Custos e riscos

- A solução possui mais deploys, pipelines, bancos, dashboards e alertas para operar.
- O saldo passa a ter consistência eventual.
- Logs e traces precisam ser correlacionados entre processos.
- Contratos de eventos e procedimentos de reconciliação tornam-se obrigatórios.
