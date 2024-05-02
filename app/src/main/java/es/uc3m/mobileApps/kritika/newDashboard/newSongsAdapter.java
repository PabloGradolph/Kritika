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

/**
 * Adapter class for the RecyclerView to display songs.
 */
public class newSongsAdapter extends RecyclerView.Adapter<newSongsAdapter.SongViewHolder> {
    private static List<Song> songs;
    private LayoutInflater inflater;

    public newSongsAdapter(Context context, List<Song> songs) {
        this.inflater = LayoutInflater.from(context);
        this.songs = songs;
    }

    /**
     * Interface to handle item click events.
     */
    public interface OnItemClickListener {
        void onItemClick(Song song);
    }
    private static OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {this.listener = listener;}

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.new_song_item, parent, false);
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song currentSong = songs.get(position);

        Glide.with(holder.songsImageViewPoster.getContext())
                .load(currentSong.getImageUrl())
                .into(holder.songsImageViewPoster);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(currentSong);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        final ImageView songsImageViewPoster;

        SongViewHolder(View itemView) {
            super(itemView);
            songsImageViewPoster = itemView.findViewById(R.id.songsImageViewPoster);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(songs.get(position));
                    }
                }
            });
        }
    }
}

