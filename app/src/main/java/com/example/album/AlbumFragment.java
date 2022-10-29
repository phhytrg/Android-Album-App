package com.example.album;

import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

class AlbumFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private GridView gridView;

    String[] albumName = {"Camera","Videos", "Favorites","Screens","Locations","Download", "Collages",
            "Picnic", "Friends", "Selfie", "Memes"};
    int[] albumImages = {R.drawable.image, R.drawable.image3, R.drawable.image3,
            R.drawable.image3, R.drawable.image3, R.drawable.image3, R.drawable.image3,
            R.drawable.image3, R.drawable.image3, R.drawable.image3, R.drawable.image3};



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.album_layout,container, false).getRootView();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GridAdapter gridAdapter = new GridAdapter(requireContext(), albumName, albumImages);

        gridView = (GridView) view.findViewById(R.id.album_list);
        gridView.setAdapter(gridAdapter);

        ImageButton camera_btn = view.findViewById(R.id.btn_camera);
        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(requireContext(), "You're opening camera", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_newAlbum:
                Toast.makeText(requireContext(), "create new album",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_setting:
                Toast.makeText(requireContext(), "setting",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }

//    public void showAlbumOption(View v){
//          PopupMenu popupMenu = new PopupMenu(requireContext(), v);
//          popupMenu.setOnMenuItemClickListener(requireContext());
//          popupMenu.inflate(R.menu.album_option);
//          popupMenu.show();
//    }
}