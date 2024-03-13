package es.uc3m.mobileApps.kritika.model;

public class Movie {
    private int id;
    private String title;
    private String overview;
    private String posterPath;
    private double rating;
    private String releaseDate;

    // Constructor vacío requerido por Firebase
    public Movie() {
    }

    // Constructor para nuestra comodidad
    public Movie(int id, String title, String overview, String posterPath, double rating, String releaseDate) {
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
    public void setOverview(String overview) { this.overview = overview; }

    public String getPosterPath() { return posterPath; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
}

