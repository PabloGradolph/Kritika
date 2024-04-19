package es.uc3m.mobileApps.kritika.model;

public class Rating {
    private String userId;
    private String mediaId;
    private String mediaType;
    private float rating; // Puntuación del 1 al 5

    // Constructor vacío requerido para Firestore
    public Rating() {
    }

    public Rating(String userId, String mediaId, String mediaType, float rating) {
        this.userId = userId;
        this.mediaId = mediaId;
        this.mediaType = mediaType;
        this.rating = rating;
    }

    // Getters y setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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
}
