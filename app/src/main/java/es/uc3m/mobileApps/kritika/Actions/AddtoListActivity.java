package es.uc3m.mobileApps.kritika.Actions;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.model.MediaList;

public class AddtoListActivity extends AppCompatActivity {

    private Spinner listSpinner;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addto_list);

        listSpinner = findViewById(R.id.spinnerLists);
        addButton = findViewById(R.id.btnAddToList);

        String mediaType = getIntent().getStringExtra("mediaType");
        String mediaId = getIntent().getStringExtra("mediaId");

        // Verificar si la lista predeterminada ya existe antes de cargar las listas
        String defaultListName = getDefaultListName(mediaType);
        checkAndCreateDefaultList(defaultListName, mediaType, () -> {
            // La lista predeterminada se creó o ya existe, cargar las listas
            loadLists(mediaType);
        });

        addButton.setOnClickListener(v -> {
            String selectedList = listSpinner.getSelectedItem().toString();

            if (selectedList.equals("Create New List")) {
                showCreateListDialog(mediaType, mediaId);
            } else {
                // El usuario seleccionó una lista existente
                String selectedListName = listSpinner.getSelectedItem().toString();
                addToExistingList(selectedListName, mediaType, mediaId);
            }
        });
    }

    private String getDefaultListName(String mediaType) {
        // Determinar el nombre predeterminado de la lista según el tipo de medio
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
                            // La lista predeterminada no existe, créala
                            createDefaultList(listName, mediaType, listener);
                        }  else {
                            // La lista predeterminada ya existe, ejecutar el listener
                            listener.onComplete();
                        }
                    });
        }
    }

    private void createDefaultList(String listName, String mediaType, OnCompleteListener listener) {
        // Crea la lista predeterminada en la base de datos
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        MediaList newList = new MediaList(FirebaseAuth.getInstance().getCurrentUser().getUid(), listName, mediaType);
        db.collection("lists").add(newList)
                .addOnSuccessListener(documentReference -> {
                    // La lista predeterminada se creó con éxito
                    listener.onComplete();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to create default list", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadLists(String mediaType) {
        // Limpiar el ArrayAdapter antes de cargar nuevas listas
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
                            // Obtener el nombre de la lista y agregarlo al ArrayAdapter
                            String listName = document.getString("listName");
                            adapter.add(listName);
                        }
                    } else {
                        Toast.makeText(this, "Error checking lists.", Toast.LENGTH_SHORT).show();
                    }

                    // Agregar una opción para crear una nueva lista
                    adapter.add("Create New List");

                    // Establecer el ArrayAdapter actualizado en el Spinner
                    listSpinner.setAdapter(adapter);
                });
    }

    private void showCreateListDialog(String mediaType, String mediaId) {
        // Construir el cuadro de diálogo con un campo de entrada de texto
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New List");

        // Crear un EditText para que el usuario ingrese el nombre de la nueva lista
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Configurar los botones del cuadro de diálogo
        builder.setPositiveButton("Create", (dialog, which) -> {
            String listName = input.getText().toString().trim();
            if (!listName.isEmpty()) {
                // Crear la nueva lista con el nombre ingresado
                createNewList(listName, mediaType, mediaId);
            } else {
                // Mostrar un mensaje si el nombre de la lista está vacío
                Toast.makeText(this, "Please enter a list name", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Mostrar el cuadro de diálogo
        builder.show();
    }

    private void createNewList(String listName, String mediaType, String mediaId) {
        // Obtener una instancia de FirebaseFirestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Verificar si el nombre de la lista ya existe para el usuario y el tipo de medio
        db.collection("lists")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereEqualTo("mediaType", mediaType)
                .whereEqualTo("listName", listName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Verificar si hay documentos coincidentes
                        if (task.getResult().isEmpty()) {
                            // No hay listas con el mismo nombre, crear una nueva lista
                            MediaList newList = new MediaList(FirebaseAuth.getInstance().getCurrentUser().getUid(), listName, mediaType);
                            newList.getMediaIds().add(mediaId);

                            // Guardar la nueva lista en la colección "lists" de Firestore
                            db.collection("lists").add(newList)
                                    .addOnSuccessListener(documentReference -> {
                                        // La lista se creó con éxito
                                        Toast.makeText(this, "Added to " + listName, Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Error al crear la lista
                                        Toast.makeText(this, "Failed to create list", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Ya existe una lista con el mismo nombre
                            Toast.makeText(this, "List with the same name already exists", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Error al verificar las listas existentes
                        Toast.makeText(this, "Failed to check existing lists", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addToExistingList(String listName, String mediaType, String mediaId) {
        // Obtener una instancia de FirebaseFirestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Consultar la lista seleccionada
        db.collection("lists")
                .whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereEqualTo("mediaType", mediaType)
                .whereEqualTo("listName", listName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Verificar si se encontró la lista
                        if (!task.getResult().isEmpty()) {
                            // Obtener la referencia del documento de la lista
                            String listDocumentId = task.getResult().getDocuments().get(0).getId();

                            // Verificar si el medio ya está en la lista
                            db.collection("lists").document(listDocumentId)
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        MediaList list = documentSnapshot.toObject(MediaList.class);
                                        if (list != null && list.getMediaIds().contains(mediaId)) {
                                            // El medio ya está en la lista, mostrar un mensaje
                                            Toast.makeText(this, "Media already exists in the list", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Actualizar la lista para agregar el nuevo medio
                                            db.collection("lists").document(listDocumentId)
                                                    .update("mediaIds", FieldValue.arrayUnion(mediaId))
                                                    .addOnSuccessListener(aVoid -> {
                                                        // El medio se agregó con éxito a la lista
                                                        Toast.makeText(this, "Added to " + listName, Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        // Error al agregar el medio a la lista
                                                        Toast.makeText(this, "Failed to add to list", Toast.LENGTH_SHORT).show();
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        // Error al obtener la lista
                                        Toast.makeText(this, "Failed to retrieve list", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // No se encontró la lista seleccionada
                            Toast.makeText(this, "Selected list not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Error al buscar la lista seleccionada
                        Toast.makeText(this, "Failed to retrieve selected list", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private interface OnCompleteListener {
        void onComplete();
    }

}