# Fase 0 — Baseline da Infraestrutura Atual

## 1. Informações do documento

| Campo               | Valor                                                            |
| ------------------- | ---------------------------------------------------------------- |
| Projeto             | DevOps Playground — Help Desk                                    |
| Repositório         | `LuizArtur29/devops-playground`                                  |
| Fase                | Fase 0 — Foundation Hardening                                    |
| Tipo                | Auditoria de estado atual                                        |
| Branch da auditoria | `docs/28-docsaudit-document-the-current-infrastructure-baseline` |
| Data da auditoria   | 4 de agosto de 2026                                              |
| Status              | Em andamento                                                     |
| Responsável         | Luiz Arthur                                                      |

---

## 2. Objetivo

Este documento registra o estado atual da aplicação, da infraestrutura, dos pipelines de CI/CD, da observabilidade, da segurança e dos processos operacionais do projeto.

O baseline será utilizado como ponto de partida para as melhorias da Fase 0.

O objetivo desta auditoria não é corrigir imediatamente todos os problemas encontrados, mas:

* inventariar os componentes existentes;
* identificar configurações atuais;
* registrar riscos;
* identificar processos manuais;
* classificar débitos técnicos;
* definir prioridades;
* impedir que alterações futuras escondam problemas já existentes.

---

## 3. Limitações da auditoria

A VPS anteriormente utilizada pelo projeto não está mais acessível.

Por esse motivo, não foi possível verificar diretamente:

* containers que estavam efetivamente em execução;
* imagem e versão realmente implantadas;
* estado dos volumes Docker;
* configuração efetiva do SSH;
* configuração do firewall local;
* certificados instalados;
* tarefas agendadas;
* backups existentes;
* logs do sistema;
* métricas históricas;
* conteúdo e permissões do diretório de produção;
* conteúdo real do arquivo `.env`;
* configurações executadas manualmente na VPS;
* versão instalada do Docker e Docker Compose.

As informações relacionadas ao ambiente de produção representam o **estado desejado versionado no repositório**, e não uma confirmação do estado real que existia na VPS.

A indisponibilidade da antiga VPS também demonstra que o ambiente anterior não era completamente reproduzível a partir do código versionado.

---

## 4. Visão geral do projeto

O projeto consiste em uma plataforma de Help Desk utilizada como laboratório de práticas DevOps.

A aplicação é construída como um monólito modular utilizando Java e Spring Boot.

Os principais módulos identificados são:

```text
modules/
├── user/
├── ticket/
└── storage/
```

O projeto também utiliza ou planeja utilizar:

* PostgreSQL;
* Redis;
* RabbitMQ;
* Flyway;
* Docker;
* Docker Compose;
* Terraform;
* DigitalOcean;
* Nginx;
* GitHub Actions;
* GitHub Container Registry;
* Prometheus;
* Grafana;
* Testcontainers.

---

## 5. Estrutura atual do repositório

A estrutura principal encontrada foi:

```text
devops-playground/
├── .github/
│   └── workflows/
│       ├── cd-main.yml
│       └── pr-validation.yml
├── .idea/
├── backend/
│   ├── .mvn/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   ├── pom.xml
│   ├── mvnw
│   └── .gitignore
├── docs/
│   ├── adr/
│   │   └── adr/
│   │       └── 0001-tech-stack-inicial.md
│   └── Help Desk System Ecosystem-2026-07-08-140747.png
├── infra/
│   ├── docker/
│   │   └── Dockerfile
│   ├── production/
│   │   ├── docker-compose.yml
│   │   ├── nginx/
│   │   │   └── nginx.conf
│   │   └── prometheus/
│   │       └── prometheus.yml
│   └── terraform/
│       ├── main.tf
│       ├── outputs.tf
│       ├── variables.tf
│       ├── terraform.tfstate
│       └── terraform.tfstate.backup
├── scripts/
├── docker-compose.yml
└── devops-playground.iml
```

---

## 6. Organização do repositório

### 6.1 Pontos positivos

A estrutura apresenta separação inicial entre:

* aplicação;
* infraestrutura;
* documentação;
* scripts;
* workflows.

