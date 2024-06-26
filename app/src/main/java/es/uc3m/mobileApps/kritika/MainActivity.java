package es.uc3m.mobileApps.kritika;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import es.uc3m.mobileApps.kritika.databinding.ActivityMainBinding;
import es.uc3m.mobileApps.kritika.logs.LoginActivity;

public class MainActivity extends AppCompatActivity {

    // view binding
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // handle loginBtn click, start login screen
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    };
}