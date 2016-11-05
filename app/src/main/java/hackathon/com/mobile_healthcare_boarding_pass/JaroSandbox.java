package hackathon.com.mobile_healthcare_boarding_pass;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

public class JaroSandbox extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jaro_sandbox);

    }

    public void showNotification(View view)  {
        String myToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("tag", myToken);
        TextView tv = (TextView)findViewById(R.id.textView2);
        if(tv != null) {
            tv.setText(myToken);
        }
    }

}
