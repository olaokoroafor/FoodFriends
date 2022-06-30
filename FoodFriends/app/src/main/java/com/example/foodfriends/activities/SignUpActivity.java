package com.example.foodfriends.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.foodfriends.R;
import com.example.foodfriends.observable_models.UserObservable;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "Sign Up Activity";
    EditText etName;
    EditText etUsername;
    EditText etPassword;
    EditText etState;
    EditText etCity;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etName = findViewById(R.id.etSignUpName);
        etUsername = findViewById(R.id.etSignUpUsername);
        etPassword = findViewById(R.id.etSignUpPassword);
        etState = findViewById(R.id.etStateEntry);
        etCity = findViewById(R.id.etCityEntry);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.btnSignUp:
                signUp();
                break;
        }

    }

    private void signUp() {
        UserObservable user = new UserObservable();
        // Set the user's username and password, which can be obtained by a forms
        user.setUsername(etUsername.getText().toString());
        user.setPassword(etPassword.getText().toString());
        user.setState(etState.getText().toString());
        user.setCity(etCity.getText().toString());
        user.setName(etName.getText().toString());
        switch (user.isValid()){
            case -1:
                user.getUser().signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            showAlert("Successful Sign Up!", "Welcome " + user.getName() +"!");
                        } else {
                            ParseUser.logOut();
                            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case 0:
                Toast.makeText(SignUpActivity.this, "Username must not be empty", Toast.LENGTH_SHORT).show();
                break;

            case 1:
                Toast.makeText(SignUpActivity.this, "Password must not be empty", Toast.LENGTH_SHORT).show();
                break;

            case 2:
                Toast.makeText(SignUpActivity.this, "Password must be over 5 characters", Toast.LENGTH_SHORT).show();
                break;

            case 3:
                Toast.makeText(SignUpActivity.this, "Name must not be empty", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showAlert(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }


}