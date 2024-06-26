package es.uc3m.mobileApps.kritika.logs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import es.uc3m.mobileApps.kritika.databinding.ActivityRegisterBinding;
import es.uc3m.mobileApps.kritika.newDashboard.newDashboardUserActivity;

public class RegisterActivity extends AppCompatActivity {


    // view binding
    private ActivityRegisterBinding binding;

    // firebase auth
    private FirebaseAuth firebaseAuth;

    // progress dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        // setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        // handle click, go back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // handle click, begin register
        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private String name = "", email = "", password = "";

    private void validateData() {
        /* Before creating account, lets do some data validation */

        // get data
        name = binding.nameEt.getText().toString().trim();
        email = binding.emailEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString().trim();
        String cPassword = binding.cPasswordEt.getText().toString().trim();

        // validate data
        if (TextUtils.isEmpty(name)) {
            // name edit text is empyt, must enter name
            Toast.makeText(this, "Enter your name...", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            // email is either not entered or email pattern is invalid, don't allow to continue in that case
            Toast.makeText(this, "Invalid email pattern...!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)){
            // password edit text is empty, must enter password
            Toast.makeText(this, "Enter password...!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(cPassword)){
            // confirm password edit text is empty, must enter confirm password
            Toast.makeText(this, "Confirm Password...!", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(cPassword)){
            // password and confirm password doesn't match, don't allow to continue in that case, both password must match
            Toast.makeText(this, "Password doesn't match...!", Toast.LENGTH_SHORT).show();
        } else {
            // all data is validated, begin creating account
            createUserAccount();
        }
    }

    private void createUserAccount() {
        // show progress
        progressDialog.setMessage("Creating account...");
        progressDialog.show();

        // create user in firebase auth
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // account creation success, now add in firebase realtime database
                        updateUserInfo();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // acount creation failed
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserInfo(){
        progressDialog.setMessage("Saving user info...");

        // timestamp
        long timestamp = System.currentTimeMillis();

        // get current user uid, since user is registered so we can get now
        String uid = firebaseAuth.getUid();

        // setup data to add in db
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("email", email);
        hashMap.put("name", name);
        hashMap.put("profileImage", "https://firebasestorage.googleapis.com/v0/b/uc3m-it-2024-16504-g08.appspot.com/o/users_pictures%2Fimages.png?alt=media&token=4b912db9-bb61-4746-bfcf-b6f912df0516");
        hashMap.put("userType", "user"); // possible values are [user, admin]. Will make admin manually in firebase realtime database by changing this value
        hashMap.put("timestamp", timestamp);

        // set data to db
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(uid)
                .set(hashMap)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Account created...", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, newDashboardUserActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}