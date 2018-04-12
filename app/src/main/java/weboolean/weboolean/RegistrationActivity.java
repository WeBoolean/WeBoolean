package weboolean.weboolean;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import weboolean.weboolean.models.User;
import weboolean.weboolean.models.UserType;

/**
 * Activity to register the user.
 */
public class RegistrationActivity extends AppCompatActivity {
    // Firebase references
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    // TAG for logcat
    private static final String TAG = RegistrationActivity.class.getSimpleName();

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
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    // [Methods] =================================================================================//
    private void attemptRegistration() {
        // Get username and password from user input
        String userEmail = ((EditText) findViewById(R.id.email_input)).getText().toString();
        String userPassword = ((EditText) findViewById(R.id.password_input)).getText().toString();

        // Invalid input check
        if ("".equals(userEmail) || "".equals(userPassword)
            || "".equals(((EditText) findViewById(R.id.age_input)).getText().toString())) {
            Toast.makeText(this, "Must Provide Values", Toast.LENGTH_LONG).show();
        } else if (((Switch) findViewById(R.id.family_check)).isChecked() &&
            ("".equals(((EditText) findViewById(R.id.dependent_input)).getText().toString())
            || "".equals(((EditText) findViewById(R.id.youngest_age_input)).getText().toString()))){
            Toast.makeText(this, "Must Input Family Size", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Valid Values", Toast.LENGTH_LONG).show();
            Task user = mAuth.createUserWithEmailAndPassword(userEmail, userPassword);
            user.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();

                    // Get remaining user info, must be final to access in inner classes
                    UserType usertype = UserType.User;
                    String sex = ((Spinner) findViewById(R.id.sex_input))
                            .getSelectedItem().toString();
                    boolean family = ((Switch) findViewById(R.id.family_check)).isChecked();
                    int dependents =
                            "".equals(((EditText) findViewById(R.id.dependent_input))
                                    .getText().toString())
                        ? 0 : Integer.parseInt(((EditText) findViewById(R.id.dependent_input))
                                    .getText().toString());
                    int youngestAge =
                            "".equals(((EditText) findViewById(R.id.youngest_age_input))
                                    .getText().toString())
                        ? -1 : Integer.parseInt(((EditText)
                                    findViewById(R.id.youngest_age_input))
                                    .getText().toString());
                    String spouse =
                            "None".equals(((Spinner) findViewById(R.id.spouse_input))
                                    .getSelectedItem().toString())
                        ? null : ((Spinner) findViewById(R.id.spouse_input))
                                    .getSelectedItem().toString();
                    boolean veteran = ((Switch) findViewById(R.id.veteran_check)).isChecked();
                    int age = Integer.parseInt(((EditText) findViewById(R.id.age_input))
                            .getText().toString());
//                        boolean checkedIn = false;
                    int currentShelter = -1;
//                        boolean locked = false;

                    //Create custom user
                    User u;
                    assert(user != null);
                    String Uid = user.getUid();
                    u = new User(Uid, usertype, sex, family, dependents,
                        youngestAge, spouse, veteran, age, false, currentShelter,
                        false);


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
                    Intent intent = new Intent(RegistrationActivity.this, MapsActivity.class);
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
