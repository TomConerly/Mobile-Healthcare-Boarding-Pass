package hackathon.com.mobile_healthcare_boarding_pass;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.*;
import android.view.View;

public class EventActivity extends AppCompatActivity {
    private ActionBar toolbar;
    private Server.Slot slot;
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("hh:mm a 'on' MMM dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Intent intent = getIntent();
        int slotId = intent.getIntExtra("slotId", -1);
        Server server = Server.getInstance();
        List<Server.Slot> apts = server.getMyAppointments(1337);
        for (Server.Slot s : apts)
            if (s.slotId == slotId)
                slot = s;

        toolbar = getSupportActionBar();
        toolbar.setTitle("Appointment");

        final TextView text = (TextView) findViewById(R.id.event_text);
        String content ="Your appointment is scheduled for " + dateFormatForDisplaying.format(slot.scheduledStartTime) + " with " + slot.doctor +".";
        if (slot.scheduledStartTime.compareTo(slot.expectedStartTime) != 0)
            content += " The expected start time is " + dateFormatForDisplaying.format(slot.expectedStartTime)+ ".";
        text.setText(content);
    }
    public void swapCheckBox(View view) {
        Log.d("eventactivity", "swapCheckBox");
    }
}
