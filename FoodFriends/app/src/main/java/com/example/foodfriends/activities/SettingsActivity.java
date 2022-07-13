package com.example.foodfriends.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.example.foodfriends.R;
import com.example.foodfriends.observable_models.UserObservable;
import com.parse.ParseUser;

import java.time.temporal.TemporalAccessor;
import java.util.Observable;
import java.util.Observer;

public class SettingsActivity extends AppCompatActivity implements Observer {
    private static final String TAG = "Settings Activity";
    private UserObservable user;
    private Button btnLogOut;
    private EditText etName;
    private EditText etCity;
    private EditText etState;
    private TextView tvDiscard;
    private TextView tvSave;
    private Switch switchPrivacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        user = getIntent().getParcelableExtra("user");
        btnLogOut = findViewById(R.id.btnLogOut);
        etName = findViewById(R.id.etSettingsName);
        etCity = findViewById(R.id.etSettingsCity);
        etState = findViewById(R.id.etSettingsState);
        tvDiscard = findViewById(R.id.tvSettingsDiscard);
        tvSave = findViewById(R.id.tvSettingsSave);
        switchPrivacy = findViewById(R.id.switchPrivacy);

        etName.setText(user.getName());
        etCity.setText(user.getCity());
        etState.setText(user.getState());
        switchPrivacy.setChecked(user.getPrivateAccount());

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogOut();
            }
        });
        tvDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setState(etState.getText().toString());
                user.setCity(etCity.getText().toString());
                user.setName(etName.getText().toString());
                user.setPrivateAccount(switchPrivacy.isChecked());
                user.save_user();
                userLogOut();
            }
        });

    }

    /**
     * Logs user out of account and sends them to login page
     */
    private void userLogOut() {
        ParseUser.logOut();
        Intent i = new Intent(this, LogInActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}