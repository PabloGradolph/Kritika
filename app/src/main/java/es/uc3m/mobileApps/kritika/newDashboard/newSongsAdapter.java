package es.uc3m.mobileApps.kritika.newDashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.Song;

public class newSongsAdapter extends RecyclerView.Adapter<newSongsAdapter.SongViewHolder> {
    private List<Song> songs;
    private LayoutInflater inflater;

    public newSongsAdapter(Context context, List<Song> songs) {
        this.inflater = LayoutInflater.from(context);
        this.songs = songs;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.new_song_item, parent, false);
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song currentSong = songs.get(position);
        // Cargar imagen con Glide
        Glide.with(holder.imageView.getContext())
                .load(currentSong.getImageUrl())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        SongViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.songsImageViewPoster);
        }
    }
}

