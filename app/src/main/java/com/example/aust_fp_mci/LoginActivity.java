package com.example.aust_fp_mci;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();

            if (username.equals("admin") && password.equals("admin")) {
                // Start MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Show error message
                Toast.makeText(LoginActivity.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}