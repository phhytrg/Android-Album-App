package com.example.album;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.transition.Slide;
import androidx.transition.TransitionManager;
import androidx.transition.Transition;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private GridView gridView;
    private ImageButton camera_btn;
    private ImageButton close_btn;
    private ImageButton choose_all_btn;
    private ImageButton delete_album_btn;
    private ImageButton change_album_name_btn;
    final Context c = this;
    final int[] count = {0};
    GridAdapter gridAdapter;

    ArrayList<String> albumName  = new ArrayList<String>();
    ArrayList<Integer> albumImages = new ArrayList<Integer>();
    ArrayList<String> chose = new ArrayList<String>();

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

        final int[] flag ={0};
        choose_all_btn = findViewById(R.id.btn_chooseAll);
        choose_all_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView countText = (TextView) findViewById(R.id.count_item);
                if (flag[0] == 0){
                    gridAdapter = new GridAdapter(MainActivity.this, albumName, albumImages, true);
                    gridView.setAdapter(gridAdapter);
                    gridAdapter.notifyDataSetChanged();
                    chose = (ArrayList)albumName.clone();
                    flag[0] = 1;
                    count[0]=chose.size();
                }
                else{
                    gridAdapter = new GridAdapter(MainActivity.this, albumName, albumImages, false);
                    gridView.setAdapter(gridAdapter);
                    gridAdapter.notifyDataSetChanged();
                    chose.clear();
                    flag[0] = 0;
                    count[0] = 0;
                }
                countText.setText(count[0]+" Selected item");
            }
        });

        close_btn = findViewById(R.id.btn_close);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RelativeLayout bottom_nav = (RelativeLayout) findViewById(R.id.bottom_album_nav);
                RelativeLayout front_nav = (RelativeLayout) findViewById(R.id.front_album_nav);
                RelativeLayout layout_top = (RelativeLayout) findViewById(R.id.layout_top);
                RelativeLayout nav = (RelativeLayout) findViewById(R.id.navagation);

                front_nav.setVisibility(View.GONE);
                layout_top.setVisibility(View.VISIBLE);
                nav.setVisibility(View.VISIBLE);
                bottom_nav.setVisibility(View.GONE);

                TextView countText = (TextView) findViewById(R.id.count_item);
                countText.setText("1 Selected item");
                count[0] = 0;

                gridAdapter = new GridAdapter(MainActivity.this, albumName, albumImages, false);
                gridView.setAdapter(gridAdapter);
                gridAdapter.notifyDataSetChanged();
            }
        });

        delete_album_btn = findViewById(R.id.btn_delete);
        delete_album_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteAlbumDialog();
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

        gridAdapter = new GridAdapter(MainActivity.this, albumName, albumImages, false);
        gridView = (GridView) findViewById(R.id.album_list);
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long l) {
                RelativeLayout bottom_nav = (RelativeLayout) findViewById(R.id.bottom_album_nav);
                RelativeLayout front_nav = (RelativeLayout) findViewById(R.id.front_album_nav);
                RelativeLayout layout_top = (RelativeLayout) findViewById(R.id.layout_top);
                RelativeLayout nav = (RelativeLayout) findViewById(R.id.navagation);
                TextView countText = (TextView) findViewById(R.id.count_item);

                Transition transition = new Slide(Gravity.BOTTOM);
                transition.setDuration(300);
                transition.addTarget(bottom_nav);
                TransitionManager.beginDelayedTransition(parent, transition);
                bottom_nav.setVisibility(View.VISIBLE);
                front_nav.setVisibility(View.VISIBLE);
                layout_top.setVisibility(View.GONE);
                nav.setVisibility(View.GONE);

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String name_selected = ((TextView) view.findViewById(R.id.album_name)).getText().toString();

                        ImageView tick = (ImageView)view.findViewById(R.id.tick);
                        int visibility = tick.getVisibility();
                        if(visibility == View.VISIBLE)
                        {
                            tick.setVisibility(View.GONE);
                            chose.remove(name_selected);
                            count[0]--;
                            countText.setText(count[0]+" Selected item");

                        }
                        else
                        {
                            tick.setVisibility(View.VISIBLE);
                            chose.add(name_selected);
                            count[0]++;
                            countText.setText(count[0]+" Selected item");
                        }
                    }
                });

                String name_selected = ((TextView) view.findViewById(R.id.album_name)).getText().toString();
                int img_selected = ((ImageView) view.findViewById(R.id.avatar)).getId();
//                Object itemObject =parent.getAdapter().getItem(position);
                ImageView tick = (ImageView)view.findViewById(R.id.tick);
                int visibility = tick.getVisibility();

                if(visibility == View.VISIBLE)
                {
                    tick.setVisibility(View.GONE);
                    chose.remove(name_selected);
                    count[0]--;
                    countText.setText(count[0]+" Selected item");
                }
                else
                {
                    tick.setVisibility(View.VISIBLE);
                    chose.add(name_selected);
                    count[0]++;
                    countText.setText(count[0]+" Selected item");
                }
                return true;
            }
        });
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

    public void showDeleteAlbumDialog(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.delete_album_dialog_layout);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button deleteButton = dialog.findViewById(R.id.deleteDialogBtn);
        Button cancelButton = dialog.findViewById(R.id.cancelDeleteDialogBtn);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAlbum(albumName, albumImages, chose);

                RelativeLayout bottom_nav = (RelativeLayout) findViewById(R.id.bottom_album_nav);
                RelativeLayout front_nav = (RelativeLayout) findViewById(R.id.front_album_nav);
                RelativeLayout layout_top = (RelativeLayout) findViewById(R.id.layout_top);
                RelativeLayout nav = (RelativeLayout) findViewById(R.id.navagation);

                front_nav.setVisibility(View.GONE);
                layout_top.setVisibility(View.VISIBLE);
                nav.setVisibility(View.VISIBLE);
                bottom_nav.setVisibility(View.GONE);

                TextView countText = (TextView) findViewById(R.id.count_item);
                countText.setText("1 Selected item");
                count[0] = 0;

                gridAdapter.notifyDataSetChanged();
                gridView.setAdapter(gridAdapter);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void deleteAlbum(ArrayList<String> src, ArrayList<Integer> img, ArrayList<String> b){
        for (int i = 0; i < src.size(); i++){
            for (int j = 0; j < b.size(); j++){
                if (src.get(i) == b.get(j)){
                    src.remove(i);
                    b.remove(j);
                    img.remove(i);
                    i--;
                    break;
                }
            }
        }
    }

    public void showAlbumOption(View v){
          PopupMenu popupMenu = new PopupMenu(this, v);
          popupMenu.setOnMenuItemClickListener(this);
          popupMenu.inflate(R.menu.album_option);
          popupMenu.show();
      }
}