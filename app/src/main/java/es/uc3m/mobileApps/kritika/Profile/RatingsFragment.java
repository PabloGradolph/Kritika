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
import es.uc3m.mobileApps.kritika.model.Rating;

/**
 * Fragment responsible for displaying user ratings.
 */
public class RatingsFragment extends Fragment {
    private RecyclerView rvRatings;
    private List<Rating> ratingsList;
    private RatingsAdapter ratingsAdapter;

    int ratingsProcessed = 0;
    int totalRatings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_ratings_fragment, container, false);

        // Initialize RecyclerView and adapter
        rvRatings = rootView.findViewById(R.id.rvRatings);
        ratingsList = new ArrayList<>();
        ratingsAdapter = new RatingsAdapter(getContext(), ratingsList);
        rvRatings.setAdapter(ratingsAdapter);
        rvRatings.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Query Firebase Firestore to get user ratings
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ratings")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ratingsList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Rating rating = document.toObject(Rating.class);
                            Log.d("RatingsFragment", "Ratings: " + rating.getRating());
                            // After getting user ratings, call methods to get title and media image
                            ratingsList.add(rating);
                        }
                        Log.d("RatingsFragment", "Ratings obtenidos correctamente: " + ratingsList.size());
                        // After getting user ratings, call methods to get title and media image
                        totalRatings = ratingsList.size();

                        for (Rating rating : ratingsList) {
                            fetchMediaInfo(rating, new OnRatingProcessedListener() {

                                @Override
                                public void onRatingProcessed() {
                                    ratingsProcessed++;
                                    if (ratingsProcessed == totalRatings) {
                                        // All asynchronous operations have finished, now we can print the ratings
                                        for (Rating r : ratingsList) {
                                            Log.d("RatingsFragment", "Rating: " + r.getTitle() + " - " + r.getRating() + " - " + r.getImageUrl());
                                        }

                                        // Avoid CalledFromWrongThreadException by using runOnUiThread.
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ratingsAdapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    } else {
                        Log.e("RatingsFragment", "Error al obtener ratings: " + task.getException());
                    }
                });
    }

    /**
     * Method to fetch media information.
     *
     * @param rating   The rating object.
     * @param listener Listener to notify when the rating processing is done.
     */
    private void fetchMediaInfo(Rating rating, OnRatingProcessedListener listener) {

        MediaInfoFetcher.fetchTitle(rating.getMediaId(), rating.getMediaType(), new MediaInfoFetcher.OnTitleFetchedListener() {
            @Override
            public void onTitleFetched(String title) {
                rating.setTitle(title);
                if (rating.getImageUrl() != null) {
                    // If both are obtained, notify that the rating operation has been processed
                    listener.onRatingProcessed();
                }
            }

            @Override
            public void onTitleFetchFailed() {
                Log.e("RatingsFragment", "Error in obtaining the media title");
            }
        });

        MediaInfoFetcher.fetchImageUrl(rating.getMediaId(), rating.getMediaType(), new MediaInfoFetcher.OnImageUrlFetchedListener() {

            @Override
            public void onImageUrlFetched(String imageUrl) {
                rating.setImageUrl(imageUrl);
                if (rating.getTitle() != null) {
                    // If both are obtained, notify that the rating operation has been processed
                    listener.onRatingProcessed();
                }
            }

            @Override
            public void onImageUrlFetchFailed() {
                Log.e("RatingsFragment", "Error retrieving media image URL");
            }
        });
    }

    /**
     * Listener interface for rating processing.
     */
    public interface OnRatingProcessedListener {
        void onRatingProcessed();
    }
}
