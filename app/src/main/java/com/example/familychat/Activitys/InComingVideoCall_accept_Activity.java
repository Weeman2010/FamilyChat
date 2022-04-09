package com.example.familychat.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.familychat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class InComingVideoCall_accept_Activity extends AppCompatActivity {
    private CircleImageView profilImage;
    private TextView  userName;
    private FloatingActionButton accept,decline;
    private Ringtone ringtoneSound;
    private Vibrator v;
    private String caller_ID;

    LocalBroadcastManager mLocalBroadcastManager;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("close")){
                finish();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_coming_video_call_accept);
        InitWidges();
        InitOnClick();
        sendNotificationRinging();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("close");
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mIntentFilter);

        if(checkRingerIsOn(this)){
            Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
             ringtoneSound = RingtoneManager.getRingtone(getApplicationContext(), ringtoneUri);

            if (ringtoneSound != null) {
                ringtoneSound.play();
            }
        }else if(checkVibreationIsOn(this)){
             v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {0, 100, 1000};
            v.vibrate(pattern, 0);
        }

    }


    private void InitWidges() {
        profilImage=findViewById(R.id.incoming_profilimage);
        userName=findViewById(R.id.incoming_caller);
        accept=findViewById(R.id.incoming_accept);
        decline=findViewById(R.id.incoming_decline);
        caller_ID=getIntent().getStringExtra("channelname");

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users").child(caller_ID);
        ref.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                userName.setText(dataSnapshot.child("Name").getValue().toString());
                if (!dataSnapshot.child("Image").getValue().toString().equals("")) {
                    Picasso.get().load(dataSnapshot.child("Image").getValue().toString()).into(profilImage);
                }
            }
        });
    }
    private void InitOnClick() {
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkVibreationIsOn(getApplicationContext())){
                    v.cancel();
                }else{ringtoneSound.stop();
                }
                sendNotificationDecline();
                finish();
            }
        });
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(InComingVideoCall_accept_Activity.this,InComingVideoCallActivity.class);
                intent.putExtra("channelname",caller_ID);
                sendNotifivationAccept();
                startActivity(intent);
                finish();
            }
        });
    }

    private void sendNotificationDecline() {
        getToken("decline");
    }
    private void sendNotificationRinging(){getToken("ringing");}
    private void sendNotifivationAccept(){getToken("accept");}

    public static boolean checkVibreationIsOn(Context context){
        boolean status = false;
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if(am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE){
            status = true;
        } else if (1 == Settings.System.getInt(context.getContentResolver(), "vibrate_when_ringing", 0)) //vibrate on
            status = true;
        return status;
    }

    public static boolean checkRingerIsOn(Context context){
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        return am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(checkVibreationIsOn(getApplicationContext())){
            v.cancel();
        }if(checkRingerIsOn(getApplicationContext())){
            ringtoneSound.stop();}
    }

    private void getToken(String message){
        FirebaseDatabase.getInstance().getReference().child("Users").child(caller_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DatabaseReference user=FirebaseDatabase.getInstance().getReference().child("Users").child(caller_ID);
                user.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        String  token=dataSnapshot.child("token").getValue().toString();
                        Log.d("Message", "onDataChange: "+token);
                        try {
                            JSONObject to = new JSONObject();
                            JSONObject data = new JSONObject();
                            data.put("Type",message);
                            to.put("to", token);
                            to.put("data", data);
                            to.put("priority", "high");
                            sendNotification(to);
                        } catch (JSONException e) {
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendNotification(JSONObject to) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", to, response -> {

        }, error -> {

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Authorization", "key=" + "AAAAuDZwb4g:APA91bFOE8HZskklWLHN_5F94xkPhZbLK65m8GTcB12YXSQEL2zgAxLubDuFWD-hlxGbbK3t2ZtBogTiV6yvEy-y76GcGqVtB58q18ctBszmABxSjd29m1aO9hL-RB5i967KIS8AisJS");
                map.put("Content-Type", "application/json");
                return map;
            }
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        request.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }
}