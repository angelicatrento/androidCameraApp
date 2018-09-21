package com.example.angelica.cameratest;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import static com.example.angelica.cameratest.CameraActivity.CAMERA_FRONT;

public class PictureConfirmation extends AppCompatActivity {
    private ImageButton save_picture;
    private ImageButton cancel_picture;
    private ImageView show_picture;
    String image_path;
    String camera_id;
	Bitmap image_bitmap;
    Boolean origemGallery = false;
	
	Bitmap myBitmap = null;
    private boolean isAlbum;
    private int imageID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_confirmation);
        File image;
		isAlbum = false;
        //Get any potential intent that was passed to the activity
        Bitmap bmp = null;
        Intent intent = getIntent();
        Uri imageURI = null;
        show_picture = (ImageView) findViewById(R.id.confirm_shown_picture);
        Boolean openImage = false;
        assert show_picture != null;
        //        System.out.println("TESTE ------ " + intent);
        if (intent != null) {
        //            System.out.println("ENTROU INTENT ------ ");
            //Is there a string extra in that intent?
            image_path = intent.getStringExtra("PICTURE");
            camera_id = intent.getStringExtra("CAMERA");
            if(intent.hasExtra("IMAGE_ID")){
                isAlbum = true;
                String validate = intent.getStringExtra("IMAGE_ID");
                System.out.println("Validate camera_id " + validate);
                if(validate != null && !validate.trim().equals("")){

                    imageID = Integer.parseInt(intent.getStringExtra("IMAGE_ID"));
                    System.out.println("ORIGEM ALBUM - " + imageID);

                }

            }

            if(intent.hasExtra("GALLERY"))
            {
                origemGallery = true;
            }
            if(intent.hasExtra("image"))
            {
                imageURI = intent.getData();
                openImage = true;
                System.out.println("image " + intent.getStringExtra("image"));
                String filename = getIntent().getStringExtra("image");
                try {
                    FileInputStream is = this.openFileInput(filename);
                    bmp = BitmapFactory.decodeStream(is);
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("IMAGE PATH ---------- " + image_path );
            File imgFile = new  File(image_path);

            if(imgFile.exists()){

                myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                if (camera_id.equals(CAMERA_FRONT) /*CAMERA FRONTAL*/)
                {
                    myBitmap =  flipImage(myBitmap);
                }
                show_picture.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                show_picture.setScaleType(ImageView.ScaleType.FIT_CENTER);
                show_picture.setAdjustViewBounds(true);
                show_picture.setImageBitmap(myBitmap);

            }else {
                if (openImage && imageURI != null)
                {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageURI);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    show_picture.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    show_picture.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    show_picture.setAdjustViewBounds(true);
                    show_picture.setImageBitmap(bitmap);

                }
            }
        }

        save_picture = (ImageButton) findViewById(R.id.btn_save);
        assert save_picture != null;

        cancel_picture = (ImageButton) findViewById(R.id.btn_cancel);
        assert cancel_picture != null;

        save_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!origemGallery)
                {
                savePicture(image_path);
            }

                if(isAlbum)
                {
                   // uploadMultipart();
                }else{
                    backToPreviousScreen(image_path);
                }

            }
        });

        cancel_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!origemGallery)
                {
                deletePicture(image_path);
            }
                backToCamera();

            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    public void backToPreviousScreen(String image_path){

        //Intent backActivity = new Intent(this,CameraActivity.class);
       // dbRegisterClient.updateImagePathTempRegister(image_path);
//                    String teste = dbRegisterClient.getTableAsString(dbRegisterClient.getReadableDatabase(),"TempRegister");
        //if(!image_path.trim().equals(""))
        //   backActivity.putExtra("IMAGE_PATCH",image_path);


        finish();
        myBitmap.recycle();
    }

    public void backToCamera(){

        Intent backActivity = new Intent(this,CameraActivity.class);
        finish();
        myBitmap.recycle();
        startActivity(backActivity);
    }
    public static Bitmap rotateImage(Bitmap imageToOrient, int degreesToRotate) {
        Bitmap result = imageToOrient;
        try {
            if (degreesToRotate != 0) {
                Matrix matrix = new Matrix();
                matrix.setRotate(degreesToRotate);
                result = Bitmap.createBitmap(
                        imageToOrient,
                        0,
                        0,
                        imageToOrient.getWidth(),
                        imageToOrient.getHeight(),
                        matrix,
                        true);
            }
        } catch (Exception e) {
            if (Log.isLoggable("PictureConfirmation", Log.ERROR)) {
                Log.e("PictureConfirmation", "Exception when trying to orient image", e);
            }
        }
        return result;
    }

    public static Bitmap flipImage(Bitmap imageToOrient) {
        Bitmap result = imageToOrient;

            Matrix matrix = new Matrix();
            matrix.postScale(-1, 1, result.getWidth()/2f, result.getHeight()/2f);
            result = Bitmap.createBitmap(
                        imageToOrient,
                        0,
                        0,
                        imageToOrient.getWidth(),
                        imageToOrient.getHeight(),
                        matrix,
                        true);

        return result;
    }

    public void savePicture(String filePath)
    {
        File imgFile = new  File(filePath);

        //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

        //Toast.makeText(PictureConfirmation.this, "Saved:" + filePath, Toast.LENGTH_SHORT).show();
//        insertImage(getContentResolver(),
//                myBitmap,
//                "Test",
//                "GoMovvn picture");
        galleryAddPic(filePath);

    }

    public void deletePicture(String filePath)
    {
        File file = new File(filePath);
        file.delete();
//        Toast.makeText(PictureConfirmation.this, "Cancel Picture:", Toast.LENGTH_SHORT).show();

    }

    private void galleryAddPic(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    /**
     * A copy of the Android internals  insertImage method, this method populates the
     * meta data with DATE_ADDED and DATE_TAKEN. This fixes a common problem where media
     * that is inserted manually gets saved at the end of the gallery (because date is not populated).
     * @see android.provider.MediaStore.Images.Media#insertImage(ContentResolver, Bitmap, String, String)
     */
    public static final String insertImage(ContentResolver cr,
                                           Bitmap source,
                                           String title,
                                           String description) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        // Add the date meta data to ensure the image is added at the front of the gallery
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

        Uri url = null;
        String stringUrl = null;    /* value to be returned */

        try {
            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (source != null) {
                OutputStream imageOut = cr.openOutputStream(url);
                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut);
                } finally {
                    imageOut.close();
                }

                long id = ContentUris.parseId(url);
                // Wait until MINI_KIND thumbnail is generated.
                Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
                // This is for backward compatibility.
                storeThumbnail(cr, miniThumb, id, 50F, 50F, MediaStore.Images.Thumbnails.MICRO_KIND);
            } else {
                cr.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }

        if (url != null) {
            stringUrl = url.toString();
        }

        return stringUrl;
    }

    /**
     * A copy of the Android internals StoreThumbnail method, it used with the insertImage to
     * populate the android.provider.MediaStore.Images.Media#insertImage with all the correct
     * meta data. The StoreThumbnail method is private so it must be duplicated here.
     * @see android.provider.MediaStore.Images.Media (StoreThumbnail private method)
     */
    private static final Bitmap storeThumbnail(
            ContentResolver cr,
            Bitmap source,
            long id,
            float width,
            float height,
            int kind) {

        // create the matrix to scale it
        Matrix matrix = new Matrix();

        float scaleX = width / source.getWidth();
        float scaleY = height / source.getHeight();

        matrix.setScale(scaleX, scaleY);

        Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
                source.getWidth(),
                source.getHeight(), matrix,
                true
        );

        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Images.Thumbnails.KIND,kind);
        values.put(MediaStore.Images.Thumbnails.IMAGE_ID,(int)id);
        values.put(MediaStore.Images.Thumbnails.HEIGHT,thumb.getHeight());
        values.put(MediaStore.Images.Thumbnails.WIDTH,thumb.getWidth());

        Uri url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream thumbOut = cr.openOutputStream(url);
            thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
            thumbOut.close();
            return thumb;
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }

}

//Toast.makeText(MainActivity.this, "Saved:" + file, Toast.LENGTH_SHORT).show();