package com.example.album.detail_album;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.album.R;
import com.example.album.data.Image;

import java.util.List;

public class DetailAlbumAdapter extends RecyclerView.Adapter<DetailAlbumAdapter.GalleryViewHolder> {

    public interface OnClickListener{
        void OnItemClick(DetailAlbumAdapter.GalleryViewHolder holder, int position);
    }
//    View view;
//    Context context;

    private OnClickListener listener;

    private boolean isLinearLayout;
    public void setLinearLayout(boolean linearLayout) {
        isLinearLayout = linearLayout;
    }
    Context context;
    List<Image> images;

    public DetailAlbumAdapter(OnClickListener listener, List<Image> images) {
        this.listener = listener;
        this.images = images;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_item,parent,false);
        context = parent.getContext();
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
//        holder.imageView.setImageURI(images.get(position).getImageUri());
        Glide.with(context)
                .load(images.get(position).getImageUri())
                .into(holder.imageView);
        ConstraintLayout.LayoutParams itemView = (ConstraintLayout.LayoutParams)holder.imageView.getLayoutParams();
        if(isLinearLayout){
            int h = holder.imageView.getDrawable().getIntrinsicHeight();
            int w = holder.imageView.getDrawable().getIntrinsicWidth();
            itemView.dimensionRatio = "H,"+ w +":"+ h;
        }else{
            itemView.dimensionRatio = "H,1:1";
            itemView.width=0;
            itemView.height=0;
        }
        holder.imageView.setOnClickListener(v -> {listener.OnItemClick(holder, position);});
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class GalleryViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
}