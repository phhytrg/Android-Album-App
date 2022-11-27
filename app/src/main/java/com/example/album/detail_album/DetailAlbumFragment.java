package com.example.album.detail_album;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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

import com.example.album.R;
import com.example.album.data.Image;
import com.example.album.data.ImagesViewModel;
import com.example.album.item_decoration.GridSpacingItemDecoration;
import com.example.album.item_decoration.LinearSpacingItemDecoration;
import com.example.album.ui.SplitToolbar;

import java.util.List;

public class DetailAlbumFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    DetailAlbumAdapter adapter;
    String albumName;
    NavController navController;
    ImagesViewModel imagesViewModel;
    List<Image> images;

    boolean isLinearLayout = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.gallery_fragment, container, false).getRootView();
        if(getActivity()!=null){
            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if(actionBar!=null) {
                actionBar.setTitle(albumName);
            }
        }

        SplitToolbar toolbar = getActivity().findViewById(R.id.navigation_bar);
        toolbar.setVisibility(View.GONE);
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

        NavHostFragment navHostFragment = (NavHostFragment) getActivity()
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
                NavDirections action = DetailAlbumFragmentDirections
                        .actionDetailAlbumFragmentToDetailFragment(images.get(position));
                navController.navigate(action);
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
