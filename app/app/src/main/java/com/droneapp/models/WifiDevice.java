package com.droneapp.models;

import java.io.Serializable;

public class WifiDevice implements Serializable {
    private String ssid;
    private int signalLevel;
    private int frequency;
    private String securityType;
    private boolean isConnected;
    private long lastSeen;

    public WifiDevice() {
        this.lastSeen = System.currentTimeMillis();
    }

    public WifiDevice(String ssid, int signalLevel, int frequency, String securityType) {
        this.ssid = ssid;
        this.signalLevel = signalLevel;
        this.frequency = frequency;
        this.securityType = securityType;
        this.isConnected = false;
        this.lastSeen = System.currentTimeMillis();
    }

    // Getters
    public String getSsid() {
        return ssid;
    }

    public int getSignalLevel() {
        return signalLevel;
    }

    public int getFrequency() {
        return frequency;
    }

    public String getSecurityType() {
        return securityType;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    // Setters
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public void setSignalLevel(int signalLevel) {
        this.signalLevel = signalLevel;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void setSecurityType(String securityType) {
        this.securityType = securityType;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    // Utility methods
    public String getSignalStrength() {
        if (signalLevel >= -50) {
            return "Excelente";
        } else if (signalLevel >= -60) {
            return "Bom";
        } else if (signalLevel >= -70) {
            return "Regular";
        } else {
            return "Fraco";
        }
    }

    public int getSignalBars() {
        if (signalLevel >= -50) {
            return 4;
        } else if (signalLevel >= -60) {
            return 3;
        } else if (signalLevel >= -70) {
            return 2;
        } else if (signalLevel >= -80) {
            return 1;
        } else {
            return 0;
        }
    }

    public String getFrequencyBand() {
        if (frequency >= 2400 && frequency <= 2500) {
            return "2.4 GHz";
        } else if (frequency >= 5000 && frequency <= 6000) {
            return "5 GHz";
        } else {
            return "Desconhecido";
        }
    }

    public boolean isSecured() {
        return !"Open".equals(securityType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ssid);
        sb.append(" (").append(getSignalStrength()).append(")");
        
        if (isSecured()) {
            sb.append(" 🔒");
        }
        
        if (isConnected) {
            sb.append(" ✓");
        }
        
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        WifiDevice that = (WifiDevice) obj;
        return ssid != null ? ssid.equals(that.ssid) : that.ssid == null;
    }

    @Override
    public int hashCode() {
        return ssid != null ? ssid.hashCode() : 0;
    }
}

