package es.uc3m.mobileApps.kritika.Actions;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.Review;

/**
 * Activity for creating or updating a review.
 */
public class ReviewActivity extends AppCompatActivity {

    private EditText reviewEditText;
    private Button submitButton;
    private CheckBox isPublicCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        // Initialize views
        reviewEditText = findViewById(R.id.etReview);
        submitButton = findViewById(R.id.btnSubmitReview);
        isPublicCheckBox = findViewById(R.id.chkIsPublic);

        // Submit button click listener
        submitButton.setOnClickListener(v -> {
            String reviewText = reviewEditText.getText().toString();
            String mediaId = getIntent().getStringExtra("mediaId");
            String mediaType = getIntent().getStringExtra("mediaType");
            boolean isPublic = isPublicCheckBox.isChecked();

            // Get Firebase instance
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Get current authenticated user ID
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Check if user already submitted a review for this media
            db.collection("reviews")
                    .whereEqualTo("user", userId)
                    .whereEqualTo("mediaId", mediaId)
                    .limit(1)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                // No review exists, create a new one
                                Review review = new Review(userId, reviewText, isPublic, mediaId, mediaType);
                                db.collection("reviews")
                                        .add(review)
                                        .addOnSuccessListener(documentReference -> {
                                            Toast.makeText(ReviewActivity.this, "Review submitted successfully", Toast.LENGTH_SHORT).show();
                                            NotificationHelper.showNotification(this, "New Review", "You have made a new review!", NotificationHelper.CHANNEL_ID_REVIEW);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(ReviewActivity.this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                // Review exists, update it
                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                String reviewId = documentSnapshot.getId();
                                DocumentReference reviewRef = db.collection("reviews").document(reviewId);

                                // Update existing review
                                reviewRef.update("reviewText", reviewText,
                                                "public", isPublic)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(ReviewActivity.this, "Review updated successfully", Toast.LENGTH_SHORT).show();
                                            NotificationHelper.showNotification(this, "New Review", "You have updated your review!", NotificationHelper.CHANNEL_ID_REVIEW);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(ReviewActivity.this, "Failed to update review", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            Toast.makeText(ReviewActivity.this, "Error checking existing review", Toast.LENGTH_SHORT).show();
                        }
                    });
            finish();
        });
    }
}