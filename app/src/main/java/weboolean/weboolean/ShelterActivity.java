package weboolean.weboolean;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import weboolean.weboolean.models.User;

import static weboolean.weboolean.ShelterSingleton.getShelterArrayCopy;
import static weboolean.weboolean.ShelterSingleton.updateShelter;

public class ShelterActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference ref;
    Shelter shelter;
    public static final String TAG = ShelterActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter);
        // Firebase references
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("shelters");
        // Get current shelter
        shelter = getShelter();

        // Create back button and listener
        final Button backButton = findViewById(R.id.backButton);
        final Button checkinButton = findViewById(R.id.checkinButton);
        final Button checkoutButton = findViewById(R.id.checkoutButton);

        checkinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkIn()) {
                    updateShelter(shelter.getKey(), shelter);
                    User u = CurrentUser.getCurrentUser();
                    u.setCheckedIn(true);
                    u.setCurrentShelter(shelter.getKey());
                    CurrentUser.getCurrentUser().setCheckedIn(true);
                    CurrentUser.updateUser(u);
                    Toast.makeText(ShelterActivity.this, "Successful Check In!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ShelterActivity.this, "Check In Failed, make sure you meet all shelter restrictions & there is available space.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkOut()) {
                    updateShelter(shelter.getKey(), shelter);
                    User u = CurrentUser.getCurrentUser();
                    u.setCheckedIn(false);
                    u.setCurrentShelter(-1);
                    CurrentUser.updateUser(u);

                    Toast.makeText(ShelterActivity.this, "Successful Check Out!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ShelterActivity.this, "Check Out Failed: have to be checked in to checkout", Toast.LENGTH_LONG).show();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

    //TODO: Doesn't push checked in status to firebase or update UI yet
    //TODO: also probably lots of edge cases and situations to check
    //TODO: should make users with several different parameters and try to check into every shelter
    private boolean checkIn() {
        if (CurrentUser.getCurrentUser().getCheckedIn()) {
            return false;
        } else {
            Shelter s = shelter;
            Log.d(TAG, String.valueOf(shelter.getAnyone()));
            if (!s.getAnyone()) {
                if ((boolean) s.getRestrictions().get("fam") && !CurrentUser.getCurrentUser().getFamily()) {
                    return false;
                }
                Log.w(TAG, "sex " + CurrentUser.getCurrentUser().getSex());
                if ((boolean) s.getRestrictions().get("men") && (!CurrentUser.getCurrentUser().getSex().equals("Male")
                    ||(CurrentUser.getCurrentUser().getSpouse() != null && CurrentUser.getCurrentUser().getSpouse().equals("Female")))) {
                    return false;
                }
                if ((boolean) s.getRestrictions().get("women") && ((!CurrentUser.getCurrentUser().getSex().equals("Female")
                    || (CurrentUser.getCurrentUser().getSpouse() != null && CurrentUser.getCurrentUser().getSpouse().equals("Male"))))) {
                    return false;
                }
                if ((boolean) s.getRestrictions().get("vets") && !CurrentUser.getCurrentUser().getVeteran()) {
                    return false;
                }
                if ((boolean) s.getRestrictions().get("children")) {
                    if (CurrentUser.getCurrentUser().getDependents() == 0) {
                        return false;
                    }
                    if (((Long)s.getRestrictions().get("child_age")).intValue() < CurrentUser.getCurrentUser().getYoungest()) {
                        return false;
                    }


                }
            }
            // They passed restrictions, check if the space is available
            if (CurrentUser.getCurrentUser().getFamily()) {
                if (s.getAvailable().get("rooms") != null && s.getAvailable().get("rooms") > 0) {
                    CurrentUser.getCurrentUser().setCurrentShelter(s.getKey());
                    CurrentUser.getCurrentUser().setCheckedIn(true);
                    Map<String, Integer> newAvaibility = new HashMap<>();
                    newAvaibility.put("rooms", s.getAvailable().get("rooms") - 1);
                    s.setAvailable(newAvaibility);
                    shelter = s;

                    return true;
                } else {
                    if (s.getAvailable().get("beds") != null && s.getAvailable().get("beds") >= CurrentUser.getCurrentUser().getFamilySize()) {
                        CurrentUser.getCurrentUser().setCurrentShelter(s.getKey());
                        CurrentUser.getCurrentUser().setCheckedIn(true);
                        Map<String, Integer> newAvaibility = new HashMap<>();
                        newAvaibility.put("beds", s.getAvailable().get("beds") - CurrentUser.getCurrentUser().getFamilySize());
                        s.setAvailable(newAvaibility);
                        shelter = s;
                        return true;
                    }
                }
            } else {
                if (s.getAvailable().get("rooms") != null && s.getAvailable().get("rooms") > 0) {
                    CurrentUser.getCurrentUser().setCurrentShelter(s.getKey());
                    CurrentUser.getCurrentUser().setCheckedIn(true);
                    Map<String, Integer> newAvaibility = new HashMap<>();
                    newAvaibility.put("rooms", s.getAvailable().get("rooms") - 1);
                    s.setAvailable(newAvaibility);
                    shelter = s;

                    return true;
                } else if (s.getAvailable().get("beds") != null && s.getAvailable().get("beds") >= CurrentUser.getCurrentUser().getFamilySize()) {
                    CurrentUser.getCurrentUser().setCurrentShelter(s.getKey());
                    CurrentUser.getCurrentUser().setCheckedIn(true);
                    Map<String, Integer> newAvaibility = new HashMap<>();
                    newAvaibility.put("beds", s.getAvailable().get("beds") - CurrentUser.getCurrentUser().getFamilySize());
                    s.setAvailable(newAvaibility);
                    shelter = s;
                    return true;
                }
            }

        }
        return false;
    }
    private boolean checkOut() {
        if (CurrentUser.getCurrentUser().getCheckedIn() && CurrentUser.getCurrentUser().getCurrentShelter() == shelter.getKey()) {
            CurrentUser.getCurrentUser().setCheckedIn(false);
            CurrentUser.getCurrentUser().setCurrentShelter(-1);
            Map<String, Integer> newAvaibility = new HashMap<>();
            Shelter s = getShelter();
            if (CurrentUser.getCurrentUser().getFamily()) {
                if (s.getCapacity().get("rooms") != null && s.getCapacity().get("rooms") > 0) {
                    newAvaibility.put("rooms", s.getAvailable().get("rooms") + 1);
                } else {
                    newAvaibility.put("beds", s.getAvailable().get("beds") + CurrentUser.getCurrentUser().getFamilySize());
                }
            } else {
                if (s.getCapacity().get("beds") != null && s.getCapacity().get("beds") > 0) {
                    newAvaibility.put("beds", s.getAvailable().get("beds") + CurrentUser.getCurrentUser().getFamilySize());
                } else {
                    newAvaibility.put("rooms", s.getAvailable().get("rooms") + 1);
                }
            }
            s.setAvailable(newAvaibility);
            shelter = s;
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
