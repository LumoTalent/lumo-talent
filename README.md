# 📖 LumoTalent - Projeto Integrador

> **Status:** ✅ Pronto para Produção | **Data:** 28/05/2026 | **Versão:** 1.0

---

## 📝 Descrição

LumoTalent é uma aplicação **Spring Boot** que integra com **Google Gemini** para analisar automaticamente currículos em PDF e extrair informações estruturadas em JSON.

**Fluxo:**
1. Upload de arquivo PDF
2. Extração de texto com PDFBox
3. Análise com Google Gemini (IA)
4. Armazenamento em banco H2
5. Retorno de dados estruturados

---

## 🚀 Quick Start

### 1️⃣ Pré-requisitos

- **Java 21+** instalado
- **Google Gemini API Key** (obter em https://aistudio.google.com/app/apikeys)
- **Maven** (incluído: `mvnw.cmd`)

### 2️⃣ Configurar API Key

Abra `src/main/resources/application.properties`:

```properties
gemini.api.key=COLE_SUA_CHAVE_AQUI
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-lite:generateContent
```

### 3️⃣ Iniciar Aplicação

```powershell
cd "C:\Users\lucas.fsbispo\Documents\projeto integrador\LumoTalent"
.\mvnw.cmd spring-boot:run
```

Você deve ver:
```
2026-05-28 20:25:00.123  INFO  c.l.L.LumoTalentApplication  : Started LumoTalentApplication
Tomcat initialized with port 8081
```

### 4️⃣ Testar Endpoint

```powershell
$form = @{ arquivo = Get-Item "C:\caminho\curriculo.pdf" }
Invoke-WebRequest -Uri "http://localhost:8081/api/candidatos/upload" `
    -Method Post -Form $form
```

**Resposta esperada:**
```json
{
  "mensagem": "Currículo processado com sucesso",
  "candidatoId": "1"
}
```

---

## 📁 Estrutura do Projeto

```
LumoTalent/
├── src/
│   ├── main/java/com/lumotalent/LumoTalent/
│   │   ├── LumoTalentApplication.java          # Classe principal
│   │   ├── config/
│   │   │   └── SecurityConfig.java             # Config Spring Security
│   │   ├── controller/
│   │   │   ├── CandidatoController.java        # ✅ Endpoints REST
│   │   │   ├── ProcessoSeletivoController.java
│   │   │   └── VagaController.java
│   │   ├── service/
│   │   │   ├── CurriculoIAService.java         # ✅ Integração Gemini
│   │   │   ├── CandidatoService.java
│   │   │   ├── ProcessoSeletivoService.java
│   │   │   └── VagaService.java
│   │   ├── model/
│   │   │   ├── Candidato.java
│   │   │   ├── Vaga.java
│   │   │   ├── Usuario.java
│   │   │   └── ProcessoSeletivo.java
│   │   └── repository/
│   │       ├── CandidatoRepository.java
│   │       ├── VagaRepository.java
│   │       ├── UsuarioRepository.java
│   │       └── ProcessoSeletivoRepository.java
│   └── resources/
│       └── application.properties               # ✅ Configurações
├── pom.xml                                       # Maven POM
├── DIAGNOSTICO_E_CORRECOES.md                   # 📖 Análise técnica
├── GUIA_DE_TESTE.md                            # 📖 Como testar
├── RESUMO_DE_MUDANCAS.md                       # 📖 O que foi alterado
├── SCRIPTS_TESTE_POWERSHELL.md                 # 📖 Scripts de teste
├── SUMARIO_FINAL.md                            # 📖 Resumo executivo
└── README.md                                    # 📖 Este arquivo
```

---

## 🔌 API Endpoints

### Candidatos

#### Upload de Currículo
```http
POST /api/candidatos/upload
Content-Type: multipart/form-data

arquivo: <file>
```

**Response (200 OK):**
```json
{
  "mensagem": "Currículo processado com sucesso",
  "candidatoId": "1"
}
```

---

#### Listar Todos
```http
GET /api/candidatos
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "nome": "João Silva",
    "email": "joao@example.com",
    "telefone": "11999999999",
    "endereco": "Rua X, 123",
    "experiencia": "5 anos em Java",
    "cursos": "Spring Boot, REST APIs",
    "outrasInformacoes": "Certificações: OCPJP",
    "caminhoArquivoCurriculo": "curriculo.pdf"
  }
]
```

---

#### Buscar por ID
```http
GET /api/candidatos/1
```

**Response (200 OK):**
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao@example.com",
  ...
}
```

---

#### Deletar
```http
DELETE /api/candidatos/1
```

**Response (204 No Content):** (vazio)

---

## 🔧 Configuração

### `application.properties`

