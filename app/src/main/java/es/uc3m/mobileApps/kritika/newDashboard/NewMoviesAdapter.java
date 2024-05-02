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

import es.uc3m.mobileApps.kritika.Misc.ApiConstants;
import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.Movie;

/**
 * Adapter for displaying a list of new movies in a RecyclerView.
 */
public class NewMoviesAdapter extends RecyclerView.Adapter<NewMoviesAdapter.MovieViewHolder> {
    private static List<Movie> movies;
    private LayoutInflater inflater;

    public NewMoviesAdapter(Context context, List<Movie> movies) {
        this.inflater = LayoutInflater.from(context);
        this.movies = movies;
    }

    /**
     * Interface for handling item clicks in the RecyclerView.
     */
    public interface OnItemClickListener {
        void onItemClick(Movie movie);
    }

    private static OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.new_movie_item, parent, false);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie currentMovie = movies.get(position);

        // Load image using Glide
        Glide.with(holder.movieImageViewPoster.getContext())
                .load(ApiConstants.MOVIEDB_IMAGE_URL + currentMovie.getPosterPath())
                .into(holder.movieImageViewPoster);

        // Set OnClickListener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(currentMovie);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    /**
     * ViewHolder class for holding views of each movie item.
     */
    static class MovieViewHolder extends RecyclerView.ViewHolder {
        public ImageView movieImageViewPoster;

        MovieViewHolder(View itemView) {
            super(itemView);
            movieImageViewPoster = itemView.findViewById(R.id.movieImageViewPoster); // Inicializa el ImageView

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(movies.get(position));
                    }
                }
            });
        }
    }

}
