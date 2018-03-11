package weboolean.weboolean;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import weboolean.weboolean.models.Shelter;


public class SearchActivity extends AppCompatActivity {
    // [AppCompat Activity Overridden Methods] ===================================================//
    public final static String TAG =  SearchActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // Create toolbar ann search button with listener
        final RadioButton rb_male, rb_female;
        final CheckBox age, fam, vet;
        rb_male = findViewById(R.id.gender_radio);
        rb_female = findViewById(R.id.female_radio);
        age = findViewById(R.id.age_check);
        fam = findViewById(R.id.fam_check);
        vet = findViewById(R.id.vet_check);
        final String shelter_name_match = ((EditText) findViewById(R.id.name_input)).getText().toString();
        String child_age_string = ((EditText) findViewById(R.id.age_input)).getText().toString();
        final int child_age = Integer.parseInt(child_age_string);
        final Button searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                HashMap vals = new HashMap();
//                if (!(rb_male.isChecked()) && !(rb_female.isChecked()) && !(age.isChecked()) && !(vet.isChecked()) && !fam.isChecked()) {
//                    vals.put("anyone", true);
//                } else {
//                    vals.put("anyone", false);
//                }
                vals.put("children", age.isChecked());
                vals.put("fam", fam.isChecked());
                vals.put("men", rb_male.isChecked());
                vals.put("vets", vet.isChecked());
                vals.put("women", rb_female.isChecked());
                vals.put("name", shelter_name_match);
                vals.put("child_age", child_age);
                List<Shelter> okShelters = searchShelters(vals);
                Intent browseShelters = new Intent(SearchActivity.this, WelcomeActivity.class);
                Bundle shelterBundle = new Bundle();
                shelterBundle.putStringArrayList("shelters", Shelter.toStrings(okShelters));
                browseShelters.putExtras(shelterBundle);
                startActivity(browseShelters);

            }
        });
    }

    protected static List<Shelter> searchShelters(Map<String, Object> parameters) {
        List<Shelter> shelter_list = ShelterSingleton.getShelterArrayCopy();
        Set<Shelter> consideration = new HashSet<>(shelter_list);
        //Exact String Searching -- later we'll move onto fuzzy matching with a more advanced mech
        if (parameters.containsKey("name") && !((String) parameters.get("name")).equals("") ) {
            ArrayList<Shelter> exactMatch = new ArrayList<Shelter>();
            Log.d(TAG, "Exact String Searching launched for " + (String) parameters.get("name"));
            for (Shelter shelter: consideration) {
                if (shelter.toString().equals(parameters.get("name"))) {
                    exactMatch.add(shelter);
                }
            }
            return exactMatch;
        }
        //By default, this goes to "else"
        for (String restriction: parameters.keySet()) {
            Log.d(TAG, "Searching for Restriction\t" + restriction);
            Set<Shelter> removeSet = new HashSet<>();
            if (restriction == "child_age") {
                // now do restriction matching
                searchChildAge((Integer) parameters.get(restriction), removeSet, consideration);
            } else {
                if ((Boolean) parameters.get(restriction)) {
                    Log.d(TAG, parameters.get(restriction).toString());
                    for (Shelter shelter : consideration) {
                        if ((Boolean) shelter.getRestrictions().get(restriction)) {
                            // Pass here. If the restriction is true then we don't wanna do anything
                        } else {
                            removeSet.add(shelter);
                        }
                    }
                }
            }
            consideration.removeAll(removeSet);
        }
        return new ArrayList<>(consideration);
    }

    private static void searchChildAge(Integer restriction, Set<Shelter> removeSet, final Set<Shelter> consideration) {
        //modifies removeSet with all matches from consdieration
        if (restriction == null) {
            return;
        }
        // now, we can cast to int
        int rest = restriction.intValue();
        for (Shelter s: consideration) {
            int ShelterAgeRestriction = ((Boolean) (s.getRestrictions().get("children")))
                    ? (Integer) s.getRestrictions().get("child_age") : 19;
            if (ShelterAgeRestriction > rest) {
                removeSet.add(s);
            }
        }
    }


}
