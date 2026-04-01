# Shorten Backend

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3-green)
![Redis](https://img.shields.io/badge/Redis-Cache-red)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED)
![AWS](https://img.shields.io/badge/AWS-Cloud-FF9900)
![Terraform](https://img.shields.io/badge/Terraform-IaC-7B42BC)
![Ansible](https://img.shields.io/badge/Ansible-Config-EE0000)

API REST para encurtamento de URLs com autenticação, URLs personalizadas, analytics em tempo real e deploy automatizado na AWS.

> 🔭 [Visualizar arquitetura interativa](https://guibsantos.github.io/Shorten-backend/)

---

## ✨ Funcionalidades

- 🔗 Encurtamento com slugs personalizados
- 👤 Autenticação via JWT e Google OAuth
- 📊 Analytics de acessos em tempo real
- 📧 Notificações por e-mail assíncronas (RabbitMQ)
- ⚡ Cache de alta performance com Redis (Cache-Aside)
- 🛡️ Rate limiting por IP
- 🖼️ Upload de foto de perfil
- 🌐 Deploy automatizado em domínio próprio

---

## 🏗️ Arquitetura

| Camada | Tecnologia |
|---|---|
| API | Java 21, Spring Boot 3 |
| Banco de dados | PostgreSQL 16 + Flyway |
| Cache | Redis 7 (Cache-Aside) |
| Mensageria | RabbitMQ |
| Auth | Spring Security + JWT + Google OAuth |
| DevOps | Docker, Terraform, Ansible, AWS EC2 |
| Documentação | Swagger / OpenAPI |
| Load test | k6 |

---

## 🚀 Rodando localmente

### Pré-requisitos
- Docker e Docker Compose
- Arquivo `.env` configurado (veja `.env.example`)
```bash
git clone https://github.com/GuiBSantos/Shorten-backend.git
cd Shorten-backend
cp .env.example .env
docker compose up --build
```

Documentação disponível em:
```
http://localhost:8080/swagger-ui/index.html
```

---

## ☁️ Deploy na AWS

### 1. Provisionar infraestrutura (Terraform)
```bash
cd infra/terraform
terraform init
terraform apply -auto-approve
```

### 2. Configurar servidor (Ansible)
```bash
cd infra/ansible
ansible-playbook -i hosts playbook.yml
```

---

## 📁 Variáveis de Ambiente

Crie um `.env` baseado no `.env.example`:
```env
# Postgres
POSTGRES_USERNAME=
POSTGRES_PASSWORD=
POSTGRES_DB=

# Redis
REDIS_PASSWORD=

# RabbitMQ
SPRING_RABBITMQ_USERNAME=
SPRING_RABBITMQ_PASSWORD=

# Google OAuth
TOKEN=
GOOGLE_ID=

# Mail
MAIL_USER=
MAIL_PASSWORD=
```

---

## 📦 Estrutura
```
src/main/java/com/guibsantos/shorterURL/
├── config/          # RabbitMQ, Redis, Swagger, WebMvc
├── controller/      # Auth, Url, User + docs Swagger
├── dto/             # Request e Response objects
├── email/           # EmailConsumer, EmailProducer
├── entity/          # UrlEntity, UserEntity, Role
├── exception/       # GlobalExceptionHandler
├── job/             # CleanupJob (URLs expiradas)
├── repository/      # UrlRepository, UserRepository
├── security/        # SecurityConfig, SecurityFilter, TokenService
│   └── filter/      # RateLimitFilter
└── service/         # AuthService, UrlService, UserService...
```
