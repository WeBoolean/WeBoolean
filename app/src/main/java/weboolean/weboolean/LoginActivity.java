package weboolean.weboolean;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import weboolean.weboolean.models.User;
import weboolean.weboolean.models.UserType;

/**
 * Activity for User to login or register
 */
public class LoginActivity extends AppCompatActivity {
    //FirebaseAuth to get current user
    private FirebaseAuth mAuth;
    // TAG for logcat
    private static final String TAG = LoginActivity.class.getSimpleName();

    // [AppCompat Activity Overridden Methods] ===================================================//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        //Create buttons and listeners
        final Button signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptedLogin();
            }
        });

        final Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelLogin();
            }
        });

        final Button emailRegisterButton = findViewById(R.id.email_register);
        emailRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEmailRegistrationPage();
            }
        });

        final Button facebookRegisterButton = findViewById(R.id.facebook_register);
        facebookRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchFacebookRegistration();
            }
        });

        final Button googleRegisterButton = findViewById(R.id.gplus_register);
        googleRegisterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                launchGoogleRegistration();
            }
        }) ;

    }


    private void launchFacebookRegistration() {
        Toast text = Toast.makeText(LoginActivity.this, "Unimplememnted", Toast.LENGTH_SHORT);
        text.show();
    }

    @SuppressWarnings("ChainedMethodCall")
    private void launchGoogleRegistration() {
        Toast text = Toast.makeText(LoginActivity.this, "Unimplememnted", Toast.LENGTH_SHORT);
        text.show();
    }

    // [Overridden AppCompatActivity Method] =====================================================//
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
       // FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    // [Methods] =================================================================================//
    // Cancel button handling
    private void cancelLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    // Login button handling
    @SuppressWarnings("ChainedMethodCall")
    private void attemptedLogin() {
        // Get username and password from user input
        String userEmail = ((EditText) findViewById(R.id.email)).getText().toString();
        String userPassword = ((EditText) findViewById(R.id.password)).getText().toString();
        // Invalid input checks
        if ("".equals(userEmail) || "".equals(userPassword)) {
            Toast text = Toast.makeText(this, "Must Provide Values", Toast.LENGTH_LONG);
            text.show();
        } else {
            // Attempt sign in using FirebaseAuth
            mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @SuppressWarnings("ChainedMethodCall")
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                final FirebaseUser user = mAuth.getCurrentUser();
                                try {
                                    assert user != null;
                                    CurrentUser.setUserInstance(
                                            new User(user.getUid(), UserType.User), user);
                                } catch (InstantiationException e) {
                                    e.printStackTrace();
                                }
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference mDatabase = database.getReference();
                                Query userQuery = mDatabase.orderByChild("users")
                                        .equalTo(user.getUid());
                                userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot singleSnap : dataSnapshot.getChildren()) {
                                            UserType t = singleSnap.getValue(UserType.class);
                                            try {
                                                CurrentUser.setUserInstance(
                                                        new User(user.getUid(), t), user);
                                            } catch (InstantiationException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e(TAG, "onCancelled", databaseError.toException());
                                    }
                                });
                                // Successful login, change activity
                                Toast.makeText(LoginActivity.this,
                                        "Successful Login", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Login failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    // Registration button handling
    private void openEmailRegistrationPage() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

    }
}

