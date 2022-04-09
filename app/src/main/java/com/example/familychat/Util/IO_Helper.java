package com.example.familychat.Util;

import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.example.familychat.Activitys.MainActivity;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class IO_Helper {
    private SharedPreferences sharedPreferences;



    public static void saveImage(Bitmap bmp, String filename,Context context){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();


        File myfile = new File(commonDocumentDirPath("Images",context),filename+".jpeg");

        FileOutputStream fo;
        try {
            myfile.createNewFile();
            fo = new FileOutputStream(myfile);
            fo.write(byteArray);
            fo.flush();
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void loadImage(String filename, ImageView profil,Context context){

        File file = new File(commonDocumentDirPath("Images/",context),"profilbild.jpeg");
        if(file.exists()){
            profil.setImageDrawable(Drawable.createFromPath(file.getPath()));
        }
    }

    public static File commonDocumentDirPath(String FolderName,Context context)
    {
        File dir = null;
        if (SDK_INT >= Build.VERSION_CODES.R)
        {
            //dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + FolderName);
            dir=new File(context.getFilesDir()+"/"+FolderName);

        }
        else
        {
            //dir = new File(Environment.getExternalStorageDirectory() + "/" + FolderName);
            dir=new File(context.getFilesDir()+"/"+FolderName);
        }

        // Make sure the path directory exists.
        if (!dir.exists())
        {
            // Make it, if it doesn't exit
            boolean success = dir.mkdirs();
            if (!success)
            {
                dir = null;
            }
        }

        return dir;
    }
}

