package es.uc3m.mobileApps.kritika.Actions;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import es.uc3m.mobileApps.kritika.R;

public class ReviewActivity extends AppCompatActivity {

    private EditText reviewEditText;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        reviewEditText = findViewById(R.id.etReview);
        submitButton = findViewById(R.id.btnSubmitReview);

        submitButton.setOnClickListener(v -> {
            String reviewText = reviewEditText.getText().toString();
            // Aquí, procesar y enviar la reseña
            Toast.makeText(this, "Review submitted", Toast.LENGTH_SHORT).show();
            finish(); // Opcional, cierra la actividad después de enviar
        });
    }
}