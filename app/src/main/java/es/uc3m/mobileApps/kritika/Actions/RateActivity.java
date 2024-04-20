package es.uc3m.mobileApps.kritika.Actions;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.Rating;
import es.uc3m.mobileApps.kritika.model.Review;

public class RateActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        ratingBar = findViewById(R.id.ratingBar);
        submitButton = findViewById(R.id.btnSubmitRating);

        // Crear una instancia de FirebaseFirestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Obtener el ID del usuario actualmente autenticado
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String mediaId = getIntent().getStringExtra("mediaId");
        String mediaType = getIntent().getStringExtra("mediaType");

        // Crear una referencia a la colección "ratings" y filtrar por el usuario y el medio
        db.collection("ratings")
                .whereEqualTo("userId", userId)
                .whereEqualTo("mediaId", mediaId)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // Si existe un rating para este usuario y medio, establecer el rating actual en el RatingBar
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            float currentRating = documentSnapshot.getDouble("rating").floatValue();
                            ratingBar.setRating(currentRating);
                        }
                    } else {
                        Toast.makeText(RateActivity.this, "Error checking existing rating", Toast.LENGTH_SHORT).show();
                    }
                });

        submitButton.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            // Crear una nueva instancia de Rating
            Rating finalRating = new Rating(userId, mediaId, mediaType, rating);

            // Crear una referencia a la colección "ratings" y filtrar por el usuario y el medio
            db.collection("ratings")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("mediaId", mediaId)
                    .limit(1)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                // No existe ningún rating, crear uno nuevo
                                db.collection("ratings")
                                        .add(finalRating)
                                        .addOnSuccessListener(documentReference -> {
                                            Toast.makeText(this, "Rating submitted successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Failed to submit the rating", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                // Ya existe un rating, actualizarlo
                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                String ratingId = documentSnapshot.getId();
                                DocumentReference ratingRef = db.collection("ratings").document(ratingId);

                                // Actualizar el rating existente
                                ratingRef.update("rating", finalRating.getRating())
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Rating updated successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Failed to update the rating", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            Toast.makeText(RateActivity.this, "Error checking existing rating", Toast.LENGTH_SHORT).show();
                        }
                    });
            finish();
        });

    }
}