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
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    //tag for logcat
    public static final String TAG = RegistrationActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();
        //auto generated code for floating action button (email button)
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        final Button registerButton = findViewById(R.id.Registration_RegisterButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });

        Spinner userTypeSelection = (Spinner) findViewById(R.id.user_type_spinner);
        ArrayAdapter<UserType> adapter = new ArrayAdapter<UserType>(this, android.R.layout.simple_spinner_item,
                UserType.values());
        userTypeSelection.setAdapter(adapter);
    }
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //check to see if user is currently signed in! Update ui accordingly
        //updateUI(currentUser);
    }

    private void attemptRegistration() {
        String userEmail = ((EditText) findViewById(R.id.Registration_Email)).getText().toString();
        String userPassword = ((EditText) findViewById(R.id.Registration_Password)).getText().toString();
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
                        //updateUI(user);
                        //Switch to logged in screen
                        //Get user type from spinnger
                        UserType t =  (UserType) ((Spinner) findViewById(R.id.user_type_spinner)).getSelectedItem();

                        //Create custom user
                        User u = new User(user.getUid(), t);

                        //Set current user instance
                        CurrentUser.setUserInstance(u, user);

                        //Write current user to database
                        mDatabase.child("users").child(user.getUid())
                                .setValue(t);

                        //Launch next activity.
                        Intent intent = new Intent(RegistrationActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        //updateUI(null);
                    }
                    }
                });



        }
    }
}
