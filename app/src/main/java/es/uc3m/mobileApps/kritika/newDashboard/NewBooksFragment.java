package es.uc3m.mobileApps.kritika.newDashboard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.Book;
import es.uc3m.mobileApps.kritika.Misc.ApiConstants;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A fragment that displays a list of new books.
 */
public class NewBooksFragment extends Fragment {
    private RecyclerView rvBooks;
    private NewBooksAdapter adapter;
    private List<Book> bookList = new ArrayList<>();

    // Required empty constructor for fragment.
    public NewBooksFragment() { }

    /**
     * Inflates the layout for this fragment and initializes UI components.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return                   The root view of the inflated layout.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.new_fragment_book, container, false);

        rvBooks = view.findViewById(R.id.rvBooks);
        rvBooks.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new NewBooksAdapter(getContext(), bookList);
        adapter.setOnItemClickListener(new NewBooksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {
                Intent intent = new Intent(getContext(), NewBooksDetailActivity.class);
                intent.putExtra("title", book.getTitle());
                intent.putExtra("id", book.getId());
                startActivity(intent);
            }
        });
        rvBooks.setAdapter(adapter);

        new NewBooksFragment.RetrieveBooksTask().execute();
        return view;
    }

    /**
     * AsyncTask to retrieve books from Google Books API in the background.
     */
    private class RetrieveBooksTask extends AsyncTask<Void, Void, List<Book>> {
        @Override
        protected List<Book> doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            List<Book> books = new ArrayList<>();
            String apiKey = ApiConstants.GOOGLE_BOOKS_API_KEY;
            String baseUrl = ApiConstants.GOOGLE_BOOKS_BESTSELLERS_URL + apiKey;

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
            }
        }
    }
}
