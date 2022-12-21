package com.example.album.detail_image;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

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
import com.example.album.ui.DrawableImageView;
import com.example.album.ui.ImageFilter;
import com.example.album.ui.SplitToolbar;
import com.example.album.ui.ZoomableImageView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class EditImageFragment extends Fragment{
    private BottomNavigationView navigationView;
    private ImageButton navigateUpButton;
    private ImageButton doneButton;
    private ImageButton autoFilterButton;
    private ImageButton settingFilterButton;
    private ActionBar app_bar;
    private SplitToolbar navigationBar;
    private ZoomableImageView imageView;
    private ViewGroup filter_gallery;
    private Image image;
    private Bitmap bitmap;
    private RelativeLayout relativeLayout;

    EditImageFragment.ImageCropping imageCropping;
    EditImageFragment.ImageEditing imageEditing;
    EditImageFragment.ImagePainting imagePainting;
    EditImageFragment.ImageFilterEffects imageFilterEffects;

    CurrentState currentState = CurrentState.EDIT;

    enum CurrentState{
        EDIT,
        PAINT,
        FILTER
    }
    NavController navController;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        NavHostFragment navHostFragment = (NavHostFragment) requireActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment == null ? null : navHostFragment.getNavController();
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

        return inflater.inflate(R.layout.fragment_image_edit,container,false).getRootView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        app_bar.show();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//
        imageView = view.findViewById(R.id.photo_view);
        imageView.setImage(image);
        navigateUpButton = view.findViewById(R.id.back_button);
        doneButton = view.findViewById(R.id.done);
        autoFilterButton = view.findViewById(R.id.auto_filter);
        settingFilterButton= view.findViewById(R.id.setting_filter);
        navigationView = view.findViewById(R.id.bottom_nav);
        //filter
        filter_gallery = view.findViewById(R.id.filter_gallery);
        relativeLayout = view.findViewById(R.id.wrap_photo);

        imageCropping = new ImageCropping();
        imageEditing = new ImageEditing();
        imagePainting = new ImagePainting();
        imageFilterEffects = new ImageFilterEffects();
        navigationView.getMenu().getItem(0).setCheckable(false);
        navigationView.setOnItemSelectedListener(this::itemNavigationBottomSelected);
        navigateUpButton.setOnClickListener(this::handleBack);
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
//        NavDirections action = EditImageFragmentDirections
//                .actionEditImageFragmentToDetailImage();
//        navController.navigate(action);
    }

    private void handleBack(View v){
        switch (currentState){
            case EDIT:
                this.onBackPressed();
                break;
            case PAINT:
                imagePainting.onBackPressed();
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
                this.onBackPressed();
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

    private boolean itemNavigationBottomSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.crop){
            imageCropping.handleCrop();
//            if(image.getImageUri() != null){
//                imageView.setImage(image);
//            }
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
        else if(id == R.id.eraser){
            imagePainting.setErase();
        }
        else if(id == R.id.color){
            imagePainting.setColor();
        }
        else if(id == R.id.pen){
            imagePainting.setSize();
        }
        else {
            return false;
        }
        return true;
    }


    class ImageEditing{

        private void switchLayout(){
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.edit_nav);
            doneButton.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            doneButton.requestLayout();
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
            bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), mat, true);
            imageView.setImageBitmap(bitmap);
        }

        private void handleRotate() {
            Matrix mat = new Matrix();
            mat.postRotate(90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(),bitmap.getHeight(), mat, true);
            imageView.setImageBitmap(bitmap);
        }


        private void onDonePressed(){
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
        private Uri srcUri;

        private void handleCrop() {
            String srcFileName = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String srcPath = MediaStore.Images.Media.insertImage(requireActivity().getContentResolver(),
                    bitmap, srcFileName, null);
            srcUri = Uri.parse(srcPath);

            String desFileName = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
            File newDesFile = new File(requireActivity().getCacheDir(), desFileName);
            Uri desUri = Uri.fromFile(newDesFile);

            ArrayList<Uri> listUri = new ArrayList<>();
            listUri.add(srcUri);
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
                    try {
                        if(result != null){
                            bitmap = MediaStore.Images.Media.getBitmap(requireActivity()
                                    .getContentResolver(), result);
                        }
                        getActivity().getContentResolver().delete(srcUri, null, null);
                        imageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    class ImageFilterEffects  {
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
                        tempBitmap=com.example.album.ui.ImageFilter.applyFilter(bitmap, ImageFilter.filter_values[v.getId()]);
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
            currentState = CurrentState.EDIT;

        }

        private void onDonePressed(){
            imageView.setImageBitmap(tempBitmap);
            bitmap=tempBitmap;
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
                    bmlocal = ImageFilter.applyFilter(bmscaled[0],"BRIGHTNESS",progress);
                    imageView.setImageBitmap(bmlocal);
                    stop=progress;

                }
            });

            sk_filter_corner.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                Bitmap bmlocal;
                int stop;
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    bitmap = ImageFilter.applyFilter(bitmap,"CORNER",stop);
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
        View v;
        DrawableImageView imageViewPaint;
        Bitmap temp;

        private void switchLayout() {
            v=getLayoutInflater().inflate(R.layout.paint_photo_view,null);
            relativeLayout.removeView(relativeLayout);

            imageViewPaint = (DrawableImageView) v.findViewById(R.id.photo_view_paint) ;
            imageViewPaint.setImage(image);
            relativeLayout.addView(v);
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.paint_nav);
            doneButton.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            doneButton.requestLayout();
            navigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
            currentState = CurrentState.PAINT;


            Bitmap alteredBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            imageViewPaint.setNewImage(alteredBitmap, bitmap);
            temp=alteredBitmap;

        }
        private void setErase(){
            imageViewPaint.setErase(true);
            showDialogChooseSize();
        }
        private void setSize(){
            imageViewPaint.setErase(false);
            showDialogChooseSize();

        }
        public void showDialogChooseSize(){
            final Dialog dialog = new Dialog(requireContext());
            dialog.setContentView(R.layout.brush_size_dialog);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogPopupStyle;
            dialog.show();
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            final SeekBar sk_brush_size = dialog.findViewById(R.id.brush_size);


            sk_brush_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int stop;
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    imageViewPaint.setSize(stop/10);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
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
        public void setColor(){
            imageViewPaint.setErase(false);
            new ColorPickerDialog.Builder(requireContext())
                    .setTitle(getString(R.string.colorpicker_title))
                    .setPreferenceName("MyColorPickerDialog")
                    .setPositiveButton(getString(R.string.confirm),
                            new ColorEnvelopeListener() {
                                MenuItem color = navigationView.getMenu().findItem(R.id.color);
                                @Override
                                public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                    Drawable newIcon = (Drawable)color.getIcon();
                                    newIcon.mutate().setColorFilter(envelope.getColor(), PorterDuff.Mode.SRC_IN);
                                    color.setIcon(newIcon);
                                    imageViewPaint.setColor(envelope.getColor());
                                }
                            })
                    .setNegativeButton(getString(R.string.label_cancel),
                            (dialogInterface, i) -> dialogInterface.dismiss())
                    .attachAlphaSlideBar(true) // the default value is true.
                    .attachBrightnessSlideBar(true)  // the default value is true.
                    .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
                    .show();
        }

        private void onBackPressed(){
            imageView.setImage(image);
            relativeLayout.removeView(v);
            currentState = CurrentState.EDIT;

        }
        private void onDonePressed(){
            Bitmap saved = Bitmap.createBitmap(relativeLayout.getWidth(),
                    relativeLayout.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(saved);
            relativeLayout.draw(canvas);


            if(saved!=null){
                bitmap=saved;
                imageView.setImageBitmap(saved);
                currentState = CurrentState.EDIT;
            }
            relativeLayout.removeView(v);

        }
    }
}