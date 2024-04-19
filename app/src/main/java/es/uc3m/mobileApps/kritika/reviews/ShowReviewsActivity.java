package es.uc3m.mobileApps.kritika.reviews;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import es.uc3m.mobileApps.kritika.DashboardUserActivity;
import es.uc3m.mobileApps.kritika.R;

public class ShowReviewsActivity extends DashboardUserActivity {

    private es.uc3m.mobileApps.kritika.databinding.NewActivityDashboardUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load layout
        setContentView(R.layout.activity_show_reviews);

        // TOP BAR: buttons
        Button buttonOpenMovies = findViewById(R.id.button_open_movies);
        Button buttonOpenMusic = findViewById(R.id.button_open_music);
        Button buttonOpenBooks = findViewById(R.id.button_open_books);
        Button buttonOpenReviews = findViewById(R.id.button_open_reviews);

        // BOTTOM BAR: buttons
        ImageButton buttonOpenProfile = findViewById(R.id.profileButton);
        ImageButton buttonOpenHome = findViewById(R.id.houseButton);
        ImageButton buttonOpenSearch = findViewById(R.id.searchButton);

        // ADD MEDIA: buttons
        //FloatingActionButton addMediaButton = findViewById(R.id.addMediaButton);


        // Set click listeners for buttons
        setButtonListeners(buttonOpenMovies, buttonOpenMusic, buttonOpenBooks, buttonOpenReviews, buttonOpenProfile,
                buttonOpenHome, buttonOpenSearch);


        // Load MoviesFragment
        ReviewsMoviesFragment moviesFragment = new ReviewsMoviesFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.reviewMoviesFragmentContainer, moviesFragment)
                .commit();

        // Load SongsFragment
        ReviewsSongsFragment songsFragment = new ReviewsSongsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.reviewSongsFragmentContainer, songsFragment)
                .commit();

        // Load BooksFragment
        ReviewsBooksFragment booksFragment = new ReviewsBooksFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.reviewBooksFragmentContainer, booksFragment)
                .commit();
    }
}

