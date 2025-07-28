package com.droneapp.models;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MediaFile implements Serializable {
    private File file;
    private String name;
    private long size;
    private long dateTaken;
    private boolean isVideo;
    private String mimeType;
    private int duration; // For videos, in seconds
    private int width;
    private int height;

    public MediaFile(File file) {
        this.file = file;
        this.name = file.getName();
        this.size = file.length();
        this.dateTaken = file.lastModified();
        this.isVideo = isVideoFile(file.getName());
        this.mimeType = getMimeType(file.getName());
        
        // These would typically be extracted from EXIF data or media metadata
        this.duration = 0;
        this.width = 0;
        this.height = 0;
    }

    private boolean isVideoFile(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        return extension.equals("mp4") || extension.equals("mov") || 
               extension.equals("avi") || extension.equals("mkv") ||
               extension.equals("3gp") || extension.equals("webm");
    }

    private String getMimeType(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "bmp":
                return "image/bmp";
            case "webp":
                return "image/webp";
            case "mp4":
                return "video/mp4";
            case "mov":
                return "video/quicktime";
            case "avi":
                return "video/x-msvideo";
            case "mkv":
                return "video/x-matroska";
            case "3gp":
                return "video/3gpp";
            case "webm":
                return "video/webm";
            default:
                return "application/octet-stream";
        }
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }

    // Getters
    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public long getDateTaken() {
        return dateTaken;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public String getMimeType() {
        return mimeType;
    }

    public int getDuration() {
        return duration;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    // Setters
    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    // Utility methods
    public String getFormattedSize() {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format(Locale.getDefault(), "%.1f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format(Locale.getDefault(), "%.1f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format(Locale.getDefault(), "%.1f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(dateTaken));
    }

    public String getFormattedDuration() {
        if (!isVideo || duration <= 0) {
            return "";
        }
        
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    public String getResolution() {
        if (width > 0 && height > 0) {
            return width + "x" + height;
        }
        return "";
    }

    public boolean isImage() {
        return !isVideo;
    }

    public String getFileExtension() {
        return getFileExtension(name);
    }

    public boolean exists() {
        return file != null && file.exists();
    }

    public String getAbsolutePath() {
        return file != null ? file.getAbsolutePath() : "";
    }

    @Override
    public String toString() {
        return "MediaFile{" +
                "name='" + name + '\'' +
                ", size=" + getFormattedSize() +
                ", isVideo=" + isVideo +
                ", dateTaken=" + getFormattedDate() +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        MediaFile mediaFile = (MediaFile) obj;
        return file != null ? file.equals(mediaFile.file) : mediaFile.file == null;
    }

    @Override
    public int hashCode() {
        return file != null ? file.hashCode() : 0;
    }
}

