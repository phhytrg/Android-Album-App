package com.example.album;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;

import com.example.album.gallery.GalleryFragment;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    GalleryFragment detailAlbumFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

}