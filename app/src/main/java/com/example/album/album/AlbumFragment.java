package com.example.album.album;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.album.MainActivity;
import com.example.album.R;
import com.example.album.data.Image;
import com.example.album.data.ImagesViewModel;
import com.example.album.item_decoration.GridSpacingItemDecoration;
import com.example.album.item_decoration.LinearSpacingItemDecoration;
import com.example.album.ui.SplitToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;


public class AlbumFragment extends Fragment{


    public final static int CHANGED_MODE = 1;
    public final static int UNCHANGED_MODE = 0;

    //Navigate Component
    SplitToolbar navigationBar;
    NavController navController;
    //Appbar
    ActionBar appbar;

    //RecyclerView
    AlbumAdapter adapter;
    RecyclerView recyclerView;
    boolean isLinearLayout = false;

    //Delete bar
    ConstraintLayout deleteBar;
    private CheckBox selectAllCheckBox;
    List<Integer>selectedItems;
    private TextView countItemTextView;
    private int currentState = UNCHANGED_MODE;

    ImagesViewModel imagesViewModel;
    TreeMap<String,List<Image>> map;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagesViewModel = new ViewModelProvider(requireActivity()).get(ImagesViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            ActionBar actionBar = ((AppCompatActivity)requireActivity()).getSupportActionBar();
            if(actionBar!=null) {
                actionBar.setTitle(getString(R.string.albums));
        }

        NavHostFragment navHostFragment = (NavHostFragment) requireActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment != null
                ? navHostFragment.getNavController()
                : null;
        appbar = ((MainActivity)requireActivity()).getSupportActionBar();
        navigationBar = requireActivity().findViewById(R.id.navigation_bar);
        return inflater.inflate(R.layout.album_layout,container, false).getRootView();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        map = imagesViewModel.getAlbums();

        //Set Adapter for RecyclerView
        recyclerView = view.findViewById(R.id.album_list);

//        adapter = new AlbumAdapter(AlbumAdapter.LINEAR_LAYOUT);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        chooseLayout();
        navigationBar.setVisibility(View.VISIBLE);

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.album_option, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == R.id.action_switch_layout){
                    isLinearLayout = !isLinearLayout;
                    chooseLayout();
                    setIcon(menuItem);
                    return true;
                }
                if(id == R.id.menu_newAlbum){
                    showCreateAlbumDialog();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.CREATED);

        deleteBar = view.findViewById(R.id.delete_bar);
        selectAllCheckBox = view.findViewById(R.id.select_all_option);
        countItemTextView = view.findViewById(R.id.count_item);
        ImageButton backButton = view.findViewById(R.id.go_back_option);
        ImageButton deleteButton = view.findViewById(R.id.delete_option);

        selectAllCheckBox.setOnClickListener(new View.OnClickListener() {
            void allSelected() {
                selectedItems.clear();
                for (int i = adapter.getItemCount() - 1; i >= 0; --i) {
                    selectedItems.add(i);
                }
                String syntax = (selectedItems.size() == 1)
                        ? "item"
                        : "items";
                String notification = (selectedItems.isEmpty())
                        ? ""
                        : getString(R.string.number_selected_items, selectedItems.size(), syntax);
                countItemTextView.setText(notification);
                adapter.selectAll();
                adapter.setAllSelectedFlags(true);
            }

            void allUnselected() {
                selectedItems.clear();
                adapter.setAllSelectedFlags(false);
                adapter.unSelectAll();
                countItemTextView.setText("");
            }

            @Override
            public void onClick(View v) {
                if (selectAllCheckBox.isChecked()) {
                    allSelected();
                } else {
                    allUnselected();
                }
            }
        });

        backButton.setOnClickListener(v -> {
            deleteBar.setVisibility(View.GONE);
            currentState = UNCHANGED_MODE;
            adapter.setCurrentState(UNCHANGED_MODE);
            adapter.setCheckBoxesInvisible();
            for(int i =0 ;i< navigationBar.getMenu().size(); ++i){
                navigationBar.getMenu().getItem(i).setEnabled(true);
            }
            appbar.show();
            adapter.unSelectAll();
            countItemTextView.setText("");
            navigationBar.setVisibility(View.VISIBLE);
            selectAllCheckBox.setChecked(false);
        });

        deleteButton.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder
                    = new MaterialAlertDialogBuilder(getActivity()
                    , R.style.AlertDialog_AppCompat_Submit);

