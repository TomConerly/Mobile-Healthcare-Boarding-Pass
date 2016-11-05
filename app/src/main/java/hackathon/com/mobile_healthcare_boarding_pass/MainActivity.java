package hackathon.com.mobile_healthcare_boarding_pass;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeAppointmentList();
    }

    public void schedule(View view) {
        // Kabloey
        Intent myIntent = new Intent(this, ScheduleAppointment.class);
        myIntent.putExtra("key", "test"); //Optional parameters
        startActivity(myIntent);
    }

    public void switchToJaro(View view) {
        Intent myIntent = new Intent(this, JaroSandbox.class);
        // myIntent.putExtra("key", "test"); //Optional parameters
        startActivity(myIntent);
    }

    private void initializeAppointmentList() {
        ListView appointment_list = (ListView)findViewById(R.id.appointment_list);


        List<Server.Slot> slots = Server.getInstance().getMyAppointments(1337);


        ArrayAdapter<Server.Slot> adapter = new ArrayAdapter<Server.Slot>(this, R.layout.appointment_slot, slots) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View res = getLayoutInflater().inflate(R.layout.appointment_slot, null);
                TextView appointment_name = (TextView)res.findViewById(R.id.appointment_name);
                TextView appointment_time = (TextView)res.findViewById(R.id.appointment_time);
                Server.Slot slot = getItem(position);
                appointment_name.setText("Appointment with " + slot.doctor);
                String eta = "in " + Utils.pretty_print_duration(slot.expectedStartTime.getTime() - (new Date()).getTime());

                long expected_time = slot.expectedStartTime.getTime();
                long scheduled_time = slot.scheduledStartTime.getTime();
                long diff = Math.abs(expected_time - scheduled_time);
                if (diff > 60 * 1000) {
                    if (scheduled_time < expected_time) {
                        eta += " (delayed by " + Utils.pretty_print_duration(diff) + ")";
                    } else {
                        eta += " ("  + Utils.pretty_print_duration(diff) + " earlier)";

                    }
                }
                appointment_time.setText( eta);
                return res;
            }
        };

        // Assign adapter to ListView
        appointment_list.setAdapter(adapter);
    }
}
