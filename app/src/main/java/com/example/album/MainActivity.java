package com.example.album;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private GridView gridView;
    private ImageButton camera_btn;
    final Context c = this;
    GridAdapter gridAdapter;

    ArrayList<String> albumName  = new ArrayList<String>();
    ArrayList<Integer> albumImages = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_layout);

        camera_btn = findViewById(R.id.btn_camera);
        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "You're opening camera", Toast.LENGTH_SHORT).show();
            }
        });

        albumName.add("Camera");
        albumName.add("Videos");
        albumName.add("Favorites");
        albumName.add("Screens");
        albumName.add("Location");
        albumName.add("Download");
        albumName.add("Collages");
        albumName.add("Picnic");
        albumName.add("Friends");
        albumName.add("Selfie");
        albumName.add("Memes");

        albumImages.add(R.drawable.photo1);
        albumImages.add(R.drawable.photo2);
        albumImages.add(R.drawable.photo10);
        albumImages.add(R.drawable.photo4);
        albumImages.add(R.drawable.cat1);
        albumImages.add(R.drawable.photo6);
        albumImages.add(R.drawable.photo3);
        albumImages.add(R.drawable.photo5);
        albumImages.add(R.drawable.photo8);
        albumImages.add(R.drawable.photo7);
        albumImages.add(R.drawable.photo9);

//        String[] albumName = {"Camera","Videos", "Favorites","Screens","Locations","Download", "Collages",
//                "Picnic", "Friends", "Selfie", "Memes"};
//        int[] albumImages = {R.drawable.photo1, R.drawable.photo2, R.drawable.photo10, R.drawable.photo4, R.drawable.cat1,
//                R.drawable.photo6, R.drawable.photo3, R.drawable.photo5, R.drawable.photo8, R.drawable.photo7, R.drawable.photo9};

        gridAdapter = new GridAdapter(MainActivity.this, albumName, albumImages);

        gridView = (GridView) findViewById(R.id.album_list);
        gridView.setAdapter(gridAdapter);

      }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_newAlbum:
                showCreateAlbumDialog();
                return true;
            case R.id.menu_setting:
                Toast.makeText(this, "setting",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }

    public void showCreateAlbumDialog(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.new_album_dialog_layout);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final EditText nameEt = (EditText) dialog.findViewById(R.id.userInputDialog);
        Button okButton = dialog.findViewById(R.id.okDialogBtn);
        Button cancelButton = dialog.findViewById(R.id.cancelDialogBtn);

        nameEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                okButton.setEnabled(!nameEt.getText().toString().isEmpty());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEt.getText().toString();
                albumName.add(name);
                albumImages.add(R.drawable.gray);
                gridAdapter.notifyDataSetChanged();
                gridView.setAdapter(gridAdapter);

                dialog.dismiss();
                }
        });

        dialog.show();
    }

    public void showAlbumOption(View v){
          PopupMenu popupMenu = new PopupMenu(this, v);
          popupMenu.setOnMenuItemClickListener(this);
          popupMenu.inflate(R.menu.album_option);
          popupMenu.show();
      }
}