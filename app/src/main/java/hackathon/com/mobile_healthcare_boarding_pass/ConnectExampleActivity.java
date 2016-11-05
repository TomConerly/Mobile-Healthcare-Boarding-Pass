package hackathon.com.mobile_healthcare_boarding_pass;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

public class ConnectExampleActivity extends Activity {
    TextView textResponse;
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_example);

        buttonConnect = (Button)findViewById(R.id.connect);
        buttonClear = (Button)findViewById(R.id.clear);
        textResponse = (TextView)findViewById(R.id.response);

        buttonConnect.setOnClickListener(buttonConnectOnClickListener);

        buttonClear.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                textResponse.setText("");
            }});
    }

    OnClickListener buttonConnectOnClickListener =
            new OnClickListener(){

                @Override
                public void onClick(View arg0) {
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject();
                        obj.put("action", "test");
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
                }};

}
