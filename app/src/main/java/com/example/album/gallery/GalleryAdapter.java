package com.example.album.gallery;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.example.album.R;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    public void setLinearLayout(boolean linearLayout) {
        isLinearLayout = linearLayout;
    }

    boolean isLinearLayout;
    View view;

    int[] images = {R.drawable.image2,R.drawable.image2,R.drawable.image2,
            R.drawable.image2,R.drawable.image2,R.drawable.image2,
            R.drawable.image2,R.drawable.image2,R.drawable.image2,
            R.drawable.image2,R.drawable.image2,R.drawable.image2,
            R.drawable.image2,R.drawable.image2,R.drawable.image2,
            R.drawable.image2,R.drawable.image2,R.drawable.image2};

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_item,parent,false);
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        holder.imageView.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public class GalleryViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}