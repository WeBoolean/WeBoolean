package weboolean.weboolean;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import weboolean.weboolean.models.Shelter;

public class SearchActivity extends AppCompatActivity {
    // [AppCompat Activity Overridden Methods] ===================================================//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // Create toolbar ann search button with listener
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //TODO: implement search
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            }
        });
    }

    protected static List<Shelter> searchShelters(Map<String, Object> parameters) {
        List<Shelter> shelter_list = ShelterSingleton.getShelterArrayCopy();
        Set<Shelter> consideration = new HashSet<>(shelter_list);
        for (String restriction: parameters.keySet()) {
            if ((Boolean) parameters.get(restriction))  {
                Set<Shelter> removeSet = new HashSet<>();
                for (Shelter shelter: consideration) {
                    if ((Boolean) shelter.getRestrictions().get(restriction)) {
                        // Pass here. If the restriction is true then we don't wanna do anything
                    } else {
                        removeSet.add(shelter);
                    }
                }
                consideration.removeAll(removeSet);
            }
        }
        return new ArrayList<>(consideration);
    }

}
