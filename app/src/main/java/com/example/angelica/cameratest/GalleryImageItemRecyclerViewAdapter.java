package com.example.angelica.cameratest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.angelica.cameratest.GalleryImageItemFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;


public class GalleryImageItemRecyclerViewAdapter extends RecyclerView.Adapter<GalleryImageItemRecyclerViewAdapter.ViewHolder> {

    private final OnListFragmentInteractionListener mListener;

    private ArrayList<LoadedImage> photos = new ArrayList<LoadedImage>();
    Context context;


    public GalleryImageItemRecyclerViewAdapter(ArrayList<LoadedImage> photos, Context context, OnListFragmentInteractionListener listener) {
        super();
        this.photos = photos;
        this.mListener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_galleryimageitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.getImageData = photos.get(position);
        holder.mIdView.setImageBitmap(photos.get(position).getBitmap());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.getImageData);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mIdView;
        //public final TextView mContentView;
        public LoadedImage getImageData;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (ImageView) view.findViewById(R.id.id_camera_image);
            //mContentView = (TextView) view.findViewById(R.id.content);
        }

    }
}
