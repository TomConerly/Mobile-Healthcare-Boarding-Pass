package hackathon.com.mobile_healthcare_boarding_pass;

import javax.xml.datatype.Duration;

/**
 * Created by sidor on 11/5/16.
 */

public class Utils {
    static String pretty_print_duration(long d) {
        long minutes = d / (60 * 1000);
        long hours = minutes / 60;
        long days = hours / 24;
        if (days > 0) {
            return "in " + days + " days";
        } else if (hours > 0) {
            return "in " + hours + " hours";
        } else if (minutes> 0) {
            return "in " + minutes + " minutes";
        } else {
            return "less than a minute";
        }
    }
}
