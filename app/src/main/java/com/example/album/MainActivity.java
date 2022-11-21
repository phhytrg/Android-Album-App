package com.example.album;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
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

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity{

    private Menu navigationMenu;
    NavController navController;
    SplitToolbar navigationBar;
    Toolbar app_bar;
    private Uri imageUri;

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
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogPopupStyle;
        dialog.show();
    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private ActivityResultContracts.TakePicture takePictureContract
            = new ActivityResultContracts.TakePicture();

    private ActivityResultLauncher<Uri> takePicture
            = registerForActivityResult(
            takePictureContract,
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if(result){

                    }
                }
            }
    );


    private void startCamera() {
        Intent takePictureIntent
                = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = getTmpFileDir();
                File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),"APPTAG");
                File file = new File(mediaStorageDir.getPath() + File.separator + "photo.jpg");
                Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePicture.launch(uri);
                ;
            }
        }
    }


//    private ActivityResultContract<Uri, Pair<Boolean, Uri>> cameraActivityContract
//            = new ActivityResultContract<Uri, Pair<Boolean,Uri>> () {
//        Uri imageUri;
//
//        @Nullable
//        @Override
//        public SynchronousResult<Pair<Boolean, Uri>> getSynchronousResult(@NonNull Context context, Uri input) {
//            return null;
//        }
//
//        @NonNull
//        @Override
//        public Intent createIntent(@NonNull Context context, Uri uri) {
//            Intent takePictureIntent  = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            imageUri = uri;
//            return takePictureIntent;
//        }
//
//        @Override
//        public Pair<Boolean, Uri> parseResult(int i, @Nullable Intent intent) {
//            Bundle bundle = intent.getExtras();
//            if (bundle != null) {
//                for (String key : bundle.keySet()) {
//                    Log.e("AAA", key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
//                }
//            }
//            if(i == Activity.RESULT_OK){
//                return new Pair<>(true, imageUri);
//            }
//            return new Pair<>(false,null);
//        }
//    };





//    static final int REQUEST_IMAGE_CAPTURE = 1;
//
//    private ActivityResultLauncher<Intent> startCameraActivityIntent
//            = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            (ActivityResultCallback<ActivityResult>) result -> {
//                if(result.getResultCode() == RESULT_OK){
//                    Log.d("Album", this.imageUri.toString());
//                }
//            }
////            result -> {
////            }
////                if(result.first){
////                    Uri imageUri = result.second;
////                    InputStream imageStream = null;
////                    try {
////                        ContentResolver resolver = getContentResolver();
////                        imageStream = getContentResolver()
////                                .openInputStream(imageUri);
////
////                        Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
////                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
////                        selectedImage.compress(Bitmap.CompressFormat.JPEG,100,baos);
////                        byte[] byteArray = baos.toByteArray();
////                        try {
////                        String uniqueString = UUID.randomUUID().toString();
////                        FileOutputStream fo = new FileOutputStream(
////                                Environment.getExternalStorageDirectory()
////                                        + uniqueString
////                                        + "/_camera.JPEG"
////                        );
////                        fo.write(byteArray);
////                        fo.flush();
////                        fo.close();
////                    } catch (FileNotFoundException e) {
////                        e.printStackTrace();
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                    } catch (FileNotFoundException e) {
////                        e.printStackTrace();
////                    }
////                }
//
////                if(result.first){
////                    Uri imageUri = result.second;
////                    Bitmap bitmap = null;
////                    try {
////                        bitmap = (Bitmap) MediaStore.Images.Media
////                                .getBitmap(getContentResolver(),imageUri);
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
////                    bitmap.compress(Bitmap.CompressFormat.PNG,100, stream);
////                    byte[] byteArray = stream.toByteArray();
////                    try {
////                        String uniqueString = UUID.randomUUID().toString();
////                        FileOutputStream fo = new FileOutputStream(
////                                Environment.getExternalStorageDirectory()
////                                        + uniqueString
////                                        + "/_camera.png"
////                        );
////                        fo.write(byteArray);
////                        fo.flush();
////                        fo.close();
////                    } catch (FileNotFoundException e) {
////                        e.printStackTrace();
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                }
////            }
//    );

    private Uri getTmpFileDir(){
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),"APPTAG");
        if(mediaStorageDir.exists() && !mediaStorageDir.mkdir()) {
            try {
                throw new Exception("Failed to create directory to store media temp file");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
//        String uniqueString = UUID.randomUUID().toString();
//        FileOutputStream fo = new FileOutputStream(
//                Environment.getExternalStorageDirectory()
//                        + uniqueString
//                        + "/_camera.png"
//        );
        return FileProvider.getUriForFile(this,
                getApplicationContext().getPackageName() + ".provider",
                new File(mediaStorageDir.getPath() + File.separator + "photo.jpg"));
    }
//    private void startCamera(){
//        Intent takePictureIntent  = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        imageUri = getTmpFileDir();
//        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
////        takePictureIntent.putExtra("requestCode", REQUEST_IMAGE_CAPTURE);
//        try{
//            startCamera.launch(imageUri);
//        }
//        catch (ActivityNotFoundException e){
//            e.printStackTrace();
//        }
//    }
//
//    ActivityResultLauncher<Uri> startCamera = registerForActivityResult(
//            new ActivityResultContracts.TakePicture(),
//            result -> {
//                if(result){
//
//                }
//            });
}