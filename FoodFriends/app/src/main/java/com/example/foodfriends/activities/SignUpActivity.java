package com.example.foodfriends.activities;

import static java.util.Map.entry;

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

import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Sign Up Activity";
    private final int EMPTY_USER_NAME_RESULT_CODE = 0;
    private final int EMPTY_PASSWORD_RESULT_CODE = 1;
    private final int SHORT_PASSWORD_RESULT_CODE = 2;
    private final int EMPTY_NAME_RESULT_CODE = 3;
    private final int OK_RESULT_CODE = -1;
    private final Map<Integer, String> userValidityStates = Map.ofEntries(entry(EMPTY_USER_NAME_RESULT_CODE, "Username must not be empty"), entry(EMPTY_NAME_RESULT_CODE, "Name must not be empty"), entry(EMPTY_PASSWORD_RESULT_CODE, "Password must not be empty"), entry(SHORT_PASSWORD_RESULT_CODE, "Password must be over 5 characters"));
    private EditText etName;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etState;
    private EditText etCity;
    private Button btnSignUp;

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
        switch (v.getId()) {

            case R.id.btnSignUp:
                signUp();
                break;
        }

    }

    /**
     * Creates user object for the current user, with specified attributes
     **/
    private void signUp() {
        UserObservable user = new UserObservable();
        // Set the user's username and password, which can be obtained by a forms
        user.setUsername(etUsername.getText().toString());
        user.setPassword(etPassword.getText().toString());
        user.setState(etState.getText().toString());
        user.setCity(etCity.getText().toString());
        user.setName(etName.getText().toString());
        if (user.isValid() == OK_RESULT_CODE) {
            user.getUser().signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        showAlert("Successful Sign Up!", "Welcome " + user.getName() + "!");
                    } else {
                        ParseUser.logOut();
                        Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Log.i(TAG, String.valueOf(user.isValid()));
            Toast.makeText(SignUpActivity.this, userValidityStates.get(user.isValid()), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Displays Welcome Alert to use then sends them to the Main Activity
     **/
    private void showAlert(String title, String message) {
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