package com.example.album;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.RenderScript;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.album.ui.ImageFilter;
import com.example.album.ui.SplitToolbar;
import com.example.album.ui.ZoomableImageView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class DetailImageFragment extends Fragment{
    private BottomNavigationView navigationView;
    PopupMenu popup;
    ImageButton navigateUpButton;
    ImageButton overflowButton;
    ImageButton doneButton;
    ImageButton favoriteButton;
    ImageButton autoFilterButton;
    ImageButton settingFilterButton;
    ActionBar app_bar;
    SplitToolbar navigationBar;
    ZoomableImageView imageView;
    int isChecked;
    //filter
    ViewGroup filter_gallery;
    Uri imageUri;

    ImageCropping imageCropping;
    ImageEditing imageEditing;
    ImagePainting imagePainting;
    ImageFilterEffects imageFilterEffects;

    CurrentState currentState = CurrentState.DETAIL;

    enum CurrentState{
        DETAIL,
        EDIT,
        PAINT,
        FILTER
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(getArguments() != null){
            imageUri = getArguments().getParcelable("image");
        }
        app_bar = ((MainActivity)requireActivity()).getSupportActionBar();
        if (app_bar != null) {
            app_bar.hide();
        }
        Window window = requireActivity().getWindow();
        if(window != null) {
            window.setStatusBarColor(getResources()
                    .getColor(R.color.dark_grey, requireActivity().getTheme()));
            window.getDecorView().setSystemUiVisibility(0);
        }

        navigationBar = requireActivity().findViewById(R.id.navigation_bar);
        navigationBar.setVisibility(View.GONE);

        return inflater.inflate(R.layout.fragment_image_detail,container,false).getRootView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        app_bar.show();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageView = view.findViewById(R.id.photo_view);
        imageView.setImageURI(imageUri);
        navigateUpButton = view.findViewById(R.id.back_button);
        overflowButton = view.findViewById(R.id.overflow_button);
        doneButton = view.findViewById(R.id.done);
        favoriteButton = view.findViewById(R.id.favorite);

        autoFilterButton =view.findViewById(R.id.auto_filter);
        settingFilterButton=view.findViewById(R.id.setting_filter);
//        imageView.setImageResource(resourceId);
        navigationView = view.findViewById(R.id.bottom_nav);
        //filter
        filter_gallery = view.findViewById(R.id.filter_gallery);

        imageCropping = new ImageCropping();
        imageEditing = new ImageEditing();
        imagePainting = new ImagePainting();

        isChecked=0;
        navigationView.getMenu().getItem(0).setCheckable(false);
        navigationView.setOnItemSelectedListener(this::itemNavigationBottomSelected);
        navigateUpButton.setOnClickListener(this::handleBack);
        overflowButton.setOnClickListener(this::showPopup);
        favoriteButton.setOnClickListener(v -> handleFavorite());
        doneButton.setOnClickListener(this::handleDone);
        autoFilterButton.setOnClickListener(v->imageFilterEffects.handleAutoFilter(v));
        settingFilterButton.setOnClickListener(v -> imageFilterEffects.handleSettingFilter(v));
        //Drawable drawable = img.getDrawable();

        //Android default back button now acts as "Action bar" Back button
        OnBackPressedCallback callback = new OnBackPressedCallback(true ) {
            @Override
            public void handleOnBackPressed() {
                handleBack(view);
            }
        };
        requireActivity().getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), callback);

    }

    private void onBackPressed(){
        // dang o layout_detail
        Window window = requireActivity().getWindow();
        TypedValue outValue = new TypedValue();
        requireActivity().getTheme()
                .resolveAttribute(androidx.appcompat.R.attr.colorPrimaryDark, outValue, true);
        window.setStatusBarColor(outValue.data);

        window.getDecorView().setSystemUiVisibility(0);

        NavHostFragment hostFragment = (NavHostFragment) requireActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = hostFragment != null
                ? hostFragment.getNavController()
                : null;
        if (navController != null) {
            navController.navigateUp();
        }
    }

    private void handleBack(View v){
        switch (currentState){
            case DETAIL:
                this.onBackPressed();
                break;
            case EDIT:
                imageEditing.onBackPressed();
                break;
            case PAINT:
                imageEditing.switchLayout();
                break;
            case FILTER:
                imageFilterEffects.onBackPressed();
                imageEditing.switchLayout();
                break;
        }
    }

    public void handleDone(View v){
        switch (currentState){
            case EDIT:
                imageEditing.onDonePressed();
                break;
            case PAINT:
                imagePainting.onDonePressed();
                imageEditing.switchLayout();
                break;
            case FILTER:
                imageFilterEffects.onBackPressed();
                imageEditing.switchLayout();
                break;
        }
    }

    public boolean onPopUpMenuClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.details:
                handleDetails();
                break;
            case R.id.add:
                Toast.makeText(requireContext(), "add", Toast.LENGTH_SHORT).show();
                break;
            case R.id.set:
                handleSet();
                break;
            case R.id.rename:
                handleRename();
                break;
            default:
                return false;
        }
        return true;
    }

    private void handleDetails() {
        final AlertDialog.Builder detailsDialog = new AlertDialog.Builder(requireContext());
        detailsDialog.setTitle("Details");
        final EditText input = new EditText(requireContext());
        String info = "details[0] + \n + details[1] + \n + details[2] + \n + details[3]";
        detailsDialog.setMessage(info);
        input.setHint("Enter the description of this image");
        input.setText("details[4]");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setSingleLine(false);
        detailsDialog.setView(input);
        detailsDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                details[4] = input.getText().toString();
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

    public void handleRename() {
        final AlertDialog.Builder renameDialog = new AlertDialog.Builder(requireContext());
        renameDialog.setTitle("Rename to:");
        final EditText input = new EditText(requireContext());
        input.setText("details[0]");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        renameDialog.setView(input);
        renameDialog.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                details[0] = input.getText().toString();
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


    private void handleSet() {

//        MaterialAlertDialogBuilder builder =
//                new MaterialAlertDialogBuilder(
//                        requireContext(), R.style.AlertDialogTheme);
//        View view = getLayoutInflater().inflate(R.layout.custom_dialog_set, null);
//        builder.setView(view);

        final Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.custom_dialog_set);
        ImageButton homeOption = dialog.findViewById(R.id.home_option);
        ImageButton lockOption = dialog.findViewById(R.id.lock_option);
        ImageButton homeNLock = dialog.findViewById(R.id.home_lock_option);
        AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
        homeOption.setOnClickListener(view -> {
            view.startAnimation(buttonClick);
            setWallHome();
            dialog.dismiss();
        });
        lockOption.setOnClickListener(view -> {
            view.startAnimation(buttonClick);
            setWallLock();
            dialog.dismiss();
        });
        homeNLock.setOnClickListener(view -> {
            view.startAnimation(buttonClick);
            setWallLock();
            setWallLock();
            dialog.dismiss();
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogPopupStyle;
        dialog.show();
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(layoutParams);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
    }

    public void setWallHome(){
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
            WallpaperManager.getInstance(requireContext()).setBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setWallLock(){
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
            WallpaperManager.getInstance(requireContext()).setBitmap(bitmap,null,true,WallpaperManager.FLAG_LOCK);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showPopup(View v) {
        popup = new PopupMenu(requireContext(), v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onPopUpMenuClick(menuItem);
            }
        });
        popup.inflate(R.menu.image_menu);
        popup.show();
    }

    public void handleFavorite() {
        if(isChecked==0){
            favoriteButton.setImageResource(R.drawable.ic_favorite_colored);
            isChecked=1;
        }else {
            favoriteButton.setImageResource(R.drawable.ic_favorite);
            isChecked=0;
        }
    }

    private boolean itemNavigationBottomSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.edit){
            imageEditing.switchLayout();
        }
        else if(id == R.id.delete) {
            Toast.makeText(requireContext(), "delete", Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.share){
            handleShare();
        }
        else {
            return false;
        }
        return true;
    }

    private void handleShare() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        share.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(share, "Select"));
    }

    class ImageEditing{

        private Bitmap bitmap;
        {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void switchLayout(){
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.edit_nav);
            doneButton.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            doneButton.requestLayout();
            favoriteButton.getLayoutParams().height = 0;
            favoriteButton.requestLayout();
            overflowButton.getLayoutParams().height = 0;
            overflowButton.requestLayout();
            autoFilterButton.getLayoutParams().height = 0;
            autoFilterButton.requestLayout();
            settingFilterButton.getLayoutParams().height = 0;
            settingFilterButton.requestLayout();
            navigationView.setVisibility(View.VISIBLE);
            navigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
            currentState = CurrentState.EDIT;

            navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    if(id == R.id.crop){
                        imageCropping.handleCrop();
                        if(imageUri != null){
                            imageView.setImageURI(imageUri);
                        }
                        bitmap = imageCropping.bitmap;
                    }
                    else if(id == R.id.flip){
                        handleFlip();
                    }
                    else if(id == R.id.rotate){
                        handleRotate();
                    }
                    else if(id == R.id.filter){
                        imageFilterEffects.handleFilter();
                    }
                    else if(id == R.id.paint){
                        imagePainting.switchLayout();
                    }
                    else {
                        return false;
                    }
                    return true;
                }
            });
        }

        private void handleFlip() {
            Matrix mat = new Matrix();
            mat.postScale(-1f, 1f, bitmap.getWidth() / 2f, bitmap.getHeight() / 2f);
            Bitmap image_bm_flipped = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
            bitmap = image_bm_flipped;
            imageView.setImageBitmap(bitmap);
        }

        private void handleRotate() {
            Matrix mat = new Matrix();
            mat.postRotate(90);
            Bitmap image_bm_rotated = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(),bitmap.getHeight(), mat, true);
            imageView.setImageBitmap(image_bm_rotated);
        }

        private void onBackPressed(){
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.image_option);
            navigationView.getMenu().getItem(0).setCheckable(false);
            overflowButton.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            overflowButton.requestLayout();
            favoriteButton.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            favoriteButton.requestLayout();
            doneButton.getLayoutParams().height = 0;
            doneButton.requestLayout();
            currentState = CurrentState.DETAIL;
            imageView.setImageURI(imageUri);
        }

        private void onDonePressed(){
            //dang o edit
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.image_option);
            navigationView.getMenu().getItem(0).setCheckable(false);
            overflowButton.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            overflowButton.requestLayout();
            favoriteButton.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            favoriteButton.requestLayout();
            doneButton.getLayoutParams().height = 0;
            doneButton.requestLayout();
            currentState = CurrentState.DETAIL;

            // Lưu ảnh đã chỉnh sửa vào Internal Storage
