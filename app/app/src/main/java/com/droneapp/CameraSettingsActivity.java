package com.droneapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.droneapp.models.CameraSettings;

public class CameraSettingsActivity extends AppCompatActivity {
    
    private CameraSettings cameraSettings;
    
    // UI Components
    private Spinner spinnerResolution;
    private Spinner spinnerFps;
    private Spinner spinnerWhiteBalance;
    private Spinner spinnerVideoQuality;
    private SeekBar seekBarBrightness;
    private SeekBar seekBarContrast;
    private SeekBar seekBarSaturation;
    private SeekBar seekBarSharpness;
    private SeekBar seekBarExposure;
    private SeekBar seekBarMicrophone;
    private CheckBox checkBoxAutoFocus;
    private CheckBox checkBoxStabilization;
    private CheckBox checkBoxAudioRecording;
    private TextView tvBrightness;
    private TextView tvContrast;
    private TextView tvSaturation;
    private TextView tvSharpness;
    private TextView tvExposure;
    private TextView tvMicrophone;
    private Button btnSave;
    private Button btnCancel;
    private Button btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_settings);
        
        // Get current settings from intent
        cameraSettings = (CameraSettings) getIntent().getSerializableExtra("current_settings");
        if (cameraSettings == null) {
            cameraSettings = new CameraSettings();
        }
        
        initializeViews();
        setupSpinners();
        setupSeekBars();
        setupButtons();
        loadCurrentSettings();
    }

    private void initializeViews() {
        spinnerResolution = findViewById(R.id.spinner_resolution);
        spinnerFps = findViewById(R.id.spinner_fps);
        spinnerWhiteBalance = findViewById(R.id.spinner_white_balance);
        spinnerVideoQuality = findViewById(R.id.spinner_video_quality);
        
        seekBarBrightness = findViewById(R.id.seekbar_brightness);
        seekBarContrast = findViewById(R.id.seekbar_contrast);
        seekBarSaturation = findViewById(R.id.seekbar_saturation);
        seekBarSharpness = findViewById(R.id.seekbar_sharpness);
        seekBarExposure = findViewById(R.id.seekbar_exposure);
        seekBarMicrophone = findViewById(R.id.seekbar_microphone);
        
        checkBoxAutoFocus = findViewById(R.id.checkbox_auto_focus);
        checkBoxStabilization = findViewById(R.id.checkbox_stabilization);
        checkBoxAudioRecording = findViewById(R.id.checkbox_audio_recording);
        
        tvBrightness = findViewById(R.id.tv_brightness_value);
        tvContrast = findViewById(R.id.tv_contrast_value);
        tvSaturation = findViewById(R.id.tv_saturation_value);
        tvSharpness = findViewById(R.id.tv_sharpness_value);
        tvExposure = findViewById(R.id.tv_exposure_value);
        tvMicrophone = findViewById(R.id.tv_microphone_value);
        
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
        btnReset = findViewById(R.id.btn_reset);
    }

    private void setupSpinners() {
        // Resolution spinner
        ArrayAdapter<String> resolutionAdapter = new ArrayAdapter<>(
            this, android.R.layout.simple_spinner_item, cameraSettings.getAvailableResolutions());
        resolutionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerResolution.setAdapter(resolutionAdapter);
        
        // FPS spinner
        int[] fpsOptions = cameraSettings.getAvailableFps();
        String[] fpsStrings = new String[fpsOptions.length];
        for (int i = 0; i < fpsOptions.length; i++) {
            fpsStrings[i] = fpsOptions[i] + " fps";
        }
        ArrayAdapter<String> fpsAdapter = new ArrayAdapter<>(
            this, android.R.layout.simple_spinner_item, fpsStrings);
        fpsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFps.setAdapter(fpsAdapter);
        
        // White balance spinner
        ArrayAdapter<String> whiteBalanceAdapter = new ArrayAdapter<>(
            this, android.R.layout.simple_spinner_item, cameraSettings.getAvailableWhiteBalance());
        whiteBalanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWhiteBalance.setAdapter(whiteBalanceAdapter);
        
        // Video quality spinner
        ArrayAdapter<String> videoQualityAdapter = new ArrayAdapter<>(
            this, android.R.layout.simple_spinner_item, cameraSettings.getAvailableVideoQuality());
        videoQualityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVideoQuality.setAdapter(videoQualityAdapter);
    }

    private void setupSeekBars() {
        // Brightness
        seekBarBrightness.setMax(100);
        seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvBrightness.setText(progress + "%");
                cameraSettings.setBrightness(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        // Contrast
        seekBarContrast.setMax(100);
        seekBarContrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvContrast.setText(progress + "%");
                cameraSettings.setContrast(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        // Saturation
        seekBarSaturation.setMax(100);
        seekBarSaturation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvSaturation.setText(progress + "%");
                cameraSettings.setSaturation(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        // Sharpness
        seekBarSharpness.setMax(100);
        seekBarSharpness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvSharpness.setText(progress + "%");
                cameraSettings.setSharpness(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        // Exposure
        seekBarExposure.setMax(6); // -3 to +3
        seekBarExposure.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int exposureValue = progress - 3; // Convert to -3 to +3 range
                tvExposure.setText((exposureValue >= 0 ? "+" : "") + exposureValue);
                cameraSettings.setExposureCompensation(exposureValue);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        // Microphone
        seekBarMicrophone.setMax(100);
        seekBarMicrophone.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvMicrophone.setText(progress + "%");
                cameraSettings.setMicrophone(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupButtons() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });
        
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetToDefaults();
            }
        });
        
        // Spinner listeners
        spinnerResolution.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedResolution = cameraSettings.getAvailableResolutions()[position];
                cameraSettings.setResolution(selectedResolution);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        spinnerFps.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedFps = cameraSettings.getAvailableFps()[position];
                cameraSettings.setFps(selectedFps);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        spinnerWhiteBalance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedWhiteBalance = cameraSettings.getAvailableWhiteBalance()[position];
                cameraSettings.setWhiteBalance(selectedWhiteBalance);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        spinnerVideoQuality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedQuality = cameraSettings.getAvailableVideoQuality()[position];
                cameraSettings.setVideoQuality(selectedQuality);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        // Checkbox listeners
        checkBoxAutoFocus.setOnCheckedChangeListener((buttonView, isChecked) -> 
            cameraSettings.setAutoFocus(isChecked));
        
        checkBoxStabilization.setOnCheckedChangeListener((buttonView, isChecked) -> 
            cameraSettings.setImageStabilization(isChecked));
        
        checkBoxAudioRecording.setOnCheckedChangeListener((buttonView, isChecked) -> 
            cameraSettings.setAudioRecording(isChecked));
    }

    private void loadCurrentSettings() {
        // Set spinner selections
        setSpinnerSelection(spinnerResolution, cameraSettings.getAvailableResolutions(), cameraSettings.getResolution());
        setSpinnerSelection(spinnerFps, cameraSettings.getAvailableFps(), cameraSettings.getFps());
        setSpinnerSelection(spinnerWhiteBalance, cameraSettings.getAvailableWhiteBalance(), cameraSettings.getWhiteBalance());
        setSpinnerSelection(spinnerVideoQuality, cameraSettings.getAvailableVideoQuality(), cameraSettings.getVideoQuality());
        
        // Set seekbar values
        seekBarBrightness.setProgress(cameraSettings.getBrightness());
        seekBarContrast.setProgress(cameraSettings.getContrast());
        seekBarSaturation.setProgress(cameraSettings.getSaturation());
        seekBarSharpness.setProgress(cameraSettings.getSharpness());
        seekBarExposure.setProgress(cameraSettings.getExposureCompensation() + 3);
        seekBarMicrophone.setProgress(cameraSettings.getMicrophone());
        
        // Set checkbox values
        checkBoxAutoFocus.setChecked(cameraSettings.isAutoFocus());
        checkBoxStabilization.setChecked(cameraSettings.isImageStabilization());
        checkBoxAudioRecording.setChecked(cameraSettings.isAudioRecording());
        
        // Update text views
        tvBrightness.setText(cameraSettings.getBrightness() + "%");
        tvContrast.setText(cameraSettings.getContrast() + "%");
        tvSaturation.setText(cameraSettings.getSaturation() + "%");
        tvSharpness.setText(cameraSettings.getSharpness() + "%");
        tvExposure.setText((cameraSettings.getExposureCompensation() >= 0 ? "+" : "") + cameraSettings.getExposureCompensation());
        tvMicrophone.setText(cameraSettings.getMicrophone() + "%");
    }

    private void setSpinnerSelection(Spinner spinner, String[] options, String value) {
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void setSpinnerSelection(Spinner spinner, int[] options, int value) {
        for (int i = 0; i < options.length; i++) {
            if (options[i] == value) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void resetToDefaults() {
        cameraSettings = new CameraSettings();
        loadCurrentSettings();
    }

    private void saveSettings() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updated_settings", cameraSettings);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}

