# Guia de Instalação - DroneCam Pro

## Pré-requisitos

### Software Necessário
- **Android Studio**: Versão 4.0 ou superior
- **Java Development Kit (JDK)**: Versão 8 ou superior
- **Android SDK**: API Level 33 (Android 13)
- **Gradle**: Versão 7.0 ou superior

### Hardware Recomendado
- **RAM**: Mínimo 8GB (recomendado 16GB)
- **Espaço em Disco**: 10GB livres
- **Processador**: Intel i5 ou equivalente AMD

## Instalação Passo a Passo

### 1. Preparação do Ambiente

#### Windows
1. Baixe e instale o Android Studio do site oficial
2. Durante a instalação, certifique-se de incluir:
   - Android SDK
   - Android SDK Platform-Tools
   - Android Virtual Device (AVD)

#### macOS
1. Baixe o Android Studio para macOS
2. Arraste para a pasta Applications
3. Execute e siga o assistente de configuração

#### Linux
1. Baixe o arquivo .tar.gz
2. Extraia para `/opt/android-studio`
3. Execute `./studio.sh` no diretório `bin`

### 2. Configuração do Android Studio

1. **Primeira Execução**:
   - Aceite os termos de licença
   - Escolha "Standard" setup
   - Aguarde o download dos componentes

2. **SDK Manager**:
   - Vá em `Tools > SDK Manager`
   - Na aba "SDK Platforms", instale:
     - Android 13 (API 33)
     - Android 12 (API 31)
     - Android 11 (API 30)
   - Na aba "SDK Tools", verifique se estão instalados:
     - Android SDK Build-Tools
     - Android SDK Platform-Tools
     - Android SDK Tools
     - Google Play Services

### 3. Importação do Projeto

1. **Abrir Projeto**:
   - Inicie o Android Studio
   - Clique em "Open an existing Android Studio project"
   - Navegue até a pasta extraída `android_native_app`
   - Selecione a pasta e clique "OK"

2. **Sincronização Gradle**:
   - O Android Studio detectará automaticamente o projeto
   - Clique em "Sync Now" quando solicitado
   - Aguarde o download das dependências (pode levar alguns minutos)

### 4. Configuração do Dispositivo

#### Dispositivo Físico (Recomendado)
1. **Habilitar Opções do Desenvolvedor**:
   - Vá em `Configurações > Sobre o telefone`
   - Toque 7 vezes em "Número da versão"
   - Volte e acesse "Opções do desenvolvedor"

2. **Ativar Depuração USB**:
   - Em "Opções do desenvolvedor", ative "Depuração USB"
   - Conecte o dispositivo via USB
   - Autorize a depuração quando solicitado

#### Emulador Android (Alternativo)
1. **Criar AVD**:
   - Vá em `Tools > AVD Manager`
   - Clique em "Create Virtual Device"
   - Escolha um dispositivo (ex: Pixel 4)
   - Selecione API 33 (Android 13)
   - Finalize a criação

### 5. Compilação e Execução

1. **Primeira Compilação**:
   - Clique no botão "Build" (martelo) na toolbar
   - Aguarde a compilação completa
   - Verifique se não há erros no painel "Build"

2. **Executar Aplicativo**:
   - Clique no botão "Run" (play) na toolbar
   - Selecione o dispositivo/emulador
   - Aguarde a instalação e execução

### 6. Verificação da Instalação

1. **Tela Principal**:
   - O app deve abrir mostrando o logo do drone
   - Dois botões: "Conectar Dispositivo" e "Controle da Câmera"

2. **Teste de Navegação**:
   - Toque em "Conectar Dispositivo"
   - Deve abrir a tela de conexão Wi-Fi
   - Volte e teste "Controle da Câmera"

## Solução de Problemas

### Erro: "SDK not found"
**Solução**:
1. Vá em `File > Project Structure`
2. Em "SDK Location", defina o caminho correto
3. Geralmente: `C:\Users\[user]\AppData\Local\Android\Sdk` (Windows)

### Erro: "Gradle sync failed"
**Solução**:
1. Vá em `File > Invalidate Caches and Restart`
2. Aguarde o reinício e nova sincronização
3. Se persistir, delete a pasta `.gradle` no projeto

### Erro: "Device not found"
**Solução**:
1. Verifique se a depuração USB está ativada
2. Teste com `adb devices` no terminal
3. Reinstale os drivers USB do dispositivo

### Erro: "Build failed"
**Solução**:
1. Verifique a versão do Gradle em `gradle/wrapper/gradle-wrapper.properties`
2. Atualize para a versão compatível
3. Limpe o projeto: `Build > Clean Project`

## Configuração para Desenvolvimento

### Variáveis de Ambiente
Adicione ao PATH do sistema:
- `ANDROID_HOME`: Caminho para o Android SDK
- `JAVA_HOME`: Caminho para o JDK

### Configurações Recomendadas
1. **Gradle**:
   - Ative "Offline work" se tiver conexão lenta
   - Configure "Max heap size" para 4GB

2. **Editor**:
   - Ative "Auto import" para Java
   - Configure indentação para 4 espaços

## Próximos Passos

Após a instalação bem-sucedida:

1. **Leia a Documentação**: Consulte o `README.md` para entender a arquitetura
2. **Configure o Drone**: Siga as instruções de conexão Wi-Fi
3. **Teste as Funcionalidades**: Explore todas as telas e recursos
4. **Personalize**: Modifique cores e textos conforme necessário

## Suporte

Para problemas específicos:
- Consulte os logs do Android Studio
- Verifique a documentação do Android Developer
- Teste em diferentes dispositivos/versões Android

---

**Versão do Guia**: 1.0.0  
**Compatibilidade**: Android Studio 4.0+, API 21-33

