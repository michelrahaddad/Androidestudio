package com.droneapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.droneapp.adapters.MediaAdapter;
import com.droneapp.models.MediaFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    
    private GridView gridView;
    private TextView tvEmptyGallery;
    private MediaAdapter mediaAdapter;
    private List<MediaFile> mediaFiles;
    
    private static final String DRONE_FOLDER = "DroneCam";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        
        initializeViews();
        loadMediaFiles();
        setupGridView();
    }

    private void initializeViews() {
        gridView = findViewById(R.id.grid_view_media);
        tvEmptyGallery = findViewById(R.id.tv_empty_gallery);
        
        mediaFiles = new ArrayList<>();
        mediaAdapter = new MediaAdapter(this, mediaFiles);
    }

    private void loadMediaFiles() {
        mediaFiles.clear();
        
        // Load from external storage
        File externalDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), DRONE_FOLDER);
        if (externalDir.exists()) {
            loadFromDirectory(externalDir);
        }
        
        // Load from app's private directory
        File privateDir = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM), DRONE_FOLDER);
        if (privateDir.exists()) {
            loadFromDirectory(privateDir);
        }
        
        // Sort by date (newest first)
        Collections.sort(mediaFiles, new Comparator<MediaFile>() {
            @Override
            public int compare(MediaFile f1, MediaFile f2) {
                return Long.compare(f2.getDateTaken(), f1.getDateTaken());
            }
        });
        
        // Update UI
        if (mediaFiles.isEmpty()) {
            gridView.setVisibility(View.GONE);
            tvEmptyGallery.setVisibility(View.VISIBLE);
        } else {
            gridView.setVisibility(View.VISIBLE);
            tvEmptyGallery.setVisibility(View.GONE);
        }
        
        mediaAdapter.notifyDataSetChanged();
    }

    private void loadFromDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (isMediaFile(file)) {
                    MediaFile mediaFile = new MediaFile(file);
                    mediaFiles.add(mediaFile);
                }
            }
        }
    }

    private boolean isMediaFile(File file) {
        if (!file.isFile()) return false;
        
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
               name.endsWith(".png") || name.endsWith(".mp4") || 
               name.endsWith(".mov") || name.endsWith(".avi");
    }

    private void setupGridView() {
        gridView.setAdapter(mediaAdapter);
        
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MediaFile mediaFile = mediaFiles.get(position);
                openMediaFile(mediaFile);
            }
        });
        
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                MediaFile mediaFile = mediaFiles.get(position);
                showMediaOptions(mediaFile, position);
                return true;
            }
        });
    }

    private void openMediaFile(MediaFile mediaFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri fileUri = Uri.fromFile(mediaFile.getFile());
        
        if (mediaFile.isVideo()) {
            intent.setDataAndType(fileUri, "video/*");
        } else {
            intent.setDataAndType(fileUri, "image/*");
        }
        
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Não foi possível abrir o arquivo", Toast.LENGTH_SHORT).show();
        }
    }

    private void showMediaOptions(MediaFile mediaFile, int position) {
        String[] options = {"Visualizar", "Compartilhar", "Excluir"};
        
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(mediaFile.getFile().getName());
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Visualizar
                    openMediaFile(mediaFile);
                    break;
                case 1: // Compartilhar
                    shareMediaFile(mediaFile);
                    break;
                case 2: // Excluir
                    deleteMediaFile(mediaFile, position);
                    break;
            }
        });
        builder.show();
    }

    private void shareMediaFile(MediaFile mediaFile) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        Uri fileUri = Uri.fromFile(mediaFile.getFile());
        
        if (mediaFile.isVideo()) {
            shareIntent.setType("video/*");
        } else {
            shareIntent.setType("image/*");
        }
        
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Compartilhado via DroneCam");
        
        try {
            startActivity(Intent.createChooser(shareIntent, "Compartilhar"));
        } catch (Exception e) {
            Toast.makeText(this, "Não foi possível compartilhar o arquivo", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteMediaFile(MediaFile mediaFile, int position) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Excluir arquivo");
        builder.setMessage("Tem certeza que deseja excluir este arquivo?");
        builder.setPositiveButton("Sim", (dialog, which) -> {
            if (mediaFile.getFile().delete()) {
                mediaFiles.remove(position);
                mediaAdapter.notifyDataSetChanged();
                
                // Update empty state
                if (mediaFiles.isEmpty()) {
                    gridView.setVisibility(View.GONE);
                    tvEmptyGallery.setVisibility(View.VISIBLE);
                }
                
                Toast.makeText(this, "Arquivo excluído", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Erro ao excluir arquivo", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Não", null);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload media files in case new ones were added
        loadMediaFiles();
    }
}

