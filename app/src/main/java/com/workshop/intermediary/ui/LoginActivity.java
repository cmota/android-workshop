package com.workshop.intermediary.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.workshop.intermediary.R;

public class LoginActivity extends Activity {

    private final String PREFERENCE_USERNAME = "username";
    private final String PREFERENCE_PASSWORD = "password";
    private final String PREFERENCE_CHECKBOX = "remember_me";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();

        setContentView(R.layout.activity_login);

        initializeViewComponents();
    }

    private void initializeViewComponents() {
        String fontPath = "Helvetica.ttf";

        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);

        TextView appName = (TextView) findViewById(R.id.text_app_name);
        appName.setTypeface(tf);

        final EditText username = (EditText) findViewById(R.id.editText_username);
        final EditText password = (EditText) findViewById(R.id.editText_password);

        CheckBox checkbox = (CheckBox) findViewById(R.id.checkBox);

        if(isDataAvailable()) {
            username.setText(getUsername());
            password.setText(getPassword());
            checkbox.setChecked(true);
        }

        Button loginButton = (Button) findViewById(R.id.btn_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();

                if(user.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Invalid username and/or password", Toast.LENGTH_SHORT).show();
                } else {
                    shouldSaveUserPreferences(user, pass);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void shouldSaveUserPreferences(String username, String password) {
        CheckBox checkbox = (CheckBox) findViewById(R.id.checkBox);
        saveUserPreferences(username, password, checkbox.isChecked());
    }

    private void saveUserPreferences(String username, String password, boolean shouldRemember) {
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putString (PREFERENCE_USERNAME, username);
        editor.putString(PREFERENCE_PASSWORD, password);
        editor.putBoolean(PREFERENCE_CHECKBOX, shouldRemember);
        editor.commit();
    }

    private boolean isDataAvailable() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        return prefs.getBoolean(PREFERENCE_CHECKBOX, false);
    }

    private String getUsername() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        return prefs.getString(PREFERENCE_USERNAME, null);
    }

    private String getPassword() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        return prefs.getString(PREFERENCE_PASSWORD, null);
    }
}
