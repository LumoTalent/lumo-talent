# 🧪 Script de Teste - LumoTalent API

## 📋 Pré-requisitos

- PowerShell 5.1+
- Curl instalado (ou use `Invoke-WebRequest`)
- Aplicação rodando em `http://localhost:8081`
- Arquivo PDF para testar

---

## 🚀 Script 1: Upload com Sucesso

```powershell
# Defina o caminho do seu PDF
$pdfPath = "C:\Users\lucas.fsbispo\Downloads\curriculo.pdf"
$url = "http://localhost:8081/api/candidatos/upload"

# Verifique se o arquivo existe
if (-not (Test-Path $pdfPath)) {
    Write-Host "❌ Arquivo não encontrado: $pdfPath" -ForegroundColor Red
    exit
}

# Faça o upload
Write-Host "📤 Enviando arquivo: $pdfPath" -ForegroundColor Cyan

$form = @{
    arquivo = Get-Item $pdfPath
}

$response = Invoke-WebRequest -Uri $url -Method Post -Form $form

# Exiba resultado
Write-Host "✅ Status: $($response.StatusCode)" -ForegroundColor Green
Write-Host "Resposta:" -ForegroundColor Cyan
$response.Content | ConvertFrom-Json | Format-List
```

---

## 🚀 Script 2: Upload com Arquivo Vazio (Teste de Validação)

```powershell
# Crie um arquivo vazio
$emptyFile = "C:\temp\vazio.pdf"
New-Item -Path $emptyFile -ItemType File -Force | Out-Null

Write-Host "📤 Enviando arquivo vazio..." -ForegroundColor Cyan

$form = @{
    arquivo = Get-Item $emptyFile
}

$response = Invoke-WebRequest -Uri "http://localhost:8081/api/candidatos/upload" `
    -Method Post -Form $form -ErrorAction SilentlyContinue

Write-Host "✅ Status: $($response.StatusCode)" -ForegroundColor Green
Write-Host "Resposta esperada (400 Bad Request):" -ForegroundColor Cyan
$response.Content | ConvertFrom-Json | Format-List

# Limpe
Remove-Item $emptyFile -Force
```

---

## 🚀 Script 3: Listar Todos os Candidatos

```powershell
Write-Host "📋 Listando candidatos..." -ForegroundColor Cyan

$response = Invoke-WebRequest -Uri "http://localhost:8081/api/candidatos" `
    -Method Get

Write-Host "✅ Status: $($response.StatusCode)" -ForegroundColor Green
Write-Host "Candidatos:" -ForegroundColor Cyan

if ($response.Content) {
    $response.Content | ConvertFrom-Json | ForEach-Object {
        Write-Host "  - ID: $($_.id), Nome: $($_.nome), Email: $($_.email)" -ForegroundColor Yellow
    }
} else {
    Write-Host "  (nenhum candidato registrado)" -ForegroundColor Gray
}
```

---

## 🚀 Script 4: Buscar Candidato por ID

```powershell
$candidatoId = 1

Write-Host "🔍 Buscando candidato ID: $candidatoId" -ForegroundColor Cyan

$response = Invoke-WebRequest -Uri "http://localhost:8081/api/candidatos/$candidatoId" `
    -Method Get -ErrorAction SilentlyContinue

if ($response.StatusCode -eq 200) {
    Write-Host "✅ Encontrado:" -ForegroundColor Green
    $response.Content | ConvertFrom-Json | Format-List
} else {
    Write-Host "❌ Candidato não encontrado (Status: $($response.StatusCode))" -ForegroundColor Red
}
```

---

## 🚀 Script 5: Deletar Candidato

```powershell
$candidatoId = 1

Write-Host "🗑️  Deletando candidato ID: $candidatoId" -ForegroundColor Cyan

$response = Invoke-WebRequest -Uri "http://localhost:8081/api/candidatos/$candidatoId" `
    -Method Delete -ErrorAction SilentlyContinue

if ($response.StatusCode -eq 204) {
    Write-Host "✅ Candidato deletado com sucesso (Status: 204 No Content)" -ForegroundColor Green
} else {
    Write-Host "⚠️  Status: $($response.StatusCode)" -ForegroundColor Yellow
}
```

---

## 🚀 Script 6: Teste Completo (Full Flow)

```powershell
# Cores
$green = "Green"
$red = "Red"
$cyan = "Cyan"
$yellow = "Yellow"

function Test-Endpoint {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Uri,
        [hashtable]$Form,
        [int]$ExpectedStatus
    )
    
    Write-Host "`n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor $cyan
    Write-Host "🧪 Teste: $Name" -ForegroundColor $cyan
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor $cyan
    
    try {
        if ($Form) {
            $response = Invoke-WebRequest -Uri $Uri -Method $Method -Form $Form -ErrorAction Continue
        } else {
            $response = Invoke-WebRequest -Uri $Uri -Method $Method -ErrorAction Continue
        }
        
        $actualStatus = $response.StatusCode
        
        if ($actualStatus -eq $ExpectedStatus) {
            Write-Host "✅ Status: $actualStatus (esperado: $ExpectedStatus)" -ForegroundColor $green
            if ($response.Content) {
                Write-Host "Resposta:" -ForegroundColor $cyan
                $response.Content | ConvertFrom-Json | Format-List
            }
            return $true
        } else {
            Write-Host "⚠️  Status: $actualStatus (esperado: $ExpectedStatus)" -ForegroundColor $yellow
            return $false
        }
    } catch {
        Write-Host "❌ Erro: $_" -ForegroundColor $red
        return $false
    }
}