//                saveToInternalStorage(bitmap_mod);
            try {
                ImageUri.saveImage(requireContext(), bitmap, "Edit");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    private class ImageCropping{
        private Uri resultUri;

        @Nullable
        private Bitmap bitmap;

        private void handleCrop() {

            String desFileName = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
            File newFile = new File(requireActivity().getCacheDir(),desFileName);
            Uri desUri=Uri.fromFile(newFile);

            ArrayList<Uri> listUri = new ArrayList<>();
            listUri.add(imageUri);
            listUri.add(desUri);
            cropImage.launch(listUri);
        }

        private final ActivityResultContract<List<Uri>, Uri> uCropContract = new ActivityResultContract<List<Uri>, Uri>() {
            @NonNull
            @Override
            public Intent createIntent(@NonNull Context context, @NonNull List<Uri> uri) {
                UCrop.Options options = new UCrop.Options();

                Uri imageUri = uri.get(0);
                Uri desUri = uri.get(1);
                options.setToolbarColor(requireActivity()
                        .getTheme()
                        .obtainStyledAttributes(new int[]{R.attr.ucrop_actionbar_color})
                        .getInt(0,0)
                );
                options.setStatusBarColor(requireActivity()
                        .getTheme()
                        .obtainStyledAttributes(new int[]{android.R.attr.colorPrimaryDark})
                        .getInt(0,0)
                );
                options.setToolbarWidgetColor(requireActivity()
                        .getTheme()
                        .obtainStyledAttributes(new int[]{android.R.attr.textColorPrimary})
                        .getInt(0,0)
                );
                options.setActiveControlsWidgetColor(requireActivity()
                        .getTheme()
                        .obtainStyledAttributes(new int[]{R.attr.highlightTextColor})
                        .getInt(0,0));

                options.setRootViewBackgroundColor(ContextCompat
                        .getColor(requireContext(), R.color.white)
                );
                options.setToolbarTitle("");
                options.setFreeStyleCropEnabled(true);
                UCrop uCrop = UCrop.of(imageUri, desUri)
                        .withOptions(options)
                        .withMaxResultSize(1080, 1080);
                return uCrop.getIntent(requireContext());
            }

            @Override
            public Uri parseResult(int i, @Nullable Intent intent) {
                if(intent == null){
                    return null;
                }
                return UCrop.getOutput(intent);
            }
        };

        private ActivityResultLauncher<List<Uri>> cropImage
                = registerForActivityResult(uCropContract,
                result -> {
                    resultUri = result;
                    try {
                        if(result != null){
                            bitmap = MediaStore.Images.Media.getBitmap(requireActivity()
                                    .getContentResolver(), result);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imageView.setImageBitmap(bitmap);
                });
    }

    class ImageFilterEffects{
        private Bitmap bitmap;

        {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void handleFilter() {

            currentState = CurrentState.FILTER;
            navigationView.getMenu().clear();
            navigationView.getLayoutParams().height = 0;
            filter_gallery.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            autoFilterButton.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            autoFilterButton.requestLayout();
            settingFilterButton.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            settingFilterButton.requestLayout();
            int length = ImageFilter.filter_values.length;
            Bitmap bmScaled = Bitmap.createScaledBitmap(bitmap, 80, 80, false);
            for (int i = 0; i < length; ++i) {
                final View v = getLayoutInflater().inflate(R.layout.filter_item, null);
                v.setId(i);
                TextView tv = v.findViewById(R.id.text_filter);
                tv.setText(com.example.album.ui.ImageFilter.filter_values[i]);
                ImageView img = v.findViewById(R.id.img_filter);
                img.setImageBitmap(com.example.album.ui.ImageFilter.applyFilter(bmScaled, ImageFilter.filter_values[i]));

                filter_gallery.addView(v);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bitmap = com.example.album.ui.ImageFilter.applyFilter(bitmap, ImageFilter.filter_values[v.getId()]);
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }
        }

        private void onBackPressed(){

            navigationView.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            navigationView.requestLayout();
            filter_gallery.getLayoutParams().height =0;
            filter_gallery.requestLayout();
            imageView.setImageURI(imageUri);
        }

        private void onDonePressed(){
            imageView.setImageBitmap(bitmap);
//                saveToInternalStorage(bitmap_mod);
            try {
                ImageUri.saveImage(requireContext(), bitmap, "Filter");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            navigationView.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            filter_gallery.getLayoutParams().height =0;
            filter_gallery.requestLayout();
            currentState = CurrentState.EDIT;
        }

        private void handleAutoFilter(View v) {
            int id = randomNum(0,ImageFilter.auto_filter_values.length-1);
            bitmap = ImageFilter.applyFilter(bitmap,ImageFilter.auto_filter_values[id],randomNum(-100,100));
            imageView.setImageBitmap(bitmap);
        }
        private int randomNum(int min,int max){
            return (int)Math.floor(Math.random()*(max-min+1)+min);
        }
        private void handleSettingFilter(View view) {
            final Dialog dialog = new Dialog(requireContext());
            dialog.setContentView(R.layout.setting_filter_dialog);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogPopupStyle;
            dialog.show();
            dialog.getWindow().setGravity(Gravity.BOTTOM);

            final Bitmap[] bmscaled = {bitmapResize(bitmap)};

            final SeekBar sk_filter_bright = dialog.findViewById(R.id.filter_bright);
            final SeekBar sk_filter_corner = dialog.findViewById(R.id.filter_corner);
            final SeekBar sk_filter_tint = dialog.findViewById(R.id.filter_tint);
            final SeekBar sk_filter_blur = dialog.findViewById(R.id.filter_blur);

            sk_filter_bright.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                Bitmap bmlocal;int stop;
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if(stop !=0 ){
                        bitmap =ImageFilter.applyFilter(bitmap,"BRIGHTNESS",stop);

                    }
                    bmscaled[0] = bitmapResize(bitmap);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                    bmlocal =ImageFilter.applyFilter(bmscaled[0],"BRIGHTNESS",progress);
                    imageView.setImageBitmap(bmlocal);
                    stop=progress;

                }
            });

            sk_filter_corner.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                Bitmap bmlocal;
                int stop;
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    bitmap =ImageFilter.applyFilter(bitmap,"CORNER",stop);
                    bmscaled[0] = bitmapResize(bitmap);

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
//                imageView.setImageBitmap(bmlocal);
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                    bmlocal =ImageFilter.applyFilter(bmscaled[0],"CORNER",progress*2);
                    imageView.setImageBitmap(bmlocal);
                    stop=progress*2;

                }
            });
            sk_filter_tint.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                Bitmap bmlocal;int stop;
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if(stop!=0){
                        bitmap = ImageFilter.applyFilter(bitmap,"TINT",stop);
                    }

                    bmscaled[0] = bitmapResize(bitmap);

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                    bmlocal =ImageFilter.applyFilter(bmscaled[0],"TINT",progress);
                    imageView.setImageBitmap(bmlocal);
                    stop=progress;


                }
            });

            RenderScript renderScript = RenderScript.create(requireContext());
            sk_filter_blur.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                Bitmap bmlocal; int stop;
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    bitmap = ImageFilter.blurImage(renderScript, bitmap,  stop);
                    bmscaled[0] = bitmapResize(bitmap);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    bmlocal = ImageFilter.blurImage(renderScript, bmscaled[0],  progress);
                    imageView.setImageBitmap(bmlocal);
                    stop =  progress;


                }
            });

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes(layoutParams);
            dialog.getWindow().setDimAmount(0f);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.custom_setting_filter_dialog);

        }

        private Bitmap bitmapResize(Bitmap bm){
            int maxHeight = 400;
            int maxWidth = 400;
            float scale = Math.min(((float)maxHeight / bm.getWidth()), ((float)maxWidth / bm.getHeight()));
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        }
    }

    class ImagePainting{
        private Bitmap bitmap;

        private void switchLayout(){
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.paint_nav);
            doneButton.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            doneButton.requestLayout();
            overflowButton.getLayoutParams().height = 0;
            overflowButton.requestLayout();
            navigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
            currentState = CurrentState.PAINT;
            navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    if(id == R.id.undo){

                    }
                    /*else if(id == R.id.redo){

                    }*/
                    else if(id == R.id.eraser){

                    }
                    else if(id == R.id.color){

                    }
                    else if(id == R.id.pen){

                    }else{
                        return false;
                    }
                    return true;
                }
            });
        }

        private void onDonePressed(){
            if(bitmap != null){
                imageView.setImageBitmap(bitmap);
//                saveToInternalStorage(bitmap_mod);
                try {
                    ImageUri.saveImage(requireContext(), bitmap, "Paint");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                currentState = CurrentState.EDIT;
            }
        }
    }
}