A configuração de produção também está separada da configuração local.

```text
infra/production/
├── docker-compose.yml
├── nginx/
└── prometheus/
```

### 6.2 Problemas encontrados

#### Arquivos da IDE versionados

Os seguintes arquivos do IntelliJ estão rastreados pelo Git:

```text
.idea/.gitignore
.idea/compiler.xml
.idea/encodings.xml
.idea/jarRepositories.xml
.idea/material_theme_project_new.xml
.idea/misc.xml
.idea/terraform.xml
.idea/vcs.xml
devops-playground.iml
```

Esses arquivos podem:

* gerar conflitos;
* criar alterações irrelevantes;
* poluir Pull Requests;
* armazenar preferências pessoais;
* misturar configurações locais com código do projeto.

#### Diretório duplicado de ADR

O ADR atual está localizado em:

```text
docs/adr/adr/0001-tech-stack-inicial.md
```

A estrutura esperada seria:

```text
docs/adr/0001-tech-stack-inicial.md
```

#### Nome do diagrama

O arquivo:

```text
docs/Help Desk System Ecosystem-2026-07-08-140747.png
```

possui:

* espaços no nome;
* timestamp de exportação;
* ausência de diretório específico de arquitetura;
* ausência de arquivo-fonte editável identificado.

#### Diretório de scripts vazio

O diretório:

```text
scripts/
```

existe, mas não possui scripts versionados para:

* deploy;
* rollback;
* smoke tests;
* backup;
* restore;
* validação.

---

## 7. Estratégia de branches atual

Durante a auditoria, a branch ativa era:

```text
docs/28-docsaudit-document-the-current-infrastructure-baseline
```

Branches locais identificadas:

```text
dev
main
feature/cd-pipeline-setup
feature/ci-pipeline-setupp
```

Branches remotas identificadas:

```text
origin/dev
origin/main
```

O fluxo observado utiliza uma branch intermediária `dev`.

Fluxo aparente:

```text
feature/*
    ↓
dev
    ↓
main
```

Esse fluxo ainda não está formalmente documentado.

Também foram encontrados:

* branches locais antigas;
* erro de digitação em `feature/ci-pipeline-setupp`;
* ausência de política documentada de limpeza de branches.

---

## 8. Conventional Commits

O histórico demonstra adoção parcial de Conventional Commits.

Exemplos adequados:

```text
fix(compose): explicitly disable embedded healthcheck for backend
fix(config): move management actuator properties to root level
feat(observability): add prometheus and grafana to production stack
```

Exemplos inconsistentes:

```text
infra: lock down security and enable production secure HTTPS
ci/cd: add infra/production path to workflow triggers
fix: corrigindo erro de digitação
fix: removendo password mockada
```

Problemas identificados:

* `infra` não é um tipo convencional padrão;
* `ci/cd` não é um tipo válido;
* mensagens sem escopo;
* mistura entre português e inglês;
* descrições genéricas;
* ausência de validação automática de commits.

---

## 9. Arquivos ignorados pelo Git

### 9.1 Estado atual

Não existe um `.gitignore` na raiz do repositório.

As regras relacionadas ao Terraform foram colocadas incorretamente em:

```text
.idea/.gitignore
```

Esse arquivo só controla conteúdo dentro do diretório `.idea`.

Por isso, os seguintes arquivos aparecem como não rastreados:

```text
infra/terraform/.terraform/
infra/terraform/.terraform.lock.hcl
infra/terraform/terraform.tfstate
infra/terraform/terraform.tfstate.backup
```

### 9.2 Risco

Um comando como:

```bash
git add .
```

pode adicionar acidentalmente:

* state do Terraform;
* backup do state;
* binários dos providers;
* arquivos de variáveis;
* informações potencialmente sensíveis.

### 9.3 Lock file

O arquivo:

```text
.terraform.lock.hcl
```

está sendo ignorado pela regra atual.

Isso está incorreto.

O lock file deve ser versionado para garantir:

* versão do provider;
* checksums;
* maior reprodução do ambiente.

---

## 10. Ambiente local com Docker Compose

