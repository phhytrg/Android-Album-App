package com.example.album.album;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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

import com.example.album.MainActivity;
import com.example.album.R;
import com.example.album.SplitToolbar;
import com.example.album.item_decoration.GridSpacingItemDecoration;
import com.example.album.item_decoration.LinearSpacingItemDecoration;

public class AlbumFragment extends Fragment{

    SplitToolbar navigationBar;
    NavController navController;
    boolean isLinearLayout = false;
    AlbumAdapter adapter;
    RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(getActivity()!=null){
            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if(actionBar!=null) {
                actionBar.setTitle(getString(R.string.albums));
            }
        }

        NavHostFragment navHostFragment = (NavHostFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment != null
                ? navHostFragment.getNavController()
                : null;

        navigationBar = (SplitToolbar) ((MainActivity)getActivity()).findViewById(R.id.navigation_bar);
        return inflater.inflate(R.layout.album_layout,container, false).getRootView();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Set Adapter for RecyclerView
        recyclerView = view.findViewById(R.id.album_list);

//        adapter = new AlbumAdapter(AlbumAdapter.LINEAR_LAYOUT);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        chooseLayout();
        adapter.setNavController(navController);
        navigationBar.setVisibility(View.VISIBLE);

        if(getActivity() == null)
            return;
        getActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {}

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.action_switch_layout){
                    isLinearLayout = !isLinearLayout;
                    chooseLayout();
                    setIcon(menuItem);
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.CREATED);
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
        if(isLinearLayout){
            adapter = new AlbumAdapter(requireContext(),
                    this, navController, AlbumAdapter.LINEAR_LAYOUT);
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

            adapter = new AlbumAdapter(requireContext(),
                    this, navController, AlbumAdapter.GRID_LAYOUT);
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