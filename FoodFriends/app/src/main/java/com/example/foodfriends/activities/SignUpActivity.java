package com.example.foodfriends.activities;

import static java.util.Map.entry;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.foodfriends.R;
import com.example.foodfriends.observable_models.UserObservable;
import com.google.gson.JsonArray;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
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
    private Spinner spinnerState;
    private Spinner spinnerCity;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etName = findViewById(R.id.etSignUpName);
        etUsername = findViewById(R.id.etSignUpUsername);
        etPassword = findViewById(R.id.etSignUpPassword);
        spinnerState = findViewById(R.id.spinnerStateEntry);
        spinnerCity = findViewById(R.id.spinnerCityEntry);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(this);

        String[] state_list = new String[]{"Select State", "AK", "AL", "AR", "AZ", "CA", "CO", "CT", "DC", "DE", "FL", "GA",
                "HI", "IA", "ID", "IL", "IN", "KS", "KY", "LA", "MA", "MD", "ME",
                "MI", "MN", "MO", "MS", "MT", "NC", "ND", "NE", "NH", "NJ", "NM",
                "NV", "NY", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX",
                "UT", "VA", "VT", "WA", "WI", "WV", "WY"};
        ArrayAdapter<String> stateSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, state_list);
        spinnerState.setAdapter(stateSpinnerAdapter);
        stateSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerState.setAdapter(stateSpinnerAdapter);

        JsonElement root = null;
        try {
            root = new JsonParser().parse(new FileReader("../misc/CitiesByState.json"));
            Log.i(TAG, "Found file");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //Get the content of the first map
        JsonObject states = root.getAsJsonObject();

        spinnerState.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JsonArray arr = states.getAsJsonArray(spinnerState.getSelectedItem().toString());
                List<String> list = new ArrayList<String>();
                for(int i = 0; i < arr.size(); i++){
                    list.add(arr.get(i).getAsString());
                }
                String[] city_list = list.toArray(new String[list.size()]);
                ArrayAdapter<String> citySpinnerAdapter = new ArrayAdapter<String>(SignUpActivity.this, android.R.layout.simple_spinner_dropdown_item, city_list);
                spinnerCity.setAdapter(citySpinnerAdapter);
                citySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCity.setAdapter(citySpinnerAdapter);
            }
        });
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
        user.setState(spinnerState.getSelectedItem().toString());
        user.setCity(spinnerCity.getSelectedItem().toString());
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