O Compose local foi validado com:

```bash
docker compose config --quiet
```

A configuração é sintaticamente válida.

Foi apresentado apenas o aviso:

```text
the attribute `version` is obsolete
```

### 10.1 Serviços locais

```text
postgres-db
redis-cache
rabbitmq-broker
helpdesk-api
```

### 10.2 Imagens locais

```text
postgres:16-alpine
redis:7.2-alpine
rabbitmq:3.13-management-alpine
helpdesk-api:1.0.0
```

### 10.3 Portas publicadas

| Serviço             | Porta |
| ------------------- | ----: |
| PostgreSQL          |  5432 |
| Redis               |  6379 |
| RabbitMQ            |  5672 |
| RabbitMQ Management | 15672 |
| Backend             |  8080 |

### 10.4 Credenciais locais

O Compose contém credenciais fixas:

```text
PostgreSQL: postgres/postgres
RabbitMQ: guest/guest
```

Essas credenciais podem ser aceitas apenas para desenvolvimento local.

Entretanto, ainda não existe:

* `.env.example`;
* documentação formal do ambiente;
* separação por profiles;
* proteção contra uso dessas credenciais fora do ambiente local.

### 10.5 Volumes locais

```text
postgres_data
redis_data
```

RabbitMQ não possui volume local configurado.

A remoção do container pode eliminar:

* filas;
* exchanges;
* mensagens;
* configurações.

### 10.6 Rede local

Todos os serviços compartilham:

```text
helpdesk-network
```

### 10.7 Configuração de RabbitMQ no backend

O backend depende da saúde do RabbitMQ, porém não recebe explicitamente:

```text
SPRING_RABBITMQ_HOST
SPRING_RABBITMQ_PORT
SPRING_RABBITMQ_USERNAME
SPRING_RABBITMQ_PASSWORD
```

Dentro do container, o valor padrão `localhost` aponta para o próprio backend e não para o container RabbitMQ.

### 10.8 Caminho do Dockerfile

O Compose utiliza:

```yaml
build:
  context: backend
  dockerfile: infra/docker/Dockerfile
```

O caminho do Dockerfile é interpretado em relação ao build context.

Isso pode fazer o Docker procurar:

```text
backend/infra/docker/Dockerfile
```

enquanto o arquivo real está em:

```text
infra/docker/Dockerfile
```

A validação com `docker compose config` não executa o build, portanto esse problema ainda precisa ser confirmado com um build real.

---

## 11. Ambiente de produção com Docker Compose

O Compose de produção foi validado com variáveis fictícias.

A configuração é sintaticamente válida.

### 11.1 Serviços de produção

```text
postgres
redis
rabbitmq
backend
nginx
prometheus
grafana
```

### 11.2 Imagens de produção

| Serviço    | Imagem                            |
| ---------- | --------------------------------- |
| PostgreSQL | `postgres:16-alpine`              |
| Redis      | `redis:7.2-alpine`                |
| RabbitMQ   | `rabbitmq:3.13-management-alpine` |
| Backend    | `${BACKEND_IMAGE}:latest`         |
| Nginx      | `nginx:1.25-alpine`               |
| Prometheus | `prom/prometheus:v2.51.0`         |
| Grafana    | `grafana/grafana:10.4.0`          |

### 11.3 Variáveis utilizadas

```text
BACKEND_IMAGE
DB_USERNAME
DB_PASSWORD
RABBIT_USER
RABBIT_PASSWORD
GRAFANA_PASSWORD
```

### 11.4 Variáveis não obrigatórias

Quando as variáveis não são fornecidas, o Docker Compose apenas apresenta avisos e utiliza strings vazias.

Isso permite que uma configuração inválida avance com:

* nome de imagem vazio;
* usuário do banco vazio;
* senha do banco vazia;
* credenciais do RabbitMQ vazias;
* senha do Grafana vazia.

As variáveis deveriam futuramente usar validação explícita.

Exemplo:

```yaml
${DB_PASSWORD:?DB_PASSWORD is required}
```

### 11.5 Volumes de produção

```text
postgres_data
redis_data
rabbitmq_data
grafana_data
```

