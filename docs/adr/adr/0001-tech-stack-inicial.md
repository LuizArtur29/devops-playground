# ADR 0001: Escolha da Stack Base para a Plataforma de Help Desk

## Contexto
Precisamos construir uma plataforma de Help Desk resiliente, com suporte a autenticação robusta, upload de arquivos e cache dinâmico. O foco principal é criar um laboratório DevOps propício para simular cenários de alta disponibilidade, observabilidade e conteinerização avançada.

## Problema
Definir a pilha de tecnologia inicial e os frameworks de backend e banco de dados que permitam desenvolvimento ágil, forte suporte a testes de integração e fácil adaptação para ambientes de contêineres (Docker/Kubernetes).

## Opções Avaliadas
1. **Node.js (NestJS) + MongoDB:** Alta velocidade de desenvolvimento, mas menor rigidez de tipos e ecossistema de observabilidade nativa menos padronizado para ambientes corporativos pesados.
2. **Java 21 + Spring Boot 3.x + PostgreSQL:** Casamento de ecossistema maduro, suporte excelente a padrões de projeto corporativos, gerenciamento de threads nativo refinado (Virtual Threads) e ferramentas consagradas de migração de banco (Flyway).

## Decisão
Adotar **Java 21**, **Spring Boot 3.x** e **PostgreSQL** como banco de dados relacional primário. O ecossistema utilizará **Flyway** para versionamento de schema, **Redis** para caching/rate limiting, **RabbitMQ** para mensageria assíncrona e **MinIO** para armazenamento de anexos de chamados.

## Justificativa e Trade-offs
* **Vantagens:** O Spring Boot possui integração nativa excepcional com o **Spring Actuator** e **OpenTelemetry**, facilitando a exportação de métricas para o Prometheus e Grafana. O uso do Testcontainers para Java tornará nossos testes de integração idênticos ao ambiente produtivo.
* **Desvantagens:** O tempo de inicialização (cold start) da JVM e o consumo inicial de memória são superiores a stacks como Node.js ou Go. No entanto, mitigaremos isso nas fases futuras configurando limites de recursos apropriados (Resource Limits/Requests) no Kubernetes e otimizando o Garbage Collector.