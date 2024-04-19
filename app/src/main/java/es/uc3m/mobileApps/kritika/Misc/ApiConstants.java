package es.uc3m.mobileApps.kritika.Misc;

public class ApiConstants {
    public static final String SPOTIFY_BASE_URL = "https://api.spotify.com/v1/";
    public static final String SPOTIFY_TOKEN_URL = "https://accounts.spotify.com/api/token";
    public static final String SPOTIFY_TRACKS_URL = "https://api.spotify.com/v1/tracks/";
    public static final String SPOTIFY_SEARCH_URL = "https://api.spotify.com/v1/search?q=";

    public static final String SPOTIFY_PLAYLIST_URL = SPOTIFY_BASE_URL + "playlists/37i9dQZEVXbMDoHDwVN2tF/tracks";
    public static final String SPOTIFY_NEWRELEASES_URL = SPOTIFY_BASE_URL + "playlists/37i9dQZF1DX6PYajiT4pAf/tracks";


    // Spotify API credentials
    public static final String S_CLIENT_ID = "904e4d28994c4a70963a2fb5b5744729";
    public static final String S_CLIENT_SECRET = "fb988307f5fd400fb34e8400fd557ca8";

    //Movies
    public static final String MOVIEDB_BASE_URL = "https://api.themoviedb.org/3/movie/";
    public static final String MOVIEDB_POPULAR_MOVIES_URL = MOVIEDB_BASE_URL + "popular?language=en-US&sort_by=popularity.desc&page=";
    public static final String MOVIEDB_UPCOMING_MOVIES_URL = MOVIEDB_BASE_URL + "upcoming?language=en-US&page=";
    public static final String MOVIEDB_TOPRATED_MOVIES_URL = MOVIEDB_BASE_URL +"top_rated?language=en-US&page=";
    public static final String MOVIEDB_IMAGE_URL = "https://image.tmdb.org/t/p/w500";
    // MovieDB API access token
    public static final String MOVIEDB_ACCESS_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI1YzBmM2FmNjcyNjM5YTJjZmUyNmY4NDMyMjk5NjNmNCIsInN1YiI6IjY1ZDg5ZjZiMTQ5NTY1MDE2MmY1YTZhNCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.Io4x374YopHoiG57NIBLZEroKn2vInK1Dzfddkp-ECE";

    // Books
    public static final String GOOGLE_BOOKS_BASE_URL = "https://www.googleapis.com/books/v1/";
    public static final String GOOGLE_BOOKS_VOLUMES_URL = GOOGLE_BOOKS_BASE_URL + "volumes/";
    public static final String GOOGLE_BOOKS_BESTSELLERS_URL = GOOGLE_BOOKS_BASE_URL + "volumes?q=bestsellers&key=";
    public static final String GOOGLE_BOOKS_NEWRELEASES_URL = GOOGLE_BOOKS_BASE_URL + "volumes?q=new+releases&orderBy=newest&key=";

    // Google Books API key
    public static final String GOOGLE_BOOKS_API_KEY = "AIzaSyAYXAuFSEO31onyneSK__KfxiYEdyyhIaA";
    public static final String GOOGLE_BOOKS_SEARCH_URL = "https://www.googleapis.com/books/v1/volumes?q=intitle:";

}
