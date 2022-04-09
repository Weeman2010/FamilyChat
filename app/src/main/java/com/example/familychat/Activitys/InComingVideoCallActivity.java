package com.example.familychat.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.familychat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class InComingVideoCallActivity extends AppCompatActivity {

    private String appId = "9fbde79ae0534813b673d3ceab28b78a";
    private String channelName;
    private String token = "";
    private RtcEngine mRtcEngine;
    private FrameLayout container,containerRemote;
    private SurfaceView surfaceView,surfaceViewRemote;
    private boolean isSwitched=false;
    private FloatingActionButton video_toggle,audio_toggle,call_end;
    private boolean video_status=false,audio_status=false;
    LocalBroadcastManager mLocalBroadcastManager;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("close")){

                finish();
            }
        }
    };

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // Listen for the remote user joining the channel to get the uid of the user.
        public void onUserJoined(int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Call setupRemoteVideo to set the remote video view after getting uid from the onUserJoined callback.
                    setupRemoteVideo(uid);
                }
            });

        }
    };

    private static final int PERMISSION_REQ_ID = 22;

    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_coming_call);

        channelName=getIntent().getStringExtra("channelname");

        int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().addFlags(flags);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("close");
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mIntentFilter);
        initializeAndJoinChannel();
        initWidges();
    }

    private void initWidges() {
        video_toggle=findViewById(R.id.in_video_toggle);
        audio_toggle=findViewById(R.id.in_audio_toggle);
        call_end=findViewById(R.id.in_end_call);

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchContainer();
            }
        });
        call_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getToken("callend");
                finish();

            }
        });
        audio_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(audio_status){
                    Toast.makeText(InComingVideoCallActivity.this, "Audio ein", Toast.LENGTH_SHORT).show();
                    audio_status=false;
                    audio_toggle.setImageResource(R.drawable.ic_mic_off);
                }
                else{
                    Toast.makeText(InComingVideoCallActivity.this, "Audio aus", Toast.LENGTH_SHORT).show();
                    audio_status=true;
                    audio_toggle.setImageResource(R.drawable.ic_mic_on);
                }
            }
        });
        video_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(video_status){
                    Toast.makeText(InComingVideoCallActivity.this, "Video ein", Toast.LENGTH_SHORT).show();
                    video_status=false;
                    video_toggle.setImageResource(R.drawable.ic_videocam_off);
                }else{
                    Toast.makeText(InComingVideoCallActivity.this, "Video aus", Toast.LENGTH_SHORT).show();
                    video_status=true;
                    video_toggle.setImageResource(R.drawable.ic_videocam_on);
                }
            }
        });
    }


    private void initializeAndJoinChannel() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), appId, mRtcEventHandler);
        } catch (Exception e) {
            throw new RuntimeException("Check the error.");
        }

        // By default, video is disabled, and you need to call enableVideo to start a video stream.
        mRtcEngine.enableVideo();

         container = findViewById(R.id.in_local_video_view_container);
        // Call CreateRendererView to create a SurfaceView object and add it as a child to the FrameLayout.
         surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        // Pass the SurfaceView object to Agora so that it renders the local video.
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0));

        // Join the channel with a token.
        mRtcEngine.joinChannel(token, channelName, "", 0);
    }
    private void setupRemoteVideo(int uid) {
        containerRemote = findViewById(R.id.in_remote_video_view_container);
        surfaceViewRemote = RtcEngine.CreateRendererView(getBaseContext());
        containerRemote.addView(surfaceViewRemote);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceViewRemote, VideoCanvas.RENDER_MODE_FIT, uid));
    }
    protected void onDestroy() {
        super.onDestroy();

        mRtcEngine.leaveChannel();
        mRtcEngine.destroy();
    }
    private void getToken(String message){
        String currentUserID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DatabaseReference user=FirebaseDatabase.getInstance().getReference().child("Users").child(channelName);
                user.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        String  token=dataSnapshot.child("token").getValue().toString();
                        try {
                            JSONObject to = new JSONObject();
                            JSONObject data = new JSONObject();
                            data.put("Type",message);
                            data.put("sender", currentUserID);

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
        Log.d("NOTIFICATION", "sendNotification: ");
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
    private void switchContainer() {

        if (!isSwitched){
            containerRemote.removeAllViews();
            container.removeAllViews();
            containerRemote.addView(surfaceView);
            container.addView(surfaceViewRemote);
            surfaceViewRemote.setZOrderMediaOverlay(true);
            isSwitched=true;
        }else{
            containerRemote.removeAllViews();
            container.removeAllViews();
            containerRemote.addView(surfaceViewRemote);
            container.addView(surfaceView);
            surfaceViewRemote.setZOrderMediaOverlay(true);
            isSwitched=false;
        }

    }
}