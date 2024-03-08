package es.uc3m.mobileApps.kritika;

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

import es.uc3m.mobileApps.kritika.model.Movie;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    private List<Movie> movies;
    private LayoutInflater inflater;

    public MoviesAdapter(Context context, List<Movie> movies) {
        this.inflater = LayoutInflater.from(context);
        this.movies = movies;
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
        // Aquí puedes continuar asignando los demás datos de la película a los elementos de la vista

        // Cargar imagen con Glide
        Glide.with(holder.imageViewPoster.getContext())
                .load("https://image.tmdb.org/t/p/w500" + currentMovie.getPosterPath()) // Cambia la URL base según sea necesario
                .into(holder.imageViewPoster);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle, tvOverview; // Declara aquí otros elementos de la vista si es necesario
        public ImageView imageViewPoster; // Asegúrate de declarar el ImageView

        MovieViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOverview = itemView.findViewById(R.id.tvOverview);
            imageViewPoster = itemView.findViewById(R.id.imageViewPoster); // Inicializa el ImageView
            // Inicializa aquí otros elementos de la vista
        }
    }

}
