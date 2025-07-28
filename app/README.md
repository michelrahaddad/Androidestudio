# DroneCam Pro - Aplicativo Android Nativo

## Visão Geral

DroneCam Pro é um aplicativo Android nativo desenvolvido para controle avançado de drones via Wi-Fi, baseado no SDK GoPlusDrone. O aplicativo oferece duas telas principais: conexão Wi-Fi e controle da câmera, com design futurista e funcionalidades completas.

## Características Principais

### 🔗 Tela de Conexão
- Busca automática de dispositivos drone via Wi-Fi
- Interface intuitiva para seleção de dispositivos
- Suporte a redes protegidas por senha
- Indicadores de força do sinal e status de conexão
- Design moderno com tema escuro e acentos laranja

### 📹 Tela da Câmera
- Stream de vídeo em tempo real via RTSP
- Controles completos de zoom (1x a 10x)
- Captura de fotos e gravação de vídeos
- Rotação da câmera
- Modo tela cheia
- Indicadores de status (bateria, Wi-Fi, resolução)
- Cronômetro de gravação
- Acesso rápido à galeria e configurações

### ⚙️ Configurações Avançadas
- Resolução de vídeo (360p a 4K)
- Taxa de quadros (15-120 FPS)
- Controles de imagem (brilho, contraste, saturação, nitidez)
- Balanço de branco automático e manual
- Compensação de exposição
- Foco automático e estabilização de imagem
- Gravação de áudio com controle de microfone

### 🖼️ Galeria Integrada
- Visualização em grade de fotos e vídeos
- Thumbnails automáticos
- Informações de arquivo (tamanho, duração, data)
- Compartilhamento e exclusão de arquivos
- Suporte a múltiplos formatos de mídia

## Arquitetura Técnica

### SDK de Integração
- **DroneSDK**: Classe principal para comunicação com o drone
- **Protocolo Wi-Fi**: Implementação baseada no GoPlusDrone SDK original
- **Comunicação Socket**: TCP para comandos e RTSP para streaming
- **Gerenciamento de Estado**: Handlers para conexão e câmera

### Modelos de Dados
- **WifiDevice**: Representação de dispositivos Wi-Fi disponíveis
- **CameraSettings**: Configurações completas da câmera
- **MediaFile**: Metadados de arquivos de mídia

