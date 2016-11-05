package hackathon.com.mobile_healthcare_boarding_pass;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Server {
    private final int FREE = -1;
    private final int BOOKED = -2;
    class Slot {
        int slotId;
        int patientId; // -1 indicates free, -2 indicates another patient
        Date scheduledStartTime;
        Date expectedStartTime;
        Date scheduledEndTime;
        Date expectedEndTime;
        String doctor;
    }

    void updateSlotsFromServer() {
        JSONObject obj = null;
        try {
            obj = new JSONObject();
            obj.put("action", "list_slots");
            obj.put("patientId", Constants.PATIENT_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONRequestTask myClientTask = new JSONRequestTask(Constants.SERVER_ADDR, Constants.SERVER_PORT, obj) {
            @Override
            protected void onSuccessfulRequest(JSONObject response) {
                super.onSuccessfulRequest(response);
                allSlots.clear();
                Log.d("tag", "hello!");
                try {
                    JSONArray slots = response.getJSONArray("slots");
                    for (int i = 0; i < slots.length(); ++i) {
                        allSlots.add(parseSlot(slots.getJSONObject(i)));
                    }
                    Log.d("tag", "parsed " + allSlots.size() + " slots");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onFailedRequest() {
                Log.d("tag", "doom!");
            }
        };
        myClientTask.execute();
    }

    private Slot parseSlot(JSONObject obj) {
        try {
            Slot slot = new Slot();
            slot.slotId = obj.getInt("slotId");
            slot.patientId = obj.getInt("patientId");
            slot.scheduledStartTime = new Date(1000 * obj.getLong("scheduledStartTime"));
            slot.expectedStartTime = new Date(1000 * obj.getLong("expectedStartTime"));
            slot.scheduledEndTime = new Date(1000 * obj.getLong("scheduledEndTime"));
            slot.expectedEndTime = new Date(1000 * obj.getLong("expectedEndTime"));
            slot.doctor = obj.getString("provider");
            return slot;
        } catch (JSONException e) {
            return null;
        }
    }


    List<Slot> getFreeAppointments() {
        ArrayList<Slot> res = new ArrayList<Slot>();
        for (Slot s : allSlots)
            if (s.patientId == FREE)
                res.add(s);
        Log.d("tconerly", "num free" + Integer.toString(res.size()));
        return res;
    }
    List<Slot> getMyAppointments(int patientId) {
        ArrayList<Slot> res = new ArrayList<Slot>();
        for (Slot s : allSlots)
            if (s.patientId == patientId)
                res.add(s);
        Log.d("tconerly", "num mine" + Integer.toString(res.size()));
        return res;
    }
    // Slots that are booked by someone else
    List<Slot> getBookedAppointments() {
        ArrayList<Slot> res = new ArrayList<Slot>();
        for (Slot s : allSlots)
            if (s.patientId == BOOKED)
                res.add(s);
        Log.d("tconerly", "num booked" + Integer.toString(res.size()));
        return res;
    }
    boolean takeAppointment(int slotId, int patientId) {
        for (Slot s : allSlots) {
            if (s.slotId == slotId) {
                if (s.patientId == FREE) {
                    s.patientId = patientId;
                    return true;
                }
            }
        }
        return false;
    }
    void cancel(int slotId, int patientId) {
        for (Slot s : allSlots) {
            if (s.slotId == slotId && s.patientId == patientId) {
                s.patientId = FREE;
            }
        }
    }


    private static Server instance = null;
    private static boolean useRealServer = false;
    private static List<Slot> allSlots;

    static Calendar randomTime(Random r) {
        Calendar c = Calendar.getInstance(Locale.getDefault());
        c.setTime(new Date());
        int date = r.nextInt(30)+1;
        int hour = r.nextInt(8) + 9;
        int minute = r.nextBoolean() ? 30 : 0;
        c.set(2016, Calendar.NOVEMBER, date, hour, minute);
        return c;
    }
    protected Server() {
        Random r = new Random(0L);
        allSlots = new ArrayList<Slot>();
        int numOtherSlots = 50;
        for (int i = 0; i < numOtherSlots; i++) {
            Calendar c = randomTime(r);
            Slot s = new Slot();
            s.scheduledStartTime = c.getTime();
            s.expectedStartTime = c.getTime();
            c.add(Calendar.MINUTE, 30);
            s.scheduledEndTime = c.getTime();
            s.expectedEndTime = c.getTime();
            s.doctor = "Doctor Mc. Doctorface";
            s.patientId = r.nextInt(5) == 0 ? FREE : BOOKED;
            s.slotId = i;
            allSlots.add(s);
        }
        for (int i = 0; i < 3; i++) {
            Calendar c = randomTime(r);
            Slot s = new Slot();
            s.scheduledStartTime = c.getTime();
            c.add(Calendar.MINUTE, 5);
            s.expectedStartTime = c.getTime();
            c.add(Calendar.MINUTE, 30);
            s.scheduledEndTime = c.getTime();
            c.add(Calendar.MINUTE, 5);
            s.expectedEndTime = c.getTime();
            if (i == 2) {
                c = Calendar.getInstance(Locale.getDefault());
                c.setTime(s.expectedStartTime);
                c.add(Calendar.HOUR, 2);
                s.expectedStartTime = c.getTime();
                c.setTime(s.expectedEndTime);
                c.add(Calendar.HOUR, 2);
                s.expectedEndTime = c.getTime();
            }
            s.doctor = "Doctor Mc. Doctorface";
            s.patientId = Constants.PATIENT_ID;
            s.slotId = i + numOtherSlots;
            allSlots.add(s);
        }
    }

    public static Server getInstance() {
        if(instance == null) {
            instance = new Server();
        }
        return instance;
    }
}
