package weboolean.weboolean;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import weboolean.weboolean.models.Shelter;

import static weboolean.weboolean.ShelterSingleton.getShelterArrayCopy;

public class ShelterActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter);
        // Firebase references
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("shelters");

        // Create back button and listener
        final Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final Button checkInButton = findViewById(R.id.checkin_button);
        checkInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIn();
            }
        });

        // Get current shelter
        Shelter shelter = getShelter();

        // TextViews to display shelter data
        TextView name = findViewById(R.id.name);
        TextView address = findViewById(R.id.address);
        TextView available = findViewById(R.id.available);
        TextView capacity = findViewById(R.id.capacity);
        TextView note = findViewById(R.id.note);
        TextView number = findViewById(R.id.number);
        TextView anyone = findViewById(R.id.anyone);

        // Set shelter data
        name.setText(shelter.getName());
        address.setText(shelter.getAddress());
        setCapacities(shelter, available, capacity);
        note.setText("Notes: " + shelter.getNote());
        number.setText("Number: " + shelter.getNumber());

        // [Restrictions] ========================================================================//
        // Anyone case
        if (shelter.getAnyone()) {
            anyone.setText("This shelter accepts anyone");
        } else {
            // Case where there ARE restrictions
            anyone.setText("Restrictions: \n\n");
            boolean childRestriction = (boolean) shelter.getRestrictions().get("children");
            boolean familyRestriction = (boolean) shelter.getRestrictions().get("fam");
            boolean maleRestriction = (boolean) shelter.getRestrictions().get("men");
            boolean femaleRestriction = (boolean) shelter.getRestrictions().get("women");
            boolean veteranRestriction = (boolean) shelter.getRestrictions().get("vets");
            if (childRestriction) {
                if (shelter.getRestrictions().get("child_age") != null) {
                    if ((boolean) shelter.getRestrictions().get("women")) {
                        anyone.setText(anyone.getText() + "This shelter accepts women and children " + shelter.getRestrictions().get("child_age") + " or younger. \n");
                        // already set female
                        femaleRestriction = false;
                    } else {
                        anyone.setText(anyone.getText() + "You must have a child " + shelter.getRestrictions().get("child_age") + " or younger in your party. \n");
                    }
                }
                else {
                    anyone.setText(anyone.getText() + "You must have a child under 18 in your party. \n");
                }
            }

            if (familyRestriction) {
                anyone.setText(anyone.getText() + "This shelter accepts families only.");
            }
            if (maleRestriction) {
                anyone.setText(anyone.getText() + "This shelter accepts males only. \n");
            }
            if (femaleRestriction) {
                anyone.setText(anyone.getText() + "This shelter accepts females only. \n");
            }
            if (veteranRestriction) {
                anyone.setText(anyone.getText() + "This shelter accepts veterans only. \n");
            }
        }
    }

    private Shelter getShelter() {
        // Create shelter view intent, passed a shelter's number through the Intent extra data
        Intent intent = getIntent();
        int numberData = intent.getIntExtra("Shelter", -1);
        // Select our shelter from shelter list to display
        List<Shelter> shelterList = getShelterArrayCopy();
        Shelter shelter = shelterList.get(numberData);
        return shelter;
    }

    private void setCapacities(Shelter shelter, TextView available, TextView capacity) {
        // [Availability] =========================================================================/
        if (shelter.getAvailable().get("beds") != null) {
            available.setText("" + shelter.getAvailable().get("beds") + " Beds available ");
        } else {
            available.setText("");
        }
        if (shelter.getAvailable().get("rooms") != null && shelter.getAvailable().get("rooms")!=0) {
            available.setText(available.getText() + "" + shelter.getAvailable().get("rooms") + " Rooms available");
        }
        if (shelter.getAvailable().get("rooms") != null && shelter.getAvailable().get("rooms")==0) {
            available.setText("N/A Rooms available");
        }
        // [Capacity] =============================================================================/
        if (shelter.getCapacity().get("beds") != null && shelter.getCapacity().get("beds") != 0) {
            capacity.setText("" + shelter.getCapacity().get("beds") + " Bed capacity ");
        } else if (shelter.getCapacity().get("beds") != null && shelter.getCapacity().get("beds") == 0){
            capacity.setText("N/A Beds Capacity \n");
        } else {
            capacity.setText("");
        }
        if (shelter.getCapacity().get("rooms") != null && shelter.getCapacity().get("rooms") != 0) {
            capacity.setText(capacity.getText() + "" + shelter.getCapacity().get("rooms") + " Room capacity");
        } else if (shelter.getCapacity().get("rooms") != null && shelter.getCapacity().get("rooms") == 0) {
            capacity.setText("N/A Room capacity");
        }
    }
    private void checkIn() {
        //TODO: implement check in logic with firebase querying, updating, and updating views
    }
}
