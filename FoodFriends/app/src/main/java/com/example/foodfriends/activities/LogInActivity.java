package com.example.foodfriends.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodfriends.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        etUsername = findViewById(R.id.etLogInUsername);
        etPassword = findViewById(R.id.etLogInPassword);
        btnLogin = findViewById(R.id.btnLogIn);
        tvSignUp = findViewById(R.id.tvSignUpLink);
        tvSignUp.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

    }

    /**
     * Provides different functionality depending on view clicked
     * Either taking user to sign up page or logging the user in**/
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.tvSignUpLink:
                toSignUp();
                break;

            case R.id.btnLogIn:
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password);
                break;
        }
    }

    /**
     * Fires intent to to take user to Sign Up Activity **/
    private void toSignUp() {
        Intent i = new Intent(LogInActivity.this, SignUpActivity.class);
        startActivity(i);
    }

    /**
     * Users ParseUser object method to log in current user
     * Takes user to main activity if log in attempt is successful
     * **/
    private void loginUser(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Toast.makeText(LogInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Intent i = new Intent(LogInActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            }
        });
    }


}