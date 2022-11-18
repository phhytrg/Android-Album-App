package com.example.album.album;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.album.R;
import com.google.android.material.imageview.ShapeableImageView;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>
        implements View.OnClickListener, View.OnLongClickListener {

    public void setNavController(NavController navController) {
        this.navController = navController;
    }

    NavController navController;
    //data field
    String[] albumNames = {"Camera","Videos", "Favorites","Screens","Locations","Download", "Collages",
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
        holder.albumName.setText(albumNames[position]);
        holder.shapeableImageView.setOnClickListener(v -> {
            if(navController == null){
                return;
            }
            String albumName = holder.albumName.getText().toString();
            NavDirections action = AlbumFragmentDirections
                    .actionAlbumFragmentToDetailAlbumFragment(albumName);
            navController.navigate(action);
        });

        holder.albumName.setOnClickListener(v -> {
            if(navController == null){
                return;
            }
            String albumName = holder.albumName.getText().toString();
            NavDirections action = AlbumFragmentDirections
                    .actionAlbumFragmentToDetailAlbumFragment(albumName);
            Navigation.findNavController(holder.itemView).navigate(action);
        });
    }

    @Override
    public int getItemCount() {
        return albumNames.length;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
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
