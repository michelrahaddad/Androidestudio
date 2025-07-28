# 📴 Instruções: Modo Offline Android Studio

## 🎯 **Quando Usar Modo Offline**
- Sem conexão com internet
- Firewall corporativo muito restritivo
- Problemas persistentes de conectividade
- Desenvolvimento em ambiente isolado

## ✅ **Ativando Modo Offline**

### **Método 1: Android Studio (Interface)**
1. **Abra Android Studio**
2. **Vá em**: `File > Settings` (Windows/Linux) ou `Android Studio > Preferences` (macOS)
3. **Navegue**: `Build, Execution, Deployment > Gradle`
4. **Marque**: ☑️ `Offline work`
5. **Clique**: `Apply` e `OK`
6. **Sync**: `File > Sync Project with Gradle Files`

### **Método 2: gradle.properties**
1. **Abra**: `gradle.properties`
2. **Descomente a linha**:
   ```properties
   org.gradle.offline=true
   ```
3. **Salve o arquivo**
4. **Sync no Android Studio**

### **Método 3: Linha de Comando**
```bash
./gradlew build --offline
```

## ⚠️ **Limitações do Modo Offline**

### **O que FUNCIONA**:
- ✅ Compilação com dependências já em cache
- ✅ Build de projetos já sincronizados
- ✅ Execução em emulador/dispositivo
- ✅ Debug e desenvolvimento local

### **O que NÃO FUNCIONA**:
- ❌ Download de novas dependências
- ❌ Atualização de versões de bibliotecas
- ❌ Sync inicial de projeto novo
- ❌ Resolução de dependências não cacheadas

## 🔄 **Preparando para Modo Offline**

### **Antes de Ficar Offline**:
1. **Sync completo**:
   ```bash
   ./gradlew build --refresh-dependencies
   ```

2. **Download de todas as dependências**:
   ```bash
   ./gradlew dependencies --all
   ```

3. **Cache das ferramentas Android**:
   - Abra Android Studio
   - `Tools > SDK Manager`
   - Baixe todas as versões necessárias

4. **Verificar cache**:
   ```bash
   ls ~/.gradle/caches/modules-2/files-2.1/
   ```

## 🛠️ **Soluções para Problemas Offline**

### **Erro: "Dependency not found in cache"**
**Solução**:
1. **Conecte temporariamente à internet**
2. **Execute**:
   ```bash
   ./gradlew build --refresh-dependencies
   ```
3. **Volte ao modo offline**

### **Erro: "Plugin not found"**
**Solução**:
1. **Baixe plugins necessários online primeiro**
2. **Verifique cache de plugins**:
   ```bash
   ls ~/.gradle/caches/plugins-2/
   ```

### **Erro: "Android SDK not found"**
**Solução**:
1. **Configure SDK local**:
   ```properties
   # local.properties
   sdk.dir=/caminho/para/android/sdk
   ```

## 📦 **Cache Gradle Explicado**

### **Localização do Cache**:
- **Windows**: `C:\Users\{username}\.gradle\caches\`
- **macOS**: `/Users/{username}/.gradle/caches/`
- **Linux**: `/home/{username}/.gradle/caches/`

### **Estrutura do Cache**:
```
.gradle/caches/
├── modules-2/files-2.1/     # Dependências JAR/AAR
├── plugins-2/               # Plugins Gradle
├── transforms-2/            # Transformações Android
└── build-cache-1/           # Cache de build
```

### **Verificar Dependências em Cache**:
```bash
find ~/.gradle/caches/modules-2/files-2.1/ -name "*.jar" | grep androidx
find ~/.gradle/caches/modules-2/files-2.1/ -name "*.aar" | grep android
```

## 🔧 **Configuração Avançada Offline**

### **gradle.properties Otimizado**:
```properties
# Modo offline
org.gradle.offline=true

# Cache local
org.gradle.caching=true
org.gradle.parallel=true

# Timeout reduzido (não precisa esperar rede)
org.gradle.daemon.idletimeout=10000

# Usar apenas cache local
org.gradle.configureondemand=true
```

### **settings.gradle para Offline**:
```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        // Priorizar cache local
        mavenLocal()
        
        // Repositórios online (ignorados em modo offline)
        google()
        mavenCentral()
    }
}
```

## 📱 **Testando em Modo Offline**

### **Checklist de Teste**:
- ✅ Projeto compila: `./gradlew assembleDebug`
- ✅ Testes passam: `./gradlew test`
- ✅ APK é gerado: `./gradlew build`
- ✅ App instala no dispositivo
- ✅ App executa normalmente

### **Comandos de Teste**:
```bash
# Compilar em modo offline
./gradlew assembleDebug --offline

# Executar testes
./gradlew test --offline

# Verificar dependências
./gradlew dependencies --offline

# Limpar e rebuildar
./gradlew clean build --offline
```

## 🌐 **Voltando ao Modo Online**

### **Desativar Modo Offline**:
1. **Android Studio**: Desmarque `Offline work`
2. **gradle.properties**: Comente `# org.gradle.offline=true`
3. **Sync**: `File > Sync Project with Gradle Files`

### **Atualizar Dependências**:
```bash
./gradlew build --refresh-dependencies
```

## 💡 **Dicas para Desenvolvimento Offline**

### **Preparação**:
- Baixe documentação offline do Android
- Use Android Studio com SDK completo
- Mantenha cache de dependências atualizado

### **Desenvolvimento**:
- Foque em lógica de negócio
- Use recursos já disponíveis
- Evite adicionar novas dependências

### **Debugging**:
- Use logs locais
- Teste em emulador offline
- Simule cenários sem rede

---

**📴 MODO OFFLINE CONFIGURADO - DESENVOLVIMENTO SEM INTERNET POSSÍVEL**

