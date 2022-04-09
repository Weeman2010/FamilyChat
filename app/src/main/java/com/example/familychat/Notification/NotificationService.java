package com.example.familychat.Notification;




import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.familychat.Activitys.ChatActivity;
import com.example.familychat.Activitys.InComingVideoCallActivity;
import com.example.familychat.Activitys.InComingVideoCall_accept_Activity;
import com.example.familychat.Activitys.OutgoingVideoCallActivity;
import com.example.familychat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

    public class NotificationService extends FirebaseMessagingService {
        public static boolean isRunning=false;
        private static final String TAG = "NOTIFICATION";

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

            if (remoteMessage.getData().size() > 0) {
                Map<String, String> map = remoteMessage.getData();
                String type= map.get("Type");
                String name= map.get("Name");
                String message = map.get("message");
                String senderID=map.get("sender");
                String imageURL=map.get("image");
                Log.d(TAG, "onMessageReceived: "+type);
                if (type.equals("ringing")){
                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager
                            .getInstance(NotificationService.this);
                    localBroadcastManager.sendBroadcast(new Intent(
                            "ringing"));

                }
                if (type.equals("accept")){
                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager
                            .getInstance(NotificationService.this);
                    localBroadcastManager.sendBroadcast(new Intent(
                            "accept"));

                }
                if(type.equals("decline")){
                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager
                            .getInstance(NotificationService.this);
                    localBroadcastManager.sendBroadcast(new Intent(
                            "close"));
                }
                if(type.equals("callend")){
                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager
                            .getInstance(NotificationService.this);
                    localBroadcastManager.sendBroadcast(new Intent(
                            "close"));
                }
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                    if (!isRunning) {
                        if (type.equals("message")) {

                            createOreoNotification(name, message, imageURL, senderID);
                        } else if (type.equals("videocall")) {
                            Log.d(TAG, "onMessageReceived: "+"videocall");
                            Intent dialogIntent = new Intent(this, InComingVideoCall_accept_Activity.class);
                            dialogIntent.putExtra("channelname",senderID);
                            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(dialogIntent);
                        }
                    }
                }
                else{
                    if(!isRunning){
                        if(type.equals("message")){
                            createNormalNotification(name, message, imageURL,senderID);
                        }
                }else if(type.equals("videocall")){
                    Intent dialogIntent = new Intent(this, InComingVideoCall_accept_Activity.class);
                    dialogIntent.putExtra("channelname",senderID);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(dialogIntent);

                }
            }
            }else Log.d(TAG, "onMessageReceived: no data ");

            super.onMessageReceived(remoteMessage);
        }

        private void createVideoCallNotification(String name) {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            RemoteViews incomingvideocall_view=new RemoteViews(getPackageName(),R.layout.incomingvideocall_layout);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1000");
            builder.setContentTitle("Videoanruf")
                    .setContentText(name)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSmallIcon(R.mipmap.ic_icon)
                    .setCustomContentView(incomingvideocall_view)
                    .setSound(uri);

            Intent intent = new Intent(this, InComingVideoCallActivity.class);
            intent.putExtra("ID", name);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            builder.setFullScreenIntent(pendingIntent,true);
            builder.setContentIntent(pendingIntent);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(new Random().nextInt(85 - 65), builder.build());

        }

        @Override
        public void onNewToken(@NonNull String s) {
            updateToken(s);
            super.onNewToken(s);
        }

        private void updateToken(String token) {

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            if (firebaseAuth.getCurrentUser() != null) {
                HashMap<String, Object> map= new HashMap();
                map.put("token",token);
                DatabaseReference userRef= FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid());
                userRef.setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });
            }

        }


        private void createNormalNotification(String title, String message, String hisImage,String senderID) {

            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1000");
            builder.setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSmallIcon(R.mipmap.ic_icon)
                    .setAutoCancel(true)
                    .setColor(ResourcesCompat.getColor(getResources(), R.color.primary, null))
                    .setSound(uri);

            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("Name", title);
            intent.putExtra("ID", senderID);
            intent.putExtra("Image", hisImage);


            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            builder.setContentIntent(pendingIntent);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(new Random().nextInt(85 - 65), builder.build());

        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void createOreoNotification(String title, String message, String hisImage,String senderID) {
            Log.d(TAG, "createOreoNotification: ");
            NotificationChannel channel = new NotificationChannel("1000", "Message", NotificationManager.IMPORTANCE_HIGH);
            channel.setShowBadge(true);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);

            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("Name", title);
            intent.putExtra("ID", senderID);
            intent.putExtra("Image", hisImage);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);

            Notification notification = new Notification.Builder(this, "1000")
                    .setContentTitle(title)
                    .setContentText(message)
                    .setColor(ResourcesCompat.getColor(getResources(), R.color.primary, null))
                    .setSmallIcon(R.mipmap.ic_icon)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
            manager.notify(100, notification);
        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        private void createOreoVideoCallNotification(String name) {
            Log.d(TAG, "createOreoVideoNotification: ");
            Log.d(TAG, "createOreoNotification: ");
            RemoteViews incomingvideocall_view=new RemoteViews(getPackageName(),R.layout.incomingvideocall_layout);
            NotificationChannel channel = new NotificationChannel("1000", "Message", NotificationManager.IMPORTANCE_HIGH);
            channel.setShowBadge(true);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);

            Intent intent = new Intent(this, InComingVideoCallActivity.class);
            intent.putExtra("channelname",name);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);

            Notification notification = new Notification.Builder(this, "1000")
                    .setColor(ResourcesCompat.getColor(getResources(), R.color.primary, null))
                    .setSmallIcon(R.mipmap.ic_icon)
                    .setAutoCancel(true)
                    .setCustomContentView(incomingvideocall_view)
                    .setFullScreenIntent(pendingIntent,true)
                    .build();
            manager.notify(100, notification);



        }
    }


