package com.droneapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.droneapp.R;
import com.droneapp.models.MediaFile;

import java.util.List;

public class MediaAdapter extends BaseAdapter {
    private Context context;
    private List<MediaFile> mediaFiles;
    private LayoutInflater inflater;

    public MediaAdapter(Context context, List<MediaFile> mediaFiles) {
        this.context = context;
        this.mediaFiles = mediaFiles;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mediaFiles.size();
    }

    @Override
    public Object getItem(int position) {
        return mediaFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_media_grid, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.iv_thumbnail);
            holder.tvDuration = convertView.findViewById(R.id.tv_duration);
            holder.tvSize = convertView.findViewById(R.id.tv_size);
            holder.ivPlayIcon = convertView.findViewById(R.id.iv_play_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MediaFile mediaFile = mediaFiles.get(position);

        // Load thumbnail
        loadThumbnail(holder.imageView, mediaFile);

        // Show/hide video indicators
        if (mediaFile.isVideo()) {
            holder.ivPlayIcon.setVisibility(View.VISIBLE);
            holder.tvDuration.setVisibility(View.VISIBLE);
            holder.tvDuration.setText(mediaFile.getFormattedDuration());
        } else {
            holder.ivPlayIcon.setVisibility(View.GONE);
            holder.tvDuration.setVisibility(View.GONE);
        }

        // Show file size
        holder.tvSize.setText(mediaFile.getFormattedSize());

        return convertView;
    }

    private void loadThumbnail(ImageView imageView, MediaFile mediaFile) {
        // This is a simplified thumbnail loading
        // In a production app, you'd want to use a proper image loading library like Glide
        
        new Thread(() -> {
            Bitmap thumbnail = null;
            
            try {
                if (mediaFile.isVideo()) {
                    // Create video thumbnail
                    thumbnail = ThumbnailUtils.createVideoThumbnail(
                        mediaFile.getAbsolutePath(),
                        MediaStore.Images.Thumbnails.MINI_KIND
                    );
                } else {
                    // Create image thumbnail
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 4; // Scale down by factor of 4
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    
                    thumbnail = BitmapFactory.decodeFile(
                        mediaFile.getAbsolutePath(), 
                        options
                    );
                }
                
                // Update UI on main thread
                final Bitmap finalThumbnail = thumbnail;
                if (context instanceof android.app.Activity) {
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        if (finalThumbnail != null) {
                            imageView.setImageBitmap(finalThumbnail);
                        } else {
                            imageView.setImageResource(R.drawable.ic_broken_image);
                        }
                    });
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                // Set error image on main thread
                if (context instanceof android.app.Activity) {
                    ((android.app.Activity) context).runOnUiThread(() -> 
                        imageView.setImageResource(R.drawable.ic_broken_image)
                    );
                }
            }
        }).start();
    }

    static class ViewHolder {
        ImageView imageView;
        TextView tvDuration;
        TextView tvSize;
        ImageView ivPlayIcon;
    }

    public void updateMediaFiles(List<MediaFile> newMediaFiles) {
        this.mediaFiles = newMediaFiles;
        notifyDataSetChanged();
    }

    public void addMediaFile(MediaFile mediaFile) {
        this.mediaFiles.add(0, mediaFile); // Add to beginning
        notifyDataSetChanged();
    }

    public void removeMediaFile(int position) {
        if (position >= 0 && position < mediaFiles.size()) {
            mediaFiles.remove(position);
            notifyDataSetChanged();
        }
    }
}

