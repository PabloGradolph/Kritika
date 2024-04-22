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

public class MediaInfoFetcher {

    private static final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

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
                            // Si no contiene "imagePath", comprobar si contiene "image" y usarlo como URL de la imagen
                            String imageUrl = documentSnapshot.getString("image");
                            if (mediaType.equals("movies")){
                                imageUrl = ApiConstants.MOVIEDB_IMAGE_URL + imageUrl;
                            }
                            listener.onImageUrlFetched(imageUrl);
                        } else {
                            // Si no contiene "imagePath" ni "image", llamar a una API para obtener la URL de la imagen
                            fetchImageUrlFromAPI(mediaId, mediaType, listener);
                        }
                    } else {
                        listener.onImageUrlFetchFailed();
                    }
                })
                .addOnFailureListener(e -> listener.onImageUrlFetchFailed());
    }

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
                // Si el mediaType no es reconocido, simplemente devuelve una URL de imagen ficticia
                String fakeImageUrl = "https://example.com/image.jpg";
                listener.onImageUrlFetched(fakeImageUrl);
        }
    }

    private void fetchImageUrlFromSpotifyAPI(String trackId, OnImageUrlFetchedListener listener) {
        OkHttpClient client = new OkHttpClient();

        Log.d("MediaInfoFetcher", "Solicitando token de acceso a Spotify API");

        // Construye las credenciales codificadas para la autenticación
        String credentials = ApiConstants.S_CLIENT_ID + ":" + ApiConstants.S_CLIENT_SECRET;
        String base64Credentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        // Solicita un token de acceso a la API de Spotify
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
                    Log.e("MediaInfoFetcher", "Respuesta no exitosa al solicitar el token de acceso a Spotify API");
                    throw new IOException("Unexpected code " + tokenResponse);
                }

                try {
                    JSONObject tokenJson = new JSONObject(tokenResponse.body().string());
                    String accessToken = tokenJson.getString("access_token");

                    // Utiliza el token de acceso para hacer la solicitud al endpoint de tracks
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
                            Log.e("MediaInfoFetcher", "Error al obtener la información del track desde Spotify API");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                listener.onImageUrlFetchFailed();
                                Log.e("MediaInfoFetcher", "Respuesta no exitosa al solicitar la información del track desde Spotify API");
                                throw new IOException("Unexpected code " + response);
                            }

                            try {
                                String jsonData = response.body().string();
                                JSONObject track = new JSONObject(jsonData);

                                // Obtener la URL de la imagen del track
                                JSONArray images = track.getJSONObject("album").getJSONArray("images");
                                if (images.length() > 0) {
                                    String imageUrl = images.getJSONObject(0).getString("url");
                                    listener.onImageUrlFetched(imageUrl);
                                    Log.d("MediaInfoFetcher", "URL de la imagen del track: " + imageUrl);
                                } else {
                                    listener.onImageUrlFetchFailed();
                                    Log.e("MediaInfoFetcher", "No se encontraron imágenes del track");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                listener.onImageUrlFetchFailed();
                                Log.e("MediaInfoFetcher", "Error al procesar la respuesta JSON del track");
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onImageUrlFetchFailed();
                    Log.e("MediaInfoFetcher", "Error al procesar la respuesta JSON del token de acceso a Spotify API");
                }
            }
        });
    }

    private void fetchImageUrlFromMovieDBAPI(String mediaId, OnImageUrlFetchedListener listener) {
        int movieId = Integer.parseInt(mediaId);
        OkHttpClient client = new OkHttpClient();

        // Construye la solicitud para obtener detalles de la película por su ID
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

                    // Extrae la URL de la imagen de la respuesta JSON
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

    public interface OnTitleFetchedListener {
        void onTitleFetched(String title);

        void onTitleFetchFailed();
    }

    public interface OnImageUrlFetchedListener {
        void onImageUrlFetched(String imageUrl);

        void onImageUrlFetchFailed();
    }
}

