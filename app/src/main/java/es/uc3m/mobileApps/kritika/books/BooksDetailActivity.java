package es.uc3m.mobileApps.kritika.books;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.uc3m.mobileApps.kritika.Actions.AddtoListActivity;
import es.uc3m.mobileApps.kritika.Actions.RateActivity;
import es.uc3m.mobileApps.kritika.Actions.ReviewActivity;
import es.uc3m.mobileApps.kritika.Misc.ApiConstants;
import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.Book;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class BooksDetailActivity extends AppCompatActivity {

    private FloatingActionButton openMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_item_detail);

        String bookTitle = getIntent().getStringExtra("title");

        if (bookTitle != null) {
            new FetchBooksDetailsTask().execute(bookTitle);
        } else {
            // Manejar el caso de que no se encuentre un ID válido
        }

        openMenuButton = findViewById(R.id.openMenuButton);

        openMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetMenu();
            }
        });
    }

    private void showBottomSheetMenu() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_menu, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        bottomSheetView.findViewById(R.id.rateButton).setOnClickListener(v -> {
            // Iniciar RateActivity
            Intent intent = new Intent(this, RateActivity.class);
            startActivity(intent);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.addToListButton).setOnClickListener(v -> {
            // Iniciar AddToListActivity
            Intent intent = new Intent(this, AddtoListActivity.class);
            startActivity(intent);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.reviewButton).setOnClickListener(v -> {
            // Iniciar ReviewActivity
            Intent intent = new Intent(this, ReviewActivity.class);
            startActivity(intent);
            bottomSheetDialog.dismiss();
        });
    }

    private class FetchBooksDetailsTask extends AsyncTask<String, Void, Book> {
        @Override
        protected Book doInBackground(String... bookTitles) {
            OkHttpClient client = new OkHttpClient();
            String bookTitleQuery = bookTitles[0].replace(" ", "+"); // Reemplaza los espacios con el símbolo '+' para la request a la API
            String apiKey = ApiConstants.GOOGLE_BOOKS_API_KEY;
            String baseUrl = ApiConstants.GOOGLE_BOOKS_SEARCH_URL + bookTitleQuery + "&key=" + apiKey;

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

                    return new Book(item.getString("id"), title, authors, publisher, publishedDate, description, thumbnail);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }




        protected void onPostExecute(Book book) {
            super.onPostExecute(book);
            if (book != null) {
                TextView tvTitle = findViewById(R.id.tvTitle);
                TextView tvOverview = findViewById(R.id.tvOverview);
                TextView tvAuthor = findViewById(R.id.tvAuthor);
                ImageView imageViewPoster = findViewById(R.id.imageViewThumbnail);

                tvTitle.setText(book.getTitle());
                String shortDescription = book.getDescription().length() > 100
                        ? book.getDescription().substring(0, 100) + "..."
                        : book.getDescription();
                tvOverview.setText(shortDescription);
                tvAuthor.setText(String.join(", ", book.getAuthors()));
                // Cargar imagen de portada con Glide
                Glide.with(BooksDetailActivity.this)
                        .load(book.getThumbnail())
                        .into(imageViewPoster);

            }else {
                Toast.makeText(BooksDetailActivity.this, "Error al cargar los detalles del libro.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
