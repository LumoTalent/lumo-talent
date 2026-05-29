# 🧪 Guia de Teste - LumoTalent

## 📌 Pré-requisitos

1. **Java 21+** instalado
   ```bash
   java -version
   ```

2. **Chave da API Google Gemini válida**
   - Gere em: https://aistudio.google.com/app/apikeys
   - Copie o token

3. **Arquivo PDF de teste** com currículo (qualquer PDF)

---

## 🚀 Passo 1: Iniciar a Aplicação

### Opção A: Com Maven (recomendado para desenvolvimento)

```powershell
cd "C:\Users\lucas.fsbispo\Documents\projeto integrador\LumoTalent"
.\mvnw.cmd spring-boot:run
```

Você deve ver:
```
[INFO] --- spring-boot:3.5.0:run (default-cli) @ LumoTalent ---
...
o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8081 (http)
...
c.l.LumoTalent.LumoTalentApplication     : Started LumoTalentApplication in 5.234 seconds
```

**Porta:** `http://localhost:8081` (alterada para 8081 para evitar conflitos)

### Opção B: Com JAR pré-compilado

```powershell
java -jar "C:\Users\lucas.fsbispo\Documents\projeto integrador\LumoTalent\target\LumoTalent-0.0.1-SNAPSHOT.jar"
```

---

## 🧪 Passo 2: Testar Endpoints

### 2.1 Upload de Currículo (O teste principal)

**Endpoint:** `POST /api/candidatos/upload`

#### Teste com Sucesso:

```powershell
# PowerShell
$filePath = "C:\caminho\para\seu\curriculo.pdf"
$url = "http://localhost:8081/api/candidatos/upload"

$form = @{
    arquivo = Get-Item $filePath
}

Invoke-WebRequest -Uri $url -Method Post -Form $form
```

#### Esperado (Sucesso):
```json
{
  "mensagem": "Currículo processado com sucesso",
  "candidatoId": "1"
}
```
Status: **200 OK**

#### Esperado (Erro - chave inválida):
```json
{
  "erro": "Erro ao processar currículo",
  "detalhes": "Erro da Gemini: 401 - Invalid API Key"
}
```
Status: **500 Internal Server Error**

#### Teste com arquivo vazio:

```powershell
# Cria arquivo vazio
New-Item -Path "C:\temp\vazio.pdf" -ItemType File -Force

# Tenta upload
Invoke-WebRequest -Uri "http://localhost:8081/api/candidatos/upload" `
  -Method Post `
  -Form @{ arquivo = Get-Item "C:\temp\vazio.pdf" }
```

**Esperado:**
```json
{
  "erro": "Arquivo vazio"
}
```
Status: **400 Bad Request**

---

### 2.2 Listar Candidatos

**Endpoint:** `GET /api/candidatos`

```powershell
curl http://localhost:8081/api/candidatos
```

**Esperado (após upload bem-sucedido):**
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

### 2.3 Buscar Candidato por ID

**Endpoint:** `GET /api/candidatos/{id}`

```powershell
curl http://localhost:8081/api/candidatos/1
```

