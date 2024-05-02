package es.uc3m.mobileApps.kritika.Profile;

import android.util.Base64;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import es.uc3m.mobileApps.kritika.Misc.ApiConstants;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Class responsible for fetching media information.
 */
public class MediaInfoFetcher {

    private static final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    /**
     * Fetches the title of the media.
     *
     * @param mediaId   The ID of the media.
     * @param mediaType The type of media.
     * @param listener  Listener to be notified when the title is fetched or fetch fails.
     */
    public static void fetchTitle(String mediaId, String mediaType, OnTitleFetchedListener listener) {
        firestore.collection(mediaType)
                .document(mediaId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String title = documentSnapshot.getString("title");
                        listener.onTitleFetched(title);
                    } else {
                        listener.onTitleFetchFailed();
                    }
                })
                .addOnFailureListener(e -> listener.onTitleFetchFailed());
    }

    /**
     * Fetches the image URL of the media.
     *
     * @param mediaId   The ID of the media.
     * @param mediaType The type of media.
     * @param listener  Listener to be notified when the image URL is fetched or fetch fails.
     */
    public static void fetchImageUrl(String mediaId, String mediaType, OnImageUrlFetchedListener listener) {
        firestore.collection(mediaType)
                .document(mediaId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        if (documentSnapshot.contains("imagePath")) {
                            String imagePath = documentSnapshot.getString("imagePath");
                            if (mediaType.equals("movies")){
                                imagePath = ApiConstants.MOVIEDB_IMAGE_URL + imagePath;
                            }
                            listener.onImageUrlFetched(imagePath);
                        } else if (documentSnapshot.contains("image")) {
                            // If it doesn't contain "imagePath", check if it contains "image" and use it as image URL
                            String imageUrl = documentSnapshot.getString("image");
                            if (mediaType.equals("movies")){
                                imageUrl = ApiConstants.MOVIEDB_IMAGE_URL + imageUrl;
                            }
                            listener.onImageUrlFetched(imageUrl);
                        } else {
                            // If it doesn't contain "imagePath" nor "image", call an API to fetch image URL
                            fetchImageUrlFromAPI(mediaId, mediaType, listener);
                        }
                    } else {
                        listener.onImageUrlFetchFailed();
                    }
                })
                .addOnFailureListener(e -> listener.onImageUrlFetchFailed());
    }

    /**
     * Fetches the image URL from the corresponding API based on media type and ID.
     *
     * @param mediaId   The ID of the media.
     * @param mediaType The type of media.
     * @param listener  Listener to be notified when the image URL is fetched or fetch fails.
     */
    private static void fetchImageUrlFromAPI(String mediaId, String mediaType, OnImageUrlFetchedListener listener) {
        MediaInfoFetcher mediaInfoFetcher = new MediaInfoFetcher();

        switch (mediaType) {
            case "songs":
                mediaInfoFetcher.fetchImageUrlFromSpotifyAPI(mediaId, listener);
                break;
            case "movies":
                mediaInfoFetcher.fetchImageUrlFromMovieDBAPI(mediaId, listener);
                break;
            case "books":
                mediaInfoFetcher.fetchImageUrlFromGoogleBooksAPI(mediaId, listener);
                break;
            default:
                String fakeImageUrl = "https://example.com/image.jpg";
                listener.onImageUrlFetched(fakeImageUrl);
        }
    }

    /**
     * Fetches the image URL from the Spotify API for the given track ID.
     *
     * @param trackId   The ID of the track.
     * @param listener  Listener to be notified when the image URL is fetched or fetch fails.
     */
    private void fetchImageUrlFromSpotifyAPI(String trackId, OnImageUrlFetchedListener listener) {
        OkHttpClient client = new OkHttpClient();

        Log.d("MediaInfoFetcher", "Solicitando token de acceso a Spotify API");

        // Builds encrypted credentials for authentication
        String credentials = ApiConstants.S_CLIENT_ID + ":" + ApiConstants.S_CLIENT_SECRET;
        String base64Credentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        // Request a Spotify API Access Token
        Request tokenRequest = new Request.Builder()
                .url(ApiConstants.SPOTIFY_TOKEN_URL)
                .post(new FormBody.Builder().add("grant_type", "client_credentials").build())
                .addHeader("Authorization", "Basic " + base64Credentials)
                .build();

        client.newCall(tokenRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                listener.onImageUrlFetchFailed();
            }

            @Override
            public void onResponse(Call call, Response tokenResponse) throws IOException {
                if (!tokenResponse.isSuccessful()) {
                    listener.onImageUrlFetchFailed();
                    Log.e("MediaInfoFetcher", "Unsuccessful response when requesting Spotify API access token");
                    throw new IOException("Unexpected code " + tokenResponse);
                }

                try {
                    JSONObject tokenJson = new JSONObject(tokenResponse.body().string());
                    String accessToken = tokenJson.getString("access_token");

                    // Use the access token to make the request to the tracks endpoint.
                    String searchUrl = ApiConstants.SPOTIFY_TRACKS_URL + trackId;
                    Request request = new Request.Builder()
                            .url(searchUrl)
                            .addHeader("Authorization", "Bearer " + accessToken)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            listener.onImageUrlFetchFailed();
                            Log.e("MediaInfoFetcher", "Error when getting track information from Spotify API");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                listener.onImageUrlFetchFailed();
                                Log.e("MediaInfoFetcher", "Unsuccessful response when requesting the track information from Spotify API");
                                throw new IOException("Unexpected code " + response);
                            }

                            try {
                                String jsonData = response.body().string();
                                JSONObject track = new JSONObject(jsonData);

                                // Get the URL of the track image
                                JSONArray images = track.getJSONObject("album").getJSONArray("images");
                                if (images.length() > 0) {
                                    String imageUrl = images.getJSONObject(0).getString("url");
                                    listener.onImageUrlFetched(imageUrl);
                                } else {
                                    listener.onImageUrlFetchFailed();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                listener.onImageUrlFetchFailed();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onImageUrlFetchFailed();
                }
            }
        });
    }

    /**
     * Fetches the image URL from the MovieDB API for the given movie ID.
     *
     * @param mediaId   The ID of the movie.
     * @param listener  Listener to be notified when the image URL is fetched or fetch fails.
     */
    private void fetchImageUrlFromMovieDBAPI(String mediaId, OnImageUrlFetchedListener listener) {
        int movieId = Integer.parseInt(mediaId);
        OkHttpClient client = new OkHttpClient();

        // Build the request to obtain details of the movie by its ID
        Request request = new Request.Builder()
                .url(ApiConstants.MOVIEDB_BASE_URL + movieId)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", ApiConstants.MOVIEDB_ACCESS_TOKEN)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                listener.onImageUrlFetchFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    listener.onImageUrlFetchFailed();
                    throw new IOException("Unexpected code " + response);
                }

                try {
                    String jsonData = response.body().string();
                    JSONObject movieJson = new JSONObject(jsonData);

                    // Extracts the URL of the image from the JSON response
                    String posterPath = movieJson.getString("poster_path");
                    if (!posterPath.isEmpty()) {
                        String imageUrl = ApiConstants.MOVIEDB_IMAGE_URL + posterPath;
                        listener.onImageUrlFetched(imageUrl);
                    } else {
                        listener.onImageUrlFetchFailed();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onImageUrlFetchFailed();
                }
            }
        });
    }

    /**
     * Fetches the image URL from the Google Books API for the given book ID.
     *
     * @param mediaId   The ID of the book.
     * @param listener  Listener to be notified when the image URL is fetched or fetch fails.
     */
    private void fetchImageUrlFromGoogleBooksAPI(String mediaId, OnImageUrlFetchedListener listener) {
        OkHttpClient client = new OkHttpClient();
        String apiKey = ApiConstants.GOOGLE_BOOKS_API_KEY;
        String baseUrl = ApiConstants.GOOGLE_BOOKS_VOLUMES_URL + mediaId + "?key=" + apiKey;

        Request request = new Request.Builder()
                .url(baseUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                listener.onImageUrlFetchFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    listener.onImageUrlFetchFailed();
                    throw new IOException("Unexpected code " + response);
                }

                try {
                    String jsonData = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonData);
                    JSONObject volumeInfo = jsonObject.getJSONObject("volumeInfo");
                    JSONObject imageLinks = volumeInfo.optJSONObject("imageLinks");

                    if (imageLinks != null) {
                        String thumbnail = imageLinks.optString("thumbnail", "");
                        thumbnail = thumbnail.replace("http://", "https://");
                        listener.onImageUrlFetched(thumbnail);
                    } else {
                        listener.onImageUrlFetchFailed();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onImageUrlFetchFailed();
                }
            }
        });
    }

    /**
     * Listener interface for receiving title fetch results.
     */
    public interface OnTitleFetchedListener {
        void onTitleFetched(String title);

        void onTitleFetchFailed();
    }

    /**
     * Listener interface for receiving image URL fetch results.
     */
    public interface OnImageUrlFetchedListener {
        void onImageUrlFetched(String imageUrl);

        void onImageUrlFetchFailed();
    }
}

