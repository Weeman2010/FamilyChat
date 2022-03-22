package com.example.familychat.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.familychat.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {
    private CircleImageView profilImage;
    private EditText userName;
    private Button next;
    private Boolean setImage=false;
    private FirebaseAuth auth=FirebaseAuth.getInstance();
    private BottomSheetDialog dialog;
    private static final int IMAGE_REQUEST_CODE = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FirebaseAuth auth=FirebaseAuth.getInstance();
        auth.signOut();
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
                  registerUser();
                  startMainactivity();
              }
          }
      });
      profilImage.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            showBottomSheetPicture();
          }
      });
    }

    private void registerUser() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(auth.getCurrentUser() != null){
            startMainactivity();
        }
    }

    private void startMainactivity() {

        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void showBottomSheetPicture() {
        View view= getLayoutInflater().inflate(R.layout.bottom_sheet_pick,null);
        dialog=new BottomSheetDialog(this);
        dialog.setContentView(view);
        view.findViewById(R.id.bottom_sheet_delete).setVisibility(View.GONE);
        ((View) view.findViewById(R.id.bottom_sheet_camera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
                dialog.dismiss();
            }
        });

        ((View) view.findViewById(R.id.bottom_sheet_gallery)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                dialog.dismiss();
            }
        });

        ((View) view.findViewById(R.id.bottom_sheet_delete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePicture();
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog=null;
            }
        });
        dialog.show();


    }

    private void deletePicture() {

    }
    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver =getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }


    private void openGallery() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Bild auswählen"),IMAGE_REQUEST_CODE);
    }

    private void openCamera() {
    }

}