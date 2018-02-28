package weboolean.weboolean;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class WelcomeActivity extends AppCompatActivity {
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        final Button logoutButton = findViewById(R.id.LogOutButton);
        final ListView listView = findViewById(R.id.list);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });

        if (mListView == null) {
            mListView = findViewById(R.id.list);
        }
        for (int i = 0; i < 50; i++) {
            listItems.add("Shelter " + i);
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        setListAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //ItemClicked item = adapter.getItemAtPosition(i);

                Intent intent = new Intent(WelcomeActivity.this, ShelterActivity.class);
                intent.putExtra("Shelter", i);
                //based on item add info to intent
                startActivity(intent);
            }
        });
    }

    protected void setListAdapter(ListAdapter adapter) {
        getListView().setAdapter(adapter);
    }

    protected ListView getListView() {
        if (mListView == null) {
            mListView = findViewById(R.id.list);
        }
        return mListView;
    }

    private void logOut() {
        if ( CurrentUser.logOutUser()) {
            Toast.makeText(WelcomeActivity.this, "Successful LogOut", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(WelcomeActivity.this, "Unsuccessful LogOut", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
