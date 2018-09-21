package com.example.angelica.cameratest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.angelica.cameratest.GalleryImageItemFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;


public class GalleryImageItemRecyclerViewAdapter extends RecyclerView.Adapter<GalleryImageItemRecyclerViewAdapter.ViewHolder> {

    private final GalleryImageItemFragment.OnListFragmentInteractionListener mListener;

    private ArrayList<LoadedImage> photos = new ArrayList<LoadedImage>();
    Context context;


    public GalleryImageItemRecyclerViewAdapter(ArrayList<LoadedImage> photos, Context context, GalleryImageItemFragment.OnListFragmentInteractionListener listener) {
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
        final String message = String.valueOf(position);
//        holder.mIdView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    mListener.onListFragmentInteraction(holder.getImageData);
//                    Toast.makeText(v.getContext(),holder.getImageData.getPath(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final ImageView mIdView;
        //public final TextView mContentView;
        public LoadedImage getImageData;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (ImageView) view.findViewById(R.id.id_camera_image);
            view.setOnClickListener(this);
            //mContentView = (TextView) view.findViewById(R.id.content);
        }


        @Override
        public void onClick(View view) {
            final int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                System.out.println("Click " + position);
                Toast.makeText(view.getContext(), "Click " + position + " photo " + photos.get(position).getPath(), Toast.LENGTH_SHORT).show();
                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(photos.get(position));
                }
            }
        }

    }
}
