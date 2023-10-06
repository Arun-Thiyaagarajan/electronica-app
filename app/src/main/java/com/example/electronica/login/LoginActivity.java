package com.example.electronica.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.electronica.MainActivity;
import com.example.electronica.R;
import com.example.electronica.SharedPref;
import com.example.electronica.register.RegisterActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    Button loginBtn;
    Intent intent;
    TextInputLayout txtEmail, txtPassword;
    TextView newUser, skipLogin;
    EditText email, password;
    private FirebaseAuth auth;
    private Dialog progressDialog;
    public static LoginActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        instance = this;

        auth = FirebaseAuth.getInstance();

        progressDialog = new Dialog(this, R.style.ProgressDialogStyle);
        progressDialog.setContentView(R.layout.activity_progress_bar);
        progressDialog.setCancelable(false);

        loginBtn = findViewById(R.id.loginBtn);
        newUser = findViewById(R.id.newUser);
        txtEmail = findViewById(R.id.email);
        txtPassword = findViewById(R.id.password);
        skipLogin = findViewById(R.id.skipLogin);

        email = txtEmail.getEditText();
        password = txtPassword.getEditText();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString();
                String passcode = password.getText().toString();

                if (!Email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
                    if (!passcode.isEmpty()) {

                        progressDialog.show();
                        auth.signInWithEmailAndPassword(Email, passcode)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        progressDialog.dismiss();

                                        // Retrieve user's data from Firestore
                                        String userUid = authResult.getUser().getUid();
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        DocumentReference userRef = db.collection("users").document(userUid);

                                        userRef.get().addOnSuccessListener(documentSnapshot -> {
                                            if (documentSnapshot.exists()) {

                                                String userName = documentSnapshot.getString("name");
                                                String userEmail = documentSnapshot.getString("email");

                                                // Show a welcome message with the user's name
                                                Toast.makeText(LoginActivity.this, "Welcome back, " + userName + "!", Toast.LENGTH_SHORT).show();

                                                SharedPref.setName(LoginActivity.this,userName);
                                                SharedPref.setEmail(LoginActivity.this,userEmail);

                                                intent = new Intent(LoginActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                clearInput();
                                                finish();
                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        password.setError("Password can't be empty");
                    }
                } else if (Email.isEmpty()){
                    email.setError("Email can't be empty");
                } else {
                    Toast.makeText(LoginActivity.this, "Please Enter a valid Email", Toast.LENGTH_SHORT).show();
                    email.setError("Please Enter a valid Email");
                }
            }
        });

        skipLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                clearInput();
            }
        });

        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                clearInput();
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
//    }

    private void clearInput() {
        email.setText("");
        password.setText("");
    }
}