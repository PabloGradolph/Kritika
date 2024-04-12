package es.uc3m.mobileApps.kritika.logs;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import es.uc3m.mobileApps.kritika.DashboardUserActivity;
import es.uc3m.mobileApps.kritika.MainActivity;
import es.uc3m.mobileApps.kritika.R;
import es.uc3m.mobileApps.kritika.newDashboard.newDashboardUserActivity;

public class SplashActivity extends AppCompatActivity {

    // firebase auth
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        // start main screen after 0'5 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUser();
            }
        }, 500); //500 means 0'5 seconds
    }

    private void checkUser() {
        // get current user, if logged in
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(firebaseUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String userType = documentSnapshot.getString("userType");
                        if ("user".equals(userType)) {
                            startActivity(new Intent(SplashActivity.this, newDashboardUserActivity.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SplashActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    });
        } else {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }
}