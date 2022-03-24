package com.example.familychat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familychat.Model.User;
import com.example.familychat.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private ArrayList<User> userArrayList;
    private Context context;


    public ContactAdapter(ArrayList<User> userArrayList, Context context) {
        this.userArrayList = userArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(context).inflate(R.layout.contactlist_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ViewHolder holder, int position) {
          holder.userName.setText(userArrayList.get(position).getUserName());
          if( ! userArrayList.get(position).getUserImageURL().equals("")){
          Picasso.get().load(userArrayList.get(position).getUserImageURL()).into(holder.profilImage);
          };

    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profilImage;
        private TextView userName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

           profilImage= itemView.findViewById(R.id.contactlist_profilimage);
           userName= itemView.findViewById(R.id.contactlist_username);
        }
    }
}
