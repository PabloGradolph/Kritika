package es.uc3m.mobileApps.kritika.movies;

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

import es.uc3m.mobileApps.kritika.Misc.ApiConstants;
import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.Movie;

/**
 * Adapter for displaying a list of movies in a RecyclerView.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    private static List<Movie> movies;
    private LayoutInflater inflater;

    /**
     * Constructor for the MoviesAdapter.
     * @param context The context of the activity or fragment.
     * @param movies The list of movies to display.
     */
    public MoviesAdapter(Context context, List<Movie> movies) {
        this.inflater = LayoutInflater.from(context);
        this.movies = movies;
    }

    /**
     * Interface definition for a callback to be invoked when a movie item is clicked.
     */
    public interface OnItemClickListener {
        void onItemClick(Movie movie);
    }

    private static OnItemClickListener listener;

    /**
     * Sets the click listener for movie items.
     * @param listener The listener to set.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie currentMovie = movies.get(position);
        holder.tvTitle.setText(currentMovie.getTitle());
        holder.tvOverview.setText(currentMovie.getOverview());
        holder.tvRating.setText(currentMovie.getRating());

        // Load image using Glide
        Glide.with(holder.imageViewPoster.getContext())
                .load(ApiConstants.MOVIEDB_IMAGE_URL + currentMovie.getPosterPath())
                .into(holder.imageViewPoster);

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
     * ViewHolder class for movie items.
     */
    static class MovieViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle, tvOverview, tvRating;
        public ImageView imageViewPoster;

        MovieViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOverview = itemView.findViewById(R.id.tvOverview);
            imageViewPoster = itemView.findViewById(R.id.imageViewPoster);
            tvRating = itemView.findViewById(R.id.tvRating);

            // Initialize other view elements here
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
