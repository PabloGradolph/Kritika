package es.uc3m.mobileApps.kritika.Actions;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.MediaList;

/**
 * Activity to add media to a user's list.
 */
public class AddtoListActivity extends AppCompatActivity {

    private Spinner listSpinner;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addto_list);

        // Initialize views
        listSpinner = findViewById(R.id.spinnerLists);
        addButton = findViewById(R.id.btnAddToList);

        // Get media type and id from intent
        String mediaType = getIntent().getStringExtra("mediaType");
        String mediaId = getIntent().getStringExtra("mediaId");

        // Check if the default list already exists before loading the lists
        String defaultListName = getDefaultListName(mediaType);
        checkAndCreateDefaultList(defaultListName, mediaType, () -> {
            // Default list created or already exists, load lists
            loadLists(mediaType);
        });

        // Add button click listener
        addButton.setOnClickListener(v -> {
            String selectedList = listSpinner.getSelectedItem().toString();

            if (selectedList.equals("Create New List")) {
                // Show dialog to create new list
                showCreateListDialog(mediaType, mediaId);
            } else {
                // User selected an existing list
                String selectedListName = listSpinner.getSelectedItem().toString();
                addToExistingList(selectedListName, mediaType, mediaId);
            }
        });
    }

    /**
     * Get the default list name based on media type.
     *
     * @param mediaType Type of media
     * @return Default list name
     */
    private String getDefaultListName(String mediaType) {
        // Determine default list name based on media type
        switch (mediaType) {
            case "movies":
                return "Movies Watched";
            case "songs":
                return "Music Listened";
            case "books":
                return "Books Read";
            default:
                return null;
        }
    }

    /**
     * Check if the default list exists and create if necessary.
     *
     * @param listName  Name of the default list
     * @param mediaType Type of media
     * @param listener  Callback listener
     */
    private void checkAndCreateDefaultList(String listName, String mediaType, OnCompleteListener listener) {
        if (listName != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("lists")
                    .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .whereEqualTo("mediaType", mediaType)
                    .whereEqualTo("listName", listName)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().isEmpty()) {
                            // Default list does not exist, create it
                            createDefaultList(listName, mediaType, listener);
                        }  else {
                            // Default list already exists, execute listener
                            listener.onComplete();
                        }
                    });
        }
    }

    /**
     * Create the default list.
     *
     * @param listName  Name of the default list
     * @param mediaType Type of media
     * @param listener  Callback listener
     */
    private void createDefaultList(String listName, String mediaType, OnCompleteListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        MediaList newList = new MediaList(FirebaseAuth.getInstance().getCurrentUser().getUid(), listName, mediaType);
        db.collection("lists").add(newList)
                .addOnSuccessListener(documentReference -> {
                    // Default list created successfully
                    listener.onComplete();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to create default list", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Load user's lists.
     *
     * @param mediaType Type of media
     */
    private void loadLists(String mediaType) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("lists")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereEqualTo("mediaType", mediaType)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get list name and add to adapter
                            String listName = document.getString("listName");
                            adapter.add(listName);
                        }
                    } else {
                        Toast.makeText(this, "Error checking lists.", Toast.LENGTH_SHORT).show();
                    }

                    // Add option to create new list
                    adapter.add("Create New List");

                    // Set updated adapter to spinner
                    listSpinner.setAdapter(adapter);
                });
    }

    /**
     * Show dialog to create a new list.
     *
     * @param mediaType Type of media
     * @param mediaId   ID of the media
     */
    private void showCreateListDialog(String mediaType, String mediaId) {
        // Build dialog box with a text input field
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New List");

        // Create an EditText for the user to input the name of the new list
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Configuring dialog box buttons
        builder.setPositiveButton("Create", (dialog, which) -> {
            String listName = input.getText().toString().trim();
            if (!listName.isEmpty()) {
                // Create new list with entered name
                createNewList(listName, mediaType, mediaId);
            } else {
                Toast.makeText(this, "Please enter a list name", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
     * Create a new list.
     *
     * @param listName  Name of the list
     * @param mediaType Type of media
     * @param mediaId   ID of the media
     */
    private void createNewList(String listName, String mediaType, String mediaId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Check if the list name already exists for the user and media type
        db.collection("lists")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereEqualTo("mediaType", mediaType)
                .whereEqualTo("listName", listName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            // No list with the same name exists, create new list
                            MediaList newList = new MediaList(FirebaseAuth.getInstance().getCurrentUser().getUid(), listName, mediaType);
                            newList.getMediaIds().add(mediaId);

                            db.collection("lists").add(newList)
                                    .addOnSuccessListener(documentReference -> {
                                        // List created successfully
                                        Toast.makeText(this, "Added to " + listName, Toast.LENGTH_SHORT).show();
                                        NotificationHelper.showNotification(this, "Add to List", "A media has been added to your list!", NotificationHelper.CHANNEL_ID_ADD_TO_LIST);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to create list", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // List with the same name already exists
                            Toast.makeText(this, "List with the same name already exists", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to check existing lists", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Add media to an existing list.
     *
     * @param listName  Name of the list
     * @param mediaType Type of media
     * @param mediaId   ID of the media
     */
    private void addToExistingList(String listName, String mediaType, String mediaId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("lists")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereEqualTo("mediaType", mediaType)
                .whereEqualTo("listName", listName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            String listDocumentId = task.getResult().getDocuments().get(0).getId();

                            db.collection("lists").document(listDocumentId)
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        MediaList list = documentSnapshot.toObject(MediaList.class);
                                        if (list != null && list.getMediaIds().contains(mediaId)) {
                                            // Media already exists in the list
                                            Toast.makeText(this, "Media already exists in the list", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Update the list to add the new media
                                            db.collection("lists").document(listDocumentId)
                                                    .update("mediaIds", FieldValue.arrayUnion(mediaId))
                                                    .addOnSuccessListener(aVoid -> {
                                                        // Media added successfully to the list
                                                        Toast.makeText(this, "Added to " + listName, Toast.LENGTH_SHORT).show();
                                                        NotificationHelper.showNotification(this, "Add to List", "A media has been added to your list.", NotificationHelper.CHANNEL_ID_ADD_TO_LIST);
                                                        finish();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(this, "Failed to add to list", Toast.LENGTH_SHORT).show();
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to retrieve list", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(this, "Selected list not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to retrieve selected list", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Callback interface for completion of tasks.
     */
    private interface OnCompleteListener {
        void onComplete();
    }

}