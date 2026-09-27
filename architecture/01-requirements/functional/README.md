# Catálogo de Requisitos Funcionais

Este catálogo refina os dois requisitos de negócio apresentados no desafio: controlar lançamentos
de débito e crédito e disponibilizar o saldo diário consolidado. Os RFs descrevem o comportamento
esperado pelo negócio; ADRs e RNFs registram, respectivamente, as decisões técnicas e os atributos
de qualidade usados para implementá-lo.

| RF | Capacidade | Origem | Owner principal | Quem resolve |
|---|---|---|---|---|
| [RF 001](RF-001-registrar-lancamento.md) | Registrar lançamento de crédito ou débito | Explícito no desafio | Time de Lançamentos | Lançamentos API e PostgreSQL |
| [RF 002](RF-002-consultar-lancamentos.md) | Consultar o histórico de lançamentos do comerciante | Refinamento do controle de lançamentos | Time de Lançamentos | Lançamentos API e PostgreSQL |
| [RF 003](RF-003-atualizar-consolidado-diario.md) | Incorporar lançamentos confirmados ao saldo diário | Derivado do consolidado diário | Time de Consolidado | Pub/Sub, Consolidado Worker e MongoDB |
| [RF 004](RF-004-consultar-consolidado-diario.md) | Consultar o saldo diário consolidado | Explícito no desafio | Time de Consolidado | Consolidado API e MongoDB |


[Voltar aos requisitos](../README.md)
