package com.example.album.review_image;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.album.MainActivity;
import com.example.album.R;
import com.example.album.ui.SplitToolbar;
import com.example.album.ui.ZoomableImageView;

public class ReviewImageFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_detail,container,false).getRootView();
    }
    ActionBar app_bar;
    SplitToolbar navigationBar;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        ZoomableImageView imageView = view.findViewById(R.id.photo_view);
        if(getArguments() != null){
            Bitmap bmp = getArguments().getParcelable("image");
            imageView.setImageBitmap(bmp);
        }
//        imageView.set
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onBackPressed();
    }

    private void onBackPressed(){
        // dang o layout_detail
        app_bar.show();
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
}
