package com.example.album;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.album.album.AlbumFragmentDirections;
import com.example.album.gallery.GalleryFragmentDirections;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.yalantis.ucrop.UCropFragment;
import com.yalantis.ucrop.UCropFragmentCallback;

public class MainActivity extends AppCompatActivity implements UCropFragmentCallback {

    private Menu navigationMenu;
    NavController navController;
    SplitToolbar navigationBar;
    Toolbar app_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = getIntent().getIntExtra("theme",0);

        if (style != 0) {
            getTheme().applyStyle(style,true);
        }

        setContentView(R.layout.activity_main);
        navigationBar = findViewById(R.id.navigation_bar);
        app_bar = findViewById(R.id.app_bar);
        setUpNavController();
        setUpMainActionBar();
        setUpNavigationActionBar();
//        NavHostFragment navHostFragment =
//                (NavHostFragment) getSupportFragmentManager()
//                        .findFragmentById(R.id.nav_host_fragment);

    }

    private void setUpNavController(){
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment != null
                ? navHostFragment.getNavController()
                : null;
    }

    private void setUpMainActionBar(){

        setSupportActionBar(app_bar);
        AppBarConfiguration config = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this,navController,config);
        addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.album_option,menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == R.id.menu_choose_color){
                    execChooseTheme();
                }
                if(id == R.id.menu_settings){
                    navController.navigate(R.id.settingsFragment);
                    navigationBar.setVisibility(View.GONE);
                }
                return false;
            }
        });
        app_bar.setNavigationOnClickListener(v -> navController.navigateUp());
    }

    private void setUpNavigationActionBar(){
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.galleryFragment,R.id.albumFragment).build();
        if(navController!= null) {
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        }
        navigationBar.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.navigation_bar_menu,menu);
                navigationMenu = menu;
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                for(int i =0; i < navigationMenu.size(); i++){
                    MenuItem currentItem = navigationMenu.getItem(i);
                    if (currentItem.getItemId() == R.id.galleryFragment) {
                        currentItem.setTitle(
                                getSpannableStringFromMenuItem(currentItem, R.attr.iconColor));
                    } else if (currentItem.getItemId() == R.id.albumFragment) {
                        currentItem.setTitle(
                                getSpannableStringFromMenuItem(currentItem, R.attr.iconColor));
                    } else if (currentItem.getItemId() == R.id.privacyFragment) {
                        currentItem.setTitle(
                                getSpannableStringFromMenuItem(currentItem, R.attr.iconColor));
                    }
                }

                menuItem.setTitle(getSpannableStringFromMenuItem(menuItem,R.attr.highlightTextColor));

                int destinationId = menuItem.getItemId();
                int currentId = (navController != null) ?
                        navController.getCurrentDestination().getId()
                        : 0;
                if(navController == null) {
                    return false;
                }
                if(currentId == R.id.albumFragment){
                    if(destinationId == R.id.galleryFragment) {
                        NavDirections action = AlbumFragmentDirections
                                .actionAlbumFragmentToGalleryFragment();
                        navController.navigate(action, setUpSpecificNavOpts(0));
                    }
                }
                else if(currentId == R.id.galleryFragment){
                    if(destinationId == R.id.albumFragment){
                        NavDirections action = GalleryFragmentDirections
                                .actionGalleryFragmentToAlbumFragment();
                        navController.navigate(action, setUpSpecificNavOpts(1));
                    }
                }
                return true;
            }
        });

        String currentFragmentName = getForegroundFragment().getClass().getSimpleName();
        if(currentFragmentName.equals("AlbumFragment")
                || currentFragmentName.equals("DetailAlbumFragment")){
            MenuItem currentItem = navigationMenu.getItem(1);
            currentItem.setTitle(getSpannableStringFromMenuItem(currentItem,R.attr.highlightTextColor));
        }
        else if(currentFragmentName.equals("DetailFragment")
                || currentFragmentName.equals("GalleryFragment")) {
            MenuItem currentItem = navigationMenu.getItem(0);
            currentItem.setTitle(getSpannableStringFromMenuItem(currentItem, R.attr.highlightTextColor));
        }
    }

    @NonNull
    private SpannableString getSpannableStringFromMenuItem(@NonNull MenuItem item, int colorAttr){
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(colorAttr, typedValue, true);
        int color = ContextCompat.getColor(this, typedValue.resourceId);
        SpannableString spanString =
                new SpannableString(item.getTitle().toString());
        spanString.setSpan(
                new ForegroundColorSpan(color),
                0,
                spanString.length(),
                0
        ); //fix the color to white
        return spanString;
    }

    @NonNull
    private NavOptions setUpSpecificNavOpts(int direction){
        if(direction == 0){
            return new NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setEnterAnim(R.anim.slide_in_left)
                    .setExitAnim(R.anim.slide_out_right)
                    .setPopEnterAnim(R.anim.slide_in_right_slow)
                    .setPopExitAnim(R.anim.slide_out_left_slow)
                    .setPopUpTo(R.id.nav_graph,false,true)
                    .setLaunchSingleTop(true)
                    .build();
        }
        else {
            return new NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setEnterAnim(R.anim.slide_in_right)
                    .setExitAnim(R.anim.slide_out_left)
                    .setPopEnterAnim(R.anim.slide_in_left_slow)
                    .setPopExitAnim(R.anim.slide_in_right_slow)
                    .setPopUpTo(R.id.nav_graph,false,true)
                    .setLaunchSingleTop(true)
                    .build();
        }
    }

    public Fragment getForegroundFragment(){
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        return navHostFragment == null ? null : navHostFragment.getChildFragmentManager().getFragments().get(0);
    }

    private void execChooseTheme(){
        MaterialAlertDialogBuilder builder =
                new MaterialAlertDialogBuilder(
                        MainActivity.this, R.style.AlertDialogTheme);
        View view = getLayoutInflater().inflate(R.layout.choosing_color_dialog, null);
        builder.setView(view);
        ToggleButtonGroupTableLayout radioGroup;
        radioGroup = view.findViewById(R.id.radioGroup);
        builder.setNegativeButton("Cancel", (dialog, which) -> {
        });
        builder.setPositiveButton("Select", (dialog, which) -> {
            int radioButtonId = radioGroup.getCheckedRadioButtonId();
            int themeId;
            int nightModeFlag = getResources().getConfiguration().uiMode
                    & Configuration.UI_MODE_NIGHT_MASK;
            if(nightModeFlag == Configuration.UI_MODE_NIGHT_NO) {
                if (radioButtonId == R.id.sea_pink_button) {
                    themeId = R.style.AppTheme_SeaPink;
                } else if (radioButtonId == R.id.pink_button) {
                    themeId = R.style.AppTheme_Pink;
                } else if (radioButtonId == R.id.red_button) {
                    themeId = R.style.AppTheme_Red;
                } else if (radioButtonId == R.id.purple_button) {
                    themeId = R.style.AppTheme_Purple;
                } else if (radioButtonId == R.id.blue_button) {
                    themeId = R.style.AppTheme_Blue;
                } else if (radioButtonId == R.id.garden_button) {
                    themeId = R.style.AppTheme_Garden;
                } else if (radioButtonId == R.id.bermuda_button) {
                    themeId = R.style.AppTheme_Bermuda;
                } else if (radioButtonId == R.id.bright_sun_button) {
                    themeId = R.style.AppTheme_BrightSun;
                } else {
                    return;
                }
            }
            else if (nightModeFlag == Configuration.UI_MODE_NIGHT_YES){
                if (radioButtonId == R.id.sea_pink_button) {
                    themeId = R.style.AppTheme_Dark_SeaPink;
                } else if (radioButtonId == R.id.pink_button) {
                    themeId = R.style.AppTheme_Dark_Pink;
                } else if (radioButtonId == R.id.red_button) {
                    themeId = R.style.AppTheme_Dark_Red;
                } else if (radioButtonId == R.id.purple_button) {
                    themeId = R.style.AppTheme_Dark_Purple;
                } else if (radioButtonId == R.id.blue_button) {
                    themeId = R.style.AppTheme_Dark_Blue;
                } else if (radioButtonId == R.id.garden_button) {
                    themeId = R.style.AppTheme_Dark_Garden;
                } else if (radioButtonId == R.id.bermuda_button) {
                    themeId = R.style.AppTheme_Dark_Bermuda;
                } else if (radioButtonId == R.id.bright_sun_button) {
                    themeId = R.style.AppTheme_Dark_BrightSun;
                } else {
                    return;
                }
            }
            else{
                return;
            }
            getIntent().putExtra("theme",themeId);
            recreate();
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }

    @Override
    public void loadingProgress(boolean showLoader) {

    }

    @Override
    public void onCropFinish(UCropFragment.UCropResult result) {

    }
}