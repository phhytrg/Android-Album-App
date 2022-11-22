package com.example.album.detail_album;

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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.album.R;
import com.example.album.item_decoration.GridSpacingItemDecoration;
import com.example.album.item_decoration.LinearSpacingItemDecoration;
import com.example.album.ui.SplitToolbar;

public class DetailAlbumFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    DetailAlbumAdapter adapter;
    String albumName;

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

//        MenuHost menuHost = getActivity();
//        MenuProvider menuProvider = new MenuProvider() {
//            @Override
//            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
////                menuInflater.inflate(R.menu.switch_layout_menu, menu);
//                MenuItem layoutMenu = menu.findItem(R.id.action_switch_layout);
//                setIcon(layoutMenu);
//            }
//
//            @Override
//            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
//                if(menuItem.getItemId() == R.id.action_switch_layout) {
//                    isLinearLayout = !isLinearLayout;
////                    adapter.setLinearLayout(isLinearLayout);
////                    chooseLayout();
//                    setIcon(menuItem);
//                    if(isLinearLayout){
//                        for (int childCount = adapter.getItemCount(), i = 0; i < childCount; ++i) {
////                            GalleryAdapter.GalleryViewHolder viewHolder =
////                                    (GalleryAdapter.GalleryViewHolder) recyclerView
////                                            .findViewHolderForAdapterPosition(i);
//                            adapter.setLinearLayout(isLinearLayout);
//                            adapter.notifyItemChanged(i);
//                        }
//                    }
//                    else{
//                        for (int childCount = adapter.getItemCount(), i = 0; i < childCount; ++i) {
////                            GalleryAdapter.GalleryViewHolder viewHolder =
////                                    (GalleryAdapter.GalleryViewHolder) recyclerView
////                                            .findViewHolderForAdapterPosition(i);
//
//                            adapter.setLinearLayout(isLinearLayout);
//                            adapter.notifyItemChanged(i);
//                        }
//                    }
//                    chooseLayout();
//                }
//
//                return true;
//            }
//
//        };
//        if(menuHost != null) {
//            menuHost.addMenuProvider(menuProvider, getViewLifecycleOwner(), Lifecycle.State.CREATED);
//        }

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
        NavController navController = navHostFragment != null
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
        adapter = new DetailAlbumAdapter(requireContext());
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
