package hackathon.com.mobile_healthcare_boarding_pass;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class CreateEvent extends AppCompatActivity {
    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        Log.d("CreateEvent", name);
        slotId = intent.getIntExtra("slotId", -1);
        String doctor = intent.getStringExtra("doctor");

        final TextView text = (TextView) findViewById(R.id.event_text);
        text.setText(name + "\nWith " + doctor);

        toolbar = getSupportActionBar();
        toolbar.setTitle("Schedule Appointment");

    }
    int slotId;

    public void book(View view) {
        Server s = Server.getInstance();
        s.takeAppointment(slotId, 1337);
        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.addFlags(android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP | android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myIntent);
    }
    public void back(View view) {
        finish();
    }
}
