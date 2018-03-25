package weboolean.weboolean;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import weboolean.weboolean.models.User;
import weboolean.weboolean.models.UserType;

public class RegistrationActivity extends AppCompatActivity {
    // Firebase references
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    // TAG for logcat
    public static final String TAG = RegistrationActivity.class.getSimpleName();

    // [AppCompat Activity Overridden Methods] ===================================================//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setup and Firebase connection
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();

        // Create button and listener, spinner and adapter for registration
        final Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });

        /*final Spinner userTypeSelection = (Spinner) findViewById(R.id.user_type_spinner);
        ArrayAdapter<UserType> adapter = new ArrayAdapter<UserType>(this, android.R.layout.simple_spinner_item,
                UserType.values());
        userTypeSelection.setAdapter(adapter);*/
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    // [Methods] =================================================================================//
    private void attemptRegistration() {
        // Get username and password from user input
        String userEmail = ((EditText) findViewById(R.id.email_input)).getText().toString();
        String userPassword = ((EditText) findViewById(R.id.password_input)).getText().toString();
        // Invalid input check
        if (userEmail.equals("") || userPassword.equals("")) {
            Toast.makeText(this, "Must Provide Values", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Valid Values", Toast.LENGTH_LONG).show();
            mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        //Get user type from spinner
                        //UserType t =  (UserType) ((Spinner) findViewById(R.id.user_type_spinner)).getSelectedItem();

                        //Create custom user
                        //User u = new User(user.getUid(), t);
                        //Log.e(TAG, "" + u.getCurrentShelter());
                        User u = new User();

                        u = new User(user.getUid(), u.getUserType(), u.getSex(), u.getFamily(), u.getDependents(), u.getYoungest(),
                        u.getSpouse(), u.getVeteran(), u.getAge(), u.getCheckedIn(), u.getCurrentShelter(), u.getLocked());

                        //Set current user instance
                        try {
                            CurrentUser.setUserInstance(u, user);
                        } catch (InstantiationException e) {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistrationActivity.this, "Already logged in.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        //Write current user to database
                        mDatabase.child("users").child(user.getUid())
                                .setValue(u);

                        //Launch login activity
                        Intent intent = new Intent(RegistrationActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                    }
                });
        }
    }
}
