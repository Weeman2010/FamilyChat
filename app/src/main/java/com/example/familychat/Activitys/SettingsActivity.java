package com.example.familychat.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.familychat.R;
import com.example.familychat.Util.IO_Helper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private CircleImageView profilImage;
    private TextView userName;
    private Button save;
    private ImageView editImage;
    private DatabaseReference rootRef= FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth auth=FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        InitWidges();
    }

    private void InitWidges() {
        profilImage=findViewById(R.id.settings_profilimage);
        userName=findViewById(R.id.settings_username);
        rootRef.child("Users").child(auth.getCurrentUser().getUid()).child("Name").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                userName.setText(dataSnapshot.getValue().toString());
            }
        });
        IO_Helper.loadImage("profilbild",profilImage,getApplicationContext());
    }
}