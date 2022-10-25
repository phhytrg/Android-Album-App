package com.example.album;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav=(BottomNavigationView)findViewById(R.id.bottom_nav);
        bottomNav.setItemIconTintList(null);




    }
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);

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
                Toast.makeText(this, "details", Toast.LENGTH_SHORT).show();
                break;
            case R.id.add:
                Toast.makeText(this, "add", Toast.LENGTH_SHORT).show();
                break;
            case R.id.set:
                Toast.makeText(this, "set", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rename:
                break;
            default:
                return false;
        }
        return true;
    }

}