package com.example.album;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.album.gallery.GalleryFragment;

public class MainActivity extends AppCompatActivity {

//    GalleryFragment galleryFragment;
    AlbumFragment albumFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        galleryFragment = new GalleryFragment();
        albumFragment = new AlbumFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.nav_host_fragment,albumFragment);
        fragmentTransaction.commit();
    }
}