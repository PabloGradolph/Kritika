package es.uc3m.mobileApps.kritika.newDashboard;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import es.uc3m.mobileApps.kritika.R;


public class newDashboardUserActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Carga el layout
        setContentView(R.layout.new_activity_dashboard_user);

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

}