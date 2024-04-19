package es.uc3m.mobileApps.kritika.Actions;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.Review;

public class ReviewActivity extends AppCompatActivity {

    private EditText reviewEditText;
    private Button submitButton;
    private CheckBox isPublicCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        reviewEditText = findViewById(R.id.etReview);
        submitButton = findViewById(R.id.btnSubmitReview);
        isPublicCheckBox = findViewById(R.id.chkIsPublic);

        submitButton.setOnClickListener(v -> {
            String reviewText = reviewEditText.getText().toString();
            String mediaId = getIntent().getStringExtra("mediaId");
            String mediaType = getIntent().getStringExtra("mediaType");
            boolean isPublic = isPublicCheckBox.isChecked();

            // Crear una instancia de FirebaseFirestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Obtener el ID del usuario actualmente autenticado
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Crear una nueva instancia de Review
            Review review = new Review(userId, reviewText, isPublic, mediaId, mediaType);

            // Obtener una referencia a la colección "reviews" en Firestore y guardar la revisión
            db.collection("reviews")
                    .add(review)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(ReviewActivity.this, "Review submitted successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Opcional: cierra la actividad después de enviar la revisión
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ReviewActivity.this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                    });
            finish();
        });
    }
}