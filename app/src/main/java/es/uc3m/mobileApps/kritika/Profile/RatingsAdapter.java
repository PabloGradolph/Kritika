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

import com.bumptech.glide.Glide;

import java.util.List;

import es.uc3m.mobileApps.kritika.model.Rating;
import es.uc3m.mobileApps.kritika.R;

/**
 * Adapter class for displaying ratings in a RecyclerView.
 */
public class RatingsAdapter extends RecyclerView.Adapter<RatingsAdapter.RatingViewHolder> {
    private Context context;
    private static List<Rating> ratingsList;
    private LayoutInflater inflater;

    /**
     * Constructor for RatingsAdapter.
     *
     * @param context     The context.
     * @param ratingsList The list of ratings.
     */
    public RatingsAdapter(Context context, List<Rating> ratingsList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.ratingsList = ratingsList;
    }

    /**
     * Interface for item click listener.
     */
    public interface OnItemClickListener {
        void onItemClick(Rating ratingsList);
    }

    private static RatingsAdapter.OnItemClickListener listener;

    /**
     * Sets the item click listener.
     *
     * @param listener The listener to set.
     */
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

        String title = rating.getTitle();
        int maxLength = 15;
        if (title.length() > maxLength) {
            title = title.substring(0, maxLength) + "...";
        }

        holder.titleTextView.setText(title);
        holder.ratingTextView.setText(String.valueOf(rating.getRating()));
        Glide.with(context).load(rating.getImageUrl()).into(holder.mediaImageViewPoster);
    }

    @Override
    public int getItemCount() {
        return ratingsList.size();
    }

    /**
     * ViewHolder class for ratings.
     */
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
