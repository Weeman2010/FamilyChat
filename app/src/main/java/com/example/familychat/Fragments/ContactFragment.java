package com.example.familychat.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.familychat.Adapter.ContactAdapter;
import com.example.familychat.Interfaces.ContactOnClick;
import com.example.familychat.Model.User;
import com.example.familychat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class ContactFragment extends Fragment implements ContactOnClick{
    private static final String TAG = "CONTACT";
    private RecyclerView recyclerView;
   private ContactAdapter adapter;
   private ArrayList<User> userArrayList=new ArrayList<>();
   private String userID;
   private DatabaseReference rootRef;

    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_contact, container, false);
        userArrayList = getUserFromFirebase();
        recyclerView=v.findViewById(R.id.contact_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        return v;
    }

    private ArrayList<User> getUserFromFirebase() {

        userID=FirebaseAuth.getInstance().getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference().child("Users");
        rootRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(!snapshot.child("userID").getValue().equals(userID)){
                    userArrayList.add(new User(snapshot.child("userID").getValue().toString(),snapshot.child("Name").getValue().toString(),snapshot.child("Image").getValue().toString(),""));
                    adapter=new ContactAdapter(userArrayList,getContext(),ContactFragment.this);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                int i=0;
                for (User user: userArrayList
                     ) {
                    if((user.getUserID()).equals(snapshot.child("userID").getValue())){

                        userArrayList.get(i).setUserName(snapshot.child("Name").getValue().toString());
                        adapter.notifyDataSetChanged();
                    }
                    i++;

                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onChildRemoved: ");
                int i=0;
                for (User user: userArrayList
                ) {
                    if((user.getUserID()).equals(snapshot.child("userID").getValue())){

                        userArrayList.remove(i);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                    i++;

                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return userArrayList;
    }

    @Override
    public void onClick(View view) {

    }
}