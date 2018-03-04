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
        ArrayList<Shelter> shelterList = getShelterArrayCopy();
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
            anyone.setText("Anyone is allowed");
        } else {
            anyone.setText("Restriction on who is allowed \n\n");
            if ((boolean) shelter.getRestrictions().get("children")) {
                anyone.setText(anyone.getText() + "Children are allowed \n");
            } else {
                anyone.setText(anyone.getText() + "Children are not allowed \n");
            }
            if (shelter.getRestrictions().get("child_age") != null) {
                anyone.setText(anyone.getText() + "(Children are considered to be age " + shelter.getRestrictions().get("child_age") + " and under) \n");
            }
            if ((boolean) shelter.getRestrictions().get("fam")) {
                anyone.setText(anyone.getText() + "Families are allowed \n");
            } else {
                anyone.setText(anyone.getText() + "Families are not allowed \n");
            }
            if ((boolean) shelter.getRestrictions().get("men")) {
                anyone.setText(anyone.getText() + "Men are allowed \n");
            } else {
                anyone.setText(anyone.getText() + "Men are not allowed \n");
            }
            if ((boolean) shelter.getRestrictions().get("women")) {
                anyone.setText(anyone.getText() + "Women are allowed \n");
            } else {
                anyone.setText(anyone.getText() + "Women are not allowed \n");
            }
            if ((boolean) shelter.getRestrictions().get("vets")) {
                anyone.setText(anyone.getText() + "Veterans are allowed \n");
            } else {
                anyone.setText(anyone.getText() + "Veterans are not allowed \n");
            }
        }
    }
}
