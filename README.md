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
- [Estrutura do Projeto](#-estrutura-do-projeto)
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