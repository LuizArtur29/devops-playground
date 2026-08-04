# Arquitetura Atual — DevOps Playground

## 1. Objetivo

Este documento representa a arquitetura atualmente declarada no repositório do projeto.

A antiga VPS não está mais acessível. Portanto, os diagramas abaixo representam o **estado desejado versionado**, e não uma confirmação do estado real que estava executando no servidor.

---

## 2. Visão geral

A plataforma utiliza uma aplicação Spring Boot executada em container Docker.

Em produção, o tráfego seria recebido pelo Nginx, encaminhado para o backend e processado com apoio de PostgreSQL, Redis e RabbitMQ.

Prometheus coleta métricas do backend, enquanto Grafana é utilizado para visualização.

O deploy é automatizado pelo GitHub Actions, que:

1. constrói a imagem;
2. publica a imagem no GHCR;
3. copia as configurações de produção;
4. acessa a VPS por SSH;
5. executa Docker Compose.

---

## 3. Diagrama da arquitetura de produção

```mermaid
flowchart TB
    User[Usuário ou cliente]
    Developer[Desenvolvedor]
    Repository[GitHub Repository]
    Actions[GitHub Actions]
    GHCR[GitHub Container Registry]
    DigitalOcean[DigitalOcean API]

    subgraph VPS[DigitalOcean Droplet]
        direction TB

        subgraph Edge[Camada de entrada]
            Nginx[Nginx<br/>Portas 80 e 443]
        end

        subgraph Application[Camada de aplicação]
            Backend[Help Desk Backend<br/>Spring Boot<br/>Porta 8080 interna]
        end

        subgraph Data[Camada de dados]
            PostgreSQL[(PostgreSQL 16)]
            Redis[(Redis 7.2)]
            RabbitMQ[(RabbitMQ 3.13)]
        end

        subgraph Observability[Observabilidade]
            Prometheus[Prometheus 2.51]
            Grafana[Grafana 10.4<br/>Porta 3000 no host]
        end

        Nginx -->|HTTP interno| Backend
        Backend -->|JDBC| PostgreSQL
        Backend -->|Cache| Redis
        Backend -->|AMQP| RabbitMQ
        Prometheus -->|Scrape /actuator/prometheus| Backend
        Grafana -->|Consulta métricas| Prometheus
    end

    User -->|HTTP ou HTTPS| Nginx
    Developer -->|Push ou Pull Request| Repository
    Repository --> Actions
    Actions -->|Build e push| GHCR
    Actions -->|SCP e SSH como root| VPS
    VPS -->|Pull da imagem latest| GHCR
    DigitalOcean -->|Provisionamento Terraform| VPS
```

---

## 4. Fluxo atual de CI

```mermaid
flowchart LR
    PR[Pull Request para main]
    Paths{Arquivos alterados}
    BackendJob[Build e testes do backend]
    InfraJob[Lint da infraestrutura]
    MavenBuild[Maven package sem testes]
    MavenTests[Maven test]
    Hadolint[Hadolint]
    Actionlint[Actionlint]
    Result[Resultado da CI]

    PR --> Paths

    Paths -->|backend, infra ou workflows| BackendJob
    Paths -->|backend, infra ou workflows| InfraJob

    BackendJob --> MavenBuild
    MavenBuild --> MavenTests

    InfraJob --> Hadolint
    Hadolint --> Actionlint

    MavenTests --> Result
    Actionlint --> Result
```

### Limitações do fluxo

* executa apenas em Pull Requests direcionados à `main`;
* não cobre Pull Requests direcionados à `dev`;
* não valida Terraform;
* não valida Docker Compose;
* não executa análise de cobertura;
* não executa scan de secrets;
* não executa scan de vulnerabilidades;
* não produz SBOM.

---

## 5. Fluxo atual de CD

```mermaid
sequenceDiagram
    actor Developer as Desenvolvedor
    participant Main as Branch main
    participant Actions as GitHub Actions
    participant GHCR as GitHub Container Registry
    participant VPS as DigitalOcean VPS
    participant Compose as Docker Compose
    participant Backend as Backend

    Developer->>Main: Push ou merge
    Main->>Actions: Dispara workflow de CD
    Actions->>Actions: Checkout do código
    Actions->>Actions: Configura Docker Buildx
    Actions->>GHCR: Publica imagem com tag SHA
    Actions->>GHCR: Publica imagem com tag latest
    Actions->>VPS: Copia infra/production via SCP
    Actions->>VPS: Conecta por SSH como root
    Actions->>VPS: Cria arquivo .env
    VPS->>GHCR: docker compose pull
    GHCR-->>VPS: Retorna imagem latest
    VPS->>Compose: docker compose up -d
    Compose->>Backend: Inicia container
    VPS->>VPS: docker image prune -f

    Note over Actions,Backend: Não existe readiness, smoke test ou rollback automatizado
```

