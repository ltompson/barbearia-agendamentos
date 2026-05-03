<div align="center">

# 💈 Alquimista Barbearia

**Sistema completo de agendamentos para barbearia**  
Aplicação full-stack com autenticação JWT, painel administrativo e integração WhatsApp

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.6-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-21-DD0031?style=for-the-badge&logo=angular&logoColor=white)](https://angular.io/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/JWT-Auth-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)](https://jwt.io/)

</div>

---

## 📋 Índice

- [Sobre o Projeto](#-sobre-o-projeto)
- [Funcionalidades](#-funcionalidades)
- [Tecnologias](#-tecnologias)
- [Arquitetura](#-arquitetura)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Banco de Dados](#-banco-de-dados)
- [API REST](#-api-rest)
- [Como Executar](#-como-executar)
- [Variáveis de Ambiente](#-variáveis-de-ambiente)
- [Próximos Passos](#-próximos-passos)
- [Autor](#-autor)

---

## 💡 Sobre o Projeto

O **Alquimista Barbearia** é um sistema de agendamentos desenvolvido para uma barbearia real, com o objetivo de substituir agendamentos feitos por mensagem e otimizar a gestão da agenda dos barbeiros.

O projeto nasceu como solução prática para um problema real e evoluiu para uma aplicação full-stack robusta, abrangendo desde a experiência do cliente até o controle administrativo completo.

> ⚙️ Desenvolvido do zero por um desenvolvedor solo, cobrindo backend, frontend, modelagem de banco e regras de negócio.

---

## ✨ Funcionalidades

### 👤 Área do Cliente (pública)
- Agendamento online sem necessidade de login
- Seleção de barbeiro, serviço, data e horário
- Horários organizados por período: **Manhã / Tarde / Noite**
- Calendário inteligente que bloqueia fins de semana (com exceções liberadas pelo admin)
- Horários indisponíveis ocultados automaticamente em tempo real
- **Reagendamento** via link direto (sem novo cadastro)
- Confirmação automática por **WhatsApp** após agendar ou reagendar

### 🔐 Área Administrativa (protegida por JWT)
- Login seguro com token JWT (expiração 24h)
- Visualização de todos os agendamentos com filtro por data
- Cancelamento de agendamentos
- Envio de **link de reagendamento** direto para o WhatsApp do cliente
- Bloqueio de horários específicos por barbeiro
- Liberação de fins de semana para datas pontuais
- Notificação automática no WhatsApp do admin ao reagendar
- Interface responsiva com menu hambúrguer animado no mobile

### 🎨 UX / Interface
- Tema claro e escuro com alternância dinâmica
- Layout 100% responsivo (mobile-first)
- Feedback visual de carregamento e erros

---

## 🛠 Tecnologias

### Backend
| Tecnologia | Uso |
|---|---|
| Java 21 | Linguagem principal |
| Spring Boot 4.0.6 | Framework web |
| Spring Security | Controle de acesso e autenticação |
| JWT (JSON Web Token) | Autenticação stateless |
| Hibernate / JPA | ORM e persistência |
| PostgreSQL 16 | Banco de dados relacional |
| Maven | Gerenciamento de dependências |

### Frontend
| Tecnologia | Uso |
|---|---|
| Angular 21 | Framework SPA |
| Angular Material | Componentes de UI |
| TypeScript | Tipagem estática |
| RxJS | Programação reativa |
| CSS3 | Estilização e responsividade |

---

## 🏗 Arquitetura

```
┌─────────────────────────────────────────────────────┐
│                  CLIENTE (Browser)                   │
│              Angular 21 — porta 4200                 │
│   Guards │ Interceptors │ Services │ Components      │
└────────────────────────┬────────────────────────────┘
                         │ HTTP + Bearer JWT
                         ▼
┌─────────────────────────────────────────────────────┐
│              BACKEND — Spring Boot                   │
│                    porta 8080                        │
│  Controllers → Services → Repositories → Entities   │
│         Spring Security + JWT Filter                 │
└────────────────────────┬────────────────────────────┘
                         │ JPA / Hibernate
                         ▼
┌─────────────────────────────────────────────────────┐
│              PostgreSQL — barbearia_db               │
│  clientes │ barbeiros │ servicos │ agendamentos      │
│  bloqueios │ dias_disponiveis │ admins               │
└─────────────────────────────────────────────────────┘
```

**Fluxo de autenticação:**
```
Login (POST /api/auth/login)
    → Valida credenciais → Gera JWT
    → Frontend armazena token
    → Interceptor injeta Bearer em todas as requisições protegidas
    → JwtFilter valida token a cada request
```

---

## 📁 Estrutura do Projeto

```
alquimista-barbearia/
│
├── backend/
│   └── src/main/java/com/barbearia/agendamentos/
│       ├── controller/          # Endpoints REST
│       │   ├── AgendamentoController.java
│       │   ├── AuthController.java
│       │   ├── BloqueioController.java
│       │   └── DiaDisponivelController.java
│       ├── service/             # Regras de negócio
│       │   ├── AgendamentoService.java
│       │   ├── BloqueioService.java
│       │   └── DiaDisponivelService.java
│       ├── repository/          # Acesso ao banco (Spring Data JPA)
│       ├── entity/              # Entidades JPA
│       ├── config/
│       │   ├── SecurityConfig.java   # Rotas públicas e protegidas
│       │   ├── JwtService.java       # Geração e validação do JWT
│       │   └── JwtFilter.java        # Interceptor de requisições
│       └── resources/
│           └── application.yml
│
└── frontend/
    └── src/app/
        ├── agendamento/         # Tela pública de agendamento
        ├── admin/               # Painel administrativo
        ├── login/               # Autenticação
        ├── services/            # Comunicação com a API
        │   ├── agendamento.service.ts
        │   ├── auth.service.ts
        │   ├── bloqueio.service.ts
        │   ├── dia-disponivel.service.ts
        │   └── theme.service.ts
        ├── guards/
        │   └── auth.guard.ts    # Proteção de rotas admin
        └── interceptors/
            └── auth.interceptor.ts  # Injeção de JWT
```

---

## 🗄 Banco de Dados

```sql
-- Principais tabelas e relacionamentos

clientes        (id, nome, telefone)
barbeiros       (id, nome)
servicos        (id, nome, duracao_minutos, preco)

agendamentos    (id, cliente_id, barbeiro_id, servico_id,
                 data_hora, status)
                 -- status: 'AGENDADO' | 'CANCELADO'

bloqueios       (id, barbeiro_id, data, hora_inicio, hora_fim)
                 -- bloqueia horários específicos

dias_disponiveis (id, barbeiro_id, data)
                 -- libera fins de semana pontuais

admins          (id, username, password)
                 -- senha armazenada com BCrypt
```

**Regras de negócio do agendamento:**
- Expediente: **08:00 às 18:30**, slots de **30 em 30 minutos**
- Fins de semana bloqueados por padrão (liberáveis individualmente)
- Horários ocupados ou bloqueados são excluídos automaticamente da listagem
- Reagendamento cancela o horário anterior e cria um novo atomicamente

---

## 🔌 API REST

### Autenticação
| Método | Endpoint | Acesso | Descrição |
|---|---|---|---|
| POST | `/api/auth/login` | Público | Login e geração do JWT |

### Agendamentos
| Método | Endpoint | Acesso | Descrição |
|---|---|---|---|
| GET | `/api/agendamentos` | Admin | Lista todos os agendamentos |
| POST | `/api/agendamentos` | Público | Cria novo agendamento |
| GET | `/api/agendamentos/{id}` | Público | Busca agendamento por ID |
| PATCH | `/api/agendamentos/{id}/cancelar` | Admin | Cancela agendamento |
| POST | `/api/agendamentos/{id}/reagendar` | Público | Reagenda horário |
| GET | `/api/agendamentos/disponiveis` | Público | Horários livres por barbeiro/data |
| GET | `/api/agendamentos/dia-disponivel` | Público | Verifica se o dia tem expediente |

### Bloqueios e Disponibilidade
| Método | Endpoint | Acesso | Descrição |
|---|---|---|---|
| POST | `/api/bloqueios` | Admin | Bloqueia horário |
| GET | `/api/bloqueios` | Admin | Lista bloqueios por data |
| DELETE | `/api/bloqueios/{id}` | Admin | Remove bloqueio |
| POST | `/api/dias-disponiveis` | Admin | Libera fim de semana |
| GET | `/api/dias-disponiveis/barbeiro/{id}` | Admin | Lista dias liberados |
| DELETE | `/api/dias-disponiveis/{id}` | Admin | Remove liberação |

---

## 🚀 Como Executar

### Pré-requisitos
- Java 21+
- Node.js 20+ e npm
- PostgreSQL 16+
- Maven 3.9+

### 1. Banco de Dados

```sql
CREATE DATABASE barbearia_db;
```

> As tabelas são criadas automaticamente pelo Hibernate na primeira execução (`spring.jpa.hibernate.ddl-auto=update`).

### 2. Backend

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/alquimista-barbearia.git
cd alquimista-barbearia/backend

# Configure o application.yml (veja seção abaixo)

# Execute
mvn spring-boot:run
```

O backend estará disponível em `http://localhost:8080`.

### 3. Frontend

```bash
cd ../frontend

# Instale as dependências
npm install

# Execute
ng serve
```

O frontend estará disponível em `http://localhost:4200`.

---

## ⚙️ Variáveis de Ambiente

Configure o `application.yml` com suas credenciais:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/barbearia_db
    username: seu_usuario
    password: sua_senha
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false

jwt:
  secret: SUA_CHAVE_SECRETA_AQUI   # mín. 32 caracteres
  expiration: 86400000              # 24 horas em ms

server:
  port: 8080
```

---

## 🗺 Próximos Passos

- [ ] Deploy em nuvem (Railway / Render para o backend, Vercel para o frontend)
- [ ] Substituição de `localStorage` por cookies `HttpOnly` para maior segurança do JWT
- [ ] Envio de e-mail de confirmação (JavaMailSender)
- [ ] Painel com métricas e gráficos de agendamentos
- [ ] Testes unitários e de integração (JUnit 5 + Mockito)
- [ ] Histórico de agendamentos por cliente

---

## 👨‍💻 Autor

Desenvolvido por **[Lucas Tompson]**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/lucastompson/)
[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/ltompson)
[![Gmail](https://img.shields.io/badge/Gmail-D14836?style=for-the-badge&logo=gmail&logoColor=white)](mailto:lucastompson99@gmail.com)

---

<div align="center">

Feito com ☕ Java e muita dedicação

</div>
