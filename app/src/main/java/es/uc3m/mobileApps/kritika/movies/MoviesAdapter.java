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

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    private static List<Movie> movies;
    private LayoutInflater inflater;

    public MoviesAdapter(Context context, List<Movie> movies) {
        this.inflater = LayoutInflater.from(context);
        this.movies = movies;
    }


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
        View itemView = inflater.inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie currentMovie = movies.get(position);
        holder.tvTitle.setText(currentMovie.getTitle());
        holder.tvOverview.setText(currentMovie.getOverview());
        holder.tvRating.setText(currentMovie.getRating());
        // Aquí puedes continuar asignando los demás datos de la película a los elementos de la vista

        // Cargar imagen con Glide
        Glide.with(holder.imageViewPoster.getContext())
                .load(ApiConstants.MOVIEDB_IMAGE_URL + currentMovie.getPosterPath()) // Cambia la URL base según sea necesario
                .into(holder.imageViewPoster);

        // Configuración del OnClickListener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Asegúrate de que la actividad contenedora implementa la interfaz OnItemClickListener
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

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle, tvOverview, tvRating; // Declara aquí otros elementos de la vista si es necesario
        public ImageView imageViewPoster; // Asegúrate de declarar el ImageView

        MovieViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOverview = itemView.findViewById(R.id.tvOverview);
            imageViewPoster = itemView.findViewById(R.id.imageViewPoster); // Inicializa el ImageView
            tvRating = itemView.findViewById(R.id.tvRating);

            // Inicializa aquí otros elementos de la vista
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
