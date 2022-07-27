package com.example.foodfriends.activities;

import static java.util.Map.entry;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "Sign Up Activity";
    private final static int EMPTY_USER_NAME_RESULT_CODE = 0;
    private final static int EMPTY_PASSWORD_RESULT_CODE = 1;
    private final static int SHORT_PASSWORD_RESULT_CODE = 2;
    private final static int EMPTY_NAME_RESULT_CODE = 3;
    private final static int OK_RESULT_CODE = -1;
    private static JSONObject finalStates;
    private final static Map<Integer, String> userValidityStates = Map.ofEntries(entry(EMPTY_USER_NAME_RESULT_CODE, "Username must not be empty"), entry(EMPTY_NAME_RESULT_CODE, "Name must not be empty"), entry(EMPTY_PASSWORD_RESULT_CODE, "Password must not be empty"), entry(SHORT_PASSWORD_RESULT_CODE, "Password must be over 5 characters"));
    private final static String[] stateList = new String[]{"Select State", "Alaska", "Alabama", "Arkansas", "Arizona", "California", "Colorado", "Connecticut", "District of Columbia", "Delaware", "Florida", "Georgia", "Hawaii", "Iowa", "Idaho", "Illinois", "Indiana", "Kansas", "Kentucky", "Louisiana", "Massachusetts", "Maryland", "Maine", "Michigan", "Minnesota", "Missouri", "Mississippi", "Montana", "North Carolina", "North Dakota", "Nebraska", "New Hampshire", "New Jersey", "New Mexico", "Nevada", "New York", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Virginia", "Vermont", "Washington", "Wisconsin", "West Virginia", "Wyoming"};
    private final static Map<String, String> stateAbbreviations = Map.ofEntries(entry("Alaska", "AK"), entry("Alabama", "AL"), entry("Arkansas", "AR"), entry("Arizona", "AZ"), entry("California", "CA"), entry("Colorado", "CO"), entry("Connecticut", "CT"), entry("District of Columbia", "DC"), entry("Delaware", "DE"), entry("Florida", "FL"), entry("Georgia", "GA"), entry("Hawaii", "HI"), entry("Iowa", "IA"), entry("Idaho", "ID"), entry("Illinois", "IL"), entry("Indiana", "IN"), entry("Kansas", "KS"), entry("Kentucky", "KY"), entry("Louisiana", "LA"), entry("Massachusetts", "MA"), entry("Maryland", "MD"), entry("Maine", "ME"), entry("Michigan", "MI"), entry("Minnesota", "MN"), entry("Missouri", "MO"), entry("Mississippi", "MS"), entry("Montana", "MT"), entry("North Carolina", "NC"), entry("North Dakota", "ND"), entry("Nebraska", "NE"), entry("New Hampshire", "NH"), entry("New Jersey", "NJ"), entry("New Mexico", "NM"), entry("Nevada", "NV"), entry("New York", "NY"), entry("Ohio", "OH"), entry("Oklahoma", "OK"), entry("Oregon", "OR"), entry("Pennsylvania", "PA"), entry("Rhode Island", "RI"), entry("South Carolina", "SC"), entry("South Dakota", "SD"), entry("Tennessee", "TN"), entry("Texas", "TX"), entry("Utah", "UT"), entry("Virginia", "VA"), entry("Vermont", "VT"), entry("Washington", "WA"), entry("Wisconsin", "WI"), entry("West Virginia", "WV"), entry("Wyoming", "WY"));
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
        
        ArrayAdapter<String> stateSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, stateList);
        spinnerState.setAdapter(stateSpinnerAdapter);
        stateSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerState.setAdapter(stateSpinnerAdapter);
        finalStates = populateStates();

        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                JSONArray arr = null;
                try {
                    arr = finalStates.getJSONArray(spinnerState.getSelectedItem().toString());
                } catch (JSONException e) {
                    Log.e(TAG, "Error message", e);
                }
                List<String> list = new ArrayList<String>();

                String[] cityList;
                if (arr == null) {
                    cityList = new String[]{"No State Selected"};
                }

                else {
                    for (int i = 0; i < arr.length(); i++) {
                        try {
                            list.add(arr.get(i).toString());
                        } catch (JSONException e) {
                            Log.e(TAG, "Error message", e);
                        }
                    }
                    cityList = list.toArray(new String[list.size()]);
                }
                ArrayAdapter<String> citySpinnerAdapter = new ArrayAdapter<String>(SignUpActivity.this, android.R.layout.simple_spinner_dropdown_item, cityList);
                spinnerCity.setAdapter(citySpinnerAdapter);
                citySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCity.setAdapter(citySpinnerAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private JSONObject populateStates() {
        InputStream inputStream = getResources().openRawResource(R.raw.cities_by_state);
        JSONParser parser = new JSONParser();
        String objString = null;
        try {
            objString = parser.parse(new InputStreamReader(inputStream)).toString();
        } catch (IOException | org.json.simple.parser.ParseException e) {
            Log.e(TAG, "Error message", e);
        }
        JSONObject states = null;
        try {
            states = new JSONObject(objString);
        } catch (JSONException e) {
            Log.e(TAG, "Error message", e);
        }
        return states;
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
        // Set the user"s username and password, which can be obtained by a forms
        user.setUsername(etUsername.getText().toString());
        user.setPassword(etPassword.getText().toString());
        user.setState(stateAbbreviations.get(spinnerState.getSelectedItem().toString()));
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