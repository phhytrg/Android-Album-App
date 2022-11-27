package com.example.album.data;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

import com.example.album.data.livedata.ImagesLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ImagesViewModel
        extends AndroidViewModel {

    private static final Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    ImagesLiveData images;

    public ImagesViewModel(@NonNull Application application) {
        super(application);
        Context context = application.getApplicationContext();
        images = new ImagesLiveData(context, uri);
    }

    public ImagesLiveData getImages(){
        return images;
    }

    @Nullable
    public TreeMap<String, List<Image>> getAlbums(){
        TreeMap<String, List<Image>> map = new TreeMap<>();
        if(images.getValue() == null)
            return null;
        for(Image image: images.getValue()){
            List<Image> value = map.computeIfAbsent(image.getBucketName(), k -> new ArrayList<>());
            value.add(image);
        }
        return map;
    }

}
