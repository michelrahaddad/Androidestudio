# 🚀 Setup Completo - DroneCam Pro Android Studio

## 📋 Pré-requisitos

### 1. Software Necessário
- **Android Studio**: Versão 2022.3.1 ou superior
- **Java Development Kit (JDK)**: Versão 8, 11 ou 17
- **Android SDK**: API Level 34 (Android 14)
- **Gradle**: 7.6+ (incluído no projeto)

### 2. Hardware Recomendado
- **RAM**: Mínimo 8GB (recomendado 16GB)
- **Espaço em Disco**: 15GB livres
- **Processador**: Intel i5 ou equivalente AMD

## 🔧 Instalação Passo a Passo

### Passo 1: Instalar Android Studio

#### Windows
1. Baixe do site oficial: https://developer.android.com/studio
2. Execute o instalador `.exe`
3. Siga o assistente de instalação
4. Aceite todas as licenças

#### macOS
1. Baixe o arquivo `.dmg`
2. Arraste para a pasta Applications
3. Execute e siga o setup inicial

#### Linux
1. Baixe o arquivo `.tar.gz`
2. Extraia para `/opt/android-studio`
3. Execute `./studio.sh` no diretório `bin`

### Passo 2: Configurar Android SDK

1. **Abra Android Studio**
2. **Configure SDK**:
   - Vá em `Tools > SDK Manager`
   - Na aba "SDK Platforms", instale:
     - ✅ Android 14 (API 34) - **OBRIGATÓRIO**
     - ✅ Android 13 (API 33)
     - ✅ Android 12 (API 31)
     - ✅ Android 11 (API 30)

3. **SDK Tools**:
   - Na aba "SDK Tools", verifique se estão instalados:
     - ✅ Android SDK Build-Tools 34.0.0
     - ✅ Android SDK Platform-Tools
     - ✅ Android SDK Tools
     - ✅ Google Play Services
     - ✅ Android Emulator

### Passo 3: Importar o Projeto

1. **Extrair o ZIP**:
   ```
   Extraia DroneCam_Pro_Android_Native_FINAL.zip
   ```

2. **Abrir no Android Studio**:
   - Inicie o Android Studio
   - Clique em "Open an existing Android Studio project"
   - Navegue até a pasta extraída `android_native_app`
   - Clique "OK"

3. **Configurar SDK Path**:
   - Copie `local.properties.template` para `local.properties`
   - Edite `local.properties` e defina o caminho do SDK:
   
   **Windows**:
   ```
   sdk.dir=C\:\\Users\\SeuUsuario\\AppData\\Local\\Android\\Sdk
   ```
   
   **macOS**:
   ```
   sdk.dir=/Users/SeuUsuario/Library/Android/sdk
   ```
   
   **Linux**:
   ```
   sdk.dir=/home/SeuUsuario/Android/Sdk
   ```

4. **Sincronizar Gradle**:
   - O Android Studio detectará automaticamente o projeto
   - Clique em "Sync Now" quando aparecer a notificação
   - Aguarde o download das dependências (5-10 minutos)

### Passo 4: Configurar Dispositivo

#### Opção A: Dispositivo Físico (Recomendado)

1. **Habilitar Opções do Desenvolvedor**:
   - Vá em `Configurações > Sobre o telefone`
   - Toque 7 vezes em "Número da versão"
   - Volte e acesse "Opções do desenvolvedor"

2. **Ativar Depuração USB**:
   - Em "Opções do desenvolvedor", ative "Depuração USB"
   - Conecte o dispositivo via USB
   - Autorize a depuração quando solicitado

#### Opção B: Emulador Android

1. **Criar AVD**:
   - Vá em `Tools > AVD Manager`
   - Clique em "Create Virtual Device"
   - Escolha um dispositivo (ex: Pixel 6)
   - Selecione API 34 (Android 14)
   - Configure RAM: 4GB
   - Finalize a criação

### Passo 5: Compilar e Executar

1. **Primeira Compilação**:
   - Clique no botão "Build" (🔨) na toolbar
   - Aguarde a compilação completa (2-5 minutos)
   - Verifique se não há erros no painel "Build"

2. **Executar Aplicativo**:
   - Clique no botão "Run" (▶️) na toolbar
   - Selecione o dispositivo/emulador
   - Aguarde a instalação e execução