O Prometheus não possui volume persistente.

### 11.6 Rede de produção

Todos os serviços compartilham:

```text
helpdesk-net
```

Não existe segmentação entre:

* entrada;
* aplicação;
* dados;
* observabilidade.

### 11.7 Exposição de portas

| Serviço    | Porta | Estado             |
| ---------- | ----: | ------------------ |
| Nginx      |    80 | Pública            |
| Nginx      |   443 | Pública            |
| Grafana    |  3000 | Publicada no host  |
| Backend    |  8080 | Apenas rede Docker |
| PostgreSQL |  5432 | Apenas rede Docker |
| Redis      |  6379 | Apenas rede Docker |
| RabbitMQ   |  5672 | Apenas rede Docker |
| Prometheus |  9090 | Apenas rede Docker |

---

## 12. Dockerfile

### 12.1 Pontos positivos

O Dockerfile utiliza:

* multi-stage build;
* Maven;
* Java 21;
* runtime distroless;
* imagem fixada por digest;
* usuário não root;
* ownership adequado do JAR;
* `ENTRYPOINT` em formato exec.

### 12.2 Healthcheck inválido

O Dockerfile declara:

```dockerfile
HEALTHCHECK CMD ["java", "-jar", "/app/app.jar", "--health"]
```

Esse comando não verifica a aplicação já em execução.

Ele tenta iniciar outra instância da aplicação.

Por esse motivo, o Compose de produção desabilita o healthcheck:

```yaml
healthcheck:
  disable: true
```

Consequências:

* backend sem status de saúde;
* Nginx não aguarda readiness;
* CD não consegue validar o serviço;
* rollback automatizado não possui sinal confiável;
* `docker compose ps` não indica saúde real.

---

## 13. Nginx

### 13.1 Funções atuais

O Nginx é responsável por:

* receber tráfego HTTP;
* redirecionar HTTP para HTTPS;
* responder ao desafio ACME;
* terminar TLS;
* encaminhar requisições para o backend.

### 13.2 Erro de domínio

O bloco HTTP utiliza:

```text
meuhelpdesk.software
```

O bloco HTTPS e os certificados utilizam:

```text
meulhelpdesk.software
```

Existe um caractere `l` adicional no domínio HTTPS.

Isso pode causar:

* certificado não encontrado;
* falha na inicialização do Nginx;
* erro de TLS;
* domínio sem bloco HTTPS correspondente;
* redirecionamento para configuração incorreta.

### 13.3 Lacunas

Não foram identificados:

* headers de segurança;
* HSTS;
* rate limiting;
* timeouts de proxy;
* healthcheck;
* ocultação de versão;
* logs estruturados;
* configuração explícita de upload.

A aplicação aceita arquivos de até 10 MB, mas o Nginx não declara:

```nginx
client_max_body_size
```

---

## 14. Prometheus

A configuração atual possui:

```text
scrape_interval: 15s
evaluation_interval: 15s
```

O target configurado é:

```text
backend:8080/actuator/prometheus
```

### Pontos positivos

* Prometheus está integrado ao backend;
* target usa rede interna;
* endpoint Micrometer está configurado.

### Lacunas

Não existem:

* volume persistente;
* retenção explícita;
* regras de alerta;
* Alertmanager;
* Node Exporter;
* cAdvisor;
* exporters para PostgreSQL, Redis ou RabbitMQ;
* métricas de host;
* SLI;
* SLO.

---

## 15. Grafana

O Grafana utiliza:

```text
grafana/grafana:10.4.0
```

e possui volume persistente.

### Problemas

* porta 3000 publicada no host;
* ausência de datasource versionado;
* ausência de dashboards versionados;
* ausência de provisioning;
* ausência de alertas versionados;
* dependência de configurações manuais;
* senha exigida pelo Compose, mas não escrita pelo CD.

---

## 16. Configuração da aplicação

### 16.1 Aplicação

```text
spring.application.name: helpdesk-api
```

### 16.2 Java

Virtual threads estão habilitadas.

### 16.3 Banco de dados

A aplicação utiliza PostgreSQL.

