package notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.boostup.InfluencerChatAcitivity;
import com.example.boostup.ManagerChatActivity;
import com.example.boostup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessaging extends FirebaseMessagingService {
    String sendid;
    DatabaseReference databaseReference;

    @Override
    public void onMessageReceived(@NonNull final RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);
        databaseReference= FirebaseDatabase.getInstance().getReference("user");
        final FirebaseUser fuser= FirebaseAuth.getInstance().getCurrentUser();

        Query query=databaseReference.child("Influencers").orderByChild("uid").equalTo(fuser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>0){
                    SharedPreferences sp=getSharedPreferences("SP_USER",MODE_PRIVATE);
                    String savedCurrentUser = sp.getString("Current_USERID","None");
                    String sent = remoteMessage.getData().get("sent");
                    String user= remoteMessage.getData().get("user");
                    if(fuser!=null && sent.equals(fuser.getUid())){
                        if(!savedCurrentUser.equals(user)){
                            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                                IsendOAndAboveNotification(remoteMessage);
                            }
                            else{
                                IsendNormalNotification(remoteMessage);
                            }
                        }
                    }
                }
                else{
                    SharedPreferences sp=getSharedPreferences("SP_USER",MODE_PRIVATE);
                    String savedCurrentUser = sp.getString("Current_USERID","None");
                    String sent = remoteMessage.getData().get("sent");
                    String user= remoteMessage.getData().get("user");
                    if(fuser!=null && sent.equals(fuser.getUid())){
                        if(!savedCurrentUser.equals(user)){
                            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                                sendOAndAboveNotification(remoteMessage);
                            }
                            else{
                                sendNormalNotification(remoteMessage);
                            }
                        }
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void IsendNormalNotification(RemoteMessage remoteMessage) {
        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");

        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent= new Intent(this, InfluencerChatAcitivity.class);
        Bundle bundle=new Bundle();
        bundle.putString(sendid,user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentText(body)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setSound(defSoundUri)
                .setContentIntent(pIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notification.getBody()));
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int j=0;
        if(i>0){
            j=i;
        }
        notificationManager.notify(j,builder.build());
    }

    private void IsendOAndAboveNotification(RemoteMessage remoteMessage) {
        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");

        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent= new Intent(this, InfluencerChatAcitivity.class);
        Bundle bundle=new Bundle();
        bundle.putString(sendid,user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoAndAboveNotification notification1=new OreoAndAboveNotification(this);
        Notification.Builder builder=notification1.getONotifications(title,body,pIntent,defSoundUri,icon);

        int j=0;
        if(i>0){
            j=i;
        }
        notification1.getManager().notify(j,builder.build());
    }

    private void sendNormalNotification(RemoteMessage remoteMessage) {
        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");

        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent= new Intent(this, ManagerChatActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString(sendid,user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentText(body)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setShowWhen(true)
                .setWhen(System.currentTimeMillis())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notification.getBody()))
                .setSound(defSoundUri)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setSummaryText("#hashtag"))
                .setContentIntent(pIntent);
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int j=0;
        if(i>0){
            j=i;
        }
        notificationManager.notify(j,builder.build());
    }

    private void sendOAndAboveNotification(RemoteMessage remoteMessage) {

        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");

        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent= new Intent(this, ManagerChatActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString(sendid,user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoAndAboveNotification notification1=new OreoAndAboveNotification(this);
        Notification.Builder builder=notification1.getONotifications(title,body,pIntent,defSoundUri,icon);

        int j=0;
        if(i>0){
            j=i;
        }
        notification1.getManager().notify(j,builder.build());

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            updateToken(s);
        }
    }

    private void updateToken(String tokenRefresh) {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Tokens");
        Token token= new Token(tokenRefresh);
        ref.child(user.getUid()).setValue(token);

    }
}
