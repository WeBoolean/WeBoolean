package weboolean.weboolean;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
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

import java.util.HashMap;

import weboolean.weboolean.models.User;
import weboolean.weboolean.models.UserType;

public class LoginActivity extends AppCompatActivity {

    private static HashMap<String, String> accounts = new HashMap<>();
    private FirebaseAuth mAuth;
    //tag for logcat
    public static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //accounts.put("user", "pass");
        mAuth = FirebaseAuth.getInstance();

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
            public void onClick(View view) {
                openEmailRegistrationPage();
            }
        });

        final Button facebookRegisterButton = findViewById(R.id.facebook_register);
        facebookRegisterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                launchFacebookRegistration(view);
            }
        });

        final Button googleRegisterButton = findViewById(R.id.gplus_register);
        googleRegisterButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                launchGoogleRegistration(view);
            }
        }) ;

    }

    //TODO: Implement.
    private void launchFacebookRegistration(View view) {
        Toast.makeText(LoginActivity.this, "Unimplememnted", Toast.LENGTH_SHORT).show();
    }

    //TODO: Implement.
    private void launchGoogleRegistration(View view) {
        Toast.makeText(LoginActivity.this, "Unimplememnted", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    private void cancelLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void attemptedLogin() {
        String userEmail = ((EditText) findViewById(R.id.email)).getText().toString();
        String userPassword = ((EditText) findViewById(R.id.password)).getText().toString();
        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference mDatabase = database.getReference();
                            Query userQuery = mDatabase.orderByChild("users").equalTo(user.getUid());
                            userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                        UserType t = singleSnapshot.getValue(UserType.class);
                                        CurrentUser.setUserInstance(new User(user.getUid(), t), user);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e(TAG, "onCancelled", databaseError.toException());
                                }
                            });
                            //Set my static user instance

                            //updateUI(user);
                            Toast.makeText(LoginActivity.this, "Successful Login", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Login failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
//        if (userEmail.equals("") || userPassword.equals("")) {
//            Toast.makeText(this, "Must Provide Values", Toast.LENGTH_LONG).show();
//        } else if (userPassword.equals(accounts.get(userEmail))) {
//            Toast.makeText(this, "Successful Login", Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(this, WelcomeActivity.class);
//            startActivity(intent);
//        } else {
//            Toast.makeText(this, "Wrong Credentials ", Toast.LENGTH_LONG).show();
//        }
    }
    private void openEmailRegistrationPage() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }
}

