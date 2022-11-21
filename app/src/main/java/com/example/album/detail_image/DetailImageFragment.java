package com.example.album.detail_image;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
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
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.album.MainActivity;
import com.example.album.R;
import com.example.album.SplitToolbar;
import com.example.album.ZoomableImageView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class DetailImageFragment extends Fragment {
    BottomNavigationView navigationView;
    PopupMenu popup;
    ImageButton navigateUpButton;
    ImageButton overflowButton;
    ImageButton doneButton;
    ImageButton favoriteButton;
    ActionBar app_bar;
    SplitToolbar navigationBar;
    ZoomableImageView imageView;
    int isChecked;
    int resourceId;
    String[] details;
    Bitmap image_bm_mod, image_bm_orig;

    CurrentState currentState = CurrentState.DETAIL;

    enum CurrentState{
        DETAIL,
        EDIT,
        PAINT
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(getArguments()!=null){
            resourceId = getArguments().getInt("image");
        }
        app_bar = ((MainActivity)getActivity()).getSupportActionBar();
        if (app_bar != null) {
            app_bar.hide();
        }
        Window window = getActivity().getWindow();
        if(window != null) {
            window.setStatusBarColor(getResources()
                    .getColor(R.color.dark_grey, getActivity().getTheme()));
            window.getDecorView().setSystemUiVisibility(0);
        }

        navigationBar = getActivity().findViewById(R.id.navigation_bar);
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
        navigateUpButton = view.findViewById(R.id.back_button);
        overflowButton = view.findViewById(R.id.overflow_button);
        doneButton = view.findViewById(R.id.done);
        favoriteButton = view.findViewById(R.id.favorite);
        imageView = view.findViewById(R.id.photo_view);
        imageView.setImageResource(resourceId);
        navigationView = view.findViewById(R.id.bottom_nav);

        Bitmap image_bm0 = BitmapFactory.decodeResource(this.getResources(),
                resourceId);
        String pathSaved = saveToInternalStorage(image_bm0);
        loadImageFromStorage(pathSaved);

        isChecked=0;

        navigationView.getMenu().getItem(0).setCheckable(false);
        navigationView.setOnItemSelectedListener(this::itemNavigationBottomSelected);
        navigateUpButton.setOnClickListener(this::handleBack);
        overflowButton.setOnClickListener(this::showPopup);
        favoriteButton.setOnClickListener(v -> handleFavorite());
        doneButton.setOnClickListener(this::handleDone);

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
    public boolean onPopUpMenuClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.details:
                handleDetails();
//                Toast.makeText(requireContext(), "details", Toast.LENGTH_SHORT).show();
                break;
            case R.id.add:
                Toast.makeText(requireContext(), "add", Toast.LENGTH_SHORT).show();
                break;
            case R.id.set:
                handleSet();
//                Toast.makeText(requireContext(), "set", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rename:
                handleRename();
                Toast.makeText(requireContext(), "rename", Toast.LENGTH_SHORT).show();
                break;
            default:
                return false;
        }
        return true;
    }

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


    public boolean itemNavigationBottomSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.edit){
            handleEdit();
        }
        else if(id == R.id.delete) {
            Toast.makeText(requireContext(), "delete", Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.share){
            handleShare();
//            Toast.makeText(requireContext(), "share", Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.crop){
            handleCrop();
        }
        else if(id == R.id.rotate){
            handleRotate();
        }
        else if(id == R.id.flip){
            handleFlip();
        }
        else if(id == R.id.paint){
            handleBrush();
        }
        else if(id == R.id.undo){

        }
        else if(id == R.id.redo){

        }
        else if(id == R.id.eraser){

        }
        else if(id == R.id.color){
            handleColor();
        }
        else {
            return false;
        }
        return true;
    }


    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(requireContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory,"img.png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void loadImageFromStorage(String path)
    {
        try {
            File f = new File(path, "img.png");
            image_bm_orig = BitmapFactory.decodeStream(new FileInputStream(f));
            image_bm_mod = image_bm_orig;
//            img = (com.github.chrisbanes.photoview.PhotoView)getView().findViewById(R.id.photo_view);
            imageView.setImageBitmap(image_bm_orig);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    public void handleRename() {
        final AlertDialog.Builder renameDialog = new AlertDialog.Builder(requireContext());
        renameDialog.setTitle("Rename to:");
        final EditText input = new EditText(requireContext());
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
        try {
            WallpaperManager.getInstance(requireContext()).setBitmap(image_bm_orig);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setWallLock(){
        try {
            WallpaperManager.getInstance(requireContext()).setBitmap(image_bm_orig,null,true,WallpaperManager.FLAG_LOCK);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////
    private void handleBack(View v){
        switch (currentState){
            case DETAIL:
                // dang o layout_detail
                Window window = getActivity().getWindow();
                TypedValue outValue = new TypedValue();
                getActivity().getTheme()
                        .resolveAttribute(androidx.appcompat.R.attr.colorPrimaryDark,outValue,true);
                window.setStatusBarColor(outValue.data);

                window.getDecorView().setSystemUiVisibility(0);

                NavHostFragment hostFragment = (NavHostFragment) getActivity()
                        .getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
                NavController navController = hostFragment != null ? hostFragment.getNavController() : null;

                if(navController!= null){
                    navController.navigateUp();
                }
                break;
            case EDIT:
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

                // Trả lại ảnh cũ khi chưa chỉnh
                image_bm_mod = image_bm_orig;
                imageView.setImageBitmap(image_bm_mod);
                //saveToInternalStorage(image_bm_mod);
                break;
            case PAINT:
                //dang o paint
                handleEdit();
                break;
        }
    }

    public void handleDone(View v){
        switch (currentState){
            case EDIT:
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
                image_bm_orig = image_bm_mod;
                imageView.setImageBitmap(image_bm_mod);
                saveToInternalStorage(image_bm_mod);
                break;
            case PAINT:
                image_bm_orig = image_bm_mod;
                imageView.setImageBitmap(image_bm_mod);
                saveToInternalStorage(image_bm_mod);

                currentState = CurrentState.EDIT;
                break;
        }
    }


    private void handleDetails() {
        final AlertDialog.Builder detailsDialog = new AlertDialog.Builder(requireContext());
        detailsDialog.setTitle("Details");
        final EditText input = new EditText(requireContext());
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

    public void handleFavorite() {
        if(isChecked==0){
//            navigationView.getMenu().findItem(R.id.favorite).setIcon(R.drawable.ic_favorite_colored);
            favoriteButton.setImageResource(R.drawable.ic_favorite_colored);
            isChecked=1;
        }else {
//            navigationView.getMenu().findItem(R.id.favorite).setIcon(R.drawable.ic_favorite);
            favoriteButton.setImageResource(R.drawable.ic_favorite);
            isChecked=0;
        }
    }

    public void handleShare() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        if(getActivity() == null)
            return;
        String path = MediaStore.Images.Media.insertImage(getActivity().
                getContentResolver(), image_bm_mod,null, null);
        Uri imageUri =  Uri.parse(path);
        share.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(share, "Select"));
    }

    public void handleEdit(){
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.edit_nav);
        doneButton.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        doneButton.requestLayout();
        favoriteButton.getLayoutParams().height = 0;
        favoriteButton.requestLayout();
        overflowButton.getLayoutParams().height = 0;
        overflowButton.requestLayout();
        navigationView.setVisibility(View.VISIBLE);
        navigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        currentState = CurrentState.EDIT;
    }

    public void handleBrush(){
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.paint_nav);
        doneButton.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        doneButton.requestLayout();
        overflowButton.getLayoutParams().height = 0;
        overflowButton.requestLayout();
        navigationView.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        currentState = CurrentState.PAINT;
    }

    public void handleColor(){
        new ColorPickerDialog.Builder(requireContext())
                .setTitle("ColorPicker Dialog")
                .setPreferenceName("MyColorPickerDialog")
                .setPositiveButton("Confirm",
                        new ColorEnvelopeListener() {
                            MenuItem color = navigationView.getMenu().findItem(R.id.color);
                            @Override
                            public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                color = navigationView.getMenu().findItem(R.id.color);
                                Drawable wrapDrawable = DrawableCompat.wrap(
                                        ContextCompat.getDrawable(requireContext(),
                                        R.drawable.ic_circle)
                                );
                                DrawableCompat.setTint(wrapDrawable, envelope.getColor());
                                color.setIcon(wrapDrawable);
                            }
                        })
                .setNegativeButton("cancel",
                        (dialogInterface, i) -> dialogInterface.dismiss())
                .attachAlphaSlideBar(true) // the default value is true.
                .attachBrightnessSlideBar(true)  // the default value is true.
                .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
                .show();
    }


    public void handleRotate() {
        Matrix mat = new Matrix();
        mat.postRotate(90);
        Bitmap image_bm_rotated = Bitmap.createBitmap(image_bm_mod, 0, 0,image_bm_mod.getWidth(),image_bm_mod.getHeight(), mat, true);
        image_bm_mod = image_bm_rotated;
        imageView.setImageBitmap(image_bm_mod);
//        saveToInternalStorage(image_bm_mod);
    }

    public void handleFlip() {
        Matrix mat = new Matrix();
        mat.postScale(-1f, 1f, image_bm_mod.getWidth() / 2f, image_bm_mod.getHeight() / 2f);
        Bitmap image_bm_flipped = Bitmap.createBitmap(image_bm_mod, 0, 0, image_bm_mod.getWidth(), image_bm_mod.getHeight(), mat, true);
        image_bm_mod = image_bm_flipped;
        imageView.setImageBitmap(image_bm_mod);
//        saveToInternalStorage(image_bm_mod);
    }

    public void handleCrop() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image_bm_mod.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(
                requireContext().getContentResolver(),
                image_bm_mod,
                Integer.toString(new Random().nextInt()), null
        );

        Uri imageUri =  Uri.parse(path);

        String desFileName = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
        File newFile = new File(getActivity().getCacheDir(),desFileName);
        Uri desUri=Uri.fromFile(newFile);

        ArrayList<Uri> listUri = new ArrayList<>();
        listUri.add(imageUri);
        listUri.add(desUri);
        cropImage.launch(listUri);


    }

    private ActivityResultContract<List<Uri>, Uri> uCropConstract = new ActivityResultContract<List<Uri>, Uri>() {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, List<Uri> uri) {
            UCrop.Options options = new UCrop.Options();

            Uri imageUri = uri.get(0);
            Uri desUri = uri.get(1);
            options.setToolbarColor(getActivity()
                    .getTheme()
                    .obtainStyledAttributes(new int[]{R.attr.ucrop_actionbar_color})
                    .getInt(0,0)
            );
            options.setStatusBarColor(getActivity()
                    .getTheme()
                    .obtainStyledAttributes(new int[]{android.R.attr.colorPrimaryDark})
                    .getInt(0,0)
            );
            options.setToolbarWidgetColor(getActivity()
                    .getTheme()
                    .obtainStyledAttributes(new int[]{android.R.attr.textColorPrimary})
                    .getInt(0,0)
            );
            options.setActiveControlsWidgetColor(getActivity()
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
            = registerForActivityResult(uCropConstract,
            result -> {
//                imageView.setImageURI(result);
                try {
                    if(result != null){
                        image_bm_mod = MediaStore.Images.Media.getBitmap(getActivity()
                                .getContentResolver(), result);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(image_bm_mod);
            });

}