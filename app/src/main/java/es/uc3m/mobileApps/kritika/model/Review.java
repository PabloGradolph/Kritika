package es.uc3m.mobileApps.kritika.model;

public class Review {
    private String user;
    private String reviewText;
    private boolean isPublic;
    private String mediaId;
    private String mediaType;
    private String title;
    private String imageUrl;

    public Review() {
    }

    public Review(String user, String reviewText, boolean isPublic, String mediaId, String mediaType) {
        this.user = user;
        this.reviewText = reviewText;
        this.isPublic = isPublic;
        this.mediaId = mediaId;
        this.mediaType = mediaType;
    }

    // Getters y setters
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getReviewText() {
        return reviewText;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
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
