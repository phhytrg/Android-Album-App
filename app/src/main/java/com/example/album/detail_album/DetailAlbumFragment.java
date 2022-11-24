package com.example.album.detail_album;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.album.ImageUri;
import com.example.album.R;
import com.example.album.gallery.PhotosFragmentDirections;
import com.example.album.item_decoration.GridSpacingItemDecoration;
import com.example.album.item_decoration.LinearSpacingItemDecoration;
import com.example.album.ui.SplitToolbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetailAlbumFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    DetailAlbumAdapter adapter;
    String albumName;
    NavController navController;

    boolean isLinearLayout = false;

    List<Integer> images = new ArrayList<>(Arrays.asList(R.drawable.photo1, R.drawable.photo2,
            R.drawable.photo10, R.drawable.photo4, R.drawable.cat1, R.drawable.photo6,
            R.drawable.photo3, R.drawable.photo5, R.drawable.photo8, R.drawable.photo7,
            R.drawable.photo9, R.drawable.photo1, R.drawable.photo2,
            R.drawable.photo10, R.drawable.photo4, R.drawable.cat1, R.drawable.photo6,
            R.drawable.photo3, R.drawable.photo5, R.drawable.photo8, R.drawable.photo7,
            R.drawable.photo9));

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

        if(getArguments() != null){
            albumName = getArguments().getString("label");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.gallery_recyclerview);
        DetailAlbumAdapter.OnClickListener listener = new DetailAlbumAdapter.OnClickListener() {
            @Override
            public void OnItemClick(DetailAlbumAdapter.GalleryViewHolder holder) {

                //There are 2 different path for navigation.
                //The first comes from DetailAlbumFragment -> DetailFragment
                //The second comes from PhotosFragment -> Detail Fragment
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                        images.get(holder.getAdapterPosition()));
                Uri imageUri = ImageUri.getImageUri(requireContext(), largeIcon);
//                ByteArrayOutputStream bs = new ByteArrayOutputStream();
//                largeIcon.compress(Bitmap.CompressFormat.JPEG,100, bs);

                NavDirections action = DetailAlbumFragmentDirections
                        .actionDetailAlbumFragmentToDetailFragment(imageUri);
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
                    new LinearSpacingItemDecoration(8,false)
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
