package com.bartintveld.royaltyfreeimages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Bart on 8-3-2017.
 */

public class PictureAdapter extends ArrayAdapter<Picture>{

    public PictureAdapter(Context context, ArrayList<Picture> pictures) {
        super(context, 0, pictures);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Picture picture = getItem(position);

        if( convertView == null ) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_layout, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.nameView);
        TextView creator = (TextView) convertView.findViewById(R.id.creator);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);

        // Vul de NAAM en CREATOR
        String n = picture.getpictureName();
        name.setText(n);
        String c = getContext().getResources().getString(R.string.creator) + ": " + picture.getcreator();
        creator.setText(c);

        Picasso.with(getContext()).load(picture.getImage_thumb_url()).into(imageView);
        // Get image vanuit /drawable and vul
        /*String imageName = "@drawable/" + person.getImageID();
        int imageId = getContext().getResources().getIdentifier(imageName, null, getContext().getPackageName());
        imageView.setImageResource(imageId);*/

        return convertView;
    }


}

