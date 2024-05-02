package es.uc3m.mobileApps.kritika.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.Song;

/**
 * Adapter class for displaying songs in a RecyclerView.
 */
public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongViewHolder> {
    private List<Song> songs;
    private LayoutInflater inflater;

    public SongsAdapter(Context context, List<Song> songs) {
        this.inflater = LayoutInflater.from(context);
        this.songs = songs;
    }

    public interface OnItemClickListener {
        void onItemClick(Song song);
    }
    private static OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {this.listener = listener;}

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.song_item, parent, false);
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song currentSong = songs.get(position);
        holder.tvName.setText(currentSong.getName());
        holder.tvArtistName.setText(currentSong.getArtistName());

        // Load image using Glide
        Glide.with(holder.imageView.getContext())
                .load(currentSong.getImageUrl())
                .into(holder.imageView);

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
        final TextView tvName, tvArtistName;
        final ImageView imageView;

        SongViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvArtistName = itemView.findViewById(R.id.tvArtistName);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}