### Interface do Usuário
- **Design System**: Tema escuro futurista com acentos laranja (#ff6b35)
- **Material Design**: Componentes modernos e responsivos
- **Layouts Adaptativos**: Suporte a diferentes tamanhos de tela
- **Animações**: Transições suaves e feedback visual

## Requisitos do Sistema

### Versões Android
- **Mínima**: Android 5.0 (API 21)
- **Alvo**: Android 13 (API 33)
- **Compilação**: SDK 33

### Permissões Necessárias
- `INTERNET`: Comunicação de rede
- `ACCESS_WIFI_STATE` / `CHANGE_WIFI_STATE`: Controle Wi-Fi
- `ACCESS_FINE_LOCATION`: Localização para Wi-Fi
- `CAMERA`: Acesso à câmera (opcional)
- `RECORD_AUDIO`: Gravação de áudio
- `WRITE_EXTERNAL_STORAGE`: Salvamento de arquivos

### Hardware Requerido
- Wi-Fi 802.11 b/g/n
- Processador ARM ou x86
- Mínimo 2GB RAM
- 100MB espaço livre

## Instalação e Configuração

### Pré-requisitos
1. Android Studio 4.0 ou superior
2. SDK Android 33
3. Gradle 7.0+
4. Java 8 ou superior

### Passos de Instalação
1. Clone ou extraia o projeto
2. Abra no Android Studio
3. Sincronize as dependências Gradle
4. Configure um dispositivo/emulador
5. Execute o projeto

### Configuração do Drone
1. Ligue o drone e ative o modo Wi-Fi
2. Conecte o dispositivo Android à rede do drone
3. Abra o aplicativo DroneCam Pro
4. Use a tela de conexão para estabelecer comunicação

## Estrutura do Projeto

```
app/
├── src/main/
│   ├── java/com/droneapp/
│   │   ├── MainActivity.java              # Tela principal
│   │   ├── ConnectionActivity.java        # Tela de conexão
│   │   ├── CameraActivity.java           # Tela da câmera
│   │   ├── CameraSettingsActivity.java   # Configurações
│   │   ├── GalleryActivity.java          # Galeria
│   │   ├── sdk/
│   │   │   └── DroneSDK.java             # SDK principal
│   │   ├── models/
│   │   │   ├── WifiDevice.java           # Modelo de dispositivo
│   │   │   ├── CameraSettings.java       # Configurações da câmera
│   │   │   └── MediaFile.java            # Arquivo de mídia
│   │   └── adapters/
│   │       └── MediaAdapter.java         # Adapter da galeria
│   ├── res/
│   │   ├── layout/                       # Layouts XML
│   │   ├── drawable/                     # Recursos gráficos
│   │   ├── values/                       # Cores, strings, estilos
│   │   └── mipmap/                       # Ícones do app
│   └── AndroidManifest.xml               # Configurações do app
├── build.gradle                          # Dependências
└── proguard-rules.pro                    # Regras de ofuscação
```

## Dependências Principais

### AndroidX
- AppCompat 1.6.1
- Material Design 1.9.0
- CardView, RecyclerView
- Lifecycle Extensions

### Rede e Mídia
- OkHttp 4.11.0 (HTTP client)
- Retrofit 2.9.0 (API client)
- ExoPlayer 2.18.7 (Video player)
- Glide 4.15.1 (Image loading)

### Utilitários
- Dexter 6.2.3 (Permissions)
- Gson 2.10.1 (JSON parsing)

## Protocolo de Comunicação

### Comandos Suportados
- **Conexão**: Autenticação e status do dispositivo
- **Câmera**: Captura, gravação, zoom, rotação
- **Configurações**: Resolução, qualidade, parâmetros de imagem
- **Vendor**: Configuração de credenciais Wi-Fi

### Formato de Pacotes
```
Header (8 bytes):
- Type (2 bytes): CMD/ACK/NAK
- Size (2 bytes): Tamanho total
- Mode (1 byte): Modo de operação
- CmdID (1 byte): ID do comando
- DataSize (2 bytes): Tamanho dos dados

Data (variável):
- Payload específico do comando
```

## Testes e Validação

### Testes Unitários
- Modelos de dados
- Lógica de negócio
- Utilitários

### Testes de Integração
- Comunicação SDK
- Interface do usuário
- Fluxos completos

### Testes de Dispositivo
- Diferentes versões Android
- Vários tamanhos de tela
- Cenários de conectividade

## Solução de Problemas

### Problemas Comuns

**Conexão Wi-Fi falha**
- Verifique se o drone está no modo Wi-Fi
- Confirme a senha da rede
- Reinicie o Wi-Fi do dispositivo

**Stream de vídeo não carrega**
- Verifique a conexão de rede
- Confirme a URL de streaming
- Reinicie o aplicativo

**Fotos/vídeos não salvam**
- Verifique permissões de armazenamento
- Confirme espaço livre no dispositivo
- Verifique configurações da câmera

### Logs de Debug
Use `adb logcat` com filtros:
```bash
adb logcat | grep -E "(DroneSDK|ConnectionActivity|CameraActivity)"
```

## Roadmap Futuro

### Versão 1.1
- [ ] Suporte a múltiplos drones
- [ ] Gravação em nuvem
- [ ] Edição básica de vídeos
- [ ] Compartilhamento social

### Versão 1.2
- [ ] Controle por gestos
- [ ] Realidade aumentada
- [ ] Voo autônomo básico
- [ ] Integração com mapas

## Contribuição

### Diretrizes
1. Siga o padrão de código existente
2. Adicione testes para novas funcionalidades
3. Atualize a documentação
4. Teste em múltiplos dispositivos

### Processo
1. Fork do repositório
2. Crie uma branch para sua feature
3. Implemente e teste as mudanças
4. Submeta um pull request

## Licença

Este projeto é proprietário e desenvolvido especificamente para integração com o SDK GoPlusDrone. Todos os direitos reservados.

## Suporte

Para suporte técnico ou dúvidas sobre o desenvolvimento:
- Consulte a documentação do SDK GoPlusDrone
- Verifique os logs de debug
- Teste em ambiente controlado

---

**DroneCam Pro v1.0.0** - Desenvolvido com foco em qualidade, performance e experiência do usuário.