Valores padrão:

```text
host: localhost
port: 5432
database: helpdesk_db
user: postgres
password: postgres
```

Esses valores facilitam desenvolvimento local, mas não deveriam ser usados como fallback de produção.

### 16.4 HikariCP

```text
maximum-pool-size: 25
minimum-idle: 5
```

A configuração não possui justificativa de carga registrada.

A antiga Droplet tinha configuração pequena, com 1 vCPU e 2 GB de RAM.

O tamanho do pool deve futuramente ser validado por medição.

### 16.5 Flyway

Flyway está habilitado com:

```text
baseline-on-migrate: true
```

Migration atual:

```text
V1__create_initial_schema.sql
```

### 16.6 Upload

```text
max-file-size: 10MB
max-request-size: 10MB
```

### 16.7 Actuator

Endpoints configurados:

```text
health
info
metrics
prometheus
```

O health está configurado com:

```text
show-details: always
```

Isso pode expor informações internas da aplicação.

O Nginx encaminha qualquer rota ao backend, então os endpoints podem ficar publicamente acessíveis.

### 16.8 Open Session in View

O build apresentou o aviso de que:

```text
spring.jpa.open-in-view
```

está habilitado por padrão.

Para uma API REST, isso pode permitir consultas durante a serialização da resposta e esconder problemas de acesso lazy.

---

## 17. Dependências Maven

### 17.1 Configuração principal

```text
Java: 21
Spring Boot: 4.1.0
Versão da aplicação: 0.0.1-SNAPSHOT
```

### 17.2 Dependências principais

* Spring Boot Actuator;
* Spring Data JPA;
* Validation;
* Spring MVC;
* PostgreSQL;
* Lombok;
* Micrometer Prometheus;
* Testcontainers;
* PostgreSQL Testcontainer;
* RabbitMQ Testcontainer.

### 17.3 Dependência duplicada

O `spring-boot-starter-actuator` está declarado duas vezes.

### 17.4 Metadados incompletos

O POM contém campos vazios:

* URL;
* licença;
* desenvolvedores;
* SCM.

Também utiliza valores genéricos:

```text
artifactId: backend
name: backend
description: backend
```

### 17.5 Ausências

Não estão configurados:

* JaCoCo;
* Checkstyle;
* SpotBugs;
* Spotless;
* Maven Enforcer;
* OWASP Dependency Check;
* quality gate;
* separação formal entre testes unitários e integração.

---

## 18. Testes

O comando:

```bash
./mvnw clean verify
```

foi executado com sucesso.

Resultado:

