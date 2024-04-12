package es.uc3m.mobileApps.kritika.newDashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.uc3m.mobileApps.kritika.MainActivity;
import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.books.BooksActivity;
import es.uc3m.mobileApps.kritika.functionalities.Profile;
import es.uc3m.mobileApps.kritika.functionalities.SearchActivity;
import es.uc3m.mobileApps.kritika.movies.MoviesActivity;
import es.uc3m.mobileApps.kritika.music.MusicActivity;


public class newDashboardUserActivity extends AppCompatActivity {

    private es.uc3m.mobileApps.kritika.databinding.NewActivityDashboardUserBinding binding;

    // firebase auth
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Carga el layout
        setContentView(R.layout.new_activity_dashboard_user);

        // binding
        binding = es.uc3m.mobileApps.kritika.databinding.NewActivityDashboardUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        // buttons
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
                buttonOpenHome, buttonOpenSearch); // NEW BUTTON



        // Carga el MoviesFragment
        NewMoviesFragment moviesFragment = new NewMoviesFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.moviesFragmentContainer, moviesFragment)
                .commit();

        // Carga el SongsFragment
        newMusicFragment songsFragment = new newMusicFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.songsFragmentContainer, songsFragment)
                .commit();

        // Carga el BooksFragment
        NewBooksFragment booksFragment = new NewBooksFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.booksFragmentContainer, booksFragment)
                .commit();

    }


    // Method to set click listeners for buttons
    protected void setButtonListeners(Button buttonOpenMovies, Button buttonOpenMusic,
                                      Button buttonOpenBooks, ImageButton buttonOpenProfile, // NEW BUTTON
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