package hackathon.com.mobile_healthcare_boarding_pass;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

        String[] values = new String[] { "Android List View",
                "Adapter implementation",
                "Simple List View In Android",
                "Create List View Android",
                "Android Example",
                "List View Source Code",
                "List View Array Adapter",
                "Android Example List View"
        };


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);


        // Assign adapter to ListView
        appointment_list.setAdapter(adapter);
    }
}
