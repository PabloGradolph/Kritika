package es.uc3m.mobileApps.kritika.functionalities;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import es.uc3m.mobileApps.kritika.DashboardUserActivity;
import es.uc3m.mobileApps.kritika.R;

public class SearchActivity extends DashboardUserActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        Button buttonOpenMovies = findViewById(R.id.button_open_movies);
        Button buttonOpenMusic = findViewById(R.id.button_open_music);
        Button buttonOpenBooks = findViewById(R.id.button_open_books);
        Button buttonOpenNew = findViewById(R.id.button_open_new); // NEW BUTTON
        ImageButton buttonOpenProfile = findViewById(R.id.profileButton);
        ImageButton buttonOpenHome = findViewById(R.id.houseButton);
        ImageButton buttonOpenSearch = findViewById(R.id.searchButton);

        // Set click listeners for buttons
        setButtonListeners(buttonOpenMovies, buttonOpenMusic, buttonOpenBooks, buttonOpenNew, buttonOpenProfile,
                buttonOpenHome, buttonOpenSearch);// You can add any specific initialization code for the Profile activity here
    }
}
