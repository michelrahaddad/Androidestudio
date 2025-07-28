package com.droneapp.models;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CameraSettings implements Serializable {
    private String resolution;
    private int fps;
    private int brightness;
    private int contrast;
    private int saturation;
    private int sharpness;
    private boolean autoFocus;
    private boolean imageStabilization;
    private String whiteBalance;
    private int exposureCompensation;
    private String videoQuality;
    private boolean audioRecording;
    private int microphone;

    public CameraSettings() {
        // Default settings
        this.resolution = "1920x1080";
        this.fps = 30;
        this.brightness = 50;
        this.contrast = 50;
        this.saturation = 50;
        this.sharpness = 50;
        this.autoFocus = true;
        this.imageStabilization = true;
        this.whiteBalance = "Auto";
        this.exposureCompensation = 0;
        this.videoQuality = "High";
        this.audioRecording = true;
        this.microphone = 50;
    }

    // Getters
    public String getResolution() {
        return resolution;
    }

    public int getFps() {
        return fps;
    }

    public int getBrightness() {
        return brightness;
    }

    public int getContrast() {
        return contrast;
    }

    public int getSaturation() {
        return saturation;
    }

    public int getSharpness() {
        return sharpness;
    }

    public boolean isAutoFocus() {
        return autoFocus;
    }

    public boolean isImageStabilization() {
        return imageStabilization;
    }

    public String getWhiteBalance() {
        return whiteBalance;
    }

    public int getExposureCompensation() {
        return exposureCompensation;
    }

    public String getVideoQuality() {
        return videoQuality;
    }

    public boolean isAudioRecording() {
        return audioRecording;
    }

    public int getMicrophone() {
        return microphone;
    }

    // Setters
    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public void setBrightness(int brightness) {
        this.brightness = Math.max(0, Math.min(100, brightness));
    }

    public void setContrast(int contrast) {
        this.contrast = Math.max(0, Math.min(100, contrast));
    }

    public void setSaturation(int saturation) {
        this.saturation = Math.max(0, Math.min(100, saturation));
    }

    public void setSharpness(int sharpness) {
        this.sharpness = Math.max(0, Math.min(100, sharpness));
    }

    public void setAutoFocus(boolean autoFocus) {
        this.autoFocus = autoFocus;
    }

    public void setImageStabilization(boolean imageStabilization) {
        this.imageStabilization = imageStabilization;
    }

    public void setWhiteBalance(String whiteBalance) {
        this.whiteBalance = whiteBalance;
    }

    public void setExposureCompensation(int exposureCompensation) {
        this.exposureCompensation = Math.max(-3, Math.min(3, exposureCompensation));
    }

    public void setVideoQuality(String videoQuality) {
        this.videoQuality = videoQuality;
    }

    public void setAudioRecording(boolean audioRecording) {
        this.audioRecording = audioRecording;
    }

    public void setMicrophone(int microphone) {
        this.microphone = Math.max(0, Math.min(100, microphone));
    }

    // Utility methods
    public String[] getAvailableResolutions() {
        return new String[]{
            "3840x2160", // 4K
            "2560x1440", // 2K
            "1920x1080", // Full HD
            "1280x720",  // HD
            "854x480",   // SD
            "640x360"    // Low
        };
    }

    public int[] getAvailableFps() {
        return new int[]{15, 24, 30, 60, 120};
    }

    public String[] getAvailableWhiteBalance() {
        return new String[]{
            "Auto",
            "Daylight",
            "Cloudy",
            "Tungsten",
            "Fluorescent",
            "Flash"
        };
    }

    public String[] getAvailableVideoQuality() {
        return new String[]{
            "Low",
            "Medium",
            "High",
            "Ultra"
        };
    }

    public int getResolutionWidth() {
        if (resolution != null && resolution.contains("x")) {
            return Integer.parseInt(resolution.split("x")[0]);
        }
        return 1920;
    }

    public int getResolutionHeight() {
        if (resolution != null && resolution.contains("x")) {
            return Integer.parseInt(resolution.split("x")[1]);
        }
        return 1080;
    }

    public boolean isHighDefinition() {
        int width = getResolutionWidth();
        return width >= 1280;
    }

    public boolean is4K() {
        int width = getResolutionWidth();
        return width >= 3840;
    }

    public long getEstimatedBitrate() {
        int width = getResolutionWidth();
        int height = getResolutionHeight();
        
        long pixelsPerSecond = (long) width * height * fps;
        
        switch (videoQuality) {
            case "Ultra":
                return pixelsPerSecond / 10; // High bitrate
            case "High":
                return pixelsPerSecond / 15;
            case "Medium":
                return pixelsPerSecond / 25;
            case "Low":
                return pixelsPerSecond / 40;
            default:
                return pixelsPerSecond / 20;
        }
    }

    // Convert settings to byte array for transmission to drone
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(64);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        
        // Resolution (4 bytes: width + height)
        buffer.putShort((short) getResolutionWidth());
        buffer.putShort((short) getResolutionHeight());
        
        // FPS (1 byte)
        buffer.put((byte) fps);
        
        // Image settings (4 bytes)
        buffer.put((byte) brightness);
        buffer.put((byte) contrast);
        buffer.put((byte) saturation);
        buffer.put((byte) sharpness);
        
        // Boolean settings (1 byte as flags)
        byte flags = 0;
        if (autoFocus) flags |= 0x01;
        if (imageStabilization) flags |= 0x02;
        if (audioRecording) flags |= 0x04;
        buffer.put(flags);
        
        // White balance (1 byte)
        buffer.put((byte) getWhiteBalanceIndex());
        
        // Exposure compensation (1 byte, signed)
        buffer.put((byte) exposureCompensation);
        
        // Video quality (1 byte)
        buffer.put((byte) getVideoQualityIndex());
        
        // Microphone level (1 byte)
        buffer.put((byte) microphone);
        
        // Padding to align
        while (buffer.position() < 64) {
            buffer.put((byte) 0);
        }
        
        return buffer.array();
    }

    private int getWhiteBalanceIndex() {
        String[] options = getAvailableWhiteBalance();
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(whiteBalance)) {
                return i;
            }
        }
        return 0; // Auto
    }

    private int getVideoQualityIndex() {
        String[] options = getAvailableVideoQuality();
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(videoQuality)) {
                return i;
            }
        }
        return 2; // High
    }

    @Override
    public String toString() {
        return "CameraSettings{" +
                "resolution='" + resolution + '\'' +
                ", fps=" + fps +
                ", brightness=" + brightness +
                ", contrast=" + contrast +
                ", videoQuality='" + videoQuality + '\'' +
                '}';
    }

    public CameraSettings copy() {
        CameraSettings copy = new CameraSettings();
        copy.resolution = this.resolution;
        copy.fps = this.fps;
        copy.brightness = this.brightness;
        copy.contrast = this.contrast;
        copy.saturation = this.saturation;
        copy.sharpness = this.sharpness;
        copy.autoFocus = this.autoFocus;
        copy.imageStabilization = this.imageStabilization;
        copy.whiteBalance = this.whiteBalance;
        copy.exposureCompensation = this.exposureCompensation;
        copy.videoQuality = this.videoQuality;
        copy.audioRecording = this.audioRecording;
        copy.microphone = this.microphone;
        return copy;
    }
}

