package com.example.album.album;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.album.ImageStorageHandler;
import com.example.album.MainActivity;
import com.example.album.R;
import com.example.album.data.Image;
import com.example.album.data.ImagesViewModel;
import com.example.album.gallery.ListItem;
import com.example.album.ui.SplitToolbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class pickImagesFragment extends Fragment {
    View view;
    RecyclerView recyclerView;
    pickImagesAdapter adapter;
    String albumName;
    NavController navController;
    ImagesViewModel imagesViewModel;
    List<Image> images;

    private CheckBox selectAllCheckBox;
    List<Integer>selectedItems;
    SplitToolbar navigationBar;
    ActionBar appbar;
    List<ListItem> listItems;

//    boolean isLinearLayout = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        navigationBar = requireActivity().findViewById(R.id.navigation_bar);
        appbar = ((MainActivity)requireActivity()).getSupportActionBar();
        view = inflater.inflate(R.layout.pick_images_fragment, container, false).getRootView();
        if(getActivity()!=null){
            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if(actionBar!=null) {
                actionBar.setTitle(albumName);
            }
        }

        SplitToolbar toolbar = getActivity().findViewById(R.id.navigation_bar);
        toolbar.setVisibility(View.GONE);
        appbar.hide();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            albumName = getArguments().getString("label");
        }

        imagesViewModel = new ViewModelProvider(requireActivity()).get(ImagesViewModel.class);
        images = imagesViewModel.getImages().getValue();

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

        selectAllCheckBox = view.findViewById(R.id.select_all_option);
        ImageButton backButton = view.findViewById(R.id.go_back_option);
        selectAllCheckBox = view.findViewById(R.id.select_all_option);
        ImageButton doneButton = view.findViewById(R.id.pick_done);
        selectedItems = new ArrayList<>();

        imagesViewModel.getImages().observe(
                getViewLifecycleOwner(),
                new Observer<List<Image>>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChanged(List<Image> is) {
                        images = imagesViewModel.getImages().getValue();
                        adapter.setImages(images);
                        adapter.notifyDataSetChanged();
                    }
                }
        );

        selectAllCheckBox.setOnClickListener(new View.OnClickListener() {
            void allSelected() {
                selectedItems.clear();
                for (int i = adapter.getItemCount() - 1; i >= 0; --i) {
                    selectedItems.add(i);
                }
                adapter.selectAll();
                adapter.setAllSelectedFlags(true);
            }

            void allUnselected() {
                selectedItems.clear();
                adapter.setAllSelectedFlags(false);
                adapter.unSelectAll();
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

        recyclerView = view.findViewById(R.id.gallery_recyclerview);

        pickImagesAdapter.OnClickListener listener = new pickImagesAdapter.OnClickListener() {
            @Override
            public void onItemClick(pickImagesAdapter.PickImageViewHolder holder) {

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
            }

            @Override
            public void onCheckBoxClick(pickImagesAdapter.PickImageViewHolder holder) {
                if(holder.checkbox.isChecked()){
                    selectedItems.add((int)holder.getAdapterPosition());
                    //Blur item here
                }
                else{
                    selectedItems.remove((Object)holder.getAdapterPosition());
                    //Back
                }
            }

        };
        adapter = new pickImagesAdapter(listener, images);
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        recyclerView.setLayoutManager(layoutManager);

        backButton.setOnClickListener(v -> {
            adapter.setCheckBoxesInvisible();
            for(int i =0 ;i< navigationBar.getMenu().size(); ++i){
                navigationBar.getMenu().getItem(i).setEnabled(true);
            }
            appbar.show();
            adapter.unSelectAll();
            navigationBar.setVisibility(View.VISIBLE);
            NavDirections action = pickImagesFragmentDirections
                    .actionPickImagesFragmentToAlbumFragment();
            navController.navigate(action);
        });
        doneButton.setOnClickListener(v -> {
//            ImageStorageHandler.saveImage(requireContext(), saved, albumName);
            Bitmap bitmap;
            for (int i = 0 ; i < selectedItems.size(); ++i){
                Image image = images.get(selectedItems.get(i));
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(),
                            image.getImageUri());
                    ImageStorageHandler.saveImage(requireContext(), bitmap, albumName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            appbar.show();
//            NavDirections action = pickImagesFragmentDirections
//                    .actionPickImagesFragmentToDetailAlbumFragment(albumName);
            NavDirections action = pickImagesFragmentDirections.actionPickImagesFragmentToAlbumFragment();
            navController.navigate(action);
        });
    }

}
