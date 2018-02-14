package weboolean.weboolean;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private static HashMap<String, String> accounts = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        accounts.put("user", "pass");

        final Button signInButton = findViewById(R.id.sign_in_button);

        final Button cancelButton = findViewById(R.id.cancelButton);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptedLogin();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelLogin();
            }
        });
    }

    private void cancelLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void attemptedLogin() {
        String userEmail = ((EditText) findViewById(R.id.email)).getText().toString();
        String userPassword = ((EditText) findViewById(R.id.password)).getText().toString();

        if (userEmail.equals("") || userPassword.equals("")) {
            Toast.makeText(this, "Must Provide Values", Toast.LENGTH_LONG).show();
        } else if (userPassword.equals(accounts.get(userEmail))) {
            Toast.makeText(this, "Successful Login", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Wrong Credentials ", Toast.LENGTH_LONG).show();
        }
    }
}

