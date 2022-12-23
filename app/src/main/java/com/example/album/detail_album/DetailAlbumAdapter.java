package com.example.album.detail_album;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.example.album.R;
import com.example.album.data.Image;

import java.util.ArrayList;
import java.util.List;

public class DetailAlbumAdapter extends RecyclerView.Adapter<DetailAlbumAdapter.GalleryViewHolder> {

    public interface OnClickListener{
        void OnItemClick(DetailAlbumAdapter.GalleryViewHolder holder, int position);
        void OnItemLongClick(DetailAlbumAdapter.GalleryViewHolder holder);
        void OnCheckBoxClick(DetailAlbumAdapter.GalleryViewHolder holder);
    }
//    View view;
//    Context context;

    private OnClickListener listener;

    private boolean allSelectedFlags;

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    public int currentState = DetailAlbumFragment.UNCHANGED_MODE;

    private boolean isLinearLayout;
    public void setLinearLayout(boolean linearLayout) {
        isLinearLayout = linearLayout;
    }
    Context context;
    List<Image> images;
    private final List<CheckBox> checkBoxes = new ArrayList<>();

    public DetailAlbumAdapter(OnClickListener listener, List<Image> images) {
        this.listener = listener;
        this.images = images;
    }

    public void setAllSelectedFlags(boolean allSelectedFlags) {
        this.allSelectedFlags = allSelectedFlags;
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
                .signature(new ObjectKey(images.get(position).getId()))
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
        if(currentState == DetailAlbumFragment.CHANGED_MODE){
            holder.checkBox.setVisibility(View.VISIBLE);
            if(allSelectedFlags){
                holder.checkBox.setChecked(true);
            }
            else{
                holder.checkBox.setChecked(false);
            }
        }
        else{
            holder.checkBox.setVisibility(View.GONE);
        }
        checkBoxes.add(holder.checkBox);
        holder.imageView.setOnClickListener(v -> listener.OnItemClick(holder, position));
        holder.imageView.setOnLongClickListener(v -> {
            listener.OnItemLongClick(holder);
            setCheckBoxesVisible();
            return true;});
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class GalleryViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        CheckBox checkBox;
        boolean isCheckedFlag = false;
        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            checkBox = itemView.findViewById(R.id.selected_item);

            checkBox.setOnClickListener(v -> listener.OnCheckBoxClick(this));
//            if (layoutType == LINEAR_LAYOUT) {
//                numImages = itemView.findViewById(R.id.number_of_images);
//            }

        }
    }

    public void setImages(List<Image> images) {
        this.images = images;
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
}