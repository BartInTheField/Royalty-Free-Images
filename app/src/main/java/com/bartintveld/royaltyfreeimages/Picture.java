package com.bartintveld.royaltyfreeimages;

import java.io.Serializable;

/**
 * Created by Bart on 8-3-2017.
 */

public class Picture implements Serializable {

    public String pictureName; // pictureName
    public String image_url; // Albumart 640x640px
    public String image_thumb_url; // Thumbnail 64x64px
    public String creator; // Unique Spotify ID

    public String getpictureName() {
        return pictureName;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getImage_thumb_url() {
        return image_thumb_url;
    }

    public String getcreator() {
        return creator;
    }

    public Picture(String pictureName, String image_url, String image_thumb_url, String creator) {
        this.pictureName = pictureName;
        this.image_url = image_url;
        this.image_thumb_url = image_thumb_url;
        this.creator = creator;


    }
}
