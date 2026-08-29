# 📚 API Biblioteca Universitária

API REST desenvolvida para gerenciamento de uma **Biblioteca Universitária**, permitindo o controle completo de **usuários**, **livros**, **empréstimos** e **reservas**, com **autenticação JWT**, persistência em **PostgreSQL**, documentação via **Swagger** e suporte a **Docker**.

---

## 🎯 Objetivo do Projeto

Fornecer uma API segura, organizada e escalável para bibliotecas universitárias, aplicando boas práticas de:
- Arquitetura REST
- Segurança com JWT
- Persistência relacional
- Regras de negócio
- Versionamento e deploy

Projeto desenvolvido para fins **acadêmicos e educacionais**.

---

## 🚀 Tecnologias Utilizadas

- Java 17
- Spring Boot 3.3.4
- Spring Security
- JWT (JSON Web Token)
- Spring Data JPA (Hibernate)
- PostgreSQL
- Swagger / OpenAPI (springdoc)
- Maven
- Docker & Docker Compose

---

## 🏗️ Arquitetura

- **Controller** → Camada de entrada (REST)
- **Service** → Regras de negócio
- **Repository** → Persistência (JPA)
- **Security** → Autenticação e autorização
- **Domain** → Entidades e enums

---

## 🗂️ Estrutura do Prjeto
```bash
      🗂️ biblioteca_universitaria
       |
       ├─ 🗂️ controller
       ├─ 🗂️ domain
           ├─ 🗂️ enums
           ├─ 🗂️ entities
       |
       ├─ 🗂️ repository
       ├─ 🗂️ security
       ├─ 🗂️ service

```

---
## 🔐 Segurança e Autenticação

A API utiliza **JWT (Bearer Token)** para autenticação.

### 🔓 Rotas públicas
- `POST /api/auth/register`
- `POST /api/auth/login`

### 🔒 Rotas protegidas
Todas as rotas de:
- Livros
- Empréstimos
- Reservas

Exigem o header:
`Authorization: Bearer <TOKEN>`

---

## 🔄 Fluxo de Autenticação

1. Usuário se registra
2. Usuário faz login
3. API retorna um token JWT
4. Token é enviado no header das requisições
5. API valida o token e libera o acesso

---

## 🗄️ Banco de Dados (PostgreSQL)

O banco de dados utilizado é **PostgreSQL**, com geração automática de schema via JPA/Hibernate.

```properties
spring.jpa.hibernate.ddl-auto=update
```
Em produção, recomenda-se uso de migrations (Flyway), mas para fins acadêmicos o update é suficiente.

---

## 📌 Entidades e Relacionamentos

### 👤 `Users`

Armazena os usuários do sistema.

| Campo |	Tipo | Observação |
|-------|--------|------------|
|   id  |	 PK  | Identificador |
|  nome |	String | Nome do usuário |
| email |	String | Único |
| senha |	String |	Hash |
|  role |	 Enum  |	ADMIN / USER |

### 📚 `Livros`

Cadastro de livros da biblioteca.

| Campo |	Tipo | Observação |
|-------|--------|------------|
|  id   |	 PK  | Identificador |
| titulo |	String | Título do livro |
| autor |	String | Nome do Autor |
| isbn |	String |	Único |
|  ano_publicado |	 Int  |	|
| quantidade_total | Int | Total de cópias |
| quantidade_disponivel | Int | Atualizada automaticamente |

### 🔄 `empréstimos`

Registra os empréstimos realizados.

Relacionamentos:

- `users (1) → (N) emprestimos`
- `livros (1) → (N) emprestimos`

| Campo |	Tipo | 
|-------|--------|
| id | PK |    |
| user_id | FK |
| livro_id | FK |
| data_emprestimo | Date |
| data_prevista_devolucao | Date |
| data_devolucao | Date |
| status | Enum |

### 📌 `reservas`

Registra reservas de livros.

Relacionamentos:

- `users (1) → (N) reservas`
- `livros (1) → (N) reservas`

| Campo |	Tipo | 
|-------|--------|
| id | PK |    |
| user_id | FK |
| livro_id | FK |
| data_reserva | Date |
| status | Enum |

---

## ✅ Regras de Negócio

- Livro só pode ser emprestado se `quantidade_disponivel > 0`
- Empréstimo reduz a quantidade disponível
- Devolução:
   - Atualiza data_devolucao
   - Incrementa quantidade_disponivel

- Reserva pode ser criada quando não há disponibilidade
- Email e ISBN são únicos

---

## 📌 Endpoints da API

### `🔐 Autenticação`

| Método |         Rota         | Protegido  | Descrição |
|--------|----------------------|------------|-----------|
|  POST	 | `/api/auth/register` |	   ❌     |	Cadastro  |
|  POST  | `/api/auth/login`    |	   ❌     |   Login   |

### `📚 livroa`

| Método |	       Rota        |  Protegido  |
|--------|---------------------|--------------|
|   GET  |	`/api/livros`      |	   ✅     |
|   GET  |	`/api/livros/{id}` |	   ✅     |
|  POST  |	`/api/livros`      |	   ✅     |
|   PUT  |	`/api/livros/{id}` |	   ✅     |
| DELETE |	`/api/livros/{id}` |	   ✅     | 

### `🔄Empréstimos`

| Método |                 Rota                | Protegido |
|--------|-------------------------------------|-----------|
|  POST  |	       `/api/emprestimos`          |     ✅    |
|  GET   |	       `/api/emprestimos`          |     ✅    |
|  GET	 |       `/api/emprestimos/{id}`   |     ✅    |
|  PATCH |	  `/api/emprestimos/{id}/devolver` |     ✅    |

### `📌 Reservas`

| Método |          Rota         |	Protegido |
|--------|-----------------------|------------|
|  POST  |	  `/api/reservas`    |	   ✅    |
|  GET   |  	`/api/reservas`|	   ✅    |
| DELETE |	`/api/reservas/{id}` |	   ✅    |

---

## ⚙️ Configuração Local (sem Docker)

### Pré-requisitos
- Java 17+
- Maven
- PostgreSQL

### Banco de dados
```sql
CREATE DATABASE biblioteca;
```

### `application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/biblioteca
spring.datasource.username=postgres
spring.datasource.password=postgres

spring.jpa.hibernate.ddl-auto=update

jwt.secret=CHAVE_SECRETA_GRANDE
jwt.expiration=3600000
```

### Executar

`mvn spring-boot:run`

---

## 🐳 Execução com Docker 

### docker-compose.yml

```yaml
services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: biblioteca
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - biblioteca_pgdata:/var/lib/postgresql/data

  api:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - postgres
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/biblioteca
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
      JWT_SECRET: CHAVE_SECRETA_GRANDE
      JWT_EXPIRATION: 3600000

volumes:
  biblioteca_pgdata:
```

```bash
docker compose up -d --build
```

---

## Swagger
- Local:
  `http://localhost:8080/swagger-ui/index.html`
- Produção:
  `https://SUA-URL/swagger-ui/index.html`

---

## Status HTTP Utilizados
- 200 OK
- 201 Created
- 204 No Content
- 400 Bad Request
- 401 Unauthorized
- 403 Forbidden
- 404 Not Found

---

## 🌐 Deploy 

A aplicação pode ser publicada em:

- Render
- Railway
- Fly.io

Utilizando variáveis de ambiente para segurança

---

## 👩‍💻 Autora

**Larissa Muniz**

---

Projeto desenvolvido para fins acadêmicos e estudo de APIs REST com Spring Boot










