package com.example.album.album;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.album.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>{

    private int currentState;

    public void setAllSelectedFlags(boolean allSelectedFlags) {
        this.allSelectedFlags = allSelectedFlags;
    }

    private boolean allSelectedFlags;
    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    public interface OnClickListener {
        void onItemClick(AlbumAdapter.AlbumViewHolder holder);
        void onCheckBoxClick(AlbumAdapter.AlbumViewHolder holder);
        void onItemLongClick(AlbumAdapter.AlbumViewHolder holder);
    }

    private final OnClickListener listener;

    public final static int LINEAR_LAYOUT = 0;
    public final static int GRID_LAYOUT = 1;
    private List<Integer> albumImages;
    private List<String> albumNames;
    private int layoutType;

//    public final static int CHANGED_MODE = 1;
//    public final static int UNCHANGED_MODE = 0;
    private MutableLiveData<Integer> totalItemsSelected;

    public MutableLiveData<Integer> getTotalItemsSelected(){
        if(totalItemsSelected == null){
            totalItemsSelected = new MutableLiveData<>();
        }
        return totalItemsSelected;
    }

    public void setLayoutType(int layoutType) {
        this.layoutType = layoutType;
    }

    public AlbumAdapter(List<String> albumNames, List<Integer>albumImages,
                        OnClickListener listener, int layoutType) {
        this.layoutType = layoutType;
        this.albumImages = albumImages;
        this.albumNames = albumNames;
        this.listener = listener;
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
            return new AlbumViewHolder(view);
        }
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linear_album_item, parent, false)
                .getRootView();
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        holder.shapeableImageView.setImageResource(albumImages.get(position));
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

    class AlbumViewHolder extends RecyclerView.ViewHolder {
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
                numImages.setText(Integer.toString(100));

            }

            itemView.setOnClickListener(v -> listener.onItemClick(this));
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
