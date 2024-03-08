package es.uc3m.mobileApps.kritika;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MusicActivity extends AppCompatActivity {

    private TextView musicResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        musicResponse = findViewById(R.id.musicResponse);
        new DiscoverArtistsTask().execute();
    }
    //Comment testea si funciona esta llamada a la API a la que tengas WIFI
    // KEY: 96f13ee809056c77d25defbd0f813024
    private class DiscoverArtistsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://ws.audioscrobbler.com/2.0/?method=chart.gettopartists&api_key=96f13ee809056c77d25defbd0f813024&format=json")
                    .get()
                    .build();

            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                // Update the UI with the JSON response
                musicResponse.setText(result);
            } else {
                Toast.makeText(MusicActivity.this, "Failed to fetch data!", Toast.LENGTH_LONG).show();
            }
        }
    }
}