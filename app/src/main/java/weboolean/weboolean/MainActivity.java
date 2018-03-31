package weboolean.weboolean;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    // [AppCompat Activity Overridden Methods] ===================================================//
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
        // read shared preferences for previous user

        // Automatically opens login activity after main activity runs
        openLoginPage();
        finish();
    }
    // See above
    private void openLoginPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}
