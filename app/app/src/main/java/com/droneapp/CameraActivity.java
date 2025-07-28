package com.droneapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

import com.droneapp.sdk.DroneSDK;
import com.droneapp.models.CameraSettings;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "CameraActivity";
    
    // UI Components
    private VideoView videoView;
    private ImageButton btnCapture;
    private ImageButton btnRecord;
    private ImageButton btnZoomIn;
    private ImageButton btnZoomOut;
    private ImageButton btnRotate;
    private ImageButton btnSettings;
    private ImageButton btnGallery;
    private ImageButton btnFullscreen;
    private SeekBar seekBarZoom;
    private TextView tvZoomLevel;
    private TextView tvRecordingTime;
    private TextView tvBatteryLevel;
    private TextView tvWifiSignal;
    private TextView tvResolution;
    private ProgressBar progressBarConnection;
    private View controlsOverlay;
    
    // SDK and Settings
    private DroneSDK droneSDK;
    private CameraSettings cameraSettings;
    private String deviceSSID;
    
    // Recording state
    private boolean isRecording = false;
    private boolean isFullscreen = false;
    private long recordingStartTime = 0;
    private Handler recordingTimeHandler = new Handler();
    private Runnable recordingTimeRunnable;
    
    // Zoom settings
    private int currentZoomLevel = 1;
    private int maxZoomLevel = 10;
    
    // Handler para receber mensagens do SDK
    private Handler cameraHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case DroneSDK.PHOTO_CAPTURED:
                    Toast.makeText(CameraActivity.this, "Foto capturada com sucesso", Toast.LENGTH_SHORT).show();
                    return true;
                    
                case DroneSDK.RECORDING_STARTED:
                    Toast.makeText(CameraActivity.this, "Gravação iniciada", Toast.LENGTH_SHORT).show();
                    return true;
                    
                case DroneSDK.RECORDING_STOPPED:
                    Toast.makeText(CameraActivity.this, "Gravação finalizada", Toast.LENGTH_SHORT).show();
                    return true;
                    
                case DroneSDK.CONNECTION_LOST:
                    Toast.makeText(CameraActivity.this, "Conexão perdida", Toast.LENGTH_LONG).show();
                    finish(); // Voltar para a tela de conexão
                    return true;
                    
                case DroneSDK.MSG_STREAM_STARTED:
                    Log.d(TAG, "Stream iniciado pelo SDK");
                    return true;
                    
                case DroneSDK.MSG_STREAM_FAILED:
                    Toast.makeText(CameraActivity.this, "Falha ao iniciar stream", Toast.LENGTH_SHORT).show();
                    return true;
                    
                case DroneSDK.MSG_STREAM_ALTERNATIVE:
                    String message = msg.getData().getString("message");
                    Toast.makeText(CameraActivity.this, message, Toast.LENGTH_SHORT).show();
                    return true;
                    
                case DroneSDK.MSG_STREAM_URL_FOUND:
                    String url = msg.getData().getString("url");
                    Log.d(TAG, "Nova URL de stream encontrada: " + url);
                    tryLoadVideoStream(); // Tentar carregar com a nova URL
                    return true;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        
        // Get device SSID from intent
        deviceSSID = getIntent().getStringExtra("device_ssid");
        
        initializeViews();
        initializeDroneSDK();
        setupListeners();
        startVideoStream();
    }

    private void initializeViews() {
        videoView = findViewById(R.id.video_view);
        btnCapture = findViewById(R.id.btn_capture);
        btnRecord = findViewById(R.id.btn_record);
        btnZoomIn = findViewById(R.id.btn_zoom_in);
        btnZoomOut = findViewById(R.id.btn_zoom_out);
        btnRotate = findViewById(R.id.btn_rotate);
        btnSettings = findViewById(R.id.btn_settings);
        btnGallery = findViewById(R.id.btn_gallery);
        btnFullscreen = findViewById(R.id.btn_fullscreen);
        seekBarZoom = findViewById(R.id.seekbar_zoom);
        tvZoomLevel = findViewById(R.id.tv_zoom_level);
        tvRecordingTime = findViewById(R.id.tv_recording_time);
        tvBatteryLevel = findViewById(R.id.tv_battery_level);
        tvWifiSignal = findViewById(R.id.tv_wifi_signal);
        tvResolution = findViewById(R.id.tv_resolution);
        progressBarConnection = findViewById(R.id.progress_connection);
        controlsOverlay = findViewById(R.id.controls_overlay);
        
        // Setup zoom seekbar
        seekBarZoom.setMax(maxZoomLevel - 1);
        seekBarZoom.setProgress(0);
        updateZoomDisplay();
        
        // Hide recording time initially
        tvRecordingTime.setVisibility(View.GONE);
    }

    private void initializeDroneSDK() {
        droneSDK = DroneSDK.getInstance();
        droneSDK.setCameraHandler(cameraHandler);
        
        // Initialize camera settings
        cameraSettings = new CameraSettings();
        cameraSettings.setResolution("1920x1080");
        cameraSettings.setFps(30);
        cameraSettings.setBrightness(50);
        cameraSettings.setContrast(50);
    }

    private void setupListeners() {
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturePhoto();
            }
        });

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleRecording();
            }
        });

        btnZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomIn();
            }
        });

        btnZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomOut();
            }
        });

        btnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateCamera();
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btnFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFullscreen();
            }
        });

        seekBarZoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentZoomLevel = progress + 1;
                    updateZoomDisplay();
                    droneSDK.setZoomLevel(currentZoomLevel);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Video view click to hide/show controls
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleControlsVisibility();
            }
        });
    }

    private void startVideoStream() {
        progressBarConnection.setVisibility(View.VISIBLE);
        
        // Configurar handler para receber mensagens do SDK
        droneSDK.setCameraHandler(cameraHandler);
        
        // Iniciar streaming do drone
        droneSDK.startVideoStream();
        
        // Tentar iniciar o VideoView com a URL de streaming
        tryLoadVideoStream();
    }
    
    private void tryLoadVideoStream() {
        try {
            String streamUrl = droneSDK.getStreamUrl();
            Log.d(TAG, "Tentando carregar stream: " + streamUrl);
            
            if (streamUrl != null) {
                Uri videoUri = Uri.parse(streamUrl);
                videoView.setVideoURI(videoUri);
                
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        progressBarConnection.setVisibility(View.GONE);
                        videoView.start();
                        updateConnectionStatus();
                        
                        // Configurar loop contínuo
                        mp.setLooping(true);
                        
                        // Configurar volume
                        mp.setVolume(0.5f, 0.5f);
                        
                        Log.d(TAG, "Stream iniciado com sucesso!");
                    }
                });
                
                videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        Log.e(TAG, "Erro no stream de vídeo: " + what + ", " + extra);
                        
                        // Tentar novamente após um atraso
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "Tentando reconectar ao stream...");
                                tryLoadVideoStream();
                            }
                        }, 3000);
                        
                        return true;
                    }
                });
                
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // Reiniciar o stream se terminar
                        videoView.start();
                    }
                });
                
                // Iniciar reprodução
                videoView.start();
                
            } else {
                progressBarConnection.setVisibility(View.GONE);
                Toast.makeText(this, "URL de streaming não disponível", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao iniciar stream: " + e.getMessage(), e);
            progressBarConnection.setVisibility(View.GONE);
            Toast.makeText(this, "Erro ao iniciar stream de vídeo", Toast.LENGTH_SHORT).show();
        }
    }

    private void capturePhoto() {
        btnCapture.setEnabled(false);
        
        // Animate button
        btnCapture.animate().scaleX(0.8f).scaleY(0.8f).setDuration(100)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        btnCapture.animate().scaleX(1f).scaleY(1f).setDuration(100);
                    }
                });
        
        droneSDK.capturePhoto();
        
        // Re-enable button after delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btnCapture.setEnabled(true);
            }
        }, 2000);
    }

    private void toggleRecording() {
        if (isRecording) {
            stopRecording();
        } else {
            startRecording();
        }
    }

    private void startRecording() {
        isRecording = true;
        recordingStartTime = System.currentTimeMillis();
        
        // Update UI
        btnRecord.setImageResource(R.drawable.ic_stop_recording);
        tvRecordingTime.setVisibility(View.VISIBLE);
        
        // Start recording timer
        startRecordingTimer();
        
        // Start recording via SDK
        droneSDK.startRecording();
        
        Toast.makeText(this, "Gravação iniciada", Toast.LENGTH_SHORT).show();
    }

    private void stopRecording() {
        isRecording = false;
        
        // Update UI
        btnRecord.setImageResource(R.drawable.ic_record);
        tvRecordingTime.setVisibility(View.GONE);
        
        // Stop recording timer
        if (recordingTimeRunnable != null) {
            recordingTimeHandler.removeCallbacks(recordingTimeRunnable);
        }
        
        // Stop recording via SDK
        droneSDK.stopRecording();
        
        Toast.makeText(this, "Gravação finalizada", Toast.LENGTH_SHORT).show();
    }

    private void startRecordingTimer() {
        recordingTimeRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRecording) {
                    long elapsedTime = System.currentTimeMillis() - recordingStartTime;
                    updateRecordingTime(elapsedTime);
                    recordingTimeHandler.postDelayed(this, 1000);
                }
            }
        };
        recordingTimeHandler.post(recordingTimeRunnable);
    }

    private void updateRecordingTime(long elapsedTime) {
        int seconds = (int) (elapsedTime / 1000) % 60;
        int minutes = (int) ((elapsedTime / (1000 * 60)) % 60);
        int hours = (int) ((elapsedTime / (1000 * 60 * 60)) % 24);
        
        String timeString;
        if (hours > 0) {
            timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeString = String.format("%02d:%02d", minutes, seconds);
        }
        
        tvRecordingTime.setText(timeString);
    }

    private void zoomIn() {
        if (currentZoomLevel < maxZoomLevel) {
            currentZoomLevel++;
            updateZoomDisplay();
            seekBarZoom.setProgress(currentZoomLevel - 1);
            droneSDK.setZoomLevel(currentZoomLevel);
        }
    }

    private void zoomOut() {
        if (currentZoomLevel > 1) {
            currentZoomLevel--;
            updateZoomDisplay();
            seekBarZoom.setProgress(currentZoomLevel - 1);
            droneSDK.setZoomLevel(currentZoomLevel);
        }
    }

    private void updateZoomDisplay() {
        tvZoomLevel.setText(currentZoomLevel + "x");
    }

    private void rotateCamera() {
        droneSDK.rotateCamera();
    }

    private void openSettings() {
        Intent intent = new Intent(this, CameraSettingsActivity.class);
        intent.putExtra("device_ssid", deviceSSID);
        startActivity(intent);
    }

    private void openGallery() {
        Intent intent = new Intent(this, GalleryActivity.class);
        intent.putExtra("device_ssid", deviceSSID);
        startActivity(intent);
    }

    private void toggleFullscreen() {
        isFullscreen = !isFullscreen;
        
        if (isFullscreen) {
            // Hide status bar and navigation bar
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            
            // Hide action bar if present
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
            
            // Hide controls
            controlsOverlay.setVisibility(View.GONE);
            
            // Change icon
            btnFullscreen.setImageResource(R.drawable.ic_fullscreen_exit);
        } else {
            // Show status bar and navigation bar
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            
            // Show action bar if it was previously visible
            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
            }
            
            // Show controls
            controlsOverlay.setVisibility(View.VISIBLE);
            
            // Change icon
            btnFullscreen.setImageResource(R.drawable.ic_fullscreen);
        }
    }

    private void toggleControlsVisibility() {
        if (!isFullscreen) return;
        
        if (controlsOverlay.getVisibility() == View.VISIBLE) {
            controlsOverlay.setVisibility(View.GONE);
        } else {
            controlsOverlay.setVisibility(View.VISIBLE);
            
            // Auto-hide controls after 3 seconds
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isFullscreen) {
                        controlsOverlay.setVisibility(View.GONE);
                    }
                }
            }, 3000);
        }
    }

    private void updateConnectionStatus() {
        // Update battery level
        int batteryLevel = droneSDK.getBatteryLevel();
        tvBatteryLevel.setText(batteryLevel + "%");
        
        // Update WiFi signal strength
        int signalStrength = droneSDK.getWifiSignalStrength();
        tvWifiSignal.setText(signalStrength + " dBm");
        
        // Update resolution
        tvResolution.setText(cameraSettings.getResolution());
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Refresh connection status
        updateConnectionStatus();
        
        // Resume video if needed
        if (!videoView.isPlaying()) {
            videoView.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        // Pause video
        if (videoView.isPlaying()) {
            videoView.pause();
        }
        
        // Stop recording if active
        if (isRecording) {
            stopRecording();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Disconnect from device
        droneSDK.disconnect();
    }
}

