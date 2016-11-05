package hackathon.com.mobile_healthcare_boarding_pass;

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

    List<Slot> getFreeAppointments() {
        ArrayList<Slot> res = new ArrayList<Slot>();
        for (Slot s : allSlots)
            if (s.patientId == FREE)
                res.add(s);
        return res;
    }
    List<Slot> getMyAppointments(int patientId) {
        ArrayList<Slot> res = new ArrayList<Slot>();
        for (Slot s : allSlots)
            if (s.patientId == patientId)
                res.add(s);
        return res;
    }
    // Slots that are booked by someone else
    List<Slot> getBookedAppointments() {
        ArrayList<Slot> res = new ArrayList<Slot>();
        for (Slot s : allSlots)
            if (s.patientId == BOOKED)
                res.add(s);
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
            s.doctor = "Doctor Mc. Doctorface";
            s.patientId = 1337;
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
