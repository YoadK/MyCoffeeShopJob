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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText inputEmail, inputPassword;
    private Button btnLogin;
    private TextView register_hyperlink;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "Check";
    private String email, password, sessionId;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUI();
        initListeners();
    }

    private void initUI() {
        inputEmail = findViewById(R.id.loginEmailTextInputEditText);
        inputPassword = findViewById(R.id.loginPasswordTextInputEditText);
        btnLogin = findViewById(R.id.btnLogin);
        register_hyperlink = findViewById(R.id.register_text_button);
        mProgressBar = findViewById(R.id.progressBar);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        sessionId = getIntent().getStringExtra("regCheck");

        if (sessionId == null) {
            mAuthListener = firebaseAuth -> {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            };
        }
    }

    private void initListeners() {
        btnLogin.setOnClickListener(this);
        register_hyperlink.setOnClickListener(this);
    }

    private void login() {
        email = inputEmail.getText().toString().trim();
        password = inputPassword.getText().toString().trim();
        mProgressBar.setVisibility(View.VISIBLE);

        checkFields();

        if (EmailPasswordPhoneValidator.getInstance().isValidEmail(email) && EmailPasswordPhoneValidator.getInstance().isValidPassword(password)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            mProgressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.d(TAG, "singInWithEmail:Fail");
                            Toast.makeText(LoginActivity.this, "The login failed", Toast.LENGTH_LONG).show();
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
        if (inputEmail.getText().toString().isEmpty()) {
            inputEmail.setError("Required field");
            inputEmail.requestFocus();
        }

        if (inputPassword.getText().toString().isEmpty()) {
            inputPassword.setError("Required field");
            inputPassword.requestFocus();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                login();
                break;
            case R.id.register_text_button:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (sessionId == null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

}