            View dialogView = getLayoutInflater().inflate(R.layout.submit_dialog,null);
            TextView notification = (TextView)dialogView.findViewById(R.id.notification);
            String albumSyntax;
            if(selectedItems.size() > 1){
                albumSyntax = "albums";
            }else{
                albumSyntax = "album";
            }
            notification.setText(getString(
                    R.string.delete_notification,
                    selectedItems.size(),
                    albumSyntax)
            );
            builder.setView(dialogView);
            builder.setPositiveButton(R.string.label_ok, (dialog1, id) -> {
                selectedItems.sort(Collections.reverseOrder());
                while (!selectedItems.isEmpty()) {
                    int selectedIndex = selectedItems.get(0);
                    selectedItems.remove(0);
                    adapter.notifyItemRemoved(selectedIndex);
                }
                if(selectAllCheckBox.isChecked()){
                    selectAllCheckBox.setChecked(false);
                }
                countItemTextView.setText("");
            });
            builder.setNegativeButton(R.string.label_cancel, (dialog12, id) -> {
            });
            builder.show();
        });
    }

    private void setIcon(MenuItem menuItem){
        if(menuItem == null)
            return;
        if(isLinearLayout){
            menuItem.setIcon(ContextCompat
                    .getDrawable(this.requireContext(),R.drawable.ic_grid_layout));
        }
        else{
            menuItem.setIcon(ContextCompat
                    .getDrawable(this.requireContext(),R.drawable.ic_linear_layout));
        }
    }

    private void chooseLayout(){
        AlbumAdapter.OnClickListener listener = new AlbumAdapter.OnClickListener() {
            @Override
            public void onItemClick(AlbumAdapter.AlbumViewHolder holder, List<Image> images) {
                if(currentState == UNCHANGED_MODE){
                    String albumName = holder.albumName.getText().toString();
                    Image[] arrayImages = new Image[images.size()];
                    images.toArray(arrayImages);
                    NavDirections action = AlbumFragmentDirections
                            .actionAlbumFragmentToDetailAlbumFragment(albumName);
                    navController.navigate(action);
                }
                else if(currentState == CHANGED_MODE){
                    holder.checkbox.setChecked(!holder.checkbox.isChecked());
                    //Blur item here
                    if(selectAllCheckBox.isChecked()){
                        selectAllCheckBox.setChecked(false);
                    }
                    if(holder.checkbox.isChecked()){
                        selectedItems.add(holder.getAdapterPosition());
                        //Blur item here
                    }
                    else{
                        selectedItems.remove((Object)holder.getAdapterPosition());
                        holder.isCheckedFlag = true;
                        //Back
                    }
                    String syntax = (selectedItems.size() == 1)
                            ? "item"
                            : "items";
                    String notification = (selectedItems.isEmpty())
                            ? ""
                            : getString(R.string.number_selected_items, selectedItems.size(), syntax);
                    countItemTextView.setText(notification);
                    selectAllCheckBox.setChecked(selectedItems.size() >= imagesViewModel.getAlbums().size());
                }
            }

            @Override
            public void onCheckBoxClick(AlbumAdapter.AlbumViewHolder holder) {
                if(holder.checkbox.isChecked()){
                    selectedItems.add((int)holder.getAdapterPosition());
                    //Blur item here
                }
                else{
                    Log.d("AAA",Integer.toString(holder.getAdapterPosition()));
                    selectedItems.remove((Object)holder.getAdapterPosition());
                    //Back
                }
                String syntax = (selectedItems.size() == 1)
                        ? "item"
                        : "items";
                String notification = (selectedItems.isEmpty())
                        ? ""
                        : getString(R.string.number_selected_items, selectedItems.size(), syntax);
                countItemTextView.setText(notification);

                selectAllCheckBox.setChecked(selectedItems.size() >= imagesViewModel.getAlbums().size());
            }

            @Override
            public void onItemLongClick(AlbumAdapter.AlbumViewHolder holder){
                selectedItems = new ArrayList<>();
                if(currentState == CHANGED_MODE)
                {
                    return;
                }
                currentState = CHANGED_MODE;
                adapter.setCurrentState(currentState);
                navigationBar.setVisibility(View.GONE);
                appbar.hide();
                showDialog();
            }

            void showDialog(){
                ConstraintLayout bar = getView().findViewById(R.id.delete_bar);
                bar.setVisibility(View.VISIBLE);
            }

        };

        if(isLinearLayout){
            adapter = new AlbumAdapter(map,
                    listener, AlbumAdapter.LINEAR_LAYOUT);
            recyclerView.setAdapter(adapter);
            adapter.setLayoutType(AlbumAdapter.LINEAR_LAYOUT);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            while (recyclerView.getItemDecorationCount() > 0) {
                recyclerView.removeItemDecorationAt(0);
            }
            recyclerView.addItemDecoration(
                    new LinearSpacingItemDecoration(8,false)
            );
        }
        else{
            adapter = new AlbumAdapter(map,
                    listener
                    , AlbumAdapter.GRID_LAYOUT);
            recyclerView.setAdapter(adapter);
            adapter.setLayoutType(AlbumAdapter.GRID_LAYOUT);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
            recyclerView.setLayoutManager(layoutManager);
            while (recyclerView.getItemDecorationCount() > 0) {
                recyclerView.removeItemDecorationAt(0);
            }
            recyclerView.addItemDecoration(
                    new GridSpacingItemDecoration(3,32,false)
            );
        }
    }

    public void showCreateAlbumDialog(){
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.new_album_dialog_layout);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final EditText nameEt = dialog.findViewById(R.id.userInputDialog);
        Button okButton = dialog.findViewById(R.id.okDialogBtn);
        Button cancelButton = dialog.findViewById(R.id.cancelDialogBtn);

        nameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                okButton.setEnabled(!nameEt.getText().toString().isEmpty());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        cancelButton.setOnClickListener(view -> dialog.dismiss());
        okButton.setOnClickListener(v -> {
            String new_album_name = nameEt.getText().toString();

            NavDirections action = AlbumFragmentDirections.actionAlbumFragmentToPickImagesFragment(new_album_name);
            navController.navigate(action);

            adapter.notifyDataSetChanged();
            dialog.dismiss();
        });
        dialog.show();
    }
}
