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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter);

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

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("shelters");

        Intent intent = getIntent();
        int numberData = intent.getIntExtra("Shelter", -1);
        List<Shelter> shelterList = getShelterArrayCopy();
        shelter = shelterList.get(numberData);

        TextView name = findViewById(R.id.name);
        TextView address = findViewById(R.id.address);
        TextView available = findViewById(R.id.available);
        TextView capacity = findViewById(R.id.capacity);
        TextView note = findViewById(R.id.note);
        TextView number = findViewById(R.id.number);
        TextView anyone = findViewById(R.id.anyone);

        name.setText(shelter.getName());
        address.setText(shelter.getAddress());
        if (shelter.getAvailable().get("beds") != null) {
            available.setText("" + shelter.getAvailable().get("beds") + " Beds available ");
        } else {
            available.setText("");
        }
        if (shelter.getAvailable().get("rooms") != null) {
            available.setText(available.getText() + "" + shelter.getAvailable().get("rooms") + " Rooms available");
        }
        if (shelter.getCapacity().get("beds") != null) {
            capacity.setText("" + shelter.getCapacity().get("beds") + " Beds capacity ");
        } else {
            capacity.setText("");
        }
        if (shelter.getCapacity().get("rooms") != null) {
            capacity.setText(capacity.getText() + "" + shelter.getCapacity().get("rooms") + " Rooms capacity");
        }
        note.setText("Notes: " + shelter.getNote());
        number.setText("Number: " + shelter.getNumber());
        if (shelter.getAnyone()) {
            anyone.setText("This shelter has no restrictions.");
        } else {
            anyone.setText("Restrictions: \n\n");
            if ((boolean) shelter.getRestrictions().get("children")) {
                if (shelter.getRestrictions().get("child_age") != null) {
                    if ((boolean) shelter.getRestrictions().get("women")) {
                        anyone.setText(anyone.getText() + "This shelter accepts women and children " + shelter.getRestrictions().get("child_age") + " or younger. \n");
                    } else {
                        anyone.setText(anyone.getText() + "You must have a child " + shelter.getRestrictions().get("child_age") + " or younger in your party. \n");
                    }
                }
                else {
                    anyone.setText(anyone.getText() + "You must have a child under 18 in your party. \n");
                }
            }
            if ((boolean) shelter.getRestrictions().get("fam")) {
                anyone.setText(anyone.getText() + "This shelter is available for families only. \n");
            }
            if ((boolean) shelter.getRestrictions().get("men")) {
                anyone.setText(anyone.getText() + "This shelter accepts males only. \n");
            }
            if ((boolean) shelter.getRestrictions().get("women")) {
                anyone.setText(anyone.getText() + "This shelter accepts females only. \n");
            }
            if ((boolean) shelter.getRestrictions().get("vets")) {
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
}
