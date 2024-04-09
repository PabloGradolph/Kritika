package es.uc3m.mobileApps.kritika.newDashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.books.BooksActivity;
import es.uc3m.mobileApps.kritika.functionalities.Profile;
import es.uc3m.mobileApps.kritika.functionalities.SearchActivity;
import es.uc3m.mobileApps.kritika.movies.MoviesActivity;
import es.uc3m.mobileApps.kritika.music.MusicActivity;

public class newDashboardUserActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Button buttonOpenMovies = findViewById(R.id.button_open_movies);
        Button buttonOpenMusic = findViewById(R.id.button_open_music);
        Button buttonOpenBooks = findViewById(R.id.button_open_books);
        Button buttonOpenNew = findViewById(R.id.button_open_new); // NEW BUTTON
        ImageButton buttonOpenProfile = findViewById(R.id.profileButton);
        ImageButton buttonOpenHome = findViewById(R.id.houseButton);
        ImageButton buttonOpenSearch = findViewById(R.id.searchButton);



        // Set click listeners for buttons

        setButtonListeners(buttonOpenMovies, buttonOpenMusic, buttonOpenBooks, buttonOpenNew, buttonOpenProfile,
                buttonOpenHome, buttonOpenSearch); // NEW BUTTON
        // Start the dashboard with movies opened.
    }

    // Method to set click listeners for buttons
    protected void setButtonListeners(Button buttonOpenMovies, Button buttonOpenMusic,
                                      Button buttonOpenBooks, Button buttonOpenNew, ImageButton buttonOpenProfile, // NEW BUTTON
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

        // NEW BUTTON
        buttonOpenNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNew();
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

    // NEW BUTTON
    public void openNew() {
        Intent intent = new Intent(this, newDashboardUserActivity.class);
        startActivity(intent);
    }
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
        Intent intent = new Intent(this, newDashboardUserActivity.class);
        startActivity(intent);
    }
    public void openSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }


}