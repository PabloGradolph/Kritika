package es.uc3m.mobileApps.kritika.Profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import es.uc3m.mobileApps.kritika.DashboardUserActivity;
import es.uc3m.mobileApps.kritika.MainActivity;
import es.uc3m.mobileApps.kritika.R;

/**
 * Activity for user profile management.
 */
public class Profile extends DashboardUserActivity {

    private FirebaseAuth firebaseAuth;

    // Constant to identify image selection request
    private static final int PICK_IMAGE_REQUEST = 1;

    // Firebase storage reference
    private StorageReference storageRef;

    // Selected image Uri
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        // Initialize Firebase storage reference
        storageRef = FirebaseStorage.getInstance().getReference();

        ImageButton buttonLogOut = findViewById(R.id.logoutBtn);
        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

        // Get reference to profile picture ImageView
        ImageView profileThumbnail = findViewById(R.id.profileThumbnail);
        // Add OnClickListener to profile picture ImageView
        profileThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start an activity to select an image from gallery
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE_REQUEST);
            }
        });

        // TOP BAR: buttons
        Button buttonOpenMovies = findViewById(R.id.button_open_movies);
        Button buttonOpenMusic = findViewById(R.id.button_open_music);
        Button buttonOpenBooks = findViewById(R.id.button_open_books);
        Button buttonOpenReviews = findViewById(R.id.button_open_reviews);

        // BOTTOM BAR: buttons
        ImageButton buttonOpenProfile = findViewById(R.id.profileButton);
        ImageButton buttonOpenHome = findViewById(R.id.houseButton);
        ImageButton buttonOpenSearch = findViewById(R.id.searchButton);

        // Set click listeners for buttons
        setButtonListeners(buttonOpenMovies, buttonOpenMusic, buttonOpenBooks, buttonOpenReviews, buttonOpenProfile,
                buttonOpenHome, buttonOpenSearch);

        // Add fragments
        RatingsFragment ratingsFragment = new RatingsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.ratingsMediaFragmentContainer, ratingsFragment)
                .commit();

        ReviewsFragment reviewsFragment = new ReviewsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.reviewsMediaFragmentContainer, reviewsFragment)
                .commit();

        ListsFragment listsFragment = new ListsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.listsMediaFragmentContainer, listsFragment)
                .commit();
    }


    @Override
    public void openProfile() {
        // This is the Profile activity, so no need to navigate to itself
        // You can handle this method differently if needed
    }

    /**
     * Check if user is logged in. If not, redirect to main screen.
     */
    private void checkUser() {
        // get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null){
            // not logged in, go to main screen
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            // logged in, get user info
            String userName = firebaseUser.getDisplayName();
            // If username is not set in Firebase Auth, try to fetch it from database
            if (userName == null || userName.isEmpty()) {
                // Get reference to user document in Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference userRef = db.collection("users").document(firebaseUser.getUid());

                userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Get username from user document in Firestore
                            String databaseUserName = documentSnapshot.getString("name");
                            // Set username in toolbar TextView
                            TextView subTitleTv = findViewById(R.id.userName);
                            subTitleTv.setText(databaseUserName);

                            // Get user profile image URL
                            String profileImageUrl = documentSnapshot.getString("profileImage");
                            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                                // If image URL is available, load the image from Firebase Storage
                                ImageView profileThumbnail = findViewById(R.id.profileThumbnail);
                                Glide.with(Profile.this).load(profileImageUrl).into(profileThumbnail);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Exception: ", e.toString());
                    }
                });
            } else {
                // Set username in toolbar TextView
                TextView subTitleTv = findViewById(R.id.userName);
                subTitleTv.setText(userName);
            }

            String email = firebaseUser.getEmail();
            TextView subTitleTv = findViewById(R.id.subTitleTv);
            subTitleTv.setText(email);
        }
    }

    // Method to handle result of image selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get Uri of the selected image
            imageUri = data.getData();

            // Upload the selected image to Firebase Storage
            uploadImageToStorage();
        }
    }

    // Method to upload selected image to Firebase Storage
    private void uploadImageToStorage() {
        if (imageUri != null) {
            // Create reference to file in Firebase Storage with a unique name
            StorageReference profileImageRef = storageRef.child("users_pictures/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

            // Upload the image to Firebase Storage
            profileImageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get the URL of the image stored in Firebase Storage
                            profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Update user profile image URL in Firestore with the new URL
                                    updateProfileImageUri(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Profile", "Error al subir la imagen", e);
                        }
                    });
        }
    }

    // Method to update user profile image URL in Firestore
    private void updateProfileImageUri(String imageUrl) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(user.getUid());

            // Update user profile image URL in Firestore
            userRef.update("profileImage", imageUrl)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Update profile image in profile activity
                            ImageView profileThumbnail = findViewById(R.id.profileThumbnail);
                            profileThumbnail.setImageURI(imageUri);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Profile", "Error al actualizar la URL de la imagen de perfil", e);
                        }
                    });
        }
    }
}
