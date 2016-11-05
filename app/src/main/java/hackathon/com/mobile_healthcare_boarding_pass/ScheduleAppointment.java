package hackathon.com.mobile_healthcare_boarding_pass;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.SimpleDateFormat;
import java.util.*;

public class ScheduleAppointment extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("hh:mm a 'on' MMM dd", Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
    private boolean shouldShow = false;
    private CompactCalendarView compactCalendarView;
    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_appointment);

        final List<String> mutableBookings = new ArrayList<>();

        final ListView bookingsListView = (ListView) findViewById(R.id.bookings_listview);
        bookingsListView.setClickable(true);

        final ArrayAdapter adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, mutableBookings);
        bookingsListView.setAdapter(adapter);

        final ScheduleAppointment act = this;

        bookingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String s = (String)bookingsListView.getItemAtPosition(position);
                android.util.Log.d("click", String.format("position %d name %s", position, s));

                Intent myIntent = new Intent(act, CreateEvent.class);
                myIntent.putExtra("name", s);
                Server.Slot slot = currentListView.get(position);
                myIntent.putExtra("slotId", slot.slotId);
                myIntent.putExtra("doctor", slot.doctor);
                act.startActivity(myIntent);
            }
        });

        compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);

        // below allows you to configure color for the current day in the month
        // compactCalendarView.setCurrentDayBackgroundColor(getResources().getColor(R.color.black));
        // below allows you to configure colors for the current day the user has selected
        // compactCalendarView.setCurrentSelectedDayBackgroundColor(getResources().getColor(R.color.dark_red));

        Server s = Server.getInstance();
        List<Server.Slot> free = s.getFreeAppointments();
        List<Event> events = new ArrayList<Event>();
        for (Server.Slot slot : free) {
            events.add(new Event(Color.argb(255, 169, 68, 65), slot.expectedStartTime.getTime(), slot));
        }
        compactCalendarView.addEvents(events);

        compactCalendarView.invalidate();
        compactCalendarView.setDayColumnNames(new String[]{"M", "Tu", "W", "Th", "F", "Sa", "Su"});

        //set initial title
        toolbar = getSupportActionBar();
        toolbar.setTitle(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

        //set title on calendar scroll
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                toolbar.setTitle("Appointments for " + dateFormatForMonth.format(dateClicked));
                List<Event> bookingsFromMap = compactCalendarView.getEvents(dateClicked);
                Collections.sort(bookingsFromMap, new Comparator<Event>() {
                    @Override
                    public int compare(Event e1, Event e2) {
                        if (e1.getTimeInMillis() < e2.getTimeInMillis()) {
                            return -1;
                        } else if (e1.getTimeInMillis() > e2.getTimeInMillis()) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                });
                Log.d(TAG, "inside onclick " + dateFormatForDisplaying.format(dateClicked));

                if (bookingsFromMap != null) {
                    currentListView = new ArrayList<Server.Slot>();
                    Log.d(TAG, bookingsFromMap.toString());
                    mutableBookings.clear();
                    for (Event booking : bookingsFromMap) {
                        mutableBookings.add(makeName(booking.getTimeInMillis()));
                        currentListView.add((Server.Slot)booking.getData());
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                toolbar.setTitle("Appointments for " + dateFormatForMonth.format(firstDayOfNewMonth));
            }
        });

        compactCalendarView.shouldDrawIndicatorsBelowSelectedDays(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setTitle("Appointments for " + dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
        // Set to current day on resume to set calendar to latest day
        // toolbar.setTitle(dateFormatForMonth.format(new Date()));
    }

    private String makeName(long timeInMillis) {
        return "Appointment at " + dateFormatForDisplaying.format(new Date(timeInMillis));
    }
    List<Server.Slot> currentListView = new ArrayList<Server.Slot>();
}
