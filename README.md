# Quarkus API Project - Rethink

Este é um projeto completo de API RESTful desenvolvido com Quarkus, demonstrando as melhores práticas para desenvolvimento de microsserviços modernos em Java. O projeto implementa um sistema de e-commerce com gerenciamento de produtos, clientes e pedidos.

## 🚀 Tecnologias Utilizadas

- **Java 21** - Versão LTS mais recente do Java
- **Quarkus 3.15.1** - Framework Java supersônico e subatômico
- **RESTEasy Reactive** - Implementação JAX-RS reativa para endpoints não-bloqueantes
- **Hibernate ORM com Panache** - Simplificação do acesso a dados com Active Record pattern
- **PostgreSQL** - Banco de dados relacional para produção
- **H2 Database** - Banco de dados em memória para desenvolvimento e testes
- **OpenAPI/Swagger** - Documentação automática e interativa da API
- **JUnit 5 + Mockito** - Framework de testes unitários e mocks
- **Docker & Docker Compose** - Containerização e orquestração
- **GraalVM** - Compilação nativa para executáveis otimizados
- **Maven** - Gerenciamento de dependências e build

## 📁 Estrutura do Projeto

```
quarkus-api-project/
├── .mvn/                       # Maven Wrapper
├── src/
│   ├── main/
│   │   ├── java/com/rethink/api/
│   │   │   ├── dto/           # Data Transfer Objects
│   │   │   │   └── CreateOrderRequest.java
│   │   │   ├── entity/        # Entidades JPA
│   │   │   │   ├── Customer.java
│   │   │   │   ├── Order.java
│   │   │   │   ├── OrderItem.java
│   │   │   │   └── Product.java
│   │   │   ├── repository/    # Repositórios Panache
│   │   │   │   ├── CustomerRepository.java
│   │   │   │   ├── OrderItemRepository.java
│   │   │   │   ├── OrderRepository.java
│   │   │   │   └── ProductRepository.java
│   │   │   ├── resource/      # Endpoints REST
│   │   │   │   ├── CustomerResource.java
│   │   │   │   ├── OrderResource.java
│   │   │   │   └── ProductResource.java
│   │   │   └── service/       # Lógica de negócio
│   │   │       ├── CustomerService.java
│   │   │       ├── OrderService.java
│   │   │       └── ProductService.java
│   │   └── resources/
│   │       ├── application.properties  # Configurações
│   │       └── import.sql             # Dados iniciais
│   └── test/                  # Testes unitários e integração
│       └── java/com/rethink/api/
│           ├── repository/    # Testes de repositórios
│           ├── resource/      # Testes de endpoints
│           └── service/       # Testes de serviços
├── .dockerignore             # Arquivos ignorados pelo Docker
├── .gitignore               # Arquivos ignorados pelo Git
├── Dockerfile               # Build JVM
├── Dockerfile.native        # Build nativo
├── docker-compose.yml       # Orquestração de containers
├── mvnw                     # Maven Wrapper
├── pom.xml                  # Dependências Maven
└── README.md               # Este arquivo
```

## ⚙️ Pré-requisitos

