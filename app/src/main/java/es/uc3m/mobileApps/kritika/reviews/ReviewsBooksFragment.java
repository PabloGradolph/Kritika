
package es.uc3m.mobileApps.kritika.reviews;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.Review;

/**
 * Fragment class for displaying public reviews for books.
 */
public class ReviewsBooksFragment extends Fragment {
    private RecyclerView rvReviewsBooks;
    private ReviewsBooksAdapter adapter;
    private List<Review> reviewsBooksList = new ArrayList<Review>();

    // Firestore instance
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Required empty constructor.
    public ReviewsBooksFragment() { }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.reviews_fragment_books, container, false);

        rvReviewsBooks = view.findViewById(R.id.rvReviewsBooks);
        rvReviewsBooks.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new ReviewsBooksAdapter(getContext(), reviewsBooksList);
        rvReviewsBooks.setAdapter(adapter);

        // Load reviews
        loadPublicReviews();

        return view;
    }

    /**
     * Loads public reviews for books from Firestore.
     */
    private void loadPublicReviews() {
        db.collection("reviews")
                .whereEqualTo("public", true)
                .whereEqualTo("mediaType", "books")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            reviewsBooksList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Review review = document.toObject(Review.class);
                                reviewsBooksList.add(review);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.w("ShowReviewsActivity", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}

