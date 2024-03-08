package es.uc3m.mobileApps.kritika;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MoviesActivity extends AppCompatActivity {
    private TextView tvResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        tvResponse = findViewById(R.id.tvResponse);
        new DiscoverMoviesTask().execute();
    }

    private class DiscoverMoviesTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://api.themoviedb.org/3/movie/popular?language=en-US&page=1")
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJiOWE1NGY3MzRjMzYxZjFmOWZkNjFiNDUyNDBhMzVmNyIsInN1YiI6IjY1ZDQ5ODc2YmJlMWRkMDE3ZDVmODhmYiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.sHPGWnO65YwdqEebPvXIjgBobTKEpE9WufBh9ebal8M") // Replace with your actual access token
                    .build();

            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                // Actualizar el TextView con la respuesta JSON
                tvResponse.setText(result);
            } else {
                Toast.makeText(MoviesActivity.this, "Failed to fetch data!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
