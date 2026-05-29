# Diagnóstico e Correções - LumoTalent

## 📋 Resumo Executivo

O projeto **compilava com sucesso**, mas tinha problemas em **runtime** ao processar uploads de currículo com a API Google Gemini. Foram implementadas correções para melhorar robustez, logging e tratamento de erros.

---

## 🔴 Erros Identificados

### 1. **RuntimeException: Retries exhausted: 2/2** (CurriculoIAService.java:104)

**Sintoma:**
```
java.lang.RuntimeException: Erro ao chamar Gemini: Retries exhausted: 2/2
	at com.lumotalent.LumoTalent.service.CurriculoIAService.analisarCurriculo(CurriculoIAService.java:104)
	at com.lumotalent.LumoTalent.service.CandidatoService.processarCurriculo(CandidatoService.java:38)
	at com.lumotalent.LumoTalent.controller.CandidatoController.uploadCurriculo(CandidatoController.java:27)
```

**Causa Raiz:**
- Tentativa de chamar API Google Gemini com **chave API inválida/expirada** ou **URL incorreta**
- Erro capturado pela Reactor library (retry exaurido)
- Logging insuficiente para debugar qual foi a real falha
- Timeout muito curto (10s)
- Retry muito agressivo (2 tentativas) para erros não-transitórios

### 2. **Resposta do Endpoint não informativa**

O endpoint `/api/candidatos/upload` retornava `500 Internal Server Error` sem detalhes úteis do erro.

### 3. **Falta de Logging Estruturado**

`printStackTrace()` no controller sem logger apropriado.

---

## ✅ Correções Implementadas

### 1. **CurriculoIAService.java**

#### Melhorias:
- ✅ Adicionado `Logger` (SLF4J) com logs informativos em cada etapa
- ✅ Timeout aumentado de 10s para **30s** (mais realista para LLM)
- ✅ Retry reduzido de 2 para **1 tentativa** (com backoff até 5s)
- ✅ Melhor tratamento de exceções com `e.getMessage()` e `e.getCause()`
- ✅ Validação de campo "candidates" e "parts" com `.isEmpty()` mais idiomático
- ✅ Logs diferenciados por nível: `INFO` (sucesso), `ERROR` (falhas), `DEBUG` (detalhes)

**Código-chave:**
```java
logger.info("Chamando API Gemini para análise de currículo");
// ... chamada HTTP ...
logger.error("Erro ao chamar Gemini: ", e); // Com stack trace completo
```

### 2. **CandidatoController.java**

#### Melhorias:
- ✅ Resposta HTTP retorna `Map<String, String>` com mensagem clara e detalhes do erro
- ✅ Validação de arquivo vazio antes de processar
- ✅ Diferenciação entre `RuntimeException` (erros do negócio) e `Exception` (erros técnicos)
- ✅ HTTP Status apropriados: `400 Bad Request` (arquivo vazio), `500 Internal Server Error` (falhas)
- ✅ Logger estruturado para rastrear erros

**Exemplo de resposta:**
```json
{
  "erro": "Erro ao processar currículo",
  "detalhes": "Configuração da API Gemini ausente (apiKey/apiUrl)"
}
```

---

## 📊 Fluxo Corrigido

```
POST /api/candidatos/upload [arquivo.pdf]
        ↓
[CandidatoController.uploadCurriculo()]
    ├─ Valida se arquivo vazio → retorna 400 Bad Request
    ├─ Chama CandidatoService.processarCurriculo()
    │   ↓
    │   [CandidatoService.processarCurriculo()]
    │   ├─ Extrai texto do PDF com PDFBox
    │   ├─ Chama CurriculoIAService.analisarCurriculo()
    │   │   ↓
    │   │   [CurriculoIAService.analisarCurriculo()]
    │   │   ├─ Valida apiKey e apiUrl (antes de chamar)
    │   │   ├─ Chama Google Gemini API
    │   │   │   └─ Timeout: 30s, Retry: 1x com backoff
    │   │   │   └─ Loga cada tentativa (INFO) e erro (ERROR)
    │   │   ├─ Valida resposta JSON (candidates → parts → text)
    │   │   └─ Retorna texto parseado
    │   ├─ Limpa JSON (remove markdown)
    │   ├─ Converte para objeto Candidato
    │   └─ Salva no banco H2
    └─ Retorna 200 OK com mensagem de sucesso

❌ Se falhar:
    └─ Retorna 500 Internal Server Error com detalhes do erro
```

---

## 🔍 Como Debugar Erros Agora

### 1. **Verifique as propriedades da API Gemini**

Arquivo: `src/main/resources/application.properties`

```properties
gemini.api.key=SEU_TOKEN_AQUI
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-lite:generateContent
```

- **Chave expirou?** Gere uma nova em https://aistudio.google.com/app/apikeys
- **URL correta?** Verifique a versão do modelo no console do Google

### 2. **Ative logging detalhado**

Adicione ao `application.properties`:

```properties
logging.level.com.lumotalent.LumoTalent.service.CurriculoIAService=DEBUG
logging.level.com.lumotalent.LumoTalent.controller.CandidatoController=DEBUG
```

Então veja os logs:
```
2026-05-28T20:25:00 INFO  CurriculoIAService - Chamando API Gemini...
2026-05-28T20:25:10 ERROR CurriculoIAService - Erro: Configuração da API ausente
```

### 3. **Teste o endpoint com cURL**

```bash
# Com arquivo real
curl -v -F "arquivo=@C:\caminho\curriculo.pdf" \
  http://localhost:8081/api/candidatos/upload

# Resposta de sucesso (200 OK)
{
  "mensagem": "Currículo processado com sucesso",
  "candidatoId": "1"
}

# Resposta de erro (500)
{
  "erro": "Erro ao processar currículo",
  "detalhes": "Erro da Gemini: 401 - Invalid API Key"
}
```

---

## 📝 Checklist de Validação

- [x] Código compila sem erros (`mvn -DskipTests package`)
- [x] Logging estruturado em todos os pontos críticos
- [x] Tratamento de exceções diferenciado por tipo
- [x] Timeout apropriado (30s para LLM)
- [x] Retry conservador (1x) para evitar loops infinitos
- [x] Resposta HTTP com mensagem clara e detalhes do erro
- [x] Validações antes de acessar campos (evita NPE/IndexOutOfBounds)
- [x] Imports limpos (sem warnings)

---

## 🚀 Próximos Passos (Opcional)

1. **Remover chave da fonte:** Mover `gemini.api.key` para variável de ambiente
   ```bash
   set GEMINI_API_KEY=seu_token
   # ou no application.properties
   gemini.api.key=${GEMINI_API_KEY}
   ```

2. **Adicionar CircuitBreaker:** Usar Resilience4j para pausar requisições após N falhas
   ```xml
   <dependency>
       <groupId>io.github.resilience4j</groupId>
       <artifactId>resilience4j-spring-boot3</artifactId>
   </dependency>
   ```

3. **Testes Unitários:** Mockar `WebClient` e testar `CurriculoIAService` com respostas fictícias

4. **Cache:** Cachear análises duplicadas de currículos similares

---

## 📚 Referências

- Google Gemini API Docs: https://ai.google.dev/docs
- Spring WebClient: https://spring.io/blog/2021/03/30/spring-tips-the-webflux-client
- Reactor Retry: https://projectreactor.io/docs/core/latest/reference/#retrying

---

**Última atualização:** 28/05/2026
**Status:** ✅ Pronto para teste em produção

