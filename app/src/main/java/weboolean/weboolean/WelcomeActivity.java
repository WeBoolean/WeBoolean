package weboolean.weboolean;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import weboolean.weboolean.models.Shelter;


/**
 * Shows Login and Registration Screen Options.
 */
public class WelcomeActivity extends AppCompatActivity {

    // List of shelters
    private ArrayList<String> listItems = new ArrayList<>();
    private ListView mListView;
    // TAG for log
    public static final String TAG = WelcomeActivity.class.getSimpleName();

    // [AppCompat Activity Overridden Methods]
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Create logout button and list of shelters with listener and adapter
        final Button logoutButton = findViewById(R.id.LogOutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });



        if (mListView == null) {
            mListView = findViewById(R.id.list);
        }

        // Obtain intent
        Intent startIntent = getIntent();
        Bundle instructions = startIntent.getExtras();
        if ((instructions != null) && !instructions.isEmpty()
                && instructions.containsKey("shelters")) {
            // Read Shelters from bundle
            listItems = (ArrayList<String>) instructions.get("shelters");
        } else {
            // Get Shelter array list from Shelter Singleton (Firebase)
            listItems = Shelter.toStrings(ShelterSingleton.getShelterArrayCopy());
        }
        // in case no shelters were found, and it's magically null (will never happen)
        if (listItems == null) {
            listItems = new ArrayList<>();
        }
        Log.d("SearchActivity", "" + listItems.size());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        setListAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(WelcomeActivity.this,
                        ShelterActivity.class);
                intent.putExtra("Shelter",
                        ShelterSingleton.getShelterKeyByCommonName(listItems.get(i)));
                // based on item add info to intent
                startActivity(intent);
            }
        });
    }
    // [ Getters and setters ]
    private ListView getListView() {
        if (mListView == null) {
            mListView = findViewById(R.id.list);
        }
        return mListView;
    }

    private void setListAdapter(ListAdapter adapter) {
        getListView().setAdapter(adapter);
    }

    // [ Methods ]
    private void logOut() {
        if ( CurrentUser.logOutUser()) {
            Toast.makeText(WelcomeActivity.this,
                    "Successful LogOut", Toast.LENGTH_SHORT).show();
            // Go back to main activity after successful logout
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(WelcomeActivity.this,
                    "Unsuccessful LogOut", Toast.LENGTH_SHORT).show();
        }
    }
}