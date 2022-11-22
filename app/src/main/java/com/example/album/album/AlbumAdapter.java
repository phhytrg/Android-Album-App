package com.example.album.album;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.album.R;
import com.google.android.material.imageview.ShapeableImageView;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>
        implements View.OnClickListener, View.OnLongClickListener {

    public final static int LINEAR_LAYOUT = 0;
    public final static int GRID_LAYOUT = 1;
    private int layoutType;
    private Context context;
    Fragment fragment;

    public void setLayoutType(int layoutType) {
        this.layoutType = layoutType;
    }

    public void setNavController(NavController navController) {
        this.navController = navController;
    }

    public AlbumAdapter(Context context, Fragment fragment, NavController navController, int layoutType) {
        this.layoutType = layoutType;
        this.navController = navController;
        this.context = context;
        this.fragment = fragment;
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
        View view;
        if(layoutType == GRID_LAYOUT){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.grid_album_item, parent, false)
                    .getRootView();
            return new AlbumViewHolder(view);
        }
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linear_album_item, parent, false)
                .getRootView();
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        holder.shapeableImageView.setImageResource(albumImages[position]);
        holder.albumName.setText(albumNames[position]);
    }

    private void onItemClick(AlbumViewHolder holder){
        if(navController == null){
            return;
        }
        String albumName = holder.albumName.getText().toString();
        NavDirections action = AlbumFragmentDirections
                .actionAlbumFragmentToDetailAlbumFragment(albumName);
        Navigation.findNavController(holder.itemView).navigate(action);
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
        TextView numImages;
        ConstraintLayout item;
        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            shapeableImageView = itemView.findViewById(R.id.album_image);
            albumName = itemView.findViewById(R.id.album_name);
            item = itemView.findViewById(R.id.album_item);
            if(layoutType == GRID_LAYOUT){
            }
            if(layoutType == LINEAR_LAYOUT){
                numImages = itemView.findViewById(R.id.number_of_images);
            }

            itemView.setOnClickListener(v -> onItemClick(this));
            if(layoutType == LINEAR_LAYOUT){
                numImages.setText(Integer.toString(100));
            }
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showDialog();

                    return true;
                }
            });
        }
    }

    private void showDialog(){

//        final BottomSheetDialog dialog = new BottomSheetDialog(context);
//        dialog.setContentView(R.layout.multi_items_selected);
//        dialog.set
        final Dialog dialog = new Dialog(context, R.style.Theme_Album);
        dialog.setContentView(R.layout.multi_items_selected);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.format = PixelFormat.TRANSLUCENT;

        params.gravity = Gravity.TOP;
        dialog.getWindow().setAttributes(params);

//        Window window = dialog.getWindow();
//        WindowManager.LayoutParams wlp
//                = window.getAttributes();
//        wlp.width =
//                wlp.gravity = Gravity.TOP;
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//        window.setAttributes(wlp);
        dialog.show();
    }
}
