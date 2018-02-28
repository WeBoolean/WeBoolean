package weboolean.weboolean;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class shelterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter);

        Intent intent = getIntent();
        int numberData = intent.getIntExtra("Shelter", -1);
        TextView myAwesomeTextView = (TextView)findViewById(R.id.textView);
        myAwesomeTextView.setText("Shelter " + numberData);
    }
}
