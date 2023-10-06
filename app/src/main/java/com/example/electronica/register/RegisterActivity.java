package com.example.electronica.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.electronica.AuthenticationModel;
import com.example.electronica.MainActivity;
import com.example.electronica.R;
import com.example.electronica.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    EditText email, username, password, name, confirmPassword;
    Button registerBtn;
    TextInputLayout txtEmail, txtUsername, txtPassword, txtConfirmPassword, txtName;
    TextView alreadyMember, skipLogin;
    private FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;
    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        progressDialog = new Dialog(this, R.style.ProgressDialogStyle);
        progressDialog.setContentView(R.layout.activity_progress_bar);
        progressDialog.setCancelable(false);

        registerBtn = findViewById(R.id.registerBtn);
        alreadyMember = findViewById(R.id.alreadyMember);
        txtEmail = findViewById(R.id.email);
        txtUsername = findViewById(R.id.username);
        txtName = findViewById(R.id.name);
        txtPassword = findViewById(R.id.password);
        txtConfirmPassword = findViewById(R.id.confirmPassword);
        skipLogin = findViewById(R.id.skipLogin);

        email = txtEmail.getEditText();
        username = txtUsername.getEditText();
        name = txtName.getEditText();
        password = txtPassword.getEditText();
        confirmPassword = txtConfirmPassword.getEditText();

        skipLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                clearInput();
            }
        });

        alreadyMember.setOnClickListener(view -> {
            onBackPressed();
            clearInput();
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userName = username.getText().toString().trim();
                String Name = name.getText().toString().trim();
                String Email = email.getText().toString().trim();
                String passcode = password.getText().toString().trim();
                String confirmPasscode = confirmPassword.getText().toString().trim();

                if (userName.isEmpty()) {
                    username.setError("Username can't be empty");
                }
                if (Name.isEmpty()) {
                    name.setError("Name can't be empty");
                }
                if (passcode.isEmpty()) {
                    password.setError("Password can't be empty");
                }
                if (Email.isEmpty()) {
                    email.setError("Email can't be empty");
                }
                if (confirmPasscode.isEmpty()) {
                    confirmPassword.setError("Confirm Password can't be empty");
                }
                else if(confirmPasscode.equals(passcode)) {
                    progressDialog.show();
                    auth.createUserWithEmailAndPassword(Email, passcode).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

                                firebaseFirestore.collection("users")
                                        .document(FirebaseAuth.getInstance().getUid())
                                        .set(new AuthenticationModel(Name, userName, Email, passcode));

                                clearInput();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Registration Failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    confirmPassword.setError("Password doesn't matches");
                }
            }
        });
    }

    private void clearInput() {
        username.setText("");
        password.setText("");
        name.setText("");
        email.setText("");
        confirmPassword.setText("");
    }
}