**Esperado:**
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao@example.com",
  ...
}
```

**Se não encontrado:**
```
Status: 404 Not Found
```

---

### 2.4 Deletar Candidato

**Endpoint:** `DELETE /api/candidatos/{id}`

```powershell
curl -X DELETE http://localhost:8081/api/candidatos/1
```

**Esperado:**
```
Status: 204 No Content
```

---

## 📊 Passo 3: Verificar Logs

### 3.1 Logs no Console

Enquanto a aplicação está rodando, você verá logs como:

**Upload bem-sucedido:**
```
2026-05-28T20:25:00.123-03:00  INFO 12345 --- [nio-8081-exec-1] c.l.L.c.CandidatoController              : Processando upload de currículo
2026-05-28T20:25:01.234-03:00  INFO 12345 --- [nio-8081-exec-1] c.l.L.s.CurriculoIAService              : Chamando API Gemini para análise de currículo
2026-05-28T20:25:08.456-03:00  INFO 12345 --- [nio-8081-exec-1] c.l.L.s.CurriculoIAService              : Análise de currículo concluída com sucesso
2026-05-28T20:25:09.567-03:00  INFO 12345 --- [nio-8081-exec-1] c.l.L.c.CandidatoController              : Currículo processado: candidatoId=1
```

**Upload com erro (chave inválida):**
```
2026-05-28T20:25:00.123-03:00  INFO 12345 --- [nio-8081-exec-1] c.l.L.s.CurriculoIAService              : Chamando API Gemini para análise de currículo
2026-05-28T20:25:02.345-03:00  ERROR 12345 --- [nio-8081-exec-1] c.l.L.s.CurriculoIAService              : Erro da Gemini: 401 - {"error": {"code": 401, "message": "Invalid API key provided"}}
2026-05-28T20:25:04.567-03:00  ERROR 12345 --- [nio-8081-exec-1] c.l.L.c.CandidatoController              : Erro ao processar upload de currículo - RuntimeException
```

### 3.2 Ativar Mais Detalhes (Debug Mode)

Adicione ao `application.properties`:

```properties
logging.level.com.lumotalent.LumoTalent=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.security=DEBUG
```

Reinicie a aplicação.

---

## 🔧 Passo 4: Troubleshooting

### ❌ Erro: "Port 8080 is already in use"

**Solução:**
1. Mude a porta em `application.properties`:
   ```properties
   server.port=8082
   ```
2. Ou mate o processo que usa 8080:
   ```powershell
   Get-Process | Where-Object { $_.Name -match "java" } | Stop-Process -Force
   ```

---

### ❌ Erro: "Erro da Gemini: 401 - Invalid API Key"

**Causa:** A chave da API está inválida ou expirada.

**Solução:**
1. Gere uma nova chave: https://aistudio.google.com/app/apikeys
2. Copie o token
3. Atualize em `application.properties`:
   ```properties
   gemini.api.key=NOVO_TOKEN_AQUI
   ```
4. Reinicie a aplicação

---

### ❌ Erro: "Timeout: Did not observe any item or terminal signal within 30s"

**Causa:** A API Gemini está lenta ou há problema de conectividade.

**Solução:**
1. Verifique sua conexão de internet
2. Tente acessar https://ai.google.dev diretamente
3. Aumente o timeout em `CurriculoIAService.java`:
   ```java
   .timeout(Duration.ofSeconds(60))  // aumentado de 30s
   ```

---

### ❌ Erro: "Resposta da Gemini sem campo 'candidates' ou vazio"

**Causa:** A API Gemini retornou uma resposta inesperada (formato mudou, erro interno, etc).

**Solução:**
1. Ative DEBUG logging (veja item 3.2)
2. Verifique se o modelo está correto:
   ```properties
   gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-lite:generateContent
   ```
3. Teste a API diretamente com cURL:
   ```bash
   curl -X POST \
     "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-lite:generateContent?key=YOUR_KEY" \
     -H "Content-Type: application/json" \
     -d '{
       "contents": [{
         "parts": [{"text": "Olá"}]
       }]
     }'
   ```

---

## 📋 Checklist de Teste

- [ ] Aplicação inicia sem erros (`Started LumoTalentApplication`)
- [ ] Upload com PDF válido retorna 200 OK + candidato criado
- [ ] Listar candidatos mostra dados corretos
- [ ] Buscar candidato por ID retorna dados corretos
- [ ] Deletar candidato retorna 204 No Content
- [ ] Upload com arquivo vazio retorna 400 Bad Request
- [ ] Erro na API Gemini é capturado e retorna 500 com mensagem clara
- [ ] Logs mostram eventos INFO/ERROR em pontos chave
- [ ] Banco de dados H2 foi criado (verifique em `/h2-console`)

---

## 💾 Acessar Banco de Dados H2

**URL:** http://localhost:8081/h2-console

**Configuração:**
- JDBC URL: `jdbc:h2:mem:lumotalentdb`
- User Name: `sa`
- Password: (deixe em branco)

**Clique em "Connect"** e execute SQL para verificar dados:

```sql
-- Ver todos os candidatos
SELECT * FROM candidato;

-- Ver candidato específico
SELECT * FROM candidato WHERE id = 1;

-- Contar candidatos
SELECT COUNT(*) FROM candidato;
```

---

## 📞 Se Tudo Falhar

1. **Verifique se a aplicação está rodando:**
   ```powershell
   curl http://localhost:8081/api/candidatos
   ```
   Deve retornar `[]` ou lista de candidatos (sem erro de conexão recusada)

2. **Verifique a chave da API:**
   ```bash
   # No PowerShell
   echo $env:GEMINI_API_KEY  # se usar variável de ambiente
   ```

3. **Reinstale e reconfigure:**
   ```powershell
   cd "C:\Users\lucas.fsbispo\Documents\projeto integrador\LumoTalent"
   .\mvnw.cmd clean install
   .\mvnw.cmd spring-boot:run
   ```

4. **Colete o stacktrace completo:**
   Quando houver erro, copie a mensagem completa e compartilhe

---

**Boa sorte no teste! 🎉**

