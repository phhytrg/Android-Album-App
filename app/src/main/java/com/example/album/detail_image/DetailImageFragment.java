package com.example.album.detail_image;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.example.album.ImageUri;
import com.example.album.MainActivity;
import com.example.album.R;
import com.example.album.data.Image;
import com.example.album.ui.ImageFilter;
import com.example.album.ui.SplitToolbar;
import com.example.album.ui.ZoomableImageView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DetailImageFragment extends Fragment {
    private BottomNavigationView navigationView;
    private PopupMenu popup;
    private ImageButton navigateUpButton;
    private ImageButton overflowButton;
    private ImageButton doneButton;
    private ImageButton favoriteButton;
    private ImageButton autoFilterButton;
    private ImageButton settingFilterButton;
    private ActionBar app_bar;
    private SplitToolbar navigationBar;
    private ZoomableImageView imageView;
    private int isChecked;
    //filter
    private ViewGroup filter_gallery;
    private Image image;
    private Bitmap bitmap;

    DetailImageFragment.ImageCropping imageCropping;
    DetailImageFragment.ImageEditing imageEditing;
    DetailImageFragment.ImagePainting imagePainting;
    DetailImageFragment.ImageFilterEffects imageFilterEffects;

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
            image = getArguments().getParcelable("image");
            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(),
                        image.getImageUri());
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        imageView.setImage(image);
        navigateUpButton = view.findViewById(R.id.back_button);
        overflowButton = view.findViewById(R.id.overflow_button);
        doneButton = view.findViewById(R.id.done);
        favoriteButton = view.findViewById(R.id.favorite);
        autoFilterButton = view.findViewById(R.id.auto_filter);
        settingFilterButton= view.findViewById(R.id.setting_filter);
        navigationView = view.findViewById(R.id.bottom_nav);
        //filter
        filter_gallery = view.findViewById(R.id.filter_gallery);

        imageCropping = new ImageCropping();
        imageEditing = new ImageEditing();
        imagePainting = new ImagePainting();
        imageFilterEffects = new ImageFilterEffects();

        isChecked = 0;
        navigationView.getMenu().getItem(0).setCheckable(false);
        navigationView.setOnItemSelectedListener(this::itemNavigationBottomSelected);
        navigateUpButton.setOnClickListener(this::handleBack);
        overflowButton.setOnClickListener(this::showPopup);
        favoriteButton.setOnClickListener(v -> handleFavorite());
        doneButton.setOnClickListener(this::handleDone);
        autoFilterButton.setOnClickListener(v-> imageFilterEffects.handleAutoFilter(v));
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
        try {
            bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), image.getImageUri());
        } catch (IOException e) {
            e.printStackTrace();
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
                imageFilterEffects.onDonePressed();
                imageEditing.switchLayout();
                break;
        }
    }

    public boolean onPopUpMenuClick(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.details){
            handleDetails();
        }
        else if(id == R.id.add){
            Toast.makeText(requireContext(), "add", Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.set){
            handleSet();
        }
        else if(id == R.id.rename){
            handleRename();
        }
        else {
            return false;
        }
        return true;
    }

    private void handleDetails() {
        Context context = requireContext();
        LayoutInflater factory = LayoutInflater.from(context);
        final View detailsDialogView = factory.inflate(R.layout.details_dialog, null);
        final AlertDialog detailsDialog = new AlertDialog.Builder(context).create();

        TextView image_name = (TextView) detailsDialogView.findViewById(R.id.image_name);
        TextView last_modified_date = (TextView) detailsDialogView.findViewById(R.id.last_modified_date);
        TextView sizeText = (TextView) detailsDialogView.findViewById(R.id.size);
        EditText description = (EditText) detailsDialogView.findViewById(R.id.description);

        image_name.setText(image.getName());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss, dd-MM-yyyy");
        String date = image.getDate().format(formatter);
        last_modified_date.setText(date);

        Double size = image.getSize().doubleValue();
        String sizeUnit = "B";
        int scale;

        if (size / 1024 > 1) {
            size = size / 1024;
            sizeUnit = "KB";

            if (size / 1024 > 1) {
                size = size / 1024;
                sizeUnit = "MB";
            }
            scale = (int) Math.pow(10, 1);
            size = (double) Math.round(size * scale) / scale;
        }
        sizeText.setText(size + " " + sizeUnit);

        description.setInputType(InputType.TYPE_CLASS_TEXT);
        description.setSingleLine(false);
        description.setText(image.getDescription());

        detailsDialog.setView(detailsDialogView);
        detailsDialogView.findViewById(R.id.cancel_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsDialog.dismiss();
            }
        });
        detailsDialogView.findViewById(R.id.save_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image.setDescription(description.getText().toString());
                detailsDialog.dismiss();
            }
        });

        detailsDialog.show();
    }

    public void handleRename() {
        //Context context = requireContext();
//        final Dialog renameDialog = new Dialog(context);
//        renameDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        renameDialog.setCancelable(true);
//        renameDialog.setContentView(R.layout.rename_dialog);

        //renameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Context context = requireContext();
        LayoutInflater factory = LayoutInflater.from(context);
        final View renameDialogView = factory.inflate(R.layout.rename_dialog, null);
        final AlertDialog renameDialog = new AlertDialog.Builder(context).create();

        EditText renameEditText = (EditText) renameDialogView.findViewById(R.id.rename);
        renameEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        renameEditText.setSingleLine(false);
        renameEditText.setText(image.getName());

        renameDialog.setView(renameDialogView);
        renameDialogView.findViewById(R.id.cancel_rename).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renameDialog.dismiss();
            }
        });
        renameDialogView.findViewById(R.id.save_rename).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = renameEditText.getText().toString();
                if (newName.equals("")) {
                    Toast t = Toast.makeText(context, getString(R.string.alert_rename), Toast.LENGTH_SHORT);
                }
                else {
                    image.setName(renameEditText.getText().toString());
                    renameDialog.dismiss();
                }
            }
        });

        renameDialog.show();
    }


    private void handleSet() {

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
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), image.getImageUri());
            WallpaperManager.getInstance(requireContext()).setBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setWallLock(){
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), image.getImageUri());
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
        else if(id == R.id.crop){
            imageCropping.handleCrop();
            if(image.getImageUri() != null){
                imageView.setImage(image);
            }
        }
        else if(id == R.id.flip){
            imageEditing.handleFlip();
        }
        else if(id == R.id.rotate){
            imageEditing.handleRotate();
        }
        else if(id == R.id.filter){
            imageFilterEffects.handleFilter();
        }
        else if(id == R.id.paint){
            imagePainting.switchLayout();
        }
        else if(id == R.id.undo){

        }
