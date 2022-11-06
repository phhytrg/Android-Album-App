package com.example.album;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private Menu navigationMenu;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpNavController();
        setUpMainActionBar();
        setUpNavigationActionBar();
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
        int a = navHostFragment.getChildFragmentManager().getBackStackEntryCount();
        Log.d("AAA", Integer.toString(a));

//        detailAlbumFragment = new GalleryFragment();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.nav_host_fragment,detailAlbumFragment);
//        fragmentTransaction.commit();

//        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.nav_host_fragment);
//        navController = navHostFragment != null ? navHostFragment.getNavController() : null;
//        NavigationUI.setupActionBarWithNavController(this, navController);

    }

    private void setUpNavController(){
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment != null
                ? navHostFragment.getNavController()
                : null;
    }

    private void setUpMainActionBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.album_option,menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigateUp();
            }
        });
    }

    private void setUpNavigationActionBar(){
        SplitToolbar navigationBar = (SplitToolbar)findViewById(R.id.navigation_bar);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.galleryFragment,R.id.albumFragment).build();
        if(navController!= null) {
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        }
        navigationBar.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.navigation_bar_menu,menu);
                navigationMenu = menu;
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                for(int i =0; i < navigationMenu.size(); i++){
                    MenuItem currentItem = navigationMenu.getItem(i);
                    if (currentItem.getItemId() == R.id.galleryFragment) {
                        currentItem.setTitle(getSpannableStringFromMenuItem(currentItem,R.color.textColorPrimary));
                    } else if (currentItem.getItemId() == R.id.albumFragment) {
                        currentItem.setTitle(getSpannableStringFromMenuItem(currentItem,R.color.textColorPrimary));
                    } else if (currentItem.getItemId() == R.id.privacyFragment) {
                        currentItem.setTitle(getSpannableStringFromMenuItem(currentItem,R.color.textColorPrimary));
                    }
                }
                menuItem.setTitle(getSpannableStringFromMenuItem(menuItem, R.color.highlightColorText));

                int destinationId = menuItem.getItemId();
                int currentId = (navController != null) ?
                        navController.getCurrentDestination().getId()
                        : 0;
                if(navController == null) {
                    return false;
                }
                if(currentId == R.id.albumFragment){
                    if(destinationId == R.id.galleryFragment) {
                        navController.navigate(R.id.action_albumFragment_to_galleryFragment,
                                null, setUpSpecificNavOpts(0));


//                        navController.clearBackStack(R.id.albumFragment);
//                        Log.d("AAA", String
//                                .valueOf(navController.getBackStackEntry(R.id.albumFragment)));
                    }
                }
                else if(currentId == R.id.galleryFragment){
                    if(destinationId == R.id.albumFragment){
                        navController.navigate(R.id.action_galleryFragment_to_albumFragment, null, setUpSpecificNavOpts(1));
                    }
                }
                return true;
            }
        });

        MenuItem defaultItem = navigationMenu.getItem(1);
        defaultItem.setTitle(getSpannableStringFromMenuItem(defaultItem,R.color.highlightColorText));
    }

    @NonNull
    private SpannableString getSpannableStringFromMenuItem(@NonNull MenuItem item, int colorResource){
        SpannableString spanString =
                new SpannableString(item.getTitle().toString());
        spanString.setSpan(
                new ForegroundColorSpan(ContextCompat
                        .getColor(this,colorResource)),
                0,
                spanString.length(),
                0
        ); //fix the color to white
        return spanString;
    }

    @NonNull
    private NavOptions setUpSpecificNavOpts(int direction){
        if(direction == 0){
            return new NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setEnterAnim(R.anim.slide_in_left)
                    .setExitAnim(R.anim.slide_out_right)
                    .setPopEnterAnim(R.anim.slide_in_right_slow)
                    .setPopExitAnim(R.anim.slide_out_left_slow)
                    .setPopUpTo(R.id.nav_graph,false,true)
                    .setLaunchSingleTop(true)
                    .build();
        }
        else {
            return new NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setEnterAnim(R.anim.slide_in_right)
                    .setExitAnim(R.anim.slide_out_left)
                    .setPopEnterAnim(R.anim.slide_in_left_slow)
                    .setPopExitAnim(R.anim.slide_in_right_slow)
                    .setPopUpTo(R.id.nav_graph,false,true)
                    .setLaunchSingleTop(true)
                    .build();
        }
    }

}