```text
Tests run: 1
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

### 18.1 Teste existente

O único teste identificado é:

```text
HelpDeskApplicationTests.contextLoads
```

Ele verifica apenas se o contexto Spring inicializa.

### 18.2 Testcontainers

São inicializados:

* PostgreSQL;
* Redis;
* RabbitMQ.

O PostgreSQL utiliza `@ServiceConnection`.

Redis e RabbitMQ utilizam propriedades dinâmicas.

### 18.3 Limitações

Não foram identificados testes para:

* criação de usuários;
* duplicidade de e-mail;
* criação de chamados;
* transições de status;
* controllers;
* services;
* repositories;
* exceptions;
* segurança;
* Redis funcional;
* RabbitMQ funcional;
* upload;
* contrato;
* carga;
* smoke test;
* cobertura.

O build verde demonstra que a aplicação inicializa, mas não comprova o funcionamento das regras de negócio.

---

## 19. Migration do banco

A migration cria:

* usuários;
* chamados;
* comentários;
* histórico de alterações;
* constraints;
* índices.

### 19.1 UUID

A migration habilita:

```sql
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
```

mas utiliza:

```sql
gen_random_uuid()
```

Existe uma inconsistência entre a extensão habilitada e a função usada.

### 19.2 Índice redundante

A coluna `email` é declarada como `UNIQUE`, o que já cria um índice único.

A migration também cria:

```sql
CREATE INDEX idx_users_email ON users(email);
```

Esse índice pode ser redundante.

### 19.3 `updated_at`

O campo recebe:

```text
DEFAULT CURRENT_TIMESTAMP
```

Isso define o valor inicial, mas não garante atualização automática em alterações posteriores.

### 19.4 Imutabilidade

Como a migration pode já ter sido aplicada, ela não deve ser alterada diretamente sem avaliar o histórico.

Correções devem ser realizadas por uma nova migration.

---

## 20. ADR atual

O ADR existente documenta:

* contexto;
* problema;
* opções;
* decisão;
* vantagens;
* desvantagens;
* trade-offs.

### 20.1 Divergência de versão

O ADR define:

```text
Spring Boot 3.x
```

A implementação utiliza:

```text
Spring Boot 4.1.0
```

### 20.2 Componentes futuros

O ADR apresenta como parte da decisão:

* Redis;
* RabbitMQ;
* MinIO.

Estado atual:

* Redis está presente na infraestrutura;
* RabbitMQ está presente na infraestrutura;
* uso funcional de Redis não foi comprovado;
* uso funcional de RabbitMQ não foi comprovado;
* MinIO não está implementado.

O ADR não diferencia claramente decisão arquitetural, implementação atual e planejamento futuro.

---

## 21. CI atual

O workflow de Pull Request possui dois jobs:

```text
validate-backend
validate-infra
```

### 21.1 Validações implementadas

* checkout;
* Java 21;
* cache Maven;
* build sem testes;
* execução de testes;
* Hadolint;
* Actionlint.

### 21.2 Gatilho

A CI executa em Pull Requests destinados apenas à branch:

```text
main
```

Entretanto, o fluxo atual utiliza `dev`.

Se as branches de feature forem integradas primeiro em `dev`, a CI não será executada nesses Pull Requests.

### 21.3 Lacunas

A CI não executa:

* `terraform fmt -check`;
* `terraform validate`;
* TFLint;
* Checkov;
* `docker compose config`;
* JaCoCo;
* análise estática Java;
* scan de secrets;
* scan de vulnerabilidades;
* CodeQL;
* Trivy;
* geração de SBOM.

### 21.4 Build Maven duplicado

O workflow executa:

```bash
./mvnw clean package -DskipTests
```

e depois:

```bash
./mvnw test
```

Parte do ciclo Maven é executada mais de uma vez.

---

## 22. CD atual

O CD é executado após push na branch:

```text
main
```

### 22.1 Fluxo atual

```text
push em main
    ↓
build da imagem
    ↓
publicação no GHCR
    ↓
cópia da configuração para VPS
    ↓
SSH na VPS
    ↓
criação do .env
    ↓
docker compose pull
    ↓
docker compose up
    ↓