//        else if(id == R.id.redo){
//
//        }
        else if(id == R.id.eraser){

        }
        else if(id == R.id.color){

        }
        else if(id == R.id.pen){

        }
        else {
            return false;
        }
        return true;
    }

    private void handleShare() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        share.putExtra(Intent.EXTRA_STREAM, image.getImageUri());
        startActivity(Intent.createChooser(share, getString(R.string.select)));
    }

    class ImageEditing{

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
            bitmap = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(),bitmap.getHeight(), mat, true);
            imageView.setImageBitmap(bitmap);
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
            imageView.setImage(image);
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
        private Uri resultUri = image.getImageUri();

        private void handleCrop() {

            String desFileName = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
            File newFile = new File(requireActivity().getCacheDir(),desFileName);
            Uri desUri=Uri.fromFile(newFile);

            ArrayList<Uri> listUri = new ArrayList<>();
            listUri.add(image.getImageUri());
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
                            imageView.setImageBitmap(bitmap);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    class ImageFilterEffects{
        private Bitmap tempBitmap = bitmap;

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
                        tempBitmap = ImageFilter.applyFilter(bitmap, ImageFilter.filter_values[v.getId()]);
                        imageView.setImageBitmap(tempBitmap);
                    }
                });
            }
        }

        private void onBackPressed(){

            navigationView.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            navigationView.requestLayout();
            filter_gallery.getLayoutParams().height =0;
            filter_gallery.requestLayout();
            imageView.setImage(image);
        }

        private void onDonePressed(){
            imageView.setImageBitmap(bitmap);
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

        private void switchLayout(){
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.paint_nav);
            doneButton.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            doneButton.requestLayout();
            overflowButton.getLayoutParams().height = 0;
            overflowButton.requestLayout();
            navigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
            currentState = CurrentState.PAINT;
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
//
//
//public class DetailImageFragment extends Fragment{
//    BottomNavigationView navigationView;
//    PopupMenu popup;
//    ImageButton navigateUpButton;
//    ImageButton overflowButton;
//    ImageButton doneButton;
//    ImageButton favoriteButton;
//    ImageButton autoFilterButton;
//    ImageButton settingFilterButton;
//
//    ActionBar app_bar;
//    SplitToolbar navigationBar;
//    ZoomableImageView imageView;
//    int isChecked;
//    int resourceId;
//    String[] details;
////    Bitmap image_bm_mod, image_bm_orig;
//    Bitmap bitmap, bitmap_mod;
//    //filter
//    ViewGroup filter_gallery;
//    Uri imageUri;
//
//    CurrentState currentState = CurrentState.DETAIL;
//
//    enum CurrentState{
//        DETAIL,
//        EDIT,
//        PAINT,
//        FILTER
//    }
//
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        if(getArguments() != null){
//            imageUri = getArguments().getParcelable("image");
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
//                bitmap_mod = bitmap;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        app_bar = ((MainActivity)requireActivity()).getSupportActionBar();
//        if (app_bar != null) {
//            app_bar.hide();
//        }
//        Window window = requireActivity().getWindow();
//        if(window != null) {
//            window.setStatusBarColor(getResources()
//                    .getColor(R.color.dark_grey, requireActivity().getTheme()));
//            window.getDecorView().setSystemUiVisibility(0);
//        }
//
//        navigationBar = requireActivity().findViewById(R.id.navigation_bar);
//        navigationBar.setVisibility(View.GONE);
//
//        return inflater.inflate(R.layout.fragment_image_detail,container,false).getRootView();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        app_bar.show();
//        navigationBar.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        navigateUpButton = view.findViewById(R.id.back_button);
//        overflowButton = view.findViewById(R.id.overflow_button);
//        doneButton = view.findViewById(R.id.done);
//        favoriteButton = view.findViewById(R.id.favorite);
//        autoFilterButton =view.findViewById(R.id.auto_filter);
//        settingFilterButton=view.findViewById(R.id.setting_filter);
//        imageView = view.findViewById(R.id.photo_view);
//        imageView.setImageBitmap(bitmap);
////        imageView.setImageResource(resourceId);
//        navigationView = view.findViewById(R.id.bottom_nav);
//        //filter
//        filter_gallery = (ViewGroup) view.findViewById(R.id.filter_gallery);
//        Bitmap image_bm0 = BitmapFactory.decodeResource(this.getResources(),
//                resourceId);
////        String pathSaved = saveToInternalStorage(image_bm0);
////        loadImageFromStorage(pathSaved);
//
//        isChecked=0;
//
//        navigationView.getMenu().getItem(0).setCheckable(false);
//        navigationView.setOnItemSelectedListener(this::itemNavigationBottomSelected);
//        navigateUpButton.setOnClickListener(this::handleBack);
//        overflowButton.setOnClickListener(this::showPopup);
//        favoriteButton.setOnClickListener(v -> handleFavorite());
//        doneButton.setOnClickListener(this::handleDone);
//        autoFilterButton.setOnClickListener(this::handleAutoFilter);
//        settingFilterButton.setOnClickListener(this::handleSettingFilter);
//        // details chứa name, date, location, size, description
//        // theo thứ tự index 0 -> 4
//        // name, date, location, size, description phải được lấy từ database
//        // demo chưa có database nên chỉ gán cứng chạy thử
//        details = new String[5];
//        details[0] = "Image";
//        details[1] = "29/10/2022";
//        details[2] = "Ho Chi Minh";
//        details[3] = "5KB";
//        details[4] = "Flexing at Circle K with my bros";
//        //Drawable drawable = img.getDrawable();
//
//        //Android default back button now acts as "Action bar" Back button
//        OnBackPressedCallback callback = new OnBackPressedCallback(true ) {
//            @Override
//            public void handleOnBackPressed() {
//                handleBack(view);
//            }
//        };
//        requireActivity().getOnBackPressedDispatcher()
//                .addCallback(getViewLifecycleOwner(), callback);
//
//    }
//
//
//
//    public void showPopup(View v) {
//        popup = new PopupMenu(requireContext(), v);
//
//        // This activity implements OnMenuItemClickListener
//        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                return onPopUpMenuClick(menuItem);
//            }
//        });
//        popup.inflate(R.menu.image_menu);
//        popup.show();
//    }
//    public boolean onPopUpMenuClick(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.details:
//                handleDetails();
//                break;
//            case R.id.add:
//                Toast.makeText(requireContext(), "add", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.set:
//                handleSet();
//                break;
//            case R.id.rename:
//                handleRename();
//                break;
//            default:
//                return false;
//        }
//        return true;
//    }
//
////    public void handleRename() {
////        final AlertDialog.Builder renameDialog = new AlertDialog.Builder(requireContext());
////        renameDialog.setTitle("Rename to:");
////        final EditText input = new EditText(requireContext());
////        input.setText(details[0]);
////        input.setInputType(InputType.TYPE_CLASS_TEXT);
////        renameDialog.setView(input);
////        renameDialog.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
////            @Override
////            public void onClick(DialogInterface dialog, int which) {
////                details[0] = input.getText().toString();
////            }
////        });
////        renameDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
////            @Override
////            public void onClick(DialogInterface dialog, int which) {
////                dialog.cancel();
////            }
////        });
////        renameDialog.show();
////    }
//
//
//    public boolean itemNavigationBottomSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        if(id == R.id.edit){
//            handleEdit();
//        }
//        else if(id == R.id.delete) {
//            Toast.makeText(requireContext(), "delete", Toast.LENGTH_SHORT).show();
//        }
//        else if(id == R.id.share){
//            handleShare();
//        }
//        else if(id == R.id.crop){
//            handleCrop();
//        }
//        else if(id == R.id.rotate){
//            handleRotate();
//        }
//        else if(id == R.id.flip){
//            handleFlip();
//        }
//        else if(id == R.id.paint){
//            handleBrush();
//        }
//        else if(id == R.id.undo){
//
//        }
//        else if(id == R.id.redo){
//
//        }
//        else if(id == R.id.eraser){
//
//        }
//        else if(id == R.id.color){
//            handleColor();
//        }
//        else if(id == R.id.filter){
//            handleFilter();
//        }
//        else {
//            return false;
//        }
//        return true;
//    }
//
//    public void handleRename() {
//        final AlertDialog.Builder renameDialog = new AlertDialog.Builder(requireContext());
//        renameDialog.setTitle("Rename to:");
//        final EditText input = new EditText(requireContext());
//        input.setText(details[0]);
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
//        renameDialog.setView(input);
//        renameDialog.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                details[0] = input.getText().toString();
//            }
//        });
//        renameDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        renameDialog.show();
//    }
//
//
//    private void handleSet() {
//
////        MaterialAlertDialogBuilder builder =
////                new MaterialAlertDialogBuilder(
////                        requireContext(), R.style.AlertDialogTheme);
////        View view = getLayoutInflater().inflate(R.layout.custom_dialog_set, null);
////        builder.setView(view);
//
//        final Dialog dialog = new Dialog(requireContext());
//        dialog.setContentView(R.layout.custom_dialog_set);
//        ImageButton homeOption = dialog.findViewById(R.id.home_option);
//        ImageButton lockOption = dialog.findViewById(R.id.lock_option);
//        ImageButton homeNLock = dialog.findViewById(R.id.home_lock_option);
//        AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
//        homeOption.setOnClickListener(view -> {
//            view.startAnimation(buttonClick);
//            setWallHome();
//            dialog.dismiss();
//        });
//        lockOption.setOnClickListener(view -> {
//            view.startAnimation(buttonClick);
//            setWallLock();
//            dialog.dismiss();
//        });
//        homeNLock.setOnClickListener(view -> {
//            view.startAnimation(buttonClick);
//            setWallLock();
//            setWallLock();
//            dialog.dismiss();
//        });
//
//        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogPopupStyle;
//        dialog.show();
//        dialog.getWindow().setGravity(Gravity.BOTTOM);
//        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//        layoutParams.copyFrom(dialog.getWindow().getAttributes());
//        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//        dialog.getWindow().setAttributes(layoutParams);
//        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
//    }
//
//    public void setWallHome(){
//        try {
//            WallpaperManager.getInstance(requireContext()).setBitmap(bitmap);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void setWallLock(){
//        try {
//            WallpaperManager.getInstance(requireContext()).setBitmap(bitmap,null,true,WallpaperManager.FLAG_LOCK);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    ///////////////////////////////////////////////////////////////////////////////
//    private void handleBack(View v){
//        switch (currentState){
//            case DETAIL:
//                // dang o layout_detail
//                Window window = getActivity().getWindow();
//                TypedValue outValue = new TypedValue();
//                getActivity().getTheme()
//                        .resolveAttribute(androidx.appcompat.R.attr.colorPrimaryDark,outValue,true);
//                window.setStatusBarColor(outValue.data);
//
//                window.getDecorView().setSystemUiVisibility(0);
//
//                NavHostFragment hostFragment = (NavHostFragment) getActivity()
//                        .getSupportFragmentManager()
//                        .findFragmentById(R.id.nav_host_fragment);
//                NavController navController = hostFragment != null ? hostFragment.getNavController() : null;
//
//                if(navController!= null){
//                    navController.navigateUp();
//                }
//                break;
//            case EDIT:
//                //dang o edit
//                navigationView.getMenu().clear();
//                navigationView.inflateMenu(R.menu.image_option);
//                navigationView.getMenu().getItem(0).setCheckable(false);
//                overflowButton.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
//                overflowButton.requestLayout();
//                favoriteButton.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
//                favoriteButton.requestLayout();
//                doneButton.getLayoutParams().height = 0;
//                doneButton.requestLayout();
//                currentState = CurrentState.DETAIL;
//
//                // Trả lại ảnh cũ khi chưa chỉnh
//                bitmap_mod = bitmap;
//                imageView.setImageBitmap(bitmap);
//                //saveToInternalStorage(image_bm_mod);
//                break;
//            case PAINT:
//                //dang o paint
//                handleEdit();
//                break;
//            case FILTER:
//                //dang o filter
//                navigationView.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
////                navigationView.requestLayout();
//                filter_gallery.getLayoutParams().height =0;
//                filter_gallery.requestLayout();
//                bitmap_mod=bitmap;
//                imageView.setImageBitmap(bitmap);
//
//                handleEdit();
//                break;
//        }
//    }
//
//    public void handleDone(View v){
//        switch (currentState){
//            case EDIT:
//                //dang o edit
//                navigationView.getMenu().clear();
//                navigationView.inflateMenu(R.menu.image_option);
//                navigationView.getMenu().getItem(0).setCheckable(false);
//                overflowButton.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
//                overflowButton.requestLayout();
//                favoriteButton.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
//                favoriteButton.requestLayout();
//                doneButton.getLayoutParams().height = 0;
//                doneButton.requestLayout();
//                currentState = CurrentState.DETAIL;
//
//                // Lưu ảnh đã chỉnh sửa vào Internal Storage
//                bitmap = bitmap_mod;
//                imageView.setImageBitmap(bitmap_mod);
////                saveToInternalStorage(bitmap_mod);
//                try {
//                    ImageUri.saveImage(requireContext(), bitmap_mod, "Edit");
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                break;
//            case PAINT:
//                bitmap = bitmap_mod;
//                imageView.setImageBitmap(bitmap_mod);
////                saveToInternalStorage(bitmap_mod);
//                try {
//                    ImageUri.saveImage(requireContext(), bitmap_mod, "Paint");
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                currentState = CurrentState.EDIT;
//                break;
//            case FILTER:
//                bitmap = bitmap_mod;
//                imageView.setImageBitmap(bitmap_mod);
////                saveToInternalStorage(bitmap_mod);
//                try {
//                    ImageUri.saveImage(requireContext(), bitmap_mod, "Filter");
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                navigationView.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
//                filter_gallery.getLayoutParams().height =0;
//                filter_gallery.requestLayout();
//                currentState = CurrentState.EDIT;
//                handleEdit();
//
//                break;
//        }
//    }
//
//
//    private void handleDetails() {
//        final AlertDialog.Builder detailsDialog = new AlertDialog.Builder(requireContext());
//        detailsDialog.setTitle("Details");
//        final EditText input = new EditText(requireContext());
//        String info = details[0] + "\n" + details[1] + "\n" + details[2] + "\n" + details[3];
//        detailsDialog.setMessage(info);
//        input.setHint("Enter the description of this image");
//        input.setText(details[4]);
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
//        input.setSingleLine(false);
//        detailsDialog.setView(input);
//        detailsDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                details[4] = input.getText().toString();
//            }
//        });
//        detailsDialog.setNegativeButton("Back", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        detailsDialog.show();
//    }
//
//    public void handleFavorite() {
//        if(isChecked==0){
////            navigationView.getMenu().findItem(R.id.favorite).setIcon(R.drawable.ic_favorite_colored);
//            favoriteButton.setImageResource(R.drawable.ic_favorite_colored);
//            isChecked=1;
//        }else {
////            navigationView.getMenu().findItem(R.id.favorite).setIcon(R.drawable.ic_favorite);
//            favoriteButton.setImageResource(R.drawable.ic_favorite);
//            isChecked=0;
//        }
//    }
//
//    public void handleShare() {
//        Intent share = new Intent(Intent.ACTION_SEND);
//        share.setType("image/jpeg");
//        if(getActivity() == null)
//            return;
//        String path = MediaStore.Images.Media.insertImage(getActivity().
//                getContentResolver(), bitmap_mod,null, null);
//        Uri imageUri =  Uri.parse(path);
//        share.putExtra(Intent.EXTRA_STREAM, imageUri);
//        startActivity(Intent.createChooser(share, "Select"));
//    }
//
//    public void handleEdit(){
//        navigationView.getMenu().clear();
//
//        navigationView.inflateMenu(R.menu.edit_nav);
//        doneButton.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
//        doneButton.requestLayout();
//        favoriteButton.getLayoutParams().height = 0;
//        favoriteButton.requestLayout();
//        overflowButton.getLayoutParams().height = 0;
//        overflowButton.requestLayout();
//        autoFilterButton.getLayoutParams().height = 0;
//        autoFilterButton.requestLayout();
//        settingFilterButton.getLayoutParams().height = 0;
//        settingFilterButton.requestLayout();
//        navigationView.setVisibility(View.VISIBLE);
//        navigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
//        currentState = CurrentState.EDIT;
//    }
//    //filter
//    private Bitmap bitmapResize(Bitmap bm){
//        int maxHeight = 400;
//        int maxWidth = 400;
//        float scale = Math.min(((float)maxHeight / bm.getWidth()), ((float)maxWidth / bm.getHeight()));
//        Matrix matrix = new Matrix();
//        matrix.postScale(scale, scale);
//        return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
//    }
//    private void handleSettingFilter(View view) {
//        final Dialog dialog = new Dialog(requireContext());
//        dialog.setContentView(R.layout.setting_filter_dialog);
//        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogPopupStyle;
//        dialog.show();
//        dialog.getWindow().setGravity(Gravity.BOTTOM);
//
//        final Bitmap[] bmscaled = {bitmapResize(bitmap_mod)};
//
//        final SeekBar sk_filter_bright = dialog.findViewById(R.id.filter_bright);
//        final SeekBar sk_filter_corner = dialog.findViewById(R.id.filter_corner);
//        final SeekBar sk_filter_tint = dialog.findViewById(R.id.filter_tint);
//
//        sk_filter_bright.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            Bitmap bmlocal;int stop;
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                if(stop !=0 ){
//                    bitmap_mod =ImageFilter.applyFilter(bitmap_mod,"BRIGHTNESS",stop);
//
//                }
//                bmscaled[0] = bitmapResize(bitmap_mod);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
//                bmlocal =ImageFilter.applyFilter(bmscaled[0],"BRIGHTNESS",progress);
//                imageView.setImageBitmap(bmlocal);
//                stop=progress;
//
//            }
//        });
//
//        sk_filter_corner.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            Bitmap bmlocal;int stop;
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                bitmap_mod =ImageFilter.applyFilter(bitmap_mod,"CORNER",stop);
//                bmscaled[0] = bitmapResize(bitmap_mod);
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
////                imageView.setImageBitmap(bmlocal);
//            }
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
//                bmlocal =ImageFilter.applyFilter(bmscaled[0],"CORNER",progress*2);
//                imageView.setImageBitmap(bmlocal);
//                stop=progress*2;
//
//            }
//        });
//        sk_filter_tint.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            Bitmap bmlocal;int stop;
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                if(stop!=0){
//                    bitmap_mod =ImageFilter.applyFilter(bitmap_mod,"TINT",stop);
//                }
//
//                bmscaled[0] = bitmapResize(bitmap_mod);
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
//                bmlocal =ImageFilter.applyFilter(bmscaled[0],"TINT",progress);
//                imageView.setImageBitmap(bmlocal);
//                stop=progress;
//
//            }
//        });
//
//        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//        layoutParams.copyFrom(dialog.getWindow().getAttributes());
//        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//        dialog.getWindow().setAttributes(layoutParams);
//        dialog.getWindow().setDimAmount(0f);
//        dialog.getWindow().setBackgroundDrawableResource(R.drawable.custom_setting_filter_dialog);
//
//    }
//    private void handleAutoFilter(View v) {
//        int id = randomNum(0,ImageFilter.auto_filter_values.length-1);
//        bitmap_mod =ImageFilter.applyFilter(bitmap,ImageFilter.auto_filter_values[id],randomNum(-100,100));
//        imageView.setImageBitmap(bitmap_mod);
//    }
//    private int randomNum(int min,int max){
//        return (int)Math.floor(Math.random()*(max-min+1)+min);
//    }
//
//    private void handleFilter() {
//        currentState = CurrentState.FILTER;
//        navigationView.getMenu().clear();
//        navigationView.getLayoutParams().height = 0;
//        filter_gallery.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
//        autoFilterButton.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
//        autoFilterButton.requestLayout();
//        settingFilterButton.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
//        settingFilterButton.requestLayout();
//
//        int length = ImageFilter.filter_values.length;
//        Bitmap bmScaled = Bitmap.createScaledBitmap(bitmap_mod, 80, 80, false);
//
//        for(int i=0;i< length;++i){
//
//            final View v = getLayoutInflater().inflate(R.layout.filter_item,null);
//            v.setId(i);
//            TextView tv = (TextView) v.findViewById(R.id.text_filter);
//            tv.setText(ImageFilter.filter_values[i]);
//            ImageView img = (ImageView) v.findViewById(R.id.img_filter);
//            img.setImageBitmap(ImageFilter.applyFilter(bmScaled,ImageFilter.filter_values[i]));
//
//            filter_gallery.addView(v);
//            v.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    bitmap_mod =ImageFilter.applyFilter(bitmap,ImageFilter.filter_values[v.getId()]);
//                    imageView.setImageBitmap(bitmap_mod);
//                }});
//        }
//    }
//
//    //
//    public void handleBrush(){
//        navigationView.getMenu().clear();
//        navigationView.inflateMenu(R.menu.paint_nav);
//        doneButton.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
//        doneButton.requestLayout();
//        overflowButton.getLayoutParams().height = 0;
//        overflowButton.requestLayout();
//        navigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
//        currentState = CurrentState.PAINT;
//    }
//
//    public void handleColor(){
//        new ColorPickerDialog.Builder(requireContext())
//                .setTitle("ColorPicker Dialog")
//                .setPreferenceName("MyColorPickerDialog")
//                .setPositiveButton("Confirm",
//                        new ColorEnvelopeListener() {
//                            MenuItem color = navigationView.getMenu().findItem(R.id.color);
//                            @Override
//                            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
//                                color = navigationView.getMenu().findItem(R.id.color);
//                                Drawable wrapDrawable = DrawableCompat.wrap(
//                                        ContextCompat.getDrawable(requireContext(),
//                                        R.drawable.ic_circle)
//                                );
//                                DrawableCompat.setTint(wrapDrawable, envelope.getColor());
//                                color.setIcon(wrapDrawable);
//                            }
//                        })
//                .setNegativeButton("cancel",
//                        (dialogInterface, i) -> dialogInterface.dismiss())
//                .attachAlphaSlideBar(true) // the default value is true.
//                .attachBrightnessSlideBar(true)  // the default value is true.
//                .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
//                .show();
//    }
//
//
//    public void handleRotate() {
//        Matrix mat = new Matrix();
//        mat.postRotate(90);
//        Bitmap image_bm_rotated = Bitmap.createBitmap(bitmap_mod, 0, 0,bitmap_mod.getWidth(),bitmap_mod.getHeight(), mat, true);
//        bitmap_mod = image_bm_rotated;
//        imageView.setImageBitmap(bitmap_mod);
//    }
//
//    public void handleFlip() {
//        Matrix mat = new Matrix();
//        mat.postScale(-1f, 1f, bitmap_mod.getWidth() / 2f, bitmap_mod.getHeight() / 2f);
//        Bitmap image_bm_flipped = Bitmap.createBitmap(bitmap_mod, 0, 0, bitmap_mod.getWidth(), bitmap_mod.getHeight(), mat, true);
//        bitmap_mod = image_bm_flipped;
//        imageView.setImageBitmap(bitmap_mod);
//    }
//
//    public void handleCrop() {
//
//        String desFileName = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
//        File newFile = new File(requireActivity().getCacheDir(),desFileName);
//        Uri desUri=Uri.fromFile(newFile);
//
//        ArrayList<Uri> listUri = new ArrayList<>();
//        listUri.add(imageUri);
//        listUri.add(desUri);
//        cropImage.launch(listUri);
//
//    }
//
//    private final ActivityResultContract<List<Uri>, Uri> uCropContract = new ActivityResultContract<List<Uri>, Uri>() {
//        @NonNull
//        @Override
//        public Intent createIntent(@NonNull Context context, List<Uri> uri) {
//            UCrop.Options options = new UCrop.Options();
//
//            Uri imageUri = uri.get(0);
//            Uri desUri = uri.get(1);
//            options.setToolbarColor(requireActivity()
//                    .getTheme()
//                    .obtainStyledAttributes(new int[]{R.attr.ucrop_actionbar_color})
//                    .getInt(0,0)
//            );
//            options.setStatusBarColor(requireActivity()
//                    .getTheme()
//                    .obtainStyledAttributes(new int[]{android.R.attr.colorPrimaryDark})
//                    .getInt(0,0)
//            );
//            options.setToolbarWidgetColor(requireActivity()
//                    .getTheme()
//                    .obtainStyledAttributes(new int[]{android.R.attr.textColorPrimary})
//                    .getInt(0,0)
//            );
//            options.setActiveControlsWidgetColor(requireActivity()
//                    .getTheme()
//                    .obtainStyledAttributes(new int[]{R.attr.highlightTextColor})
//                    .getInt(0,0));
//
//            options.setRootViewBackgroundColor(ContextCompat
//                    .getColor(requireContext(), R.color.white)
//            );
//            options.setToolbarTitle("");
//            options.setFreeStyleCropEnabled(true);
//            UCrop uCrop = UCrop.of(imageUri, desUri)
//                    .withOptions(options)
//                    .withMaxResultSize(1080, 1080);
//            return uCrop.getIntent(requireContext());
//        }
//
//        @Override
//        public Uri parseResult(int i, @Nullable Intent intent) {
//            if(intent == null){
//                return null;
//            }
//            return UCrop.getOutput(intent);
//        }
//    };
//
//    private ActivityResultLauncher<List<Uri>> cropImage
//            = registerForActivityResult(uCropContract,
//            result -> {
////                imageView.setImageURI(result);
//                try {
//                    if(result != null){
//                        bitmap_mod = MediaStore.Images.Media.getBitmap(requireActivity()
//                                .getContentResolver(), result);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                imageView.setImageBitmap(bitmap_mod);
//            });
//
//}