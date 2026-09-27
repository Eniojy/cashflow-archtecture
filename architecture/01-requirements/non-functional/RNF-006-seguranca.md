# RNF 006 - Segurança dos serviços e dos dados

**Origem:** requisito diferencial de segurança para consumo e integração de serviços  
**Owner principal:** Segurança/Identidade, com execução pelos times das APIs  
**Status:** Atendido no desenho; pendente de testes de segurança

## Requisito

Somente identidades autenticadas e autorizadas poderão acessar os serviços e cada lojista poderá
consultar apenas os próprios dados. Tráfego, dados, segredos e artefatos de implantação devem ser
protegidos durante todo o ciclo de vida.

## Critérios de aceite

- Validar tokens OAuth 2.0/OIDC, emissor, audiência, assinatura, expiração e scopes.
- Derivar `comercianteId` de claim confiável, sem aceitar o identificador do corpo ou da URL para
  decidir o escopo de acesso.
- Usar identidade IAM/OIDC e privilégio mínimo nas integrações entre Gateway, Pub/Sub e Cloud Run.
- Usar TLS em trânsito, criptografia em repouso e segredos fora do código, com rotação.
- Testar autenticação, autorização, IDOR, rate limit, validação de payload e ameaças do fluxo.

## Quem resolve

| Responsável na solução | Como atende |
|---|---|
| IdP corporativo | Autentica identidades e emite tokens OAuth 2.0/OIDC. |
| API Gateway | Valida token, aplica políticas de borda, quotas e rate limit. |
| APIs e Behaviors | Aplicam autorização, validação e isolamento por `comercianteId`. |
| IAM e identidades de serviço | Limitam chamadas internas e acesso a recursos de nuvem. |
| Secret Manager e serviços gerenciados | Protegem segredos e dados em trânsito e repouso. |
| Pipeline CI/CD | Verifica dependências e assinatura do artefato. |

## ADRs relacionadas

- [ADR 004 - CQRS leve e MediatR](../../03-adrs/ADR-004-cqrs-mediatr-sem-event-sourcing.md)
- [ADR 005 - Stack .NET, PostgreSQL, MongoDB e serviços gerenciados](../../03-adrs/ADR-005-stack-dotnet-postgresql-mongodb-gcp.md)

[Voltar ao catálogo de RNFs](README.md)
