package com.example.album;

import android.os.Bundle;
import android.app.Activity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private GridView gridView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_layout);

        ImageButton camera_btn = findViewById(R.id.btn_camera);
        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "You're opening camera", Toast.LENGTH_SHORT).show();
            }
        });

        String[] albumName = {"Camera","Videos", "Favorites","Screens","Locations","Download", "Collages",
                "Picnic", "Friends", "Selfie", "Memes"};
        int[] albumImages = {R.drawable.photo1, R.drawable.photo2, R.drawable.photo10, R.drawable.photo4, R.drawable.cat1,
                R.drawable.photo6, R.drawable.photo3, R.drawable.photo5, R.drawable.photo8, R.drawable.photo7, R.drawable.photo9};

        GridAdapter gridAdapter = new GridAdapter(MainActivity.this, albumName, albumImages);

        gridView = (GridView) findViewById(R.id.album_list);
        gridView.setAdapter(gridAdapter);
      }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_newAlbum:
                Toast.makeText(this, "create new album",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_setting:
                Toast.makeText(this, "setting",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }

    public void showAlbumOption(View v){
          PopupMenu popupMenu = new PopupMenu(this, v);
          popupMenu.setOnMenuItemClickListener(this);
          popupMenu.inflate(R.menu.album_option);
          popupMenu.show();
      }
}