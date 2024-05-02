package es.uc3m.mobileApps.kritika.books;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import es.uc3m.mobileApps.kritika.Misc.ApiConstants;
import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.Book;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Activity for displaying a list of books and fetching book data from Google Books API.
 */
public class BooksActivity extends DashboardUserActivity {
    private RecyclerView rvBooks;
    private BooksAdapter adapter;
    private List<Book> bookList = new ArrayList<>();
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);

        // Initialize RecyclerView
        rvBooks = findViewById(R.id.rvBooks);
        rvBooks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BooksAdapter(this, bookList);
        adapter.setOnItemClickListener(new BooksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {
                Intent intent = new Intent(BooksActivity.this,BooksDetailActivity.class);
                intent.putExtra("title", book.getTitle());
                intent.putExtra("id", book.getId());
                startActivity(intent);
            }
        });
        rvBooks.setAdapter(adapter);

        // Initialize Firebase authentication
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        // Set logout button click listener
        ImageButton buttonLogOut = findViewById(R.id.logoutBtn);
        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

        // Set click listeners for top and bottom bar buttons
        Button buttonOpenMovies = findViewById(R.id.button_open_movies);
        Button buttonOpenMusic = findViewById(R.id.button_open_music);
        Button buttonOpenBooks = findViewById(R.id.button_open_books);
        Button buttonOpenReviews = findViewById(R.id.button_open_reviews);
        ImageButton buttonOpenProfile = findViewById(R.id.profileButton);
        ImageButton buttonOpenHome = findViewById(R.id.houseButton);
        ImageButton buttonOpenSearch = findViewById(R.id.searchButton);
        setButtonListeners(buttonOpenMovies, buttonOpenMusic, buttonOpenBooks, buttonOpenReviews, buttonOpenProfile,
                buttonOpenHome, buttonOpenSearch);

        // Fetch books data from Google Books API
        new DiscoverBooksTask().execute();
    }

    /**
     * AsyncTask to fetch books data from Google Books API.
     */
    private class DiscoverBooksTask extends AsyncTask<Void, Void, List<Book>> {
        @Override
        protected List<Book> doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            List<Book> books = new ArrayList<>();
            String apiKey = ApiConstants.GOOGLE_BOOKS_API_KEY;
            String baseUrl = ApiConstants.GOOGLE_BOOKS_NEWRELEASES_URL + apiKey;

            Request request = new Request.Builder()
                    .url(baseUrl)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonData);
                JSONArray items = jsonObject.getJSONArray("items");

                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                    JSONArray authorsJsonArray = volumeInfo.optJSONArray("authors");
                    List<String> authors = new ArrayList<>();
                    if (authorsJsonArray != null) {
                        for (int j = 0; j < authorsJsonArray.length(); j++) {
                            authors.add(authorsJsonArray.getString(j));
                        }
                    }
                    String title = volumeInfo.getString("title");
                    String publisher = volumeInfo.optString("publisher", "N/A");
                    String publishedDate = volumeInfo.optString("publishedDate", "N/A");
                    String description = volumeInfo.optString("description", "No description available.");
                    String thumbnail = volumeInfo.getJSONObject("imageLinks").optString("thumbnail", "");
                    thumbnail = thumbnail.replace("http://", "https://");
                    double averageRating = volumeInfo.optDouble("averageRating", 0.0);

                    books.add(new Book(item.getString("id"), title, authors, publisher, publishedDate, description, thumbnail, averageRating));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return books;
        }

        @Override
        protected void onPostExecute(List<Book> books) {
            super.onPostExecute(books);
            if (!books.isEmpty()) {
                bookList.clear();
                bookList.addAll(books);
                adapter.notifyDataSetChanged();

                // Save books data to Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference booksRef = db.collection("books");

                for (Book book : books) {
                    DocumentReference docRef = booksRef.document(String.valueOf(book.getId()));
                    docRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (!document.exists()) {
                                booksRef.document(String.valueOf(book.getId())).set(book);
                                Log.d(TAG, "The document for book with ID: " + book.getId() + " added.");
                            } else {
                                Log.d(TAG, "The document exists for book with ID: " + book.getId());
                            }
                        } else {
                            Log.d(TAG, "Error getting document: ", task.getException());
                        }
                    });
                }
            } else {
                Toast.makeText(BooksActivity.this, "Failed to fetch books data!", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Method to check if the user is authenticated.
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
