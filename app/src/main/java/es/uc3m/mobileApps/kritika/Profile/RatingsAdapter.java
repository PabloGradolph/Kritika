package es.uc3m.mobileApps.kritika.Profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.Rating;

/*
public class RatingsAdapter extends RecyclerView.Adapter<RatingsAdapter.RatingsViewHolder>{
    private List<Rating> ratingsList;

    public RatingsAdapter(List<Rating> ratingsList) {
        this.ratingsList = ratingsList;
    }
    @NonNull
    @Override
    public RatingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_rating_item, parent, false);
        return new RatingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingsViewHolder holder, int position) {
        Rating rating = ratingsList.get(position);

        // Cargar la imagen del media (suponiendo que el rating tiene una URL de imagen)
        Glide.with(holder.itemView.getContext())
                .load(rating.getMediaImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.mediaImageView);

        holder.titleTextView.setText(rating.getMediaTitle());
        holder.ratingValueTextView.setText(String.valueOf(rating.getRatingValue()));
    }

    @Override
    public int getItemCount() {
        return ratingsList.size();
    }

    static class RatingsViewHolder extends RecyclerView.ViewHolder {
        ImageView mediaImageView;
        TextView titleTextView;
        TextView ratingValueTextView;

        public RatingsViewHolder(@NonNull View itemView) {
            super(itemView);
            mediaImageView = itemView.findViewById(R.id.mediaImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            ratingValueTextView = itemView.findViewById(R.id.ratingValueTextView);
        }
    }

}*/
