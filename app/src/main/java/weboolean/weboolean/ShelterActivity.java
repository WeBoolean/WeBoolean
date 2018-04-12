package weboolean.weboolean;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weboolean.weboolean.models.Shelter;
import weboolean.weboolean.models.User;

import static weboolean.weboolean.ShelterSingleton.getShelterArrayCopy;
import static weboolean.weboolean.ShelterSingleton.updateShelter;

/**
 * Activity which can show you the details of each shelter.
 */
public class ShelterActivity extends AppCompatActivity {
    private Shelter shelter;
    private static final String TAG = ShelterActivity.class.getSimpleName();

    // Listeners
    private final View.OnClickListener checkInButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(checkIn()) {
                updateShelter(shelter.getKey(), shelter);
                User u = CurrentUser.getCurrentUser();
                u.setCheckedIn(true);
                u.setCurrentShelter(shelter.getKey());
                CurrentUser.getCurrentUser().setCheckedIn(true);
                CurrentUser.updateUser(u);
                Toast.makeText(ShelterActivity.this, "Successful Check In!",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ShelterActivity.this,
                        "Check In Failed, make sure you meet all shelter restrictions " +
                                "& there is available space.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final View.OnClickListener checkOutButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(checkOut()) {
                updateShelter(shelter.getKey(), shelter);
                User u = CurrentUser.getCurrentUser();
                u.setCheckedIn(false);
                u.setCurrentShelter(-1);
                CurrentUser.updateUser(u);

                Toast.makeText(ShelterActivity.this, "Successful Check Out!",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ShelterActivity.this,
                        "Check Out Failed: have to be checked in to checkout",
                        Toast.LENGTH_LONG).show();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter);
        // FireBase references
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //DatabaseReference ref = database.getReference("shelters");
        // Get current shelter
        shelter = getShelter();

        // Create back button and listener
        final Button backButton = findViewById(R.id.backButton);
        final Button checkInButton = findViewById(R.id.checkInButton);
        final Button checkoutButton = findViewById(R.id.checkoutButton);

        checkInButton.setOnClickListener(checkInButtonListener);

        checkoutButton.setOnClickListener(checkOutButtonListener);

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
        if (shelter.getNote() != null) {
            note.setText(getString(R.string.notes, shelter.getNote()));
        }
        if (shelter.getPhoneNumber() != null) {
            number.setText(getString(R.string.number, shelter.getPhoneNumber()));
        }

        // [Restrictions] ========================================================================//
        // Anyone case
        if (shelter.getAnyone()) {
            anyone.setText(R.string.anyone_accepted);
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
                        anyone.setText(getString(R.string.women_children_restriction,
                            anyone.getText(), shelter.getRestrictions().get("child_age")));
                        // already set female
                        femaleRestriction = false;
                    } else {
                        anyone.setText(getString(R.string.children_restriction, anyone.getText(),
                            shelter.getRestrictions().get("child_age")));
                    }
                }
                else {
                    anyone.setText(getString(R.string.children_restriction,
                            anyone.getText(), "18"));
                }
            }

            if (familyRestriction) {
                anyone.setText(getString(R.string.families_restriction, anyone.getText()));
            }
            if (maleRestriction) {
                anyone.setText(getString(R.string.males_restriction, anyone.getText()));
            }
            if (femaleRestriction) {
                anyone.setText(getString(R.string.females_restriction, anyone.getText()));
            }
            if (veteranRestriction) {
                anyone.setText(getString(R.string.shelter_veterans_restriction, anyone.getText()));
            }
        }
    }

    private boolean checkInFailureTest1() {
        User currentUser = CurrentUser.getCurrentUser();
        Shelter s = shelter;
        if (!s.getAnyone()) {
            if ((boolean) s.getRestrictions().get("fam")
                    && !currentUser.getFamily()) {
                return false;
            }
            Log.w(TAG, "sex " + currentUser.getSex());
            if ((boolean) s.getRestrictions().get("men") && (!"Male"
                    .equals(currentUser.getSex())
                    || ((currentUser.getSpouse() != null) &&
                    "Female".equals(currentUser.getSpouse())))) {
                return false;
            }
        }
        return true;
    }

    private boolean checkInFailureTest2() {
        User currentUser = CurrentUser.getCurrentUser();
        Shelter s = shelter;
        if (!s.getAnyone()) {
            if ((boolean) s.getRestrictions().get("vets") &&
                    !currentUser.getVeteran()) {
                return false;
            }
            if ((boolean) s.getRestrictions().get("children")) {
                if (currentUser.getDependents() == 0) {
                    return false;
                }
                if (((Long)s.getRestrictions().get("child_age")).intValue()
                        < currentUser.getYoungest()) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkInFailureTest3() {
        User currentUser = CurrentUser.getCurrentUser();
        Shelter s = shelter;
        if (!s.getAnyone()) {
            if ((boolean) s.getRestrictions().get("women") && ((!"Female"
                    .equals(currentUser.getSex())
                    || ((currentUser.getSpouse() != null)
                    && "Male".equals(currentUser.getSpouse()))))) {
                return false;
            }
        }
        return true;
    }

    private boolean checkInFamily() {
        User currentUser = CurrentUser.getCurrentUser();
        Shelter s = shelter;
        if ((s.getAvailable().get("rooms") != null)
                && (s.getAvailable().get("rooms") > 0)) {
            currentUser.setCurrentShelter(s.getKey());
            currentUser.setCheckedIn(true);
            Map<String, Integer> newAvailability = new HashMap<>();
            newAvailability.put("rooms", s.getAvailable().get("rooms") - 1);
            s.setAvailable(newAvailability);
            shelter = s;
            return true;
        } else {
            if ((s.getAvailable().get("beds") != null) &&
                    (s.getAvailable().get("beds")
                            >= currentUser.getFamilySize())) {
                currentUser.setCurrentShelter(s.getKey());
                currentUser.setCheckedIn(true);
                Map<String, Integer> newAvailability = new HashMap<>();
                newAvailability.put("beds", s.getAvailable().get("beds")
                        - currentUser.getFamilySize());
                s.setAvailable(newAvailability);
                shelter = s;
                return true;
            }
        }
        return false;
    }

    private boolean checkInNonFamily() {
        User currentUser = CurrentUser.getCurrentUser();
        Shelter s = shelter;
        if ((s.getAvailable().get("rooms") != null)
                && (s.getAvailable().get("rooms") > 0)) {
            currentUser.setCurrentShelter(s.getKey());
            currentUser.setCheckedIn(true);
            Map<String, Integer> newAvailability = new HashMap<>();
            newAvailability.put("rooms", s.getAvailable().get("rooms") - 1);
            s.setAvailable(newAvailability);
            shelter = s;

            return true;
        } else if ((s.getAvailable().get("beds") != null)
                && (s.getAvailable().get("beds")
                >= currentUser.getFamilySize())) {
            currentUser.setCurrentShelter(s.getKey());
            currentUser.setCheckedIn(true);
            Map<String, Integer> newAvailability = new HashMap<>();
            newAvailability.put("beds", s.getAvailable().get("beds")
                    - currentUser.getFamilySize());
            s.setAvailable(newAvailability);
            shelter = s;
            return true;
        }
        return false;
    }

    private boolean checkIn() {
        User currentUser = CurrentUser.getCurrentUser();
        if (currentUser.getCheckedIn()) {
            return false;
        } else {
            Log.d(TAG, String.valueOf(shelter.getAnyone()));
            if (!checkInFailureTest1() || !checkInFailureTest2() || checkInFailureTest3()) {
                return false;
            }
            // They passed restrictions, check if the space is available
            if (currentUser.getFamily()) {
                if (checkInFamily()) {
                    return true;
                }
            } else {
                if (checkInNonFamily()) {
                    return true;
                }
            }

        }
        return false;
    }
    private boolean checkOut() {
        User currentUser = CurrentUser.getCurrentUser();
        if (currentUser.getCheckedIn()
                && (currentUser.getCurrentShelter() == shelter.getKey())) {
            currentUser.setCheckedIn(false);
            currentUser.setCurrentShelter(-1);
            Map<String, Integer> newAvailability = new HashMap<>();
            Shelter s = getShelter();
            if (currentUser.getFamily()) {
                if ((s.getCapacity().get("rooms") != null) && (s.getCapacity().get("rooms") > 0)) {
                    newAvailability.put("rooms", s.getAvailable().get("rooms") + 1);
                } else {
                    newAvailability.put("beds", s.getAvailable().get("beds")
                            + currentUser.getFamilySize());
                }
            } else {
                if ((s.getCapacity().get("beds") != null) && (s.getCapacity().get("beds") > 0)) {
                    newAvailability.put("beds", s.getAvailable().get("beds")
                            + currentUser.getFamilySize());
                } else {
                    newAvailability.put("rooms", s.getAvailable().get("rooms") + 1);
                }
            }
            s.setAvailable(newAvailability);
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
        return shelterList.get(numberData);
    }

    private void setCapacities(Shelter shelter, TextView available, TextView capacity) {
        // [Availability] =========================================================================/
        setAvailability_hidden(shelter, available);
        // [Capacity] =============================================================================/
        setCapacity_hidden(shelter, capacity);
    }

    private void setAvailability_hidden(Shelter shelter, TextView available) {
        if (shelter.getAvailable().get("beds") != null) {
            available.setText("" + shelter.getAvailable().get("beds") + " Beds available ");
        } else {
            available.setText("");
        }
        if ((shelter.getAvailable().get("rooms") != null) &&
                (shelter.getAvailable().get("rooms") != 0)) {
            available.setText(available.getText() + "" + shelter.getAvailable().get("rooms") +
                    " Rooms available");
        }
        if ((shelter.getAvailable().get("rooms") != null) &&
                (shelter.getAvailable().get("rooms") == 0)) {
            available.setText("N/A Rooms available");
        }
    }
    private void setCapacity_hidden(Shelter shelter, TextView capacity) {
        if ((shelter.getCapacity().get("beds") != null) &&
                (shelter.getCapacity().get("beds") != 0)) {
            capacity.setText("" + shelter.getCapacity().get("beds") + " Bed capacity ");
        } else if ((shelter.getCapacity().get("beds") != null) &&
                (shelter.getCapacity().get("beds") == 0)){
            capacity.setText("N/A Beds Capacity \n");
        } else {
            capacity.setText("");
        }
        if ((shelter.getCapacity().get("rooms") != null) &&
                (shelter.getCapacity().get("rooms") != 0)) {
            capacity.setText(capacity.getText() + "" + shelter.getCapacity().get("rooms") +
                    " Room capacity");
        } else if ((shelter.getCapacity().get("rooms") != null) &&
                (shelter.getCapacity().get("rooms") == 0)) {
            capacity.setText("N/A Room capacity");
        }
    }


}
