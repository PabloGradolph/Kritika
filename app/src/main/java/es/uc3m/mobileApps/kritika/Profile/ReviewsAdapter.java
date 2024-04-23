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
import es.uc3m.mobileApps.kritika.model.Review;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {
    private Context context;
    private static List<Review> reviewsList;

    private LayoutInflater inflater;

    public ReviewsAdapter(Context context, List<Review> reviewsList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.reviewsList = reviewsList;
    }

    public interface OnItemClickListener {
        void onItemClick(Review reviewsList);
    }

    private static ReviewsAdapter.OnItemClickListener listener;

    public void setOnItemClickListener(ReviewsAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.profile_review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewsList.get(position);

        String title = review.getTitle();
        //int maxLength = 15;
        //if (title.length() > maxLength) {
            //title = title.substring(0, maxLength) + "...";
        //}
        holder.titleTextView.setText(title);

        holder.reviewTextView.setText(String.valueOf(review.getReviewText()));
        // Carga la imagen del media utilizando Glide
        Glide.with(context).load(review.getImageUrl()).into(holder.mediaImageViewPoster);

        Log.d("Adapter", "Tamaño de ratingsList: " + reviewsList.size());
        Log.d("Adapter", "Tamaño de ratingsList: " + getItemCount());
        Log.d("Adapter", "Posición: " + position);
        Log.d("Adapter", "Título: " + review.getTitle());
        Log.d("Adapter", "Review: " + review.getReviewText());
        Log.d("Adapter", "URL de la imagen: " + review.getImageUrl());
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, reviewTextView;
        ImageView mediaImageViewPoster;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tvTitle);
            reviewTextView = itemView.findViewById(R.id.tvText);
            mediaImageViewPoster = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(reviewsList.get(position));
                    }
                }
            });
        }
    }
}
