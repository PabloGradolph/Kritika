package es.uc3m.mobileApps.kritika.Profile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import es.uc3m.mobileApps.kritika.DashboardUserActivity;
import es.uc3m.mobileApps.kritika.R;

public class Profile extends DashboardUserActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
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

        // Agregar el fragmento RatingsFragment al contenedor ratingsMediaFragmentContainer
        RatingsFragment ratingsFragment = new RatingsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.ratingsMediaFragmentContainer, ratingsFragment)
                .commit();
    }


    @Override
    public void openProfile() {
        // This is the Profile activity, so no need to navigate to itself
        // You can handle this method differently if needed
    }
}
