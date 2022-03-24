package com.example.familychat.Activitys;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.familychat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private CircleImageView profilImage;
    private EditText userName;
    private Button next;
    private Boolean setImage=false;
    private FirebaseAuth auth=FirebaseAuth.getInstance();

    private ProgressDialog progressDialog ;
    private Uri imageUri=null;
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Users");
    private StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Images");

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

              }
          }
      });
      profilImage.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1)
                      .start(RegisterActivity.this);
          }
      });
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

    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver =getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                profilImage.setImageURI(imageUri);

            }
        }

    }

    private void registerUser() {
        auth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                uploadToFirebase();
            }
        });


    }

    private void uploadToFirebase() {
        progressDialog =new ProgressDialog(this);
        progressDialog.setMessage("Bitte warten...");
        progressDialog.show();
        if(imageUri !=null) {
            storageReference.child(auth.getCurrentUser().getUid()).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    Uri downloadUri = uriTask.getResult();
                    final String download_url = String.valueOf(downloadUri);


                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("imageProfile", download_url);
                    hashMap.put("Name", userName.getText().toString());
                    hashMap.put("userID", auth.getCurrentUser().getUid());
                    rootRef.setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                    startMainactivity();
                                }
                            });
                }



            });
        }else{
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("Image", "");
            hashMap.put("Name", userName.getText().toString());
            hashMap.put("userID", auth.getCurrentUser().getUid());
            rootRef.child(auth.getCurrentUser().getUid()).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            startMainactivity();
                        }
                    });
        }
    }

}