package com.example.album.album;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.album.R;
import com.example.album.data.Image;
import com.example.album.detail_album.DetailAlbumAdapter;
import com.example.album.gallery.DateUtils;
import com.example.album.gallery.HeaderItem;
import com.example.album.gallery.ImageItem;
import com.example.album.gallery.ListItem;
import com.example.album.gallery.PhotosAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class pickImagesAdapter extends RecyclerView.Adapter<pickImagesAdapter.PickImageViewHolder> {

    public interface OnClickListener {
        void onItemClick(pickImagesAdapter.PickImageViewHolder holder);
        void onCheckBoxClick(pickImagesAdapter.PickImageViewHolder holder);
//        void onItemLongClick(pickImagesAdapter.PickImageViewHolder holder);
    }

    private OnClickListener listener;
    Context context;
    List<Image> images;
    private boolean allSelectedFlags;

    public void setAllSelectedFlags(boolean allSelectedFlags) {
        this.allSelectedFlags = allSelectedFlags;
    }

    public pickImagesAdapter(pickImagesAdapter.OnClickListener listener, List<Image> images) {
        this.listener = listener;
        this.images = images;
    }

    @NonNull
    @Override
    public pickImagesAdapter.PickImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_item,parent,false);
        context = parent.getContext();
        return new pickImagesAdapter.PickImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull pickImagesAdapter.PickImageViewHolder holder, int position) {
        holder.checkbox.setVisibility(View.VISIBLE);
        if(allSelectedFlags){
            holder.checkbox.setChecked(true);
        }
        else{
            holder.checkbox.setChecked(false);
        }
        Glide.with(context)
                .load(images.get(position).getImageUri())
                .into(holder.imageView);
        ConstraintLayout.LayoutParams itemView = (ConstraintLayout.LayoutParams)holder.imageView.getLayoutParams();
        itemView.dimensionRatio = "H,1:1";
        itemView.width=0;
        itemView.height=0;
        checkBoxes.add(holder.checkbox);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    private final List<CheckBox> checkBoxes = new ArrayList<>();

    public class PickImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public CheckBox checkbox;
        public boolean isCheckedFlag = false;

        public PickImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            checkbox = itemView.findViewById(R.id.selected_item);

            checkbox.setOnClickListener(v -> listener.onCheckBoxClick(this));
            itemView.setOnClickListener(v -> listener.onItemClick(this));
//            itemView.setOnLongClickListener(v -> {
//                listener.onItemLongClick(this);
//                setCheckBoxesVisible();
//                return true;
//            });
        }
    }

    public void selectAll() {
        for (int i = 0; i < checkBoxes.size(); i++) {
            checkBoxes.get(i).setChecked(true);
        }
    }

    public void unSelectAll() {
        for (int i = 0; i < checkBoxes.size(); i++) {
            checkBoxes.get(i).setChecked(false);
        }
    }

    public void setCheckBoxesVisible() {
        for (int i = 0; i < checkBoxes.size(); i++) {
            checkBoxes.get(i).setVisibility(View.VISIBLE);
        }
    }

    public void setCheckBoxesInvisible() {
        for (int i = 0; i < checkBoxes.size(); i++) {
            checkBoxes.get(i).setVisibility(View.GONE);
        }
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
}