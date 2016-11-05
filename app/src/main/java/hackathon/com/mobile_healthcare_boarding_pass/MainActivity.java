package hackathon.com.mobile_healthcare_boarding_pass;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.*;


public class MainActivity extends AppCompatActivity {
    private ActionBar toolbar;
    private List<Server.Slot> my_slots;
    ArrayAdapter<Server.Slot> my_slots_adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeAppointmentList();

        toolbar = getSupportActionBar();
        toolbar.setTitle("Your Appointments");

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        my_slots.clear();
                        Server.getInstance().updateSlotsFromServer();
                        my_slots.addAll(Server.getInstance().getMyAppointments(Constants.PATIENT_ID));
                        sortMySlots();
                        my_slots_adapter.notifyDataSetChanged();
                    }
                });
            }

        },0,1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        my_slots.clear();
        Server.getInstance().updateSlotsFromServer();
        my_slots.addAll(Server.getInstance().getMyAppointments(Constants.PATIENT_ID));
        sortMySlots();
        Log.d("siema", "slots: " + my_slots.size());
        my_slots_adapter.notifyDataSetChanged();

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
    public void switchToConnectExample(View view) {
        Intent myIntent = new Intent(this, ConnectExampleActivity.class);
        // myIntent.putExtra("key", "test"); //Optional parameters
        startActivity(myIntent);
    }
    private void sortMySlots() {
        Collections.sort(my_slots, new Comparator<Server.Slot>() {
            @Override
            public int compare(Server.Slot e1, Server.Slot e2) {
                return e1.scheduledStartTime.compareTo(e2.scheduledStartTime);
            }
        });
    }
    private void initializeAppointmentList() {
        ListView appointment_list = (ListView)findViewById(R.id.appointment_list);
        Server.getInstance().updateSlotsFromServer();
        my_slots = Server.getInstance().getMyAppointments(Constants.PATIENT_ID);
        sortMySlots();

        my_slots_adapter = new ArrayAdapter<Server.Slot>(this, R.layout.appointment_slot, my_slots) {
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


        final MainActivity act = this;
        appointment_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Server.Slot slot = my_slots_adapter.getItem(position);

                Intent myIntent = new Intent(act, EventActivity.class);
                myIntent.putExtra("slotId", slot.slotId);
                myIntent.putExtra("doctor", slot.doctor);
                startActivity(myIntent);
            }
        });

        appointment_list.setAdapter(my_slots_adapter);

    }
}
