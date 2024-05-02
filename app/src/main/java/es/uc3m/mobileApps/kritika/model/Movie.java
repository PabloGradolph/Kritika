package es.uc3m.mobileApps.kritika.model;

public class Movie implements SearchInterface {
    private int id;
    private String title;
    private String overview;
    private String posterPath;
    private String rating;
    private String releaseDate;

    public Movie() {
    }

    public Movie(int id, String title, String overview, String posterPath, String rating, String releaseDate) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.rating = rating;
        this.releaseDate = releaseDate;
    }

    // Getters y Setters para cada campo
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOverview() { return overview; }

    public String getPosterPath() { return posterPath; }

    public String getImagePath() { return posterPath; } // for interface

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    // Method for updating movie details
    public void updateDetails(String title, String overview, String posterPath, String rating, String releaseDate) {
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.rating = rating;
        this.releaseDate = releaseDate;
    }
}

