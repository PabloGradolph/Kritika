package es.uc3m.mobileApps.kritika.model;

public class Song implements SearchInterface {

    private String id;
    private String name;
    private String artistName;
    private String url;
    private String imageUrl;

    // Constructor, getters y setters
    public Song() {
    }

    public Song(String id, String name, String artistName, String url, String imageUrl) {
        this.id = id;
        this.name = name;
        this.artistName = artistName;
        this.url = url;
        this.imageUrl = imageUrl;
    }

    // Getters y Setters
    public String getId() { return id; }
    public String getName() { return name; }

    public String getTitle() { return name; } // for search interface

    @Override
    public String getImagePath() { // for search interface
        return imageUrl;
    }

    public void setName(String name) { this.name = name; }

    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }


    // Método para actualizar los detalles
    public void updateDetails(String name, String artistName, String url, String imageUrl) {
        this.name = name;
        this.artistName = artistName;
        this.url = url;
        this.imageUrl = imageUrl;
    }
}

