package es.uc3m.mobileApps.kritika;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import es.uc3m.mobileApps.kritika.books.BooksActivity;
import es.uc3m.mobileApps.kritika.functionalities.Profile;
import es.uc3m.mobileApps.kritika.functionalities.SearchActivity;
import es.uc3m.mobileApps.kritika.movies.MoviesActivity;
import es.uc3m.mobileApps.kritika.music.MusicActivity;
import es.uc3m.mobileApps.kritika.newDashboard.newDashboardUserActivity;
import es.uc3m.mobileApps.kritika.reviews.ShowReviewsActivity;

public class DashboardUserActivity extends AppCompatActivity {

    // view binding
    private es.uc3m.mobileApps.kritika.databinding.ActivityDashboardUserBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = es.uc3m.mobileApps.kritika.databinding.ActivityDashboardUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // TOP BAR: buttons
        Button buttonOpenMovies = findViewById(R.id.button_open_movies);
        Button buttonOpenMusic = findViewById(R.id.button_open_music);
        Button buttonOpenBooks = findViewById(R.id.button_open_books);
        Button buttonOpenReviews = findViewById(R.id.button_open_reviews);

        // BOTTOM BAR: buttons
        ImageButton buttonOpenProfile = findViewById(R.id.profileButton);
        ImageButton buttonOpenHome = findViewById(R.id.houseButton);
        ImageButton buttonOpenSearch = findViewById(R.id.searchButton);



        // Set click listeners for buttons

        setButtonListeners(buttonOpenMovies, buttonOpenMusic, buttonOpenBooks, buttonOpenReviews, buttonOpenProfile,
                buttonOpenHome, buttonOpenSearch);
        // Start the dashboard with movies opened.
    }

    // Method to set click listeners for buttons
    protected void setButtonListeners(Button buttonOpenMovies, Button buttonOpenMusic,
                                      Button buttonOpenBooks, Button buttonOpenReviews, ImageButton buttonOpenProfile,
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

        buttonOpenReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReviews();
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

    public void openReviews() {
        Intent intent = new Intent(this, ShowReviewsActivity.class);
        startActivity(intent);
    }

    public void openProfile() {
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
    }
    public void openHome() {
        Intent intent = new Intent(this, newDashboardUserActivity.class);
        startActivity(intent);
    }
    public void openSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

}