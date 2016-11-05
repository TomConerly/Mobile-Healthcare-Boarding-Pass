package hackathon.com.mobile_healthcare_boarding_pass;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Notification.Builder builder = new Notification.Builder(this);

        if (remoteMessage.getNotification() != null) {
            builder.setContentTitle(remoteMessage.getNotification().getTitle().concat(" from App"))
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setSmallIcon(R.drawable.notification_icon);
        }
        else
        {
            builder.setContentTitle("Health Boarding Pass")
                    .setContentText(remoteMessage.getData().get("message"))
                    .setSmallIcon(R.drawable.notification_icon);
        }
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(0, builder.build());
    }

}
