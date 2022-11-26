package com.example.album;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.content.CursorLoader;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.example.album.album.AlbumFragmentDirections;
import com.example.album.data.ImagesModel;
import com.example.album.gallery.PhotosFragmentDirections;
import com.example.album.ui.SplitToolbar;
import com.example.album.ui.ToggleButtonGroupTableLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String TAG = "MainActivity";
    private Menu navigationMenu;
    NavController navController;
    SplitToolbar navigationBar;
    Toolbar app_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences shared_prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String langCode = shared_prefs.getString("language", "en");
        setLocal(MainActivity.this, langCode);
        resources = getResources();
        ArrayList<Integer> extra = (ArrayList<Integer>) getIntent().getSerializableExtra("theme");
        if (extra != null) {
            int nightModeFlag = getResources().getConfiguration().uiMode
                    & Configuration.UI_MODE_NIGHT_MASK;
            if(nightModeFlag == Configuration.UI_MODE_NIGHT_YES){
                int style = extra.get(1);
                getTheme().applyStyle(style,true);
            }
            else{
                int style = extra.get(0);
                getTheme().applyStyle(style,true);
            }
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

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences shared_prefs = PreferenceManager.getDefaultSharedPreferences(this);
        shared_prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        SharedPreferences shared_prefs = PreferenceManager.getDefaultSharedPreferences(this);
        shared_prefs.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("language")) {
            String langCode = sharedPreferences.getString(key, "en");
            setLocal(MainActivity.this, langCode);
            finish();
            startActivity(getIntent());
        }
    }

    public void setLocal(Activity activity, String langCode) {
        Locale locale = new Locale(langCode);
        locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
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
                menuInflater.inflate(R.menu.default_menu,menu);
            }
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == R.id.menu_choose_color){
                    execChooseTheme();
                }
                if(id == R.id.menu_settings){
                    navController.navigate(R.id.SettingsFragment);
                    navigationBar.setVisibility(View.GONE);
                }
                if(id == R.id.camera){
//                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//                    startActivity(intent);
                    startCamera();
                }
                return false;
            }
        });
        app_bar.setNavigationOnClickListener(v -> navController.navigateUp());
    }

    private void setUpNavigationActionBar(){
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.PhotosFragment,R.id.AlbumFragment).build();
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
                    if (currentItem.getItemId() == R.id.PhotosFragment) {
                        currentItem.setTitle(
                                getSpannableStringFromMenuItem(currentItem, R.attr.iconColor));
                    } else if (currentItem.getItemId() == R.id.AlbumFragment) {
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
                if(currentId == R.id.AlbumFragment){
                    if(destinationId == R.id.PhotosFragment) {
                        NavDirections action = AlbumFragmentDirections
                                .actionAlbumFragmentToPhotosFragment();
                        navController.navigate(action, setUpSpecificNavOpts(0));
                    }
                }
                else if(currentId == R.id.PhotosFragment){
                    if(destinationId == R.id.AlbumFragment){
                        NavDirections action = PhotosFragmentDirections
                                .actionPhotosFragmentToAlbumFragment();
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
                || currentFragmentName.equals("PhotosFragment")) {
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
        builder.setNegativeButton(resources.getString(R.string.label_cancel), (dialog, which) -> {
        });
        builder.setPositiveButton(resources.getString(R.string.select), (dialog, which) -> {
            int radioButtonId = radioGroup.getCheckedRadioButtonId();
//            int themeId;
            ArrayList<Integer> themeId = new ArrayList<>();
//            int nightModeFlag = getResources().getConfiguration().uiMode
//                    & Configuration.UI_MODE_NIGHT_MASK;
            if (radioButtonId == R.id.sea_pink_button) {
                themeId.add(R.style.AppTheme_SeaPink);
                themeId.add(R.style.AppTheme_Dark_SeaPink);
            } else if (radioButtonId == R.id.pink_button) {
                themeId.add(R.style.AppTheme_Pink);
                themeId.add(R.style.AppTheme_Dark_Pink);
            } else if (radioButtonId == R.id.red_button) {
                themeId.add(R.style.AppTheme_Red);
                themeId.add(R.style.AppTheme_Dark_Red);
            } else if (radioButtonId == R.id.purple_button) {
                themeId.add(R.style.AppTheme_Purple);
                themeId.add(R.style.AppTheme_Dark_Purple);
            } else if (radioButtonId == R.id.blue_button) {
                themeId.add(R.style.AppTheme_Blue);
                themeId.add(R.style.AppTheme_Dark_Blue);
            } else if (radioButtonId == R.id.garden_button) {
                themeId.add(R.style.AppTheme_Garden);
                themeId.add(R.style.AppTheme_Dark_Garden);
            } else if (radioButtonId == R.id.bermuda_button) {
                themeId.add(R.style.AppTheme_Bermuda);
                themeId.add(R.style.AppTheme_Dark_Bermuda);
            } else if (radioButtonId == R.id.bright_sun_button) {
                themeId.add(R.style.AppTheme_BrightSun);
                themeId.add(R.style.AppTheme_Dark_BrightSun);
            } else {
                themeId.add(R.style.AppTheme_SeaPink);
                themeId.add(R.style.AppTheme_Dark_SeaPink);
            }

            getIntent().putExtra("theme",themeId);
//            getIntent().putExtra("theme", (Parcelable) themeId);
            recreate();
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogPopupStyle;
        dialog.show();
    }

    private Uri saveImage(Bitmap bitmap) throws FileNotFoundException {
        // Create an image file name
        Uri imageUri;
        String timeStamp = Long.toString(System.currentTimeMillis());
        String imageFileName = "JPEG_" + timeStamp + "_";
        OutputStream fos;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName + ".jpg");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/" +getString(R.string.app_name) + "/Camera");
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(imageUri);
        }else{
            String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            File image = new File(imagesDir + "/" +getString(R.string.app_name) + "/Camera", imageFileName + ".jpg");
            fos = new FileOutputStream(image);
            imageUri = Uri.fromFile(image);
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageUri;
    }

    private void startCamera(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        takePicture.launch(cameraIntent);

    }

    ActivityResultLauncher<Intent> takePicture
            = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Bitmap bmp;
                    if (result.getData() != null) {
                        bmp = (Bitmap) result.getData().getExtras().get("data");

                        try {
                            Uri imageUri = saveImage(bmp);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("image", imageUri);
                            navController.navigate(R.id.DetailImage, bundle);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                    else{
                        throw new IllegalStateException("Can not get image returned by camera :)");
                    }
                }
            });

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    ImagesModel imagesModel;
    @Override
    protected void onStart() {
        super.onStart();
        imagesModel = new ViewModelProvider(this).get(ImagesModel.class);
        imagesModel.setCursor(getCursor());
//        imagesModel.getCursor().observe((LifecycleOwner) getLifecycle(), new Observer<Cursor>() {
//            @Override
//            public void onChanged(Cursor cursor) {
//
//            }
//        });
//        imagesModel.getCursor().getValue().setNotificationUri();
    }

    @SuppressLint("Range")
    private MutableLiveData<Cursor> getCursor(){
        String[]projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.ORIENTATION,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.MIME_TYPE ,
                MediaStore.Images.ImageColumns.DATE_MODIFIED,
                MediaStore.Images.ImageColumns.WIDTH,
                MediaStore.Images.ImageColumns.HEIGHT,
                MediaStore.Images.ImageColumns.DESCRIPTION
        };

        final CursorLoader cursorLoader = new CursorLoader(this,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                ,projection,
                null,
                null,
                "date_modified DESC"
        );
//
//        final CursorLoader cursorLoader2 = new CursorLoader(this,
//                MediaStore.Images.Media.INTERNAL_CONTENT_URI
//                ,null,
//                null,
//                null,
//                "date_modified DESC"
//        );
//
//        Cursor c2 = cursorLoader2.loadInBackground();
//        int b = c2.getCount();

        MutableLiveData<Cursor> cursor = new MutableLiveData<>();
        cursor.setValue(cursorLoader.loadInBackground());
//        int a = cursor.getValue().getCount();
        return cursor;
    }

}
