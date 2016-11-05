package hackathon.com.mobile_healthcare_boarding_pass;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.*;
import android.view.View;
import android.widget.CheckBox;

import com.github.sundeepk.compactcalendarview.domain.Event;

public class EventActivity extends AppCompatActivity {
    private ActionBar toolbar;
    private Server.Slot slot;
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("hh:mm a 'on' MMM dd", Locale.getDefault());
    private SimpleDateFormat dateFormatForDisplaying2 = new SimpleDateFormat("hh:mm a 'on' MMM dd", Locale.getDefault());
    private List<Server.Slot> my_slots;
    ArrayAdapter<Server.Slot> my_slots_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("tconerly", "onCreate");

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
        String content = "Your appointment is with " + slot.doctor +".";
        content += "\nScheduled start time: " + dateFormatForDisplaying.format(slot.scheduledStartTime);
        if (slot.scheduledStartTime.compareTo(slot.expectedStartTime) != 0) {
            long expected_time = slot.expectedStartTime.getTime();
            long scheduled_time = slot.scheduledStartTime.getTime();
            long diff = Math.abs(expected_time - scheduled_time);
            if (diff > 60 * 1000) {
                if (scheduled_time < expected_time) {
                    content += "\nDelayed by " + Utils.pretty_print_duration(diff);
                } else {
                    content += "\n"  + Utils.pretty_print_duration(diff) + " earlier";

                }
            }
        }
        text.setText(content);

        initializeAppointmentList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        my_slots.clear();
        my_slots.addAll(Server.getInstance().getBookedAppointments());
        Collections.sort(my_slots, new Comparator<Server.Slot>() {
            @Override
            public int compare(Server.Slot e1, Server.Slot e2) {
                return e1.scheduledStartTime.compareTo(e2.scheduledStartTime);
            }
        });
        Log.d("siema", "slots: " + my_slots.size());
        my_slots_adapter.notifyDataSetChanged();

    }
    public void checkBox(View view) {

        boolean checked = ((CheckBox)view).isChecked();
        View parentRow = (View)((View) view.getParent()).getParent();
        ListView listView = (ListView) parentRow.getParent();
        final int position = listView.getPositionForView(parentRow);
        Log.d("tconerly", String.format("checked %d %d", position, checked ? 1 : 0));
    }
    public void cancel(View view) {
        Server.getInstance().cancel(slot.slotId, 1337);
        finish();
    }

    private void initializeAppointmentList() {
        Log.d("tconerly", "initializeAppointmentList");
        ListView appointment_list = (ListView)findViewById(R.id.swap_appointment_list);

        my_slots = Server.getInstance().getBookedAppointments();
        Collections.sort(my_slots, new Comparator<Server.Slot>() {
            @Override
            public int compare(Server.Slot e1, Server.Slot e2) {
                return e1.scheduledStartTime.compareTo(e2.scheduledStartTime);
            }
        });

        my_slots_adapter = new ArrayAdapter<Server.Slot>(this, R.layout.swap_slot, my_slots) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View res = getLayoutInflater().inflate(R.layout.swap_slot, null);
                TextView appointment_name = (TextView)res.findViewById(R.id.swap_appointment_name);
                TextView appointment_time = (TextView)res.findViewById(R.id.swap_appointment_time);
                Server.Slot slot = getItem(position);
                appointment_name.setText("Appointment with " + slot.doctor);
                String time = dateFormatForDisplaying2.format(slot.scheduledStartTime);

                appointment_time.setText(time);
                return res;
            }
        };

        appointment_list.setAdapter(my_slots_adapter);

    }
}