# ===== EXECUTAR TESTES =====

Write-Host "`n🚀 Iniciando suite de testes do LumoTalent..." -ForegroundColor $cyan

# 1. Upload bem-sucedido
$pdfPath = "C:\Users\lucas.fsbispo\Downloads\curriculo.pdf"
if (Test-Path $pdfPath) {
    $form = @{ arquivo = Get-Item $pdfPath }
    Test-Endpoint "Upload de Currículo" "Post" `
        "http://localhost:8081/api/candidatos/upload" `
        $form 200
}

# 2. Listar candidatos
Test-Endpoint "Listar Candidatos" "Get" `
    "http://localhost:8081/api/candidatos" `
    $null 200

# 3. Buscar candidato específico
Test-Endpoint "Buscar Candidato ID=1" "Get" `
    "http://localhost:8081/api/candidatos/1" `
    $null 200

# 4. Arquivo vazio
$emptyFile = "C:\temp\vazio.pdf"
New-Item -Path $emptyFile -ItemType File -Force | Out-Null
$form = @{ arquivo = Get-Item $emptyFile }
Test-Endpoint "Upload com Arquivo Vazio" "Post" `
    "http://localhost:8081/api/candidatos/upload" `
    $form 400
Remove-Item $emptyFile -Force

# Resumo
Write-Host "`n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor $cyan
Write-Host "✅ Suite de testes concluída!" -ForegroundColor $green
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor $cyan
```

---

## 🚀 Script 7: Monitorar Logs em Tempo Real

```powershell
# Se estiver rodando com Maven, os logs aparecerão no console
# Para capturar em arquivo:

Write-Host "📝 Capturando logs em arquivo..." -ForegroundColor Cyan

$logFile = "C:\temp\lumotalent_logs.txt"

# Se estiver usando JAR:
# java -jar target/LumoTalent-0.0.1-SNAPSHOT.jar > $logFile 2>&1

# Se estiver usando Maven:
# .\mvnw.cmd spring-boot:run 2>&1 | Tee-Object -FilePath $logFile

Write-Host "Logs salvos em: $logFile" -ForegroundColor Yellow
Write-Host "`nÚltimas 20 linhas:" -ForegroundColor Cyan
Get-Content $logFile -Tail 20
```

---

## 🚀 Script 8: Verificar Saúde da Aplicação

```powershell
function Check-ApplicationHealth {
    param(
        [string]$BaseUrl = "http://localhost:8081"
    )
    
    Write-Host "💚 Verificando saúde da aplicação..." -ForegroundColor Cyan
    
    try {
        # Tente um endpoint simples
        $response = Invoke-WebRequest -Uri "$BaseUrl/api/candidatos" `
            -Method Get -TimeoutSec 5 -ErrorAction Stop
        
        Write-Host "✅ Aplicação está rodando!" -ForegroundColor Green
        Write-Host "   URL: $BaseUrl" -ForegroundColor Green
        Write-Host "   Status: $($response.StatusCode)" -ForegroundColor Green
        Write-Host "   Tempo de resposta: ~$($response.ResponseTime)ms" -ForegroundColor Green
        
        return $true
    } catch {
        Write-Host "❌ Aplicação não está respondendo" -ForegroundColor Red
        Write-Host "   Erro: $_" -ForegroundColor Red
        Write-Host "   Inicie com: .\mvnw.cmd spring-boot:run" -ForegroundColor Yellow
        
        return $false
    }
}

# Executar
Check-ApplicationHealth
```

---

## 🚀 Script 9: Limpar Dados (Reset do Banco)

```powershell
# O banco H2 é in-memory, então ao parar e reiniciar a aplicação, 
# todos os dados são apagados automaticamente

Write-Host "🔄 Para resetar o banco de dados:" -ForegroundColor Cyan
Write-Host "1. Pare a aplicação (Ctrl+C)" -ForegroundColor Yellow
Write-Host "2. Aguarde 2-3 segundos" -ForegroundColor Yellow
Write-Host "3. Reinicie com: .\mvnw.cmd spring-boot:run" -ForegroundColor Yellow
Write-Host "`n✅ Pronto! Banco limpo." -ForegroundColor Green
```

---

## 📊 Consumo de Recursos

Para monitorar durante os testes:

```powershell
# CPU e Memória
Get-Process | Where-Object { $_.Name -like "*java*" } | Select-Object `
    Name, 
    @{Name="CPU(%)"; Expression={$_.CPU}}, 
    @{Name="Memória(MB)"; Expression={[math]::Round($_.WorkingSet/1MB, 2)}} | 
Format-Table -AutoSize

# Conexões de rede
netstat -ano | Select-String "8081"
```

---

## 🎯 Checklist de Testes

- [ ] Upload de PDF válido (200 OK)
- [ ] Upload com arquivo vazio (400 Bad Request)
- [ ] Listar candidatos (200 OK + array JSON)
- [ ] Buscar candidato por ID (200 OK)
- [ ] Deletar candidato (204 No Content)
- [ ] Erro na API Gemini é capturado (500 com detalhes)
- [ ] Logs aparecem no console com INFO/ERROR
- [ ] Resposta HTTP sempre é JSON válido
- [ ] Aplicação não trava com múltiplos uploads
- [ ] Banco H2 aceita dados (verificar em /h2-console)

---

**Dica:** Salve estes scripts em `.ps1` e execute com:
```powershell
powershell -ExecutionPolicy Bypass -File seu_script.ps1
```

---

**Última atualização:** 28/05/2026

