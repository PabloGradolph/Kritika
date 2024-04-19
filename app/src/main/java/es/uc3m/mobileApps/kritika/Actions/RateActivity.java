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
import com.google.firebase.firestore.FirebaseFirestore;

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

        submitButton.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String mediaId = getIntent().getStringExtra("mediaId");
            String mediaType = getIntent().getStringExtra("mediaType");

            // Crear una instancia de FirebaseFirestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Obtener el ID del usuario actualmente autenticado
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Crear una nueva instancia de Rating
            Rating finalRating = new Rating(userId, mediaId, mediaType, rating);

            // Obtener una referencia a la colección "reviews" en Firestore y guardar la revisión
            db.collection("ratings")
                    .add(finalRating)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Rating submitted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to submit the rating", Toast.LENGTH_SHORT).show();
                    });
            finish();
        });

    }
}