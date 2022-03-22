package com.example.familychat.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.familychat.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class FirebaseHelper{
    private static FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private static DatabaseReference rootRef= FirebaseDatabase.getInstance().getReference();
    private static StorageReference storageReference;

    public static String getcurrentUserID(){
        String userID =mAuth.getCurrentUser().getUid();
        return userID;
    }

    public static ArrayList<User> getAllContacts(){
        ArrayList<User> userArrayList=new ArrayList<>();
        rootRef.child("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

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

    public static String getProfilImageURL(String ID){
        String URL=rootRef.child("Users").child(ID).child("Image").toString();

        return URL;
    }


    public static void registerUser(String userName, Uri imageUri, Activity activity){
        ProgressDialog dialog=new ProgressDialog(activity);
        dialog.setMessage("Benutzer wird erstellt");
        dialog.show();
        mAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                rootRef.child("Users").setValue(userName).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(activity, "Fehler beim erstellen des Benutzers", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        if(imageUri != null) {
            storageReference=FirebaseStorage.getInstance().getReference().child("Images/"+getcurrentUserID()+".jpg");
            storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(activity, "Benutzer erfolgriech erstellt", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        }

    }

}
