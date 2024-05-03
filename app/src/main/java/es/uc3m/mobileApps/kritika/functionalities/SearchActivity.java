package es.uc3m.mobileApps.kritika.functionalities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import es.uc3m.mobileApps.kritika.DashboardUserActivity;
import es.uc3m.mobileApps.kritika.MainActivity;
import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.newDashboard.NewBooksDetailActivity;
import es.uc3m.mobileApps.kritika.newDashboard.NewMoviesDetailActivity;
import es.uc3m.mobileApps.kritika.newDashboard.newMusicDetailActivity;

public class SearchActivity extends DashboardUserActivity {

    private EditText searchBar;
    private Spinner contentTypeSpinner;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private SearchAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // layout
        setContentView(R.layout.search_activity);

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

        // dropdown
        setupSpinner();

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

        // views
        searchBar = findViewById(R.id.searchBar);
        contentTypeSpinner = findViewById(R.id.contentTypeSpinner);
        recyclerView = findViewById(R.id.rvSearchResults);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);



        // Search functionality
        ImageButton buttonSearch = findViewById(R.id.buttonSearch);

        // OnClickListener in search button
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // search query and type of media
                String searchQuery = searchBar.getText().toString().trim();
                String contentType = contentTypeSpinner.getSelectedItem().toString();

                // Check search query is not empty before searching
                if (!searchQuery.isEmpty()) {
                    performSearch(searchQuery, contentType);
                } else {
                    Toast.makeText(SearchActivity.this, "Please enter a search term", Toast.LENGTH_SHORT).show();
                }
            }
        });


        adapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot item) {
                String type = item.getString("type");
                Intent intent = new Intent();

                switch (type) {
                    case "movies":
                        // Start movie detail activity
                        intent = new Intent(SearchActivity.this, NewMoviesDetailActivity.class);
                        try {
                            intent.putExtra("id", Integer.parseInt(item.getId()));
                        } catch (NumberFormatException e) {
                            intent.putExtra("id", item.getId());
                        }
                        break;
                    case "songs":
                        // Start song detail activity
                        intent = new Intent(SearchActivity.this, newMusicDetailActivity.class);
                        intent.putExtra("id", item.getId());
                        break;
                    case "books":
                        // Start book detail activity
                        intent = new Intent(SearchActivity.this, NewBooksDetailActivity.class);
                        intent.putExtra("id", item.getId());
                        break;
                }
                // Add common data to intent
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });
    }

    /**
     * Task to set up the Spinner for the search
     */
    private void setupSpinner() {
        Spinner spinner = findViewById(R.id.contentTypeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.content_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /**
     * Task to perform a search from the API
     */
    private void performSearch(String query, String type) {
        // convert type of media ("movies", "songs", "books") to name of the collection
        String collectionPath = type.toLowerCase();

        // prepare dynamic search query
        Query searchQuery = db.collection(collectionPath)
                .orderBy("title")
                .startAt(query)
                .endAt(query + "\uf8ff") // \uf8ff unicode, to incluse all fields containing "query"
                .limit(20);
        // Execute search query
        searchQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                // update RecyclerView with results of the search
                updateUIWithResults(documents);
            } else {
                Toast.makeText(SearchActivity.this, "Search failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * Task to update the UI with the details from the API
     */
    private void updateUIWithResults(List<DocumentSnapshot> results) {
        List<Object> searchables = new ArrayList<>();
        for (DocumentSnapshot document : results) {
            if (!document.exists()) {
                continue;
            }

            String type = document.getString("type");
            if (type == null) {
                continue;
            }
            Log.d("Document Data", document.getId() + " => " + document.getData());
            try {
                switch (type) {
                    case "movies":
                        DocumentSnapshot movie = document;
                        if (movie != null) searchables.add(movie);
                        break;
                    case "songs":
                        DocumentSnapshot song = document;
                        if (song != null) searchables.add(song);
                        break;
                    case "books":
                        DocumentSnapshot book = document;
                        if (book != null) searchables.add(book);
                        break;
                }
            } catch (Exception e) {
                Log.e("updateUIWithResults", "Error processing document: " + document.getId(), e);
            }
        }
        adapter.updateData(searchables);
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

