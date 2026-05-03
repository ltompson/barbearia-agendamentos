## 🗄 Banco de Dados

```sql
clientes        (id, nome, telefone)
barbeiros       (id, nome)
servicos        (id, nome, duracao_minutos, preco)

agendamentos    (id, cliente_id, barbeiro_id, servico_id,
                 data_hora, status)
                 -- status: 'AGENDADO' | 'CANCELADO'

bloqueios       (id, barbeiro_id, data, hora_inicio, hora_fim)

dias_disponiveis (id, barbeiro_id, data, horario, motivo)
                 -- horario null = dia inteiro liberado

admins          (id, username, password)
                 -- senha armazenada com BCrypt
```

**Regras de negócio:**
- Expediente: **08:00 às 18:30**, slots de **30 em 30 minutos**
- Fins de semana bloqueados por padrão (liberáveis individualmente)
- Horários ocupados ou bloqueados são excluídos automaticamente
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

```sql
CREATE DATABASE barbearia_db;
```

> As tabelas são criadas automaticamente pelo Hibernate na primeira execução.

### 2. Backend

```bash
git clone https://github.com/ltompson/barbearia-agendamentos.git
cd barbearia-agendamentos/backend
mvn spring-boot:run
```

O backend estará disponível em `http://localhost:8080`.

### 3. Frontend

```bash
cd ../frontend
npm install
ng serve
```

O frontend estará disponível em `http://localhost:4200`.

## ⚙️ Variáveis de Ambiente

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
  secret: SUA_CHAVE_SECRETA_AQUI
  expiration: 86400000

server:
  port: 8080
```

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