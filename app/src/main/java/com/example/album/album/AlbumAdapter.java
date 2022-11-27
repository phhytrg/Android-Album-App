package com.example.album.album;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.album.R;
import com.example.album.data.Image;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>{

    private final TreeMap<String, List<Image>> map;
    private int currentState;

    public void setAllSelectedFlags(boolean allSelectedFlags) {
        this.allSelectedFlags = allSelectedFlags;
    }

    private boolean allSelectedFlags;
    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    public interface OnClickListener {
        void onItemClick(AlbumAdapter.AlbumViewHolder holder, List<Image>images);
        void onCheckBoxClick(AlbumAdapter.AlbumViewHolder holder);
        void onItemLongClick(AlbumAdapter.AlbumViewHolder holder);
    }

    private final OnClickListener listener;

    public final static int LINEAR_LAYOUT = 0;
    public final static int GRID_LAYOUT = 1;

    private List<Image> albumImages;
    private List<String> albumNames;
    private int layoutType;
    Context context;

//    public final static int CHANGED_MODE = 1;
//    public final static int UNCHANGED_MODE = 0;

    public void setLayoutType(int layoutType) {
        this.layoutType = layoutType;
    }

    public AlbumAdapter(TreeMap<String, List<Image>> map,
                        OnClickListener listener, int layoutType) {
        this.layoutType = layoutType;
        this.map = map;
        this.listener = listener;
        exec();
    }

    private void exec(){
        this.albumNames = new ArrayList<>();
        this.albumNames.addAll(map.keySet());
        this.albumImages = map.get(map.firstKey());
    }


    //data field
    private final List<CheckBox> checkBoxes = new ArrayList<>();

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(layoutType == GRID_LAYOUT){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.grid_album_item, parent, false)
                    .getRootView();
            context = parent.getContext();
            return new AlbumViewHolder(view);
        }
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linear_album_item, parent, false)
                .getRootView();
        context = parent.getContext();
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
//        Uri imageUri = Objects.requireNonNull(map.get(albumNames.get(position))).get(0).getImageUri();
        albumImages = map.get(albumNames.get(position));
        if(albumImages != null){
            Uri imageUri = albumImages.get(0).getImageUri();
            Glide.with(context)
                    .load(imageUri)
                    .into(holder.shapeableImageView);
            if(holder.numImages != null){
                holder.numImages.setText(Integer.toString(albumImages.size()));
            }
        }
        else{
            holder.shapeableImageView.setImageResource(R.drawable.default_image_thumbnail);
            holder.numImages.setText("0");
        }
        holder.albumName.setText(albumNames.get(position));
        if(currentState == AlbumFragment.UNCHANGED_MODE){
            holder.checkbox.setVisibility(View.GONE);
        }
        else{
            holder.checkbox.setVisibility(View.VISIBLE);
            if(allSelectedFlags){
                holder.checkbox.setChecked(true);
            }
            else{
                holder.checkbox.setChecked(false);
            }
        }
        checkBoxes.add(holder.checkbox);
    }

    @Override
    public int getItemCount() {
        return albumNames.size();
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView shapeableImageView;
        TextView albumName;
        TextView numImages;
        ConstraintLayout item;
        CheckBox checkbox;

        boolean isCheckedFlag = false;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            shapeableImageView = itemView.findViewById(R.id.album_image);
            albumName = itemView.findViewById(R.id.album_name);
            item = itemView.findViewById(R.id.album_item);
            checkbox = itemView.findViewById(R.id.selected_item);

            checkbox.setOnClickListener(v -> listener.onCheckBoxClick(this));
            if (layoutType == LINEAR_LAYOUT) {
                numImages = itemView.findViewById(R.id.number_of_images);
            }

            itemView.setOnClickListener(v -> listener.onItemClick(this, albumImages));
            itemView.setOnLongClickListener(v -> {
                listener.onItemLongClick(this);
                setCheckBoxesVisible();
                return true;
            });
        }
    }

    public void selectAll(){
        for(int i =0 ;i<checkBoxes.size(); i++){
            checkBoxes.get(i).setChecked(true);
        }
    }

    public void unSelectAll(){
        for(int i =0 ;i<checkBoxes.size(); i++){
            checkBoxes.get(i).setChecked(false);
        }
    }

    public void setCheckBoxesVisible(){
        for(int i =0 ;i<checkBoxes.size(); i++){
            checkBoxes.get(i).setVisibility(View.VISIBLE);
        }
    }

    public void setCheckBoxesInvisible(){
        for(int i =0 ;i<checkBoxes.size(); i++){
            checkBoxes.get(i).setVisibility(View.GONE);
        }
    }
}


//                    final Dialog dialog = new Dialog(context, R.style.Theme_Album);
//                    dialog.setContentView(R.layout.selected_option);
//                    WindowManager.LayoutParams params = new WindowManager.LayoutParams();
//                    params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//                    params.width = WindowManager.LayoutParams.MATCH_PARENT;
//                    params.format = PixelFormat.TRANSLUCENT;
//                    params.gravity = Gravity.TOP;
//                    params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
//                    dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//                    dialog.getWindow().setAttributes(params);
