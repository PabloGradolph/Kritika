package es.uc3m.mobileApps.kritika.Actions;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.Rating;

/**
 * Activity for rating media.
 */
public class RateActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        // Initialize views
        ratingBar = findViewById(R.id.ratingBar);
        submitButton = findViewById(R.id.btnSubmitRating);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get current authenticated user ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get media ID and type from intent
        String mediaId = getIntent().getStringExtra("mediaId");
        String mediaType = getIntent().getStringExtra("mediaType");

        // Check if user already rated this media
        db.collection("ratings")
                .whereEqualTo("userId", userId)
                .whereEqualTo("mediaId", mediaId)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // If a rating exists, set the current rating on the RatingBar
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
            // Create new Rating instance
            Rating finalRating = new Rating(userId, mediaId, mediaType, rating);

            // Check if user already rated this media
            db.collection("ratings")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("mediaId", mediaId)
                    .limit(1)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                // No rating exists, create a new one
                                db.collection("ratings")
                                        .add(finalRating)
                                        .addOnSuccessListener(documentReference -> {
                                            Toast.makeText(this, "Rating submitted successfully", Toast.LENGTH_SHORT).show();
                                            NotificationHelper.showNotification(this, "New Rating", "You have made a rating!", NotificationHelper.CHANNEL_ID_RATING);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Failed to submit the rating", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                // Rating exists, update it
                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                String ratingId = documentSnapshot.getId();
                                DocumentReference ratingRef = db.collection("ratings").document(ratingId);

                                // Update existing rating
                                ratingRef.update("rating", finalRating.getRating())
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Rating updated successfully", Toast.LENGTH_SHORT).show();
                                            NotificationHelper.showNotification(this, "New Rating", "You have updated your rating!", NotificationHelper.CHANNEL_ID_RATING);
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