package com.example.mycoffeeshop.Pages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycoffeeshop.OthersPackage.EmailPasswordPhoneValidator;
import com.example.mycoffeeshop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText email_id, passwordCheck;
    private Button btnRegister;
    private TextView login_hyperlink;
    private static final String TAG = "RegisterActivity-Check";
    private String email, password, userId;
    private FirebaseAuth mAuth;

    // 2 lines, added by me, 17062020 :
    private ProgressBar mProgressBar;
    private FirebaseFirestore fireStoreDB = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initUI();
        initListeners();
    }

    private void initUI() {
        email_id = findViewById(R.id.registerEmailTextInputEditText);
        passwordCheck = findViewById(R.id.registerPasswordTextInputEditText);
        btnRegister = findViewById(R.id.btnRegister);
        login_hyperlink = findViewById(R.id.login_text_button);
        mProgressBar = findViewById(R.id.progressBar);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
    }

    private void initListeners() {
        btnRegister.setOnClickListener(this);
        login_hyperlink.setOnClickListener(this);
    }

    private void register() {
        email = email_id.getText().toString().trim();
        password = passwordCheck.getText().toString().trim();
        mProgressBar.setVisibility(View.VISIBLE);

        checkFields();

        //checkEmailPasswordValidity();
        if (EmailPasswordPhoneValidator.getInstance().isValidPassword(email) && EmailPasswordPhoneValidator.getInstance().isValidPassword(password)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(RegisterActivity.this, "Regisration successfull!", Toast.LENGTH_LONG).show();
                            //holding 'userId' in order to save user email to DB
                            userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                            saveUserToDB(userId, email);
                            mProgressBar.setVisibility(View.GONE);
                            Intent intent1 = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent1.putExtra("regCheck", "2");
                            startActivity(intent1);
                            finish();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Registration failed!", Toast.LENGTH_LONG).show();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
        } else if (!EmailPasswordPhoneValidator.getInstance().isValidEmail(email)) {
            Toast.makeText(this, "Email address is invalid", Toast.LENGTH_SHORT).show();

            mProgressBar.setVisibility(View.GONE);
        } else if (!EmailPasswordPhoneValidator.getInstance().isValidPassword(password)) {
            Toast.makeText(this, "The password must be 8 characters", Toast.LENGTH_SHORT).show();

            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void checkFields() {
        if (email_id.getText().toString().isEmpty()) {
            email_id.setError("Required field");
            email_id.requestFocus();
        }

        if (passwordCheck.getText().toString().isEmpty()) {
            passwordCheck.setError("Required field");
            passwordCheck.requestFocus();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister:
                register();
                break;
            case R.id.login_text_button:
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void saveUserToDB(String uid, String email) {
        Toast.makeText(RegisterActivity.this, "uid is: " + uid, Toast.LENGTH_LONG).show();
        DocumentReference docRef = fireStoreDB.collection("users").document(uid);
        Map<String, Object> user = new HashMap<>();
        //setting email address
        user.put("email", email);
        //setting all other user detail values to "0" /  empty string. -->
        // (user will be able to edit his othher profile details in the "userProfileFragment"
        user.put("fullName", "");
        user.put("dateOfBirth", "");
        user.put("phonenNumber", "");
        user.put("streetName", "");
        user.put("streetNumber", "");
        user.put("city", "");
        user.put("zipCode", "");

        docRef.set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "profile card was successfully created to user: " + email);
                    Toast.makeText(RegisterActivity.this, "user registration was created successfully. \n" + "User Email: " + email, Toast.LENGTH_LONG).show();

                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error created profile card for user: " + email, e);
                    Toast.makeText(RegisterActivity.this, "user creation failed.", Toast.LENGTH_LONG).show();
                });
    }

}
