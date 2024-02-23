package es.uc3m.mobileApps.kritika.model;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "movies")
public class Movie {
    @PrimaryKey
    private int id;

    private String title;
    private String overview;
    private String posterPath;
    private double rating;
    private String releaseDate;

    // Constructor, getters y setters
    public Movie(int id, String title, String overview, String posterPath, double rating, String releaseDate) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.rating = rating;
        this.releaseDate = releaseDate;
    }

    // Getters y Setters para cada campo

    // id
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    // title
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    // overview
    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }

    // posterPath
    public String getPosterPath() { return posterPath; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }

    // rating
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    // releaseDate
    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
}
