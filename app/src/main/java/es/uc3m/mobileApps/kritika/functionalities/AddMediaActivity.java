package es.uc3m.mobileApps.kritika.functionalities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import es.uc3m.mobileApps.kritika.DashboardUserActivity;
import es.uc3m.mobileApps.kritika.R;

public class AddMediaActivity extends DashboardUserActivity {

    private Spinner spinnerMediaType;
    private LinearLayout layoutMovies, layoutMusic, layoutBooks;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_media);

        spinnerMediaType = findViewById(R.id.spinnerMediaType);
        layoutMovies = findViewById(R.id.layoutMovies);
        layoutMusic = findViewById(R.id.layoutMusic);
        layoutBooks = findViewById(R.id.layoutBooks);

        // buttons
        Button buttonOpenMovies = findViewById(R.id.button_open_movies);
        Button buttonOpenMusic = findViewById(R.id.button_open_music);
        Button buttonOpenBooks = findViewById(R.id.button_open_books);
        ImageButton buttonOpenProfile = findViewById(R.id.profileButton);
        ImageButton buttonOpenHome = findViewById(R.id.houseButton);
        ImageButton buttonOpenSearch = findViewById(R.id.searchButton);

        // Set click listeners for buttons
        setButtonListeners(buttonOpenMovies, buttonOpenMusic, buttonOpenBooks, buttonOpenProfile,
                buttonOpenHome, buttonOpenSearch);

        spinnerMediaType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateFormVisibility(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                layoutMovies.setVisibility(View.GONE);
                layoutMusic.setVisibility(View.GONE);
                layoutBooks.setVisibility(View.GONE);
            }
        });
    }

    private void updateFormVisibility(int typeIndex) {
        layoutMovies.setVisibility(View.GONE);
        layoutMusic.setVisibility(View.GONE);
        layoutBooks.setVisibility(View.GONE);

        switch (typeIndex) {
            case 0: // Movies
                layoutMovies.setVisibility(View.VISIBLE);
                break;
            case 1: // Music
                layoutMusic.setVisibility(View.VISIBLE);
                break;
            case 2: // Books
                layoutBooks.setVisibility(View.VISIBLE);
                break;
        }
    }
}