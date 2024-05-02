package es.uc3m.mobileApps.kritika.Profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.Review;

/**
 * Fragment for displaying user reviews.
 */
public class ReviewsFragment extends Fragment {
    private RecyclerView rvReviews;
    private List<Review> reviewsList;
    private ReviewsAdapter reviewsAdapter;
    int reviewsProcessed = 0;
    int totalReviews;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_reviews_fragment, container, false);

        // Initialize RecyclerView and adapter
        rvReviews = rootView.findViewById(R.id.rvReviews);
        reviewsList = new ArrayList<>();
        reviewsAdapter = new ReviewsAdapter(getContext(), reviewsList);
        rvReviews.setAdapter(reviewsAdapter);
        rvReviews.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Query Firebase Firestore to get user reviews
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("reviews")
                .whereEqualTo("user", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reviewsList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Review review = document.toObject(Review.class);
                            Log.d("ReviewsFragment", "Reviews: " + review.getReviewText());
                            reviewsList.add(review);
                        }
                        totalReviews = reviewsList.size();

                        for (Review review : reviewsList) {
                            fetchMediaInfo(review, new OnReviewProcessedListener() {

                                @Override
                                public void onReviewProcessed() {
                                    reviewsProcessed++;
                                    if (reviewsProcessed == totalReviews) {
                                        // All asynchronous operations have finished, now we can print the reviews
                                        for (Review r : reviewsList) {
                                            Log.d("ReviewsFragment", "Review: " + r.getTitle() + " - " + r.getReviewText() + " - " + r.getImageUrl());
                                        }

                                        // Avoid CalledFromWrongThreadException using runOnUiThread.
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                reviewsAdapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    } else {
                        Log.e("ReviewsFragment", "Error getting reviews: " + task.getException());
                    }
                });
    }

    private void fetchMediaInfo(Review review, OnReviewProcessedListener listener) {

        MediaInfoFetcher.fetchTitle(review.getMediaId(), review.getMediaType(), new MediaInfoFetcher.OnTitleFetchedListener() {
            @Override
            public void onTitleFetched(String title) {
                review.setTitle(title);
                if (review.getImageUrl() != null) {
                    // If both are obtained, notify that the review operation has been processed
                    listener.onReviewProcessed();
                }
            }

            @Override
            public void onTitleFetchFailed() {
                Log.e("ReviewsFragment", "Error fetching media title");
            }
        });

        MediaInfoFetcher.fetchImageUrl(review.getMediaId(), review.getMediaType(), new MediaInfoFetcher.OnImageUrlFetchedListener() {

            @Override
            public void onImageUrlFetched(String imageUrl) {
                review.setImageUrl(imageUrl);
                if (review.getTitle() != null) {
                    // If both are obtained, notify that the review operation has been processed
                    listener.onReviewProcessed();
                }
            }

            @Override
            public void onImageUrlFetchFailed() {
                Log.e("ReviewsFragment", "Error fetching media image URL");
            }
        });
    }

    public interface OnReviewProcessedListener {
        void onReviewProcessed();
    }
}

