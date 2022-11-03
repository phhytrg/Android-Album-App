package com.example.album.album;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.album.R;
import com.google.android.material.imageview.ShapeableImageView;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>
        implements View.OnClickListener{

    //data field
    String[] albumName = {"Camera","Videos", "Favorites","Screens","Locations","Download", "Collages",
            "Picnic", "Friends", "Selfie", "Memes"};
    int[] albumImages = {R.drawable.photo1, R.drawable.photo2, R.drawable.photo10,
            R.drawable.photo4, R.drawable.cat1, R.drawable.photo6, R.drawable.photo3,
            R.drawable.photo5, R.drawable.photo8, R.drawable.photo7, R.drawable.photo9};

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_item,parent,false)
                .getRootView();
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        holder.shapeableImageView.setImageResource(albumImages[position]);
        holder.albumName.setText(albumName[position]);
        holder.shapeableImageView.setOnClickListener(this);
        holder.albumName.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return albumName.length;
    }

    @Override
    public void onClick(View v) {

    }

    class AlbumViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView shapeableImageView;
        TextView albumName;
        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            shapeableImageView = itemView.findViewById(R.id.album_image);
            albumName = itemView.findViewById(R.id.album_name);
        }
    }
}
