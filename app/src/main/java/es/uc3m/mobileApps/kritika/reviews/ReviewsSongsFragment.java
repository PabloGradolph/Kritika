
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

public class ReviewsSongsFragment extends Fragment {
    private RecyclerView rvReviewsSongs;
    private ReviewsSongsAdapter adapter;
    private List<Review> reviewsSongsList = new ArrayList<Review>();

    // Firestore instance
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Constructor vacío requerido
    public ReviewsSongsFragment() { }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el layout para este fragmento
        View view = inflater.inflate(R.layout.reviews_fragment_songs, container, false);

        rvReviewsSongs = view.findViewById(R.id.rvReviewsSongs);
        rvReviewsSongs.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new ReviewsSongsAdapter(getContext(), reviewsSongsList);


        rvReviewsSongs.setAdapter(adapter);



        // Load reviews
        loadPublicReviews();

        return view;
    }


    private void loadPublicReviews() {
        db.collection("reviews")
                .whereEqualTo("public", true)
                .whereEqualTo("mediaType", "songs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            reviewsSongsList.clear(); // Limpiar la lista antes de agregar los nuevos elementos
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Review review = document.toObject(Review.class);
                                reviewsSongsList.add(review);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.w("ShowReviewsActivity", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

}

