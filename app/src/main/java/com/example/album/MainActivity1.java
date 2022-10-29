package com.example.album;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity1 extends Activity{
    BottomNavigationView bottomNav;
    PopupMenu popup;
    //com.github.chrisbanes.photoview.PhotoView img;
    int isChecked;
    String[] details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //img = (com.github.chrisbanes.photoview.PhotoView) findViewById(R.id.photo_view);
        bottomNav=(BottomNavigationView)findViewById(R.id.bottom_nav);
        bottomNav.setItemIconTintList(null);
        isChecked=0;
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return itemNavigationBottomSelected(item);
            }
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
        //Drawable drawable = img.getDrawable();
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
                handleRename();
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
                Toast.makeText(this, "edit", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.favorite:
                if(isChecked==0){
                    bottomNav.getMenu().findItem(R.id.favorite).setIcon(R.drawable.ic_favorite_colored);
                    isChecked=1;
                }else {
                    bottomNav.getMenu().findItem(R.id.favorite).setIcon(R.drawable.ic_favorite);
                    isChecked=0;
                }
                Toast.makeText(this, "favorite", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.delete:
                Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.share:
                Toast.makeText(this, "share", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }
}