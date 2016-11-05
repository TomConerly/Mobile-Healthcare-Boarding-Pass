package hackathon.com.mobile_healthcare_boarding_pass;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by sidor on 11/5/16.
 */

public class JSONRequestTask extends AsyncTask<Void, Void, Void> {

    String dstAddress;
    int dstPort;
    String request;
    String response = "";

    JSONRequestTask(String addr, int port, JSONObject json_request) {
        dstAddress = addr;
        dstPort = port;
        request = json_request.toString();
    }

    @Override
    protected Void doInBackground(Void... arg0) {

        Socket socket = null;

        try {
            socket = new Socket(dstAddress, dstPort);
            OutputStream os = socket.getOutputStream();

            byte[] bytes = request.getBytes();
            os.write(bytes);

            ByteArrayOutputStream byteArrayOutputStream =
                    new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];

            int bytesRead;
            InputStream inputStream = socket.getInputStream();

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.reset();
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                response += byteArrayOutputStream.toString("UTF-8");
            }

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        try {
            onSuccessfulRequest(new JSONObject(response));
        } catch (JSONException e) {
            Log.d("tag", "failed to parse response: " + response);
            onFailedRequest();
        }
    }

    protected void onSuccessfulRequest(JSONObject response) {
    }

    protected void onFailedRequest() {
    }
}