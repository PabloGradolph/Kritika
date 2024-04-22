package es.uc3m.mobileApps.kritika.Profile;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Importa Glide

import java.util.List;

import es.uc3m.mobileApps.kritika.model.Rating;
import es.uc3m.mobileApps.kritika.R;

public class RatingsAdapter extends RecyclerView.Adapter<RatingsAdapter.RatingViewHolder> {
    private Context context;
    private static List<Rating> ratingsList;

    private LayoutInflater inflater;

    public RatingsAdapter(Context context, List<Rating> ratingsList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.ratingsList = ratingsList;
    }

    public interface OnItemClickListener {
        void onItemClick(Rating ratingsList);
    }

    private static RatingsAdapter.OnItemClickListener listener;

    public void setOnItemClickListener(RatingsAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.profile_rating_item, parent, false);
        return new RatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
        Rating rating = ratingsList.get(position);
        holder.titleTextView.setText(rating.getTitle());
        holder.ratingTextView.setText(String.valueOf(rating.getRating()));
        // Carga la imagen del media utilizando Glide
        Glide.with(context).load(rating.getImageUrl()).into(holder.mediaImageViewPoster);

        Log.d("Adapter", "Tamaño de ratingsList: " + ratingsList.size());
        Log.d("Adapter", "Tamaño de ratingsList: " + getItemCount());
        Log.d("Adapter", "Posición: " + position);
        Log.d("Adapter", "Título: " + rating.getTitle());
        Log.d("Adapter", "Rating: " + rating.getRating());
        Log.d("Adapter", "URL de la imagen: " + rating.getImageUrl());
    }

    @Override
    public int getItemCount() {
        return ratingsList.size();
    }

    public static class RatingViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, ratingTextView;
        ImageView mediaImageViewPoster;

        public RatingViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
            mediaImageViewPoster = itemView.findViewById(R.id.mediaImageViewPoster);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(ratingsList.get(position));
                    }
                }
            });
        }
    }
}
