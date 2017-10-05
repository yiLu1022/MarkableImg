package com.netatmo.ylu.markableimage.model;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by ylu on 10/4/17.
 */

public class ImageLoader {
    Context context;

    public ImageLoader(final Context context) {
        this.context = context;
    }

    public void load(String path, ImageView view){
        File file = new File(path);
        Picasso.with(context).load(file).into(view);
    }
}
