package com.itra.itraremotemedicationapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.gcm.GcmListenerService;


public class mGcmListenerService extends GcmListenerService
    {

        private static final String TAG = "mGCMListenerService";

        //Recieves GCM Messages

        /**
         * Called when message is received.
         *
         * @param from SenderID of the sender.
         * @param data Data bundle containing message data as key/value pairs.
         *             For Set of keys use data.keySet().
         */
        // [START receive_message]
        @Override
        public void onMessageReceived(String from, Bundle data)
            {
//                if (from.startsWith("/topics/"))
//                {
//                    // message received from some topic.
//                }
//                else
//                {
//                    // normal downstream message.
//                }

                String message = "hello"; //data.getString("foo");
                try {
                    String NotificationType = data.getString("Type");

                    if (NotificationType != null && NotificationType.equalsIgnoreCase("Test"))
                    {
                        //TODO launch test notification
                        Toast.makeText(mGcmListenerService.this, "TestPush", Toast.LENGTH_SHORT).show();
                    }
                    else if (NotificationType != null && NotificationType.equalsIgnoreCase("PrescriptionRequest"))
                    {
                        launchPrescriptionActivity(data);
                        //PrescriptionActivity is used for Doctors to write prescriptions
                    }
                    else if (NotificationType != null && NotificationType.equalsIgnoreCase("PatientPrescription"))
                    {
                        //TODO launch AdviceActivity
                        launchReadPrescriptionActivity(data);
                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(mGcmListenerService.this, e.toString(), Toast.LENGTH_SHORT).show();
                }

            }

        private void launchReadPrescriptionActivity(Bundle data) {
            Context context = getApplicationContext();
            Intent notificationIntent = new Intent(context, ReadPrescriptionActivity.class);
            notificationIntent.putExtras(data);

            PendingIntent contentIntent = PendingIntent.getActivity(context,
                    1, notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            NotificationManager nm = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            Resources res = context.getResources();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.ic_doctor_notification)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_doctor_notification))
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentTitle("Medical Alert");
            Notification n = builder.build();
            n.defaults |= Notification.DEFAULT_ALL;
            nm.notify(1, n);
        }

        private void launchPrescriptionActivity(Bundle data) {
            Context context = getApplicationContext();
            Intent notificationIntent = new Intent(context, PrescriptionActivity.class);
            notificationIntent.putExtras(data);

            PendingIntent contentIntent = PendingIntent.getActivity(context,
                    0, notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);


            NotificationManager nm = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            Resources res = context.getResources();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.ic_doctor_notification)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_doctor_notification))
//                    .setTicker(payload)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentTitle("Medical Alert");
//                    .setContentText(payload);
            Notification n = builder.build();

            n.defaults |= Notification.DEFAULT_ALL;
            nm.notify(0, n);
        }


        // [END receive_message]
        public void sendNotification(String message)
            {
                Intent intent = new Intent(this, PrescriptionActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                        PendingIntent.FLAG_ONE_SHOT);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
//                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                notificationBuilder.setSmallIcon(R.drawable.ic_doctor_notification);
                notificationBuilder.setContentTitle("Emergency");
                notificationBuilder.setContentText("Patient Status");
//                notificationBuilder.setAutoCancel(true);
//                notificationBuilder.setSound(defaultSoundUri);
//                notificationBuilder.setContentIntent(pendingIntent);

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
            }


    }
