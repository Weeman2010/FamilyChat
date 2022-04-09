package com.example.familychat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familychat.Activitys.ChatActivity;
import com.example.familychat.Activitys.MainActivity;
import com.example.familychat.Activitys.OutgoingVideoCallActivity;
import com.example.familychat.Interfaces.ContactOnClick;
import com.example.familychat.Model.User;
import com.example.familychat.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private ArrayList<User> userArrayList;
    private Context context;
    private ContactOnClick onClickAdapter;


    public ContactAdapter(ArrayList<User> userArrayList, Context context, ContactOnClick onClickAdapter) {
        this.userArrayList = userArrayList;
        this.context = context;
        this.onClickAdapter=onClickAdapter;
    }

    @NonNull
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(context).inflate(R.layout.contactlist_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ViewHolder holder, int position) {
         final User user= userArrayList.get(position);
          holder.userName.setText(userArrayList.get(position).getUserName());
          if( ! userArrayList.get(position).getUserImageURL().equals("")){
          Picasso.get().load(userArrayList.get(position).getUserImageURL()).into(holder.profilImage);
          };

              holder.root.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      if(MainActivity.newChat){
                          onClickAdapter.onClick(v);
                          MainActivity.newChat=false;
                          context.startActivity(new Intent(context, ChatActivity.class)
                                  .putExtra("ID",user.getUserID())
                                  .putExtra("Name",user.getUserName())
                                  .putExtra("Image",user.getUserImageURL()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                      }else if (MainActivity.newCall){
                          MainActivity.newCall=false;
                          context.startActivity(new Intent(context, OutgoingVideoCallActivity.class)
                                  .putExtra("receiver",user.getUserID())
                                  .putExtra("ID",user.getUserID())
                                  .putExtra("Name",user.getUserName())
                                  .putExtra("Image",user.getUserImageURL()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                      }{
                          //TODO ELSE
                      }
                      }

              });



    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profilImage;
        private TextView userName;
        private LinearLayout root;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
           root=itemView.findViewById(R.id.contact_root);
           profilImage= itemView.findViewById(R.id.contactlist_profilimage);
           userName= itemView.findViewById(R.id.contactlist_username);
        }
    }
}
