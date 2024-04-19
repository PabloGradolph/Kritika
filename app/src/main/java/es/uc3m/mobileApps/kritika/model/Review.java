package es.uc3m.mobileApps.kritika.model;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Review {
    private String user;
    private String reviewText;
    private boolean isPublic;
    private String mediaId;
    private String mediaType;

    // Constructor vacío requerido para Firestore
    public Review() {
    }

    // Constructor con parámetros
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

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
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

    // Método para guardar la review en Firestore
    public void saveReviewToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Obtener una referencia a la colección "reviews" en Firestore
        DocumentReference reviewRef = db.collection("reviews").document();

        // Guardar la review en Firestore
        reviewRef.set(this)
                .addOnSuccessListener(aVoid -> {
                    // Éxito al guardar la review
                })
                .addOnFailureListener(e -> {
                    // Error al guardar la review
                });
    }
}