docker image prune
```

### 22.2 Tags publicadas

O workflow publica:

* tag baseada no SHA;
* tag `latest`.

### 22.3 Tag implantada

O Compose utiliza:

```text
${BACKEND_IMAGE}:latest
```

Portanto, a tag imutável gerada não é utilizada em produção.

### 22.4 Usuário de deploy

SCP e SSH utilizam:

```text
root
```

### 22.5 Secrets gravados

O workflow grava:

```text
BACKEND_IMAGE
DB_USERNAME
DB_PASSWORD
RABBIT_USER
RABBIT_PASSWORD
```

O Compose também exige:

```text
GRAFANA_PASSWORD
```

Essa variável não é escrita no `.env`.

### 22.6 Lacunas

Não existem:

* GitHub Environment;
* approval gate;
* usuário dedicado de deploy;
* `chmod 600` no `.env`;
* controle de concorrência;
* timeout;
* readiness;
* smoke test;
* rollback;
* verificação de HTTPS;
* validação da versão implantada;
* preservação explícita da imagem anterior.

### 22.7 Limpeza de imagens

O workflow executa:

```bash
docker image prune -f
```

imediatamente após o deploy.

Isso pode dificultar rollback e remover recursos úteis para recuperação.

---

## 23. Terraform

### 23.1 Recursos atuais

O Terraform declara:

* provider DigitalOcean;
* chave SSH;
* Droplet Ubuntu 24.04;
* firewall;
* instalação inicial do Docker via `user_data`.

### 23.2 Validação

Os comandos:

```bash
terraform init -backend=false
terraform validate
```

foram executados com sucesso.

Resultado:

```text
Success! The configuration is valid.
```

### 23.3 Formatação

O comando:

```bash
terraform fmt -check -diff -recursive
```

falhou.

Arquivos afetados:

```text
main.tf
variables.tf
outputs.tf
```

Também foram identificados arquivos sem nova linha no final.

### 23.4 Provider

A configuração permite:

```text
digitalocean/digitalocean ~> 2.0
```

O lock file local utiliza:

```text
v2.95.0
```

### 23.5 State

O projeto utiliza state local:

```text
terraform.tfstate
terraform.tfstate.backup
```

O state ainda referencia o provider DigitalOcean.

Como a VPS não está mais acessível, o state pode representar recursos:

* inexistentes;
* inacessíveis;
* divergentes;
* ainda ativos em outra conta.

Nenhum `plan`, `apply`, `destroy` ou `refresh` deve ser executado sem uma decisão sobre esse state.

### 23.6 Riscos

* SSH aberto para qualquer endereço;
* deploy como root;
* chave pública pessoal codificada;
* uso de `apt-key`;
* ausência de usuário dedicado;
* ausência de hardening;
* ausência de remote state;
* ausência de locking;
* ausência de módulos;
* recursos concentrados em `main.tf`;
* erro de digitação em `heldesk_fw`.

---

## 24. Backup e restore

### Estado identificado

PostgreSQL utiliza volume Docker.

Não foram encontrados:

* scripts de `pg_dump`;
* armazenamento externo;
* criptografia;
* política de retenção;
* restore;
* teste de restauração;
* checksum;
* RPO;
* RTO;
* runbook.

### Conclusão

O projeto não possui uma estratégia comprovada de backup.

Um volume Docker não deve ser considerado backup.

---

## 25. Observabilidade

### Implementado

* Spring Boot Actuator;
* Micrometer;
* Prometheus;
* Grafana;
* endpoint `/actuator/prometheus`.

### Não implementado

* Loki;
* Alloy;
* Alertmanager;
* Node Exporter;
* cAdvisor;
* dashboards como código;
* datasource como código;
* alertas;
* logs centralizados;
* retenção de métricas;
* SLI;
* SLO;
* métricas da infraestrutura.

---

## 26. Inventário de variáveis e secrets

### Variáveis locais

```text
DB_HOST
DB_PORT
DB_NAME
DB_USER
DB_PASSWORD
SPRING_DATA_REDIS_HOST
SPRING_DATA_REDIS_PORT
```

### Variáveis de produção

```text
BACKEND_IMAGE
DB_USERNAME
DB_PASSWORD
RABBIT_USER
RABBIT_PASSWORD
GRAFANA_PASSWORD
```

### Secrets do GitHub identificados

```text
DROPLET_IP
SSH_PRIVATE_KEY
DB_USERNAME
DB_PASSWORD
RABBIT_USER
RABBIT_PASSWORD
GITHUB_TOKEN
```

Possível secret ausente:

```text
GRAFANA_PASSWORD
```

Nenhum valor real foi incluído nesta auditoria.

---

## 27. Matriz de riscos

| ID    | Risco                                             | Severidade |
| ----- | ------------------------------------------------- | ---------: |
| R-001 | Produção utiliza a tag mutável `latest`           |    Crítica |
| R-002 | Backend não possui healthcheck real               |    Crítica |
| R-003 | Não existe rollback automatizado                  |    Crítica |
| R-004 | Não existe backup externo e restore testado       |    Crítica |
| R-005 | Domínio HTTPS do Nginx possui erro de digitação   |    Crítica |
| R-006 | Deploy é executado como root                      |       Alta |
| R-007 | SSH está aberto para qualquer origem              |       Alta |
| R-008 | Variáveis de produção aceitam valores vazios      |       Alta |
| R-009 | `GRAFANA_PASSWORD` não é escrito pelo CD          |       Alta |
| R-010 | Não existe smoke test pós-deploy                  |       Alta |
| R-011 | CI não executa em PR destinado à branch `dev`     |       Alta |
| R-012 | State do Terraform não é ignorado na raiz         |       Alta |
| R-013 | Antiga VPS não é reproduzível de forma comprovada |       Alta |
| R-014 | Grafana publica a porta 3000 no host              |       Alta |
| R-015 | Actuator utiliza `show-details: always`           |       Alta |
| R-016 | Prometheus não possui persistência                |      Média |
| R-017 | Todos os serviços usam uma única rede Docker      |      Média |
| R-018 | Arquivos do IntelliJ estão versionados            |      Média |
| R-019 | ADR utiliza Spring Boot 3.x e código usa 4.1.0    |      Média |
| R-020 | Índice de e-mail possivelmente redundante         |      Média |
| R-021 | UUID extension e função estão inconsistentes      |      Média |
| R-022 | Pool Hikari não possui justificativa de carga     |      Média |
| R-023 | RabbitMQ local não está configurado no backend    |       Alta |
| R-024 | Caminho do Dockerfile local pode estar inválido   |       Alta |
| R-025 | Terraform não passa em `terraform fmt -check`     |      Baixa |
| R-026 | Compose local utiliza atributo `version` obsoleto |      Baixa |
| R-027 | Dependência Actuator duplicada                    |      Baixa |
| R-028 | Apenas um teste de carregamento de contexto       |       Alta |
| R-029 | Mockito utiliza agent dinâmico com aviso futuro   |      Baixa |
| R-030 | Não existe `.gitignore` na raiz                   |       Alta |

---

## 28. Priorização

### P0 — Crítico

* corrigir domínio do Nginx;
* implantar tag imutável;
* implementar healthcheck e readiness;
* implementar smoke test;
* implementar rollback;
* implementar backup e restore;
* corrigir proteção de arquivos Terraform.

### P1 — Alta prioridade

* remover deploy como root;
* restringir SSH;
* tornar variáveis obrigatórias;
* corrigir secret do Grafana;
* proteger Grafana;
* ajustar CI para o fluxo real;
* adicionar testes funcionais;
* criar `.gitignore` na raiz;
* tornar o ambiente reproduzível.

### P2 — Fundação operacional

* segmentar redes;
* provisionar Grafana como código;
* persistir Prometheus;
* adicionar logs centralizados;
* criar alertas;
* estruturar Terraform;
* definir remote state;
* revisar Actuator;
* padronizar commits e branches.

### P3 — Maturidade

* SLI e SLO;
* SBOM;
* assinatura de imagem;
* testes de disaster recovery;
* releases automatizadas;
* métricas de negócio.

---

## 29. Avaliação geral

O projeto já possui uma fundação relevante para um laboratório DevOps:

* aplicação containerizada;
* imagem distroless;
* usuário não root;
* infraestrutura declarada em Terraform;
* pipeline de CI;
* pipeline de CD;
* Nginx;
* HTTPS;
* Prometheus;
* Grafana;
* Testcontainers;
* Flyway;
* PostgreSQL;
* Redis;
* RabbitMQ.

Entretanto, o ambiente ainda não possui as garantias necessárias para avançar com segurança para Kubernetes, Helm e GitOps.

As principais lacunas estão relacionadas a:

* reprodução;
* segurança;
* versionamento;
* healthchecks;
* rollback;
* backup;
* testes;
* observabilidade;
* governança.

---

## 30. Conclusão

O baseline confirma que a Fase 0 é necessária antes da evolução para uma plataforma orquestrada.

A infraestrutura atual deve ser fortalecida até que seja possível:

1. criar um novo ambiente do zero;
2. implantar uma versão imutável;
3. validar automaticamente o deploy;
4. detectar falhas;
5. executar rollback;
6. restaurar dados;
7. observar aplicação e infraestrutura;
8. operar sem depender da antiga VPS;
9. compreender todas as decisões pela documentação.

Este documento deve ser atualizado apenas para corrigir informações da auditoria ou adicionar evidências que ainda estejam dentro do escopo da Issue.

As correções técnicas identificadas devem ser implementadas em Issues separadas.
