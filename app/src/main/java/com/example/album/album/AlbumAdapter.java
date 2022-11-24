package com.example.album.album;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.album.MainActivity;
import com.example.album.R;
import com.example.album.ui.SplitToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>{

    public final static int LINEAR_LAYOUT = 0;
    public final static int GRID_LAYOUT = 1;
    private List<Integer> albumImages;
    private List<String> albumNames;
    private int layoutType;
    private Context context;
    public final static int CHANGED_MODE = 1;
    public final static int UNCHANGED_MODE = 0;
    int countItems = 0;
    private MutableLiveData<Integer> totalItemsSelected;
    private TextView countItemTextView;
    private SplitToolbar navigationBar;
    private ActionBar appbar;

    public MutableLiveData<Integer> getTotalItemsSelected(){
        if(totalItemsSelected == null){
            totalItemsSelected = new MutableLiveData<>();
        }
        return totalItemsSelected;
    }

    private int currentMode = UNCHANGED_MODE;
    Fragment fragment;

    public void setLayoutType(int layoutType) {
        this.layoutType = layoutType;
    }

    public void setNavController(NavController navController) {
        this.navController = navController;
    }

    public AlbumAdapter(List<String> albumNames, List<Integer>albumImages,
                        Context context, Fragment fragment, NavController navController, int layoutType) {
        this.layoutType = layoutType;
        this.navController = navController;
        this.context = context;
        this.fragment = fragment;
        selectedItems = new ArrayList<>();
        this.albumImages = albumImages;
        this.albumNames = albumNames;

        navigationBar =
                fragment.requireActivity().findViewById(R.id.navigation_bar);
        appbar = ((MainActivity)fragment.requireActivity()).getSupportActionBar();
    }

    NavController navController;
    //data field
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
            holder.checkbox.setVisibility(View.VISIBLE);
        }
        else{
            holder.checkbox.setVisibility(View.GONE);
        }
        if(currentMode == CHANGED_MODE){
            if(selectedItems.contains(position)){
                holder.checkbox.setChecked(true);
            }
            else{
                holder.checkbox.setChecked(false);
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
            holder.checkbox.setChecked(!holder.checkbox.isChecked());
            //Blur item here
            if(holder.checkbox.isChecked()){
                selectedItems.add(holder.getAdapterPosition());
//                totalItemsSelected.setValue();
                countItems++;
                //Blur item here
            }
            else{
                selectedItems.remove((Object)holder.getAdapterPosition());
                holder.isCheckedFlag = true;
                countItems--;
                //Back
            }
            countItemTextView.setText(Integer.toString(countItems));
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
        CheckBox checkbox;

        boolean isCheckedFlag = false;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            shapeableImageView = itemView.findViewById(R.id.album_image);
            albumName = itemView.findViewById(R.id.album_name);
            item = itemView.findViewById(R.id.album_item);
            checkbox = itemView.findViewById(R.id.selected_item);

            checkbox.setOnClickListener(v -> {
                //Blur item here
                if(checkbox.isChecked()){
                    selectedItems.add((int)getAdapterPosition());
                    countItems++;
                    //Blur item here
                }
                else{
                    Log.d("AAA",Integer.toString(getAdapterPosition()));
                    selectedItems.remove((Object)getAdapterPosition());
                    isCheckedFlag = true;
                    countItems--;
                    //Back
                }
//                checkbox.setChecked(!checkbox.isChecked());
                countItemTextView.setText(Integer.toString(countItems));
            });

            if (layoutType == LINEAR_LAYOUT) {
                numImages = itemView.findViewById(R.id.number_of_images);
                numImages.setText(Integer.toString(100));

            }

            itemView.setOnClickListener(v -> onItemClick(this));
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(currentMode == CHANGED_MODE)
                        return false;
                    currentMode = CHANGED_MODE;
//                    for(int i =0 ;i< navigationBar.getMenu().size(); ++i){
//                        navigationBar.getMenu().getItem(i).setEnabled(false);
//                    }
                    for (int i = 0; i < getItemCount(); i++) {
                        notifyItemChanged(i);
                    }
                    navigationBar.setVisibility(View.GONE);
                    appbar.hide();
                    showDialog();
                    return true;
                }


            });

        }
        private void showDialog() {

            View fragmentView = fragment.getView();
            ConstraintLayout bar  = fragment.getView().findViewById(R.id.front_album_nav);
            bar.setVisibility(View.VISIBLE);

            final ImageButton backButton
                    = fragmentView.findViewById(R.id.go_back_option);
            final ImageButton deleteButton
                    = fragmentView.findViewById(R.id.delete_option);
            final CheckBox selectAll
                    = fragmentView.findViewById(R.id.select_all_option);
            countItemTextView
                    = fragmentView.findViewById(R.id.count_item);

            backButton.setOnClickListener(v -> {
                bar.setVisibility(View.GONE);
                currentMode = UNCHANGED_MODE;
                checkbox.setVisibility(View.GONE);
                for (int i = 0; i < getItemCount(); i++) {
                    notifyItemChanged(i);
                }
                for(int i =0 ;i< navigationBar.getMenu().size(); ++i){
                    navigationBar.getMenu().getItem(i).setEnabled(true);
                }
                appbar.show();
                navigationBar.setVisibility(View.VISIBLE);
            });

            deleteButton.setOnClickListener(v -> {
                MaterialAlertDialogBuilder builder
                        = new MaterialAlertDialogBuilder(fragment.getActivity()
                        , R.style.AlertDialog_AppCompat_Submit);

                View view = fragment.getLayoutInflater().inflate(R.layout.submit_dialog,null);
                TextView notification = (TextView)view.findViewById(R.id.notification);
                String albumSyntax;
                if(selectedItems.size() >= 1){
                    albumSyntax = "albums";
                }else{
                    albumSyntax = "album";
                }
                notification.setText(context.getString(
                        R.string.delete_notification,
                        selectedItems.size(),
                        albumSyntax)
                );
                builder.setView(view);
                builder.setPositiveButton(R.string.label_ok, (dialog1, id) -> {
                    selectedItems.sort(Collections.reverseOrder());
                    while (!selectedItems.isEmpty()) {
                        int selectedIndex = selectedItems.get(0);
                        selectedItems.remove(0);
                        albumImages.remove(selectedIndex);
                        albumNames.remove(selectedIndex);
                        notifyItemRemoved(selectedIndex);
                    }
                });
                builder.setNegativeButton(R.string.label_cancel, (dialog12, id) -> {

                });
                AlertDialog submitDialog = builder.create();
                submitDialog.show();


            });

            selectAll.setOnClickListener(v -> {
                if (selectAll.isChecked()) {
                    allSelected();
                } else {
                    allUnselected();
                }
            });
        }

        private void allSelected() {
            selectedItems.clear();
            for (int i = getItemCount() - 1; i >= 0; --i) {
                selectedItems.add(i);
                notifyItemChanged(i);
            }
            countItems = getItemCount();
            countItemTextView.setText(Integer.toString(countItems));

        }

        private void allUnselected() {
            selectedItems.clear();
            for (int i = getItemCount() - 1; i >= 0; --i) {
                notifyItemChanged(i);
            }
            countItems = 0;
            countItemTextView.setText(Integer.toString(countItems));
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
