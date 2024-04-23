package es.uc3m.mobileApps.kritika.reviews;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.uc3m.mobileApps.kritika.DashboardUserActivity;
import es.uc3m.mobileApps.kritika.MainActivity;
import es.uc3m.mobileApps.kritika.R;

public class ShowReviewsActivity extends DashboardUserActivity {

    private es.uc3m.mobileApps.kritika.databinding.NewActivityDashboardUserBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load layout
        setContentView(R.layout.activity_show_reviews);

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        ImageButton buttonLogOut = findViewById(R.id.logoutBtn);
        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

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
            TextView subTitleTv = findViewById(R.id.subTitleTv);
            subTitleTv.setText(UserName);

        }
    }
}

