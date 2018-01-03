package com.bartintveld.royaltyfreeimages;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

import static android.app.ActivityOptions.makeSceneTransitionAnimation;

public class MainActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener, PictureModel.PicturesAvailable {

    private ArrayList<Picture> spotifys = new ArrayList<Picture>();
    private ListView listView;
    private TextView textView;
    private AdView mAdView;
    private PictureAdapter arrayAdapter;
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        //Log.d("SelectedItem: ", position + "");

        Picture spotify = spotifys.get(position);

        //Animation so you can see selected
        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
        animation1.setDuration(2000);
        view.startAnimation(animation1);

        Intent intent = new Intent(getApplicationContext(), DetailedActivity.class);
        intent.putExtra("SPOTIFY", spotify);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this,"ca-app-pub-6428622231744693~4022860947");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Setting adapter
        arrayAdapter = new PictureAdapter(this, spotifys);

        //Maak referrentie naar ListView
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        listView.setAdapter(arrayAdapter);

        textView = (TextView) findViewById(R.id.resultCount);
        EditText editText = (EditText) findViewById(R.id.editText);
        final Button button = (Button) findViewById(R.id.button);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ( (actionId == EditorInfo.IME_ACTION_SEARCH) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN ))){
                    button.performClick();
                    return true;
                }
                else{
                    return false;
                }
            }
        });

        mAdView = ( AdView ) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public void searchArtist(View v) {

        //Animation so you can see selected
        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
        animation1.setDuration(2000);
        v.startAnimation(animation1);

        //Cool easter egg
        EditText editText = (EditText) findViewById(R.id.editText);
        if (editText.getText().toString().equals(getResources().getString(R.string.bart_easter))) {
            final ImageView easterImage = new ImageView(this);
            final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
            easterImage.setImageResource(R.drawable.bartaveld);
            easterImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    frameLayout.removeView(easterImage);
                }
            });
            frameLayout.addView(easterImage);
        }

        else if (editText.getText().toString().equals("")) {
            editText.setHintTextColor(getResources().getColor(R.color.failure));
            Vibrator vibrator = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
            vibrator.vibrate(500);
        }

        else {
            editText.setHintTextColor(getResources().getColor(R.color.hinttext));
            //Search for artist and replace " " with "+"
            String artist = editText.getText().toString();
            String newArtist = "";
            for (int i = 0; i < artist.length(); i++) {
                Character c = artist.charAt(i);
                if (artist.charAt(i) != ' ') {
                    newArtist = newArtist + artist.charAt(i);
                } else {
                    newArtist = newArtist + "+";
                }
            }
            //Log.i("MainActivity: ", newArtist);

            String[] urls = new String[]{"https://pixabay.com/api/?key=4810896-b6eb6311a29ccc6332639e0f8&q=" + newArtist + "&per_page=200"};

            // Connect and pass self for callback
            PictureModel getAlbums = new PictureModel(this);
            getAlbums.execute(urls);
        }
    }

    @Override
    public void pictureAvailable(ArrayList<Picture> result) {

        EditText editText = (EditText) findViewById(R.id.editText);
        // Toevoegen array
        spotifys = result;
        arrayAdapter = new PictureAdapter(this, spotifys);
        listView.setAdapter(arrayAdapter);
        textView.setText(editText.getText() + " " + getString(R.string.returned) + " " + spotifys.size() + " " + getString(R.string.results));
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.info_text);
                builder.setTitle(R.string.info_title);
                builder.setNegativeButton(R.string.negative, null);
                builder.setNeutralButton(R.string.neutral, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    Intent pixabay = new Intent(Intent.ACTION_VIEW, Uri.parse("https://pixabay.com/"));
                    startActivity(pixabay);
                }
            });
                builder.setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent donate = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources()
                                .getString(R.string.donate_link)));
                        startActivity(donate);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}

