package com.example.album.detail_album;

import android.annotation.SuppressLint;
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
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.album.ImageUri;
import com.example.album.R;
import com.example.album.data.Image;
import com.example.album.data.ImagesViewModel;
import com.example.album.item_decoration.GridSpacingItemDecoration;
import com.example.album.item_decoration.LinearSpacingItemDecoration;
import com.example.album.ui.SplitToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class DetailAlbumFragment extends Fragment {
    View view;
    RecyclerView recyclerView;
    DetailAlbumAdapter adapter;
    String albumName;
    NavController navController;
    SplitToolbar navigationBar;
    ImagesViewModel imagesViewModel;
    List<Image> images;
    ActionBar actionBar;
    CheckBox selectAllCheckBox;
    TextView countItemTextView;

    boolean isLinearLayout = false;
    List<Integer>selectedItems;
    public final static int CHANGED_MODE = 1;
    public final static int UNCHANGED_MODE = 0;

    private int currentState = UNCHANGED_MODE;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.gallery_fragment, container, false).getRootView();

        actionBar = ((AppCompatActivity)requireActivity()).getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setTitle(albumName);
        }

        navigationBar = requireActivity().findViewById(R.id.navigation_bar);
        navigationBar.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            albumName = getArguments().getString("label");
        }

        imagesViewModel = new ViewModelProvider(requireActivity()).get(ImagesViewModel.class);
        images = imagesViewModel.getAlbums().get(albumName);

        NavHostFragment navHostFragment = (NavHostFragment) requireActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment != null
                ? navHostFragment.getNavController()
                : null;


        OnBackPressedCallback callback = new OnBackPressedCallback(true ) {
            @Override
            public void handleOnBackPressed() {
                navController.navigateUp();
            }
        };
        requireActivity().getOnBackPressedDispatcher()
                .addCallback(this, callback);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imagesViewModel.getImages().observe(
                getViewLifecycleOwner(),
                new Observer<List<Image>>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChanged(List<Image> is) {
                        images = imagesViewModel.getAlbums().get(albumName);
                        adapter.setImages(images);
                        adapter.notifyDataSetChanged();
                    }
                }
        );
        recyclerView = view.findViewById(R.id.gallery_recyclerview);
        DetailAlbumAdapter.OnClickListener listener = new DetailAlbumAdapter.OnClickListener() {
            @Override
            public void OnItemClick(DetailAlbumAdapter.GalleryViewHolder holder, int position) {
                if(currentState == UNCHANGED_MODE){
                    NavDirections action = DetailAlbumFragmentDirections
                            .actionDetailAlbumFragmentToDetailFragment(images.get(position), albumName);
                    navController.navigate(action);
                }
                else{
                    holder.checkBox.setChecked(!holder.checkBox.isChecked());
                    //Blur item here
                    if(selectAllCheckBox.isChecked()){
                        selectAllCheckBox.setChecked(false);
                    }
                    if(holder.checkBox.isChecked()){
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
            public void OnItemLongClick(DetailAlbumAdapter.GalleryViewHolder holder) {
                selectedItems = new ArrayList<>();
                if(currentState == CHANGED_MODE){
                    return;
                }
                currentState = CHANGED_MODE;
                adapter.setCurrentState(currentState);
                actionBar.hide();
                showDeleteBar();
            }

            @Override
            public void OnCheckBoxClick(DetailAlbumAdapter.GalleryViewHolder holder) {
                if(holder.checkBox.isChecked()){
                    selectedItems.add(holder.getAdapterPosition());
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

            void showDeleteBar(){
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
        adapter = new DetailAlbumAdapter(listener,images);
        recyclerView.setAdapter(adapter);
        chooseLayout();
        if(getActivity() == null){
            return;
        }
        getActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {}

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.action_switch_layout) {
                    isLinearLayout = !isLinearLayout;
                    chooseLayout();
                    setIcon(menuItem);
                    for (int childCount = adapter.getItemCount(), i = 0; i < childCount; ++i) {
                        adapter.notifyItemChanged(i);
                    }
                }
                return true;
            }
        },getViewLifecycleOwner(), Lifecycle.State.CREATED);

        ImageButton backButton = view.findViewById(R.id.go_back_option);
        ImageButton deleteButton = view.findViewById(R.id.delete_option);
        ConstraintLayout deleteBar = view.findViewById(R.id.delete_bar);
        selectAllCheckBox = view.findViewById(R.id.select_all_option);
        countItemTextView = view.findViewById(R.id.count_item);

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
            adapter.unSelectAll();
            actionBar.show();
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
            if(selectedItems.size() > 1){
                albumSyntax = "images";
            }else{
                albumSyntax = "image";
            }
            notification.setText(getString(
                    R.string.delete_notification,
                    selectedItems.size(),
                    albumSyntax)
            );
            builder.setView(dialogView);
            builder.setPositiveButton(R.string.label_ok, (dialog1, id) -> {
                selectedItems.forEach(i -> {
                    Uri uri = ImageUri.getContentUri(requireContext(),images.get(i).getImageUri());
                    adapter.notifyItemRemoved(i);
                    ImageUri.deleteImage(requireContext(), requireActivity(), uri);
                });
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
        adapter.setLinearLayout(isLinearLayout);
        if(isLinearLayout){
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            while (recyclerView.getItemDecorationCount() > 0) {
                recyclerView.removeItemDecorationAt(0);
            }
            recyclerView.addItemDecoration(
                    new LinearSpacingItemDecoration(1,false)
            );
        }
        else{
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
            recyclerView.setLayoutManager(layoutManager);
            while (recyclerView.getItemDecorationCount() > 0) {
                recyclerView.removeItemDecorationAt(0);
            }
            recyclerView.addItemDecoration(
                    new GridSpacingItemDecoration(4,8,false)
            );
        }
    }
}