package es.uc3m.mobileApps.kritika.functionalities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import es.uc3m.mobileApps.kritika.Actions.NotificationHelper;
import es.uc3m.mobileApps.kritika.DashboardUserActivity;
import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.databinding.ActivityAddMediaBinding;

/**
 * Activity to add new media (movies, music, books) to the application.
 */
public class AddMediaActivity extends DashboardUserActivity {

    //view binding
    private ActivityAddMediaBinding binding;
    private Spinner spinnerMediaType;
    private LinearLayout layoutMovies, layoutMusic, layoutBooks;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // firebase auth
    private FirebaseAuth firebaseAuth;

    // progress dialog
    private ProgressDialog progressDialog;

    // media image
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddMediaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        // configure progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        spinnerMediaType = binding.spinnerMediaType;
        layoutMovies = binding.layoutMovies;
        layoutMusic = binding.layoutMusic;
        layoutBooks = binding.layoutBooks;

        // handle click, go back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // handle displayed form
        spinnerMediaType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateFormVisibility(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                layoutMovies.setVisibility(View.GONE);
                layoutMusic.setVisibility(View.GONE);
                layoutBooks.setVisibility(View.GONE);
            }
        });

        // handle click, upload movies
        binding.buttonImageMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageSelector();
            }
        });
        binding.submitMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateMovieData();
            }
        });

        // handle click, upload music
        binding.buttonImageMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageSelector();
            }
        });
        binding.submitMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateMusicData();
            }
        });

        // handle click, upload book
        binding.buttonImageBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageSelector();
            }
        });
        binding.submitBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateBookData();
            }
        });
    }

    /**
     * Handle image selection result.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            if (layoutMovies.getVisibility() == View.VISIBLE) {
                ImageView imageView = binding.imageMovieIv;
                imageView.setImageURI(imageUri);
            } else if (layoutMusic.getVisibility() == View.VISIBLE) {
                ImageView imageView = binding.imageMusicIv;
                imageView.setImageURI(imageUri);
            } else if (layoutBooks.getVisibility() == View.VISIBLE) {
                ImageView imageView = binding.imageBookIv;
                imageView.setImageURI(imageUri);
            }
        }
    }

    private Uri imageUri = null;

    /**
     * Update visibility of form elements based on selected media type.
     */
    private void updateFormVisibility(int typeIndex) {
        layoutMovies.setVisibility(View.GONE);
        layoutMusic.setVisibility(View.GONE);
        layoutBooks.setVisibility(View.GONE);

        switch (typeIndex) {
            case 0: // Movies
                layoutMovies.setVisibility(View.VISIBLE);
                break;
            case 1: // Music
                layoutMusic.setVisibility(View.VISIBLE);
                break;
            case 2: // Books
                layoutBooks.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * Open image selector for choosing media cover image.
     */
    private void openImageSelector() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * Get file extension from URI.
     */
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    /**
     * Clear form data after media addition.
     */
    private void clearFormData() {
        binding.TitleMovieEt.setText("");
        binding.OverviewMovieEt.setText("");
        binding.imageMovieIv.setImageResource(R.drawable.ic_upload_gray);
        binding.TitleMusicEt.setText("");
        binding.ArtistsMusicEt.setText("");
        binding.imageMusicIv.setImageResource(R.drawable.ic_upload_gray);
        binding.TitleBookEt.setText("");
        binding.AuthorsBookEt.setText("");
        binding.DescriptionBookEt.setText("");
        binding.CategoriesBookEt.setText("");
        binding.imageBookIv.setImageResource(R.drawable.ic_upload_gray);
        imageUri = null;
    }

    /**
     * Upload image to Firebase Storage.
     */
    private void uploadImage(Uri imageUri, String folderPath, Consumer<String> onSuccess, Consumer<String> onFailure) {
        if (imageUri != null) {
            progressDialog.setMessage("Uploading image...");
            progressDialog.show();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(folderPath);
            final StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    progressDialog.dismiss();
                    onSuccess.accept(uri.toString());
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    onFailure.accept(e.getMessage());
                });
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                onFailure.accept(e.getMessage());
            });
        }
    }

    /**
     * Add media document to Firestore.
     */
    private void addDocumentToFirestore(Map<String, Object> data, String collectionPath, Runnable onSuccess, Consumer<String> onFailure) {
        db.collection(collectionPath).add(data)
                .addOnSuccessListener(documentReference -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Data added successfully!", Toast.LENGTH_SHORT).show();
                    NotificationHelper.showNotification(this, "Add Media", "You have added a new media to Kritika!", NotificationHelper.CHANNEL_ID_ADD_MEDIA);
                    onSuccess.run();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    onFailure.accept(e.getMessage());
                });
    }

    /**
     * Validate and submit movie data.
     */
    private void validateMovieData() {
        String title = binding.TitleMovieEt.getText().toString().trim();
        String overview = binding.OverviewMovieEt.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Please enter a title...!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(overview)) {
            Toast.makeText(this, "Please enter an overview...!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image...!", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadImage(imageUri, "movie_covers",
                imageUrl -> {
                    Map<String, Object> movieData = new HashMap<>();
                    movieData.put("title", title);
                    movieData.put("overview", overview);
                    movieData.put("image", imageUrl);
                    movieData.put("timestamp", System.currentTimeMillis());
                    addDocumentToFirestore(movieData, "movies", this::clearFormData,
                            errorMessage -> Toast.makeText(this, "Failed to add movie: " + errorMessage, Toast.LENGTH_SHORT).show());
                },
                errorMessage -> Toast.makeText(this, "Failed to upload image: " + errorMessage, Toast.LENGTH_SHORT).show()
        );
    }

    /**
     * Validate and submit music data.
     */
    private void validateMusicData() {
        String title = binding.TitleMusicEt.getText().toString().trim();
        String artistsInput = binding.ArtistsMusicEt.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Please enter a title...!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(artistsInput)) {
            Toast.makeText(this, "Please enter at least one artist...!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image...!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Splitting the artists input by commas and trimming any extra whitespace
        List<String> artists = Arrays.stream(artistsInput.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (artists.isEmpty()) {
            Toast.makeText(this, "Please enter valid artist names!", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadImage(imageUri, "music_covers",
                imageUrl -> {
                    Map<String, Object> musicData = new HashMap<>();
                    musicData.put("title", title);
                    musicData.put("artists", artists); // Using a list to store multiple artists
                    musicData.put("image", imageUrl);
                    musicData.put("timestamp", System.currentTimeMillis());
                    addDocumentToFirestore(musicData, "songs", this::clearFormData,
                            errorMessage -> Toast.makeText(this, "Failed to add music: " + errorMessage, Toast.LENGTH_SHORT).show());
                },
                errorMessage -> Toast.makeText(this, "Failed to upload image: " + errorMessage, Toast.LENGTH_SHORT).show()
        );
    }

    /**
     * Validate and submit book data.
     */
    private void validateBookData() {
        String title = binding.TitleBookEt.getText().toString().trim();
        String authorsInput = binding.AuthorsBookEt.getText().toString().trim();
        String description = binding.DescriptionBookEt.getText().toString().trim();
        String categoriesInput = binding.CategoriesBookEt.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Please enter a title...!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(authorsInput)) {
            Toast.makeText(this, "Please enter at least one author...!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please enter a description...!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(categoriesInput)) {
            Toast.makeText(this, "Please enter at least one category...!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image...!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Processing authors and categories
        List<String> authors = Arrays.stream(authorsInput.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        List<String> categories = Arrays.stream(categoriesInput.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (authors.isEmpty()) {
            Toast.makeText(this, "Please enter valid author names!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (categories.isEmpty()) {
            Toast.makeText(this, "Please enter valid categories!", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadImage(imageUri, "book_covers",
                imageUrl -> {
                    Map<String, Object> bookData = new HashMap<>();
                    bookData.put("title", title);
                    bookData.put("authors", authors);
                    bookData.put("description", description);
                    bookData.put("categories", categories);
                    bookData.put("image", imageUrl);
                    bookData.put("timestamp", System.currentTimeMillis());
                    addDocumentToFirestore(bookData, "books", this::clearFormData,
                            errorMessage -> Toast.makeText(this, "Failed to add book: " + errorMessage, Toast.LENGTH_SHORT).show());
                },
                errorMessage -> Toast.makeText(this, "Failed to upload image: " + errorMessage, Toast.LENGTH_SHORT).show()
        );
    }
}