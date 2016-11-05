package hackathon.com.mobile_healthcare_boarding_pass;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

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

    public void login(View view) {
        EditText et = (EditText)findViewById(R.id.patiendIdBox);
        TextView tv = (TextView)findViewById(R.id.textView2);
        UserData.patientId = Integer.parseInt(et.getText().toString());
        String myToken = FirebaseInstanceId.getInstance().getToken();
        JSONObject obj = null;
        try {
            obj = new JSONObject();
            obj.put("action", "setToken");
            obj.put("patientId", UserData.patientId);
            obj.put("token", myToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONRequestTask myClientTask = new JSONRequestTask(Constants.SERVER_ADDR, Constants.SERVER_PORT, obj) {
            @Override
            protected void onSuccessfulRequest(JSONObject response) {
                super.onSuccessfulRequest(response);
                try {
                    Log.d("tag", "LIST " + response.getString("test"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        myClientTask.execute();
    }

}