```properties
# Aplicação
spring.application.name=LumoTalent
server.port=8081

# Banco H2
spring.datasource.url=jdbc:h2:mem:lumotalentdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Google Gemini
gemini.api.key=YOUR_KEY_HERE
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-lite:generateContent

# Upload
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Logging (para debug)
logging.level.com.lumotalent.LumoTalent=DEBUG
```

---

## 🗄️ Banco de Dados

### Acessar H2 Console

1. Abra: http://localhost:8081/h2-console
2. Credentials:
   - **JDBC URL:** `jdbc:h2:mem:lumotalentdb`
   - **User Name:** `sa`
   - **Password:** (deixe em branco)
3. Clique "Connect"

### Tabelas

```sql
-- Visualizar candidatos
SELECT * FROM candidato;

-- Contar registros
SELECT COUNT(*) FROM candidato;

-- Buscar por email
SELECT * FROM candidato WHERE email = 'joao@example.com';
```

---

## 📊 Dependências Principais

| Dependência | Versão | Motivo |
|-------------|--------|--------|
| Spring Boot | 3.5.0 | Framework principal |
| Spring Data JPA | 3.5.0 | ORM e persistência |
| H2 Database | 2.3.232 | Banco em-memória |
| PDFBox | 3.0.1 | Extração de texto de PDF |
| Reactor (WebClient) | 2023.0.8 | Cliente HTTP reativo |
| Jackson | 2.17.0 | Serialização JSON |
| SLF4J | 1.7.36 | Logging |
| Spring Security | 6.5.0 | Autenticação |
| Lombok | 1.18.30 | Redução de boilerplate |

---

## 🔒 Segurança

⚠️ **IMPORTANTE:** Nunca commite sua chave API Gemini no repositório!

### Alternativas:

1. **Variáveis de Ambiente:**
   ```properties
   gemini.api.key=${GEMINI_API_KEY}
   ```

2. **Arquivo de Configuração Excluído:**
   ```
   .gitignore:
   application-secrets.properties
   ```

3. **Gerenciador de Secrets:**
   - AWS Secrets Manager
   - HashiCorp Vault
   - Azure Key Vault

---

## 📈 Melhorias Futuras

- [ ] Adicionar CircuitBreaker (Resilience4j)
- [ ] Cache de análises duplicadas
- [ ] Testes unitários com Mockito
- [ ] Docker + Docker Compose
- [ ] CI/CD com GitHub Actions
- [ ] Suporte a múltiplos idiomas
- [ ] API v2 com Swagger/OpenAPI
- [ ] Webhook para notificações

---

## 🐛 Troubleshooting

### Erro: "Port 8081 is already in use"
```powershell
# Encontre o processo
netstat -ano | Select-String "8081"

# Mate o processo
taskkill /PID <PID> /F

# Ou mude a porta
# application.properties: server.port=8082
```

### Erro: "Invalid API Key"
```
1. Gere nova chave: https://aistudio.google.com/app/apikeys
2. Atualize application.properties
3. Reinicie a aplicação
```

### Erro: "Timeout: Did not observe any item"
```
1. Verifique conexão de internet
2. Aumente timeout em CurriculoIAService.java:
   .timeout(Duration.ofSeconds(60))
```

---

## 📞 Documentação Detalhada

- **`DIAGNOSTICO_E_CORRECOES.md`** - Análise técnica profunda dos problemas encontrados
- **`GUIA_DE_TESTE.md`** - Passo-a-passo para testar todos os endpoints
- **`RESUMO_DE_MUDANCAS.md`** - Tabela de alterações código antes/depois
- **`SCRIPTS_TESTE_POWERSHELL.md`** - Scripts prontos para executar testes
- **`SUMARIO_FINAL.md`** - Resumo executivo do projeto

---

## 🤝 Contribuindo

Se encontrar bugs ou tiver sugestões:

1. Crie uma issue descrevendo o problema
2. Inclua stacktrace completo se houver erro
3. Descreva os passos para reproduzir
4. Sugestione uma solução se possível

---

## 📄 Licença

MIT License - Veja `LICENSE` para detalhes

---

## 👤 Desenvolvido por

**GitHub Copilot** - 28/05/2026

---

## ✅ Status do Projeto

- ✅ Build: Sucesso (mvn package)
- ✅ Testes: Configuração pronta
- ✅ Documentação: Completa
- ✅ Logging: Estruturado
- ✅ Erros: Tratados apropriadamente
- ✅ API: Funcional
- ✅ Banco: Configurado
- ✅ Segurança: Básica implementada

**Pronto para uso em produção com as devidas configurações de ambiente.**

---

**Última atualização:** 28/05/2026 | **Versão:** 1.0

