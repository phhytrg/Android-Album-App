package com.example.album;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yalantis.ucrop.UCropFragment;
import com.yalantis.ucrop.UCropFragmentCallback;

public class CustomUcropFragment extends UCropFragment {
    public CustomUcropFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void setCallback(UCropFragmentCallback callback) {
        super.setCallback(callback);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void setupViews(View view, Bundle args) {
        super.setupViews(view, args);
    }

    @Override
    public void cropAndSaveImage() {
        super.cropAndSaveImage();
    }

    @Override
    protected UCropResult getResult(Uri uri, float resultAspectRatio, int offsetX, int offsetY, int imageWidth, int imageHeight) {
        return super.getResult(uri, resultAspectRatio, offsetX, offsetY, imageWidth, imageHeight);
    }

    @Override
    protected UCropResult getError(Throwable throwable) {
        return super.getError(throwable);
    }
}
