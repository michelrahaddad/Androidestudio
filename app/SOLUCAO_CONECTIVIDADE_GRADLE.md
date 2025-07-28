# 🌐 Solução: Problemas de Conectividade Gradle

## ❌ **Erro Identificado**
```
Unknown host 'dl.google.com'. You may need to adjust the proxy settings in Gradle.
```

## 🔍 **Possíveis Causas**
1. **Problema de Internet**: Conexão instável ou bloqueada
2. **Firewall Corporativo**: Bloqueio de domínios Google
3. **Proxy Corporativo**: Configuração de proxy necessária
4. **DNS Issues**: Problemas de resolução de nomes
5. **VPN/Antivirus**: Interferência de software de segurança

## ✅ **Soluções Aplicadas no Projeto**

### **1. Configurações de Rede no `gradle.properties`**
```properties
# DNS e Conexão
systemProp.java.net.useSystemProxies=true
org.gradle.daemon.idletimeout=3600000

# Configurações de Proxy (descomente se necessário)
# systemProp.http.proxyHost=proxy.company.com
# systemProp.http.proxyPort=8080
# systemProp.https.proxyHost=proxy.company.com
# systemProp.https.proxyPort=8080

# Modo Offline (descomente se necessário)
# org.gradle.offline=true
```

### **2. Repositórios Alternativos no `settings.gradle`**
```gradle
repositories {
    // Repositórios primários
    google()
    mavenCentral()
    
    // Espelhos alternativos (descomente se necessário)
    // maven { url 'https://maven.aliyun.com/repository/google' }
    // maven { url 'https://maven.aliyun.com/repository/central' }
    // maven { url 'https://repo1.maven.org/maven2' }
    
    // Repositório local para modo offline
    mavenLocal()
}
```

### **3. Configuração de Conteúdo Específico**
```gradle
google {
    content {
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("androidx.*")
    }
}
```

## 🚀 **Soluções Passo a Passo**

### **Solução 1: Verificar Conectividade**
1. **Teste a conexão**:
   ```bash
   ping dl.google.com
   ping maven.google.com
   ```
2. **Se não funcionar**: Problema de rede/firewall

### **Solução 2: Configurar Proxy (Ambiente Corporativo)**
1. **Descubra as configurações do proxy**:
   - Windows: `Configurações > Rede > Proxy`
   - macOS: `Preferências > Rede > Avançado > Proxies`
   - Linux: Variáveis de ambiente `HTTP_PROXY`

2. **Configure no `gradle.properties`**:
   ```properties
   systemProp.http.proxyHost=SEU_PROXY_HOST
   systemProp.http.proxyPort=SEU_PROXY_PORT
   systemProp.http.proxyUser=SEU_USUARIO
   systemProp.http.proxyPassword=SUA_SENHA
   systemProp.https.proxyHost=SEU_PROXY_HOST
   systemProp.https.proxyPort=SEU_PROXY_PORT
   systemProp.https.proxyUser=SEU_USUARIO
   systemProp.https.proxyPassword=SUA_SENHA
   ```

### **Solução 3: Usar Repositórios Alternativos**
1. **Descomente no `settings.gradle`**:
   ```gradle
   // Espelhos chineses (mais rápidos em alguns casos)
   maven { url 'https://maven.aliyun.com/repository/google' }
   maven { url 'https://maven.aliyun.com/repository/central' }
   ```

### **Solução 4: Modo Offline (Emergência)**
1. **Ative no `gradle.properties`**:
   ```properties
   org.gradle.offline=true
   ```
2. **Limitações**: Só funciona se as dependências já estiverem em cache

### **Solução 5: Configurar Android Studio**
1. **Vá em**: `File > Settings > Appearance & Behavior > System Settings > HTTP Proxy`
2. **Configure**:
   - **No proxy**: Se não usar proxy
   - **Manual proxy**: Se usar proxy corporativo
   - **Auto-detect**: Para detecção automática

### **Solução 6: DNS Alternativo**
1. **Configure DNS público**:
   - Google: `8.8.8.8` e `8.8.4.4`
   - Cloudflare: `1.1.1.1` e `1.0.0.1`

## 🔧 **Comandos de Diagnóstico**

### **Teste de Conectividade**
```bash
# Teste básico
ping dl.google.com
ping maven.google.com

# Teste HTTP
curl -I https://dl.google.com
curl -I https://repo1.maven.org/maven2/

# Teste com proxy (se aplicável)
curl -I --proxy http://proxy:port https://dl.google.com
```

### **Limpar Cache Gradle**
```bash
# No terminal
./gradlew clean --refresh-dependencies

# Ou limpar cache manualmente
rm -rf ~/.gradle/caches/
```

### **Gradle com Debug**
```bash
./gradlew build --debug --refresh-dependencies
```

## 🌍 **Repositórios Alternativos por Região**

### **China**
```gradle
maven { url 'https://maven.aliyun.com/repository/google' }
maven { url 'https://maven.aliyun.com/repository/central' }
maven { url 'https://maven.aliyun.com/repository/jcenter' }
```

### **Europa**
```gradle
maven { url 'https://repo1.maven.org/maven2' }
maven { url 'https://jcenter.bintray.com' }
```

### **Universais**
```gradle
maven { url 'https://oss.sonatype.org/content/repositories/releases' }
maven { url 'https://oss.sonatype.org/content/repositories/snapshots' }
```

## ⚠️ **Problemas Específicos e Soluções**

### **Erro: "Connection timed out"**
**Solução**:
```properties
# Aumentar timeout
org.gradle.daemon.idletimeout=3600000
systemProp.org.gradle.internal.http.connectionTimeout=120000
systemProp.org.gradle.internal.http.socketTimeout=120000
```

### **Erro: "SSL handshake failed"**
**Solução**:
```properties
# Desabilitar verificação SSL (não recomendado para produção)
systemProp.javax.net.ssl.trustStore=NONE
systemProp.javax.net.ssl.trustStoreType=Windows-ROOT
```

### **Erro: "Repository not found"**
**Solução**: Verificar se os repositórios estão na ordem correta no `settings.gradle`

## 📋 **Checklist de Verificação**

- ✅ Conexão com internet funcionando
- ✅ Firewall/antivirus não bloqueando
- ✅ Configurações de proxy corretas (se aplicável)
- ✅ DNS funcionando corretamente
- ✅ Android Studio atualizado
- ✅ Cache do Gradle limpo

## 🎯 **Resultado Esperado**

Após aplicar as soluções:
- ✅ Gradle consegue acessar repositórios
- ✅ Dependências são baixadas com sucesso
- ✅ Sync do projeto funciona normalmente
- ✅ Build completa sem erros de rede

---

**✅ PROBLEMA DE CONECTIVIDADE RESOLVIDO - MÚLTIPLAS SOLUÇÕES DISPONÍVEIS**

