package com.example.album.gallery;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.album.ImageStorageHandler;
import com.example.album.R;
import com.example.album.data.Image;
import com.example.album.data.ImagesViewModel;
import com.example.album.ui.SplitToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class PhotosFragment extends Fragment {
    List<ListItem> listItems;
    NavController navController;
    PhotosAdapter adapter;
    RecyclerView recyclerView;
    ImagesViewModel imagesViewModel;
    private SharedPreferences shared_prefs;
    private SharedPreferences.Editor editor;

    private ActionBar actionBar;
    private SplitToolbar navigationBar;

    private boolean isLinearLayout;

    public final static int CHANGED_MODE = 1;
    public final static int UNCHANGED_MODE = 0;
    private int currentState;
    private CheckBox selectAllCheckBox;
    private TextView countItemTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        NavHostFragment navHostFragment = (NavHostFragment) requireActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        navController = navHostFragment == null ? null : navHostFragment.getNavController();
        imagesViewModel = new ViewModelProvider(requireActivity()).get(ImagesViewModel.class);
        actionBar = ((AppCompatActivity)requireActivity()).getSupportActionBar();
        navigationBar = requireActivity().findViewById(R.id.navigation_bar);
        navigationBar.setVisibility(View.VISIBLE);
        return inflater.inflate(R.layout.gallery_fragment, container, false).getRootView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listItems = new ArrayList<>();
        TreeMap<LocalDateTime, List<Image>> images = toMap(imagesViewModel.getImages().getValue());
        shared_prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = shared_prefs.edit();
        isLinearLayout = shared_prefs.getBoolean("photosLayout", true);

//        navController.getCurrentBackStackEntry().getSavedStateHandle().getLiveData("delete")
//                .observe(getViewLifecycleOwner(), result -> {
//                    LocalDateTime date = ((Image)result).getDate().truncatedTo(ChronoUnit.DAYS);
//                    List keys = new ArrayList(images.keySet());
//                    Collections.reverse(keys);
//                    int index = imagesViewModel.getImages().getValue().indexOf(result) + keys.indexOf(date);
//                    adapter.notifyItemRemoved( index + 1);
//                    imagesViewModel.getImages().forcingUpdate();
//                });

        for (LocalDateTime date : images.descendingKeySet()) {
            HeaderItem header = new HeaderItem(date);
            listItems.add(header);
            for (Image image : images.get(date)) {
                ImageItem item = new ImageItem(image);
                listItems.add(item);
            }
        }

        recyclerView = view.findViewById(R.id.gallery_recyclerview);
        PhotosAdapter.AdapterCallback listener = new PhotosAdapter.AdapterCallback() {
            @Override
            public void onItemClick(PhotosAdapter.ImageViewHolder holder, ImageItem item) {
                if(currentState == UNCHANGED_MODE){
                    NavDirections action = PhotosFragmentDirections
                            .actionPhotosFragmentToDetailFragment(item.getImage());
                    navController.navigate(action);
                }
                else{
                    holder.checkBox.setChecked(!holder.checkBox.isChecked());
                    //Blur item here
                    if(selectAllCheckBox.isChecked()){
                        selectAllCheckBox.setChecked(false);
                    }
                    if(holder.checkBox.isChecked()){
                        if(adapter.getItems().get(holder.getAdapterPosition()).getType() == ListItem.TYPE_EVENT){
                            adapter.getItemsSelected().add(holder.getAdapterPosition());
                        }
                        //Blur item here
                    }
                    else{
                        adapter.getItemsSelected().remove((Object)holder.getAdapterPosition());
                        //Back
                    }
                    String syntax = (adapter.getItemsSelected().size() == 1)
                            ? "item"
                            : "items";
                    String notification = (adapter.getItemsSelected().isEmpty())
                            ? ""
                            : getString(R.string.number_selected_items, adapter.getItemsSelected().size(), syntax);
                    countItemTextView.setText(notification);
                    selectAllCheckBox.setChecked(adapter.getItemsSelected().size() >= imagesViewModel.getImages().getValue().size());
                }
            }

            @Override
            public void linearItemDecoration(@NonNull ImageView imageView) {
                imageView.setBackground(ResourcesCompat.getDrawable(
                        getResources(),
                        R.drawable.image_border,
                        requireActivity().getTheme())
                );
            }

            @Override
            public void OnItemLongClick(PhotosAdapter.ImageViewHolder holder, ImageItem item) {
                if(currentState == CHANGED_MODE){
                    return;
                }
                currentState = CHANGED_MODE;
                adapter.setCurrentState(currentState);
                actionBar.hide();
                navigationBar.setVisibility(View.GONE);
                showDeleteBar();
            }

            @Override
            public void OnCheckBoxClick(PhotosAdapter.ImageViewHolder holder) {
                if(holder.checkBox.isChecked()){
                    if(adapter.getItems().get(holder.getAdapterPosition()).getType() == ListItem.TYPE_EVENT) {
                        adapter.getItemsSelected().add(holder.getAdapterPosition());
                    }
                    //Blur item here
                }
                else{
                    Log.d("AAA",Integer.toString(holder.getAdapterPosition()));
                    adapter.getItemsSelected().remove((Object)holder.getAdapterPosition());
                    //Back
                }
                String syntax = (adapter.getItemsSelected().size() == 1)
                        ? "item"
                        : "items";
                String notification = (adapter.getItemsSelected().isEmpty())
                        ? ""
                        : getString(R.string.number_selected_items, adapter.getItemsSelected().size(), syntax);
                countItemTextView.setText(notification);
                selectAllCheckBox.setChecked(adapter.getItemsSelected().size() >= imagesViewModel.getImages().getValue().size());
                int count = 0;
//                adapter.getMapCheckBoxes()
//                        .get(holder.dateTime.truncatedTo(ChronoUnit.DAYS))
//                        .get(0)
//                        .setChecked(count >= images.get(holder.dateTime.truncatedTo(ChronoUnit.DAYS)).size());
            }

            @Override
            public void OnHeaderCheckBoxClick(PhotosAdapter.HeaderViewHolder holder) {
                int count = images.get(holder.dateTime.truncatedTo(ChronoUnit.DAYS)).size();
                if(holder.checkBox.isChecked()){
//                    for(CheckBox checkBox: adapter.getMapCheckBoxes().get(holder.dateTime.truncatedTo(ChronoUnit.DAYS))){
//                        checkBox.setChecked(true);
//                    }
                    for(int i = 0; i < count; i++){
                        if(!adapter.getItemsSelected().contains(holder.getAdapterPosition()+i+1)){
                            adapter.getItemsSelected().add(holder.getAdapterPosition() + i + 1);
                            adapter.notifyItemChanged(holder.getAdapterPosition() + i + 1);
                        }
                    }
                }else{
//                    for(CheckBox checkBox: adapter.getMapCheckBoxes().get(holder.dateTime.truncatedTo(ChronoUnit.DAYS))){
//                        checkBox.setChecked(false);
//                    }
                    for(int i = 0; i < count; i++){
                        if(adapter.getItemsSelected().contains(holder.getAdapterPosition()+i+1)){
                            adapter.getItemsSelected().remove((Object)(holder.getAdapterPosition() + i + 1));
                            adapter.notifyItemChanged(holder.getAdapterPosition() + i + 1);
                        }
                    }
                }
                String syntax = (adapter.getItemsSelected().size() == 1)
                        ? "item"
                        : "items";
                String notification = (adapter.getItemsSelected().isEmpty())
                        ? ""
                        : getString(R.string.number_selected_items, adapter.getItemsSelected().size(), syntax);
                countItemTextView.setText(notification);
                selectAllCheckBox.setChecked(adapter.getItemsSelected().size() >= imagesViewModel.getImages().getValue().size());
            }

            private void showDeleteBar(){
                if(getView() == null)
                {
                    currentState = UNCHANGED_MODE;
                    adapter.setCurrentState(currentState);
                    actionBar.show();
                    return;
                }
                ConstraintLayout bar = getView().findViewById(R.id.delete_bar);
                bar.setVisibility(View.VISIBLE);
            }
        };

        adapter = new PhotosAdapter(listItems, listener);
        recyclerView.setAdapter(adapter);
        chooseLayout();

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                MenuItem layoutMenu = menu.findItem(R.id.action_switch_layout);
                setIcon(layoutMenu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == R.id.action_switch_layout){
                    isLinearLayout = !isLinearLayout;
                    editor.putBoolean("photosLayout", isLinearLayout).apply();
                    chooseLayout();
                    setIcon(menuItem);
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.CREATED);

        ImageButton backButton = view.findViewById(R.id.go_back_option);
        ImageButton deleteButton = view.findViewById(R.id.delete_option);
        ConstraintLayout deleteBar = view.findViewById(R.id.delete_bar);
        selectAllCheckBox = view.findViewById(R.id.select_all_option);
        countItemTextView = view.findViewById(R.id.count_item);

        selectAllCheckBox.setOnClickListener(new View.OnClickListener() {
            void allSelected() {
                adapter.getItemsSelected().clear();
                for (int i = adapter.getItemCount() - 1; i >= 0; --i) {
                    if(adapter.getItems().get(i).getType() == ListItem.TYPE_EVENT){
                        adapter.getItemsSelected().add(i);
                    }
                }

                String syntax = (adapter.getItemsSelected().size() == 1)
                        ? "item"
                        : "items";
                String notification = (adapter.getItemsSelected().isEmpty())
                        ? ""
                        : getString(R.string.number_selected_items, adapter.getItemsSelected().size(), syntax);
                countItemTextView.setText(notification);
                adapter.selectAll();
                adapter.setAllSelectedFlags(true);

            }

            void allUnselected() {
                adapter.getItemsSelected().clear();
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
            adapter.getItemsSelected().clear();

            for(int i =0 ;i < navigationBar.getMenu().size(); ++i){
                navigationBar.getMenu().getItem(i).setEnabled(true);
            }

            adapter.unSelectAll();
            actionBar.show();
            navigationBar.setVisibility(View.VISIBLE);
            countItemTextView.setText("");
            selectAllCheckBox.setChecked(false);
        });

        deleteButton.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder
                    = new MaterialAlertDialogBuilder(getActivity()
                    , R.style.AlertDialog_AppCompat_Submit);

            View dialogView = getLayoutInflater().inflate(R.layout.submit_dialog,null);
            TextView notification = dialogView.findViewById(R.id.notification);
            String albumSyntax;
            if(adapter.getItemsSelected().size() > 1){
                albumSyntax = "images";
            }else{
                albumSyntax = "image";
            }
            notification.setText(getString(
                    R.string.delete_notification,
                    adapter.getItemsSelected().size(),
                    albumSyntax)
            );
            builder.setView(dialogView);
            builder.setPositiveButton(R.string.label_ok, (dialog1, id) -> {
                adapter.getItemsSelected().forEach(i -> {
                    int realIndex = 0;
                    int tmp = 0;
                    boolean found = false;
                    for(LocalDateTime dateTime: images.keySet()){
                        tmp++;
                        for(Image image: images.get(dateTime)){
                            if(tmp == i){
                                found = true;
                                break;
                            }
                            realIndex++;
                            tmp++;
                        }
                        if(found){
                            break;
                        }
                    }
                    Uri uri = ImageStorageHandler.getContentUri(requireContext(),imagesViewModel
                            .getImages()
                            .getValue()
                            .get(realIndex)
                            .getImageUri());
                    ImageStorageHandler.deleteImage(requireContext(), requireActivity(), uri);
                    adapter.notifyItemRemoved(i);
                });
                if(selectAllCheckBox.isChecked()){
                    selectAllCheckBox.setChecked(false);
                }
                adapter.unSelectAll();
                countItemTextView.setText("");
            });
            builder.setNegativeButton(R.string.label_cancel, (dialog12, id) -> {
            });
            builder.show();
        });
    }


    @NonNull
    private TreeMap<LocalDateTime, List<Image>> toMap(@NonNull List<Image> images){
        TreeMap<LocalDateTime, List<Image>> map = new TreeMap<>();
        for(Image image: images){
            List<Image> value = map.get(image.getDate().truncatedTo(ChronoUnit.DAYS));
            if(value == null){
                value = new ArrayList<>();
                map.put(image.getDate().truncatedTo(ChronoUnit.DAYS), value);
            }
            value.add(image);
        }
        return map;
    }

    private void chooseLayout(){
        if(isLinearLayout){
            LinearLayoutManager manager = new LinearLayoutManager(requireContext());
            recyclerView.setLayoutManager(manager);
            adapter.setLinearLayout(isLinearLayout);
        }
        else{
            GridLayoutManager manager = new GridLayoutManager(requireContext(),4);
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    switch (adapter.getItemViewType(position)){
                        case ListItem.TYPE_HEADER:{
                            return 4;
                        }
                        case ListItem.TYPE_EVENT:{
                            return 1;
                        }
                        default: throw new IllegalStateException("Unsupported Type!");
                    }
                };
            });
            recyclerView.setLayoutManager(manager);
            adapter.setLinearLayout(isLinearLayout);
        }
        for (int childCount = adapter.getItemCount(), i = 0; i < childCount; ++i) {
            if(listItems.get(i).getType() == ListItem.TYPE_EVENT)
            {
                adapter.notifyItemChanged(i);
            }
        }
    }

    private void setIcon(MenuItem menuItem){
        if(menuItem == null)
            return;
        if(isLinearLayout){
            menuItem.setIcon(ContextCompat.getDrawable(this.requireContext(),R.drawable.ic_grid_layout));
        }
        else {
            menuItem.setIcon(ContextCompat.getDrawable(this.requireContext(),R.drawable.ic_linear_layout));
        }
    }
}
