package es.uc3m.mobileApps.kritika.model;

import java.util.ArrayList;
import java.util.List;

public class MediaList {
    private String userId;
    private String listName;
    private String mediaType;
    private List<String> mediaIds;

    // Constructor vacío requerido para Firestore
    public MediaList() {
    }

    // Constructor con parámetros
    public MediaList(String userId, String listName, String mediaType) {
        this.userId = userId;
        this.listName = listName;
        this.mediaType = mediaType;
        this.mediaIds = new ArrayList<>();
    }

    // Getters y setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
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

    public void setMediaIds(List<String> mediaIds) {
        this.mediaIds = mediaIds;
    }
}

