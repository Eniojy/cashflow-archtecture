# Arquitetura de Fluxo de Caixa

Este é o índice principal do entregável. O arquivo
[`arquitetura-fluxo-de-caixa-c4.drawio`](arquitetura-fluxo-de-caixa-c4.drawio) permanece na raiz e
é a fonte editável dos diagramas. Os SVGs são exportações para consulta rápida e inclusão em
documentação.

## Estrutura

```text
architecture/
├── 01-requirements/
│   ├── functional/
│   └── non-functional/
├── 02-c4-model/
│   ├── system-context/
│   ├── containers/
│   └── components/
├── 03-adrs/
├── 04-legacy-modernization/
├── 05-cost/
├── 06-security/
├── 07-observability/
└── sequence-diagrams/
```

## Navegação

| Seção | Conteúdo |
|---|---|
| [Architecture](architecture/README.md) | Índice completo da documentação |
| [Requisitos funcionais](architecture/01-requirements/functional/README.md) | RFs, regras de negócio e critérios de aceite |
| [Requisitos não funcionais](architecture/01-requirements/non-functional/README.md) | SLOs, qualidade e responsáveis |
| [Modelo C4](architecture/02-c4-model/README.md) | Contexto, contêineres e componentes em SVG |
| [ADRs](architecture/03-adrs/README.md) | Decisões arquiteturais e trade-offs |
| [Modernização do legado](architecture/04-legacy-modernization/README.md) | Transição por Strangler Fig, CDC e ACL |
| [Custos](architecture/05-cost/README.md) | Direcionadores e método de estimativa |
| [Segurança](architecture/06-security/README.md) | Controles de identidade, borda, dados e supply chain |
| [Observabilidade](architecture/07-observability/README.md) | Telemetria, sinais operacionais, alertas e runbooks |
| [Diagramas de sequência](architecture/sequence-diagrams/README.md) | Fluxo de registro, publicação, consolidação e consulta |
| [Documentação arc42](arc42/README.adoc) | Visão consolidada da arquitetura em AsciiDoc |
| [docToolchain](DOCTOOLCHAIN.adoc) | Comandos para gerar HTML e PDF |
