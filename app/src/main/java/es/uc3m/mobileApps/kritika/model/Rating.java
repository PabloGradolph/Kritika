package es.uc3m.mobileApps.kritika.model;

public class Rating {
    private String userId;
    private String mediaId;
    private String mediaType;
    private float rating;
    private String title;
    private String imageUrl;

    public Rating() {
    }

    public Rating(String userId, String mediaId, String mediaType, float rating) {
        this.userId = userId;
        this.mediaId = mediaId;
        this.mediaType = mediaType;
        this.rating = rating;
    }

    // Getters y setters

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
