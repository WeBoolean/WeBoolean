package weboolean.weboolean;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShelterActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter);

        database = FirebaseDatabase.getInstance();
        ref = database.getReference("shelters");

        Intent intent = getIntent();
        int numberData = intent.getIntExtra("Shelter", -1);
        TextView myAwesomeTextView = (TextView)findViewById(R.id.textView);
        myAwesomeTextView.setText("Shelter " + numberData);
    }


}
