package hackathon.com.mobile_healthcare_boarding_pass;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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

            if(remoteMessage.getData() != null &&
                    remoteMessage.getData().get("uber") != null &&
                    remoteMessage.getData().get("uber").equals("true")) {
                builder.addAction(R.drawable.notification_icon, "Order Uber",
                        PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), PendingIntent.FLAG_ONE_SHOT));
            }
        }
        else
        {
            builder.setContentTitle("Health Boarding Pass")
                    .setContentText(remoteMessage.getData().get("message"))
                    .setSmallIcon(R.drawable.notification_icon);

            if(remoteMessage.getData().get("uber") != null && remoteMessage.getData().get("uber").equals("true")) {
                builder.addAction(R.drawable.notification_icon, "Order Uber",
                        PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), PendingIntent.FLAG_ONE_SHOT));
            }

        }
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(0, builder.build());
    }

}
