# GED - Sistema de Gestão Eletrônica de Documentos

Sistema completo de gestão de documentos com versionamento, desenvolvido com Spring Boot (backend) e Angular (frontend).

## 📋 Índice

- [Visão Geral](#visão-geral)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Arquitetura](#arquitetura)
- [Funcionalidades](#funcionalidades)
- [Pré-requisitos](#pré-requisitos)
- [Instalação e Execução](#instalação-e-execução)
- [Testes](#testes)
- [API Endpoints](#api-endpoints)
- [Decisões Técnicas](#decisões-técnicas)
- [Limitações Conhecidas](#limitações-conhecidas)

## 🎯 Visão Geral

O GED (Gestão Eletrônica de Documentos) é um sistema fullstack que permite:
- Upload e gerenciamento de documentos (PDF, PNG, JPG)
- Versionamento automático de arquivos
- Controle de acesso baseado em roles (ADMIN/USER)
- Busca e filtros por título e status
- Paginação de resultados
- Download de versões específicas

## 🚀 Tecnologias Utilizadas

### Backend
- **Java 17** - Linguagem de programação
- **Spring Boot 3.2.2** - Framework principal
- **Spring Security** - Autenticação e autorização
- **JWT (jjwt 0.12.3)** - Tokens de autenticação
- **JPA/Hibernate** - ORM para persistência
- **PostgreSQL 15** - Banco de dados
- **Flyway** - Migrations de banco de dados
- **Maven** - Gerenciamento de dependências
- **JUnit 5 + Mockito** - Testes unitários

### Frontend
- **Angular 17** - Framework frontend
- **TypeScript** - Linguagem de programação
- **RxJS** - Programação reativa
- **SCSS** - Estilização
- **Standalone Components** - Arquitetura moderna do Angular

### DevOps
- **Docker & Docker Compose** - Containerização
- **GitHub Actions** - CI/CD Pipeline

## 🏗️ Arquitetura

### Backend - Layered Architecture (Clean Architecture)

```
backend/
├── presentation/     # Controllers (REST API)
├── application/      # Services, DTOs, Mappers
├── domain/          # Entities, Repositories, Enums
├── infrastructure/  # Security, Storage, Exceptions
└── config/         # Configurações Spring
```

### Frontend - Feature-Based Architecture

```
frontend/
├── core/           # Services, Guards, Interceptors, Models
├── features/       # Módulos por funcionalidade
│   ├── auth/      # Login
│   └── documents/ # Gestão de documentos
└── shared/        # Componentes reutilizáveis
```

## ✨ Funcionalidades

### Autenticação e Autorização
- ✅ Login com JWT
- ✅ Controle de acesso por roles (ADMIN/USER)
- ✅ Proteção de rotas com guards
- ✅ Interceptors para adicionar token automaticamente

### Gestão de Documentos
- ✅ Criar documento com metadados (título, descrição, tags)
- ✅ Listar documentos com paginação
- ✅ Buscar por título
- ✅ Filtrar por status (DRAFT, PUBLISHED, ARCHIVED)
- ✅ Atualizar metadados
- ✅ Alterar status do documento
- ✅ Deletar documento (apenas ADMIN)

### Versionamento de Arquivos
- ✅ Upload de arquivos (PDF, PNG, JPG)
- ✅ Versionamento automático incremental
- ✅ Histórico completo de versões
- ✅ Download de versões específicas
- ✅ Metadados de cada versão (tamanho, tipo, uploader, data)

## 📦 Pré-requisitos

### Para execução com Docker (Recomendado)
- Docker 20.10+
- Docker Compose 2.0+

### Para execução local
- Java 17+
- Maven 3.8+
- Node.js 18+
- PostgreSQL 15+

## Instalação e Execução

### Opção 1: Docker Compose (Recomendado)

1. **Clone o repositório**
```bash
git clone <repository-url>
cd MVP_UDS
```

2. **Inicie o backend e banco de dados**
```bash
docker-compose up -d
```

Isso irá:
- ✅ Criar e iniciar o PostgreSQL
- ✅ Executar as migrations do Flyway (criação de tabelas)
- ✅ **Criar automaticamente os usuários padrão com senhas criptografadas**
- ✅ Iniciar o backend na porta 8080

3. **Inicie o frontend**
```bash
cd frontend
npm install
npm start
```

4. **Acesse a aplicação**
- Frontend: http://localhost:4200
- Backend API: http://localhost:8080

### 🔐 Credenciais Padrão

Os usuários são criados **automaticamente** na primeira inicialização:

| Usuário | Senha | Role |
|---------|-------|------|
| `admin` | `password123` | ADMIN |
| `user` | `password123` | USER |

> ⚠️ **Importante**: As senhas são criptografadas automaticamente usando BCrypt.
### Opção 2: Execução Local

#### Backend

1. **Configure o PostgreSQL**
```sql
CREATE DATABASE ged_db;
CREATE USER ged_user WITH PASSWORD 'ged_pass';
GRANT ALL PRIVILEGES ON DATABASE ged_db TO ged_user;
```

2. **Configure as variáveis de ambiente**
```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=ged_db
export DB_USER=ged_user
export DB_PASSWORD=ged_pass
export JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
```

3. **Execute o backend**
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

#### Frontend

1. **Instale as dependências**
```bash
cd frontend
npm install
```

2. **Execute o servidor de desenvolvimento**
```bash
npm start
```

3. **Acesse a aplicação**
- Frontend: http://localhost:4200
- Backend API: http://localhost:8080

### Usuários Padrão

O sistema vem com dois usuários pré-cadastrados:

| Usuário | Senha | Role |
|---------|-------|------|
| admin | password123 | ADMIN |
| user | password123 | USER |

## Testes

### Backend - Testes Unitários

O projeto possui **14 testes unitários** cobrindo os principais serviços:

```bash
cd backend
mvn test
```

**Cobertura de Testes:**
- AuthService (2 testes)
  - Autenticação bem-sucedida com geração de JWT
  - Validação de roles (ADMIN/USER)
  
- DocumentService (7 testes)
  - Criação de documento
  - Busca por ID
  - Atualização de status
  - Deleção de documento
  - Tratamento de erros (documento não encontrado, usuário não encontrado)
  
- FileStorageService (5 testes)
  - Upload de arquivo
  - Download de arquivo
  - Deleção de arquivo
  - Validação de tipos de arquivo
  - Tratamento de erros

### Executar testes com Docker

```bash
docker run --rm -v ${PWD}:/app -w /app maven:3.9-eclipse-temurin-17 mvn test
```

### Executar testes com relatório de cobertura

```bash
mvn test jacoco:report
# Relatório em: target/site/jacoco/index.html
```

## CI/CD Pipeline

O projeto utiliza **GitHub Actions** para integração e entrega contínuas.

### Pipeline Configurado

O pipeline (`.github/workflows/ci.yml`) executa automaticamente em:
- Push para branches `main` e `develop`
- Pull Requests para `main` e `develop`

### Jobs do Pipeline

1. **Backend Build and Test**
   - Setup JDK 17
   - Build com Maven
   - Execução de testes unitários
   - Geração de relatórios de teste
   - Utiliza PostgreSQL 15 como serviço

2. **Frontend Build**
   - Setup Node.js 18
   - Instalação de dependências
   - Build do projeto Angular
   - Upload de artifacts

3. **Docker Build**
   - Build da imagem Docker do backend
   - Validação do docker-compose

### Executar Pipeline Localmente

```bash
# Simular build do backend
cd backend
mvn clean install
mvn test

# Simular build do frontend
cd frontend
npm ci
npm run build

# Validar Docker Compose
docker-compose config
```

## API Endpoints

### Autenticação

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password123"
}
```

### Documentos

```http
# Listar documentos (com paginação e filtros)
GET /api/documents?page=0&size=10&title=exemplo&status=PUBLISHED
Authorization: Bearer {token}

# Criar documento
POST /api/documents
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Meu Documento",
  "description": "Descrição do documento",
  "tags": ["tag1", "tag2"],
  "tenantId": "tenant1"
}

# Buscar documento por ID
GET /api/documents/{id}
Authorization: Bearer {token}

# Atualizar documento
PUT /api/documents/{id}
Authorization: Bearer {token}

# Alterar status
PATCH /api/documents/{id}/status
Authorization: Bearer {token}
Content-Type: application/json

{
  "status": "PUBLISHED"
}

# Deletar documento (apenas ADMIN)
DELETE /api/documents/{id}
Authorization: Bearer {token}
```

### Versões de Arquivos

```http
# Upload de nova versão
POST /api/documents/{id}/versions
Authorization: Bearer {token}
Content-Type: multipart/form-data

file: [arquivo]

# Listar versões
GET /api/documents/{id}/versions
Authorization: Bearer {token}

# Download de arquivo
GET /api/files/{versionId}
Authorization: Bearer {token}
```

## 🎯 Decisões Técnicas

### Backend

1. **Layered Architecture**: Escolhida para separação clara de responsabilidades e facilitar manutenção
2. **JWT Stateless**: Permite escalabilidade horizontal sem sessões no servidor
3. **Flyway Migrations**: Versionamento e controle de schema do banco de dados
4. **Versionamento Explícito**: Tabela separada para versões permite auditoria completa
5. **File System Storage**: Simples para MVP, pode evoluir para S3/MinIO
6. **Bean Validation**: Validação declarativa e consistente
7. **Global Exception Handler**: Tratamento centralizado de erros

### Frontend

1. **Standalone Components**: Arquitetura moderna do Angular 17
2. **Feature-Based Structure**: Organização por funcionalidade facilita escalabilidade
3. **Smart/Dumb Components**: Separação de lógica e apresentação
4. **RxJS**: Gerenciamento reativo de estado e chamadas HTTP
5. **HTTP Interceptors**: Adição automática de token e tratamento de erros
6. **Route Guards**: Proteção de rotas baseada em autenticação

### DevOps

1. **Docker Multi-stage Build**: Reduz tamanho da imagem final
2. **Docker Compose**: Facilita execução local e desenvolvimento
3. **GitHub Actions**: CI/CD automatizado para build e testes
4. **Health Checks**: Garante que serviços estejam prontos antes de iniciar dependentes

## ⚠️ Limitações Conhecidas

### Funcionalidades Não Implementadas (Fora do Escopo MVP)

1. **Frontend Build no Docker**: Frontend não está containerizado no docker-compose
2. **Refresh Token**: Implementado apenas token de acesso
3. **Paginação Cursor-based**: Usa offset-based (suficiente para MVP)
4. **Upload Progress**: Não mostra progresso do upload
5. **Validação de Tipo de Arquivo**: Backend aceita qualquer arquivo
6. **Soft Delete**: Documentos são deletados permanentemente
7. **Auditoria Completa**: Logs básicos, sem auditoria detalhada
8. **Testes E2E**: Apenas testes unitários implementados
9. **Internacionalização**: Interface apenas em português
10. **Notificações**: Sem sistema de notificações

### Melhorias Futuras

- [ ] Implementar refresh token rotation
- [ ] Adicionar testes de integração
- [ ] Implementar cache (Redis)
- [ ] Migrar storage para S3/MinIO
- [ ] Adicionar busca full-text (Elasticsearch)
- [ ] Implementar WebSockets para notificações em tempo real
- [ ] Adicionar preview de documentos
- [ ] Implementar OCR para PDFs
- [ ] Adicionar métricas e monitoring (Prometheus/Grafana)
- [ ] Implementar rate limiting

## 📝 Estrutura do Banco de Dados

```sql
users
├── id (PK)
├── username (UNIQUE)
├── password (hashed)
├── email (UNIQUE)
├── role (ADMIN/USER)
└── created_at

documents
├── id (PK)
├── title
├── description
├── owner_id (FK -> users)
├── tenant_id
├── status (DRAFT/PUBLISHED/ARCHIVED)
├── created_at
└── updated_at

document_tags
├── document_id (FK -> documents)
└── tag

document_versions
├── id (PK)
├── document_id (FK -> documents)
├── version_number
├── file_key (storage path)
├── file_name
├── file_size
├── mime_type
├── uploaded_by (FK -> users)
└── uploaded_at
```

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'feat: add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto foi desenvolvido como teste técnico.

## 👨‍💻 Autor

Desenvolvido para o teste técnico de Desenvolvedor Java Sênior (Fullstack).