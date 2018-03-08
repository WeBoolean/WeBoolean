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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import weboolean.weboolean.models.Shelter;

public class WelcomeActivity extends AppCompatActivity {
    // Firebase references
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference ref = mDatabase.getReference("shelters");
    //List of shelters
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private ListView mListView;
    // TAG for log
    public static final String TAG = WelcomeActivity.class.getSimpleName();

    // [AppCompat Activity Overridden Methods] ===================================================//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //Create logout button and list of shelters with listener and adapter
        final Button logoutButton = findViewById(R.id.LogOutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });

        ListView listView = findViewById(R.id.list);

        if (mListView == null) {
            mListView = findViewById(R.id.list);
        }
        // Get Shelter array list from Shelter Singleton (Firebase)

        ArrayList<Shelter> shelterList = ShelterSingleton.getShelterArrayCopy();

        for (int i = 0; i < shelterList.size(); i++) {
            listItems.add(shelterList.get(i).toString());
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        setListAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(WelcomeActivity.this, ShelterActivity.class);
                intent.putExtra("Shelter", i);
                //based on item add info to intent
                startActivity(intent);
            }
        });
    }
    // [ Getters and setters ] ===================================================================//
    protected ListView getListView() {
        if (mListView == null) {
            mListView = findViewById(R.id.list);
        }
        return mListView;
    }

    protected void setListAdapter(ListAdapter adapter) {
        getListView().setAdapter(adapter);
    }

    // [ Methods ] ===============================================================================//
    private void logOut() {
        if ( CurrentUser.logOutUser()) {
            Toast.makeText(WelcomeActivity.this, "Successful LogOut", Toast.LENGTH_SHORT).show();
            // Go back to main activity after successful logout
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(WelcomeActivity.this, "Unsuccessful LogOut", Toast.LENGTH_SHORT).show();
        }
    }
}