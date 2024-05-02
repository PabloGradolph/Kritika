package es.uc3m.mobileApps.kritika.reviews;

import static com.google.firebase.appcheck.internal.util.Logger.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.Review;

/**
 * Adapter class for managing reviews displayed in a RecyclerView for movies.
 */
public class ReviewsMoviesAdapter extends RecyclerView.Adapter<ReviewsMoviesAdapter.ReviewsViewHolder> {
    private static List<Review> reviews;
    private LayoutInflater inflater;

    /**
     * Constructs a ReviewsMoviesAdapter.
     *
     * @param context The context of the calling activity.
     * @param reviews The list of reviews to be displayed.
     */
    public ReviewsMoviesAdapter(Context context, List<Review> reviews) {
        this.inflater = LayoutInflater.from(context);
        this.reviews = reviews;
    }

    /**
     * Interface definition for a callback to be invoked when a review item is clicked.
     */
    public interface OnItemClickListener {
        void onItemClick(Review reviews);
    }

    private static OnItemClickListener listener;

    /**
     * Sets the click listener for review items.
     *
     * @param listener The click listener to be set.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.show_review_item, parent, false);
        return new ReviewsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsViewHolder holder, int position) {
        Review currentReview = reviews.get(position);
        holder.tvText.setText(currentReview.getReviewText());

        // Get Media Title from DB (Firestore)
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(currentReview.getMediaType()).document(currentReview.getMediaId()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String title = documentSnapshot.getString("title");
                        holder.tvTitle.setText(title);
                    }
                })
                .addOnFailureListener(e -> Log.d(TAG, "Error getting media details", e));

        // Get User from DB (Firestore)
        db.collection("users").document(currentReview.getUser()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("name");
                        holder.tvUser.setText(username);

                        // Get the URL of the user's profile image
                        String profileImageUrl = documentSnapshot.getString("profileImage");
                        if (profileImageUrl != null && !profileImageUrl.isEmpty() && holder.itemView.isAttachedToWindow()) {
                            // If the image URL is available and the view is attached, load the image from Firebase Storage
                            Glide.with(holder.itemView.getContext()).load(profileImageUrl).into(holder.imageView);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.d(TAG, "Error getting user details", e));

        // Listener for clicking on the item
        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onItemClick(currentReview);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    /**
     * ViewHolder class for caching View components of review items.
     */
    static class ReviewsViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle, tvUser, tvText;
        final ImageView imageView;

        /**
         * Constructs a ReviewsViewHolder.
         *
         * @param itemView The view of the review item.
         */
        ReviewsViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvText = itemView.findViewById(R.id.tvText);
            imageView = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(reviews.get(position));
                    }
                }
            });
        }
    }
}
