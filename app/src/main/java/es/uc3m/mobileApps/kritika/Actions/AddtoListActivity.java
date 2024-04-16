package es.uc3m.mobileApps.kritika.Actions;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import es.uc3m.mobileApps.kritika.R;

public class AddtoListActivity extends AppCompatActivity {

    private Spinner listSpinner;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addto_list);

        listSpinner = findViewById(R.id.spinnerLists);
        addButton = findViewById(R.id.btnAddToList);

        // Configura el Spinner con un ArrayAdapter si es necesario
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.list_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listSpinner.setAdapter(adapter);

        addButton.setOnClickListener(v -> {
            // Lógica para añadir a la lista seleccionada
            String selectedList = listSpinner.getSelectedItem().toString();
            // Aquí, ejecutar la lógica para añadir el elemento a la lista
            Toast.makeText(this, "Added to " + selectedList, Toast.LENGTH_SHORT).show();
            finish(); // Opcional, cierra la actividad después de añadir
        });
    }
}