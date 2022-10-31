package com.example.album;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.album.detail_album.DetailAlbumFragment;

public class MainActivity extends AppCompatActivity {

//    GalleryFragment galleryFragment;
//    GalleryAdapter albumFragment;
//    DetailFragment detailFragment;
//    GalleryFragment galleryFragment = new GalleryFragment();
    DetailAlbumFragment detailAlbumFragment = new DetailAlbumFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.nav_host_fragment,detailAlbumFragment);
        fragmentTransaction.commit();
    }
}