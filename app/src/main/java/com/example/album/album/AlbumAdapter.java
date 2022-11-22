package com.example.album.album;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.album.R;
import com.example.album.SplitToolbar;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    public final static int LINEAR_LAYOUT = 0;
    public final static int GRID_LAYOUT = 1;
    private int layoutType;
    private Context context;
    public final static int CHANGED_MODE = 1;
    public final static int UNCHANGED_MODE = 0;

    private int currentMode = UNCHANGED_MODE;
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
        selectedItems = new ArrayList<>();
    }

    NavController navController;
    //data field
    List<String> albumNames = new ArrayList<>(Arrays.asList("Camera", "Videos", "Favorites", "Screens", "Locations", "Download", "Collages",
            "Picnic", "Friends", "Selfie", "Memes"));
    List<Integer> albumImages = new ArrayList<>(Arrays.asList(R.drawable.photo1, R.drawable.photo2,
            R.drawable.photo10, R.drawable.photo4, R.drawable.cat1, R.drawable.photo6,
            R.drawable.photo3, R.drawable.photo5, R.drawable.photo8, R.drawable.photo7,
            R.drawable.photo9));
    List<Integer>selectedItems;

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
        if(currentMode == CHANGED_MODE){
            holder.checked.setVisibility(View.VISIBLE);
        }
        else{
            holder.checked.setVisibility(View.GONE);
        }
        if(currentMode == CHANGED_MODE){
            if(selectedItems.contains(position)){
                holder.checked.setChecked(true);
            }
            else{
                holder.checked.setChecked(false);
            }
        }
    }

    private void onItemClick(AlbumViewHolder holder){
        if(navController == null){
            return;
        }
        if(currentMode == UNCHANGED_MODE) {
            String albumName = holder.albumName.getText().toString();
            NavDirections action = AlbumFragmentDirections
                    .actionAlbumFragmentToDetailAlbumFragment(albumName);
            Navigation.findNavController(holder.itemView).navigate(action);
        }
        else{
            holder.checked.setChecked(!holder.checked.isChecked());
            //Blur item here
            if(holder.checked.isChecked()){
                selectedItems.add(holder.getAdapterPosition());
                //Blur item here
            }
            else{
                selectedItems.remove(holder.getAdapterPosition());
                holder.isCheckedFlag = true;
                //Back
            }
        }
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
        CheckBox checked;

        boolean isCheckedFlag = false;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            shapeableImageView = itemView.findViewById(R.id.album_image);
            albumName = itemView.findViewById(R.id.album_name);
            item = itemView.findViewById(R.id.album_item);
            checked = itemView.findViewById(R.id.selected_item);

            checked.setOnClickListener(v -> {
                //Blur item here
                if(checked.isChecked()){
                    selectedItems.add(getAdapterPosition());
                    //Blur item here
                }
                else{
                    selectedItems.remove(getAdapterPosition());
                    isCheckedFlag = true;
                    //Back
                }
            });

            if (layoutType == GRID_LAYOUT) {
            }
            if (layoutType == LINEAR_LAYOUT) {
                numImages = itemView.findViewById(R.id.number_of_images);
            }

            itemView.setOnClickListener(v -> onItemClick(this));
            if (layoutType == LINEAR_LAYOUT) {
                numImages.setText(Integer.toString(100));
            }
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                public final SplitToolbar navigationBar =
                        fragment.getActivity().findViewById(R.id.navigation_bar);

                @Override
                public boolean onLongClick(View v) {
                    if(currentMode == CHANGED_MODE)
                        return false;
                    currentMode = CHANGED_MODE;
                    for(int i =0 ;i< navigationBar.getMenu().size(); ++i){
                        navigationBar.getMenu().getItem(i).setEnabled(false);
                    }
                    for (int i = 0; i < getItemCount(); i++) {
                        notifyItemChanged(i);
                    }
                    showDialog();
                    return true;
                }

                private void showDialog() {
                    final Dialog dialog = new Dialog(context, R.style.Theme_Album);
                    dialog.setContentView(R.layout.selected_option);
                    WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                    params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    params.width = WindowManager.LayoutParams.MATCH_PARENT;
                    params.format = PixelFormat.TRANSLUCENT;
                    params.gravity = Gravity.TOP;
                    params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                    dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    dialog.getWindow().setAttributes(params);

                    final ImageButton backButton
                            = dialog.findViewById(R.id.go_back_option);
                    final ImageButton deleteButton
                            = dialog.findViewById(R.id.delete_option);
                    final CheckBox selectAll
                            = dialog.findViewById(R.id.select_all_option);

                    backButton.setOnClickListener(v -> {
                        dialog.dismiss();
                        currentMode = UNCHANGED_MODE;
                        checked.setVisibility(View.GONE);
                        for (int i = 0; i < getItemCount(); i++) {
                            notifyItemChanged(i);
                        }
                        for(int i =0 ;i< navigationBar.getMenu().size(); ++i){
                            navigationBar.getMenu().getItem(i).setEnabled(true);
                        }
                    });

                    deleteButton.setOnClickListener(v -> {
                        selectedItems.sort(Collections.reverseOrder());
                        while (!selectedItems.isEmpty()) {
                            int selectedIndex = selectedItems.get(0);
                            selectedItems.remove(0);
                            albumImages.remove(selectedIndex);
                            albumNames.remove(selectedIndex);
                            notifyItemRemoved(selectedIndex);
                        }
                    });

                    selectAll.setOnClickListener(v -> {
                        if (selectAll.isChecked()) {
                            allSelected();
                        } else {
                            allUnselected();
                        }
                    });

                    dialog.show();
                }

                private void allSelected() {
                    selectedItems.clear();
                    for (int i = getItemCount() - 1; i >= 0; --i) {
                        selectedItems.add(i);
                        notifyItemChanged(i);
                    }
                }

                private void allUnselected() {
                    selectedItems.clear();
                    for (int i = getItemCount() - 1; i >= 0; --i) {
                        notifyItemChanged(i);
                    }
                }
            });
        }
    }
}
