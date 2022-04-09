package com.example.familychat.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.familychat.Adapter.ChatAdapter;
import com.example.familychat.Model.Chats;
import com.example.familychat.R;
import com.example.familychat.Util.MessageType;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private ImageButton backBtn,videocallBtn,callBtn,menuBtn;
    private CircleImageView profilImage;
    private TextView userName;
    private RecyclerView mesageRecyclerview;
    private ImageView emojiImageview,attachImageview,cameraImageview;
    private EditText messageEdittext;
    private FloatingActionButton sendBtn,recordBtn,voiceBtn;
    private EmojiTextView emojiTextView;
    private Intent intent;
    private ArrayList<Chats> chats;
    private ChatAdapter chatAdapter;
    private String ID,Name,Image;
    private DatabaseReference chatlistRef;
    private String currentUserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatlistRef=FirebaseDatabase.getInstance().getReference().child("Messages");
        intent=getIntent();
        ID=intent.getStringExtra("ID");
        Name=intent.getStringExtra("Name");
        Image=intent.getStringExtra("Image");
        currentUserID=FirebaseAuth.getInstance().getCurrentUser().getUid();
      InitWidges();
      InitOnclick();
      chats = getChatsfromFirebase();
      chatAdapter= new ChatAdapter(this,chats);
      mesageRecyclerview.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
      mesageRecyclerview.setAdapter(chatAdapter);

    }

    private void InitOnclick() {
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        videocallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(ChatActivity.this, OutgoingVideoCallActivity.class);
                intent.putExtra("Name",Name);
                intent.putExtra("receiver",ID);
                intent.putExtra("IMAGE",Image);
                startActivity(intent);
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void sendMessage() {
        String message = messageEdittext.getText().toString().trim();
        String receiverID = ID;
        String senderID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String type = MessageType.TEXT.toString();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String timeStamp = sdf.format(c.getTime());


        HashMap<String,Object> messageMap =new HashMap<>();
        messageMap.put("time",timeStamp);
        messageMap.put("message",message);
        messageMap.put("type",type);
        messageMap.put("sender",senderID);
        messageMap.put("receiver",receiverID);

        FirebaseDatabase.getInstance().getReference().child("Chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                     if(!snapshot.child(receiverID).exists()){
                         HashMap<String,Object> newChat = new HashMap<>();
                         newChat.put("Chats/"+currentUserID+"/"+receiverID,receiverID);
                         FirebaseDatabase.getInstance().getReference().updateChildren(newChat);
                     }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("Chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.child(currentUserID).exists()){
                    HashMap<String,Object> newChat = new HashMap<>();
                    newChat.put("Chats/"+receiverID+"/"+currentUserID,currentUserID);
                    FirebaseDatabase.getInstance().getReference().updateChildren(newChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        chatlistRef.child(currentUserID).push().setValue(messageMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                  getToken(message);
                  messageEdittext.setText("");
                  chatlistRef.child(receiverID).push().setValue(messageMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });
            }
        });



    }

    private ArrayList<Chats> getChatsfromFirebase() {

        ArrayList<Chats> chatsArrayList=new ArrayList<>();
        chatlistRef.child(currentUserID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                 if ((snapshot.child("sender").getValue().equals(currentUserID) && snapshot.child("receiver").getValue().equals(ID))
                 || (snapshot.child("sender").getValue().equals(ID) && snapshot.child("receiver").getValue().equals(currentUserID))){

                    chatsArrayList.add(new Chats(snapshot.child("time").getValue().toString(),
                            snapshot.child("message").getValue().toString(), snapshot.child("type").getValue().toString(), snapshot.child("sender").getValue().toString(),
                            snapshot.child("receiver").getValue().toString()));

                    chatAdapter.notifyDataSetChanged();
                     Log.d("ChatsFragment", "onChildAdded: ");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return chatsArrayList;
    }

    private void InitWidges() {
        backBtn = findViewById(R.id.chat_back);
        videocallBtn =findViewById(R.id.chat_videocall);
        callBtn = findViewById(R.id.chat_call);
        menuBtn = findViewById(R.id.chat_more);
        profilImage = findViewById(R.id.chat_profilpicture);
        userName = findViewById(R.id.chat_username);
        mesageRecyclerview = findViewById(R.id.chat_recyclerview);
        emojiImageview = findViewById(R.id.chat_emoticon);
        attachImageview = findViewById(R.id.chat_attach);
        cameraImageview = findViewById(R.id.chat_camera);
        messageEdittext = findViewById(R.id.chat_message);
        sendBtn = findViewById(R.id.chat_send);
        recordBtn =findViewById(R.id.chat_record_voice);
        voiceBtn = findViewById(R.id.chat_voice);
        emojiTextView= findViewById(R.id.chat_emojitextview);
        userName.setText(intent.getStringExtra("Name"));
        if (!intent.getStringExtra("Image").equals("")) {
            Picasso.get().load(intent.getStringExtra("Image")).into(profilImage);
        }
        messageEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(messageEdittext.getText().toString().isEmpty()){
                    sendBtn.setVisibility(View.GONE);
                    voiceBtn.setVisibility(View.VISIBLE);
                }else{
                    sendBtn.setVisibility(View.VISIBLE);
                    voiceBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void getToken(String message){
            FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       String myname = snapshot.child("Name").getValue().toString();
                       DatabaseReference user=FirebaseDatabase.getInstance().getReference().child("Users").child(ID);
                       user.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                           @Override
                           public void onSuccess(DataSnapshot dataSnapshot) {
                               String  token=dataSnapshot.child("token").getValue().toString();
                               Log.d("MessageService", "onSuccess: "+token);
                               try {
                                   JSONObject to = new JSONObject();
                                   JSONObject data = new JSONObject();
                                   data.put("Type","message");
                                   data.put("Name", myname);
                                   data.put("message", message);
                                   data.put("receiver",ID );
                                   data.put("sender", currentUserID);
                                   data.put("image",Image);
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
