package es.uc3m.mobileApps.kritika.model;

import java.util.ArrayList;
import java.util.List;

public class MediaList {
    private String userId;
    private String listName;
    private String mediaType;
    private List<String> mediaIds;
    private String title;
    private String imageUrl;

    public MediaList() {
    }

    public MediaList(String userId, String listName, String mediaType) {
        this.userId = userId;
        this.listName = listName;
        this.mediaType = mediaType;
        this.mediaIds = new ArrayList<>();
    }

    // Getters y setters

    public String getListName() {
        return listName;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public List<String> getMediaIds() {
        return mediaIds;
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

