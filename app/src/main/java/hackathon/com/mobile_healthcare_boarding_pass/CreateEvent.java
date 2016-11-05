package hackathon.com.mobile_healthcare_boarding_pass;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class CreateEvent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        Log.d("CreateEvent", name);

        final TextView text = (TextView) findViewById(R.id.event_text);
        text.setText(name);
    }

    public void book(View view) {
        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.addFlags(android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP | android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myIntent);
    }
    public void back(View view) {
        finish();
    }
}
