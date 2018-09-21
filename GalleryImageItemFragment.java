package com.example.angelica.cameratest;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static android.provider.MediaStore.Images.Thumbnails.MICRO_KIND;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class GalleryImageItemFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    ArrayList<LoadedImage> photos;
    RecyclerView recyclerView;

    RecyclerView.LayoutManager recyclerViewlayoutManager;
    private LoadImagesFromSDCard mLoadImagesTask = null;
    RecyclerView.Adapter recyclerViewadapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GalleryImageItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static GalleryImageItemFragment newInstance(int columnCount) {
        GalleryImageItemFragment fragment = new GalleryImageItemFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photos = new ArrayList<LoadedImage>();
        mListener = (OnListFragmentInteractionListener) getActivity();

        int permissionCheck = ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        } else {
            mLoadImagesTask = new LoadImagesFromSDCard();
            mLoadImagesTask.execute((Void) null);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted and now can proceed
                    mLoadImagesTask = new LoadImagesFromSDCard();
                    mLoadImagesTask.execute((Void) null);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // add other cases for more permissions
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_galleryimageitem_list, container, false);

        //System.out.println("Gallery Image Item Fragment --- onCreateView");

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerViewlayoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
            recyclerView.setLayoutManager(recyclerViewlayoutManager);

            recyclerViewadapter = new GalleryImageItemRecyclerViewAdapter(photos, context, mListener);

            recyclerView.setAdapter(recyclerViewadapter);

        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(LoadedImage image);
    }

    public class LoadImagesFromSDCard extends AsyncTask<Object, LoadedImage, Object> {

        /**
         * Load images from SD Card in the background, and display each image on the screen.
         *
         */
        @Override
        protected Object doInBackground(Object... params) {
            //setProgressBarIndeterminateVisibility(true);
            Bitmap bitmap = null;
            Bitmap newBitmap = null;
            Uri uri = null;

            // System.out.println("STARTED ACTIVITY - GALLERY ");
//            uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            // Set up an array of the Thumbnail Image ID column we want
            String[] columnsImages = {MediaStore.Images.Media._ID,MediaStore.Images.Media.DATE_TAKEN,MediaStore.Images.Media.DATA};
            String sortOrderImages = MediaStore.Images.Media.DATE_TAKEN + " DESC";
            String[] columns = {MediaStore.Images.Thumbnails._ID,MediaStore.Images.Thumbnails.IMAGE_ID};
            String sortOrder = MediaStore.Images.Thumbnails.IMAGE_ID + " DESC";
            // Create the cursor pointing to the SDCard
            if(getActivity() != null && getActivity().getContentResolver() != null){
                Cursor cursor = getActivity().getContentResolver().query(uri, columnsImages, null, null, sortOrderImages  );
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int column_index2 = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int size = cursor.getCount();
            // If size is 0, there are no images on the SD Card.
            if (size == 0) {
                //No Images available, post some message to the user
            }
            int imageID = 0;
                String fileName = "";
            for (int i = 0; i < size; i++) {
                cursor.moveToPosition(i);
                imageID = cursor.getInt(columnIndex);
                    fileName = cursor.getString(column_index2);
                    // System.out.println("filename " + fileName);

                    uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + imageID);
                    Bitmap image = null;

//                if(getContext() != null){
//
//                    image = getThumbnailBitmap(uri);

//                            MediaStore.Images.Thumbnails.getThumbnail(getContext().getContentResolver(),imageID,MediaStore.Images.Thumbnails.MICRO_KIND,null);
//                uri = Uri.withAppendedPath(
//                        MediaStore.Images.Thumbnails.getThumbnail(getContext().getContentResolver(),imageID,MediaStore.Images.Thumbnails.MICRO_KIND,null)
                    //MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                                , "" + imageID);
//                windows[i] = MediaStore.Images.Thumbnails.getThumbnail(
//                        this.getApplicationContext().getContentResolver(), imageID,
//                        MediaStore.Images.Thumbnails.MICRO_KIND, null);

                try {
//                    if (image != null && getActivity() != null)
                    if (getActivity() != null)
                    {
                            //String path = getThumbnailPath(getContext(),uri.getPath());
                            //bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
                            //verify if uri is valid
                            if(bitmap != null)
                                bitmap.recycle();

                            if(image != null)
                                image.recycle();

                            InputStream is = getActivity().getContentResolver().openInputStream(uri);
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.RGB_565;
                            options.inSampleSize = 4;
                            options.inJustDecodeBounds = true; //avoid allocate memory
                            bitmap = BitmapFactory.decodeStream(is,null,options);
                            is.close();

                            if(getContext() !=null && getContext().getContentResolver()!= null) {
                                BitmapFactory.Options optionsthumb = new BitmapFactory.Options();
                                options.inSampleSize = 4;
                                options.inJustDecodeBounds = false; //avoid allocate memory
                                image = MediaStore.Images.Thumbnails.getThumbnail(getContext().getContentResolver(), imageID, MICRO_KIND, optionsthumb);
                            }

                            //MediaStore.Images.Thumbnails.getThumbnail(getContext().getContentResolver(),imageID,MediaStore.Images.Thumbnails.MICRO_KIND,null);
//                        Cursor cursorThumb = MediaStore.Images.Thumbnails.queryMiniThumbnail(getContext().getContentResolver(), imageID, MediaStore.Images.Thumbnails.MINI_KIND, null);
                            if (image != null)
                            {
//                            cursorThumb.moveToFirst();
//                            image =
//                            result = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));
//                            cursor.close();
                                bitmap = image;
                            }

//                        System.out.println("Thumbnail path " + path + " imageid " + imageID);
//                        bitmap = image;
//                        boolean isLandscape = bitmap.getWidth() > bitmap.getHeight();

                        int newWidth, newHeight;
//                        if (isLandscape)
//                        {
//                            newWidth = 100;
//                            newHeight = Math.round(((float) newWidth / bitmap.getWidth()) * bitmap.getHeight());
//                        } else
//                        {
//                            newHeight = 100;
//                            newWidth = Math.round(((float) newHeight / bitmap.getHeight()) * bitmap.getWidth());
//                        }



                            if (bitmap != null) {
                        newWidth = 100;
                        double aspectRatio = (double) bitmap.getHeight() / (double) bitmap.getWidth();
                        newHeight = (int) (newWidth * aspectRatio);

                            newBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
                            bitmap.recycle();
                            if (newBitmap != null) {

                                    publishProgress(new LoadedImage(newBitmap,fileName));
                            }
                        }
                    }
                    else{
                            //System.out.println("------------------ Activity NULL ------------------ ");
                        this.cancel(true);
                    }
//                }else{
//
//                    System.out.println("------------------ context NULL ------------------ ");
//                    this.cancel(true);
//                }
                } catch (IOException e) {
                    //Error fetching image, try to recover
                    //break;
                }
            }
            cursor.close();
            return null;
        }
            else {
                return null;
            }
        }

        @Override
        protected void onCancelled(Object o) {
            super.onCancelled(o);
            //System.out.println("CANCELOU ASYNCTASK - IMAGES");
        }

        /**
         * Add a new LoadedImage in the images grid.
         *
         * @param value The image.
         */
        @Override
        public void onProgressUpdate(LoadedImage... value) {
            for (LoadedImage image : value) {
                //System.out.println("image " + image.getPath());
                photos.add(image);
                recyclerViewadapter.notifyDataSetChanged();
            }
        }
        /**
         * Set the visibility of the progress bar to false.
         *
         * @see AsyncTask#onPostExecute(Object)
         */
        @Override
        protected void onPostExecute(Object result) {
            //setProgressBarIndeterminateVisibility(false);
            recyclerViewadapter.notifyDataSetChanged();
        }
    }

    public static String getThumbnailPath(Context context, String path)
    {
        long imageId = -1;

        String[] projection = new String[] { MediaStore.MediaColumns._ID };
        String selection = MediaStore.MediaColumns.DATA + "=?";
        String[] selectionArgs = new String[] { path };
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null);
        if (cursor != null && cursor.moveToFirst())
        {
            imageId = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
        }

        String result = null;
        cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(context.getContentResolver(), imageId, MediaStore.Images.Thumbnails.MINI_KIND, null);
        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            result = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));
            cursor.close();
        }

        return result;
    }
}
