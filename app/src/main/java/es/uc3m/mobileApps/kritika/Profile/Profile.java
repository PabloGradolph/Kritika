package es.uc3m.mobileApps.kritika.Profile;

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
import es.uc3m.mobileApps.kritika.databinding.ProfileBinding;

public class Profile extends DashboardUserActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

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

        // Set click listeners for buttons
        setButtonListeners(buttonOpenMovies, buttonOpenMusic, buttonOpenBooks, buttonOpenReviews, buttonOpenProfile,
                buttonOpenHome, buttonOpenSearch);// You can add any specific initialization code for the Profile activity here

        // Agregamos los fragmentos
        RatingsFragment ratingsFragment = new RatingsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.ratingsMediaFragmentContainer, ratingsFragment)
                .commit();

        ReviewsFragment reviewsFragment = new ReviewsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.reviewsMediaFragmentContainer, reviewsFragment)
                .commit();

        ListsFragment listsFragment = new ListsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.listsMediaFragmentContainer, listsFragment)
                .commit();
    }


    @Override
    public void openProfile() {
        // This is the Profile activity, so no need to navigate to itself
        // You can handle this method differently if needed
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