---

## 6. Fluxo de requisição

```mermaid
sequenceDiagram
    actor Client as Cliente
    participant Nginx
    participant Backend
    participant PostgreSQL
    participant Redis
    participant RabbitMQ

    Client->>Nginx: HTTPS request
    Nginx->>Backend: Proxy HTTP para backend:8080
    Backend->>PostgreSQL: Consulta ou persistência
    PostgreSQL-->>Backend: Resultado

    opt Operação com cache
        Backend->>Redis: Leitura ou escrita
        Redis-->>Backend: Resultado
    end

    opt Operação assíncrona
        Backend->>RabbitMQ: Publicação de mensagem
        RabbitMQ-->>Backend: Confirmação
    end

    Backend-->>Nginx: Resposta HTTP
    Nginx-->>Client: Resposta HTTPS
```

### Observação

A infraestrutura contém Redis e RabbitMQ, mas a auditoria não confirmou uso funcional desses componentes nas regras de negócio atuais.

---

## 7. Fluxo de métricas

```mermaid
sequenceDiagram
    participant Backend
    participant Actuator
    participant Prometheus
    participant Grafana
    actor Operator as Operador

    Backend->>Actuator: Publica métricas Micrometer
    Prometheus->>Actuator: GET /actuator/prometheus
    Actuator-->>Prometheus: Métricas
    Grafana->>Prometheus: Consulta PromQL
    Prometheus-->>Grafana: Séries temporais
    Operator->>Grafana: Visualiza dashboards
```

### Limitações

* Prometheus não possui volume persistente;
* Grafana não possui provisioning versionado;
* não existem dashboards versionados;
* não existe Alertmanager;
* não existem regras de alerta;
* não existe coleta centralizada de logs;
* não existem métricas do host ou dos containers.

---

## 8. Topologia de rede Docker atual

```mermaid
flowchart TB
    Network[helpdesk-net<br/>Bridge network única]

    Nginx[Nginx]
    Backend[Backend]
    PostgreSQL[PostgreSQL]
    Redis[Redis]
    RabbitMQ[RabbitMQ]
    Prometheus[Prometheus]
    Grafana[Grafana]

    Network --- Nginx
    Network --- Backend
    Network --- PostgreSQL
    Network --- Redis
    Network --- RabbitMQ
    Network --- Prometheus
    Network --- Grafana
```

Todos os serviços compartilham a mesma rede Docker.

Consequentemente:

* Nginx pode alcançar serviços de dados;
* Grafana pode alcançar PostgreSQL, Redis e RabbitMQ;
* Prometheus pode alcançar todos os serviços;
* não existe isolamento por responsabilidade.

A segmentação será tratada em uma Issue posterior.

---

## 9. Persistência atual

```mermaid
flowchart LR
    PostgreSQL[PostgreSQL] --> PostgresVolume[(postgres_data)]
    Redis[Redis] --> RedisVolume[(redis_data)]
    RabbitMQ[RabbitMQ] --> RabbitVolume[(rabbitmq_data)]
    Grafana[Grafana] --> GrafanaVolume[(grafana_data)]
    Prometheus[Prometheus] --> NoVolume[Sem volume persistente]
```

Volumes identificados:

```text
postgres_data
redis_data
rabbitmq_data
grafana_data
```

Não foram identificados:

* backup externo;
* retenção;
* restore;
* teste de restauração;
* persistência do Prometheus.

---

## 10. Provisionamento atual com Terraform

```mermaid
flowchart LR
    Engineer[Engenheiro]
    Terraform[Terraform local]
    State[(terraform.tfstate local)]
    DOAPI[DigitalOcean API]
    SSHKey[SSH Key]
    Droplet[Droplet Ubuntu 24.04]
    Firewall[DigitalOcean Firewall]
    UserData[Cloud-init / user_data]
    Docker[Docker e Compose]

    Engineer --> Terraform
    Terraform --> State
    Terraform --> DOAPI
    DOAPI --> SSHKey
    DOAPI --> Droplet
    DOAPI --> Firewall
    Terraform --> UserData
    UserData --> Docker
    Docker --> Droplet
```

