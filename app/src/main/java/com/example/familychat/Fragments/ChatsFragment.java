package com.example.familychat.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.familychat.Adapter.ChatListAdapter;
import com.example.familychat.Model.Chatlist;
import com.example.familychat.Model.Chats;
import com.example.familychat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ChatsFragment extends Fragment {


    private final String TAG = "ChatsFragment";
    private List<Chatlist> list;
    private RecyclerView recyclerView;
    private DatabaseReference reference;
    private ChatListAdapter adapter;
    private List<Chatlist> allUser;

    public ChatsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        list = new ArrayList<>();

        View v = inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView = v.findViewById(R.id.chats_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new ChatListAdapter(getContext(),list);
        recyclerView.setAdapter(adapter);
        getChatsFromFirebase();
        return v;
    }

    private void getChatsFromFirebase() {
        String currentUser=FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference=FirebaseDatabase.getInstance().getReference();
        DatabaseReference userInfo=FirebaseDatabase.getInstance().getReference().child("Users");
        reference.child("Chats").child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userID:snapshot.getChildren()
                     ) {
                    userInfo.child(userID.getKey()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            list.add(new Chatlist(userID.getKey(),dataSnapshot.child("Name").getValue().toString(),"",dataSnapshot.child("Image").getValue().toString()));
                            adapter.notifyDataSetChanged();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}

