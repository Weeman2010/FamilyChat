package com.example.familychat.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.familychat.Adapter.ContactAdapter;
import com.example.familychat.Model.User;
import com.example.familychat.R;

import java.util.ArrayList;


public class ContactFragment extends Fragment {
   private RecyclerView recyclerView;
   private ContactAdapter adapter;
   private ArrayList<User> userArrayList=new ArrayList<>();
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
        adapter=new ContactAdapter(userArrayList,getContext());
        recyclerView=v.findViewById(R.id.contact_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter);

        return v;
    }
}