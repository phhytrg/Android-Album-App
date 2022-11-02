package com.example.album;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private Menu navigationMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpMainActionBar();
        setUpNavigationActionBar();

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
    }

    private void setUpNavigationActionBar(){
        SplitToolbar navigationBar = (SplitToolbar)findViewById(R.id.navigation_bar);
        navigationBar.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.navigation_bar_menu,menu);
                MenuItem defaultItem = menu.getItem(1);
                defaultItem.setTitle(getSpannableStringFromMenuItem(defaultItem,R.color.highlightColorText));
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
                NavController navController =
                        Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);

                menuItem.setTitle(getSpannableStringFromMenuItem(menuItem, R.color.highlightColorText));

                return NavigationUI.onNavDestinationSelected(menuItem,navController)
                        || MainActivity.super.onOptionsItemSelected(menuItem);
            }
        });
    }

    private SpannableString getSpannableStringFromMenuItem(MenuItem item, int colorResource){
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

}