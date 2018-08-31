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

    LoadedImage(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }
}