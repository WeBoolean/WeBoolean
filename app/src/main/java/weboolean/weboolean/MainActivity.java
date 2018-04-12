package weboolean.weboolean;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


/**
 * Initialization Activity Only.
 */
public class MainActivity extends AppCompatActivity {
    // [AppCompat Activity Overridden Methods] ===================================================//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Automatically navigate to LoginPage
        //Launch singleton
        try {
            Thread thread1 = new Thread(new ShelterSingleton());
            thread1.start();
        } catch (InstantiationException e) {
            e.printStackTrace(); //this will never happen.
        }
        // read shared preferences for previous user
        // TOO.

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
