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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter);

        final Button backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShelterActivity.this, WelcomeActivity.class);
                startActivity(intent);
            }
        });

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("shelters");

        Intent intent = getIntent();
        int numberData = intent.getIntExtra("Shelter", -1);
        List<Shelter> shelterList = getShelterArrayCopy();
        Shelter shelter = shelterList.get(numberData);

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
}