### Obrigatório
- **Java 21** (LTS) - [Download Temurin JDK 21](https://adoptium.net/temurin/releases/?version=21)
- **Maven 3.9+** - [Download Maven](https://maven.apache.org/download.cgi)

### Opcional
- **Docker 24+** e **Docker Compose 2.20+** - Para containerização
- **GraalVM 21** - Para builds nativos [Download GraalVM](https://www.graalvm.org/downloads/)
- **PostgreSQL 16** - Se preferir rodar localmente sem Docker

## 🛠️ Instalação e Configuração

### 1. Verificar versão do Java

```bash
java -version
# Deve mostrar: openjdk version "21.x.x"
```

### 2. Clonar o Repositório

```bash
git clone <url-do-repositorio>
cd quarkus-api-project
```

### 3. Instalar Dependências

```bash
./mvnw clean install
```

## 🏃‍♂️ Executando o Projeto

### Modo de Desenvolvimento (Hot Reload)

```bash
./mvnw compile quarkus:dev
```

A aplicação estará disponível em `http://localhost:8080/api`

**Funcionalidades do modo dev:**
- Hot reload automático
- Dev UI: `http://localhost:8080/q/dev`
- Banco H2 em memória com dados de exemplo
- Logs detalhados no console

### Executar Testes

```bash
# Todos os testes
./mvnw test

# Testes específicos
./mvnw test -Dtest=ProductServiceTest

# Com cobertura
./mvnw test jacoco:report
```

### Build da Aplicação

#### Build JVM (Recomendado para desenvolvimento):
```bash
./mvnw clean package
java -jar target/quarkus-app/quarkus-run.jar
```

#### Build Nativo (Requer GraalVM):
```bash
# Configurar GraalVM
export JAVA_HOME=/path/to/graalvm-21
export GRAALVM_HOME=$JAVA_HOME

# Build nativo
./mvnw package -Dnative

# Executar
./target/quarkus-api-project-1.0.0-SNAPSHOT-runner
```

### Executar com Docker

#### Desenvolvimento com Docker Compose:
```bash
# Subir aplicação + PostgreSQL
docker-compose up --build

# Parar e remover containers
docker-compose down

# Remover volumes (limpar dados)
docker-compose down -v
```

#### Build de produção:
```bash
# Build da imagem
docker build -f Dockerfile -t quarkus-api-project:latest .

# Executar container
docker run -d \
  --name quarkus-api \
  -p 8080:8080 \
  -e QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://host.docker.internal:5432/quarkus_db \
  -e QUARKUS_DATASOURCE_USERNAME=quarkus \
  -e QUARKUS_DATASOURCE_PASSWORD=quarkus \
  quarkus-api-project:latest
```

## 📚 Documentação da API

### Swagger UI
Acesse a documentação interativa em: `http://localhost:8080/api/swagger-ui`

### OpenAPI Specification
Download da especificação: `http://localhost:8080/api/swagger`

## 🔌 Endpoints da API

### 📦 Produtos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/products` | Lista todos os produtos |
| GET | `/api/products/{id}` | Busca produto por ID |
| GET | `/api/products/search?name={termo}` | Pesquisa produtos por nome |
| GET | `/api/products/available` | Lista produtos disponíveis em estoque |
| GET | `/api/products/count` | Retorna estatísticas de produtos |
| POST | `/api/products` | Cria novo produto |
| PUT | `/api/products/{id}` | Atualiza produto existente |
| DELETE | `/api/products/{id}` | Remove produto |

### 👥 Clientes

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/customers` | Lista todos os clientes |
| GET | `/api/customers/{id}` | Busca cliente por ID |
| GET | `/api/customers/email/{email}` | Busca cliente por email |
| GET | `/api/customers/cpf/{cpf}` | Busca cliente por CPF |
| GET | `/api/customers/search?name={termo}` | Pesquisa clientes por nome |
| GET | `/api/customers/city/{city}` | Lista clientes por cidade |
| GET | `/api/customers/state/{state}` | Lista clientes por estado |
| GET | `/api/customers/recent?limit={n}` | Lista clientes recentes |
| GET | `/api/customers/count` | Retorna total de clientes |
| POST | `/api/customers` | Cria novo cliente |
| PUT | `/api/customers/{id}` | Atualiza cliente existente |
| DELETE | `/api/customers/{id}` | Remove cliente |

### 🛒 Pedidos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/orders` | Lista todos os pedidos |
| GET | `/api/orders/{id}` | Busca pedido por ID |
| GET | `/api/orders/customer/{customerId}` | Lista pedidos por cliente |
| GET | `/api/orders/status/{status}` | Lista pedidos por status |
| GET | `/api/orders/recent?limit={n}` | Lista pedidos recentes |
| GET | `/api/orders/pending` | Lista pedidos pendentes |
| GET | `/api/orders/count` | Retorna estatísticas de pedidos |
| POST | `/api/orders` | Cria novo pedido |
| POST | `/api/orders/{id}/items` | Adiciona item ao pedido |
| PUT | `/api/orders/{id}/status?status={status}` | Atualiza status do pedido |
| DELETE | `/api/orders/{id}` | Cancela pedido |
| DELETE | `/api/orders/{orderId}/items/{itemId}` | Remove item do pedido |

## 📝 Exemplos de Requisições

### Criar Cliente
```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao.silva@email.com",
    "phone": "(11) 98765-4321",
    "cpf": "123.456.789-00",
    "address": "Rua das Flores, 123",
    "city": "São Paulo",
    "state": "SP",
    "zipCode": "01234-567"
  }'
```

### Criar Produto
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Notebook Dell Inspiron",
    "description": "Notebook com processador Intel i7",
    "price": 3500.00,
    "quantity": 10
  }'
```

### Criar Pedido
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "items": [
      {"productId": 1, "quantity": 2},
      {"productId": 2, "quantity": 1}
    ],
    "notes": "Entregar pela manhã"
  }'
```

## ⚙️ Configuração

### Banco de Dados

#### Desenvolvimento (H2)
```properties
quarkus.datasource.db-kind=h2
quarkus.datasource.jdbc.url=jdbc:h2:mem:testdb
quarkus.hibernate-orm.database.generation=drop-and-create
```

#### Produção (PostgreSQL)
```properties
%prod.quarkus.datasource.db-kind=postgresql
%prod.quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/quarkus_db
%prod.quarkus.datasource.username=quarkus
%prod.quarkus.datasource.password=quarkus
%prod.quarkus.hibernate-orm.database.generation=update
```

### Variáveis de Ambiente

| Variável | Descrição | Valor Padrão |
|----------|-----------|--------------|
| `QUARKUS_DATASOURCE_JDBC_URL` | URL de conexão do banco | jdbc:h2:mem:testdb |
| `QUARKUS_DATASOURCE_USERNAME` | Usuário do banco | sa |
| `QUARKUS_DATASOURCE_PASSWORD` | Senha do banco | (vazio) |
| `QUARKUS_HTTP_PORT` | Porta HTTP | 8080 |
| `QUARKUS_LOG_LEVEL` | Nível de log | INFO |

## 🧪 Testes

O projeto possui cobertura completa de testes:

- **Testes Unitários**: Services com Mockito
- **Testes de Integração**: Resources com REST Assured
- **Testes de Repositório**: Com @TestTransaction

```bash
# Executar testes com relatório
./mvnw test

# Ver cobertura de código
./mvnw verify
# Relatório em: target/site/jacoco/index.html
```

## 📊 Monitoramento

### Health Checks
- Liveness: `http://localhost:8080/q/health/live`
- Readiness: `http://localhost:8080/q/health/ready`
- Geral: `http://localhost:8080/q/health`

### Métricas
- Prometheus: `http://localhost:8080/q/metrics`
- JSON: `http://localhost:8080/q/metrics/json`

### Dev UI (Apenas em desenvolvimento)
- Dashboard: `http://localhost:8080/q/dev`
- Config: `http://localhost:8080/q/dev/io.quarkus.quarkus-vertx-http/config`
- Beans: `http://localhost:8080/q/dev/io.quarkus.quarkus-arc/beans`

## 🚀 Deploy em Produção

### Kubernetes
```bash
# Gerar manifestos
./mvnw package -Dquarkus.kubernetes.deploy=true

# Deploy
kubectl apply -f target/kubernetes/
```

### AWS Lambda
```bash
# Adicionar extensão
./mvnw quarkus:add-extension -Dextensions="amazon-lambda"

# Build
./mvnw package -Dquarkus.package.type=native-amazon-lambda
```

## 🔧 Troubleshooting

### Problemas Comuns

1. **Erro de versão do Java**
   ```bash
   # Verificar versão
   java -version
   
   # Configurar JAVA_HOME
   export JAVA_HOME=/path/to/jdk-21
   export PATH=$JAVA_HOME/bin:$PATH
   ```

2. **Erro de conexão com banco**
   ```bash
   # Verificar se PostgreSQL está rodando
   docker ps
   
   # Ver logs do container
   docker logs postgres
   ```

3. **Porta 8080 em uso**
   ```bash
   # Mudar porta
   ./mvnw quarkus:dev -Dquarkus.http.port=8090
   ```

## 📈 Performance

### Benchmarks (Hardware de referência)
- **Startup time**: ~0.9s (JVM) / ~0.02s (Native)
- **Memory footprint**: ~128MB (JVM) / ~25MB (Native)
- **Requests/sec**: ~50k (Reactive) / ~30k (Blocking)

## 🤝 Contribuindo

1. Fork o projeto
2. Crie sua feature branch (`git checkout -b feature/NovaFuncionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/NovaFuncionalidade`)
5. Abra um Pull Request

### Padrões de Código
- Seguir convenções Java
- Testes obrigatórios para novas funcionalidades
- Documentação JavaDoc para métodos públicos
- Commits semânticos (feat:, fix:, docs:, etc.)

## 👤 Autor

**Jonatas Pedroso**
- GitHub: [@JonatasPedroso](https://github.com/JonatasPedroso)
- Desenvolvido para o programa de treinamento Level UP da Rethink

## 📄 Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

## 👥 Suporte

Para suporte e dúvidas:
- Email: suporte@rethink.com.br
- GitHub Issues: [https://github.com/JonatasPedroso/quarkus-api-project/issues](https://github.com/JonatasPedroso/quarkus-api-project/issues)
- Documentação: Este README

---
Desenvolvido com ❤️ por Jonatas Pedroso para o programa Level UP da Rethink

## Instalação e Execução

### 1. Clonar o Repositório

```bash
git clone <url-do-repositorio>
cd quarkus-api-project
```

### 2. Executar em Modo de Desenvolvimento

```bash
./mvnw compile quarkus:dev
```

A aplicação estará disponível em `http://localhost:8080/api`

### 3. Executar os Testes

```bash
./mvnw test
```

### 4. Build da Aplicação

#### Build JVM:
```bash
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```

#### Build Nativo (requer GraalVM):
```bash
./mvnw package -Dnative
./target/quarkus-api-project-1.0.0-SNAPSHOT-runner
```

### 5. Executar com Docker

#### Build e execução com Docker Compose:
```bash
docker-compose up --build
```

#### Build apenas do container:
```bash
docker build -f Dockerfile -t quarkus-api-project .
docker run -i --rm -p 8080:8080 quarkus-api-project
```

## Endpoints da API

### Produtos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/products` | Lista todos os produtos |
| GET | `/api/products/{id}` | Busca produto por ID |
| GET | `/api/products/search?name={termo}` | Pesquisa produtos por nome |
| GET | `/api/products/available` | Lista produtos disponíveis |
| GET | `/api/products/count` | Retorna contagem de produtos |
| POST | `/api/products` | Cria novo produto |
| PUT | `/api/products/{id}` | Atualiza produto existente |
| DELETE | `/api/products/{id}` | Remove produto |

### Exemplo de Requisição

#### Criar Produto:
```json
POST /api/products
{
  "name": "Notebook Dell",
  "description": "Notebook Dell Inspiron 15",
  "price": 3500.00,
  "quantity": 10
}
```

#### Resposta:
```json
{
  "id": 1,
  "name": "Notebook Dell",
  "description": "Notebook Dell Inspiron 15",
  "price": 3500.00,
  "quantity": 10
}
```

## Documentação da API

A documentação interativa da API está disponível através do Swagger UI:

- Swagger UI: `http://localhost:8080/api/swagger-ui`
- OpenAPI Spec: `http://localhost:8080/api/swagger`

## Configuração

### Banco de Dados

#### Desenvolvimento (H2 em memória):
```properties
quarkus.datasource.db-kind=h2
quarkus.datasource.jdbc.url=jdbc:h2:mem:testdb
```

#### Produção (PostgreSQL):
```properties
%prod.quarkus.datasource.db-kind=postgresql
%prod.quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/quarkus_db
%prod.quarkus.datasource.username=quarkus
%prod.quarkus.datasource.password=quarkus
```

### Variáveis de Ambiente

- `QUARKUS_DATASOURCE_JDBC_URL` - URL de conexão do banco
- `QUARKUS_DATASOURCE_USERNAME` - Usuário do banco
- `QUARKUS_DATASOURCE_PASSWORD` - Senha do banco

## Funcionalidades Implementadas

- ✅ API RESTful com operações CRUD completas
- ✅ Persistência com JPA/Hibernate e Panache
- ✅ Validação de dados com Bean Validation
- ✅ Testes unitários e de integração
- ✅ Documentação automática com OpenAPI
- ✅ Configuração para múltiplos ambientes
- ✅ Build nativo com GraalVM
- ✅ Containerização com Docker
- ✅ Docker Compose para ambiente completo

## Monitoramento e Health Check

- Health: `http://localhost:8080/q/health`
- Metrics: `http://localhost:8080/q/metrics`

## Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## Autor

Desenvolvido para o programa de treinamento da Rethink.

## Licença

Este projeto é propriedade da Rethink e foi desenvolvido como parte do programa de capacitação.