## ✅ Verificação da Instalação

### 1. Tela Principal
- ✅ O app deve abrir mostrando o logo do drone
- ✅ Dois botões: "Conectar Dispositivo" e "Controle da Câmera"
- ✅ Design futurista com tema escuro e acentos laranja

### 2. Teste de Navegação
- ✅ Toque em "Conectar Dispositivo" → Abre tela de Wi-Fi
- ✅ Volte e toque em "Controle da Câmera" → Abre tela da câmera
- ✅ Teste os botões de configurações e galeria

### 3. Logs de Debug
```bash
# No terminal do Android Studio
adb logcat | grep -E "(DroneSDK|DroneCam)"
```

## 🔧 Dependências Incluídas

### Core Android
- ✅ AndroidX AppCompat 1.6.1
- ✅ Material Design 1.11.0
- ✅ ConstraintLayout 2.1.4
- ✅ Lifecycle Components 2.7.0

### Rede e Comunicação
- ✅ OkHttp 4.12.0
- ✅ Retrofit 2.9.0
- ✅ Gson 2.10.1

### Mídia e Vídeo
- ✅ ExoPlayer 2.19.1 (com suporte RTSP)
- ✅ Glide 4.16.0
- ✅ Camera2 API

### Utilitários
- ✅ Dexter 6.2.3 (Permissões)
- ✅ RxJava 3.1.8
- ✅ Work Manager 2.9.0

## 🚨 Solução de Problemas

### Erro: "SDK not found"
**Solução**:
1. Vá em `File > Project Structure`
2. Em "SDK Location", defina o caminho correto
3. Clique "Apply" e "OK"

### Erro: "Gradle sync failed"
**Solução**:
1. Vá em `File > Invalidate Caches and Restart`
2. Aguarde o reinício
3. Se persistir, delete `.gradle` e `.idea` na pasta do projeto

### Erro: "Build failed"
**Solução**:
1. Limpe o projeto: `Build > Clean Project`
2. Rebuild: `Build > Rebuild Project`
3. Verifique se o JDK está configurado corretamente

### Erro: "Device not found"
**Solução**:
1. Verifique se a depuração USB está ativada
2. Teste com `adb devices` no terminal
3. Reinstale os drivers USB do dispositivo

### Erro: "Manifest merger failed"
**Solução**:
1. Verifique conflitos no AndroidManifest.xml
2. Adicione `tools:replace="android:theme"` se necessário

## 📱 Teste com Drone Real

### 1. Configuração do Drone
1. Ligue o drone GoPlusDrone
2. Ative o modo Wi-Fi no drone
3. Anote o SSID da rede (ex: "GoPlusDrone_XXXX")

### 2. Conexão no App
1. Abra o DroneCam Pro
2. Toque em "Conectar Dispositivo"
3. Aguarde a busca automática
4. Selecione a rede do drone
5. Digite a senha (se necessário)
6. Toque em "Conectar"

### 3. Teste da Câmera
1. Após conectar, toque em "Controle da Câmera"
2. Aguarde o stream de vídeo carregar
3. Teste os controles:
   - ✅ Zoom (SeekBar)
   - ✅ Captura de foto
   - ✅ Gravação de vídeo
   - ✅ Rotação da câmera

## 🎯 URLs de Teste

### Stream RTSP
```
rtsp://192.168.25.1:8080/?action=stream
```

### Comandos TCP
```
IP: 192.168.25.1
Porta: 8081
```

## 📞 Suporte

### Logs Úteis
```bash
# Logs gerais do app
adb logcat | grep DroneCam

# Logs do SDK
adb logcat | grep DroneSDK

# Logs de rede
adb logcat | grep -E "(okhttp|retrofit)"

# Logs de vídeo
adb logcat | grep ExoPlayer
```

### Arquivos de Configuração
- `build.gradle` - Dependências e configurações
- `AndroidManifest.xml` - Permissões e Activities
- `local.properties` - Caminho do SDK
- `gradle.properties` - Propriedades do Gradle

---

**🎉 Projeto Pronto para Desenvolvimento e Produção!**

Todas as dependências estão incluídas e o projeto está configurado para compilação direta no Android Studio.

