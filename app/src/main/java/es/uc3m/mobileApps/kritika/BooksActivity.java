package es.uc3m.mobileApps.kritika;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.uc3m.mobileApps.kritika.model.Book;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BooksActivity extends DashboardUserActivity {
    private RecyclerView rvBooks;
    private BooksAdapter adapter;
    private List<Book> bookList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);

        rvBooks = findViewById(R.id.rvBooks);
        rvBooks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BooksAdapter(this, bookList);
        rvBooks.setAdapter(adapter);

        Button buttonOpenMovies = findViewById(R.id.button_open_movies);
        Button buttonOpenMusic = findViewById(R.id.button_open_music);
        Button buttonOpenBooks = findViewById(R.id.button_open_books);
        ImageButton buttonOpenProfile = findViewById(R.id.profileButton);
        ImageButton buttonOpenHome = findViewById(R.id.houseButton);
        ImageButton buttonOpenSearch = findViewById(R.id.searchButton);

        // Set click listeners for buttons
        setButtonListeners(buttonOpenMovies, buttonOpenMusic, buttonOpenBooks, buttonOpenProfile,
                buttonOpenHome, buttonOpenSearch);
        new DiscoverBooksTask().execute();
    }

    private class DiscoverBooksTask extends AsyncTask<Void, Void, List<Book>> {
        @Override
        protected List<Book> doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            List<Book> books = new ArrayList<>();
            String apiKey = "AIzaSyAYXAuFSEO31onyneSK__KfxiYEdyyhIaA";
            String baseUrl = "https://www.googleapis.com/books/v1/volumes?q=bestsellers&key=" + apiKey;

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

                    books.add(new Book(title, authors, publisher, publishedDate, description, thumbnail));
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
            } else {
                Toast.makeText(BooksActivity.this, "Failed to fetch books data!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
