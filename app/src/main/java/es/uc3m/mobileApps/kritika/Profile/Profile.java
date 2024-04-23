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
import es.uc3m.mobileApps.kritika.databinding.ProfileBinding;

public class Profile extends DashboardUserActivity {

    private FirebaseAuth firebaseAuth;

    // Constante para identificar la solicitud de selección de imagen
    private static final int PICK_IMAGE_REQUEST = 1;

    // Referencia al almacenamiento de Firebase
    private StorageReference storageRef;

    // Uri de la imagen seleccionada
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        // Inicializar la referencia al almacenamiento de Firebase
        storageRef = FirebaseStorage.getInstance().getReference();

        ImageButton buttonLogOut = findViewById(R.id.logoutBtn);
        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

        // Obtener referencia al ImageView de la foto de perfil
        ImageView profileThumbnail = findViewById(R.id.profileThumbnail);
        // Agregar OnClickListener al ImageView de la foto de perfil
        profileThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar una actividad para seleccionar una imagen de la galería
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"), PICK_IMAGE_REQUEST);
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
                buttonOpenHome, buttonOpenSearch);// You can add any specific initialization code for the Profile activity here

        // Agregamos los fragmentos
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
            // Si el nombre de usuario no está configurado en Firebase Auth, intenta obtenerlo de la base de datos
            if (userName == null || userName.isEmpty()) {
                // Obtiene la referencia del documento del usuario en Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference userRef = db.collection("users").document(firebaseUser.getUid());

                userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Obtiene el nombre de usuario del documento de usuario en Firestore
                            String databaseUserName = documentSnapshot.getString("name");
                            // Establece el nombre de usuario en el TextView de la barra de herramientas
                            TextView subTitleTv = findViewById(R.id.userName);
                            subTitleTv.setText(databaseUserName);

                            // Obtener la URL de la imagen de perfil del usuario
                            String profileImageUrl = documentSnapshot.getString("profileImage");
                            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                                // Si la URL de la imagen está disponible, cargar la imagen desde Firebase Storage
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
                // Establece el nombre de usuario en el TextView de la barra de herramientas
                TextView subTitleTv = findViewById(R.id.userName);
                subTitleTv.setText(userName);
            }

            String email = firebaseUser.getEmail();
            TextView subTitleTv = findViewById(R.id.subTitleTv);
            subTitleTv.setText(email);
        }
    }

    // Método para manejar el resultado de la selección de imagen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Obtener la Uri de la imagen seleccionada
            imageUri = data.getData();

            // Subir la imagen seleccionada a Firebase Storage
            uploadImageToStorage();
        }
    }

    // Método para subir la imagen seleccionada a Firebase Storage
    private void uploadImageToStorage() {
        if (imageUri != null) {
            // Crear una referencia al archivo en Firebase Storage con un nombre único
            StorageReference profileImageRef = storageRef.child("users_pictures/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

            // Subir la imagen a Firebase Storage
            profileImageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Obtener la URL de la imagen almacenada en Firebase Storage
                            profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Actualizar la URL de la imagen de perfil del usuario en Firestore con la nueva URL
                                    updateProfileImageUri(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Manejar el fallo de la subida de la imagen
                            Log.e("Profile", "Error al subir la imagen", e);
                        }
                    });
        }
    }

    // Método para actualizar la URL de la imagen de perfil del usuario en Firestore
    private void updateProfileImageUri(String imageUrl) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(user.getUid());

            // Actualizar la URL de la imagen de perfil del usuario en Firestore
            userRef.update("profileImage", imageUrl)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Actualizar la imagen de perfil en la actividad de perfil
                            ImageView profileThumbnail = findViewById(R.id.profileThumbnail);
                            profileThumbnail.setImageURI(imageUri);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Manejar el fallo de la actualización de la URL de la imagen de perfil en Firestore
                            Log.e("Profile", "Error al actualizar la URL de la imagen de perfil", e);
                        }
                    });
        }
    }
}
