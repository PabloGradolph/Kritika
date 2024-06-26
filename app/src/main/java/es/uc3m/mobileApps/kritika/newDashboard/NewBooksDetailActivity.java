package es.uc3m.mobileApps.kritika.newDashboard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import es.uc3m.mobileApps.kritika.Actions.AddtoListActivity;
import es.uc3m.mobileApps.kritika.Actions.RateActivity;
import es.uc3m.mobileApps.kritika.Actions.ReviewActivity;
import es.uc3m.mobileApps.kritika.Misc.ApiConstants;
import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.Book;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class NewBooksDetailActivity extends AppCompatActivity {

    private FloatingActionButton openMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_book_item_detail);

        // Get id of book and fetch details from DB
        String bookId = getIntent().getStringExtra("id");
        if (bookId != null) {
            fetchBookFromDB(bookId);
        } else {
            Toast.makeText(this, "Book name not provided", Toast.LENGTH_SHORT).show();
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
        String bookId = getIntent().getStringExtra("id");
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_menu, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        bottomSheetView.findViewById(R.id.rateButton).setOnClickListener(v -> {
            // Start RateActivity
            Intent intent = new Intent(this, RateActivity.class);
            intent.putExtra("mediaId", bookId);
            intent.putExtra("mediaType", "books");
            startActivity(intent);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.addToListButton).setOnClickListener(v -> {
            // Start AddToListActivity
            Intent intent = new Intent(this, AddtoListActivity.class);
            intent.putExtra("mediaId", bookId);
            intent.putExtra("mediaType", "books");
            startActivity(intent);
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.reviewButton).setOnClickListener(v -> {
            // Start ReviewActivity
            Intent intent = new Intent(this, ReviewActivity.class);
            intent.putExtra("mediaId", bookId);
            intent.putExtra("mediaType", "books");
            startActivity(intent);
            bottomSheetDialog.dismiss();
        });
    }


    /**
     * AsyncTask to fetch details of the book from the API.
     */
    private class FetchBooksFromAPI extends AsyncTask<String, Void, Book> {
        @Override
        protected Book doInBackground(String... bookIds) {
            OkHttpClient client = new OkHttpClient();
            String apiKey = ApiConstants.GOOGLE_BOOKS_API_KEY;
            String baseUrl = ApiConstants.GOOGLE_BOOKS_VOLUMES_URL + bookIds[0] + "?key=" + apiKey;

            Request request = new Request.Builder()
                    .url(baseUrl)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonData);

                JSONObject volumeInfo = jsonObject.getJSONObject("volumeInfo");
                JSONArray authorsJsonArray = volumeInfo.optJSONArray("authors");
                List<String> authors = new ArrayList<>();
                if (authorsJsonArray != null) {
                    for (int j = 0; j < authorsJsonArray.length(); j++) {
                        authors.add(authorsJsonArray.getString(j));
                    }
                String title = volumeInfo.getString("title");
                String publisher = volumeInfo.optString("publisher", "N/A");
                String publishedDate = volumeInfo.optString("publishedDate", "N/A");
                String rawDescription = volumeInfo.optString("description", "No description available.");
                String description = stripHtml(rawDescription); //After parsing
                String thumbnail = volumeInfo.getJSONObject("imageLinks").optString("thumbnail", "");
                thumbnail = thumbnail.replace("http://", "https://");
                double averageRating = volumeInfo.optDouble("averageRating", 0.0);

                return new Book(jsonObject.getString("id"), title, authors, publisher, publishedDate, description, thumbnail, averageRating, "books");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        public String stripHtml(String html) {
            // Remove HTML tags
            String cleanHtml = html.replaceAll("&quot;", "\"");
            return cleanHtml.replaceAll("\\<.*?\\>", "");
        }


        protected void onPostExecute(Book book) {

            super.onPostExecute(book);
            if (book != null) {
                TextView tvTitle = findViewById(R.id.tvTitle);
                TextView tvOverview = findViewById(R.id.tvOverview);
                TextView tvAuthor = findViewById(R.id.tvAuthor);
                ImageView imageViewPoster = findViewById(R.id.imageViewThumbnail);

                tvTitle.setText(book.getTitle());
                tvOverview.setText(book.getDescription());
                tvAuthor.setText(String.join(", ", book.getAuthors()));

                Glide.with(NewBooksDetailActivity.this)
                        .load(book.getThumbnail())
                        .into(imageViewPoster);

            } else {
                Toast.makeText(NewBooksDetailActivity.this, "Error loading details of book.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * AsyncTask to fetch details of the book from the DB.
     */
    private void fetchBookFromDB(String bookId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("books").document(bookId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {

                    if (document.contains("image")) {
                        updateUIWithBookDetails(document);
                    } else {
                        new NewBooksDetailActivity.FetchBooksFromAPI().execute(bookId);
                    }
                    //
                    Log.d("document", String.valueOf(document));
                } else {
                    Log.e("BookDetail", "No such document");
                    Toast.makeText(NewBooksDetailActivity.this, "No book details found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("BookDetail", "Error fetching book details", task.getException());
                Toast.makeText(NewBooksDetailActivity.this, "Error loading book details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Task to update the UI with the details from the API
     */
    private void updateUIWithBookDetails(DocumentSnapshot book) {
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvOverview = findViewById(R.id.tvOverview);
        TextView tvAuthor = findViewById(R.id.tvAuthor);
        ImageView imageViewPoster = findViewById(R.id.imageViewThumbnail);

        tvTitle.setText(book.getString("title"));

        tvOverview.setText(book.getString("description"));

        Object authors = book.get("authors");
        String authorsText = handleAuthors(authors);
        tvAuthor.setText(formatAuthors(authorsText));

        String posterUrl = book.getString("image");
        Glide.with(this)
                .load(posterUrl)
                .into(imageViewPoster);
    }

    /**
     * Formats a field that could be a List or a single String.
     * @param field The object that might be a String or List.
     * @return A formatted String.
     */
    private String handleAuthors(Object field) {
        if (field instanceof List) {
            List<?> list = (List<?>) field;
            return list.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
        } else if (field instanceof String) {
            return (String) field;
        } else {
            return "";
        }
    }

    /**
     * Formats authors that are String.
     * @param authors The String that contains authors to be formatted.
     * @return A formatted String.
     */
    private String formatAuthors(String authors) {
        if (authors == null || authors.isEmpty()) {
            return "No authors listed";
        }
        String[] authorsArray = authors.replace("[", "")
                .replace("]", "")
                .replace("'", "")
                .trim()
                .split("\\s*,\\s*");

        return Arrays.stream(authorsArray)
                .collect(Collectors.joining(", "));
    }
}
