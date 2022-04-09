package com.example.familychat.Adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familychat.Model.Chats;
import com.example.familychat.R;
import com.example.familychat.Util.MessageToSpeech;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.Holder> {
    private List<Chats> chatsList;
    private Context context;
    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    private FirebaseUser firebaseUser;

    public ChatAdapter(Context context,List<Chats> chatsList) {
        this.chatsList=chatsList;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_LEFT){
            View v= LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
            return new Holder(v);
        }else{
            View v= LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
            return new Holder(v);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(chatsList.get(position));
    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView textMessage,textTime;
        private ImageView imageMessage;
        public Holder(@NonNull View itemView) {
            super(itemView);
            textMessage=itemView.findViewById(R.id.chat_text_message);
            textTime=itemView.findViewById(R.id.chat_text_time);
            imageMessage=itemView.findViewById(R.id.chat_image_message);
        }

        public void bind(Chats chats) {
            if(chats.getType().equals("TEXT")){
                textMessage.setText(chats.getTextMessage());
                textTime.setText(chats.getDateTime().substring(11,16));
                textMessage.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        MessageToSpeech.play(context,textMessage.getText().toString());
                        return true;
                    }
                });
            }
            if(chats.getType().equals("AUDIO")){
                textMessage.setText(chats.getTextMessage());
                textTime.setText(chats.getDateTime().substring(11,16));
                textMessage.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        MessageToSpeech.play(context,textMessage.getText().toString());
                        return true;
                    }
                });
            }
            if(chats.getType().equals("IMAGE")){
                WindowManager window = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                DisplayMetrics dm = new DisplayMetrics();
                window.getDefaultDisplay().getMetrics(dm);
                int w = dm.widthPixels;
                int h = dm.heightPixels;
                textMessage.setVisibility(View.GONE);
                imageMessage.setVisibility(View.VISIBLE);
                imageMessage.setMaxHeight(h/3);
                imageMessage.setMaxWidth(w-50);
                Picasso.get().load(chats.getTextMessage()).into(imageMessage);
                textTime.setText(chats.getDateTime().substring(11,16));
            }
            if(chats.getType().equals("VIDEO")){
                textMessage.setText(chats.getTextMessage());
                textTime.setText(chats.getDateTime().substring(11,16));
                textMessage.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        MessageToSpeech.play(context,textMessage.getText().toString());
                        return true;
                    }
                });
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(chatsList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }
}
