package com.example.album;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends Activity{
    BottomNavigationView bottomNav;
    PopupMenu popup;
    com.github.chrisbanes.photoview.PhotoView img;
    int isChecked; //favorite btn
    String[] details;
    ImageButton back,more,done;
    String pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = (com.github.chrisbanes.photoview.PhotoView) findViewById(R.id.photo_view);
        bottomNav=(BottomNavigationView)findViewById(R.id.bottom_nav);
        back=(ImageButton)findViewById(R.id.back);
        more=(ImageButton)findViewById(R.id.more);
        done=(ImageButton)findViewById(R.id.done);


        bottomNav.setItemIconTintList(null);
        isChecked=0;
        pos= "detail";
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return itemNavigationBottomSelected(item);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v) {handleBack(v);}
        });



        // details chứa name, date, location, size, description
        // theo thứ tự index 0 -> 4
        // name, date, location, size, description phải được lấy từ database
        // demo chưa có database nên chỉ gán cứng chạy thử
        details = new String[5];
        details[0] = "Image";
        details[1] = "29/10/2022";
        details[2] = "Ho Chi Minh";
        details[3] = "5KB";
        details[4] = "Flexing at Circle K with my bros";
    }
    public void showPopup(View v) {
        popup = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onPopUpMenuClick(menuItem);
            }
        });
        popup.inflate(R.menu.ic_more_submenu);
        popup.show();
    }
    public void handleBack(View v){
        switch (pos){
            case "detail":
                // dang o layout_detail
                break;
            case "edit":
                //dang o edit
                bottomNav.getMenu().clear();
                bottomNav.inflateMenu(R.menu.bottom_navigation);
                more.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                more.requestLayout();
                done.getLayoutParams().height = 0;
                done.requestLayout();
                pos="detail";
                break;
            case "paint":
                //dang o paint
                handleEdit();
                break;
        }
    }
    public boolean onPopUpMenuClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.details:
                handleDetails();
                Toast.makeText(this, "details", Toast.LENGTH_SHORT).show();
                break;
            case R.id.add:
                Toast.makeText(this, "add", Toast.LENGTH_SHORT).show();
                break;
            case R.id.set:
                Toast.makeText(this, "set", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rename:
                Toast.makeText(this, "rename", Toast.LENGTH_SHORT).show();
                break;
            default:
                return false;
        }
        return true;
    }

    public void handleRename() {
        final AlertDialog.Builder renameDialog = new AlertDialog.Builder(MainActivity.this);
        renameDialog.setTitle("Rename to:");
        final EditText input = new EditText(MainActivity.this);
        input.setText(details[0]);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        renameDialog.setView(input);
        renameDialog.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                details[0] = input.getText().toString();
            }
        });
        renameDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        renameDialog.show();
    }

    public void handleDetails() {
        final AlertDialog.Builder detailsDialog = new AlertDialog.Builder(MainActivity.this);
        detailsDialog.setTitle("Details");
        final EditText input = new EditText(MainActivity.this);
        String info = details[0] + "\n" + details[1] + "\n" + details[2] + "\n" + details[3];
        detailsDialog.setMessage(info);
        input.setHint("Enter the description of this image");
        input.setText(details[4]);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setSingleLine(false);
        detailsDialog.setView(input);
        detailsDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                details[4] = input.getText().toString();
            }
        });
        detailsDialog.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        detailsDialog.show();
    }


    public boolean itemNavigationBottomSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.edit:
                handleEdit();
                return true;
            case R.id.favorite:
                handleFavorite();
                return true;
            case R.id.delete:
                Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.share:
                handleShare();
                return true;
            case R.id.crop:
                Toast.makeText(this, "crop", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rotate:
                Toast.makeText(this, "crop", Toast.LENGTH_SHORT).show();
                break;
            case R.id.flip:
                Toast.makeText(this, "crop", Toast.LENGTH_SHORT).show();
                break;
            case R.id.paint:
                handleBrush();
                break;
            case R.id.undo:
                return true;
            case R.id.redo:
                break;
            case R.id.eraser:
                break;
            case R.id.color:
                Toast.makeText(this, "crop", Toast.LENGTH_SHORT).show();

                break;
            case R.id.pen:
                break;
        }
        return false;
    }

    public void handleFavorite() {
        if(isChecked==0){
            bottomNav.getMenu().findItem(R.id.favorite).setIcon(R.drawable.ic_favorite_colored);
            isChecked=1;
        }else {
            bottomNav.getMenu().findItem(R.id.favorite).setIcon(R.drawable.ic_favorite);
            isChecked=0;
        }
    }

    public void handleShare() {
        Bitmap b = ((BitmapDrawable)img.getDrawable()).getBitmap();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), b, "Title", null);
        Uri imageUri =  Uri.parse(path);
        share.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(share, "Select"));
    }

    public void handleEdit(){
        bottomNav.getMenu().clear();
        bottomNav.inflateMenu(R.menu.edit_nav);
        done.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        done.requestLayout();
        more.getLayoutParams().height = 0;
        more.requestLayout();
        bottomNav.setItemIconTintList(null);
        bottomNav.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);

        pos="edit";
    }
    public void handleBrush(){
        bottomNav.getMenu().clear();
        bottomNav.inflateMenu(R.menu.paint_nav);
        done.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        done.requestLayout();
        more.getLayoutParams().height = 0;
        more.requestLayout();
        bottomNav.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        pos="paint";
    }
    public void handleColor(View v){
    }
}