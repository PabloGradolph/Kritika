package es.uc3m.mobileApps.kritika.reviews;

import static com.google.firebase.appcheck.internal.util.Logger.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.Review;

public class ReviewsMoviesAdapter extends RecyclerView.Adapter<ReviewsMoviesAdapter.ReviewsViewHolder> {
    private static List<Review> reviews;
    private LayoutInflater inflater;

    public ReviewsMoviesAdapter(Context context, List<Review> reviews) {
        this.inflater = LayoutInflater.from(context);
        this.reviews = reviews;
    }


    public interface OnItemClickListener {
        void onItemClick(Review reviews);
    }

    private static OnItemClickListener listener;

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
                    }
                })
                .addOnFailureListener(e -> Log.d(TAG, "Error getting user details", e));

        // Listener para el clic en el elemento
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

    static class ReviewsViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle, tvUser, tvText;

        ReviewsViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvText = itemView.findViewById(R.id.tvText);

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
