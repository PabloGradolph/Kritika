
package es.uc3m.mobileApps.kritika.movies;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.uc3m.mobileApps.kritika.DashboardUserActivity;
import es.uc3m.mobileApps.kritika.MainActivity;
import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.Movie;
import es.uc3m.mobileApps.kritika.Misc.ApiConstants;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Activity to display a list of movies and allow users to interact with them.
 */
public class MoviesActivity extends DashboardUserActivity {
    private RecyclerView rvMovies;
    private MoviesAdapter adapter;
    private List<Movie> movieList = new ArrayList<>();
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        // Initialize RecyclerView
        rvMovies = findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MoviesAdapter(this, movieList);
        adapter.setOnItemClickListener(new MoviesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Movie movie) {
                Intent intent = new Intent(MoviesActivity.this, MovieDetailActivity.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });
        rvMovies.setAdapter(adapter);

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        // Logout button click listener
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
                buttonOpenHome, buttonOpenSearch);

        // Fetch upcoming movies asynchronously
        new DiscoverMoviesTask().execute();
    }

    /**
     * AsyncTask to fetch upcoming movies from the MovieDB API.
     */
    protected class DiscoverMoviesTask extends AsyncTask<Void, Void, List<Movie>> {
        @Override
        protected List<Movie> doInBackground(Void... voids) {
            // Create logging interceptor and set log level
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Build OkHttpClient and add logging interceptor
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            List<Movie> movies = new ArrayList<>();
            String baseUrl = ApiConstants.MOVIEDB_UPCOMING_MOVIES_URL;
            String token = ApiConstants.MOVIEDB_ACCESS_TOKEN;

            // Iterate over pages to fetch movie data
            for (int page = 1; page <= 3; page++) {
                Request request = new Request.Builder()
                        .url(baseUrl + page)
                        .get()
                        .addHeader("accept", "application/json")
                        .addHeader("Authorization", token)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    String jsonData = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONArray results = jsonObject.getJSONArray("results");

                    // Parse JSON data and create Movie objects
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject movieJson = results.getJSONObject(i);
                        Movie movie = new Movie(
                                movieJson.getInt("id"),
                                movieJson.getString("title"),
                                movieJson.getString("overview"),
                                movieJson.getString("poster_path"),
                                movieJson.getString("vote_average"),
                                movieJson.getString("release_date")
                        );
                        movies.add(movie);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return movies;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);
            if (movies != null) {
                // Update movie list and notify adapter
                movieList.clear();
                movieList.addAll(movies);
                adapter.notifyDataSetChanged();

                // Save movies to Firestore if they don't already exist
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference moviesRef = db.collection("movies");

                for (Movie movie : movies) {
                    // Check if the document exists in Firestore based on its ID
                    DocumentReference docRef = moviesRef.document(String.valueOf(movie.getId()));
                    docRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (!document.exists()) {
                                // Movie doesn't exist in Firestore, save it
                                moviesRef.document(String.valueOf(movie.getId())).set(movie);
                            }
                        } else {
                            Log.d(TAG, "Error getting document: ", task.getException());
                        }
                    });
                }
            }
        }
    }

    /**
     * Check if a user is logged in. If not, redirect to the main screen.
     */
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
