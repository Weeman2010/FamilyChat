package com.example.familychat.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.familychat.R;
import com.example.familychat.Util.FirebaseHelper;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {
    private CircleImageView profilImage;
    private EditText userName;
    private Button next;
    private Boolean setImage=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        InitWidges();
    }

    private void InitWidges() {
      profilImage=findViewById(R.id.register_profilimage);
      userName=findViewById(R.id.register_username);
      next=findViewById(R.id.register_btn_next);

      next.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if(TextUtils.isEmpty(userName.getText().toString())){
                  Toast.makeText(RegisterActivity.this, "Benutzername darf nicht leer sein", Toast.LENGTH_SHORT).show();
              }
              else
              {
                  FirebaseHelper.registerUser(userName.getText().toString(),null,RegisterActivity.this);
                  startMainactivity();
              }
          }
      });
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseHelper.getcurrentUserID() != null){
            startMainactivity();
        }
    }

    private void startMainactivity() {

        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

}