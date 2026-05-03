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

## 📋 Índice

- [Sobre o Projeto](#-sobre-o-projeto)
- [Funcionalidades](#-funcionalidades)
- [Tecnologias](#-tecnologias)
- [Arquitetura](#-arquitetura)
- [Banco de Dados](#-banco-de-dados)
- [API REST](#-api-rest)
- [Como Executar](#-como-executar)
- [Variáveis de Ambiente](#-variáveis-de-ambiente)
- [Próximos Passos](#-próximos-passos)
- [Autor](#-autor)

## 💡 Sobre o Projeto

O **Alquimista Barbearia** é um sistema de agendamentos desenvolvido para uma barbearia real, com o objetivo de substituir agendamentos feitos por mensagem e otimizar a gestão da agenda dos barbeiros.

O projeto nasceu como solução prática para um problema real e evoluiu para uma aplicação full-stack robusta, abrangendo desde a experiência do cliente até o controle administrativo completo.

> ⚙️ Desenvolvido do zero por um desenvolvedor solo, cobrindo backend, frontend, modelagem de banco e regras de negócio.

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
- **Bloquear Dias e Horários** — bloqueia horários específicos por barbeiro e data
- **Liberar Dias e Horários** — libera qualquer dia com horário específico ou dia inteiro
- Notificação automática no WhatsApp do admin ao reagendar
- Menu lateral com navegação separada por funcionalidade
- Interface responsiva com menu hambúrguer animado no mobile

### 🎨 UX / Interface
- Tema claro e escuro com alternância dinâmica
- Layout 100% responsivo (mobile-first)
- Feedback visual de carregamento e erros

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

## 🏗 Arquitetura

A aplicação segue uma arquitetura em três camadas:

**Frontend (Angular 21 — porta 4200)**
Guards, Interceptors, Services e Components se comunicam com o backend via HTTP + Bearer JWT.

**Backend (Spring Boot — porta 8080)**
Controllers → Services → Repositories → Entities, protegidos pelo Spring Security com filtro JWT a cada requisição.

**Banco de Dados (PostgreSQL — barbearia_db)**
Persistência via Hibernate/JPA com as tabelas: clientes, barbeiros, servicos, agendamentos, bloqueios, dias_disponiveis e admins.

**Fluxo de autenticação:**
1. Cliente faz `POST /api/auth/login`
2. Backend valida credenciais e gera JWT
3. Frontend armazena o token
4. Interceptor injeta `Bearer token` em todas as requisições protegidas
5. JwtFilter valida o token a cada request

## 🗄 Banco de Dados

| Tabela | Principais colunas |
|---|---|
| clientes | id, nome, telefone |
| barbeiros | id, nome |
| servicos | id, nome, duracao_minutos, preco |
| agendamentos | id, cliente_id, barbeiro_id, servico_id, data_hora, status |
| bloqueios | id, barbeiro_id, data, hora_inicio, hora_fim |
| dias_disponiveis | id, barbeiro_id, data, horario, motivo |
| admins | id, username, password |

**Regras de negócio:**
- Expediente: **08:00 às 18:30**, slots de **30 em 30 minutos**
- Fins de semana bloqueados por padrão (liberáveis individualmente)
- Horários ocupados ou bloqueados são excluídos automaticamente
- `horario null` em dias_disponiveis = dia inteiro liberado
- Reagendamento cancela o horário anterior e cria um novo atomicamente

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
| POST | `/api/bloqueios` | Admin | Bloqueia horário específico |
| GET | `/api/bloqueios` | Admin | Lista bloqueios por data |
| DELETE | `/api/bloqueios/{id}` | Admin | Remove bloqueio |
| POST | `/api/dias-disponiveis` | Admin | Libera dia ou horário específico |
| GET | `/api/dias-disponiveis/barbeiro/{id}` | Admin | Lista dias liberados por barbeiro |
| DELETE | `/api/dias-disponiveis/{id}` | Admin | Remove liberação |

## 🚀 Como Executar

### Pré-requisitos
- Java 21+
- Node.js 20+ e npm
- PostgreSQL 16+
- Maven 3.9+

### 1. Banco de Dados

Crie o banco no PostgreSQL:

    CREATE DATABASE barbearia_db;

As tabelas são criadas automaticamente pelo Hibernate na primeira execução.

### 2. Backend

    git clone https://github.com/ltompson/barbearia-agendamentos.git
    cd barbearia-agendamentos/backend
    mvn spring-boot:run

Backend disponível em `http://localhost:8080`

### 3. Frontend

    cd ../frontend
    npm install
    ng serve

Frontend disponível em `http://localhost:4200`

## ⚙️ Variáveis de Ambiente

Configure o `application.yml` com suas credenciais locais:

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
      secret: SUA_CHAVE_SECRETA_AQUI
      expiration: 86400000
    server:
      port: 8080

## 🗺 Próximos Passos

- [ ] Deploy em nuvem (Railway / Render para o backend, Vercel para o frontend)
- [ ] Substituição de `localStorage` por cookies `HttpOnly` para maior segurança do JWT
- [ ] Envio de e-mail de confirmação (JavaMailSender)
- [ ] Painel com métricas e gráficos de agendamentos
- [ ] Testes unitários e de integração (JUnit 5 + Mockito)
- [ ] Histórico de agendamentos por cliente

## 👨‍💻 Autor

Desenvolvido por **Lucas Tompson**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/lucastompson/)
[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/ltompson)
[![Gmail](https://img.shields.io/badge/Gmail-D14836?style=for-the-badge&logo=gmail&logoColor=white)](https://mail.google.com/mail/?view=cm&to=lucastompson99@gmail.com)

<div align="center">

Feito com ☕ Java e muita dedicação

</div>