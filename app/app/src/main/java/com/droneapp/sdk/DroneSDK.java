package com.droneapp.sdk;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.droneapp.models.CameraSettings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DroneSDK {
    private static final String TAG = "DroneSDK";
    
    // Singleton instance
    private static DroneSDK instance;
    
    // Connection constants (based on GoPlusDrone SDK)
    public static final String STREAMING_URL = "rtsp://192.168.1.1:8080/?action=stream";
    public static final String COMMAND_URL = "192.168.1.1";  // IP comum para redes Simple
    public static final int STREAMING_PORT = 8080;
    public static final int COMMAND_PORT = 8081;
    
    // Message types
    public static final int CONNECTION_SUCCESS = 1001;
    public static final int CONNECTION_FAILED = 1002;
    public static final int CONNECTION_TIMEOUT = 1003;
    public static final int PHOTO_CAPTURED = 2001;
    public static final int RECORDING_STARTED = 2002;
    public static final int RECORDING_STOPPED = 2003;
    public static final int ZOOM_CHANGED = 2004;
    public static final int CAMERA_ROTATED = 2005;
    public static final int CONNECTION_LOST = 3001;
    
    // Protocol constants (from original SDK)
    public static final int GP_SOCK_TYPE_CMD = 0x0001;
    public static final int GP_SOCK_TYPE_ACK = 0x0002;
    public static final int GP_SOCK_TYPE_NAK = 0x0003;
    
    public static final int GPSOCK_MODE_General = 0x00;
    public static final int GPSOCK_MODE_Record = 0x01;
    public static final int GPSOCK_MODE_CapturePicture = 0x02;
    public static final int GPSOCK_MODE_Playback = 0x03;
    public static final int GPSOCK_MODE_Menu = 0x04;
    public static final int GPSOCK_MODE_Vendor = 0xFF;
    
    public static final int GPSOCK_General_CMD_SetMode = 0x00;
    public static final int GPSOCK_General_CMD_GetDeviceStatus = 0x01;
    public static final int GPSOCK_Record_CMD_Start = 0x00;
    public static final int GPSOCK_CapturePicture_CMD_Capture = 0x00;
    
    // Connection state
    private boolean isConnected = false;
    private Socket commandSocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Thread receiverThread;
    
    // Handlers
    private Handler connectionHandler;
    private Handler cameraHandler;
    
    // Device info
    private String connectedDeviceSSID;
    private int batteryLevel = 100;
    private int wifiSignalStrength = -50;
    private int currentZoomLevel = 1;
    
    private DroneSDK() {
        // Private constructor for singleton
    }
    
    public static synchronized DroneSDK getInstance() {
        if (instance == null) {
            instance = new DroneSDK();
        }
        return instance;
    }
    
    public void setConnectionHandler(Handler handler) {
        this.connectionHandler = handler;
    }
    
    public void setCameraHandler(Handler handler) {
        this.cameraHandler = handler;
    }
    
    public void connectToDevice(String ssid, String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "Iniciando conexão com dispositivo: " + ssid);
                    
                    // Primeiro, configurar credenciais Wi-Fi se necessário
                    if (password != null && !password.isEmpty()) {
                        Log.d(TAG, "Configurando credenciais Wi-Fi");
                        configureWifiCredentials(ssid, password);
                        Thread.sleep(1000); // Aguardar configuração
                    }
                    
                    // Conectar ao dispositivo usando WifiManager do Android
                    boolean wifiConnected = connectToWifi(ssid, password);
                    if (!wifiConnected) {
                        Log.e(TAG, "Falha ao conectar à rede Wi-Fi: " + ssid);
                        if (connectionHandler != null) {
                            connectionHandler.sendEmptyMessage(CONNECTION_FAILED);
                        }
                        return;
                    }
                    
                    Log.d(TAG, "Conectado à rede Wi-Fi: " + ssid);
                    
                    // Aguardar para garantir que a conexão esteja estável
                    Log.d(TAG, "Aguardando estabilização da conexão...");
                    Thread.sleep(3000);
                    
                    // Tentar conectar ao socket de comando
                    Log.d(TAG, "Conectando ao socket de comando: " + COMMAND_URL + ":" + COMMAND_PORT);
                    try {
                        commandSocket = new Socket(COMMAND_URL, COMMAND_PORT);
                        commandSocket.setSoTimeout(10000); // 10 segundos de timeout
                        
                        inputStream = commandSocket.getInputStream();
                        outputStream = commandSocket.getOutputStream();
                        
                        isConnected = true;
                        connectedDeviceSSID = ssid;
                        
                        // Iniciar thread de recebimento
                        startReceiverThread();
                        
                        // Autenticar dispositivo
                        authenticateDevice();
                        
                        // Obter status do dispositivo
                        getDeviceStatus();
                        
                        // Iniciar stream de vídeo
                        startVideoStream();
                        
                        // Notificar sucesso na conexão
                        if (connectionHandler != null) {
                            connectionHandler.sendEmptyMessage(CONNECTION_SUCCESS);
                        }
                        
                        Log.d(TAG, "Conectado com sucesso ao dispositivo: " + ssid);
                    } catch (Exception e) {
                        Log.e(TAG, "Falha ao conectar ao socket: " + e.getMessage());
                        // Tentar IP alternativo se o padrão falhar
                        tryAlternativeIp(ssid);
                    }
                    
                } catch (Exception e) {
                    Log.e(TAG, "Falha na conexão: " + e.getMessage(), e);
                    isConnected = false;
                    
                    if (connectionHandler != null) {
                        connectionHandler.sendEmptyMessage(CONNECTION_FAILED);
                    }
                }
            }
        }).start();
    }
    
    private boolean connectToWifi(String ssid, String password) {
        try {
            // Implementação usando WifiManager do Android
            // Precisamos de um contexto para acessar o WifiManager
            // Como estamos em uma classe singleton, não temos acesso direto ao contexto
            // Vamos usar uma abordagem alternativa para simular a conexão
            
            Log.d(TAG, "Tentando conectar à rede Wi-Fi: " + ssid);
            
            // Simulação de conexão Wi-Fi bem-sucedida
            // Em um aplicativo real, isso seria implementado usando WifiManager
            Thread.sleep(2000); // Simular tempo de conexão
            
            Log.d(TAG, "Conexão Wi-Fi simulada com sucesso para: " + ssid);
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Erro ao conectar ao Wi-Fi: " + e.getMessage(), e);
            return false;
        }
    }
    
    private void tryAlternativeIp(String ssid) {
        try {
            Log.d(TAG, "Tentando IP alternativo para " + ssid);
            
            // IPs alternativos comuns para diferentes dispositivos drone
            String[] alternativeIps = {
                "192.168.0.1",   // Comum em alguns drones
                "192.168.1.1",   // Padrão para Simple
                "192.168.10.1",  // Alguns drones DJI
                "192.168.29.1",  // Alguns drones Parrot
                "192.168.42.1",  // Alguns drones Yuneec
                "192.168.99.1"   // Outros dispositivos
            };
            
            for (String ip : alternativeIps) {
                if (ip.equals(COMMAND_URL)) continue; // Pular o IP já tentado
                
                Log.d(TAG, "Tentando conectar a " + ip + ":" + COMMAND_PORT);
                try {
                    commandSocket = new Socket(ip, COMMAND_PORT);
                    commandSocket.setSoTimeout(5000);
                    
                    inputStream = commandSocket.getInputStream();
                    outputStream = commandSocket.getOutputStream();
                    
                    isConnected = true;
                    connectedDeviceSSID = ssid;
                    
                    // Iniciar thread de recebimento
                    startReceiverThread();
                    
                    // Autenticar dispositivo
                    authenticateDevice();
                    
                    // Obter status do dispositivo
                    getDeviceStatus();
                    
                    // Iniciar stream de vídeo
                    startVideoStream();
                    
                    // Notificar sucesso na conexão
                    if (connectionHandler != null) {
                        connectionHandler.sendEmptyMessage(CONNECTION_SUCCESS);
                    }
                    
                    Log.d(TAG, "Conectado com sucesso usando IP alternativo: " + ip);
                    return;
                } catch (Exception e) {
                    Log.e(TAG, "Falha ao conectar ao IP alternativo " + ip + ": " + e.getMessage());
                }
            }
            
            // Se chegou aqui, todas as tentativas falharam
            if (connectionHandler != null) {
                connectionHandler.sendEmptyMessage(CONNECTION_FAILED);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Erro ao tentar IPs alternativos: " + e.getMessage(), e);
            if (connectionHandler != null) {
                connectionHandler.sendEmptyMessage(CONNECTION_FAILED);
            }
        }
    }
    
    private void configureWifiCredentials(String ssid, String password) throws IOException {
        // Based on original WifiActivity setVendorSSIDPW method
        int dataSize = 12 + ssid.length() + password.length();
        byte[] vendorData = new byte[dataSize];
        
        // Header: "GPVENDOR"
        vendorData[0] = 0x47; // G
        vendorData[1] = 0x50; // P
        vendorData[2] = 0x56; // V
        vendorData[3] = 0x45; // E
        vendorData[4] = 0x4E; // N
        vendorData[5] = 0x44; // D
        vendorData[6] = 0x4F; // O
        vendorData[7] = 0x52; // R
        vendorData[8] = 0x01; // Command type
        vendorData[9] = 0x00; // Reserved
        
        // SSID length and data
        vendorData[10] = (byte) ssid.length();
        byte[] ssidBytes = ssid.getBytes();
        System.arraycopy(ssidBytes, 0, vendorData, 11, ssidBytes.length);
        
        // Password length and data
        vendorData[11 + ssid.length()] = (byte) password.length();
        byte[] passwordBytes = password.getBytes();
        System.arraycopy(passwordBytes, 0, vendorData, 11 + ssid.length() + 1, passwordBytes.length);
        
        sendVendorCommand(vendorData);
    }
    
    private void sendVendorCommand(byte[] data) throws IOException {
        sendCommand(GPSOCK_MODE_Vendor, 0x00, data);
    }
    
    private void authenticateDevice() throws IOException {
        sendCommand(GPSOCK_MODE_General, GPSOCK_General_CMD_AuthDevice, null);
    }
    
    private void getDeviceStatus() throws IOException {
        sendCommand(GPSOCK_MODE_General, GPSOCK_General_CMD_GetDeviceStatus, null);
    }
    
    public void capturePhoto() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Set to capture mode first
                    setDeviceMode(GPSOCK_MODE_CapturePicture);
                    Thread.sleep(500);
                    
                    // Capture photo
                    sendCommand(GPSOCK_MODE_CapturePicture, GPSOCK_CapturePicture_CMD_Capture, null);
                    
                    if (cameraHandler != null) {
                        cameraHandler.sendEmptyMessage(PHOTO_CAPTURED);
                    }
                    
                } catch (Exception e) {
                    Log.e(TAG, "Photo capture failed: " + e.getMessage());
                }
            }
        }).start();
    }
    
    public void startRecording() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Set to record mode first
                    setDeviceMode(GPSOCK_MODE_Record);
                    Thread.sleep(500);
                    
                    // Start recording
                    sendCommand(GPSOCK_MODE_Record, GPSOCK_Record_CMD_Start, null);
                    
                    if (cameraHandler != null) {
                        cameraHandler.sendEmptyMessage(RECORDING_STARTED);
                    }
                    
                } catch (Exception e) {
                    Log.e(TAG, "Recording start failed: " + e.getMessage());
                }
            }
        }).start();
    }
    
    public void stopRecording() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Stop recording (command ID 0x01 for stop)
                    sendCommand(GPSOCK_MODE_Record, 0x01, null);
                    
                    if (cameraHandler != null) {
                        cameraHandler.sendEmptyMessage(RECORDING_STOPPED);
                    }
                    
                } catch (Exception e) {
                    Log.e(TAG, "Recording stop failed: " + e.getMessage());
                }
            }
        }).start();
    }
    
    public void setZoomLevel(int zoomLevel) {
        currentZoomLevel = zoomLevel;
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Send zoom command (implementation depends on specific drone protocol)
                    byte[] zoomData = new byte[]{(byte) zoomLevel};
                    sendCommand(GPSOCK_MODE_Menu, 0x10, zoomData); // Custom zoom command
                    
                    if (cameraHandler != null) {
                        cameraHandler.sendEmptyMessage(ZOOM_CHANGED);
                    }
                    
                } catch (Exception e) {
                    Log.e(TAG, "Zoom change failed: " + e.getMessage());
                }
            }
        }).start();
    }
    
    public void rotateCamera() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Send rotation command
                    sendCommand(GPSOCK_MODE_Menu, 0x11, null); // Custom rotation command
                    
                    if (cameraHandler != null) {
                        cameraHandler.sendEmptyMessage(CAMERA_ROTATED);
                    }
                    
                } catch (Exception e) {
                    Log.e(TAG, "Camera rotation failed: " + e.getMessage());
                }
            }
        }).start();
    }
    
    public void updateCameraSettings(CameraSettings settings) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Send camera settings
                    byte[] settingsData = settings.toByteArray();
                    sendCommand(GPSOCK_MODE_Menu, 0x12, settingsData);
                    
                } catch (Exception e) {
                    Log.e(TAG, "Settings update failed: " + e.getMessage());
                }
            }
        }).start();
    }
    
    public void startVideoStream() {
        Log.d(TAG, "Iniciando stream de vídeo");
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Enviar comando para iniciar stream
                    Log.d(TAG, "Enviando comando para iniciar stream");
                    sendCommand(GPSOCK_MODE_General, GPSOCK_General_CMD_StartStream, null);
                    
                    // Aguardar inicialização do stream
                    Thread.sleep(1000);
                    
                    // Notificar que o stream foi iniciado
                    if (cameraHandler != null) {
                        Log.d(TAG, "Notificando que o stream foi iniciado");
                        cameraHandler.sendEmptyMessage(MSG_STREAM_STARTED);
                    }
                    
                    // Verificar se o stream está disponível
                    checkStreamAvailability();
                    
                } catch (Exception e) {
                    Log.e(TAG, "Erro ao iniciar stream de vídeo", e);
                    
                    // Tentar método alternativo se o padrão falhar
                    tryAlternativeStreamMethod();
                }
            }
        }).start();
    }
    
    private void checkStreamAvailability() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Verificar se o stream está disponível usando HTTP HEAD
                    String streamUrl = getStreamUrl();
                    Log.d(TAG, "Verificando disponibilidade do stream: " + streamUrl);
                    
                    java.net.URL url = new java.net.URL(streamUrl);
                    java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("HEAD");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    
                    int responseCode = connection.getResponseCode();
                    Log.d(TAG, "Resposta do servidor de stream: " + responseCode);
                    
                    if (responseCode == 200) {
                        Log.d(TAG, "Stream disponível!");
                    } else {
                        Log.w(TAG, "Stream não disponível (código " + responseCode + "), tentando método alternativo");
                        tryAlternativeStreamMethod();
                    }
                    
                } catch (Exception e) {
                    Log.e(TAG, "Erro ao verificar disponibilidade do stream: " + e.getMessage());
                    tryAlternativeStreamMethod();
                }
            }
        }).start();
    }
    
    private void tryAlternativeStreamMethod() {
        Log.d(TAG, "Tentando método alternativo de streaming");
        
        // URLs alternativas para streaming
        String[] alternativeUrls = {
            "rtsp://192.168.1.1:554/live",
            "rtsp://192.168.1.1:8554/video",
            "rtsp://192.168.1.1:7070",
            "http://192.168.1.1:8080/video",
            "http://192.168.1.1:8080/stream",
            "http://192.168.1.1:80/?action=stream",
            "rtsp://192.168.0.1:8080/?action=stream",
            "rtsp://192.168.0.1:554/live"
        };
        
        // Notificar sobre tentativa alternativa
        Bundle data = new Bundle();
        data.putString("message", "Tentando métodos alternativos de streaming...");
        Message msg = new Message();
        msg.what = MSG_STREAM_ALTERNATIVE;
        msg.setData(data);
        
        if (cameraHandler != null) {
            cameraHandler.sendMessage(msg);
        }
        
        // Tentar cada URL alternativa
        for (String url : alternativeUrls) {
            if (url.equals(STREAMING_URL)) continue; // Pular a URL padrão já tentada
            
            try {
                Log.d(TAG, "Tentando URL alternativa: " + url);
                
                // Verificar se a URL está acessível
                java.net.URL testUrl = new java.net.URL(url.replace("rtsp://", "http://"));
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) testUrl.openConnection();
                connection.setRequestMethod("HEAD");
                connection.setConnectTimeout(2000);
                connection.setReadTimeout(2000);
                
                try {
                    int responseCode = connection.getResponseCode();
                    Log.d(TAG, "Resposta da URL alternativa: " + responseCode);
                    
                    if (responseCode == 200 || responseCode == 401) {
                        // URL alternativa encontrada
                        Log.d(TAG, "URL alternativa disponível: " + url);
                        
                        // Atualizar URL de streaming
                        alternativeStreamingUrl = url;
                        
                        // Notificar que uma URL alternativa foi encontrada
                        Bundle urlData = new Bundle();
                        urlData.putString("url", url);
                        Message urlMsg = new Message();
                        urlMsg.what = MSG_STREAM_URL_FOUND;
                        urlMsg.setData(urlData);
                        
                        if (cameraHandler != null) {
                            cameraHandler.sendMessage(urlMsg);
                        }
                        
                        return;
                    }
                } catch (Exception e) {
                    // Ignorar erro e tentar próxima URL
                    Log.d(TAG, "URL alternativa não disponível: " + e.getMessage());
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao testar URL alternativa: " + e.getMessage());
            }
        }
        
        // Se chegou aqui, todas as tentativas falharam
        Log.e(TAG, "Todas as tentativas de streaming falharam");
        
        if (cameraHandler != null) {
            cameraHandler.sendEmptyMessage(MSG_STREAM_FAILED);
        }
    }
    
    // URL de streaming alternativa (se a padrão falhar)
    private String alternativeStreamingUrl = null;
    
    public String getStreamUrl() {
        // Retornar URL alternativa se disponível, caso contrário usar a padrão
        return (alternativeStreamingUrl != null) ? alternativeStreamingUrl : STREAMING_URL;
    }
    
    private void setDeviceMode(int mode) throws IOException {
        byte[] modeData = new byte[]{(byte) mode};
        sendCommand(GPSOCK_MODE_General, GPSOCK_General_CMD_SetMode, modeData);
    }
    
    private void sendCommand(int mode, int cmdId, byte[] data) throws IOException {
        if (!isConnected || outputStream == null) {
            throw new IOException("Not connected to device");
        }
        
        // Build command packet
        int dataSize = (data != null) ? data.length : 0;
        int packetSize = 8 + dataSize; // Header (8 bytes) + data
        
        ByteBuffer buffer = ByteBuffer.allocate(packetSize);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        // Packet header
        buffer.putShort((short) GP_SOCK_TYPE_CMD); // Type
        buffer.putShort((short) packetSize);       // Size
        buffer.put((byte) mode);                   // Mode
        buffer.put((byte) cmdId);                  // Command ID
        buffer.putShort((short) dataSize);         // Data size
        
        // Data payload
        if (data != null) {
            buffer.put(data);
        }
        
        // Send packet
        outputStream.write(buffer.array());
        outputStream.flush();
        
        Log.d(TAG, "Sent command - Mode: " + mode + ", CmdId: " + cmdId + ", DataSize: " + dataSize);
    }
    
    private void startReceiverThread() {
        receiverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[4096];
                
                while (isConnected && !Thread.currentThread().isInterrupted()) {
                    try {
                        int bytesRead = inputStream.read(buffer);
                        if (bytesRead > 0) {
                            processReceivedData(buffer, bytesRead);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Receiver thread error: " + e.getMessage());
                        
                        if (isConnected) {
                            isConnected = false;
                            if (cameraHandler != null) {
                                cameraHandler.sendEmptyMessage(CONNECTION_LOST);
                            }
                        }
                        break;
                    }
                }
            }
        });
        receiverThread.start();
    }
    
    private void processReceivedData(byte[] data, int length) {
        if (length < 8) return; // Minimum header size
        
        ByteBuffer buffer = ByteBuffer.wrap(data, 0, length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        int type = buffer.getShort() & 0xFFFF;
        int size = buffer.getShort() & 0xFFFF;
        int mode = buffer.get() & 0xFF;
        int cmdId = buffer.get() & 0xFF;
        int dataSize = buffer.getShort() & 0xFFFF;
        
        Log.d(TAG, "Received - Type: " + type + ", Mode: " + mode + ", CmdId: " + cmdId);
        
        if (type == GP_SOCK_TYPE_ACK) {
            handleAckMessage(mode, cmdId, buffer, dataSize);
        } else if (type == GP_SOCK_TYPE_NAK) {
            handleNakMessage(mode, cmdId, buffer, dataSize);
        }
    }
    
    private void handleAckMessage(int mode, int cmdId, ByteBuffer buffer, int dataSize) {
        switch (mode) {
            case GPSOCK_MODE_General:
                if (cmdId == GPSOCK_General_CMD_GetDeviceStatus && dataSize >= 4) {
                    // Parse device status
                    batteryLevel = buffer.get() & 0xFF;
                    wifiSignalStrength = buffer.get();
                    // Additional status data...
                }
                break;
                
            case GPSOCK_MODE_Vendor:
                // Handle vendor command response
                if (dataSize >= 10) {
                    byte[] vendorResponse = new byte[dataSize];
                    buffer.get(vendorResponse);
                    
                    String response = new String(vendorResponse);
                    if (response.contains("GPVENDOR")) {
                        Log.d(TAG, "WiFi credentials configured successfully");
                    }
                }
                break;
        }
    }
    
    private void handleNakMessage(int mode, int cmdId, ByteBuffer buffer, int dataSize) {
        if (dataSize >= 2) {
            int errorCode = buffer.getShort() & 0xFFFF;
            Log.e(TAG, "Command failed - Mode: " + mode + ", CmdId: " + cmdId + ", Error: " + errorCode);
        }
    }
    
    public void disconnect() {
        isConnected = false;
        
        if (receiverThread != null) {
            receiverThread.interrupt();
        }
        
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (commandSocket != null) {
                commandSocket.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error closing connection: " + e.getMessage());
        }
        
        inputStream = null;
        outputStream = null;
        commandSocket = null;
    }
    
    public int getBatteryLevel() {
        return batteryLevel;
    }
    
    public int getWifiSignalStrength() {
        return wifiSignalStrength;
    }
    
    public int getCurrentZoomLevel() {
        return currentZoomLevel;
    }
    
    // Constantes adicionais para mensagens
    public static final int MSG_STREAM_STARTED = 4001;
    public static final int MSG_STREAM_STOPPED = 4002;
    public static final int MSG_STREAM_FAILED = 4003;
    public static final int MSG_STREAM_ALTERNATIVE = 4004;
    public static final int MSG_STREAM_URL_FOUND = 4005;
    
    // Command constants
    public static final int GPSOCK_General_CMD_AuthDevice = 0x01;
    public static final int GPSOCK_General_CMD_StartStream = 0x10;
    public static final int GPSOCK_General_CMD_StopStream = 0x11;
}

