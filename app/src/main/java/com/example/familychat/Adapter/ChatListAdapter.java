package com.example.familychat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familychat.Activitys.ChatActivity;
import com.example.familychat.Model.Chatlist;
import com.example.familychat.Model.Chats;
import com.example.familychat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.Holder> {
    private static final String TAG ="ChatListAdapter" ;
    private Context context;
    private List<Chatlist> chatlist;
    private FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Messages");
    private String last_msg,last_msg_type="";

    public ChatListAdapter(Context context, List<Chatlist> chatlist) {
        this.context = context;
        this.chatlist = chatlist;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.adapter_chatlist_item,parent,false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Chatlist chats = chatlist.get(position);
        lastMessage(chats.getUserID(),holder.preview,holder.date,position);
        holder.name.setText(chatlist.get(position).getUserName());
        holder.date.setText(chatlist.get(position).getDate());

        if(!TextUtils.isEmpty(chatlist.get(position).getUrlProfile())){
            Picasso.get().load(chatlist.get(position).getUrlProfile()).into(holder.profile);
        }
        Log.d(TAG, "onBindViewHolder: "+chatlist.get(position).getDate());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatActivity.class)
                        .putExtra("ID",chats.getUserID())
                        .putExtra("Name",chats.getUserName())
                        .putExtra("Image",chats.getUrlProfile()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatlist.size();
    }
   public class Holder extends RecyclerView.ViewHolder {
        private TextView name,date,preview;
        private CircleImageView profile;
        private LinearLayout linearLayout;

        public Holder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.message_item_username);
            preview=itemView.findViewById(R.id.message_item_preview);
            date=itemView.findViewById(R.id.message_item_time);
            profile=itemView.findViewById(R.id.message_item_profile);
            linearLayout=itemView.findViewById(R.id.message_item_linearlayout);
        }
    }
    public void lastMessage(String userID, TextView lastmsg, TextView date, int position){
        last_msg="default";
        reference.child(userID).orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chats chat=new Chats(dataSnapshot.child("time").getValue().toString(),dataSnapshot.child("message").getValue().toString(),
                            dataSnapshot.child("type").getValue().toString(),dataSnapshot.child("sender").getValue().toString(),dataSnapshot.child("receiver").getValue().toString());
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userID)||
                    chat.getReceiver().equals(userID) &&chat.getSender().equals(firebaseUser.getUid())){
                        last_msg= chat.getTextMessage();
                        last_msg_type=chat.getType();

                        date.setText(chat.getDateTime().substring(11,16));
                    }
                    chatlist.get(position).setDate(chat.getDateTime());
                }

                switch (last_msg_type){
                    case "TEXT":
                    lastmsg.setText(last_msg);
                    break;
                    case "AUDIO":
                        lastmsg.setText("Audio");
                        break;
                    case "VIDEO":
                        lastmsg.setText("Video");
                        break;
                    case "IMAGE":
                        lastmsg.setText("Bild");
                        break;
                }

                last_msg="default";
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}