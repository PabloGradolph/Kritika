package es.uc3m.mobileApps.kritika;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardUserActivity extends AppCompatActivity {

    // view binding
    private es.uc3m.mobileApps.kritika.databinding.ActivityDashboardUserBinding binding;

    // firebase auth
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = es.uc3m.mobileApps.kritika.databinding.ActivityDashboardUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        Button buttonOpenMovies = findViewById(R.id.button_open_movies);
        Button buttonOpenMusic = findViewById(R.id.button_open_music);
        Button buttonOpenBooks = findViewById(R.id.button_open_books);
        ImageButton buttonOpenProfile = findViewById(R.id.profileButton);
        ImageButton buttonOpenHome = findViewById(R.id.houseButton);
        ImageButton buttonOpenSearch = findViewById(R.id.searchButton);

        // handle click, logout
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

        // Set click listeners for buttons

        setButtonListeners(buttonOpenMovies, buttonOpenMusic, buttonOpenBooks, buttonOpenProfile,
                buttonOpenHome, buttonOpenSearch);
        // Start the dashboard with movies opened.
    }

    // Method to set click listeners for buttons
    protected void setButtonListeners(Button buttonOpenMovies, Button buttonOpenMusic,
                                      Button buttonOpenBooks, ImageButton buttonOpenProfile,
                                      ImageButton buttonOpenHome, ImageButton buttonOpenSearch) {
        buttonOpenMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMovies();
            }
        });

        buttonOpenMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMusic();
            }
        });

        buttonOpenBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBooks();
            }
        });

        buttonOpenProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile();
            }
        });

        buttonOpenHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHome();
            }
        });

        buttonOpenSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearch();
            }
        });
    }

    // HAGO METODOS PUBLICOS PARA PODER REUTILIZARLOS EN OTRAS PANTALLAS
    public void openMovies() {
        Intent intent = new Intent(this, MoviesActivity.class);
        startActivity(intent);
    }

    public void openMusic() {
        Intent intent = new Intent(this, MusicActivity.class);
        startActivity(intent);
    }

    public void openBooks() {
        Intent intent = new Intent(this, BooksActivity.class);
        startActivity(intent);
    }

    public void openProfile() {
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
    }
    public void openHome() {
        Intent intent = new Intent(this, DashboardUserActivity.class);
        startActivity(intent);
    }
    public void openSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    private void checkUser() {
        // get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null){
            // not logged in, go to main screen
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            // logged in, get user info
            String UserName = firebaseUser.getEmail();
            // set in textview of toolbar
            binding.subTitleTv.setText(UserName);

        }
    }
}