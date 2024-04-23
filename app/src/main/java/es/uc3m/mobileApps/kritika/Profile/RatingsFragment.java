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
import es.uc3m.mobileApps.kritika.Profile.RatingsAdapter;

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

        // Inicializa el RecyclerView y el adaptador
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

        // Realiza la consulta a Firebase Firestore para obtener los ratings del usuario
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
                            // Después de obtener los ratings del usuario, llamas a los métodos para obtener el título y
                            ratingsList.add(rating);
                        }
                        Log.d("RatingsFragment", "Ratings obtenidos correctamente: " + ratingsList.size());
                        // Después de obtener los ratings del usuario, llamas a los métodos para obtener el título y la imagen del media
                        totalRatings = ratingsList.size();

                        for (Rating rating : ratingsList) {
                            fetchMediaInfo(rating, new OnRatingProcessedListener() {

                                @Override
                                public void onRatingProcessed() {
                                    ratingsProcessed++;
                                    if (ratingsProcessed == totalRatings) {
                                        // Todas las operaciones asincrónicas han finalizado, ahora podemos imprimir los ratings
                                        for (Rating r : ratingsList) {
                                            Log.d("RatingsFragment", "Rating: " + r.getTitle() + " - " + r.getRating() + " - " + r.getImageUrl());
                                        }

                                        // Evita la excepción CalledFromWrongThreadException usando runOnUiThread.
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
                        // Manejar errores
                        Log.e("RatingsFragment", "Error al obtener ratings: " + task.getException());
                    }
                });
    }

    private void fetchMediaInfo(Rating rating, OnRatingProcessedListener listener) {

        MediaInfoFetcher.fetchTitle(rating.getMediaId(), rating.getMediaType(), new MediaInfoFetcher.OnTitleFetchedListener() {
            @Override
            public void onTitleFetched(String title) {
                rating.setTitle(title);
                if (rating.getImageUrl() != null) {
                    // Si se han obtenido ambos, notifica que la operación del rating ha sido procesada
                    listener.onRatingProcessed();
                }
            }

            @Override
            public void onTitleFetchFailed() {
                Log.e("RatingsFragment", "Error al obtener el título del media");
            }
        });

        MediaInfoFetcher.fetchImageUrl(rating.getMediaId(), rating.getMediaType(), new MediaInfoFetcher.OnImageUrlFetchedListener() {

            @Override
            public void onImageUrlFetched(String imageUrl) {
                rating.setImageUrl(imageUrl);
                if (rating.getTitle() != null) {
                    // Si se han obtenido ambos, notifica que la operación del rating ha sido procesada
                    listener.onRatingProcessed();
                }
            }

            @Override
            public void onImageUrlFetchFailed() {
                Log.e("RatingsFragment", "Error al obtener la URL de la imagen del media");
            }
        });
    }

    public interface OnRatingProcessedListener {
        void onRatingProcessed();
    }
}
