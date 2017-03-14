package com.bartintveld.royaltyfreeimages;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DetailedActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView nameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_detailed);

        imageView = (ImageView) findViewById(R.id.detailedImageView);

        Intent intent = getIntent();
        Picture album = (Picture) intent.getSerializableExtra("SPOTIFY");

        ImageView imageView = (ImageView) findViewById(R.id.detailedImageView);
        Picasso.with(getApplicationContext()).load(album.getImage_url()).into(imageView);

        nameView = (TextView) findViewById(R.id.detailedName);
        nameView.setText(album.getpictureName());

        //Add floating menu to hold on save
        registerForContextMenu(imageView);
    }

    public void backClick (View v){

        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
        animation1.setDuration(2000);
        v.startAnimation(animation1);

        onBackPressed();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.save:
                if (verifyStoragePermissions(this) == true) {
                    imageView.buildDrawingCache();
                    Bitmap bm = imageView.getDrawingCache();
                    SaveImage(bm);
                    return true;
                }
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void SaveImage(Bitmap finalBitmap) {

        File root = android.os.Environment.getExternalStorageDirectory();
        File myDir = new File(root.getAbsolutePath() + "/RoyaltyFreeImages");

        if (!myDir.exists()) {
            myDir.mkdir();
        }

        String fileName =  nameView.getText() +".png";
        File file = new File (myDir, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            //Letting user know saving worked
            Toast.makeText(getApplicationContext(), "Image: "+ nameView.getText() + " saved" ,
                    Toast.LENGTH_SHORT).show();

            //Letting gallery know of existence image
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static boolean verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        }
        else {
            return true;
        }
    }
}


