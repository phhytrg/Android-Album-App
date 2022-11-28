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
import java.util.Arrays;
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

    List<String> albumNames = new ArrayList<>(Arrays.asList("Camera", "Videos", "Favorites", "Screens", "Locations", "Download", "Collages",
            "Picnic", "Friends", "Selfie", "Memes","Camera", "Videos", "Favorites", "Screens", "Locations", "Download", "Collages",
            "Picnic", "Friends", "Selfie", "Memes"));
    List<Integer> albumImages = new ArrayList<>(Arrays.asList(R.drawable.photo1, R.drawable.photo2,
            R.drawable.photo10, R.drawable.photo4, R.drawable.cat1, R.drawable.photo6,
            R.drawable.photo3, R.drawable.photo5, R.drawable.photo8, R.drawable.photo7,
            R.drawable.photo9, R.drawable.photo1, R.drawable.photo2,
            R.drawable.photo10, R.drawable.photo4, R.drawable.cat1, R.drawable.photo6,
            R.drawable.photo3, R.drawable.photo5, R.drawable.photo8, R.drawable.photo7,
            R.drawable.photo9));

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
        });

        deleteButton.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder
                    = new MaterialAlertDialogBuilder(getActivity()
                    , R.style.AlertDialog_AppCompat_Submit);

            View dialogView = getLayoutInflater().inflate(R.layout.submit_dialog,null);
            TextView notification = (TextView)dialogView.findViewById(R.id.notification);
            String albumSyntax;
            if(selectedItems.size() >= 1){
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
                    albumImages.remove(selectedIndex);
                    albumNames.remove(selectedIndex);
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

        final EditText nameEt = (EditText) dialog.findViewById(R.id.userInputDialog);
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
        okButton.setOnClickListener(new View.OnClickListener() {

//            private void addNewAlbumIntoExternal(){
//                File newDir = new File(Environment.getExternalStoragePublicDirectory(
//                        Environment.DIRECTORY_PICTURES
//                ), "/" + getString(R.string.app_name));
//                requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, )
//            }

            @Override
            public void onClick(View v) {
                String name = nameEt.getText().toString();
//            albumNames.add(name);
//            albumImages.add(R.drawable.image2);
//            adapter.notifyItemChanged(albumImages.size() - 1);



                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}


//        boolean flag = setUpMainActionbar(view);
//        MenuHost menuHost = requireActivity();
//        menuHost.addMenuProvider(new MenuProvider() {
//            @Override
//            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
//                menuInflater.inflate(R.menu.album_option,menu);
//                if(flag)
//                {
//                    ((MainActivity)getActivity())
//                            .getSupportActionBar()
//                            .setTitle(R.string.albums);
//                }
//            }
//
//            @Override
//            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
//                switch (menuItem.getItemId()) {
//                    case R.id.menu_newAlbum:
//                        Toast.makeText(requireContext(), "create new album",Toast.LENGTH_SHORT).show();
//                        return true;
//                    case R.id.menu_setting:
//                        Toast.makeText(requireContext(), "setting",Toast.LENGTH_SHORT).show();
//                        return true;
//                    default:
//                        return false;
//                }
//            }
//
//
//        });

//        MenuHost menuHost = requireActivity();
//        MenuProvider menuProvider = new MenuProvider() {
//            @Override
//            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
////                menuInflater.inflate(R.menu.album_option,menu);
////                menu.getItem(2).setVisible(false);
//            }
//
//            @Override
//            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
//
//                return false;
//            }
//        };
//        menuHost.addMenuProvider(menuProvider, getViewLifecycleOwner(), Lifecycle.State.CREATED);

//        splitToolbar = getActivity().findViewById(R.id.navigation_bar);

//Set Layout Manager
//        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(),3));
//        //Set Item Decoration
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3,24,false));

//        //Navigation ToolBar's implement
//        navigationBar.addMenuProvider(new MenuProvider() {
//            @Override
//            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
//                menuInflater.inflate(R.menu.navigation_bar_menu,menu);
//                MenuItem defaultItem = menu.getItem(1);
//                defaultItem.setTitle(getSpannableStringFromMenuItem(defaultItem,R.color.highlightColorText));
//                navigationMenu = menu;
//            }
//
//            @Override
//            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
//                int id = menuItem.getItemId();
//                for(int i =0; i < navigationMenu.size(); i++){
//                    MenuItem currentItem = navigationMenu.getItem(i);
//                    if (currentItem.getItemId() == R.id.galleryFragment) {
//                        currentItem.setTitle(getSpannableStringFromMenuItem(currentItem,R.color.textColorPrimary));
//                    } else if (currentItem.getItemId() == R.id.albumFragment) {
//                        currentItem.setTitle(getSpannableStringFromMenuItem(currentItem,R.color.textColorPrimary));
//                    } else if (currentItem.getItemId() == R.id.privacyFragment) {
//                        currentItem.setTitle(getSpannableStringFromMenuItem(currentItem,R.color.textColorPrimary));
//                    }
//                }
//                NavController navController =
//                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
//
//                menuItem.setTitle(getSpannableStringFromMenuItem(menuItem, R.color.highlightColorText));
//
//                return NavigationUI.onNavDestinationSelected(menuItem,navController) ||
//                        AlbumFragment.super.onOptionsItemSelected(menuItem);
//                if (id == R.id.galleryFragment){
//                    return NavigationUI.onNavDestinationSelected(menuItem,navController) ||
//                            AlbumFragment.super.onOptionsItemSelected(menuItem);
//                }
//                else if(id == R.id.albumFragment){
//                    return NavigationUI.onNavDestinationSelected(menuItem, navController) ||
//                            AlbumFragment
//                }
//                else if(id == R.id.privacyFragment){
//
//                }
//                return false;
//            }
//        });

//
//        NavController navController = Navigation.findNavController(view);
//        AppBarConfiguration appBarConfiguration =
//                new AppBarConfiguration.Builder(navController.getGraph()).build();
//
//        AppBarConfiguration appBarConfiguration1 =
//                new AppBarConfiguration.Builder(R.id.galleryFragment,R.id.albumFragment).build();
//
//        NavigationUI.setupWithNavController(navigationBar, navController, appBarConfiguration1);
//        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
//            @Override
//            public void onDestinationChanged(
//                    @NonNull NavController navController
//                    , @NonNull NavDestination navDestination
//                    , @Nullable Bundle bundle) {
//                navigationBar.setTitle("");
//            }
//        });


//    private boolean setUpMainActionbar(View view){
//        AppCompatActivity activity = (AppCompatActivity)getActivity();
//        Toolbar toolbar = getActivity().findViewById(R.id.app_bar);
//        if(activity != null){
//            activity.setSupportActionBar(toolbar);
//            return true;
//        }
//        return false;
//    }
//
//    private SpannableString getSpannableStringFromMenuItem(MenuItem item, int colorResource){
//        SpannableString spanString =
//                new SpannableString(item.getTitle().toString());
//        spanString.setSpan(
//                new ForegroundColorSpan(ContextCompat
//                        .getColor(requireContext(),colorResource)),
//                0,
//                spanString.length(),
//                0
//        ); //fix the color to white
//        return spanString;
//    }

//    public void showAlbumOption(View v){
//          PopupMenu popupMenu = new PopupMenu(requireContext(), v);
//          popupMenu.setOnMenuItemClickListener(requireContext());
//          popupMenu.inflate(R.menu.album_option);
//          popupMenu.show();
//    }

//    private void setUpNavigationBar(){
//        MenuProvider menuProvider = new MenuProvider() {
//            @Override
//            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
//                menuInflater.inflate(R.menu.album_option,menu);
//            }
//
//            @Override
//            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
//                int id = menuItem.getItemId();
//                NavOptions navOptions = new NavOptions.Builder()
//                        .setLaunchSingleTop(true)
//                        .setEnterAnim(R.anim.slide_in_left)
//                        .setExitAnim(R.anim.slide_out_right)
//                        .setPopEnterAnim(R.anim.slide_in_right)
//                        .setPopExitAnim(R.anim.slide_out_left)
//                        .build();
//
//                if(id == R.id.galleryFragment){
//                    if(navController != null) {
//                        navController.navigate(id, null, navOptions);
//                        return true;
//                    }
//                }
//                return true;
//            }
//        };
//        splitToolbar.addMenuProvider(menuProvider,getViewLifecycleOwner(), Lifecycle.State.CREATED);
//    }