package com.example.album.detail_album;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.album.R;

public class DetailAlbumAdapter extends RecyclerView.Adapter<DetailAlbumAdapter.GalleryViewHolder> {

    View view;

    boolean isLinearLayout = false;

    public void setLinearLayout(boolean linearLayout) {
        isLinearLayout = linearLayout;
    }

    int[] images = {R.drawable.photo1,R.drawable.photo2,R.drawable.photo2,
            R.drawable.photo3,R.drawable.photo4,R.drawable.photo5,
            R.drawable.photo6,R.drawable.photo7,R.drawable.photo8,
            R.drawable.image,R.drawable.image2,R.drawable.image2,
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
        ConstraintLayout.LayoutParams a = (ConstraintLayout.LayoutParams)holder.imageView.getLayoutParams();
        if(isLinearLayout){
            int h = holder.imageView.getDrawable().getIntrinsicHeight();
            int w = holder.imageView.getDrawable().getIntrinsicWidth();
            a.dimensionRatio = "H,"+ w +":"+ h;
        }else{
            a.dimensionRatio = "H,1:1";
            a.width=0;
            a.height=0;
        }
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