package weboolean.weboolean;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Automatically navigate to LoginPage
        //Launch singleton
        try {
            new Thread(new ShelterSingleton()).start();
        } catch (InstantiationException e) {
            e.printStackTrace(); //this will never happen.
        }
        openLoginPage();

//        setContentView(R.layout.activity_main);
//
//
//        final Button loginButton = findViewById(R.id.LoginButton);
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openLoginPage();
//            }
//        });
//
//        final Button registerButton = findViewById(R.id.RegisterButton);
//        registerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openRegistrationPage();
//            }
//        });

    }

    private void openLoginPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
