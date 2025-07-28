package com.droneapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.droneapp.sdk.DroneSDK;
import com.droneapp.models.WifiDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ConnectionActivity extends AppCompatActivity {
    private static final String TAG = "ConnectionActivity";
    
    private WifiManager wifiManager;
    private List<ScanResult> scanResults;
    private ArrayList<WifiDevice> deviceList = new ArrayList<>();
    private BroadcastReceiver wifiScanReceiver;
    
    // UI Components
    private ListView lvDevices;
    private EditText etPassword;
    private Button btnScan;
    private Button btnConnect;
    private ProgressBar progressBar;
    private TextView tvStatus;
    private TextView tvSelectedDevice;
    
    private ArrayAdapter<WifiDevice> deviceAdapter;
    private WifiDevice selectedDevice;
    private DroneSDK droneSDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        initializeViews();
        checkPermissions();
        initializeWifi();
        initializeDroneSDK();
        setupListeners();
        
        // Auto scan on start
        scanForDevices();
    }
    
    private static final int PERMISSIONS_REQUEST_CODE = 1001;
    
    private void checkPermissions() {
        // Android 6.0+ requer permissões de localização para scan de Wi-Fi
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            String[] permissions = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            };
            
            boolean needPermission = false;
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    needPermission = true;
                }
            }
            
            if (needPermission) {
                requestPermissions(permissions, PERMISSIONS_REQUEST_CODE);
                Log.d(TAG, "Solicitando permissões de localização");
            } else {
                Log.d(TAG, "Permissões já concedidas");
            }
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            
            if (allGranted) {
                Log.d(TAG, "Todas as permissões concedidas, iniciando scan");
                scanForDevices();
            } else {
                Log.d(TAG, "Permissões negadas, scan de Wi-Fi pode não funcionar");
                Toast.makeText(this, "Permissões de localização são necessárias para buscar dispositivos Wi-Fi", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initializeViews() {
        lvDevices = findViewById(R.id.lv_devices);
        etPassword = findViewById(R.id.et_password);
        btnScan = findViewById(R.id.btn_scan);
        btnConnect = findViewById(R.id.btn_connect);
        progressBar = findViewById(R.id.progress_bar);
        tvStatus = findViewById(R.id.tv_status);
        tvSelectedDevice = findViewById(R.id.tv_selected_device);
        
        // Setup ListView adapter
        deviceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceList);
        lvDevices.setAdapter(deviceAdapter);
        
        // Initially disable connect button
        btnConnect.setEnabled(false);
        progressBar.setVisibility(View.GONE);
    }

    private void initializeWifi() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        
        if (!wifiManager.isWifiEnabled()) {
            showWifiEnableDialog();
        }
        
        setupWifiScanReceiver();
    }

    private void initializeDroneSDK() {
        droneSDK = DroneSDK.getInstance();
        droneSDK.setConnectionHandler(connectionHandler);
    }

    private void showWifiEnableDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Wi-Fi Desabilitado");
        dialog.setMessage("Seu Wi-Fi está desabilitado. Deseja habilitá-lo?");
        dialog.setIcon(android.R.drawable.ic_dialog_info);
        dialog.setCancelable(false);
        dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                wifiManager.setWifiEnabled(true);
            }
        });
        dialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.show();
    }

    private void setupWifiScanReceiver() {
        wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Broadcast recebido: " + intent.getAction());
                
                if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                    boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                    Log.d(TAG, "Scan completado, success=" + success);
                    
                    List<ScanResult> results = wifiManager.getScanResults();
                    if (results != null) {
                        Log.d(TAG, "Resultados obtidos: " + results.size() + " redes");
                        processScanResults(results);
                    } else {
                        Log.e(TAG, "Nenhum resultado de scan disponível");
                        updateStatus("Erro ao buscar dispositivos");
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        };
        
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiScanReceiver, intentFilter);
        Log.d(TAG, "WifiScanReceiver registrado");
    }

    private boolean isDroneDevice(String ssid) {
        // Implementação baseada no SDK GoPlusDrone original
        // Detecta dispositivos drone reais, incluindo Simple
        String ssidLower = ssid.toLowerCase();
        
        // Dispositivos específicos
        if (ssidLower.startsWith("simple") || 
            ssidLower.startsWith("gp_") || 
            ssidLower.startsWith("drone") ||
            ssidLower.startsWith("fpv") ||
            ssidLower.startsWith("uav") ||
            ssidLower.startsWith("cam_") ||
            ssidLower.startsWith("wifi_uav")) {
            Log.d(TAG, "Dispositivo drone detectado por prefixo: " + ssid);
            return true;
        }
        
        // Padrões de nome comuns
        if (ssidLower.contains("drone") || 
            ssidLower.contains("goplus") || 
            ssidLower.contains("cam") ||
            ssidLower.contains("wifi") && (ssidLower.contains("uav") || ssidLower.contains("drone")) ||
            ssidLower.contains("fpv") ||
            ssidLower.contains("quadcopter") ||
            ssidLower.contains("dji") ||
            ssidLower.contains("phantom") ||
            ssidLower.contains("mavic") ||
            ssidLower.contains("parrot") ||
            ssidLower.contains("bebop") ||
            ssidLower.contains("anafi") ||
            ssidLower.contains("tello") ||
            ssidLower.contains("yuneec") ||
            ssidLower.contains("autel") ||
            ssidLower.contains("hubsan") ||
            ssidLower.contains("syma") ||
            ssidLower.contains("holy") && ssidLower.contains("stone")) {
            Log.d(TAG, "Dispositivo drone detectado por palavra-chave: " + ssid);
            return true;
        }
        
        // Padrões específicos para dispositivos Simple
        if (ssidLower.matches("simple-[a-f0-9]+")) {
            Log.d(TAG, "Dispositivo Simple detectado por padrão: " + ssid);
            return true;
        }
        
        return false;
    }

    private String getSecurityType(ScanResult result) {
        String capabilities = result.capabilities;
        if (capabilities.contains("WPA")) {
            return "WPA";
        } else if (capabilities.contains("WEP")) {
            return "WEP";
        } else {
            return "Open";
        }
    }

    private void setupListeners() {
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanForDevices();
            }
        });

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToDevice();
            }
        });

        lvDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedDevice = deviceList.get(position);
                tvSelectedDevice.setText("Selecionado: " + selectedDevice.getSsid());
                btnConnect.setEnabled(true);
                
                // Show password field if device is secured
                if (!"Open".equals(selectedDevice.getSecurityType())) {
                    etPassword.setVisibility(View.VISIBLE);
                } else {
                    etPassword.setVisibility(View.GONE);
                }
            }
        });
    }

    private void scanForDevices() {
        Log.d(TAG, "=== INICIANDO SCAN DE DISPOSITIVOS ===");
        updateStatus("Procurando dispositivos...");
        progressBar.setVisibility(View.VISIBLE);
        deviceList.clear();
        deviceAdapter.notifyDataSetChanged();
        
        // Verificar se Wi-Fi está habilitado
        if (!wifiManager.isWifiEnabled()) {
            Log.e(TAG, "Wi-Fi não está habilitado");
            updateStatus("Wi-Fi não está habilitado");
            progressBar.setVisibility(View.GONE);
            return;
        }
        
        // Verificar permissões de localização (Android 6.0+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Permissão de localização não concedida");
                updateStatus("Permissão de localização necessária");
                progressBar.setVisibility(View.GONE);
                return;
            }
        }
        
        Log.d(TAG, "Iniciando scan Wi-Fi...");
        boolean scanStarted = wifiManager.startScan();
        Log.d(TAG, "Scan iniciado: " + scanStarted);
        
        if (!scanStarted) {
            Log.w(TAG, "Falha ao iniciar scan, tentando obter resultados anteriores");
            // Fallback: usar resultados de scan anterior
            List<ScanResult> previousResults = wifiManager.getScanResults();
            if (previousResults != null && !previousResults.isEmpty()) {
                Log.d(TAG, "Usando resultados anteriores: " + previousResults.size() + " redes");
                processScanResults(previousResults);
            } else {
                Log.e(TAG, "Nenhum resultado de scan disponível");
                updateStatus("Erro ao buscar dispositivos");
                progressBar.setVisibility(View.GONE);
            }
        }
        
        // Timeout de segurança
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressBar.getVisibility() == View.VISIBLE) {
                    Log.w(TAG, "Timeout do scan, forçando processamento");
                    List<ScanResult> results = wifiManager.getScanResults();
                    if (results != null) {
                        processScanResults(results);
                    } else {
                        updateStatus("Timeout na busca de dispositivos");
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        }, 10000); // 10 segundos timeout
    }
    
    private void processScanResults(List<ScanResult> scanResults) {
        Log.d(TAG, "=== PROCESSANDO RESULTADOS DO SCAN (HARDWARE REAL) ===");
        Log.d(TAG, "Total de redes encontradas: " + scanResults.size());
        
        deviceList.clear();
        
        for (ScanResult result : scanResults) {
            if (result.SSID == null || result.SSID.isEmpty()) {
                Log.d(TAG, "Ignorando rede sem SSID");
                continue;
            }
            
            Log.d(TAG, "Verificando rede: '" + result.SSID + "' (Sinal: " + result.level + " dBm)");
            
            // Filtro baseado no SDK GoPlusDrone original - apenas dispositivos drone reais
            if (isDroneDevice(result.SSID)) {
                Log.d(TAG, "DISPOSITIVO DRONE DETECTADO: " + result.SSID);
                
                boolean alreadyExists = false;
                for (WifiDevice device : deviceList) {
                    if (device.getSsid().equals(result.SSID)) {
                        alreadyExists = true;
                        break;
                    }
                }
                
                if (!alreadyExists) {
                    WifiDevice device = new WifiDevice(
                        result.SSID,
                        result.level,
                        result.frequency,
                        getSecurityType(result)
                    );
                    deviceList.add(device);
                    Log.d(TAG, "Dispositivo drone adicionado: " + result.SSID);
                }
            } else {
                Log.d(TAG, "Rede '" + result.SSID + "' não é um dispositivo drone compatível");
            }
        }
        
        // Sort by signal strength
        Collections.sort(deviceList, new Comparator<WifiDevice>() {
            @Override
            public int compare(WifiDevice d1, WifiDevice d2) {
                return Integer.compare(d2.getSignalLevel(), d1.getSignalLevel());
            }
        });
        
        Log.d(TAG, "Total de dispositivos drone encontrados: " + deviceList.size());
        deviceAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        
        if (deviceList.size() > 0) {
            updateStatus("Encontrados " + deviceList.size() + " dispositivos drone");
        } else {
            updateStatus("Nenhum dispositivo drone encontrado. Verifique se o drone está ligado e no modo Wi-Fi.");
        }
    }

    private void connectToDevice() {
        if (selectedDevice == null) {
            Toast.makeText(this, "Selecione um dispositivo primeiro", Toast.LENGTH_SHORT).show();
            return;
        }

        String password = etPassword.getText().toString();
        
        // Validate password if required
        if (!"Open".equals(selectedDevice.getSecurityType()) && password.isEmpty()) {
            Toast.makeText(this, "Digite a senha do dispositivo", Toast.LENGTH_SHORT).show();
            return;
        }

        updateStatus("Conectando ao " + selectedDevice.getSsid() + "...");
        progressBar.setVisibility(View.VISIBLE);
        btnConnect.setEnabled(false);

        // Use DroneSDK to connect
        droneSDK.connectToDevice(selectedDevice.getSsid(), password);
    }

    private Handler connectionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            
            switch (msg.what) {
                case DroneSDK.CONNECTION_SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    updateStatus("Conectado com sucesso!");
                    Toast.makeText(ConnectionActivity.this, "Conexão estabelecida", Toast.LENGTH_SHORT).show();
                    
                    // Navigate to camera activity
                    Intent intent = new Intent(ConnectionActivity.this, CameraActivity.class);
                    intent.putExtra("device_ssid", selectedDevice.getSsid());
                    startActivity(intent);
                    break;
                    
                case DroneSDK.CONNECTION_FAILED:
                    progressBar.setVisibility(View.GONE);
                    btnConnect.setEnabled(true);
                    updateStatus("Falha na conexão");
                    Toast.makeText(ConnectionActivity.this, "Falha ao conectar", Toast.LENGTH_SHORT).show();
                    break;
                    
                case DroneSDK.CONNECTION_TIMEOUT:
                    progressBar.setVisibility(View.GONE);
                    btnConnect.setEnabled(true);
                    updateStatus("Timeout na conexão");
                    Toast.makeText(ConnectionActivity.this, "Timeout na conexão", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void updateStatus(String status) {
        tvStatus.setText(status);
        Log.d(TAG, status);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wifiScanReceiver != null) {
            unregisterReceiver(wifiScanReceiver);
        }
        if (droneSDK != null) {
            droneSDK.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (droneSDK != null) {
            droneSDK.setConnectionHandler(connectionHandler);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (droneSDK != null) {
            droneSDK.setConnectionHandler(null);
        }
    }
}

