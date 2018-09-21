package com.example.angelica.cameratest;

/**
 * Created by Angelica on 11/20/2017.
 */

import android.graphics.Bitmap;

/**
 * A LoadedImage contains the Bitmap loaded for the image.
 */
public class LoadedImage {
    Bitmap mBitmap;
    String path;


    LoadedImage(Bitmap bitmap,String path) {
        mBitmap = bitmap;
        this.path = path;

    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}