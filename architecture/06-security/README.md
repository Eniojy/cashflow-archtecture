# Segurança

Os controles abaixo protegem o consumo dos serviços, os dados financeiros e a cadeia de entrega.

| Camada | Controles |
|---|---|
| Autenticação | OAuth 2.0/OIDC; Authorization Code + PKCE para usuário |
| Autorização | Scopes `lancamentos:write`, `lancamentos:read` e `consolidado:read`; `comercianteId` obtido do claim |
| Borda | TLS 1.2+, WAF, rate limit/quota por cliente e validação de schema/payload |
| Dados | Criptografia em trânsito; segredos no Secret Manager com rotação |
| Auditoria | Lançamentos append-only; correção por lançamento compensatório |
| LGPD | Minimização, finalidade, retenção e trilha de acesso definidas com negócio e jurídico |

## Proteção contra IDOR

As APIs não confiam em um `comercianteId` fornecido pelo cliente. O identificador vem do token e é
validado contra o id

Os critérios mensuráveis e responsabilidades estão no
[RNF 006](../01-requirements/non-functional/RNF-006-seguranca.md).

[Voltar à documentação de arquitetura](../README.md)
