package com.example.album;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.album.album.AlbumFragment;
import com.example.album.customize.CustomizeFragment;

public class MainActivity extends AppCompatActivity {

//    GalleryFragment galleryFragment;
    AlbumFragment albumFragment;
    DetailFragment detailFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        galleryFragment = new GalleryFragment();
        detailFragment = new DetailFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.nav_host_fragment,detailFragment);
        fragmentTransaction.commit();
    }
}