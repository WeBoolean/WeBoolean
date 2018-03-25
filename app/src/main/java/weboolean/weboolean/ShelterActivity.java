package weboolean.weboolean;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weboolean.weboolean.models.Shelter;

import static weboolean.weboolean.ShelterSingleton.getShelterArrayCopy;

public class ShelterActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference ref;
    Shelter shelter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter);
        // Firebase references
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("shelters");
        // Get current shelter
        Shelter shelter = getShelter();

        // Create back button and listener
        final Button backButton = findViewById(R.id.backButton);

        final Button checkinButton = findViewById(R.id.checkinButton);
        final Button checkoutButton = findViewById(R.id.checkoutButton);

        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkOut()) {
                    Toast.makeText(ShelterActivity.this, "Successful Check Out!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ShelterActivity.this, "Check Out Failed: have to be checked in to checkout", Toast.LENGTH_LONG).show();
                }
            }
        });

        checkinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkIn()) {
                    Toast.makeText(ShelterActivity.this, "Successful Check In!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ShelterActivity.this, "Check In Failed: you can only be checked in one shelter and if they have available space", Toast.LENGTH_LONG).show();
                }
            }
        });

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
                checkIn(shelter);
            }
        });

        Intent intent = getIntent();
        int numberData = intent.getIntExtra("Shelter", -1);
        List<Shelter> shelterList = getShelterArrayCopy();
        shelter = shelterList.get(numberData);


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


    private boolean checkIn(){
        if (CurrentUser.getCurrentUser().getCheckedIn()) {
            return false;
        } else {
            if (!shelter.getAnyone()) {
                if ((boolean) shelter.getRestrictions().get("fam") && !CurrentUser.getCurrentUser().getFamily()) {
                    return false;
                }
                if ((boolean) shelter.getRestrictions().get("men") && !CurrentUser.getCurrentUser().getSex().equals("male")) {
                    return false;
                }
                if ((boolean) shelter.getRestrictions().get("women") && !CurrentUser.getCurrentUser().getSex().equals("women")) {
                    return false;
                }
                if ((boolean) shelter.getRestrictions().get("vets") && !CurrentUser.getCurrentUser().getVeteran()) {
                    return false;
                }
                if ((boolean) shelter.getRestrictions().get("children") && CurrentUser.getCurrentUser().getDependents() == 0) {
                    return false;
                } else if ((int) shelter.getRestrictions().get("child_age") < CurrentUser.getCurrentUser().getYoungest()) {
                    return false;
                }
            }
            if (CurrentUser.getCurrentUser().getFamilySize() > 1) {
                if (shelter.getAvailable().get("rooms") != null && shelter.getAvailable().get("rooms") > 0) {
                    CurrentUser.getCurrentUser().setCurrentShelter(shelter.getKey());
                    CurrentUser.getCurrentUser().setCheckedIn(true);
                    Map<String, Integer> newAvaibility = new HashMap<>();
                    newAvaibility.put("rooms", shelter.getAvailable().get("rooms") -1 );
                    shelter.setAvailable(newAvaibility);
                    return true;
                }
            }
            if (shelter.getAvailable().get("beds") != null && shelter.getAvailable().get("beds") > CurrentUser.getCurrentUser().getFamilySize()) {
                CurrentUser.getCurrentUser().setCurrentShelter(shelter.getKey());
                CurrentUser.getCurrentUser().setCheckedIn(true);
                Map<String, Integer> newAvaibility = new HashMap<>();
                newAvaibility.put("beds", shelter.getAvailable().get("beds") - CurrentUser.getCurrentUser().getFamilySize());
                shelter.setAvailable(newAvaibility);
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean checkOut() {
        if (CurrentUser.getCurrentUser().getCheckedIn() && CurrentUser.getCurrentUser().getCurrentShelter() == shelter.getKey()) {
            CurrentUser.getCurrentUser().setCheckedIn(false);
            CurrentUser.getCurrentUser().setCurrentShelter(-1);
            Map<String, Integer> newAvaibility = new HashMap<>();
            if (shelter.getCapacity().get("rooms") > 0) {
                newAvaibility.put("rooms", shelter.getAvailable().get("rooms") + CurrentUser.getCurrentUser().getFamilySize());
            } else {
                newAvaibility.put("beds", shelter.getAvailable().get("beds") + 1);
            }
            shelter.setAvailable(newAvaibility);
            return true;
        } else {
            return false;
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


}