### Limitações

* state local;
* ausência de locking remoto;
* ausência de backend remoto;
* chave pública codificada no Terraform;
* SSH aberto para qualquer origem;
* ausência de usuário dedicado de deploy;
* configuração concentrada em `main.tf`;
* ambiente antigo não pode ser validado.

---

## 11. Estado de saúde atual

```mermaid
flowchart TD
    PostgreSQL[PostgreSQL healthcheck<br/>pg_isready]
    Redis[Redis healthcheck<br/>redis-cli ping]
    RabbitMQ[RabbitMQ healthcheck<br/>rabbitmq-diagnostics ping]
    Backend[Backend healthcheck<br/>Desabilitado]
    Nginx[Nginx healthcheck<br/>Ausente]
    Prometheus[Prometheus healthcheck<br/>Ausente]
    Grafana[Grafana healthcheck<br/>Ausente]

    PostgreSQL --> Backend
    Redis --> Backend
    RabbitMQ --> Backend
    Backend --> Nginx
```

O backend aguarda a saúde de PostgreSQL, Redis e RabbitMQ.

Entretanto:

* o backend não possui healthcheck funcional;
* o Nginx depende apenas da inicialização do container;
* o CD não consulta readiness;
* o deploy pode terminar com a aplicação indisponível.

---

## 12. Diagrama dos principais riscos

```mermaid
flowchart TB
    Deploy[Deploy atual]

    Latest[Uso de tag latest]
    Root[SSH como root]
    NoHealth[Backend sem healthcheck]
    NoSmoke[Sem smoke test]
    NoRollback[Sem rollback]
    NoBackup[Sem backup e restore]
    Secrets[Variáveis podem ficar vazias]
    SingleNetwork[Rede Docker única]
    NoVPS[Antiga VPS indisponível]
    NginxDomain[Erro no domínio HTTPS]

    Deploy --> Latest
    Deploy --> Root
    Deploy --> NoHealth
    Deploy --> NoSmoke
    Deploy --> NoRollback
    Deploy --> Secrets

    NoHealth --> NoRollback
    Latest --> NoRollback
    NoBackup --> NoVPS
    SingleNetwork --> Secrets
    NginxDomain --> Deploy
```

---

## 13. Arquitetura futura desejada após a Fase 0

Este diagrama não representa o estado atual. Ele mostra apenas a direção esperada para a base antes da adoção de Kubernetes.

```mermaid
flowchart TB
    User[Usuário]
    GitHub[GitHub]
    CI[CI com testes, lint e segurança]
    Registry[GHCR<br/>Imagem imutável]
    CD[CD controlado]
    VPS[Nova VPS reproduzível]
    Nginx[Nginx]
    Backend[Backend saudável]
    Data[(PostgreSQL)]
    Cache[(Redis)]
    Queue[(RabbitMQ)]
    Metrics[Prometheus]
    Logs[Loki]
    Dashboards[Grafana]
    Alerts[Alertmanager]
    Backup[Backup externo]
    Terraform[Terraform]
    Provisioning[Cloud-init ou Ansible]

    User --> Nginx
    GitHub --> CI
    CI --> Registry
    Registry --> CD
    CD --> VPS
    Terraform --> VPS
    Provisioning --> VPS

    VPS --> Nginx
    Nginx --> Backend
    Backend --> Data
    Backend --> Cache
    Backend --> Queue

    Metrics --> Backend
    Logs --> Backend
    Dashboards --> Metrics
    Dashboards --> Logs
    Alerts --> Metrics
    Data --> Backup
```

### Capacidades esperadas

* deploy por tag SHA;
* readiness;
* smoke test;
* rollback;
* usuário de deploy não root;
* secrets obrigatórios;
* redes segmentadas;
* Grafana protegido;
* Prometheus persistente;
* logs centralizados;
* alertas;
* backup e restore testados;
* servidor reproduzível;
* documentação operacional.

---

## 14. Conclusão

A arquitetura atual já contém os principais componentes de uma plataforma DevOps inicial.

Entretanto, os componentes ainda estão conectados de forma simples e dependem de processos frágeis.

Os diagramas demonstram que as principais lacunas não estão relacionadas apenas à existência das tecnologias, mas à capacidade de:

* reproduzir;
* validar;
* proteger;
* observar;
* recuperar;
* operar.

A arquitetura atual será utilizada como referência para comparar a evolução ao final da Fase 0.
